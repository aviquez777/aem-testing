var businessRegistrySelectorJS = (function () {
    'use strict';

    function _initialize(element) {
        var isBanner = element.classList.contains('banner'),
            searchBox = element.querySelector('.search-box'),
            searchList = searchBox.querySelector('ul'),
            searchInput = searchBox.querySelector('.search-input'),
            searchBtn = searchBox.querySelector('button'),
            searchResults = searchBox.querySelector('.search-results'),
            countries = searchBox.querySelectorAll('ul li'),
            states = element.querySelectorAll('.registry-info .info'),
            activeCountriesArray = [],
            searchResultsText = searchBox.dataset.resultsText || '',
            searchNoResultsText = searchBox.dataset.noresultsText || '',
            directionsText = searchBox.dataset.directionsText || '',
            screenReaderText = element.querySelector('#sr-announce'),
            selectedCountry = element.querySelector('.search-container').getAttribute('data-selected'),
            defaultPlaceholder = searchInput.getAttribute('data-place-holder'),
            initialPlaceholder = searchInput.placeholder,
            countryName,
            countryURL;

        // Data Layer
        function _trackEvent(country, url, state) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventName,
                    eventAction: element.dataset.eventAction,
                    eventText: '',
                    eventValue: country.toLowerCase(),
                    eventValue2: state ? state.toLowerCase() : '',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: url,
                    engagementType: element.dataset.eventEngagement,
                    eventLevel: ''
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        // Accesibility function to anounce results
        function _announceResults() {
            var number = activeCountriesArray.length,
                textToRead = number + ' ' + searchResultsText + ' ' + directionsText;

            if (activeCountriesArray.length === 0) {
                textToRead = searchNoResultsText;
            }

            screenReaderText.innerHTML = textToRead;
        }

        // Helper function to set selected country
        function _setSelectedCountry(value) {
            countries.forEach(function (country) {
                var countryInfo = country.querySelector('.country');

                if (countryInfo && countryInfo.getAttribute('data-code').toLowerCase() === value.toLowerCase()) {
                    searchInput.placeholder = countryInfo.innerText;
                    initialPlaceholder = countryInfo.innerText;
                    countryName = countryInfo.getAttribute('data-english-name');
                    countryURL = countryInfo.parentElement.getAttribute('href');
                }
            });
        }

        // Helper function to change the place holder on focus
        function _setDefaultPlaceholder() {
            searchInput.placeholder = defaultPlaceholder ? defaultPlaceholder : '';
        }

        // Helper function to change the place holder on blur
        function _setInitialPlaceholder() {
            if (initialPlaceholder) {
                searchInput.placeholder = initialPlaceholder;
            }
        }

        // Patter to match the names of the countries and the search
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
            return activeCountriesArray.length > 0;
        }

        function _hideActiveCountries() {
            var i;

            for (i = 0; i < countries.length; i++) {
                countries[i].classList.add('hidden');
            }

            activeCountriesArray = [];
        }

        function _getResults(pattern) {
            var results = 0,
                i,
                name,
                acronyms;

            _hideActiveCountries();

            for (i = 0; (results < 6 && i < countries.length); i++) {
                name = countries[i].querySelector('a > .country');
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
        }

        function _showSearchResult(pattern) {
            searchBtn.setAttribute('aria-label', searchBtn.dataset.closeLabel);
            searchBtn.setAttribute('tabindex', 0);
            searchBox.classList.add('open');
            searchResults.setAttribute('aria-hidden', false);
            searchList.setAttribute('aria-selected', true);

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

        // Helper function to set the search box position when banner
        function _contentBannerPosition() {
            var contentBannerContainer = element.querySelector('.container'),
                imageWrapper = element.querySelector('.img-wrapper'),
                imageWrapperHeight = imageWrapper.offsetHeight,
                device = EDC.utils.getDeviceViewPort(),
                contentPosition;

            // Search banner should be placed at 50% on desktop, tablet and mobile landscape 40% and 25% on mobile portrait, based on the image wrapper height.
            if (device === 'desktop') {
                contentPosition = imageWrapperHeight * 0.5;
            } else if (device === 'tablet' || window.innerWidth >= 550) {
                contentPosition = imageWrapperHeight * 0.6;
            } else {
                contentPosition = imageWrapperHeight * 0.75;
            }

            contentBannerContainer.style.marginTop = '-' + contentPosition + 'px';
        }

        // Helper function to navigate thru the list of countries
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

        // Helper function to navigate thru the list of countries
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

        // Helper function to display state info
        function _showStateInfo(e) {
            var target = e !== undefined ? e.target : '',
                targetValue = target ? target.value : selectedCountry,
                stateName;

            states.forEach(function (state) {
                if (state.getAttribute('data-code').toLowerCase() === targetValue.toLowerCase()) {
                    state.classList.remove('hide');
                } else {
                    state.classList.add('hide');
                }
            });

            if (target) {
                stateName = target.options[target.selectedIndex].getAttribute('data-english-name');
                _trackEvent(countryName, countryURL, stateName);
            }
        }

        function _checkCountry(e) {
            var target = e.target.classList.contains('country') ? e.target : e.target.querySelector('.country'),
                selectedcountryName = target && target.getAttribute('data-english-name') ? target.getAttribute('data-english-name') : '',
                selectedcountryURL = target ? target.parentElement.getAttribute('href') : '',
                dropdown;

            _trackEvent(selectedcountryName, selectedcountryURL);

            if (!isBanner && selectedCountry && target.getAttribute('data-code').toLowerCase() === selectedCountry.toLowerCase()) {
                e.preventDefault();

                if (states.length > 1) {
                    dropdown = element.querySelector('.ui.selection.dropdown');
                    $(dropdown).dropdown('restore defaults');
                    _showStateInfo();
                }
            }

            _closeSearch();
        }

        // Function to help keypress navigation
        function _stopScrolling(e) {
            var isSearchBoxOpen = searchBox.classList.contains('open'),
                hasFocus = searchBox.querySelector('a:focus');

            if (isSearchBoxOpen && hasFocus && (e.keyCode === EDC.props.arrowDownKeyCode || e.keyCode === EDC.props.arrowUpKeyCode)) {
                e.preventDefault();
            }
        }

        function _attachEvents() {
            var results = element.querySelectorAll('.search-results li a'),
                select = element.querySelector('select');

            // Handle Keyboard Navigation
            EDC.utils.attachEvents(searchInput, 'focus', _setDefaultPlaceholder);
            EDC.utils.attachEvents(searchInput, 'blur', _setInitialPlaceholder);
            EDC.utils.attachEvents(searchInput, 'keyup', _filterCountries);
            EDC.utils.attachEvents(select, 'change', _showStateInfo);
            EDC.utils.attachEvents(results, 'click', _checkCountry);
            EDC.utils.attachEvents(results, 'keyup', _keyboardNavigation);
            EDC.utils.attachEvents(results, 'focus', _markSelected);
            EDC.utils.attachEvents(document, 'keydown', _stopScrolling);

            // Handle Button Click
            EDC.utils.attachEvents(searchBtn, 'click', _closeSearch);

            // Handle Page Resize
            if (isBanner) {
                EDC.utils.attachEvents(window, 'resize', _contentBannerPosition);
            }
        }

        if (isBanner) {
            setTimeout(function () {
                _contentBannerPosition();
            }, 2000);
        }

        if (selectedCountry) {
            _setSelectedCountry(selectedCountry);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var registrySelector = document.querySelectorAll('.c-business-registry-selector:not([data-component-state="initialized"])');

        EDC.utils.debug(true);

        if (registrySelector) {
            registrySelector.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', businessRegistrySelectorJS.init);