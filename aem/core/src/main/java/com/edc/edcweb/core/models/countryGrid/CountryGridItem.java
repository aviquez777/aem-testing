package com.edc.edcweb.core.models.countryGrid;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * <h1>CountryGridItem</h1> Resource Model for the Country Grid Item Object.
 * String countryName Country's name from page properties; private
 * String countryLink relative link from the page (uses
 * LinkResolver.addHtmlExtension() utility class) 
 * String countryImage the image defined on
 * Constants.Properties.TEASER_IMAGE_SOURCE;
 * String countryImageAltTxt the Image's alternate text defined on
 * Constants.Properties.ARTICLE_IMAGE_ALT_TEXT;
 **/

@Model(adaptables = Resource.class)
public class CountryGridItem {

    private String countryName;

    private String countryLink;

    private String countryImage;

    private String countryImageAltTxt;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryLink() {
        return countryLink;
    }

    public void setCountryLink(String countryLink) {
        this.countryLink = countryLink;
    }

    public String getCountryImage() {
        return countryImage;
    }

    public void setCountryImage(String countryImage) {
        this.countryImage = countryImage;
    }

    public String getCountryImageAltTxt() {
        return countryImageAltTxt;
    }

    public void setCountryImageAltTxt(String countryImageAltTxt) {
        this.countryImageAltTxt = countryImageAltTxt;
    }

}
