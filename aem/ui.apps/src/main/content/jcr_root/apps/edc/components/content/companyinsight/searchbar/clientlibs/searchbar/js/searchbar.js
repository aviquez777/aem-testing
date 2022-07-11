var companyInsightSearchBarJS = (function () {
    'use strict';

    function _initialize(element) {
        var searchBox = element.querySelector('.search-box'),
            searchContainer = searchBox.querySelector('.search-container'),
            searchList = searchContainer.querySelector('ul'),
            searchInput = searchContainer.querySelector('.search-input'),
            searchBtn = element.querySelector('.actions button'),
            searchResults = searchContainer.querySelector('.search-results'),
            closeBtn = searchContainer.querySelector('.close-btn'),
            btns,
            countries,
            activeCountriesArray = [],
            searchResultsText = searchBox.dataset.resultsText || '',
            searchNoResultsText = searchBox.dataset.noresultsText || '',
            directionsText = searchBox.dataset.directionsText || '',
            screenReaderText = element.querySelector('#sr-announce'),
            selectedCountry = element.querySelector('.search-container').getAttribute('data-selected'),
            jsonUrl = element.getAttribute('data-json-url'),
            inputs = element.querySelectorAll('input:not([type="hidden"])'),
            companySearchInput = element.querySelector('.search-criteria > input');

        // Accesibility function to anounce results
        function _announceResults() {
            var number = activeCountriesArray.length,
                textToRead = number + ' ' + searchResultsText + ' ' + directionsText;

            if (activeCountriesArray.length === 0) {
                textToRead = searchNoResultsText;
            }

            screenReaderText.innerHTML = textToRead;
        }

        // Patter to match the names of the countries and the search
        function _matchName(btn, pattern) {
            var name = btn.getAttribute('data-name').toLowerCase().trim(),
                code = btn.getAttribute('data-code').toLowerCase(),
                match = false,
                realPattern = pattern.trim(),
                patternSpace = /\s/.test(realPattern);

            if (!patternSpace && realPattern !== '') {
                if (name.indexOf(realPattern) > -1 || code.indexOf(realPattern) > -1) {
                    match = true;
                }
            } else if (patternSpace && name.indexOf(realPattern) >= 0) {
                match = true;
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
                elem.querySelector('button').focus();
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
                noResults = element.querySelector('ul li.no-results'),
                i,
                btn;

            noResults.classList.add('hidden');
            _hideActiveCountries();

            for (i = 0; (results < 6 && i < countries.length); i++) {
                btn = countries[i].querySelector('button.country');
                if (btn && _matchName(btn, pattern.toLowerCase())) {
                    countries[i].classList.remove('hidden');
                    activeCountriesArray.push(countries[i]);
                    results++;
                }
            }

            // Show No Results
            if (results <= 0) {
                countries[0].classList.add('hidden');
                noResults.classList.remove('hidden');
            }
        }

        function _closeSearch() {
            if (searchBox.classList.contains('open')) {
                searchBox.classList.remove('open');
                searchList.setAttribute('aria-selected', false);
                _hideActiveCountries();
            }

            _clearSelected();
        }

        function _showSearchResult(pattern) {
            searchBox.classList.add('open');
            searchResults.setAttribute('aria-hidden', false);
            searchList.setAttribute('aria-selected', true);

            _getResults(pattern);
            _announceResults();
        }

        function _hideSearchResult() {
            activeCountriesArray = [];
            searchBox.classList.remove('open');
            searchResults.setAttribute('aria-hidden', true);
            searchList.setAttribute('aria-selected', false);
            searchInput.value = '';
            searchInput.focus();
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

        function _checkCountry(e) {
            var target = e.target.classList.contains('country') ? e.target : e.target.querySelector('.country');

            if (selectedCountry && target.getAttribute('data-code').toLowerCase() === selectedCountry.toLowerCase()) {
                e.preventDefault();
            }

            _closeSearch();
        }

        // Function to help keypress navigation
        function _stopScrolling(e) {
            var isSearchBoxOpen = searchBox.classList.contains('open'),
                hasFocus = searchBox.querySelector('button:focus');

            if (isSearchBoxOpen && hasFocus && (e.keyCode === EDC.props.arrowDownKeyCode || e.keyCode === EDC.props.arrowUpKeyCode)) {
                e.preventDefault();
            }
        }

        function _buildNoResultsMessage() {
            var li = document.createElement('li');

            li.classList.add('hidden');
            li.classList.add('no-results');
            li.setAttribute('role', 'option');
            li.setAttribute('aria-selected', 'false');
            li.innerHTML = searchList.getAttribute('data-not-found-message');
            searchList.appendChild(li);
        }

        function _enableSearchBtn() {
            var counter = 0;

            EDC.forms.validateField(inputs[0]);
            inputs.forEach(function (input) {
                if (input.value !== '' && input.type !== 'hidden' && !input.classList.contains('error')) {
                    counter++;
                }
            });

            if (counter === 2) {
                searchBtn.removeAttribute('disabled');
                searchBtn.classList.remove('disabled');
            } else {
                searchBtn.setAttribute('disabled', 'disabled');
                searchBtn.classList.add('disabled');
            }
        }

        function _performSearch(e) {
            e.preventDefault();
            element.querySelector('input[type="hidden"][name="country"]').value = element.querySelector('.search-input.country').getAttribute('data-code');
            element.querySelector('input[type="hidden"][name="language"]').value = EDC.props.lang;
            element.submit();
        }

        function _selectCountry(e) {
            var el = e.target,
                countryName = el.getAttribute('data-name');

            if (e.type !== 'keydown' || (e.type === 'keydown' && (e.keyCode === EDC.props.enterKeyCode || e.keyCode === EDC.props.tabKeyCode))) {
                searchInput.value = countryName.charAt(0).toUpperCase() + countryName.slice(1);
                searchInput.setAttribute('data-code', el.getAttribute('data-code'));
                _enableSearchBtn();
                _closeSearch();
            }
        }

        function _buildCountry(dataCountries) {
            dataCountries.forEach(function (c, i) {
                var country = document.createElement('li'),
                    btn = document.createElement('button'),
                    name = EDC.props.lang === 'en' ? c.nameEn : c.nameFr;

                country.classList.add('hidden');
                country.setAttribute('role', 'option');
                country.setAttribute('aria-selected', 'false');
                btn.classList.add('country');
                btn.classList.add('no-btn');
                btn.setAttribute('type', 'button');
                btn.setAttribute('data-code', c.code);
                btn.setAttribute('data-name', EDC.props.lang === 'en' ? c.nameEn : c.nameFr);
                btn.innerHTML = name;
                country.appendChild(btn);
                searchList.appendChild(country);
                if (dataCountries.length === i + 1) {
                    _buildNoResultsMessage();
                }
                EDC.utils.attachEvents(btn, 'mousedown', _selectCountry);
                EDC.utils.attachEvents(btn, 'keydown', _selectCountry);
            });
        }

        function _attachNewEvents() {
            var results = element.querySelectorAll('.search-results li button');

            EDC.utils.attachEvents(results, 'click', _checkCountry);
            EDC.utils.attachEvents(results, 'keyup', _keyboardNavigation);
            EDC.utils.attachEvents(results, 'focus', _markSelected);
        }

        function _clickIfPrepopulated() {
            var inputElem = element.querySelector('.search-box .search-input'),
                inputVal = inputElem.getAttribute('data-code');

            if (inputVal !== '') {
                countries.forEach(function (country) {
                    var countryBtn = country.querySelector('button');

                    if (countryBtn.getAttribute('data-code') === inputVal) {
                        inputElem.value = countryBtn.getAttribute('data-name');
                        countryBtn.click();
                        return;
                    }
                });
            }
        }

        function _getCountryList() {
            EDC.utils.fetchJSON('GET', jsonUrl, '', function (data) {
                var dataCountries = data;

                if (dataCountries) {
                    _buildCountry(dataCountries);
                    countries = searchBox.querySelectorAll('ul li:not(.no-results)');
                    _attachNewEvents();
                    EDC.forms.validateChange([companySearchInput]);
                    _clickIfPrepopulated();
                }

                btns = element.querySelectorAll('ul[role="listbox"] li button');
            });
        }

        function _checkCountryValue(e) {
            var val = 0,
                clickedEl = e.relatedTarget;

            btns.forEach(function (btn) {
                if (btn.getAttribute('data-name').toLowerCase() === searchInput.value.toLowerCase()) {
                    val++;
                }
            });

            if (val === 0) {
                searchInput.value = '';
            }

            if (!clickedEl || !clickedEl.classList.contains('country')) {
                _closeSearch();
            }
            _enableSearchBtn();
        }

        function _adjustLabelsHeights() {
            var companyLabel = element.querySelector('.search-criteria label'),
                countryLabel = element.querySelector('.search-box label'),
                companyLabelMarginBottom = parseInt(window.getComputedStyle(companyLabel).getPropertyValue('margin-bottom'), 10),
                companyLabelHeight = parseInt(window.getComputedStyle(companyLabel).getPropertyValue('height'), 10),
                countryLabelHeight = parseInt(window.getComputedStyle(countryLabel).getPropertyValue('height'), 10),
                actionsSection = element.querySelector('.actions'),
                isMobile = window.innerWidth < EDC.props.media.md;

            if (!isMobile) {
                if (companyLabelHeight > countryLabelHeight) {
                    countryLabel.style.height = companyLabelHeight + 'px';
                } else if (countryLabelHeight > companyLabelHeight) {
                    companyLabel.style.height = countryLabelHeight + 'px';
                }

                actionsSection.style.marginTop = (companyLabelHeight + companyLabelMarginBottom) + 'px';
            } else {
                countryLabel.removeAttribute('style');
                companyLabel.removeAttribute('style');
                actionsSection.removeAttribute('style');
            }
        }

        function _attachEvents() {
            // Handle Keyboard Navigation
            EDC.utils.attachEvents(searchInput, 'keyup', _filterCountries);
            EDC.utils.attachEvents(document, 'keydown', _stopScrolling);

            // Handle Button Click
            EDC.utils.attachEvents(searchBtn, 'click', _performSearch);
            EDC.utils.attachEvents(companySearchInput, 'blur', _enableSearchBtn);
            EDC.utils.attachEvents(closeBtn, 'click', function () {
                _closeSearch();
                searchInput.focus();
            });
            EDC.utils.attachEvents(searchInput, 'focusout', _checkCountryValue);
            EDC.utils.attachEvents(window, 'load', _adjustLabelsHeights);
            EDC.utils.attachEvents(window, 'resize', _adjustLabelsHeights);
        }

        _attachEvents();
        _getCountryList();
    }

    // Public methods
    function init() {
        var companyInsightSearchBars = document.querySelectorAll('.c-company-insight-search-bar:not([data-component-state="initialized"])');

        EDC.utils.debug(true);

        if (companyInsightSearchBars) {
            companyInsightSearchBars.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', companyInsightSearchBarJS.init);