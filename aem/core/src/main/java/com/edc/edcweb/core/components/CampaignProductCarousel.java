package com.edc.edcweb.core.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.edc.edcweb.core.helpers.Constants;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, resourceType = "edc/components/content/campaigncarousel/campaigncarousel.html", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CampaignProductCarousel {

    @Inject
    private String carouselTitle;

    @Inject
    private List<CampaignProductCard> slides;

    @Inject
    private Resource resource;

    @PostConstruct
    protected void initModel() {

        if (this.slides == null) {

            this.slides = new ArrayList<>();

        } else if (this.slides.size() >= 4 && this.slides.size() <= 10) {

            PageManager pm = resource.getResourceResolver().adaptTo(PageManager.class);
            Page originPage;
            String currentMode;
            String currentURL;
            String imagePath;

            ValueMap vm;

            for (CampaignProductCard card : this.slides) {

                card.setCardPosition(this.slides.indexOf(card));
                currentMode = card.getPopulationMode();
                currentURL = card.getCardURL();

                if (Constants.Properties.AUTOMATIC_POPULATION.equals(currentMode)
                        && StringUtils.isNotBlank(currentURL)) {

                    originPage = pm.getPage(card.getCardURL());
                    vm = originPage.getProperties();

                    if (StringUtils.isNotBlank(vm.get("teaserimage", String.class))) {
                        imagePath = vm.get("teaserimage", String.class);
                    } else if (StringUtils.isNotBlank(vm.get("tabletFileReference", String.class))) {
                        imagePath = vm.get("tabletFileReference", String.class);
                    } else if (StringUtils.isNotBlank(vm.get("fileReference", String.class))) {
                        imagePath = vm.get("fileReference", String.class);
                    } else {
                        imagePath = "Undefined";
                    }

                    card.setCardImagePath(imagePath);
                    card.setCardImageAlt(vm.get("imagealttext", String.class));
                    card.setCardTitle(originPage.getPageTitle());
                    card.setCardURL(currentURL);

                }
            }
        }
    }

    public void setCarouselTitle(String carouselTitle) {
        this.carouselTitle = carouselTitle;
    }

    public String getCarouselTitle() {
        return this.carouselTitle;
    }

    public void setCardsList(List<CampaignProductCard> slides) {
        this.slides = slides;
    }

    public List<CampaignProductCard> getCardsList() {
        return this.slides;
    }

}