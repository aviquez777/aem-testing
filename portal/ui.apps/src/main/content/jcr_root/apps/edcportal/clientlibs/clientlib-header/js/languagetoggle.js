var languageToggleJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private methods
        function _setCookieLanguage(e) {
            var language = e.currentTarget.getAttribute('data-language'),
                cookie = 'EDCLanguageCookie';

            // This should be uncommented to load component on AEM
            EDC.utils.setCookie(cookie, language, 8, '', true, true);
        }

        function _changeLangBtn() {
            var isMobile = window.innerWidth < EDC.props.media.md;

            if (isMobile) {
                element.classList.remove('button');
                element.classList.remove('edc-tertiary-btn');
            } else {
                element.classList.add('button');
                element.classList.add('edc-tertiary-btn');
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(element, 'click', _setCookieLanguage);
            EDC.utils.attachEvents(window, 'resize', _changeLangBtn);
            EDC.utils.attachEvents(window, 'load', _changeLangBtn);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var languageToggle = document.querySelectorAll('.main-header .language-toggle:not([data-component-state="initialized"])');

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
