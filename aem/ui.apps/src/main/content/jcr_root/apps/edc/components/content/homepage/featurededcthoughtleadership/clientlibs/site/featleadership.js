var featleadership = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var cards = element.querySelectorAll('.content-recent-articles .content-articles');

        function _loadMore() {
            var counter = 1,
                currentCount = window.innerWidth >= 960 ? 5 : 2;

            // loop over the cards and show them as necessary
            if (cards) {
                cards.forEach(function (item) {
                    if (counter <= currentCount) {
                        item.classList.remove('hide');
                    }

                    counter++;
                });
            }
        }

        function _resizeWindow() {
            cards.forEach(function (item) {
                item.classList.add('hide');
            });

            _loadMore();
        }

        function _attachLoadMoreEvents() {
            EDC.utils.attachEvents(window, 'resize', _resizeWindow);
        }

        // Tracking purposes
        function _trackEvent(e) {
            var clickedEl,
                obj;

            clickedEl = e.currentTarget;
            obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventAction: clickedEl.dataset.eventAction,
                    eventType: clickedEl.dataset.eventType,
                    eventLevel: clickedEl.dataset.eventLevel,
                    eventName: clickedEl.dataset.eventName + ' - ' + element.dataset.eventComponent,
                    eventText: clickedEl.querySelector('h4.title').innerHTML,
                    engagementType: clickedEl.dataset.eventEngagement,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: clickedEl.getAttribute('href')
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _attachTrackingEvents() {
            var contentArticles = element.querySelectorAll('.content-info a'),
                recentArticles = element.querySelectorAll('.content-recent-articles a');

            EDC.utils.attachEvents(contentArticles, 'click', _trackEvent);
            EDC.utils.attachEvents(recentArticles, 'click', _trackEvent);
        }

        _resizeWindow();
        _attachLoadMoreEvents();
        _attachTrackingEvents();
    }

    // Public methods
    function init() {
        var elements = document.querySelectorAll('.featured-edc-thought-leadership:not([data-component-state="initialized"])');

        if (elements) {
            elements.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', featleadership.init);