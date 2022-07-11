var AccordionPanelToggle = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var accordionBttns = document.querySelectorAll('.panel .panel-label');

        // Data Layer
        function _trackEvent() {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventName + ' - ' + element.getAttribute('name'),
                    eventAction: element.dataset.eventAction,
                    eventValue: '',
                    eventText: '',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: '',
                    engagementType: element.dataset.eventEngagement,
                    eventLevel: element.dataset.eventLevel
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _closeAllOtherTabs() {
            accordionBttns.forEach(function (elem) {
                var parent = elem.closest('.button-heading'),
                    grandParent = elem.closest('.panel');

                if (elem) {
                    elem.setAttribute('aria-expanded', 'false');
                }

                if (grandParent) {
                    grandParent.classList.remove('open');
                }

                if (parent) {
                    parent.nextElementSibling.setAttribute('aria-hidden', 'true');
                }
            });
        }

        function _handleClicks() {
            var parent = this.closest('.button-heading'),
                grandParent = this.closest('.panel'),
                isActive = this ? this.getAttribute('aria-expanded') : null,
                stayOpen = grandParent ? grandParent.getAttribute('data-stay-open') : null;

            if (isActive && isActive === 'false') {
                if (stayOpen && stayOpen !== 'true') {
                    _closeAllOtherTabs();
                }

                if (this) {
                    this.setAttribute('aria-expanded', 'true');
                }

                if (grandParent) {
                    grandParent.classList.add('open');
                }

                if (parent) {
                    parent.nextElementSibling.setAttribute('aria-hidden', 'false');
                }

                _trackEvent();
            } else {
                if (this) {
                    this.setAttribute('aria-expanded', 'false');
                }

                if (grandParent) {
                    grandParent.classList.remove('open');
                }

                if (parent) {
                    parent.nextElementSibling.setAttribute('aria-hidden', 'true');
                }

                _trackEvent();
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
        var accordionPanel = document.querySelectorAll('.panel:not(.ref-accordion):not([data-component-state="initialized"])');

        if (accordionPanel) {
            accordionPanel.forEach(function (elem) {
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

document.addEventListener('DOMContentLoaded', AccordionPanelToggle.init);