var upcomingPastEvents = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var tabLabels = element.querySelectorAll('.tab-label');

        // Private methods
        function _trackEvent(el) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventComponent + ' ' + element.dataset.eventType,
                    eventName: element.dataset.eventComponent + ' ' + el.type,
                    eventAction: element.dataset.eventAction,
                    eventText: el.target.textContent.toLowerCase(),
                    eventValue: '',
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: '',
                    engagementType: element.dataset.eventEngagement
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _tabSetClick(e) {
            _trackEvent(e);
            EDC.utils.displayTabs(e);

            EDC.utils.setHashTag(element.getAttribute('data-component-target'), e.currentTarget.getAttribute('data-link-target'));
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(tabLabels, 'click', _tabSetClick);
        }

        _attachEvents();

        setTimeout(function () {
            EDC.utils.initializeSwipe(element);
            EDC.utils.scrollToSelectedTab(element, tabLabels);
        }, 1000);
    }

    // Public methods
    function init() {
        var upcomingPastEvent = document.querySelectorAll('.c-upcoming-past-events:not([data-component-state="initialized"])');

        if (upcomingPastEvent) {
            upcomingPastEvent.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', upcomingPastEvents.init);