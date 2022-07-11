var partnerConsentModalJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            consentValue = element.getAttribute('data-consent-value') !== null ? element.getAttribute('data-consent-value') : '',
            dataDesc = element.getAttribute('data-product-desc') ? element.getAttribute('data-product-desc') : '',
            consent = element.querySelector('.consent'),
            cancel = element.querySelector('.cancel'),
            sideBar,
            timerId = null,
            timeout = 5;

        // Private functions

        // Data Layer
        function _trackEvent(e) {
            var target = e.target.classList.contains('button') ? e.target : e.target.parentElement,
                dataProduct = element.getAttribute('data-product-type') ? element.getAttribute('data-product-type') : '',
                obj = {
                    eventInfo: {
                        eventComponent: consentValue + ' ' + element.dataset.eventComponent.toLowerCase(),
                        eventType: element.dataset.eventType.toLowerCase(),
                        eventName: element.dataset.eventName.toLowerCase(),
                        eventAction: element.dataset.eventAction,
                        eventText: target.querySelector('span').innerText.toLowerCase(),
                        eventValue: dataProduct ? dataProduct.split('_')[1].toLowerCase() : '',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: target.getAttribute('href') ? target.getAttribute('href') : '',
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel
                    }
                },
                userSegment = {
                    productDesc: dataDesc.toLowerCase(),
                    productType: consentValue + '_' + dataProduct
                };

            EDC.utils.userSegmentTracking(userSegment, true);
            EDC.utils.dataLayerTracking(obj);
        }

        // Initializes the sidebar to hide it when the filter is opened.
        function _sideBarInit() {
            if (!!(timerId)) {
                if (timeout === 0) {
                    return;
                }
                if (d.querySelector('.addthis-smartlayers-mobile')) {
                    sideBar = d.querySelector('.addthis-smartlayers-mobile');
                    return;
                }
                timeout -= 1;
            }
            timerId = setTimeout(_sideBarInit, 500);
            return;
        }

        // Helper function to close the modal
        function _closeModal(e) {
            e.preventDefault();

            _trackEvent(e);
            bodyScrollLock.enableBodyScroll(element);
            element.classList.add('hide');

            if (sideBar) {
                sideBar.style.display = 'block';
            }
        }

        // Helper function to open the modal
        function _openModal(product, url) {
            bodyScrollLock.disableBodyScroll(element);
            element.setAttribute('data-escape', true);
            element.setAttribute('data-product-type', product);
            element.classList.remove('hide');
            consent.setAttribute('href', url);

            if (sideBar) {
                sideBar.style.display = 'none';
            }
        }

        // Detects Escape key pressed
        function _detectEsc(e) {
            if (e.keyCode === EDC.props.escapeKeyCode && element.getAttribute('data-escape')) {
                element.removeAttribute('data-escape');
                EDC.utils.simulateClick(cancel);
            }
        }

        // Attach events
        function _attachEvents() {
            var closeBtn = element.querySelector('.close-button'),
                consentBtn = element.querySelector('.consent');

            EDC.utils.attachEvents(consentBtn, 'click', _trackEvent);
            EDC.utils.attachEvents(cancel, 'click', _closeModal);
            EDC.utils.attachEvents(closeBtn, 'click', _closeModal);
            EDC.utils.attachEvents(d, 'keyup', _detectEsc);

            // Listens for the seach component question submission
            EDC.utils.attachEvents(element, 'openModal', function (e) {
                _openModal(e.detail.productType, e.detail.url);
            });
        }

        _attachEvents();
        _sideBarInit();
    }

    // Public methods
    function init() {
        var partnerConsentModal = document.querySelectorAll('.c-partner-consent-modal:not([data-component-state="initialized"])');

        if (partnerConsentModal) {
            partnerConsentModal.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', partnerConsentModalJS.init);