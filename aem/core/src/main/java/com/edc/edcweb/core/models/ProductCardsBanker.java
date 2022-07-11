package com.edc.edcweb.core.models;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.edc.edcweb.core.helpers.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.edc.edcweb.core.models.productCardsBanker.ProductCardsBankerCards;

/**
 * <h1>ProductCardsBanker</h1> Sling model to provide support for the Product
 * Cards Banker component. initModel() @PostConstruct with component's main
 * logic.
 *
 **/

@Model(adaptables = SlingHttpServletRequest.class)
public class ProductCardsBanker {

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Resource resource;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String description;

    @ScriptVariable
    private ValueMap properties;

    private List<ProductCardsBankerCards> cardList;

    private boolean threeColumns;

    @PostConstruct
    public void initModel() {
        if (resource != null) {
            this.threeColumns = resource.getValueMap().get(Constants.Properties.THREE_COLUMNS, Constants.NO).equals(Constants.YES);
            this.cardList = populateCardsList(resource.getChild(Constants.Properties.CARD_ITEMS));
        }
    }

    /**
     * Returns a List of Bullets
     *
     * @param resource a JCR resource.
     * @return List<String> a list of Bullets
     *
     */
    private List<String> populateBulletList(Resource resource) {
        List<String> bullets = new ArrayList<>();
        if (resource != null) {
            for (int i = 1; i < 6; i++) {
                String index = Integer.toString(i);
                String bullet = resource.getValueMap().get(Constants.Properties.BULLET.concat(index), String.class);

                if (bullet != null) {
                    bullets.add(bullet);
                }
            }
        }
        return bullets;
    }

    /**
     * Returns a List of Cards
     *
     * @param resource a JCR resource.
     * @return List<ProductCardsBankerCards> a list of Cards
     * @see ProductCardsBankerCards
     *
     */
    private List<ProductCardsBankerCards> populateCardsList(Resource resource) {
        List<ProductCardsBankerCards> cards = new ArrayList<>();
        List<String> bullets;
        Resource nextResource;
        if (resource != null) {
            Iterator<Resource> resources = resource.listChildren();
            while (resources.hasNext()) {
                nextResource = resources.next();
                ProductCardsBankerCards item = nextResource.adaptTo(ProductCardsBankerCards.class);
                bullets = populateBulletList(nextResource);
                item.setBullets(bullets);
                cards.add(item);
            }
        }
        return cards;
    }

    /**
     * This method is used to return the List of cards to display.
     *
     * @return List<ProductFormCardsItem> List of Cards;
     */
    public List<ProductCardsBankerCards> getCards() {
        return cardList;
    }

    /**
     * Get Title
     *
     * @return String
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get Description
     *
     * @return String
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get Type Display
     *
     * @return String
     *
     */
    public String getTypeDisplay() {
        String type = "";
        if (this.cardList != null) {            
            if (this.threeColumns) {
                type = "odd";
            } else {
                type = this.cardList.size() % 2 == 0 ? "even" : "odd";
            }
        }
        return type;
    }
}