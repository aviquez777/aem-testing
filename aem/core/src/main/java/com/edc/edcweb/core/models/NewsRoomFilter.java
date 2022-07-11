package com.edc.edcweb.core.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.ArticlePageHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.models.newsRoomFilter.NewsRoomFilterItem;
import com.edc.edcweb.core.search.filters.ResourcePageFilter;

/**
 * <h1>NewsRoomFilter</h1>
 **/
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsRoomFilter {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;
    
    @ValueMapValue
    private String basePage;

    private List<NewsRoomFilterItem> newsList = new ArrayList<>();

    private List<String> yearsList = new ArrayList<>();

    /**
     * This method is used to retrieve, sort and set the variables to return on the
     * getter methods Will check if the user has manually included any of the 3
     * possible cards, Will search for the pages if necessary Compare the manually
     * selected card to the ones on the Dynamic results to avoid duplicates Will
     * ensure the Right List, wil have no more than 5 items
     *
     **/
    @PostConstruct
    public void initModel() {
        List<Page> pages = new ArrayList<>();
        //Filter by new-release page
        String[] tagsToMatch = { "edc:format-type/news-release" };
        //get all the children pages with the news tags, of no path given, use current page
        if(basePage != null) {
          currentPage  = request.getResourceResolver().getResource(basePage).adaptTo(Page.class);
        }
        currentPage.listChildren(new ResourcePageFilter(tagsToMatch), false).forEachRemaining(pages::add);
        pages.sort(new ArticlePageHelper.ListSort(ArticlePageHelper.SortOrder.DESC));
        // Create our object
        for (Page page : pages) {
            // check for author settings on page's date
            ValueMap pageProperties = page.getProperties();
            Date sortDate = ArticlePageHelper.getArticlePageSortDate(page, pageProperties,
                    pageProperties.get(Constants.Properties.JCR_CREATED, Calendar.class)).getTime();
            String publishDate = pageProperties.get(Constants.Properties.PUBLISH_DATE, String.class);
            // if no publish date and the author set the date not to be shown, then skip the
            // page because it cannot be filtered. (Should not be the case)
            if (publishDate != null && !publishDate.equals(Constants.Properties.NO_DATE)) {
                String dateformat = Constants.SupportedLanguages.ENGLISH.getDateFormat();
                if (page.getLanguage().getLanguage().equals("fr")) {
                    dateformat = Constants.SupportedLanguages.FRENCH.getDateFormat();
                }
                // we have a date, format it
                SimpleDateFormat toFormatDate = new SimpleDateFormat(dateformat, page.getLanguage());
                String displayDate = toFormatDate.format(sortDate);
                // get the year
                SimpleDateFormat toYear = new SimpleDateFormat("yyyy", page.getLanguage());
                String year = toYear.format(sortDate);
                // Create the item and fill it with the goodies
                NewsRoomFilterItem newsItem = new NewsRoomFilterItem();
                String title = page.getPageTitle();
                if (title == null) {
                    title = page.getTitle();
                }
                newsItem.setTitle(title);
                newsItem.setUrl(LinkResolver.addHtmlExtension(page.getPath()));
                newsItem.setDisplayDate(displayDate);
                newsItem.setYear(year);
                // prepare the year list
                if (!yearsList.contains(year)) {
                    yearsList.add(year);
                }
                // if we are into the first 4th year item, remove it and break the loop so we
                // don't waste time with items we won't need
                if (yearsList.size() == 4) {
                    yearsList.remove(3);
                    break;
                }
                // add it to the list if all ok
                newsList.add(newsItem);
            }
        }
    }

    public List<NewsRoomFilterItem> getNewsList() {
        return newsList;
    }

    public List<String> getYearsList() {
        return yearsList;
    }

}
