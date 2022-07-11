package com.edc.edcweb.core.helpers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.constants.ConstantsEbook;

/**
 * <h1>EbookHelper</h1> Class to provide support for the Ebooks Returns a List
 * of Ebook Pages sorted by chapters
 **/

public class EbookHelper {

    /**
     * Private constructor to comply with Sonar Lint
     */
    private EbookHelper() {
    }

    /**
     * Method getEbookPages get
     *
     * @param currentPage
     *            Page the component is located at
     * @param selectedPersona
     *            String with selected persona type from Session, null for "Normal
     *            Ebook"
     * @return list of Pages that form the ebook, based on the persona type.
     */
    public static List<Page> getEbookPages(Page currentPage, String selectedPersona) {
        if (selectedPersona == null || selectedPersona.isEmpty()) {
            return getNormalEbook(currentPage);
        } else {
            return getPersonaEbook(currentPage, selectedPersona);
        }
    }


    /**
     * This function return the first chapter url of ebook by providing current page.
     * The current page may be ebook teaser page, or a chapter page in a ebook.
     * @param currentPage
     * @param persona
     * 		If not null, then indicate this is a persona ebook
     * @return
     * 		URL of first chapter of this ebook. Result contains html extension
     */
    public static String getFirstChapterURLFromCurrentPage(Page currentPage, String persona) {
        String firstChpURL = "";

        if(persona ==null || persona.isEmpty()) {
            //this is regular ebook, we get the first chapter through sequence
            String currentPageURL = currentPage.getPath();
            String pageLanguage = LanguageUtil.getLanguageAbbreviationFromPath(currentPage.getPath(), Constants.SupportedLanguages.ENGLISH.getLanguageAbbreviation());
            String premiumParentURL = "";
            String parentPath = currentPage.getParent().getPath();

            if(parentPath.contains(Constants.Paths.EDC +"/"+pageLanguage+"/ebook")){
                //current page is regular ebook teaser
                int last = currentPageURL.indexOf("/edc/"+pageLanguage+"/");
                premiumParentURL = new StringBuilder(currentPageURL).insert(last + 7, Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT).toString();
                ResourceResolver resourceResolver = currentPage.getContentResource().getResourceResolver();
                Resource res = resourceResolver.getResource(premiumParentURL);

                if(res != null) {
                    Page premiumParentPage = res.adaptTo(Page.class);
                    firstChpURL = getFirstChapterURLFromPremiumParentPage(premiumParentPage, null);
                }

            } else if(parentPath.contains(Constants.Paths.EDC +"/"+pageLanguage+Constants.Paths.PROGRESSIVEPROFILING_PREMIUMROOT+"/ebook")) {
                String pageTypePath = currentPage.getTemplate().getPageTypePath();
                if(pageTypePath.equalsIgnoreCase(ConstantsEbook.Paths.EBOOK_PAGE_TYPE)) {
                //current page is a chapter of regular ebook
                    Page premiumParentPage = currentPage.getParent();
                    firstChpURL = getFirstChapterURLFromPremiumParentPage(premiumParentPage, null);
                }
           }
        }
        return firstChpURL;
    }


    private static String getFirstChapterURLFromPremiumParentPage(Page premiumParentPage, String persona) {
        String firstChpURL = "";

        if(persona == null || persona.isEmpty()) {
            if(premiumParentPage != null) {
                Iterator<Page> chpsIt = premiumParentPage.listChildren();
                if(chpsIt!=null) {
                    Page firstChpPage = chpsIt.next();
                    firstChpURL = firstChpPage.getPath();
                    firstChpURL = LinkResolver.addHtmlExtension(firstChpURL);
                }
            }
        }

        return firstChpURL;
    }


    public static String getAttributeFromRegularEBookTeaserPageSneakPeekPPComp(Page teaserPage, String arrtName) {
        String attrValue = "";

        if(teaserPage !=null) {
            Resource sneakPeakPPRes = ResourceResolverHelper.getResourceByType(teaserPage,
                    ConstantsEbook.EbookProperties.PROGRESSIVEPROFILING_CONTENT_TYPE);

            if (sneakPeakPPRes != null) {
                ValueMap personaProperties = ResourceUtil.getValueMap(sneakPeakPPRes);
                attrValue = personaProperties.get(arrtName, String.class);
            }
        }
        return attrValue;
    }

    /**
     * Get attributes like DocId and Asset tier from persona button on teaser page
     * @param teaserPage
     * @param attrName
     * @return the attribute value
     */
    public static String getAttributeFromPersonaButton(Page teaserPage, String attrName) {
        String attrValue = "";

        if(teaserPage != null) {
            Resource personaButtonRes = ResourceResolverHelper.getResourceByType(teaserPage, ConstantsEbook.EbookProperties.PERSONA_CONTENT_TYPE);

            if (personaButtonRes != null) {
                ValueMap personaButtonProps = ResourceUtil.getValueMap(personaButtonRes);
                attrValue = personaButtonProps.get(attrName, String.class);
            }
        }

        return attrValue;
    }

    /**
     * For persona Ebook - Get the first chapter based on the selected persona
     * @param request
     * @param teaserPath
     * @param selectedPersona
     * @return the first chapter for the selected persona
     */
    public static String getPersonaFirstChapterURLFromTeaser(SlingHttpServletRequest request, String teaserPath, String selectedPersona) {
        Resource personaPageRes = request.getResourceResolver().getResource(teaserPath);
        Resource personaRes = ResourceResolverHelper.getResourceByType(personaPageRes.adaptTo(Page.class), ConstantsEbook.EbookProperties.PERSONA_CONTENT_TYPE);

        ValueMap personaProperties = ResourceUtil.getValueMap(personaRes);
        String firstChapterUrl = "";
        if (personaRes != null && selectedPersona != null) {
            for (int i = 1; i < 5; i++) {
                String thisButtonPersonaName = personaProperties.get(ConstantsEbook.PersonaButtonsProps.PERSONA_BTN + i, String.class);
                // Get the selected persona from component, and get the first chapter
                if (thisButtonPersonaName != null && !thisButtonPersonaName.isEmpty() && selectedPersona.equals(thisButtonPersonaName) && personaRes.hasChildren()) {

                    // get the chapter multifield
                    Resource chaptersRes = personaRes.getChild(ConstantsEbook.PersonaButtonsProps.CHAPTER_BTN + i);
                    if (chaptersRes != null && chaptersRes.hasChildren()) {
                        Iterator<Resource> thisChapters = chaptersRes.listChildren();
                        Resource firstChild =  thisChapters.next();
                        firstChapterUrl = firstChild.getValueMap().get(ConstantsEbook.EBOOK_CHAPTER, String.class);
                        break;
                    }
                }
            }
        }
        return firstChapterUrl;
    }

    /**
     * This method gets the pages for the "Persona" ebook.
     *
     * @param currentPage
     *            Page the component is located at
     * @param selectedPersona
     *            String with selected persona type
     * @return list of Pages that form the ebook sorted as pages were authored
     */
    private static List<Page> getPersonaEbook(Page currentPage, String selectedPersona) {
        List<Page> ebookPages = new LinkedList<>();
        // get the current page's parent, and remove the premium part
        Page parentPage = currentPage.getParent();
        if (parentPage != null) {
            String teaserPath = parentPage.getPath().replace(ConstantsEbook.Paths.PROGRESSIVEPROFILING_PREMIUMROOT, "");
            // get the persona button resource from the teaser page
            ResourceResolver resourceResolver = currentPage.getContentResource().getResourceResolver();
            Page teaserPage = resourceResolver.getResource(teaserPath).adaptTo(Page.class);
            // get the persona button resource from the teaser page
            Resource personaRes = ResourceResolverHelper.getResourceByType(teaserPage,
                    ConstantsEbook.EbookProperties.PERSONA_CONTENT_TYPE);
            ValueMap personaProperties = ResourceUtil.getValueMap(personaRes);
            if (personaRes != null) {
                for (int i = 1; i < 5; i++) {
                    String thisButtonPersonaName = personaProperties
                            .get(ConstantsEbook.PersonaButtonsProps.PERSONA_BTN + i, String.class);
                    // Get the selected persona from component, and loop over the chapters if any
                    if (thisButtonPersonaName != null && !thisButtonPersonaName.isEmpty()
                            && selectedPersona.equals(thisButtonPersonaName) && personaRes.hasChildren()) {
                        // get the chapter multifield
                        Resource chaptersRes = personaRes.getChild(ConstantsEbook.PersonaButtonsProps.CHAPTER_BTN + i);
                        if (chaptersRes != null && chaptersRes.hasChildren()) {
                            Iterator<Resource> thisChapters = chaptersRes.listChildren();
                            // loop over the chapters
                            while (thisChapters.hasNext()) {
                                Resource thisChapter = thisChapters.next();
                                String chapterPath = thisChapter.getValueMap().get(ConstantsEbook.EBOOK_CHAPTER,
                                        String.class);
                                // Get the pages and add them to the list
                                if (chapterPath != null && !chapterPath.isEmpty()) {
                                    Resource thePageRes = resourceResolver.getResource(chapterPath);
                                    if (thePageRes != null) {
                                        ebookPages.add(thePageRes.adaptTo(Page.class));
                                    }
                                }
                            }
                        }
                    }
                    if (!ebookPages.isEmpty()) {
                        break;
                    }
                }
            }
        }
        // return the list
        return ebookPages;
    }

    /**
     * This method gets the pages for the "normal" ebook.
     *
     * @param currentPage
     *            the Page the ebook is on
     * @return list of Pages that form the ebook sorted as pages were authored
     */
    private static List<Page> getNormalEbook(Page currentPage) {
        List<Page> ebookPages = new LinkedList<>();
        // Get the Parent Page
        Page parentPage = currentPage.getParent();
        if (parentPage != null) {
            // Get current page's root path
            String rootPagePath = parentPage.getPath();
            // Get the parent page
            Page rootPage = currentPage.getPageManager().getPage(rootPagePath);
            // Get all the pages
            Iterator<Page> ebookPagesIt = rootPage.listChildren();
            // convert to list
            ebookPagesIt.forEachRemaining(ebookPages::add);
        }
        // return the list
        return ebookPages;
    }


}
