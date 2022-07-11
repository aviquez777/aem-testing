var recommendedArticlesJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var exportJourney = element.closest('.c-export-journey');
        // Private methods

        // Data Layer
        function _trackEvent(e) {
            var elem = e.target.closest('li'),
                list = elem.parentElement,
                elemLink = elem ? elem.querySelector('.title') : '',
                tabButton = exportJourney.querySelector('.tab-labels button.selected'),
                obj = {
                    eventInfo: {
                        eventComponent: list.dataset.eventComponent,
                        eventType: list.dataset.eventType,
                        eventName: list.dataset.eventName,
                        eventAction: elem.dataset.eventAction,
                        eventText: elemLink.innerHTML.toLowerCase(),
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: elemLink.getAttribute('href'),
                        engagementType: list.dataset.eventEngagement,
                        eventLevel: list.dataset.eventLevel,
                        eventValue: 'Step ' + tabButton.dataset.step + ' - ' + tabButton.querySelector('span').innerHTML.toLowerCase(),
                        eventStage: ''
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        // Attach events
        function _attachEvents() {
            var allLinks;

            if (exportJourney) {
                allLinks = element.querySelectorAll('a');
                EDC.utils.attachEvents(allLinks, 'click', _trackEvent);
            }
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var recommendedArticles = document.querySelectorAll('.c-recommended-articles:not([data-component-state="initialized"])');

        if (recommendedArticles) {
            recommendedArticles.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', recommendedArticlesJS.init);