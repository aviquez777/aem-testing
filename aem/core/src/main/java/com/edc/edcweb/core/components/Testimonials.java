package com.edc.edcweb.core.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.Resource;
import com.adobe.cq.sightly.WCMUsePojo;
import com.edc.edcweb.core.helpers.Constants;
import com.edc.edcweb.core.models.Slide;

/**
 * @author Scott Ross
 * @version 1.0.0
 * @since 1.0.0
 *
 *
 * Backing Java code for the Testimonials component used by the EDC web site.
 *
 *
 */
public class Testimonials extends WCMUsePojo {

    private static final Logger log = LoggerFactory.getLogger(Testimonials.class);

    private List<Slide> slides;
    private String initialBackground;

    /**
     * Populates the Testimonials images for phone, tablet and desktop breakpoints.
     *
     */
    @Override
    public void activate() throws Exception {
        Resource resource = getResource();
        //---------------------------------------------------------------------
        log.debug("resource: {}", resource.getPath());

        slides = populateSlides(resource.getChild(Constants.Properties.SLIDES));
        if (slides.size() > 0) {
            this.initialBackground = this.slides.get(0).getFileReference();
        }
    }

    /**
     * Returns an List of Slide objects, populated from the details stored under the current page.
     * <p>
     */
    public List<Slide> populateSlides(Resource resource) {
        java.util.List<Slide> listOfSlides = new ArrayList<>();
        if (resource != null) {
            Iterator<Resource> resources = resource.listChildren();
            while (resources.hasNext()) {
                Slide item = resources.next().adaptTo(Slide.class);
                listOfSlides.add(item);
            }
        }
        return listOfSlides;
    }

    public List<Slide> getSlides() {
        return new ArrayList<>(this.slides);
    }

    /**
     * AC #3 of brand update task #14221
     * Must use the fields from the first card only
     * @return the first slide
     */
    public Slide getSlide() {
        return this.slides.get(0);
    }

    public String getInitialBackground() {
        return initialBackground;
    }
}