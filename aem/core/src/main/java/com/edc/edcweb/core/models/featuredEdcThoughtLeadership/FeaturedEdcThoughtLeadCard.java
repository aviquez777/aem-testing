package com.edc.edcweb.core.models.featuredEdcThoughtLeadership;

import com.day.cq.wcm.api.Page;

/**
 * POJO for List
 *
 */

public class FeaturedEdcThoughtLeadCard {
    private int position;
    private String cardType;
    private Page page;
    private String title;
    private String description;
    private String url;
    private String linkTarget;
    private String image;
    private String imageAlign;
    private String imageAlt;
    private String formatType;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLinkTarget() {
        return linkTarget;
    }

    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageAlign() {
        return imageAlign;
    }

    public void setImageAlign(String imageAlign) {
        this.imageAlign = imageAlign;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

}
