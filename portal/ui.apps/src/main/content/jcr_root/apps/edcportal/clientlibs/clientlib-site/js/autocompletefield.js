var autocompleteFieldJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var searchContainer = element.querySelector('.search-container'),
            searchList = searchContainer ? searchContainer.querySelector('ul') : null,
            searchInput = searchContainer ? searchContainer.querySelector('.search-input') : null,
            hiddenInput = searchContainer ? searchContainer.querySelector('#search-result-field') : null,
            searchResults = searchContainer ? searchContainer.querySelector('.search-results') : null,
            closeBtn = searchContainer ? searchContainer.querySelector('.close-btn') : null,
            btns = element.querySelectorAll('ul[role="listbox"] li button'),
            items = element.querySelectorAll('ul li:not(.no-results)'),
            activeItemsArray = [],
            searchResultsText = element.dataset.resultsText || '',
            searchNoResultsText = element.dataset.noresultsText || '',
            directionsText = element.dataset.directionsText || '',
            screenReaderText = element.querySelector('#sr-announce'),
            jsonUrl = element.getAttribute('data-json-url');

        // Accesibility function to anounce results
        function _announceResults() {
            var number = activeItemsArray.length,
                textToRead = number + ' ' + searchResultsText + ' ' + directionsText;

            if (activeItemsArray.length === 0) {
                textToRead = searchNoResultsText;
            }

            screenReaderText.innerHTML = textToRead;
        }

        // Patter to match the names of the items and the search
        function _matchName(btn, pattern) {
            var btnText = btn ? btn.textContent : null,
                name = btnText ? btnText.toLowerCase().trim() : null,
                match = false,
                realPattern = pattern.trim(),
                patternSpace = /\s/.test(realPattern);

            if (!patternSpace && realPattern !== '') {
                if (name.indexOf(realPattern) > -1) {
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
                elem.setAttribute('aria-selected', 'true');
                elem.querySelector('button').focus();
            }
        }

        function _haveResults() {
            return activeItemsArray.length > 0;
        }

        function _hideActiveItems() {
            var i;

            for (i = 0; i < items.length; i++) {
                items[i].classList.add('hidden');
            }

            activeItemsArray = [];
        }

        function _getResults(pattern) {
            var results = 0,
                noResults = element.querySelector('ul li.no-results'),
                i,
                btn;

            if (noResults) {
                noResults.classList.add('hidden');
            }

            _hideActiveItems();

            for (i = 0; (results < 6 && i < items.length); i++) {
                btn = items[i].querySelector('button.item');

                if (btn && _matchName(btn, pattern.toLowerCase())) {
                    items[i].classList.remove('hidden');
                    activeItemsArray.push(items[i]);
                    results++;
                }
            }

            // Show No Results
            if (results <= 0) {
                items[0].classList.add('hidden');

                if (noResults) {
                    noResults.classList.remove('hidden');
                }
            }
        }

        function _closeSearch() {
            if (element.classList.contains('open')) {
                element.classList.remove('open');
                searchList.setAttribute('aria-selected', false);
                _hideActiveItems();
            }

            _clearSelected();
        }

        function _showSearchResult(pattern) {
            element.classList.add('open');
            searchResults.setAttribute('aria-hidden', false);
            searchList.setAttribute('aria-selected', true);

            _getResults(pattern);
            _announceResults();
        }

        function _hideSearchResult() {
            activeItemsArray = [];
            element.classList.remove('open');
            searchResults.setAttribute('aria-hidden', true);
            searchList.setAttribute('aria-selected', false);
            searchInput.value = '';
            searchInput.focus();
        }

        function _filterItems(e) {
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

        // Helper function to navigate thru the list of items
        function _findNextElement() {
            var i,
                nextElem,
                results = searchResults.querySelectorAll('li:not(.hidden):not(.headline)');

            // When this function is called, there's at least 1 element in the Array
            if (activeItemsArray.length > 1) {
                for (i = 0; i < activeItemsArray.length; i++) {
                    if (activeItemsArray[i].getAttribute('aria-selected') === 'true') {
                        nextElem = results[i + 1];
                        break;
                    }
                }

                if (i === results.length - 1) {
                    nextElem = activeItemsArray[0];
                }
            } else {
                nextElem = activeItemsArray[0];
            }

            _focusOnElement(nextElem);
        }

        // Helper function to navigate thru the list of items
        function _findPreviousElement() {
            var i,
                nextElem,
                results = searchResults.querySelectorAll('li:not(.hidden):not(.headline)');

            // When this function is called, there's at least 1 element in the Array
            if (activeItemsArray.length > 1) {
                for (i = activeItemsArray.length - 1; i > 0; i--) {
                    if (activeItemsArray[i].getAttribute('aria-selected') === 'true') {
                        nextElem = results[i - 1];
                        break;
                    }
                }

                if (i === 0) {
                    nextElem = activeItemsArray[activeItemsArray.length - 1];
                }
            } else {
                nextElem = activeItemsArray[0];
            }

            _focusOnElement(nextElem);
        }

        // Helper function to set the aria selected item after its clicked
        function _markSelected(e) {
            var eTarget = e ? e.target : null,
                parent = eTarget ? eTarget.parentNode : null;

            if (parent) {
                parent.setAttribute('aria-selected', 'true');
            }
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

        // Function to help keypress navigation
        function _stopScrolling(e) {
            var isSearchBoxOpen = element.classList.contains('open'),
                hasFocus = element.querySelector('button:focus');

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

        function _selectItem(e) {
            var el = e && e.target ? e.target : e,
                itemName = el ? el.textContent : '',
                itemDataName = el ? el.getAttribute('data-name') : '';

            if (searchInput && hiddenInput && (e.type !== 'keydown' || (e.type === 'keydown' && (e.keyCode === EDC.props.enterKeyCode || e.keyCode === EDC.props.tabKeyCode)))) {
                searchInput.value = itemName;
                hiddenInput.value = itemDataName;
                _closeSearch();
            }
        }

        function _buildItem(dataCountries) {
            searchList.innerHTML = '';

            dataCountries.forEach(function (c, i) {
                var item = document.createElement('li'),
                    btn = document.createElement('button'),
                    name = EDC.props.lang === 'en' ? c.nameEn : c.nameFr;

                item.classList.add('hidden');
                item.setAttribute('role', 'option');
                item.setAttribute('aria-selected', 'false');
                btn.classList.add('item');
                btn.classList.add('edc-btn-unstyled');
                btn.setAttribute('type', 'button');
                btn.setAttribute('data-name', EDC.props.lang === 'en' ? c.nameEn : c.nameFr);
                btn.innerHTML = name;

                if (c.preselected) {
                    btn.setAttribute('data-selected', 'true');
                }

                item.appendChild(btn);
                searchList.appendChild(item);

                if (dataCountries.length === i + 1) {
                    _buildNoResultsMessage();
                }

                EDC.utils.attachEvents(btn, 'mousedown', _selectItem);
                EDC.utils.attachEvents(btn, 'keydown', _selectItem);
            });

            PubSub.publish('autocomplete-items-ready', true);
        }

        function _attachNewEvents() {
            var results = element.querySelectorAll('.search-results li button');

            EDC.utils.attachEvents(results, 'keyup', _keyboardNavigation);
            EDC.utils.attachEvents(results, 'focus', _markSelected);

            if (!jsonUrl) {
                EDC.utils.attachEvents(results, 'mousedown', _selectItem);
                EDC.utils.attachEvents(results, 'keydown', _selectItem);
            }
        }

        function _clickPreselectedItem() {
            var selectedItem = element.querySelector('.search-results ul li .item[data-selected="true"]');

            if (selectedItem) {
                _selectItem(selectedItem);
            }
        }

        function _getItemList() {
            if (jsonUrl) {
                EDC.utils.fetchJSON('GET', jsonUrl, '', function (data) {
                    var dataCountries = data;

                    if (dataCountries) {
                        _buildItem(dataCountries);
                        items = element.querySelectorAll('ul li:not(.no-results)');
                        _attachNewEvents();
                        _clickPreselectedItem();
                    }

                    btns = element.querySelectorAll('ul[role="listbox"] li button');
                });
            } else {
                setTimeout(function () {
                    PubSub.publish('autocomplete-items-ready', true);
                    _clickPreselectedItem();
                }, 0);
            }
        }

        function _checkItemValue(e) {
            var val = 0,
                clickedEl = e.relatedTarget;

            btns.forEach(function (btn) {
                if (btn.textContent.toLowerCase() === searchInput.value.toLowerCase()) {
                    val++;
                }
            });

            if (val === 0) {
                searchInput.value = '';
            }

            if (!clickedEl || !clickedEl.classList.contains('item')) {
                _closeSearch();
            }
        }

        function _pubSubs() {
            PubSub.subscribe('autocomplete-select-other', function (msg, status) {
                var otherBtn = element.querySelector('ul li button[data-name="Other"], ul li button[data-name="other"]');

                if (status && otherBtn && searchInput) {
                    _selectItem(otherBtn);
                    EDC.forms.validateField(searchInput);
                }
            });
        }

        function _attachEvents() {
            // Handle Keyboard Navigation
            EDC.utils.attachEvents(searchInput, 'keyup', _filterItems);
            EDC.utils.attachEvents(document, 'keydown', _stopScrolling);

            // Handle Button Click
            EDC.utils.attachEvents(closeBtn, 'click', function () {
                _closeSearch();
                searchInput.focus();
            });

            EDC.utils.attachEvents(searchInput, 'focusout', _checkItemValue);
        }

        _attachEvents();
        _attachNewEvents();
        _getItemList();
        _pubSubs();
    }

    // Public methods
    function init() {
        var fileUploader = document.querySelectorAll('.c-autocomplete-field:not([data-component-state="initialized"])');

        if (fileUploader) {
            fileUploader.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', autocompleteFieldJS.init);