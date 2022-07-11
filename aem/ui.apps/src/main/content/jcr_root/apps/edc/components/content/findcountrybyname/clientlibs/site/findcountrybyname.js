var findCountryByNameJS = (function () {
    'use strict';

    function _initialize(element) {
        var searchBox = element.querySelector('.search-box'),
            searchList = searchBox.querySelector('ul'),
            searchInput = searchBox.querySelector('.search-input'),
            searchBtn = searchBox.querySelector('button'),
            searchResults = searchBox.querySelector('.search-results'),
            countries = searchBox.querySelectorAll('ul li'),
            activeCountriesArray = [],
            searchResultsText = searchBox.dataset.resultsText || '',
            searchNoResultsText = searchBox.dataset.noresultsText || '',
            directionsText = searchBox.dataset.directionsText || '',
            screenReaderText = element.querySelector('#sr-announce');

        function _announceResults() {
            var number = activeCountriesArray.length,
                textToRead = number + ' ' + searchResultsText + ' ' + directionsText;

            if (activeCountriesArray.length === 0) {
                textToRead = searchNoResultsText;
            }

            screenReaderText.innerHTML = textToRead;
        }

        // Tracking purposes
        function _trackEvent(e) {
            var anchor = e.target.closest('a'),
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: 'search autocomplete ' + e.type,
                        eventAction: 'find country by name',
                        eventText: anchor.children[0].textContent.toLowerCase(), // name of country click
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: anchor.getAttribute('href').toLowerCase(),
                        engagementType: element.dataset.eventEngagement
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _matchName(name, acronyms, pattern) {
            var words = name.trim().split(' '),
                match = false,
                exceptionsWords = ['and', 'of', 'et', 'de'],
                realPattern = pattern.trim(),
                patternSpace = /\s/.test(realPattern),
                i;

            acronyms = acronyms.split(',');

            if (!patternSpace && realPattern !== '') {
                for (i = 0; i < words.length; i++) {
                    if (exceptionsWords.indexOf(words[i]) < 0 && words[i].indexOf(realPattern) === 0) {
                        match = true;
                    }
                }
            } else if (patternSpace && name.indexOf(realPattern) >= 0) {
                match = true;
            }

            if (match !== true && !patternSpace) {
                for (i = 0; i < acronyms.length; i++) {
                    if (acronyms[i].indexOf(realPattern) === 0) {
                        match = true;
                    }
                }
            }

            return match;
        }

        function _clearSelected() {
            var selected = searchResults.querySelector('[aria-selected="true"]');

            if ((typeof (selected) !== 'undefined') && selected !== null) {
                selected.setAttribute('aria-selected', 'false');
            }
        }

        function _focusOnElement(elem) {
            if (typeof (elem) !== 'undefined' && elem !== null) {
                _clearSelected();
                elem.setAttribute('aria-selected', true);
                elem.querySelector('a').focus();
            }
        }


        function _haveResults() {
            // TODO Review
            // resultList = searchResults.querySelectorAll('li:not(.hidden):not(.headline)');
            // console.log('_haveResults:' + activeCountriesArray.length);
            return activeCountriesArray.length > 0;
        }

        function _hideActiveCountries() {
            var i;

            for (i = 1; i < countries.length; i++) {
                countries[i].classList.add('hidden');
            }

            activeCountriesArray = [];
        }

        function _getResults(pattern) {
            var results = 0,
                i,
                name,
                acronyms;

            countries[0].classList.remove('hidden');

            _hideActiveCountries();

            for (i = 1; (results < 6 && i < countries.length); i++) {
                name = countries[i].querySelector('a > .column:first-child');
                acronyms = name && name.getAttribute('data-acronyms') ? name.getAttribute('data-acronyms') : '';
                if (name && _matchName(name.innerHTML.toLowerCase(), acronyms.toLowerCase(), pattern.toLowerCase())) {
                    countries[i].classList.remove('hidden');
                    activeCountriesArray.push(countries[i]);
                    results++;
                }
            }

            // Show No Results
            if (results <= 0) {
                countries[0].classList.add('hidden');
                countries[countries.length - 1].classList.remove('hidden');
                // _announceResults();
            }
        }

        function _clearInput() {
            searchInput.value = '';
            searchInput.focus();
        }

        function _closeSearch() {
            if (searchBox.classList.contains('open')) {
                searchInput.value = '';
                searchBox.classList.remove('open');
                searchList.setAttribute('aria-selected', false);
                _hideActiveCountries();
            }

            _clearSelected();
            searchInput.focus();


            // Enables page scrolling again
            // bodyScrollLock.enableBodyScroll(searchBox);
        }

        function _showSearchResult(pattern) {
            searchBtn.setAttribute('aria-label', searchBtn.dataset.closeLabel);
            searchBtn.setAttribute('tabindex', 0);
            searchBox.classList.add('open');
            searchResults.setAttribute('aria-hidden', false);
            searchList.setAttribute('aria-selected', true);

            // Disables scrolling on body and html elements on all devices
            // bodyScrollLock.disableBodyScroll(searchBox);

            _getResults(pattern);
            _announceResults();
        }

        function _hideSearchResult() {
            activeCountriesArray = [];
            searchBtn.setAttribute('aria-label', searchBtn.dataset.openLabel);
            searchBtn.setAttribute('tabindex', -1);
            searchBox.classList.remove('open');
            searchResults.setAttribute('aria-hidden', true);
            searchList.setAttribute('aria-selected', false);
            _clearInput();
        }

        function _filterCountries(e) {
            var pattern = this.value;

            switch (e.keyCode) {
                case EDC.props.arrowDownKeyCode:
                    if (_haveResults()) {
                        _focusOnElement(searchResults.querySelector('li:not(.hidden):not(.headline)'));
                    }
                    break;

                case EDC.props.escapeKeyCode:
                    _hideSearchResult();
                    break;

                default:
                    if (pattern.length > 0) {
                        _showSearchResult(pattern);
                    } else {
                        _hideSearchResult();
                    }
                    _announceResults();
            }
        }

        function _isMobile() {
            return window.innerWidth < EDC.props.media.lg;
        }

        function _contentBannerPosition() {
            var contentBannerContainer = element.querySelector('.container'),
                contentBanner = contentBannerContainer.querySelector('.content'),
                contentBannerHeight = parseInt(window.getComputedStyle(contentBanner).getPropertyValue('height'), 10),
                extraMargin = _isMobile() ? 0 : 25;

            contentBannerContainer.style.marginTop = '-' + ((contentBannerHeight / 2) + extraMargin) + 'px';
        }

        function _findNextElement() {
            var i,
                nextElem,
                results = searchResults.querySelectorAll('li:not(.hidden):not(.headline)');

            // When this function is called, there's at least 1 element in the Array
            if (activeCountriesArray.length > 1) {
                for (i = 0; i < activeCountriesArray.length; i++) {
                    if (activeCountriesArray[i].getAttribute('aria-selected') === 'true') {
                        nextElem = results[i + 1];
                        break;
                    }
                }

                if (i === results.length - 1) {
                    nextElem = activeCountriesArray[0];
                }
            } else {
                nextElem = activeCountriesArray[0];
            }

            _focusOnElement(nextElem);
        }

        function _findPreviousElement() {
            var i,
                nextElem,
                results = searchResults.querySelectorAll('li:not(.hidden):not(.headline)');

            // When this function is called, there's at least 1 element in the Array
            if (activeCountriesArray.length > 1) {
                for (i = activeCountriesArray.length - 1; i > 0; i--) {
                    if (activeCountriesArray[i].getAttribute('aria-selected') === 'true') {
                        nextElem = results[i - 1];
                        break;
                    }
                }

                if (i === 0) {
                    nextElem = activeCountriesArray[activeCountriesArray.length - 1];
                }
            } else {
                nextElem = activeCountriesArray[0];
            }

            _focusOnElement(nextElem);
        }

        // Helper function to set the aria selected country after its clicked
        function _markSelected(e) {
            e.target.parentNode.setAttribute('aria-selected', true);
        }

        function _keyboardNavigation(e) {
            e.preventDefault();
            switch (e.keyCode) {
                case EDC.props.arrowDownKeyCode:
                    _findNextElement();
                    break;
                case EDC.props.arrowUpKeyCode:
                    _findPreviousElement();
                    break;
                case EDC.props.escapeKeyCode:
                    _hideSearchResult();
                    break;
                case EDC.props.enterKeyCode:
                    e.target.click();
                    break;
                default:
                    break;
            }
        }

        function _stopScrolling(e) {
            var isSearchBoxOpen = searchBox.classList.value.indexOf('open') > -1,
                hasFocus = searchBox.querySelector('a:focus');

            if (isSearchBoxOpen && hasFocus && (e.keyCode === EDC.props.arrowDownKeyCode || e.keyCode === EDC.props.arrowUpKeyCode)) {
                e.preventDefault();
            }
        }

        function _attachEvents() {
            var results = element.querySelectorAll('.search-results li a');

            // Handle Keyboard Navigation
            EDC.utils.attachEvents(searchInput, 'keyup', _filterCountries);
            EDC.utils.attachEvents(results, 'keyup', _keyboardNavigation);
            EDC.utils.attachEvents(results, 'focus', _markSelected);
            EDC.utils.attachEvents(document, 'keydown', _stopScrolling);

            // Handle Button Click
            EDC.utils.attachEvents(searchBtn, 'click', _closeSearch);
            EDC.utils.attachEvents(results, 'click', _trackEvent);

            // Handle Page Resize
            EDC.utils.attachEvents(window, 'resize', _contentBannerPosition);
        }

        setTimeout(function () {
            _contentBannerPosition();
        }, 1000);

        _attachEvents();
    }

    // Public methods
    function init() {
        var findCountryByName = document.querySelectorAll('.find-country-by-name:not([data-component-state="initialized"])');

        EDC.utils.debug(true);

        if (findCountryByName) {
            findCountryByName.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', findCountryByNameJS.init);