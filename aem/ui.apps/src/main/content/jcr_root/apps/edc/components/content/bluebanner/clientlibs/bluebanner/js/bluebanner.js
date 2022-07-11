var blueBanner = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {

        // Data Layer
        function _trackEvent() {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventName,
                    eventAction: element.dataset.eventAction,
                    eventText: 'NA',
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: element.dataset.eventDestinationPage,
                    engagementType: element.dataset.eventEngagement,
                    eventLevel: element.dataset.eventLevel,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(element, 'click', _trackEvent);
        }

        _attachEvents();

    }

    // Public methods
    function init() {
        var blueBanners = document.querySelectorAll('.c-blue-banner:not([data-component-state="initialized"])');

        if (blueBanners) {
            blueBanners.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', blueBanner.init);