var sectionTitles = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var anchor = element.getElementsByClassName('anchor')[0];

        setTimeout(function () {
            if (anchor) {
                EDC.utils.scrollToDeepLink(anchor);
            }
        }, 1000);
    }

    // Public methods
    function init() {
        var sectionTitle = document.querySelectorAll('.section-title:not([data-component-state="initialized"])');

        if (sectionTitle) {
            sectionTitle.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', sectionTitles.init);