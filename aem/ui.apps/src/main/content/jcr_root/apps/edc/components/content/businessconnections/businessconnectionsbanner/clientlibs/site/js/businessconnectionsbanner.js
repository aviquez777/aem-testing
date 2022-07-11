var businessConnectionsBannerJS = (function () {
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
                    eventPage: EDC.props.pageNameURL,
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
                comp: 'business connections banner',
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

        _setElementData();
        _attachEvents();
    }

    // Public methods
    function init() {
        var businessConnectionsBanners = document.querySelectorAll('.c-business-connections-banner:not([data-component-state="initialized"])');

        if (businessConnectionsBanners) {
            businessConnectionsBanners.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', businessConnectionsBannerJS.init);