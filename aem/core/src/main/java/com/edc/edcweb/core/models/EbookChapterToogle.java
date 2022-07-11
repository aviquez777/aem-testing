package com.edc.edcweb.core.models;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.edc.edcweb.core.helpers.ContentPolicyUtil;
import com.edc.edcweb.core.helpers.EbookHelper;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.constants.ConstantsEbook;
import com.edc.edcweb.core.models.ebook.EbookChapter;

/**
 * <h1>EbookChapter</h1> Sling model to provide support for the Ebook Template
 * It pulls in the values from the Ebook helper and returns them as list. -hide
 * current chapter -title is "chapter x of y" - default to display chapter n+1
 * -if on last chapter, display chapter 1
 **/
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EbookChapterToogle {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    // Chapter list
    private List<EbookChapter> ebookChapters = new LinkedList<>();
    // Chapter toggle label variables
    private String nextChapterText;
    private String chapterText;
    private String ofText;
    private String minuteText;
    private String minutesText;
    private String readTimeText;
    // Utility Variables
    private int chapterCount = 1;
    private int currentChapter = 0;
    private boolean hasPolicy = false;
    private boolean showNav = true;
    
    // Session Variables
    private String persona;
    private String previewOnly;

    /**
     * This method is used to retrieve, sort and show the ebooks -hide current
     * chapter -title is "chapter x of y" - default to display chapter n+1 -if on
     * last chapter, display chapter 1 Also it reads the translated text from
     * compnent's policy and set a flag to prompt the author if any of the texts are
     * missing
     * 
     **/
    @PostConstruct
    public void initModel() {
        // get the texts from the variables
        ContentPolicy contentPolicy = ContentPolicyUtil.resolveLocalizedContentPolicy(request, currentPage);
        if (contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();
            nextChapterText = properties.get(ConstantsEbook.EbookProperties.NEXT_CHAPTER, String.class);
            chapterText = properties.get(ConstantsEbook.EbookProperties.CHAPTER, String.class);
            ofText = properties.get(ConstantsEbook.EbookProperties.OF, String.class);
            minuteText = properties.get(ConstantsEbook.EbookProperties.MINUTE, String.class);
            minutesText = properties.get(ConstantsEbook.EbookProperties.MINUTES, String.class);
            readTimeText = properties.get(ConstantsEbook.EbookProperties.READ, String.class);
            // set the policy flag
            hasPolicy = ((nextChapterText != null && !nextChapterText.isEmpty())
                    && (chapterText != null && !chapterText.isEmpty()) && (ofText != null && !ofText.isEmpty())
                    && (minuteText != null && !minuteText.isEmpty())
                    && (minutesText != null && !minutesText.isEmpty())
                    && (readTimeText != null && !readTimeText.isEmpty()));
        }
        // Task 12232 hide navigation from chapter 1 of ebook when customers access the
        // "sneak peak" form on the page
        persona = (String) request.getSession().getAttribute(ConstantsEbook.SessionAttr.PERSONA_ATTR);
        previewOnly = (String) request.getSession().getAttribute(ConstantsEbook.SessionAttr.PREVIEW_ONLY_ATTR);

        // Get pages from helper
        List<Page> ebooks = EbookHelper.getEbookPages(currentPage, persona);
        // if preview only attrib is on, redirect to first page
        if (previewOnly == null) {
            // Set a temp list to hide and sort the chapters
            List<EbookChapter> tempChapters = new LinkedList<>();
            // Look over the ebooks
            for (Page ebook : ebooks) {
                String chapterPath = ebook.getPath();
                EbookChapter ebookChapter = new EbookChapter();
                // we are not on the current page, set the values as necessary
                if (!currentPage.getPath().equals(chapterPath)) {
                    ebookChapter.setChapterIntro(
                            ebook.getProperties().get(ConstantsEbook.EbookProperties.SYNOPSYS, String.class));
                    String timeToRead = ebook.getProperties().get(ConstantsEbook.EbookProperties.TIMETOREAD,
                            String.class);
                    if (timeToRead != null && !timeToRead.isEmpty()) {
                        ebookChapter.setChapterTimeToRead(Integer.parseInt(timeToRead));
                    }
                    ebookChapter.setChapterTitle(
                            ebook.getProperties().get(ConstantsEbook.EbookProperties.CHAPTERNAME, String.class));
                    ebookChapter.setChapterUrl(LinkResolver.addHtmlExtension(chapterPath));
                    ebookChapter.setChapterNumber(chapterCount);
                    ebookChapters.add(ebookChapter);
                } else {
                    // Set the current Chapter
                    currentChapter = chapterCount;
                }
                chapterCount++;
            }
            // if temp list is not empty, add it to the end of the list
            if (!tempChapters.isEmpty()) {
                ebookChapters.addAll(tempChapters);
            }
            //Set the current chapter on all the elements on the list
            for(EbookChapter thisChapter : ebookChapters ) {
                thisChapter.setCurrentChapter(currentChapter);
            }
        } else {
            showNav = false;
        }
    }

    /**
     * This method is used to return the ebook list.
     * 
     * @return List<EbookChapter> Next chapter is the first one of the list
     */
    public List<EbookChapter> getEbookChapters() {
        return ebookChapters;
    }

    /**
     * This method is used to return the "Next Chapter" Text from policy.
     * 
     * @return String with the translated word
     */
    public String getNextChapterText() {
        return nextChapterText;
    }

    /**
     * This method is used to return the "Chapter" Text from policy.
     * 
     * @return String with the translated word
     */
    public String getChapterText() {
        return chapterText;
    }

    /**
     * This method is used to return "of" Text from policy.
     * 
     * @return String with the translated word
     */
    public String getOfText() {
        return ofText;
    }

    /**
     * This method is used to return the "minute" Text from policy.
     * 
     * @return String with the translated word
     */
    public String getMinuteText() {
        return minuteText;
    }

    /**
     * This method is used to return the "minutes" Text from policy.
     *
     * @return String with the translated word
     */
    public String getMinutesText() {
        return minutesText;
    }

    /**
     * This method is used to return the "Read Time" Text from policy.
     * 
     * @return String with the translated word
     */
    public String getReadTimeText() {
        return readTimeText;
    }

    /**
     * This method is used to return the Next Chapter Text from policy.
     * 
     * @return boolean true if all the texts has been set, false otherwise
     */
    public boolean getHasPolicy() {
        return hasPolicy;
    }

    /**
     * This method is used to return the total chapter's count
     * 
     * @return in with the chapter's count
     */
    public int getChapterCount() {
        // Subtract last loop
        return chapterCount - 1;
    }

    /**
     * This method is used to set whether the nav should show
     * 
     * @return boolean true if nav should show, false otherwise
     */
    public boolean isShowNav() {
        return showNav;
    }

    /**
     * This method is used to return the total chapter's count
     * 
     * @return String the persona used, null if normal ebook is used
     */
    public String getPersona() {
        return persona;
    }

    /**
     * This method is used to return the total chapter's count
     * 
     * @return String if getPreviewOnly is used, null if full book access is allow
     */
    public String getPreviewOnly() {
        return previewOnly;
    }

    /**
     * get the current chapter to display the correct carrousel slide
     * @return
     */
    public int getCurrentChapter() {
        //is we are at the end, start on zero
        if(currentChapter >=  getChapterCount()) {
            currentChapter = 0;
        }
        //remove last count
        if(currentChapter > 0) {
            currentChapter-=1;
        }
        return currentChapter;
    }
}
