package com.edc.edcweb.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 



/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 *
 * @see ContentPolicyUtil
 * @see TextUrlLinks
 *
 *
 * This class provides model support for the Series
 *
 */
@Model(adaptables = SlingHttpServletRequest.class) 
public class Series 
{
	private static final Logger log= LoggerFactory.getLogger(Series.class);

	@Inject
	private SlingHttpServletRequest request;

	@Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
	
	@Inject
	private Page currentPage;
		
// model  
	private int indexCurrentPage;
	private boolean isSerie;
	private boolean isValid;
	private boolean isComplete;
	private boolean isError;
	private int maxSize = Constants.SERIES_MAX_ITEMS;
	private String ctaText;
	private String partLabel;
	private String ofLabel;
	private String seriesLabel;
	private int  nbpartseries;
	private String sbVersion;
	private String sbmaintitle;
	
	private ValueMap properties;
	
	private List<String> serieError = new ArrayList<>();
	
// keep list
		
	//the list of  article in the serie is the one needed
	private List<Page> content = new ArrayList<>();
	
	//keep all the first article, but should be only one
	private List<Page> firstArticle = new ArrayList<>();
	 
	//need to get the index part X of count for the CTAs...
	private int indexCTA;
	private int indexCTA2;
	private int indexCTA3;
	
	/**
	 * This method is responsible for initial assignment of model properties.
	 * 
	 * Initial values are loaded from the policy and mapped onto model properties.
	 *  
	 */
	 
	
	@PostConstruct
	public void initModel() {
		log.debug("Series constructor  ");
		this.isValid=false;
		this.isComplete=false;
		this.isError = false;
		this.ctaText = "";
		this.isSerie = false;
		
		
		/**
		 * populate the model members
		 */
		log.info("Series model initialization for page  [{}]", currentPage.getPath() );
				
		this.properties = currentPage.getProperties();
		
		this.populateFromPolicy();
		this.populateModel();
						
					
	}
	
	private void populateFromPolicy( )
	{
		String titlePolicy = "";
		log.debug("PopulateFromPolicy ");
		ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(this.request, this.currentPage);
	

		if (contentPolicy != null) {
			log.debug("contentPolicy != null ");
			ValueMap myproperties = contentPolicy.getProperties();
			String tempOne = myproperties.toString();
			String temp_partLabel = myproperties.get(Constants.Properties.SERIE_PARTLABEL, String.class);
			String temp_ofLabel = myproperties.get(Constants.Properties.SERIE_OFLABEL, String.class);
			String temp_seriesLabel = myproperties.get(Constants.Properties.SERIE_SERIESLABEL, String.class);
			String temp_sbVesrion = myproperties.get(Constants.Properties.SERIES_BANNER_VERSION, Constants.Properties.SERIES_BANNER_ENHANCED);
			String temp_sbMainTitle = myproperties.get(Constants.Properties.SERIES_BANNER_ENHANCED_MAIN_TITLE, String.class);

			this.ctaText = myproperties.get(Constants.Properties.SERIE_CTATEXT, String.class);
			this.partLabel = (temp_partLabel==null ? null : temp_partLabel.trim()+" ");
			this.ofLabel = (temp_ofLabel==null ? null:" "+temp_ofLabel.trim()+" ");
			this.seriesLabel = (temp_seriesLabel==null ? null : " "+temp_seriesLabel);

			if (!StringUtils.isBlank(temp_sbMainTitle)) {
				this.sbmaintitle =  temp_sbMainTitle;
			} else {
				this.sbmaintitle = currentPage.getLanguage().getLanguage().equalsIgnoreCase(Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation()) ? Constants.Properties.SERIES_BANNER_ENGLISH_TITLE : Constants.Properties.SERIES_BANNER_FRENCH_TITLE;
			}

			if(!StringUtils.isBlank(temp_sbVesrion)) {
				this.sbVersion = temp_sbVesrion;
			}
		}
	}
	
	private void populateModel() {
		
		/**
		 *  - check that this has the nextarticle property defined or its not in a serie.
		 */
		log.debug("populateModel");
		String pathNext = properties.get(Constants.Properties.SERIE_NEXTARTICLE, String.class);
		log.debug("pathNext:  [{}]", pathNext);
		
		if ( pathNext == null || pathNext.isEmpty()  ) 
		{
			log.debug("[nextarticle] property is empty, current page is not a serie [" + currentPage.getPath() + "]");
			this.isSerie = false;	
			return;	
		}
		
		 
		this.isSerie = true;
		
		//validate all the linked pages
		this.validatePath( currentPage, pathNext );

		//validate the result of the pages content
		this.validateResult();
					
	}
	
	private void validateResult(){
		
		log.debug("validateResult ");
		Boolean aValid = true;
		if ( this.firstArticle.isEmpty() )
		{
			log.debug("no article in series defined as first article ");
			this.serieError.add("No article defined as first article." );
			aValid = false;
			 
		}
		else if ( this.firstArticle.size() > 1 )
		{
			log.debug("many articles in series defined as first article");
			if (log.isDebugEnabled()) {
				log.debug( toPagePathsList(this.firstArticle));
			}
			
			this.serieError.add( "Many articles defined as first article.");
			aValid = false;
		
		}
		else {
			
			int index = this.content.lastIndexOf(this.firstArticle.get(0));
			
			if (log.isDebugEnabled()) {
				log.debug("Only one first article [{}] at index [{}]", this.firstArticle.get(0).getPath(), index);
				log.debug( "Series before rotate {}", toPagePathsList(this.content)  );
			}
			Collections.rotate(this.content, -index);
			 
			
			if (log.isDebugEnabled()) {
				log.debug( "Series after rotate {}", toPagePathsList(this.content) );
			}
		}
		if ( !this.firstArticle.isEmpty() )
		{
			Page thefirstPage = this.firstArticle.get(0);
			ValueMap fpproperties = thefirstPage.getProperties();
			this.nbpartseries = fpproperties.get(Constants.Properties.SERIE_SERIESNBPART, Constants.SERIE_SERIES_DEFAULT_NBPART);
		 
			if ( this.nbpartseries == 0 )
			{
				log.debug(" this.nbpartseries, Series is not complete, nb part serie not defined on first article [{}]", thefirstPage.getPath());
				this.serieError.add( "[Nb part serie] not defined on first article ["+thefirstPage.getPath()+"]" );
				aValid = false;
				
			}
			
			if (  content.size() > this.nbpartseries )
			{
				log.debug(" content.size() >  this.nbpartseries, Series is not complete, nb part serie not defined on first article [{}]", thefirstPage.getPath());
				this.serieError.add( "content size ["+  content.size() + "] is greated than <Number part in series> ["+ this.nbpartseries + "] defined on first page ["+thefirstPage.getPath()+"]." );
				aValid = false;
				
			}
		}
		
		 	
		
		if ( !this.isComplete  )
		{
			log.debug("Series is not complete, last article must link to the first one");
			this.serieError.add( "Series is not complete, last article must link to the first one." );	
			aValid = false;
			 
		}
		
		if ( !this.isComplete  )
		{
			log.debug("Series is not complete, last article must link to the first one");
			this.serieError.add( "Series is not complete, last article must link to the first one." );	
			aValid = false;
			 
		}
		
		if ( this.content.size() > maxSize  )
		{
			log.debug("too many published article in the series");
			this.serieError.add(   "Too many articles ["+this.content.size()+"]. Maximum is ["+ maxSize+"]" );
			aValid = false;
			 
		}
		
		if ( this.content.size() == 1  )
		{
			log.debug("Series must contains at least to articles");
			this.serieError.add( "Series must contains at least two articles. ");
			aValid = false;
			 
		}
		
		if ( this.isError   )		{
			log.debug(" this.isError true");
			aValid = false;
		}
			
		
		//finally this is valid or not.
		if ( !aValid   )		{
			log.debug("this.isValid = false");
			this.isValid = false;
			return;
		}
		
		log.debug("this.isValid = true");
		this.isValid = true;
		
		this.indexCurrentPage = this.content.lastIndexOf(this.currentPage);
		log.debug("indexCurrentPage:{}", indexCurrentPage );
		
		
	}
	
 
	
	private String toPagePathsList( List<Page> thelist ){
		 
		StringBuilder resultBd = new StringBuilder();
				
		for(int i = 0; i < thelist.size(); i++) {   
			resultBd.append( "index["+i+"] page:("+thelist.get(i).getPath() + "). "); 
		}  
		
		return resultBd.toString();
		
	}


	private void validatePath( Page aPage, String nextPath) {
		
		/**
		 * get page pointed by pathnext
		 * if not exist
		 * 		add message return invalid 
		 * if exist 
		 * 		check that isFirst if so and no other first, keep in list or add message error and invalidate
		 * 										 
		 * 		check that is point to a next, get the path to the next, validate it. 
		 * 		if its a path already in the list, the circle is done, else if more than num max, invalidate and message.
		 * 
		 */
		
		log.debug("ValidatePath:{} nextPath: {}", aPage.getTitle(), nextPath );
			
		
		ValueMap myproperties = aPage.getProperties();
		Boolean  testFirst  = myproperties.get(Constants.Properties.SERIE_ISFIRST, Boolean.class);
		if ( testFirst == null )
		{
			log.debug(" testFirst == null, Series is not complete, article page is not a serie article [{}]", aPage.getPath());
			this.serieError.add( "Article page is not a series article ["+aPage.getPath()+"]" );
			this.isError = true;
			return;
			
		}
		
		
		//if input page is already in content, then we complete the circle, else add it to content.
		if ( this.content.contains(aPage)){
			this.isComplete = true;
			log.debug("this.content.contains(aPage)... circle closed... serie is Complete nextpath value: {}", nextPath );
			int testInd = this.content.lastIndexOf(aPage);
			log.debug("testInd  [{}] should be first one or the circle is incomplete! ", testInd  );
			
			if ( testInd > 0 )
			{
				log.debug("Series is not complete, some article are not pointed to by the series.");
				this.serieError.add( "Article not include in the series." );
				this.isError = true;			
			}
			return;
		}
		
		if ( testFirst )
		{
			log.debug("found a first page... aPage.getTitle(): {}", aPage.getPath() );
			this.firstArticle.add(aPage);
			
		}
		
		this.content.add(aPage);  
		
		if ( nextPath == null  )
		{
			log.debug("nextPath == null, Series is not complete, last article must link to the first one");
			this.serieError.add( "Next article is not defined for ["+ aPage.getPath() +"]." );
			this.isError = true;
			return;
			
		}
			
			
		
		Resource nextPageRsrc = this.resourceResolver.resolve(nextPath + Constants.Paths.JCR_CONTENT);
		Page nextPage = this.request.getResourceResolver().resolve(nextPath).adaptTo(Page.class);
		
		
		if (nextPage == null)
		{
			log.debug("(nextPage == null)" );
			this.serieError.add("Next article path [ " + nextPath + " ] is invalid for page. [ "+ aPage.getTitle()   +  " ]" );
			this.isError = true;
			 
		}
		else
		{
			 
		    log.debug("(nextPage found)" );
			ValueMap nextproperties = nextPageRsrc.getValueMap();
          	String nextPageNextPath = nextproperties.get(Constants.Properties.SERIE_NEXTARTICLE, String.class);
          	log.debug("call validate on next page" ); 
            this.validatePath( nextPage, nextPageNextPath); 
           	
            
    	}
		
		 
	
	}
 

	public List<Page> getContent() {
		return new ArrayList<>(content);
	}

	public boolean getIsSerie() {
		return isSerie;
	}
	
	public boolean getIsValid() {
		return isValid;
	}
		
	public boolean getIsComplete() {
		return isComplete;
	}


	public int getIndexCurrentPage() {
		int index = indexCurrentPage + 1;
		log.debug("getIndexCurrentPage index:[{}]", index ); 
		return index;
	}


	public int getCount() {
		return content.size();
	}

	public String getErrorMessage() {
		return this.serieError.toString();
	}
	
	public String getCtaText() {
		log.debug("getCtaText [{}]", this.ctaText); 
		return this.ctaText;
	}
	public String getOfLabel() {
		log.debug("getOfLabel [{}]", this.ofLabel); 
		return this.ofLabel;
	}
	public String getPartLabel() {
		log.debug("getPartLabel [{}]", this.partLabel); 
		return this.partLabel;
	}
	public String getSeriesLabel() {
		log.debug("getSeriesLabel [{}]", this.seriesLabel); 
		return this.seriesLabel;
	}
	public String getNbPartSeries() {
		log.debug("NbPartSeries [{}]", this.nbpartseries); 
		return String.valueOf(this.nbpartseries) ;
	}

	public String getSbVerion() {
		return this.sbVersion;
	}

	public String getSbMainTitle() {
		return this.sbmaintitle;
	}
	
	public List<String> getErrorMessageList() {
		return new ArrayList<>(this.serieError);
	}
 
	/*
	 * Valid when the serie is valid.
	 * 
	 */
	public Page getCTA() {
		if ( !this.isValid) {
			log.debug("getCTAx on invalid serie !!!" ); 
			return null;
		}
		int index = (indexCurrentPage+1) % this.content.size();
		log.debug("getCTA index:[{}]", index  ); 
		this.indexCTA = index+1;
		return content.get(index);
	}
	
	/*
	 * Valid when there is 3 articles in the series.
	 */
	public Page getCTA2() {
		
		if ( !this.isValid || this.content.size() == 2 ) {
			return null;
		}
		
		 
		int index = (indexCurrentPage+2) % this.content.size();
		log.debug("getCTA2 index:[{}]", index ); 
		this.indexCTA2 = index +1;
		return content.get(index);
	}
	
	/*
	 * Valid when there is 4 articles in the series.
	 */
	public Page getCTA3() {
		
		if ( !this.isValid || (this.content.size() ==2 || this.content.size() ==3) ) {
			return null;
		}
		 
		int index = (indexCurrentPage+3) % this.content.size();
		log.debug("getCTA3 index:[{}]", index ); 
		this.indexCTA3 = index +1;
		return content.get(index);
	}
	
	public int getIndexCTA() { return this.indexCTA; }
	public int getIndexCTA2() { return this.indexCTA2; }
	public int getIndexCTA3() { return this.indexCTA3; }



}
