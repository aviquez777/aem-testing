package com.edc.edcweb.core.models;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**
 *
 * @author monica.castillo
 * @since Feb 06 2018
 * @version 1.0
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PictureWithTwoColumns {

    @Inject
    @Named("title")
    @Optional
    private String title;

    @Inject
    @Named("image")
    @Optional
    private String image;

    @Inject
    @Named("altText")
    @Optional
    private String altText;

    @Inject
    @Named("showPictureRight")
    @Optional
    private String showPictureRight;

    @Inject
    @Named("items")
    @Optional
    private List<TwoColumnText> twoColumnTextElements;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @return the altText
     */
    public String getAltText() {
        return altText;
    }

    /**
     * @return the showPictureRight
     */
    public String getShowPictureRight() {
        return showPictureRight;
    }

    /**
     * @return the multiColumnTextModels
     */
    public List<TwoColumnText> getTwoColumnTextElements() {
        return twoColumnTextElements;
    }
}