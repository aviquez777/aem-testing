var covidResponsePageFilterJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var filtersBody = document.querySelector('body'),
            filterContainer = element.querySelector('.cards-filters'),
            filterCategories = filterContainer.querySelectorAll('.card-filter'),
            filterTitle = element.querySelector('.filter-title'),
            closeBtn = element.querySelector('.close-button'),
            applyAll = element.querySelector('.apply-filter'),
            filterTags = element.querySelector('.filter-tags'),
            filterTagsList = filterTags ? filterTags.querySelector('ul') : '',
            noMatch = element.querySelector('.no-match'),
            allCards = element.querySelectorAll('.c-covid-response-page-card'),
            modalSize = 0,
            timerId = null,
            timeout = 5,
            sideBar,
            isDevice;

        // Private functions

        // Helper function to get selected filters per secion
        function _getFilterValues(filter) {
            var selectedValues = [];

            if (filter) {
                filter.querySelectorAll('.filter-option:checked').forEach(function (option) {
                    selectedValues.push(option.getAttribute('data-english-name').toLowerCase());
                });
            }

            return selectedValues.join(';');
        }

        // Data Layer
        function _trackEvent(e) {
            var target = e ? e.currentTarget : null,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent.toLowerCase(),
                        eventType: element.dataset.eventType.toLowerCase(),
                        eventName: element.dataset.eventName.toLowerCase(),
                        eventAction: element.dataset.eventAction,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        totaResults: element.querySelectorAll('.c-covid-response-page-card:not(.hide)').length
                    }
                };

            if (target) {
                obj.eventInfo.eventText = target.closest('.c-covid-response-page-card').querySelector('.title').textContent.toLowerCase();
                obj.eventInfo.eventLevel = element.dataset.eventEngagement;
                obj.eventInfo.destinationPage = target.getAttribute('href');
                obj.eventInfo.engagementType = element.dataset.eventEngagement;
            }

            filterCategories.forEach(function (filterCategory, i) {
                obj.eventInfo[i === 0 ? 'eventValue' : 'eventValue' + (i + 1)] = _getFilterValues(filterCategory);
            });

            EDC.utils.dataLayerTracking(obj);
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

        // Helper function to determine display width and return if is a mobile device
        function _isDevice() {
            return window.innerWidth < EDC.props.media.lg;
        }

        // Helper function to determine if there's any filter checked
        function _isChecked() {
            applyAll.disabled = element.querySelectorAll('.filter-option:checked').length === 0;
        }

        // Helper function to Show Filters
        function _showFilter() {
            modalSize = window.innerWidth;
            filterContainer.classList.add('show');
            filterTitle.classList.add('filter-open');
            closeBtn.focus();

            _isChecked();

            if (sideBar) {
                sideBar.style.display = 'none';
            }

            // Disables scrolling on body and html elements on all devices
            bodyScrollLock.disableBodyScroll(filterContainer);
        }

        // Helper function to Hide Filters
        function _hideFilter() {
            // Resets Modals and filter classes
            modalSize = 0;
            filterContainer.classList.remove('show');
            filterTitle.classList.remove('filter-open');

            // Shows the sidebar
            if (sideBar) {
                sideBar.style.display = 'block';
            }

            // Enables the scroll on body
            bodyScrollLock.enableBodyScroll(filterContainer);
        }

        // Expand / Collapse Filters
        function _expandCollapseFilter(e) {
            var target = e.target.classList.contains('chevron') ? e.target : e.target.parentElement,
                filterSection = target.closest('.card-filter'),
                filterOptions = filterSection.querySelectorAll('.filter-option');

            if (filterSection) {
                if (filterSection.classList.contains('collapsed')) {
                    target.setAttribute('aria-label', target.getAttribute('data-hide'));
                    filterSection.classList.remove('collapsed');
                    filterSection.setAttribute('aria-expanded', true);

                    filterOptions.forEach(function (option) {
                        option.setAttribute('tabindex', '0');
                    });
                } else {
                    target.setAttribute('aria-label', target.getAttribute('data-show'));
                    filterSection.classList.add('collapsed');
                    filterSection.setAttribute('aria-expanded', false);

                    filterOptions.forEach(function (option) {
                        option.setAttribute('tabindex', '-1');
                    });
                }
            }
        }

        // Sets visibility of the filters on load
        function _showHideFilter() {
            if (!isDevice) {
                filterContainer.classList.add('show');
                bodyScrollLock.enableBodyScroll(filterContainer);
            } else if (window.innerWidth !== modalSize) {
                filterContainer.classList.remove('show');
            }
        }

        // Shows / Hides filter tags
        function _checkFilterTags() {
            var availableTags = filterTagsList.querySelectorAll('.filter-tag, .clear');

            if (availableTags.length <= 1) {
                filterTags.classList.add('hide');
            } else {
                filterTags.classList.remove('hide');
            }
        }

        // Performs the cards filtration
        function _cardsFiltration(criterias) {
            var cardsToHandle = [],
                cardsToShow = [],
                moreThanOneSectionCounter = false,
                showError = false;

            allCards.forEach(function (card) {
                card.classList.add('hide');
            });

            if (criterias && criterias.length > 0) {
                filterCategories.forEach(function (cat) {
                    var categoryName = cat.getAttribute('data-name'),
                        finalCards = [],
                        firstTime = true;

                    criterias.forEach(function (criteria) {
                        var thisCategory = criteria.closest('.card-filter').getAttribute('data-name'),
                            criteriaText = criteria.value;

                        if (categoryName === thisCategory) {
                            if (firstTime) {
                                moreThanOneSectionCounter++;
                                firstTime = false;
                            }

                            allCards.forEach(function (card) {
                                var tags = card.getAttribute('data-tags').split('::'),
                                    subcatFlag = true;

                                tags.forEach(function (tag) {
                                    var subcat = tag.indexOf(criteriaText + '/') > -1;

                                    if (criteriaText === tag || (subcat && subcatFlag)) {
                                        finalCards.push(card);

                                        if (criteriaText !== tag && subcat) {
                                            subcatFlag = false;
                                        }
                                    }
                                });
                            });
                        }
                    });

                    if (finalCards.length > 0) {
                        cardsToHandle.push(finalCards);
                    }
                });

                if (moreThanOneSectionCounter > 1 && cardsToHandle.length < moreThanOneSectionCounter) {
                    cardsToHandle.push([]);
                }

                if (cardsToHandle.length > 1) {
                    cardsToHandle[0].forEach(function (card) {
                        var j,
                            valid = true,
                            counter;

                        for (j = 1; j < cardsToHandle.length; j++) {
                            counter = 0;
                            if (valid && cardsToHandle[j]) {
                                cardsToHandle[j].forEach(function (thisCard) {
                                    if (card === thisCard) {
                                        counter++;
                                    }
                                });
                                valid = counter > 0;
                            }
                        }

                        if (valid) {
                            cardsToShow.push(card);
                        }
                    });
                } else if (cardsToHandle[0]) {
                    cardsToShow = cardsToHandle[0];
                }

                if (cardsToShow.length === 0) {
                    showError = true;
                }

                cardsToShow.forEach(function (card) {
                    card.classList.remove('hide');
                });
            } else {
                allCards.forEach(function (card) {
                    card.classList.remove('hide');
                });
            }

            if (showError) {
                noMatch.classList.remove('hide');
                _trackEvent();
            } else {
                noMatch.classList.add('hide');
            }
        }

        // Removes selected tag or all options and tags
        function _removeSelections(e) {
            var target = e ? e.target.classList.contains('remove-tag') ? e.target : e.target.parentElement : '',
                tag = target && target.classList.contains('remove-tag') ? target.parentElement : '',
                tagValue = tag ? tag.getAttribute('data-tag-filter') : '',
                isClearAllDevice = target && target.classList.contains('button-option'),
                selectedInput,
                currentTags = [];

            // Gets the tag value and removes it from the filter.
            if (tagValue && !isClearAllDevice) {
                selectedInput = element.querySelector('.filter-option[value="' + tagValue + '"]');

                if (selectedInput) {
                    EDC.utils.simulateClick(selectedInput);
                }

                element.querySelectorAll('.filter-option:checked').forEach(function (filterTag) {
                    currentTags.push(filterTag);
                });

                _cardsFiltration(currentTags);
            } else {
                // Removes the tags
                if (filterTagsList) {
                    while (filterTagsList.firstElementChild && filterTagsList.firstElementChild.classList.contains('filter-tag')) {
                        filterTagsList.removeChild(filterTagsList.firstElementChild);
                    }
                }

                // Unchecks all the checboxes
                element.querySelectorAll('.filter-option:checked').forEach(function (input) {
                    input.checked = false;
                });

                // Hides oppened subfilters
                element.querySelectorAll('.subfilters').forEach(function (subfilter) {
                    subfilter.classList.add('hide');
                });

                _cardsFiltration();
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
            listItem.setAttribute('data-category-name', el.closest('.card-filter').getAttribute('data-name'));
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
            } else {
                deleteTag = filterTagsList.querySelector('.filter-tag[data-tag-filter="' + el.value + '"]');
                deleteTag.parentNode.removeChild(deleteTag);
            }

            _checkFilterTags();
        }

        // Helper function to get the selected tags
        function _getselectedFilters() {
            var checkedFilters = [];

            // Iterates on each filter filter to get the selected values
            filterCategories.forEach(function (filter) {
                var filterList = filter.querySelector('.filters'),
                    selectedItems = filterList ? filterList.querySelectorAll('.filter > .filter-option:checked') : '';

                // For each filter checks if the filter is selected and if it contains subcategories
                selectedItems.forEach(function (item) {
                    var subFilters = item.parentElement.querySelectorAll('.subfilter .filter-option:checked');

                    // If the filter contains subcategories gets the selected values, if not it will get the value of the main category
                    if (subFilters.length) {
                        subFilters.forEach(function (subFilter) {
                            checkedFilters.push(subFilter);
                        });
                    } else {
                        checkedFilters.push(item);
                    }
                });
            });
            _cardsFiltration(checkedFilters);
        }

        // Function to set the filters and display data
        function _setFilters(e) {
            var selection = e.target || e,
                subfilters = selection.parentElement.querySelector('.subfilters');

            _isChecked();

            // Shows / hides the subfilters
            if (subfilters) {
                subfilters.classList.toggle('hide');

                if (subfilters.classList.contains('hide')) {
                    subfilters.querySelectorAll('.subfilter input:checked').forEach(function (option) {
                        option.checked = false;
                        _setTags(option);
                    });
                }
            }

            // Creates the tag cloud
            _setTags(selection);

            // Fills the tags
            _getselectedFilters();
        }

        // Attach events
        function _attachEvents() {
            var filterBtn = element.querySelector('.show-filters'),
                chevronBtn = element.querySelectorAll('.filter-title .chevron'),
                clearAll = element.querySelectorAll('.clear-filter, .clear-tag'),
                filterInputs = element.querySelectorAll('.filter-option');

            EDC.utils.attachEvents(filterInputs, 'change', _setFilters);
            EDC.utils.attachEvents(filterBtn, 'click', _showFilter);
            EDC.utils.attachEvents(chevronBtn, 'click', _expandCollapseFilter);
            EDC.utils.attachEvents(closeBtn, 'click', _hideFilter);
            EDC.utils.attachEvents(applyAll, 'click', _hideFilter);
            EDC.utils.attachEvents(clearAll, 'click', _removeSelections);

            allCards.forEach(function (card) {
                EDC.utils.attachEvents(card.querySelectorAll('a'), 'click', _trackEvent);
            });

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
        var covidResponsePageFilters = document.querySelectorAll('.c-covid-response-page-filter:not([data-component-state="initialized"])');

        if (covidResponsePageFilters) {
            covidResponsePageFilters.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', covidResponsePageFilterJS.init);