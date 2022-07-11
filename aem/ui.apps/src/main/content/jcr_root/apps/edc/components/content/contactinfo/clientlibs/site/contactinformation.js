var contactInformationJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var cilinks = element.querySelectorAll('.contact-information a');

        // Data Layer
        function _trackEvent(e) {
            // Find closest anchor link
            var elem = e.target.closest('.contact-information'),
                anchor = e.target.closest('a'),
                obj = {
                    eventInfo: {
                        eventComponent: elem.dataset.eventComponent,
                        eventType: elem.dataset.eventType,
                        eventName: anchor.dataset.eventName,
                        eventAction: elem.dataset.eventAction,
                        eventText: anchor.textContent.toLowerCase(),
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: anchor.getAttribute('href').toLowerCase(),
                        engagementType: elem.dataset.eventEngagement,
                        eventLevel: elem.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _attachEvents() {
            var i;

            for (i = 0; i < cilinks.length; i++) {
                EDC.utils.attachEvents(cilinks[i], 'click', _trackEvent);
            }
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var contactInformation = document.querySelectorAll('.contact-information:not([data-component-state="initialized"])');

        if (contactInformation) {
            contactInformation.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', contactInformationJS.init);