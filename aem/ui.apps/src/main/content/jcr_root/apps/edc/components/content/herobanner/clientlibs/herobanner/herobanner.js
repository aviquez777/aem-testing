var heroBannerJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var imageBtn = element.querySelector('.edc-primary-btn'),
            buttonText;

        // Data Layer
        function _trackEvent(e, data) {
            var obj = {
                eventInfo: {
                    eventComponent: data.comp,
                    eventType: data.type,
                    eventName: data.name,
                    eventText: data.text,
                    eventAction: element.dataset.eventAction,
                    eventPage: element.dataset.eventPage,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            if (data.engagement) {
                obj.eventInfo.engagementType = data.engagement;
            }

            if (data.level) {
                obj.eventInfo.eventLevel = data.level;
            }

            if (data.destinationPage) {
                obj.eventInfo.destinationPage = data.destinationPage;
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _imageBtnClicked(e) {
            _trackEvent(e, {
                comp: 'hero banner',
                type: 'button',
                name: 'button click - ' + buttonText,
                text: buttonText,
                destinationPage: '#',
                engagement: element.dataset.eventEngagement,
                level: element.dataset.eventLevel
            });
        }

        // Set buttons based on component configuration
        function _setElementData() {
            if (imageBtn) {
                buttonText = imageBtn.querySelector('span').textContent;
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(imageBtn, 'click', _imageBtnClicked);
        }

        _attachEvents();
        _setElementData();
    }

    // Public methods
    function init() {
        var heroBanner = document.querySelectorAll('.c-hero-banner:not([data-component-state="initialized"])');

        if (heroBanner) {
            heroBanner.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', heroBannerJS.init);