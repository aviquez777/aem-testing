var verifyCookieBanner = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var acceptBanner = element.querySelector('.edc-primary-btn'),
            rejectBanner = element.querySelector('.close-button');
            // Removed temporarly to support GDPR
            // timerId;

        function _activeBanner() {
            element.classList.remove('hide');

            // Removed temporarly to support GDPR
            /* setTimeout(function () {
                element.classList.remove('hide');

                timerId = setTimeout(function () {
                    element.classList.add('hide');
                }, 60000);
            }, 2500); */
        }

        function _deactivateBanner(e) {
            e.preventDefault();
            element.classList.add('hide');
            // Removed temporarly to support GDPR
            // clearTimeout(timerId);
        }

        function _attachEvents() {
            EDC.utils.attachEvents(acceptBanner, 'click', _deactivateBanner);
            EDC.utils.attachEvents(rejectBanner, 'click', _deactivateBanner);
        }

        _attachEvents();
        _activeBanner();
    }

    // Public methods
    function init() {
        var cookieBanner = document.querySelectorAll('.c-cookie-banner:not([data-component-state="initialized"])');

        if (cookieBanner) {
            cookieBanner.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

// This should be uncommented to load component on AEM
document.addEventListener('DOMContentLoaded', verifyCookieBanner.init);
