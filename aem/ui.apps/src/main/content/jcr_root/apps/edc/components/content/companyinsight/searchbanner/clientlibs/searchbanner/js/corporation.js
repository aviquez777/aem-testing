var companyInsightCorporationBannerJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var showSearchBtn = element.querySelector('.show-search'),
            searchArea = element.querySelector('.search-content');

        function _toggleSearchArea() {
            if (searchArea.classList.contains('hide')) {
                searchArea.classList.remove('hide');
                setTimeout(function () {
                    searchArea.classList.add('over-flow');
                }, 500);
            } else {
                searchArea.classList.remove('over-flow');
                searchArea.classList.add('hide');
            }
        }

        function _checkDeviceForSearchArea() {
            var isMobile = window.innerWidth < EDC.props.media.md;

            if ((!isMobile && searchArea.classList.contains('hide')) || (isMobile && !searchArea.classList.contains('hide'))) {
                _toggleSearchArea();
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(showSearchBtn, 'click', _toggleSearchArea);
            EDC.utils.attachEvents(window, 'resize', _checkDeviceForSearchArea);
        }

        _checkDeviceForSearchArea();
        _attachEvents();
    }

    // Public methods
    function init() {
        var companyInsightCorporationBanner = document.querySelectorAll('.c-company-insight-corporation-banner:not([data-component-state="initialized"])');

        if (companyInsightCorporationBanner) {
            companyInsightCorporationBanner.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', companyInsightCorporationBannerJS.init);