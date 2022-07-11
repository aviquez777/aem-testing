package com.edc.edcportal.core.servlets.dashboard.helpers;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.wcm.api.Page;
import com.edc.edcportal.core.helpers.ArticlePageHelper;
import com.edc.edcportal.core.helpers.Constants;
import com.edc.edcportal.core.helpers.DateUtilsHelper;
import com.edc.edcportal.core.helpers.LinkResolver;
import com.edc.edcportal.core.helpers.TranslationHelper;
import com.edc.edcportal.core.helpers.constants.ConstantsWebinars;
import com.edc.edcportal.core.models.pojo.DashboardItem;
import com.edc.edcportal.core.servlets.dashboard.pojo.DashboardPageListDO;
import com.edc.edcportal.core.servlets.dashboard.pojo.PageItem;

public class DashboardHelper {

    private DashboardHelper() {
        // Sonar lint
    }

    /**
     * Page List logic
     */
    public static DashboardPageListDO populatePageList(SlingHttpServletRequest request, Map<String, String> pagePaths,
            String pageLanguage) {
        DashboardPageListDO dashboardPageListDO = new DashboardPageListDO();
        List<PageItem> pagesItemsList = new LinkedList<>();
        int itemCounter = 1;
        // Page Number for our Map Index and html pagination
        int pageNum = 1;
        // Page Counter to calculate page breaks
        int pageCounter = 2;
        // Actual item list
        List<DashboardItem> items = new LinkedList<>();
        // Temporary Ebook's list
        List<String> ebookList = new LinkedList<>();
        PageItem pageItem = new PageItem();
        // loop over the results path set and get each page
        for (Map.Entry<String, String> item : pagePaths.entrySet()) {
            String pagePath = item.getKey();
            String nodeDepth = item.getValue();
            String fullPath = pagePath;
            boolean isWebinar =  false;
            // If not a webinar add content/<lang>
            if (!pagePath.contains(DashboardConstants.WEBINAR_NODE)) {
                fullPath = Constants.Paths.CONTENT_EDC.concat(Constants.FORWARD_SLASH).concat(pageLanguage)
                        .concat(pagePath);
            } else {
                isWebinar = true;
            }
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource res = resourceResolver.getResource(fullPath);
            if (res != null) {
                Page page = res.adaptTo(Page.class);
                boolean addPage = true;
                if (page != null) {
                    if (nodeDepth.equals(DashboardConstants.NODE_DEPTH)) {
                        // if it is an ebook make sure to add the first chapter if not already
                        page = page.getParent().listChildren().next();
                        String chapter1Path = page.getPath();
                        addPage = !ebookList.contains(chapter1Path);
                        ebookList.add(chapter1Path);
                    }
                    if (addPage) {
                        items.add(populateItem(request, page, nodeDepth));
                        // add the list
                        pageItem.setDashboardItems(items);
                        boolean lastItem = itemCounter == pagePaths.size();
                        boolean closePage = Math.floorMod(pageCounter, DashboardConstants.ITEMS_PER_PAGE) == 0;
                        // reset the list if we are on a page already or the total
                        if (closePage || lastItem ){
                            pageItem.setPageNumber(pageNum);
                            pagesItemsList.add(pageItem);
                            // reset the list
                            items = new LinkedList<>();
                            pageItem = new PageItem();
                            // increase the index by one
                            pageNum++;
                        }
                        // increase page counter only if there's a valid page
                        pageCounter++;
                        // full page and last item add one more for last message only for webinars
                        if(closePage && lastItem && isWebinar) {
                            pageItem = new PageItem();
                            pageItem.setPageNumber(pageNum);
                            pageItem.setDashboardItems(items);
                            pagesItemsList.add(pageItem); 
                        }
                    }
                }
            }
            itemCounter++;
        }
        dashboardPageListDO.setPageItems(pagesItemsList);
        // No error
        dashboardPageListDO.setErrorMessage(null);
        return dashboardPageListDO;
    }

    private static DashboardItem populateItem(SlingHttpServletRequest request, Page page, String mapValue) {
        // reset the helper on each card, else non existen values will not be overridden
        // from previous instance.
        ArticlePageHelper articleHelper = new ArticlePageHelper();
        articleHelper.getPropertiesFromPage(request, page);
        DashboardItem dashboardItem = new DashboardItem();
        String pageTitle = page.getPageTitle();
        if (StringUtils.isBlank(pageTitle)) {
            pageTitle = page.getTitle();
        }
        if (StringUtils.isBlank(pageTitle)) {
            pageTitle = page.getName();
        }
        dashboardItem.setTitle(pageTitle);
        dashboardItem.setDescription(page.getDescription());
        dashboardItem.setImage(page.getProperties().get(Constants.Properties.TABLET_FILE_REFERENCE, ""));
        dashboardItem.setImageAlt(page.getProperties().get(Constants.Properties.ARTICLE_IMAGE_ALT_TEXT, ""));
        String imageAlign = page.getProperties().get(Constants.Properties.IMAGE_ALIGNMENT, "");
        if (StringUtils.isBlank(imageAlign)) {
            imageAlign = Constants.Properties.CENTER;
        }
        String showKey = page.getProperties().get(ConstantsWebinars.SHOW_KEY, "");
        if (StringUtils.isNotBlank(showKey)) {
            Calendar startDate = page.getProperties().get(ConstantsWebinars.WEBINAR_START_DATE, Calendar.class);
            String dateText = DateUtilsHelper.formatDate(startDate, page.getLanguage());
            dashboardItem.setWebinarDateText(dateText);
            dashboardItem.setWebinarStatusClass(mapValue);
            String tanslatedStatus = TranslationHelper.getI18nTranslation("webinar-status-"+mapValue, request, null);
            dashboardItem.setWebinarStatus(tanslatedStatus);
        } else {
            dashboardItem.setContentType(articleHelper.getContentTypeClassName());
            dashboardItem.setContentTypeName(articleHelper.getContentTypeTitle());
        }
        dashboardItem.setImageAlign(imageAlign);
        dashboardItem.setUrl(LinkResolver.changeInternalURLtoExternal(request,
                LinkResolver.addHtmlExtension(page.getPath(), Constants.Paths.CONTENT_EDC)));
        return dashboardItem;
    }
}
