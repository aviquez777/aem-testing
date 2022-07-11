var companyInsightSearchResultsJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            btn = element.querySelector('.main-actions .no-btn'),
            dataPageCount = parseInt(element.getAttribute('data-page-count'), 10),
            searchBar = d.querySelector('form.c-company-insight-search-bar'),
            resultsSection = element.querySelector('.results-section');

        // Data Layer
        function _trackEvent() {
            var numOfResults = element.querySelectorAll('.result').length,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventAction: element.dataset.eventAction,
                        eventText: searchBar ? searchBar.querySelector('.actions button span').textContent : null,
                        errorType: element.dataset.errorType ? element.dataset.errorType : '',
                        searchQuery: searchBar ? searchBar.querySelector('input[name="name"]').value : null,
                        numofResults: numOfResults ? numOfResults : 0,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        engagementType: element.dataset.engagementType,
                        eventLevel: element.dataset.eventLevel,
                        eventCountry: searchBar ? searchBar.querySelector('input[name="country"]').value : null,
                        errorValue: resultsSection ? '' : element.querySelector('.section-blurb div').textContent
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        // Private functions
        function _showMore() {
            var hiddenEls = element.querySelectorAll('.result.hide');

            hiddenEls.forEach(function (elem, i) {
                if (i < dataPageCount) {
                    elem.classList.remove('hide');
                } else {
                    return;
                }
            });

            if (hiddenEls.length <= dataPageCount) {
                btn.classList.add('hide');
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(btn, 'click', _showMore);
        }

        _attachEvents();
        _trackEvent();
    }

    // Public methods
    function init() {
        var companyInsightSearchResults = document.querySelectorAll('.c-company-insight-search-results:not([data-component-state="initialized"])');

        if (companyInsightSearchResults) {
            companyInsightSearchResults.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', companyInsightSearchResultsJS.init);