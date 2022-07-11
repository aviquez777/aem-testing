var contactUsTabs = (function () {
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

            EDC.utils.attachEvents(window, 'resize', function () {
                EDC.utils.resizeTabLabels(tabLabels);
            });
        }

        _attachEvents();

        setTimeout(function () {
            EDC.utils.initializeSwipe(element);
            EDC.utils.scrollToSelectedTab(element, tabLabels);
            EDC.utils.resizeTabLabels(tabLabels);
        }, 1000);
    }

    // Public methods
    function init() {
        var contactUsTabSet = document.querySelectorAll('.c-contact-us-tabs:not([data-component-state="initialized"])');

        if (contactUsTabSet) {
            contactUsTabSet.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', contactUsTabs.init);