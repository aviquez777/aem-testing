var companyInsightCorporationJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var btn = element.querySelector('.view-maps');

        // Data Layer
        function _trackClickEvent(e) {
            var el = e.currentTarget,
                parent = el.closest('.c-company-insight-corporation'),
                obj = {
                    eventInfo: {
                        eventComponent: parent.dataset.eventComponent,
                        eventType: el.dataset.eventType,
                        eventName: el.dataset.eventName,
                        eventAction: parent.dataset.eventAction,
                        eventText: el.textContent,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: el.href,
                        engagementType: parent.dataset.engagementType,
                        eventLevel: parent.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _trackLoadEvent() {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventAction: element.dataset.eventAction,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    engagementType: element.dataset.engagementType,
                    eventLevel: element.dataset.eventLevel
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        // Private functions
        function _adjustBackgroundGradient() {
            var backgroundSection = element.querySelector('.background-section'),
                isMobile = window.innerWidth < EDC.props.media.md,
                titleHeight = parseInt(window.getComputedStyle(element.querySelector('h1')).getPropertyValue('height'), 10),
                firstElHeight = parseInt(window.getComputedStyle(element.querySelector('.company-info__section:first-child')).getPropertyValue('height'), 10),
                secondElHeight = parseInt(window.getComputedStyle(element.querySelector('.company-info__section:nth-child(2)')).getPropertyValue('height'), 10),
                thirdElHeight = parseInt(window.getComputedStyle(element.querySelector('.company-info__section:nth-child(3)')).getPropertyValue('height'), 10),
                wholeSectionHeight = parseInt(window.getComputedStyle(backgroundSection).getPropertyValue('height'), 10),
                greySectionPaddingTop = parseInt(window.getComputedStyle(element.querySelector('.grey-section')).getPropertyValue('padding-top'), 10),
                totalBackgroundToCover = titleHeight + (isMobile ? (firstElHeight + secondElHeight + thirdElHeight) : firstElHeight) + greySectionPaddingTop,
                percentageBackgroundToCover = totalBackgroundToCover * 100 / wholeSectionHeight;

            backgroundSection.style.background = 'linear-gradient(to bottom, #2f78c6 ' + percentageBackgroundToCover + '%, #f5f5f5 ' + percentageBackgroundToCover + '%)';
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(btn, 'click', _trackClickEvent);
            EDC.utils.attachEvents(window, 'load', _adjustBackgroundGradient);
            EDC.utils.attachEvents(window, 'resize', _adjustBackgroundGradient);
        }

        _attachEvents();
        _trackLoadEvent();
    }

    // Public methods
    function init() {
        var companyInsightSearchResults = document.querySelectorAll('.c-company-insight-corporation:not([data-component-state="initialized"])');

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

document.addEventListener('DOMContentLoaded', companyInsightCorporationJS.init);