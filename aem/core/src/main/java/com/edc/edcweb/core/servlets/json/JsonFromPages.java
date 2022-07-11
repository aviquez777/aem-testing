package com.edc.edcweb.core.servlets.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.ArticlePageHelper;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.Request;

public class JsonFromPages extends JsonFormatter {

    /**
     * Construct a new JsonFromPages object.
     *
     * @param includeTags
     *            Set to {@code true} to include each page's tags in the output
     *            JSON.
     * @param includeCurrentPage
     *            Set to {@code true} to include the current page in the output JSON
     *            (if applicable).
     */
    public JsonFromPages(boolean includeTags, boolean includeCurrentPage) {
        super(includeTags, includeCurrentPage);
    }

    /**
     * Create JSON from a list of pages
     *
     * @param request
     *            Originating request
     *            ({@see org.apache.sling.api.SlingHttpServletRequest} Request).
     * @param pages
     *            A collection of pages from which JSON will be created.
     * @return JSON array object.
     */
    public JSONArray createJsonFromListOfPages(SlingHttpServletRequest request, List<Page> pages)
            throws ServletException {
        JSONArray mainJAry = new JSONArray();
        Page currentPage = Request.adaptToPage(request);
        int count = 1;
        try {
            for (Page page : pages) {
                // -------------------------------------------------------------
                // Do not include the current page in the result set
                // -------------------------------------------------------------
                if ((this.getIncludeCurrentPage()) || !page.getPath().equals(currentPage.getPath())) {
                    ValueMap pageProperties = page.getProperties();
                    JSONObject job = new JSONObject();
                    // ---------------------------------------------------------
                    job.put(Constants.Properties.ID, count++);
                    String pageTitle = page.getPageTitle();
                    if(pageTitle == null) {
                        pageTitle = page.getTitle();
                    }
                    if(pageTitle == null) {
                        pageTitle = page.getName();
                    }
                    job.put(Constants.Properties.PAGE_TITLE, pageTitle);
                    job.put(Constants.Properties.TEASER_IMAGE_SOURCE,
                            pageProperties.get(Constants.Properties.TEASER_IMAGE_SOURCE, ""));
                    job.put(Constants.Properties.TABLET_FILE_REFERENCE,
                            pageProperties.get(Constants.Properties.TABLET_FILE_REFERENCE, ""));
                    job.put(Constants.Properties.ARTICLE_IMAGE_ALT_TEXT,
                            pageProperties.get(Constants.Properties.ARTICLE_IMAGE_ALT_TEXT, ""));
                    job.put(Constants.Properties.IMAGE_ALIGNMENT,
                            pageProperties.get(Constants.Properties.IMAGE_ALIGNMENT, "center"));
                    job.put(Constants.Properties.URL, LinkResolver.reverseMapLink(request.getResourceResolver(),
                            LinkResolver.addHtmlExtension(page.getPath())));
                    job.put(Constants.Properties.ARTICLE_SYNOPSIS,
                            pageProperties.get(Constants.Properties.ARTICLE_SYNOPSIS, ""));
                    // ---------------------------------------------------------
                    ArticlePageHelper aph = new ArticlePageHelper();
                    aph.getPropertiesFromPage(request, page);
                    // ---------------------------------------------------------
                    job.put(Constants.Properties.CONTENT_TYPE_CLASS_NAME, aph.getContentTypeClassName());
                    job.put(Constants.Properties.CONTENT_TYPE_TITLE_TEXT, aph.getContentTypeTitle());
                    job.put(Constants.Properties.IS_PREMIUM, aph.isPremium());
                    // ---------------------------------------------------------
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date sortDate = ArticlePageHelper.getArticlePageSortDate(page, pageProperties,
                            pageProperties.get(Constants.Properties.JCR_CREATED, Calendar.class)).getTime();
                    job.put(Constants.Properties.SORT_DATE, formatter.format(sortDate));
                    // ---------------------------------------------------------
                    if (this.getIncludeTags()) {
                        List<String> tagIds = new ArrayList<>();
                        for (Tag tag : page.getTags()) {
                            tagIds.add(tag.getTagID().replaceAll(Constants.TAGS_EDC_NAMESPACE, ""));
                        }
                        JSONArray jArray = new JSONArray(tagIds);
                        job.put(Constants.Properties.TAGS, jArray);
                    }
                    // ---------------------------------------------------------
                    //check for author settings on page
                    String publishDate = pageProperties.get(Constants.Properties.PUBLISH_DATE, String.class);
                    String displayDate = "";
                    if (publishDate != null && !publishDate.equals(Constants.Properties.NO_DATE)) {
                        String dateformat = Constants.SupportedLanguages.ENGLISH.getDateFormat();
                        if ("fr".equals(page.getLanguage().getLanguage())) {
                            dateformat = Constants.SupportedLanguages.FRENCH.getDateFormat();
                        }
                        SimpleDateFormat toFormatDate = new SimpleDateFormat(dateformat, page.getLanguage());
                        displayDate = toFormatDate.format(sortDate);
                    } 
                    job.put(Constants.Properties.DISPLAY_DATE, displayDate);
                    //Add Page Name to JSON
                    job.put(Constants.Properties.PAGE_NAME, page.getName());

                    mainJAry.put(job);
                }
            }
            return mainJAry;
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }
}
