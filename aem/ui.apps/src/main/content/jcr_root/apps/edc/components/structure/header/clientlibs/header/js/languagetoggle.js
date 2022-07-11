var languageToggleJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var originalAriaLabel = element.getAttribute('aria-label');

        // Private methods
        function _setCookieLanguage(e) {
            var language = e.currentTarget.getAttribute('data-language'),
                cookie = 'EDCLanguageCookie';

            EDC.utils.setCookie(cookie, language, 8);

            // EDC.utils.setCookie(cookie, language, 8, '', true, true);
        }

        function _changeLangBtn() {
            var isMobile = window.innerWidth < EDC.props.media.md,
                isCampaign = element.closest('.main-header.campaign-header'),
                lang = element.getAttribute('lang'),
                langText = element.querySelector('.language-d');

            if (isMobile || isCampaign) {
                element.classList.remove('button');
                element.classList.remove('edc-tertiary-btn');
            } else {
                element.classList.add('button');
                element.classList.add('edc-tertiary-btn');
            }

            if (isMobile && lang === 'fr') {
                element.setAttribute('aria-label', langText.textContent);
            } else {
                element.setAttribute('aria-label', originalAriaLabel);
            }
        }

        function _handleKeyPressed(e) {
            var keyCode = e.keyCode || e.which;

            if (keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode) {
                e.preventDefault();
                _setCookieLanguage(e);
                window.location = e.currentTarget.href;
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(element, 'click', _setCookieLanguage);
            EDC.utils.attachEvents(element, 'keydown', _handleKeyPressed);
            EDC.utils.attachEvents(window, 'resize', _changeLangBtn);
            EDC.utils.attachEvents(window, 'load', _changeLangBtn);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var languageToggle = document.querySelectorAll('.language-toggle:not([data-component-state="initialized"])');

        if (languageToggle) {
            languageToggle.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', languageToggleJS.init);