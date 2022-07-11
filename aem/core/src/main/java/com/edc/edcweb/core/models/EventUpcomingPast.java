package com.edc.edcweb.core.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.LanguageUtil;
import com.edc.edcweb.core.helpers.SearchForPages;
import com.edc.edcweb.core.helpers.DateFormater;
import com.edc.edcweb.core.helpers.TagHelper;
import com.edc.edcweb.core.helpers.Constants;

/**
 * @author ACN
 * @version 1.0.0
 * @since 1.0.0
 * 
 * 
 * Backing Java code for Event Upcoming and Past
 * 
 */

@Model(adaptables = SlingHttpServletRequest.class) 
public class EventUpcomingPast
{
	private static final Logger log = LoggerFactory.getLogger(EventUpcomingPast.class);

	@Self
	private SlingHttpServletRequest request;
	
	//keep the path of pages with webinar tag
	private Map<String, String> webinarTag = new LinkedHashMap<>();

	//map the separator date with the related list of event detail pages
	private Map<String, List<Page>> upcomingEventGroup = new LinkedHashMap<>();
	private Map<String, List<Page>> pastEventGroup = new LinkedHashMap<>();

	private String langAbbr = "";
	
	/**
	 * Populates the model.  
	 *
	 */		 
	@PostConstruct
	public void initModel() {	
		log.debug(" initModel  " );

		try{	 
			String url = request.getRequestURL().toString();
			langAbbr= LanguageUtil.getLanguageAbbreviationFromPath(url, Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
			
			buildUpcomingEventLists();
			buildPastEventLists();
		} catch (Exception e  ) {
			log.error("error ",e );
		}

	}		
	private void buildUpcomingEventLists() {

		List<Page> listEvt; 
		String[] tagIds = {Constants.Paths.EVENT_TYPE_TAGS};  
		
		//Query pages with event tag under 'edc/<en/fr>/event' location, filter them , sort them and build the indexes and content of separators
		//event pages will be under 'events/webinar' or  'events/matchmaking-tradeshow ' or 'events/reception ' or 'events/round-table' 
		String langPath = "/" + langAbbr.toLowerCase();
		listEvt = SearchForPages.getPagesByTagIds(request, tagIds, true, Constants.Paths.CONTENT_EDC + langPath + "/events" );
		filterList(listEvt); 
		listEvt.sort(new DateFormater.ListSortTimeDate(DateFormater.SortOrder.ASC));
		//If current date is before or equal to END date of event, it is upcoming event
		removePastEvents(listEvt, "endDate");
		buildEventGroup(upcomingEventGroup, listEvt,  "MMMM YYYY");
		buildWebinarTagIndex(listEvt, webinarTag);
		
		if ( log.isDebugEnabled()){
			log.debug("buildUpcomingEventLists: count {} ",  listEvt.size() );
		}
		

	}
	
	private void buildPastEventLists() {
		
		List<Page> listEvt; 
		String[] tagIds = {Constants.Paths.EVENT_TYPE_TAGS, Constants.Paths.ACCESS_TYPE_PREMIUM};
		
		//------Query pages with event and Premium tag under '/event' location, filter them, sort them and build the indexes and content of separators ------
		//We only treat pages with'Premium' tag as past event page. We don't compare event date with current date
		String langPath = "/" + langAbbr.toLowerCase();
		listEvt = SearchForPages.getPagesByTagIds(request, tagIds, false, Constants.Paths.CONTENT_EDC + langPath + "/events");

		filterList(listEvt);
		listEvt.sort(new DateFormater.ListSortTimeDate(DateFormater.SortOrder.DESC));
		buildEventGroup(pastEventGroup, listEvt,  "YYYY");
		buildWebinarTagIndex(listEvt, webinarTag);
		
		if ( log.isDebugEnabled()){
			log.debug("buildPastEventLists: count {} ",  listEvt.size() );
		}

	}

	private void filterList(List<Page> listEvt) {
		//check that the template is a /event/page, else remove from the list.
		for ( int i = listEvt.size() - 1; i >= 0; i -- ){
			try{	
				String containingPageType = listEvt.get(i).getTemplate().getPageTypePath(); 
				if ( !containingPageType.equalsIgnoreCase(Constants.Paths.EVENT_PAGE_TYPE)){
					listEvt.remove(i);
				}
			}catch (Exception e  ) {
				log.debug("filterList Exception error {}", e);
			}	 
		}

	}

	private void buildWebinarTagIndex(List<Page> listEvt, Map<String, String> map) {
		//keep the path of the pages that are tagged as webinar
		for ( int i = listEvt.size() - 1; i >= 0; i -- ){
			if ( TagHelper.isPageTagged(listEvt.get(i), Constants.Paths.EVENT_TYPE_TAGS_WEBINAR) ){
				map.put(listEvt.get(i).getPath(), String.valueOf(i) );
			}
		}
	}

	private void removePastEvents(List<Page> listEvt, String dateProp) {
		//remove event pages based on date comparison
		for ( int i = listEvt.size() - 1; i >= 0; i -- ){
			ValueMap prop = listEvt.get(i).getProperties();
			if ( prop != null){
				Calendar eventDate = prop.get(dateProp, Calendar.class);
				if ( eventDate != null ){
					eventDate.set(Calendar.HOUR_OF_DAY, 23);
					eventDate.set(Calendar.MINUTE, 59); 
					if (eventDate.before(Calendar.getInstance())){
						listEvt.remove(i);
					} 
				}
				else {
					log.debug(" no date?: {} ",  listEvt.get(i).getPath() );
					listEvt.remove(i);
				}
			}	
		}
	}

	private void buildEventGroup(Map<String, List<Page>> eventGroup, List<Page> listEvt, String dateFormatSeparator) {

		String currentSep = ""; 
		SimpleDateFormat dateFormat = new SimpleDateFormat( dateFormatSeparator, new Locale(langAbbr.toLowerCase()) ); 
		
		for ( int i = 0; i < listEvt.size(); i ++ ){
			ValueMap prop = listEvt.get(i).getProperties();
			if ( prop != null){
				try{
					Calendar date = prop.get("startDate", Calendar.class);
					if ( date != null){
						String sep = dateFormat.format(date.getTime());
						if ( !sep.isEmpty() && !sep.equalsIgnoreCase(currentSep)){
							currentSep = sep;
							List<Page> newlistEvt = new ArrayList<>(); 
							newlistEvt.add(listEvt.get(i));
							eventGroup.put(sep, newlistEvt);
						}
						else if (sep.equalsIgnoreCase(currentSep)){
							eventGroup.get(sep).add(listEvt.get(i));
						}
					}
				} catch (Exception e){
					log.error("error ",e );
				} 
			}	
		}
	}

	public Map<String, String> getWebinarTag(){
		return this.webinarTag;
	}

	public Map<String, List<Page>> getUpcomingEventGroup(){
		return this.upcomingEventGroup;
	}

	public Map<String, List<Page>> getPastEventGroup(){
		return this.pastEventGroup;
	}

}
