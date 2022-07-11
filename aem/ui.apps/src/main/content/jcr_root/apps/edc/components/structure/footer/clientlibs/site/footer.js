var footerJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var windowInnerWidth;

        // Private methods
        function _setElementsAccesibility() {
            var footerLinks;

            if (windowInnerWidth !== window.innerWidth) {
                footerLinks = element.querySelectorAll('.footer-nav-section .nav-links');

                if (EDC.utils.getDeviceViewPort() !== 'mobile') {
                    footerLinks.forEach(function (elem) {
                        elem.setAttribute('aria-hidden', 'false');
                        elem.parentNode.setAttribute('aria-expanded', 'true');
                    });
                } else {
                    footerLinks.forEach(function (elem) {
                        elem.setAttribute('aria-hidden', 'true');
                        elem.parentNode.setAttribute('aria-expanded', 'false');
                    });
                }
            }

            windowInnerWidth = window.innerWidth;
        }

        function _showNavigationLinks() {
            var isActive;

            if (EDC.utils.getDeviceViewPort() === 'mobile') {
                isActive = this.parentNode.getAttribute('aria-expanded');

                if (isActive === 'false') {
                    this.parentNode.setAttribute('aria-expanded', 'true');
                    this.setAttribute('aria-pressed', true);
                    this.nextElementSibling.setAttribute('aria-hidden', 'false');
                } else {
                    this.parentNode.setAttribute('aria-expanded', 'false');
                    this.setAttribute('aria-pressed', false);
                    this.nextElementSibling.setAttribute('aria-hidden', 'true');
                }
            }
        }


        // Attach events
        function _attachEvents() {
            var footerTitles = element.querySelectorAll('.footer-nav-section .expand');

            EDC.utils.attachEvents(footerTitles, 'click', _showNavigationLinks);
            EDC.utils.attachEvents(window, 'resize', _setElementsAccesibility);
        }

        _attachEvents();
        _setElementsAccesibility();

        setTimeout(function () {
            windowInnerWidth = window.innerWidth;
        }, 2000);
    }

    // Public methods
    function init() {
        var footer = document.querySelectorAll('#footer:not(.campaign-footer):not([data-component-state="initialized"])');

        if (footer) {
            footer.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', footerJS.init);