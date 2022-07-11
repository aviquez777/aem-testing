package com.edc.edcweb.core.models;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;
import com.edc.edcweb.core.helpers.ArticlePageHelper;
import com.edc.edcweb.core.helpers.SearchForPages;
import com.edc.edcweb.core.helpers.constants.ConstantsHomepage;
import com.edc.edcweb.core.models.featuredEdcThoughtLeadership.FeaturedEdcThoughtLeadCard;

/**
 * <h1>featuredEdcThoughtLeadership</h1> Sling model to provide support for the
 * Home Page's component "Featured EDC thought leadership"
 * initModel() @PostConstruct with compoent's main logic getCard1Page() Returns
 * Card One Premium Data getCard2Page() Returns Card Two Non-Premium Data
 * getCard3Page() Returns Card One Non-Premium Data getRightList() Returns a
 * Page list for the "recent articles" Non-premium list.
 **/
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FeaturedEdcThoughtLead {

    private static final String CARD = "card";
    private static final String CARD_TYPE = CARD.concat("type");

    private static final String CARD_TYPE_PULL_RECENT = "pullfrommostrecent";
    private static final String CARD_TYPE_PAGE_PATH = "pagepath";
    private static final String CARD_TYPE_CUSTOM_AUTHOR = "customauthor";

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Page currentPage;

    @Inject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    @Optional
    private String[] contentTypeTagsP = { ConstantsHomepage.Paths.CONTENT_TYPE_TAGS };

    @ValueMapValue
    @Optional
    private String[] contentTypeTagsNP = { ConstantsHomepage.Paths.CONTENT_TYPE_TAGS };

    @ValueMapValue
    private String baseSearchPage;

    @ValueMapValue
    private String showdate;

    private ArticlePageHelper articleHelper = new ArticlePageHelper();

    private List<FeaturedEdcThoughtLeadCard> cardList = new LinkedList<>();

    private String searchPath;

    private int maxNumberCards = 8;

    /**
     * This method is used to retrieve, sort and set the variables to return on the
     * getter methods Will check if the user has manually included any of the 3
     * possible cards, Will search for the pages if necessary Compare the manually
     * selected card to the ones on the Dynamic results to avoid duplicates Will
     * ensure the Right List, wil have no more than 5 items
     *
     **/
    @PostConstruct
    public void initModel() {
        searchPath = StringUtils.isNotBlank(baseSearchPage) ? baseSearchPage : currentPage.getPath();

        // if current page has no children, look on parent page
        if (StringUtils.isBlank(baseSearchPage) && !currentPage.listChildren().hasNext()
                && currentPage.getParent() != null) {
            searchPath = currentPage.getParent().getPath();
        }

        List<Page> pagesFromSearchPremium = getPremiumPagesFromTags();
        List<Page> pagesFromSearchNonPremium = getPagesFromTags();

        // if no cards check properties to fill cardList
        ValueMap vm = request.getResource().getValueMap();
        int nonPremumCounter = 0;
        for (int c = 1; c <= maxNumberCards; c++) {
            String cardType = CARD_TYPE_PULL_RECENT;
            // backwards compatibility, get cards 1-3 if defined and use it
            String cardKey = CARD.concat(Integer.toString(c));
            if (vm.containsKey(cardKey)) {
                cardType = CARD_TYPE_PAGE_PATH;
            }
            // check if we have the new CardType, if so it'll win
            String cardTypeKey = CARD_TYPE.concat(Integer.toString(c));
            if (vm.containsKey(cardTypeKey)) {
                cardType = vm.get(cardTypeKey, CARD_TYPE_PULL_RECENT);
            }
            Page page = null;
            switch (cardType) {
            case CARD_TYPE_PULL_RECENT:
                if (c == 1) {
                    if (!pagesFromSearchPremium.isEmpty()) {
                        page = pagesFromSearchPremium.get(0);
                    }
                } else {
                    if (pagesFromSearchNonPremium.size() > nonPremumCounter) {
                        page = pagesFromSearchNonPremium.get(nonPremumCounter);
                        nonPremumCounter++;
                    }
                }
                if (page != null) {
                    addCard(fillCardWithPageData(cardType, page, c));
                }
                break;
            case CARD_TYPE_PAGE_PATH:
                showdate = "no";
                String cardPath = vm.get(cardKey, "");
                if (StringUtils.isNotBlank(cardPath)) {
                    addCardByPath(cardType, cardPath, c);
                }
                break;
            case CARD_TYPE_CUSTOM_AUTHOR:
                showdate = "no";
                addCustomCard(cardType, vm, c);
                break;
            default:
                break;
            }
        }

        // if not enough cards do not send list
        if (cardList.size() < maxNumberCards) {
            cardList = null;
        } else {
            // order by position
            cardList.sort(Comparator.comparingInt(FeaturedEdcThoughtLeadCard::getPosition));
        }
    }

    private List<Page> getPagesFromTags() {
        // Get the Non-premium pages
        String[] tagIds = { ConstantsHomepage.Paths.ACCESS_TYPE_NONPREMIUM };
        tagIds = ArrayUtils.addAll(tagIds, contentTypeTagsNP);
        // all non-premium teasers should be tagged with the “non premium tag” to be
        // identified to display as the smaller card

        return SearchForPages.getPagesByTagIds(request, tagIds, false, searchPath);
    }

    private List<Page> getPremiumPagesFromTags() {
        // Get the premium teasers
        String[] premiumTagIds = { ConstantsHomepage.Paths.ACCESS_TYPE_PREMIUM };
        premiumTagIds = ArrayUtils.addAll(premiumTagIds, contentTypeTagsP);
        // all premium teasers should be tagged with the “premium tag” to be identified
        // to display as the larger card
        return SearchForPages.getPagesByTagIds(request, premiumTagIds, false, searchPath);
    }

    private void addCardByPath(String cardType, String path, int position) {
        Resource res = resourceResolver.getResource(path);
        if (res != null) {
            Page page = res.adaptTo(Page.class);
            if (page != null) {
                addCard(fillCardWithPageData(cardType, page, position));
            }
        }
    }

    private void addCard(FeaturedEdcThoughtLeadCard card) {
        cardList.add(card);
    }

    private FeaturedEdcThoughtLeadCard fillCardWithPageData(String cardType, Page page, int position) {
        articleHelper.getPropertiesFromPage(request, page);
        FeaturedEdcThoughtLeadCard card = new FeaturedEdcThoughtLeadCard();
        card.setTitle(page.getTitle());
        card.setPosition(position);
        card.setCardType(cardType);
        card.setPage(page);
        return card;
    }

    private void addCustomCard(String cardType, ValueMap vm, int position) {
        FeaturedEdcThoughtLeadCard card = new FeaturedEdcThoughtLeadCard();
        String posStr = Integer.toString(position);

        card.setPosition(position);
        card.setCardType(cardType);
        card.setPage(null);
        String fieldKey = "title".concat(posStr);
        if (vm.containsKey(fieldKey)) {
            card.setTitle(vm.get(fieldKey, ""));
        }
        fieldKey = "description".concat(posStr);
        if (vm.containsKey(fieldKey)) {
            card.setDescription(vm.get(fieldKey, ""));
        }
        fieldKey = "url".concat(posStr);
        if (vm.containsKey(fieldKey)) {
            card.setUrl(vm.get(fieldKey, ""));
        }
        fieldKey = "linkTarget".concat(posStr);
        if (vm.containsKey(fieldKey)) {
            card.setLinkTarget(vm.get(fieldKey, ""));
        }
        fieldKey = "externalImageReference".concat(posStr);
        if (vm.containsKey(fieldKey)) {
            card.setImage(vm.get(fieldKey, ""));
        }
        fieldKey = "imageAlign".concat(posStr);
        if (vm.containsKey(fieldKey)) {
            card.setImageAlign(vm.get(fieldKey, ""));
        }
        fieldKey = "imageAlt".concat(posStr);
        if (vm.containsKey(fieldKey)) {
            card.setImageAlt(vm.get(fieldKey, ""));
        }
        fieldKey = "formatType".concat(posStr);
        if (vm.containsKey(fieldKey)) {
            card.setFormatType(vm.get(fieldKey, ""));
        }
        cardList.add(card);
    }

    /**
     * This method is used to return a list of Cards
     *
     * @return List<FeaturedEdcThoughtLeadCard> with Dynamically created list
     */
    public List<FeaturedEdcThoughtLeadCard> getCardList() {
        return cardList;
    }

    /**
     * This method is used to check whether of not to display the date;
     * 
     * @return true if date is to be shown
     */
    public String isShowdate() {
        return showdate;
    }
}