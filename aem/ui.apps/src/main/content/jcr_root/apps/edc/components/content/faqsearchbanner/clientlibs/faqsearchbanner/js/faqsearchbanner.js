var faqSearchBannerJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var inputContainer = element.querySelector('.quick-search'),
            inputSearch = inputContainer.querySelector('.cmp-search__input'),
            icon = inputContainer.querySelector('.cmp-search__icon'),
            clearSearch = inputContainer.querySelector('.cmp-search__clear-icon'),
            faqSearch = EDC.utils.getURLParams().faqSearch ? EDC.utils.getURLParams().faqSearch : '',
            inputErrorMessage = inputContainer.querySelector('[data-cmp-hook-search="noResults"]');

        function toggleShow(el, show) {
            if (el) {
                if (show !== false) {
                    el.style.display = 'block';
                    el.setAttribute('aria-hidden', false);
                } else {
                    el.style.display = 'none';
                    el.setAttribute('aria-hidden', true);
                }
            }
        }

        function _trackEvent(numofResults) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventName,
                    eventAction: element.dataset.eventAction,
                    eventText: '',
                    searchQuery: inputSearch.value,
                    destinationPage: '',
                    engagementType: element.dataset.engagementType,
                    eventLevel: element.dataset.eventLevel,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            if (numofResults > 0) {
                obj.eventInfo.numofResults = numofResults;
                obj.eventInfo.searchResultStatus = '';
            } else if (inputErrorMessage) {
                obj.eventInfo.numofResults = 0;
                obj.eventInfo.searchResultStatus = inputErrorMessage.querySelector('span') ? inputErrorMessage.querySelector('span').textContent : 'Display error';
                toggleShow(inputErrorMessage, true);
                toggleShow(clearSearch, true);
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // Attach events
        function _attachEvents() {
            var observerSearch,
                numofResults = 0,
                results;

            // Get query param from previous search
            EDC.utils.attachEvents(window, 'load', function () {
                inputSearch.value = unescape(faqSearch);
                if (inputSearch.value !== '') {
                    toggleShow(clearSearch, true);
                }
            });

            // Handle the input
            EDC.utils.attachEvents(inputSearch, 'input', function () {
                toggleShow(inputErrorMessage, false);
            });

            EDC.utils.attachEvents(clearSearch, 'click', function () {
                toggleShow(inputErrorMessage, false);
            });

            // Observe the attributes changes
            observerSearch = new MutationObserver(function (mutations) {
                mutations.forEach(function (mutation) {
                    if (mutation.attributeName === 'aria-hidden' && mutation.target.getAttribute('aria-hidden') === 'false') {
                        results = inputContainer.querySelectorAll('.cmp-search__results .cmp-search__item');
                        results.forEach(function (data, index) {
                            var url = data.href;

                            if (url) {
                                if (url.indexOf('faqSearch') === -1) {
                                    url = url + '?faqSearch=' + escape(data.text);
                                }
                                data.href = url.replace('/content/edc', '');
                            }

                            data.setAttribute('tabindex', index + 1);
                        });
                        numofResults = results.length;
                        _trackEvent(numofResults);
                    }
                });
            });

            observerSearch.observe(icon, {
                attributes: true // configure it to listen to attribute changes
            });
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var banners = document.querySelectorAll('.c-faq-search-banner:not([data-component-state="initialized"])');

        if (banners) {
            banners.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', faqSearchBannerJS.init);