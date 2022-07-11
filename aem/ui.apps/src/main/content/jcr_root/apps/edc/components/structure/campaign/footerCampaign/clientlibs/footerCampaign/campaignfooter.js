var campaignFooterJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            footerInfo = element.querySelector('.footer-bottom-info'),
            footerPosition,
            windowHeight;

        function _setInitialValues() {
            footerPosition = EDC.utils.getPosition(element).y;
            windowHeight = window.innerHeight;
        }

        // Private methods
        function _handleScroll() {
            var scrollTop = (d.scrollingElement && d.scrollingElement.scrollTop) || d.documentElement.scrollTop;

            if (scrollTop >= footerPosition - windowHeight) {
                footerInfo.classList.add('show');
            } else if (footerInfo.classList.contains('show')) {
                footerInfo.classList.remove('show');
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(window, 'scroll', _handleScroll);
            EDC.utils.attachEvents(window, 'resize', function () {
                _setInitialValues();
                _handleScroll();
            });
        }

        _attachEvents();

        setTimeout(function () {
            _setInitialValues();
            _handleScroll();
        }, 2000);
    }

    // Public methods
    function init() {
        var campaignFooter = document.querySelectorAll('footer.campaign-footer:not([data-component-state="initialized"])');

        if (campaignFooter) {
            campaignFooter.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', campaignFooterJS.init);