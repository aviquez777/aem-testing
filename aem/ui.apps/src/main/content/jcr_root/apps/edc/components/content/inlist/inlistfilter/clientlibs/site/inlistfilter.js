var inListSupplierFilterJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var filtersBody = document.querySelector('body'),
            supplierCards = element.querySelectorAll('.supplier-card'),
            supplierFilters = element.querySelector('.supplier-filters'),
            filterInputs = element.querySelectorAll('.filter-option'),
            filterTitle = element.querySelector('.filter-title'),
            filterTags = element.querySelector('.filter-tags'),
            filterTagsList = filterTags ? filterTags.querySelector('ul') : '',
            closeBtn = element.querySelector('.close-button'),
            noMatch = element.querySelector('.no-match'),
            applyAll = element.querySelector('.apply-filter'),
            isDevice,
            sideBar,
            timerId = null,
            timeout = 5,
            modalSize = 0;

        // Data Layer

        function _parseFilterCheckedToString(filterArray) {
            var valuesConcat = '';

            if (filterArray.length > 0) {
                filterArray.forEach(function (elem, index) {
                    valuesConcat += (index === filterArray.length - 1) ? elem.nextElementSibling.innerHTML : elem.nextElementSibling.innerHTML + ',';
                });
            }

            return valuesConcat.toLowerCase();
        }

        function _trackEvent(e) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent.toLowerCase(),
                    eventType: element.dataset.eventType,
                    eventName: (element.dataset.eventName + ' - ' + element.querySelector('.supplier-card a').textContent).toLowerCase(),
                    eventAction: element.dataset.eventAction,
                    eventText: e.target.closest('.supplier-card').dataset.englishName.toLowerCase(),
                    eventValue: _parseFilterCheckedToString(element.querySelectorAll('.supplier-filter.service-type input[type="checkbox"]:checked')),
                    eventValue2: _parseFilterCheckedToString(element.querySelectorAll('.supplier-filter.market input[type="checkbox"]:checked')),
                    eventValue3: _parseFilterCheckedToString(element.querySelectorAll('.supplier-filter.modes-trans input[type="checkbox"]:checked')),
                    eventValue4: _parseFilterCheckedToString(element.querySelectorAll('.supplier-filter.services input[type="checkbox"]:checked')),
                    eventValue5: _parseFilterCheckedToString(element.querySelectorAll('.supplier-filter.industries input[type="checkbox"]:checked')),
                    eventValue6: _parseFilterCheckedToString(element.querySelectorAll('.supplier-filter.response-times input[type="checkbox"]:checked')),
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName().toLowerCase(),
                    destinationPage: e.target.getAttribute('href'),
                    engagementType: element.dataset.eventEngagement,
                    eventLevel: element.dataset.eventLevel
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }
        // Private methods

        // Helper function to determine display width and return if is a mobile device
        function _isDevice() {
            return window.innerWidth < EDC.props.media.lg;
        }

        // Initializes the sidebar to hide it when the filter is opened.
        function _sideBarInit() {
            if (!!(timerId)) {
                if (timeout === 0) {
                    return;
                }
                if (filtersBody.querySelector('.addthis-smartlayers-mobile')) {
                    sideBar = filtersBody.querySelector('.addthis-smartlayers-mobile');
                    return;
                }
                timeout -= 1;
            }
            timerId = setTimeout(_sideBarInit, 500);

            return;
        }

        // Sets visibility of the Country filters on load
        function _showHideFilter() {
            if (!isDevice) {
                supplierFilters.classList.add('show');
                bodyScrollLock.enableBodyScroll(supplierFilters);
            } else if (window.innerWidth !== modalSize) {
                supplierFilters.classList.remove('show');
            }
        }

        // Helper function to determine if there's any filter checked
        function _isChecked() {
            var inputsChecked = element.querySelectorAll('.filter-option:checked');

            if (inputsChecked.length > 0) {
                applyAll.disabled = false;
            } else {
                applyAll.disabled = true;
            }
        }

        // Helper function to show or hide elements
        function _showHideElements(el, elementAction, elementClass) {
            el.forEach(function (item) {
                switch (elementAction) {
                    case 'add':
                        item.classList.add(elementClass);
                        break;

                    case 'remove':
                        item.classList.remove(elementClass);
                        break;
                }
            });
        }

        // Show Filters
        function _showFilter() {
            modalSize = window.innerWidth;
            supplierFilters.classList.add('show');
            filterTitle.classList.add('filter-open');
            closeBtn.focus();

            _isChecked();

            if (sideBar) {
                sideBar.style.display = 'none';
            }

            // Disables scrolling on body and html elements on all devices
            bodyScrollLock.disableBodyScroll(supplierFilters);
        }

        // Hide Filters
        function _hideFilter() {
            if (isDevice) {
                filterInputs.forEach(function (input) {
                    if (input.checked && !input.classList.contains('selected')) {
                        EDC.utils.simulateClick(input);
                    } else if (!input.checked && input.classList.contains('selected')) {
                        EDC.utils.simulateClick(input);
                    }
                });
            }

            modalSize = 0;
            supplierFilters.classList.remove('show');
            filterTitle.classList.remove('filter-open');

            if (sideBar) {
                sideBar.style.display = 'block';
            }

            bodyScrollLock.enableBodyScroll(supplierFilters);
        }

        // Helper function to apply filters correcly on mobile
        function _applyFilters() {
            filterInputs.forEach(function (input) {
                if (input.checked) {
                    input.classList.add('selected');
                } else {
                    input.classList.remove('selected');
                }
            });

            _hideFilter();
        }

        // Helper function to set the new card array spacing
        function _setCardSpacing(cards) {
            cards.forEach(function (card, index) {
                if (index % 2 === 0) {
                    card.classList.add('even');
                } else {
                    card.classList.add('odd');
                }
            });
        }

        // Helper function to reset all cards
        function _resetAllCards() {
            if (supplierCards) {
                supplierCards.forEach(function (card) {
                    card.classList.remove('odd');
                    card.classList.remove('even');
                });
            }
        }

        // Expand / Collapse Filters
        function _expandCollapseFilter(e) {
            var filterSection = e.target.closest('.supplier-filter'),
                filterOptions = filterSection.querySelectorAll('input');

            if (filterSection) {
                if (filterSection.classList.contains('collapsed')) {
                    filterSection.classList.remove('collapsed');
                    filterSection.setAttribute('aria-expanded', true);

                    filterOptions.forEach(function (option) {
                        option.setAttribute('tabindex', '0');
                    });
                } else {
                    filterSection.classList.add('collapsed');
                    filterSection.setAttribute('aria-expanded', false);

                    filterOptions.forEach(function (option) {
                        option.setAttribute('tabindex', '-1');
                    });
                }
            }
        }

        // Shows / hides more options if available
        function _showMoreOptions(e) {
            var target = e.target.classList.contains('show-more') ? e.target : e.target.parentElement,
                targetSpan = target ? target.querySelector('span') : '',
                filterSection = e.target.closest('.supplier-filter'),
                hiddenOptions = filterSection ? filterSection.querySelectorAll('li.hide') : '',
                filterOptions;

            if (hiddenOptions.length) {
                hiddenOptions.forEach(function (option) {
                    option.classList.remove('hide');
                });

                if (targetSpan) {
                    targetSpan.innerText = target.getAttribute('data-less');
                }

            } else {
                filterOptions = filterSection ? (filterSection.querySelectorAll('li')) : '';
                filterOptions.forEach(function (option, index) {
                    if (index > 3) {
                        option.classList.add('hide');
                    }
                });

                if (targetSpan) {
                    targetSpan.innerText = target.getAttribute('data-more');
                }
            }
        }

        // Shows / Hides filter tags
        function _checkFilterTags() {
            var availableTags = filterTagsList.querySelectorAll('li');

            if (availableTags.length <= 1) {
                filterTags.classList.add('hide');
                noMatch.classList.add('hide');
            } else {
                filterTags.classList.remove('hide');
            }
        }

        // Removes selected tag or all options and tags
        function _removeSelections(e) {
            var target = e ? e.target.classList.contains('remove-tag') ? e.target : e.target.parentElement : '',
                tag = target && target.classList.contains('remove-tag') ? target.parentElement : '',
                tagValue = tag ? tag.getAttribute('data-tag-filter') : '',
                isClearAllDevice = target && target.classList.contains('button-option'),
                selectedInput;

            if (tagValue && !isClearAllDevice) {
                selectedInput = element.querySelector('input[value="' + tagValue + '"]');
                selectedInput.classList.remove('selected');
                EDC.utils.simulateClick(selectedInput);
            } else {
                if (filterInputs) {
                    filterInputs.forEach(function (input) {
                        if (!isDevice || !isClearAllDevice) {
                            input.classList.remove('selected');
                        }
                        input.checked = false;
                    });
                }

                if (filterTagsList) {
                    while (filterTagsList.firstElementChild && filterTagsList.firstElementChild.classList.contains('filter-tag')) {
                        filterTagsList.removeChild(filterTagsList.firstElementChild);
                    }
                }

                if (supplierCards) {
                    _resetAllCards();
                    _setCardSpacing(supplierCards);
                    _showHideElements(supplierCards, 'remove', 'hide');
                }
            }

            _checkFilterTags();
        }

        // Creates and sets the tag
        function _createTag(el) {
            var listItem = document.createElement('li'),
                tagSpan = document.createElement('span'),
                tagBtn = document.createElement('button');

            // Sets the span attributes
            tagSpan.classList.add('tag');
            tagSpan.appendChild(document.createTextNode(el.getAttribute('data-value')));
            // Sets the button attributes
            tagBtn.classList.add('edc-btn-icon');
            tagBtn.classList.add('remove-tag');
            tagBtn.setAttribute('aria-label', filterTags.getAttribute('data-close-label'));
            tagBtn.appendChild(document.createElement('span'));
            EDC.utils.attachEvents(tagBtn, 'click', _removeSelections);
            // Sets the li attributes
            listItem.classList.add('filter-tag');
            listItem.setAttribute('data-tag-filter', el.value);
            // Adds the elements to the li
            listItem.appendChild(tagSpan);
            listItem.appendChild(tagBtn);
            // Adds the tag to the last position
            filterTagsList.insertBefore(listItem, filterTagsList.lastElementChild);
        }

        // Sets tags on of the event filters
        function _setTags(el) {
            var deleteTag;

            if (el.checked) {
                _createTag(el);

                if (!isDevice) {
                    el.classList.add('selected');
                }
            } else {
                deleteTag = filterTagsList.querySelector('li[data-tag-filter="' + el.value + '"]');
                deleteTag.parentNode.removeChild(deleteTag);

                if (!isDevice) {
                    el.classList.remove('selected');
                }
            }

            _checkFilterTags();
        }

        // Helper function to check array length
        function _checkArr(arr) {
            return arr.length > 0;
        }

        // Display query data
        function _showFilteredData(fTypes, fMarkets, fTransport, fServices, fIndustries, fResponses) {
            var filtersData = [],
                filteredData,
                i = 0,
                filterActivated = false,
                visibleCards;

            // Checks if all of the arrays have values to start adding all the items on the filter array
            if ([fTypes, fMarkets, fTransport, fServices, fIndustries, fResponses].every(_checkArr)) {
                if (_checkArr(fTypes)) {
                    filtersData.push(fTypes);
                }

                if (_checkArr(fMarkets)) {
                    filtersData.push(fMarkets);
                }

                if (_checkArr(fTransport)) {
                    filtersData.push(fTransport);
                }

                if (_checkArr(fServices)) {
                    filtersData.push(fServices);
                }

                if (_checkArr(fIndustries)) {
                    filtersData.push(fIndustries);
                }

                if (_checkArr(fResponses)) {
                    filtersData.push(fResponses);
                }
            }

            // Checks if a filter is active
            while (i < filterInputs.length && !filterActivated) {
                if (filterInputs[i].checked) {
                    filterActivated = true;
                }
                i++;
            }

            // Shifts and reduces the array with all filtered cards to add only the ones that matches all filter criteria
            if (_checkArr(filtersData) && supplierCards) {
                filteredData = filtersData.shift().reduce(function (res, v) {
                    if (res.indexOf(v) === -1 && filtersData.every(function (a) {
                        return a.indexOf(v) !== -1;
                    })) {
                        res.push(v);
                    }
                    return res;
                }, []);

                // Checks if there's no matching cards
                if (filteredData.length === 0) {
                    noMatch.classList.remove('hide');
                } else {
                    noMatch.classList.add('hide');
                }

                // Hides all cards then only shows the ones that are filtered
                _resetAllCards();
                _showHideElements(supplierCards, 'add', 'hide');
                _showHideElements(filteredData, 'remove', 'hide');

                // Sets all cards in a new array to correctly add the spacing
                visibleCards = element.querySelectorAll('.supplier-card:not(.hide)');
                _setCardSpacing(visibleCards);

            } else if (filterActivated && supplierCards) {
                _showHideElements(supplierCards, 'add', 'hide');
                noMatch.classList.remove('hide');
            } else {
                _resetAllCards();
                _setCardSpacing(supplierCards);
                _showHideElements(supplierCards, 'remove', 'hide');
            }
        }

        // Reduces the filtered array if it contains the Filter Match All property
        function _reduceFilteredArray(cardsArray, el, dataAttribute) {
            var filteredMatchAllArray = [],
                selectedValues = [],
                selectedInputs = el.querySelectorAll('.filter-option:checked');

            selectedInputs.forEach(function (input) {
                selectedValues.push(input.value);
            });

            if (_checkArr(selectedInputs)) {
                cardsArray.forEach(function (card) {
                    var cardAttr = card.getAttribute(dataAttribute),
                        attrArray = cardAttr.split('|').length > 1 ? cardAttr.split('|') : '',
                        attrCount = 0;

                    // Checks if the card contains more than 1 value on the attribute and checks it
                    if (attrArray) {
                        // Varios attributes
                        attrArray.forEach(function (attr) {
                            selectedValues.forEach(function (value) {
                                if (attr === value) {
                                    attrCount++;
                                }
                            });
                        });

                        // Only adds a card if the card has all the values selected
                        if (attrCount >= selectedValues.length) {
                            filteredMatchAllArray.push(card);
                        }
                    } else if (selectedValues.length === 1) {
                        filteredMatchAllArray.push(card);
                    }
                });
            } else {
                filteredMatchAllArray = cardsArray;
            }

            return filteredMatchAllArray;
        }

        // Helper function to set country filters
        function _setFilterData(filterInput, filterDataAttribute) {
            var filterArray = [];

            // Checks is array has items and starts the filtering
            if (_checkArr(filterInput)) {
                filterInput.forEach(function (input) {
                    supplierCards.forEach(function (card) {
                        var cardAttr = card.getAttribute(filterDataAttribute),
                            attrArray = cardAttr.split('|').length > 1 ? cardAttr.split('|') : '';

                        // Checks if the card contains more than 1 value on the attribute and checks it
                        if (attrArray) {
                            attrArray.forEach(function (attr) {
                                if (attr === input.value) {
                                    filterArray.push(card);
                                }
                            });
                        // Checks if there's only one value
                        } else if (cardAttr === input.value) {
                            filterArray.push(card);
                        }
                    });
                });
            // If there's no items on the array it shows all cards
            } else {
                supplierCards.forEach(function (card) {
                    filterArray.push(card);
                });
            }

            return filterArray;
        }

        // Querys the suppliers to display
        function _filteredData(type, markets, transportation, services, industries, responses) {
            var fTypes = [],
                fMarkets = [],
                fTransportation = [],
                fServices = [],
                fIndustries = [],
                fResponses = [],
                iMatchAllType = element.querySelector('.supplier-filter.service-type'),
                iMatchAllMarkets = element.querySelector('.supplier-filter.market'),
                iMatchAllTransportation = element.querySelector('.supplier-filter.modes-trans'),
                iMatchAllServices = element.querySelector('.supplier-filter.services'),
                iMatchAllIndustries = element.querySelector('.supplier-filter.industries'),
                iMatchAllResponses = element.querySelector('.supplier-filter.response-times');

            // Checks if some of the arrays contains items
            if ([type, markets, transportation, services, industries, responses].some(_checkArr)) {
                // Checks every item inside the array and compares it with the data attribute selected
                fTypes = _setFilterData(type, 'data-service-type');
                fMarkets = _setFilterData(markets, 'data-markets');
                fTransportation = _setFilterData(transportation, 'data-transportation');
                fServices = _setFilterData(services, 'data-services');
                fIndustries = _setFilterData(industries, 'data-industries');
                fResponses = _setFilterData(responses, 'data-response');

                if (iMatchAllType && iMatchAllType.getAttribute('data-match-all') === 'true') {
                    fTypes = _reduceFilteredArray(fTypes, iMatchAllType, 'data-service-type');
                }

                if (iMatchAllMarkets && iMatchAllMarkets.getAttribute('data-match-all') === 'true') {
                    fMarkets = _reduceFilteredArray(fMarkets, iMatchAllMarkets, 'data-markets');
                }

                if (iMatchAllTransportation && iMatchAllTransportation.getAttribute('data-match-all') === 'true') {
                    fTransportation = _reduceFilteredArray(fTransportation, iMatchAllTransportation, 'data-transportation');
                }

                if (iMatchAllServices && iMatchAllServices.getAttribute('data-match-all') === 'true') {
                    fServices = _reduceFilteredArray(fServices, iMatchAllServices, 'data-services');
                }

                if (iMatchAllIndustries && iMatchAllIndustries.getAttribute('data-match-all') === 'true') {
                    fIndustries = _reduceFilteredArray(fIndustries, iMatchAllIndustries, 'data-industries');
                }

                if (iMatchAllResponses && iMatchAllResponses.getAttribute('data-match-all') === 'true') {
                    fResponses = _reduceFilteredArray(fResponses, iMatchAllResponses, 'data-response');
                }
            }

            // Sends selected items to be filtered
            _showFilteredData(fTypes, fMarkets, fTransportation, fServices, fIndustries, fResponses);
        }

        // Helper Function to Filter Data
        function _setFilters(e) {
            var type = [],
                markets = [],
                transportation = [],
                services = [],
                industries = [],
                responses = [],
                selection = e.target || e;

            _isChecked();

            // Looks for every input checked and adds the values to each section filter array
            filterInputs.forEach(function (input) {
                if (input.checked === true) {
                    switch (input.getAttribute('data-filter')) {
                        case 'service-type':
                            type.push(input);
                            break;
                        case 'market':
                            markets.push(input);
                            break;
                        case 'modes-trans':
                            transportation.push(input);
                            break;
                        case 'services':
                            services.push(input);
                            break;
                        case 'industries':
                            industries.push(input);
                            break;
                        case 'response-times':
                            responses.push(input);
                            break;
                    }
                }
            });

            _filteredData(type, markets, transportation, services, industries, responses);
            _setTags(selection);
        }

        // Attach events
        function _attachEvents() {
            var filterBtn = element.querySelector('.show-filters'),
                chevronBtn = element.querySelectorAll('.filter-title .chevron'),
                showMoreBtn = element.querySelectorAll('.show-more'),
                clearAll = element.querySelectorAll('.clear-filter, .clear-tag'),
                btnsSupplierCard = element.querySelectorAll('.supplier-card a');

            EDC.utils.attachEvents(filterInputs, 'change', _setFilters);
            EDC.utils.attachEvents(filterBtn, 'click', _showFilter);
            EDC.utils.attachEvents(closeBtn, 'click', _applyFilters);
            EDC.utils.attachEvents(applyAll, 'click', _applyFilters);
            EDC.utils.attachEvents(chevronBtn, 'click', _expandCollapseFilter);
            EDC.utils.attachEvents(showMoreBtn, 'click', _showMoreOptions);
            EDC.utils.attachEvents(clearAll, 'click', _removeSelections);
            EDC.utils.attachEvents(btnsSupplierCard, 'click', _trackEvent);

            EDC.utils.attachEvents(window, 'resize', function () {
                if (isDevice !== _isDevice()) {
                    isDevice = _isDevice();
                    _showHideFilter();
                }
            });
        }

        isDevice = _isDevice();
        _showHideFilter();
        _sideBarInit();
        _attachEvents();
    }

    // Public methods
    function init() {
        var inListFilter = document.querySelectorAll('.c-inlist-filter:not([data-component-state="initialized"])');

        if (inListFilter) {
            inListFilter.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', inListSupplierFilterJS.init);