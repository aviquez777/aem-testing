
var collapsibleContentComponentJS = (function () {
    'use strict';

    function _initialize(element) {
        var collapsibleContainer = element.querySelector('.collapsible-related-content'),
            viewMoreCTA = element.querySelector('.view-more-cta'),
            viewMoreCTAText = viewMoreCTA.getAttribute('data-view-more-text'),
            viewLessCTAText = viewMoreCTA.getAttribute('data-view-less-text');

        viewMoreCTA.innerHTML = viewMoreCTAText;

        function _toggleExpandAndCollapse(e) {
            e.preventDefault();
            if (collapsibleContainer.classList.contains('collapse')) {
                collapsibleContainer.classList.remove('collapse');
                collapsibleContainer.classList.add('expand');
                viewMoreCTA.innerHTML = viewLessCTAText;
            } else {
                collapsibleContainer.classList.remove('expand');
                collapsibleContainer.classList.add('collapse');
                viewMoreCTA.innerHTML = viewMoreCTAText;
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(viewMoreCTA, 'click', _toggleExpandAndCollapse);
        }

        _attachEvents();
    }

    function init() {
        var collapsibleContentComponent = document.querySelectorAll('.c-collapsible-content-component:not([data-component-state="initialized"])');

        if (collapsibleContentComponent) {
            collapsibleContentComponent.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', collapsibleContentComponentJS.init);