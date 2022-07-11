package com.edc.edcweb.core.models;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.LinkResolver;
import com.edc.edcweb.core.helpers.constants.ConstantsEbook;
import com.edc.edcweb.core.models.ebook.EbookChapter;
import com.edc.edcweb.core.helpers.EbookHelper;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

/**
 * <h1>Ebook Navigation</h1> Sling model to provide support for the
 * Ebook Chapter Navigation. It pulls in values from the Ebook helper and
 * returns them as a list of the Chapters to be displayed
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class EbookNavigation {

    @Inject
    @Optional
    private Page currentPage;

    @Self
    private SlingHttpServletRequest request;

    private List<EbookChapter> ebook = new LinkedList<>();
    private Boolean preview;

    @PostConstruct
    public void initModel() {
        String previewOnly;
        String persona;
        preview = false;

        try {
            // Retrieve session attributes
            previewOnly = (String) request.getSession().getAttribute(ConstantsEbook.SessionAttr.PREVIEW_ONLY_ATTR);
            String personaVar = (String) request.getSession().getAttribute(ConstantsEbook.SessionAttr.PERSONA_ATTR);
            /*
             * For myEDC when the user is logged in
             *
             * The link on the teaser is a direct link to the chapter
             * Since this link doesn't go through progressive profiling or myEDC interceptor
             * we need to add the persona on a query parameter
             * If there's no persona on the session we try to read the persona from the query parameter
             *
             * The AEM session variable has precedence over the query parameter
             */
            String personaParam = request.getParameter(ConstantsEbook.SessionAttr.PERSONA_ATTR);

            persona = personaVar != null ? personaVar : personaParam;

            if (previewOnly == null) {
                // Get the chapters list, if person is not null will pull order from persona button in the ebook teaser
                ebook = getEbookChapters(persona);
            } else {
                // If preview is in the session the nav should not be displayed
                preview = true;
            }

        } catch (NullPointerException nullPointerException) {
            // Render as regular ebook
            ebook = getEbookChapters(null);
        }
    }

    /**
     * Gets the list of chapters and generates a list of EbookChapters to support htl rendering.
     * The order is defined by the persona attribute, if no person then order is the order of the nodes in crx
     * @param persona
     * @return a lis of Ebook Chapters
     */
    private List<EbookChapter> getEbookChapters(String persona) {
        List<EbookChapter> ebookChapters = new LinkedList<>();
        List<Page> chapterPages = EbookHelper.getEbookPages(currentPage, persona);
        int chapterCount = 1;

        for (Page chapterPage : chapterPages) {
            String chapterPath = chapterPage.getPath();
            EbookChapter ebookChapter = new EbookChapter();

            ebookChapter.setChapterTitle(chapterPage.getProperties().get(ConstantsEbook.EbookProperties.CHAPTERNAME, String.class));
            ebookChapter.setChapterUrl(LinkResolver.addHtmlExtension(chapterPath));

            if (currentPage.getPath().equals(chapterPath)) {
                ebookChapter.setCurrentChapter(chapterCount);
            }

            ebookChapters.add(ebookChapter);
            chapterCount++;
        }
        return ebookChapters;
    }

    public List<EbookChapter> getEbook() {
        return ebook;
    }

    public Boolean getPreview() {
        return preview;
    }
}
