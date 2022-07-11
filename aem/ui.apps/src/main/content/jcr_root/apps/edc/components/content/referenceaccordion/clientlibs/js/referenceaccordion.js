var referenceAccordionPanelJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var accordionBttns = document.querySelectorAll('.ref-accordion.panel .panel-label');

        // Data Layer
        function _trackEvent() {
            var el = element.closest('.ref-accordion.panel'),
                obj = {
                    eventInfo: {
                        eventComponent: el.dataset.eventComponent,
                        eventAction: el.dataset.eventAction,
                        eventText: el.dataset.eventText,
                        destinationPage: '',
                        engagementType: el.dataset.eventEngagement,
                        eventLevel: el.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName(),
                        eventType: el.dataset.eventType,
                        eventName: el.dataset.eventType + ' click - ' + el.dataset.eventName
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _closeAllOtherTabs() {
            accordionBttns.forEach(function (elem) {
                elem.parentNode.parentNode.setAttribute('aria-expanded', 'false');
                elem.parentNode.parentNode.classList.remove('open');
                elem.parentNode.nextElementSibling.setAttribute('aria-hidden', 'true');
            });
        }

        function _handleClicks() {


            var isActive = this.parentNode.parentNode.getAttribute('aria-expanded'),
                stayOpen = this.parentNode.parentNode.getAttribute('data-stay-open');

            if (isActive === 'false') {
                if (stayOpen !== 'true') {
                    _closeAllOtherTabs();
                }
                this.parentNode.parentNode.setAttribute('aria-expanded', 'true');
                this.parentNode.parentNode.classList.add('open');
                this.parentNode.nextElementSibling.setAttribute('aria-hidden', 'false');
                _trackEvent();
            } else {
                this.parentNode.parentNode.setAttribute('aria-expanded', 'false');
                this.parentNode.parentNode.classList.remove('open');
                this.parentNode.nextElementSibling.setAttribute('aria-hidden', 'true');
            }

            EDC.utils.scrollTo('top', EDC.utils.getPosition(this).y - 150);
        }

        function _attachEvents() {
            // Toggle Panels On/Off on Click
            EDC.utils.attachEvents(element, 'click', _handleClicks);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var referenceAccordionPanel = document.querySelectorAll('.ref-accordion.panel:not([data-component-state="initialized"])');

        if (referenceAccordionPanel) {
            referenceAccordionPanel.forEach(function (elem) {
                var panelLabel = elem.querySelector('.panel-label');

                _initialize(panelLabel);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }
    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', referenceAccordionPanelJS.init);