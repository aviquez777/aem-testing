var partnerLogInJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var consentModal = document.querySelector('.c-partner-consent-modal'),
            consentValue = consentModal && consentModal.getAttribute('data-consent-value') ? consentModal.getAttribute('data-consent-value') : '';

        // Private methods

        // Data Layer
        function _trackEvent(englishText, product) {
            var obj = {
                eventInfo: {
                    eventComponent: consentValue + ' ' + element.dataset.eventComponent.toLowerCase(),
                    eventType: element.dataset.eventType.toLowerCase(),
                    eventName: element.dataset.eventName.toLowerCase() + ' - ' + englishText,
                    eventAction: element.dataset.eventAction,
                    eventText: englishText,
                    eventValue: product,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: '',
                    engagementType: element.dataset.eventEngagement,
                    eventLevel: element.dataset.eventLevel
                }
            };

            window[window.EDC.datalayerObj].user.segment.productDesc = '';
            window[window.EDC.datalayerObj].user.segment.productType = '';
            EDC.utils.dataLayerTracking(obj);
        }

        // Helper function to handle the click of the LogIn / Register buttons
        function _handleClick(e) {
            var target = e.target,
                elem = target.classList.contains('consent-url') ? target : target.parentElement,
                linkURL = elem.getAttribute('data-url') ? elem.getAttribute('data-url') : '',
                englishText = elem.getAttribute('data-english-text') ? elem.getAttribute('data-english-text').toLowerCase() : elem.querySelector('span').innerText.toLowerCase(),
                linkSections = linkURL ? linkURL.split('_!_') : [],
                consentURL = '',
                event;

            e.preventDefault();

            // Splits the given URL to add the partner value and create a new URL
            if (linkSections.length) {
                // Creates the new URL based on the splited variable
                linkSections.forEach(function (section, index) {
                    consentURL += index === 0 ? section + '+-+' + consentValue : index < linkSections.length - 1 ? '_!_' + section + '_!_' : section + '_!__!_' + consentValue;
                });

                _trackEvent(englishText, linkSections[1].split('_')[1].toLowerCase());

                // Fires the event to open the modal
                event = new CustomEvent('openModal', {
                    detail: {
                        productType: linkSections[1],
                        url: consentURL
                    }
                });

                consentModal.dispatchEvent(event);
            }
        }

        // Attach events
        function _attachEvents() {
            var actionLinks = element.querySelectorAll('.consent-url');

            EDC.utils.attachEvents(actionLinks, 'click', _handleClick);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var partnerLogIn = document.querySelectorAll('.c-partner-login:not([data-component-state="initialized"])');

        if (partnerLogIn) {
            partnerLogIn.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', partnerLogInJS.init);