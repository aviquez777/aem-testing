/**
* This class is used to generate Q&A Maker answers by integrating AEM with Q&A Maker
* 
* @author  ACN
* @version 1.0
* @since   2019-02-08 
*/

package com.edc.edcweb.core.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edc.edcweb.core.helpers.ehh.KnowledgeServiceAnswer;
import com.edc.edcweb.core.service.KnowledgeService;
import com.edc.edcweb.core.service.KnowledgeServiceConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.day.cq.tagging.TagManager;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.HTTPRequestParams;
import com.edc.edcweb.core.helpers.HTTPRequestUtil;
import com.edc.edcweb.core.helpers.HTTPResponseMessage;
import com.edc.edcweb.core.helpers.TagHelper;
import com.edc.edcweb.core.helpers.constants.ConstantsEHH;
import com.edc.edcweb.core.helpers.ehh.EHHHelper;

@Component(
        immediate = true,
        service = KnowledgeService.class,
        configurationPid = "com.edc.edcweb.core.serviceImpl.KnowledgeServiceImpl"
)

@Designate(ocd = KnowledgeServiceConfiguration.class)
public class KnowledgeServiceImpl implements KnowledgeService{

	private static final Logger log= LoggerFactory.getLogger(EloquaServiceImpl.class);
	private static String HTTP_REQUEST_POST = "POST";
	private static String HTTP_REQUEST_GET = "GET";
	private static final String EHH_STATICSVR_SEARCH_FILTER = ConstantsEHH.EHHProperties.EHH_SEARCHQUERY_FILTER;

	// Get values from OSGi configuration
	private String config_kbHost;
	private String config_kbServiceRoute;
	private String config_kbEndPointKey;
	private String config_ehhStaticSvrBaseURLWithKey;
	private String config_ehhStaticSvrParamAPIKey;
	private String config_ehhStaticSvrParamVersion;

	private String config_ehhStaticSvrParamTop;
	
    @Activate
    @Modified
    public void activate(KnowledgeServiceConfiguration config) {
        this.config_kbHost = config.kbHost();
        this.config_kbServiceRoute = config.kbServiceRoute();
        this.config_kbEndPointKey = config.kbEndPointKey();
        this.config_ehhStaticSvrBaseURLWithKey= config.ehhStaticSvrBaseURLWithKey();
        this.config_ehhStaticSvrParamAPIKey = config.ehhStaticSvrParamAPIKey();
        this.config_ehhStaticSvrParamVersion = config.ehhStaticSvrParamVersion();
        this.config_ehhStaticSvrParamTop = config.ehhStaticSvrParamTop();
    }
    

	/**
	 * Get answer(s) from Knowledge service
	 * 
	 * @param question  The question send to Q&A Maker
	 * @param langCode  language code: 'en' or 'fr'
	 * @param top Number of questions need to be return
	 * @return ArrayList<KnowledgeServiceQuestion>
	 */

    @Override
    public ArrayList<KnowledgeServiceAnswer> generateAnswer(SlingHttpServletRequest request, String question, String langCode, String top, String scorethreshold) {

		log.info(KnowledgeServiceImpl.class.getName() + " Method Name generateAnswer() [IN]");

		boolean isValidLang = false;
		String langParam = "";
		String strResponse = "";
		ArrayList<KnowledgeServiceAnswer> allQnAAnswers = new ArrayList<KnowledgeServiceAnswer>();
		TagManager tagManager = TagHelper.getTagManager(request);
		
		HTTPRequestParams requestParams = new  HTTPRequestParams();
		
		//Reqeust type: post or get
		requestParams.setRequestType(HTTP_REQUEST_POST);
		
		//URL
		requestParams.setUrl(config_kbHost + config_kbServiceRoute);
		
		//Header properies
		HashMap<String, String> reqProperties = new HashMap<String, String>(); 
		reqProperties.put("Content-Type", Constants.Properties.APPLICATION_SLASH_JSON);
		if (this.config_kbEndPointKey != null) {
			reqProperties.put("Authorization", "EndpointKey " + this.config_kbEndPointKey);
		}
		requestParams.setRequestProperties(reqProperties);
		
		//Requst body
		if(langCode!=null && (langCode.equalsIgnoreCase("en") || langCode.equalsIgnoreCase("fr"))) {
			isValidLang = true;
		}
		if(isValidLang) {
			langParam = ",\"strictFilters\":[{ 'name': 'language', 'value': '"+langCode.toLowerCase()+"' }]";
		}
		String input = "{\"question\":\"" + question + "\",\"top\":" + top + ", \"scorethreshold\":"+ scorethreshold + langParam+"}";
		requestParams.setRequestBody(input);
		
		//Send request and parse response JSON string
		HTTPResponseMessage respMsg = HTTPRequestUtil.doRESTfulRequest(requestParams);
		if(respMsg.getResponseCode() == 200) {
			strResponse = respMsg.getResponseBody();
			allQnAAnswers = parseQnAQuestionFromJsonResp(request, tagManager, strResponse);
		}
		else {
			log.error("Failed to get QnA Maker response: Error Code :"+respMsg.getResponseCode()+". Message = "+respMsg.getResponseMessage());
		}
		
		
		log.info(KnowledgeServiceImpl.class.getName() + " Method Name generateAnswer() [OUT]");
		
		return allQnAAnswers;
	}

	/**
	 * Parse the Json Response from Q&A Maker to ArrayList of KnowledgeServiceAnswer class
	 * 
	 * @param tagManager  The TagNamager class which will be used to get taxonomy information
	 * @param jsonResponse Json reponse returned by Q&A Maker
	 * @return ArrayList<KnowledgeServiceAnswer> Parsed result and saved as arraylist of KnowledgeServiceAnswer object
	 */
    private ArrayList<KnowledgeServiceAnswer> parseQnAQuestionFromJsonResp(SlingHttpServletRequest request, TagManager tagManager, String jsonResponse) {

		log.info(KnowledgeServiceImpl.class.getName() + " Method Name parseQnAQuestionFromJsonResp() [IN]");

		EHHHelper ehhHelper = new EHHHelper(request);
		ArrayList<KnowledgeServiceAnswer> allQnAAnswers = new ArrayList<KnowledgeServiceAnswer>();

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jsonResponse);
			
			JsonNode answersNode = rootNode.path(ConstantsEHH.EHHProperties.EHH_ATTR_ANSWERS);
			Iterator<JsonNode> answers = answersNode.elements();

			while (answers.hasNext()) {

				KnowledgeServiceAnswer anQnAQuestion = new KnowledgeServiceAnswer();

				JsonNode aAnswer = answers.next();
				HashMap<String, String> metadtaValuesMap = new HashMap<>();

				// questions
				JsonNode questionsNode = aAnswer.findValue(ConstantsEHH.EHHProperties.EHH_ATTR_QUESTIONS);
				Iterator<JsonNode> questionsIterator = questionsNode.elements();
				if (questionsIterator.hasNext()) {
					// Actually only one question is return
					JsonNode questionValueNode = questionsIterator.next();
					if(questionValueNode != null) {
						anQnAQuestion.setQuestion(questionValueNode.asText());
					}
				}

				// answer
				JsonNode answerNode = aAnswer.findValue(ConstantsEHH.EHHProperties.EHH_ATTR_ANSWER);
				if(answerNode != null) {
					String answerHTML = ehhHelper.parseEHHQuestionStr(answerNode.textValue());
					anQnAQuestion.setAnswer(answerHTML);
				}

				// score
				JsonNode scoreNode = aAnswer.findValue(ConstantsEHH.EHHProperties.EHH_ATTR_SCORE);
				if(scoreNode != null) {
					anQnAQuestion.setScore(scoreNode.floatValue());
				}
				
				// id
				JsonNode idNode = aAnswer.findValue(ConstantsEHH.EHHProperties.EHH_ATTR_ID);
				if(idNode != null) {
					anQnAQuestion.setId(String.valueOf(idNode.intValue()));
				}
				
				// source
				JsonNode sourceNode = aAnswer.findValue(ConstantsEHH.EHHProperties.EHH_ATTR_SOURCE);
				if(sourceNode != null) {
					anQnAQuestion.setSource(sourceNode.textValue());
				}

				// metadata: topic, author, country, subtopic, language, english_question(THis is a new added one)
				JsonNode metadataNode = aAnswer.findValue(ConstantsEHH.EHHProperties.EHH_ATTR_METADATA);
				if (metadataNode != null) {
					Iterator<JsonNode> metadataIterator = metadataNode.elements();
					while (metadataIterator.hasNext()) {
						JsonNode metadataValueNode = metadataIterator.next();
						String name = metadataValueNode.findValue(ConstantsEHH.EHHProperties.EHH_ATTR_NAME).asText();
						String value = "";
						if(name.equalsIgnoreCase(ConstantsEHH.EHHProperties.EHH_ATTR_AUTHOR)) {
							String authorTagId = ehhHelper.findTagIdWithEnTitle(tagManager, metadataValueNode.findValue(ConstantsEHH.EHHProperties.EHH_ATTR_VALUE).asText(), 2);
							value = authorTagId;
						}
						else {
							value = metadataValueNode.findValue(ConstantsEHH.EHHProperties.EHH_ATTR_VALUE).asText();
						}
						metadtaValuesMap.put(name, value);
					}
					anQnAQuestion.setMetadata(metadtaValuesMap);
				}

				// Add answer to arrayList
				allQnAAnswers.add(anQnAQuestion);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		//check if no good match found in KB
		if(allQnAAnswers != null && allQnAAnswers.size()==1 && allQnAAnswers.get(0).getQuestion().isEmpty()){
			allQnAAnswers = new ArrayList<KnowledgeServiceAnswer>();
		}
		
		log.info(KnowledgeServiceImpl.class.getName() + " Method Name parseQnAQuestionFromJsonResp() [OUT]");
		return allQnAAnswers;
	}
    
	/**
	 * Send API call to get all questions displayed on EHH Landing page.
	 * This is new requirement for EHH MVP3
	 * 
	 * @param languageCode  Language code: 'en' or 'fr'  
	 * @return ArrayList<KnowledgeServiceAnswer> Parsed result and saved as arraylist of KnowledgeServiceAnswer object
	 */
    @Override
    public String findAllQuestions(String languageCode) {
    	
    	log.info(KnowledgeServiceImpl.class.getName() + " Method Name findAllQuestions() [IN]");
    	
    	String result = "";
    	if(languageCode == null || languageCode.isEmpty()) {
    		languageCode = "en";
    	}
    	
    	HTTPRequestParams requestParams = new  HTTPRequestParams();
		
		//Reqeust type: post or get
		requestParams.setRequestType(HTTP_REQUEST_GET);
		
		//Replace the language code place holder in the parameter
		Map<String, String> params = new HashMap<String, String>();
		params.put("languageCode", languageCode.toLowerCase());StrSubstitutor sub = new StrSubstitutor(params);
		String searchParameterWithLang = sub.replace( EHH_STATICSVR_SEARCH_FILTER);
		//URL
		String url = this.config_ehhStaticSvrBaseURLWithKey + this.config_ehhStaticSvrParamVersion +"&";
		url += this.config_ehhStaticSvrParamAPIKey + "&" + searchParameterWithLang + "&" + this.config_ehhStaticSvrParamTop;
		requestParams.setUrl(url);
				
		//Header properies -
		HashMap<String, String> reqProperties = new HashMap<String, String>(); 
		requestParams.setRequestProperties(reqProperties);		
		
		//Send request and get JSON response
		HTTPResponseMessage respMsg = HTTPRequestUtil.doRESTfulRequest(requestParams);
		if(respMsg.getResponseCode() == 200) {
			String strResponse = respMsg.getResponseBody();
			result = strResponse;
		}
		else {
			log.error("Failed to get EHH questions: Error Code :"+respMsg.getResponseCode()+". Message = "+respMsg.getResponseMessage());
		}
		
		log.info(KnowledgeServiceImpl.class.getName() + " Method Name findAllQuestions() [OUT]");
    	return result;
    	
    }
   
}
