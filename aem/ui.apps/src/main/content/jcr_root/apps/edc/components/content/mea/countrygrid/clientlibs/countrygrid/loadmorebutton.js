var handleLoadMoreButton = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var startQty;

        function _howManyToLoad() {
            var load = 12,
                w = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;

            if (w <= EDC.props.media.sm) {
                load = 6;
            }

            startQty = load;
        }

        function _loadMore(e) {
            var counter = 1,
                currentCount = startQty,
                countries,
                buttonShown = false,
                buttonHidden = false,
                loadMoreButton;

            if (e && e.preventDefault) {
                e.preventDefault();
            }

            // get the countries
            countries = element.querySelectorAll('.country-grid-item.hide');

            // get the button
            loadMoreButton = element.querySelector('.country-grid-btn');

            if (countries) {
                // loop over the countries and show them as necessary
                countries.forEach(function (country) {
                    if (counter <= currentCount) {
                        country.classList.remove('hide');
                    }

                    // check if button needs to be shown
                    if (counter > currentCount && !buttonShown) {
                        loadMoreButton.classList.remove('hide');
                        buttonShown = true;
                    }

                    // if we loaded all but next loop we have all
                    // records, hide
                    // button
                    if (currentCount >= countries.length && loadMoreButton !== undefined && !buttonHidden) {
                        loadMoreButton.classList.add('hide');
                        buttonHidden = true;
                    }

                    counter++;
                });
            }
        }

        function _resizeWindow() {
            setTimeout(function () {
                _howManyToLoad();
            }, 10);
        }

        function _attachEvents() {
            var loadMoreBtns = element.querySelectorAll('.country-grid-btn');

            EDC.utils.attachEvents(loadMoreBtns, 'click', _loadMore);
            EDC.utils.attachEvents(window, 'resize', _resizeWindow);
        }

        _attachEvents();
        _howManyToLoad();
        _loadMore();
    }

    // Public methods
    function init() {
        var countryGridTabPanels = document.querySelectorAll('.country-grid .tab-panel:not([data-component-state="initialized"])');

        if (countryGridTabPanels) {
            countryGridTabPanels.forEach(function (elem) {
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
document.addEventListener('DOMContentLoaded', handleLoadMoreButton.init);