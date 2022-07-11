package com.edc.edcweb.core.components;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import javax.inject.Inject;

@Model(adaptables = Resource.class,
defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CampaignProductCard {

    // Attributes.
    @Inject
    private int cardPosition;

    @Inject
    private String cardImagePath;

    @Inject
    private String cardImageAlt;

    @Inject
    private String cardLabel;

    @Inject
    private String cardTitle;

    @Inject
    private String cardURL;

    @Inject
    private String cardLinkTargetType; // new tab or current.

    @Inject
    private String populationMode; // if Manual or automatic.


    // Setters methods.
    public void setCardPosition(int cardPosition) {
        this.cardPosition = cardPosition;
    }

    public void setCardImagePath(String cardImagePath) {
        this.cardImagePath = cardImagePath;
    }

    public void setCardImageAlt(String cardImageAlt) {
        this.cardImageAlt = cardImageAlt;
    }

    public void setCardLabel(String cardLabel) {
        this.cardLabel = cardLabel;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public void setCardURL(String cardURL) {
        this.cardURL = cardURL;
    }

    public void setCardLinkTargetType(String cardLinkTargetType) {
        this.cardLinkTargetType = cardLinkTargetType;
    }

    public void setPopulationMode(String populationMode) {
        this.populationMode = populationMode;
    }

    // Getters methods.
    public int getCardPosition() {
        return this.cardPosition;
    }

    public String getCardImagePath() {
        return this.cardImagePath;
    }

    public String getCardImageAlt(){
        return this.cardImageAlt;
    }

    public String getCardLabel() {
        return this.cardLabel;
    }

    public String getCardTitle() {
        return this.cardTitle;
    }

    public String getCardURL() {
        return this.cardURL;
    }

    public String getCardLinkTargetType() {
        return this.cardLinkTargetType;
    }

    public String getPopulationMode() {
        return this.populationMode;
    }
}