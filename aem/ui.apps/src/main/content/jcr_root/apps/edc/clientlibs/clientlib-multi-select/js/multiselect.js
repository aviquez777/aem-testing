var MultiSelect = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var displayBtns = element.querySelectorAll('.display-options'),
            searchInputSection = element.querySelector('.search-input-section'),
            closeBtn = searchInputSection.querySelector('button'),
            searchInput = searchInputSection.querySelector('.search-input'),
            searchResults = element.querySelector('.search-results'),
            resultsList = searchResults.querySelectorAll('ul li:not(.no-results)'),
            checkboxes = element.querySelectorAll('.checkbox-group input'),
            selectAll = element.querySelectorAll('.select-all'),
            searchIcon = searchInputSection.querySelector('button'),
            resetBtn = element.querySelector('.reset-options button'),
            optionsSection = element.querySelector('.options-by-categories .options-by-categories-content'),
            prepopulateValues = element.getAttribute('data-prepopulate-values');

        // Private methods
        // Data Layer
        function _trackEvent(e, type) {
            var el = e.target,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: element.dataset.eventName,
                        eventAction: element.dataset.eventAction,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: '',
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel
                    }
                };

            if (type === 'selection') {
                obj.eventInfo.eventText = el ? el.closest('.checkbox-group').querySelector('label').textContent : '';
                obj.eventInfo.eventValue = el ? el.textContent : '';
                EDC.utils.dataLayerTracking(obj);
            } else if (type === 'select-all') {
                setTimeout(function () {
                    obj.eventInfo.eventValue2 = '';
                    if (el) {
                        obj.eventInfo.eventValue3 = el.textContent === el.getAttribute('data-deselect-text') ? el.getAttribute('data-select-text') : el.getAttribute('data-deselect-text');
                    }
                    element.querySelectorAll('.options-by-categories-content .category').forEach(function (category) {
                        if (parseInt(category.querySelector('.count-section .count').textContent, 10) ===
                        category.querySelectorAll('.content-section .options-section .checkbox-group').length) {
                            obj.eventInfo.eventValue2 += (obj.eventInfo.eventValue2 !== '' ? ';' : '') + category.getAttribute('data-section-category');
                        }
                        EDC.utils.dataLayerTracking(obj);
                    });
                }, 0);
            } else if (type === 'reset') {
                obj.eventInfo.eventValue3 = 'reset';
                EDC.utils.dataLayerTracking(obj);
            }
        }

        function _optionsSectionScrolled() {
            var target = element.querySelector('.options-by-categories-content'),
                targetParent = target.closest('.options-by-categories'),
                scrollSize = target.scrollHeight - target.offsetHeight,
                newScrollTop = Math.round(target.scrollTop),
                categories = element.querySelectorAll('.category'),
                counter = 0;

            if (newScrollTop > 0 && newScrollTop < scrollSize) {
                targetParent.classList.add('shadow-before');
                targetParent.classList.add('shadow-after');
            } else if (newScrollTop === 0) {
                targetParent.classList.add('shadow-after');
                targetParent.classList.remove('shadow-before');
            } else if (newScrollTop >= scrollSize) {
                targetParent.classList.remove('shadow-after');
                targetParent.classList.add('shadow-before');
            }

            categories.forEach(function (cat) {
                if (!cat.classList.contains('open')) {
                    counter++;
                }
            });

            if (categories.length === counter) {
                targetParent.classList.remove('shadow-before');
                targetParent.classList.remove('shadow-after');
            }
        }

        function _toggleCategory(e, action) {
            var allCategories = element.querySelectorAll('.category'),
                categoryTarget = e.currentTarget ? e.currentTarget.closest('.category') : e;

            if (action && action === 'open' || !categoryTarget.classList.contains('open')) {
                allCategories.forEach(function (cat) {
                    if (cat === categoryTarget) {
                        cat.classList.add('open');
                    } else {
                        cat.classList.remove('open');
                    }
                });
            } else if (categoryTarget.classList.contains('open')) {
                categoryTarget.classList.remove('open');
            }
        }

        function _searchArgumentEntered(e) {
            var value = e.target.value,
                count = 0,
                noResults = searchResults.querySelector('.no-results');

            if (value === '') {
                searchResults.classList.add('hide');
                searchInputSection.classList.remove('searching');
            } else {
                resultsList.forEach(function (item) {
                    item.classList.add('hide');

                    if (item.textContent.toLowerCase().indexOf(value.toLowerCase()) > -1) {
                        item.classList.remove('hide');
                        noResults.classList.add('hide');
                        count++;
                    }
                });

                if (count === 0) {
                    noResults.classList.remove('hide');
                }
                searchResults.classList.remove('hide');
                searchInputSection.classList.add('searching');
            }

            _trackEvent(e, 'search');
        }

        function _closeResults() {
            searchResults.classList.add('hide');
            searchInput.value = '';
            searchInputSection.classList.remove('searching');
        }

        function _checkCounter(category, categoryLi, currentSelectAll) {
            var counter = 0,
                selectedCount = 0;

            category.querySelectorAll('input').forEach(function (item) {
                if (item.checked) {
                    counter++;
                }
            });

            selectedCount = category.querySelector('.count-section .count');
            if (counter > 0) {
                selectedCount.textContent = counter;
                selectedCount.classList.remove('hide');
            } else {
                selectedCount.textContent = 0;
                selectedCount.classList.add('hide');
            }

            if (counter < category.querySelectorAll('input').length && categoryLi) {
                categoryLi.classList.remove('selected');
                currentSelectAll.textContent = currentSelectAll.getAttribute('data-select-text');
            } else if (categoryLi && currentSelectAll && counter === category.querySelectorAll('input').length) {
                categoryLi.classList.add('selected');
                currentSelectAll.textContent = currentSelectAll.getAttribute('data-deselect-text');
            }
        }

        function _checkUncheckInput(input, inputGroup, select) {
            if (select) {
                input.classList.add('selected');
                inputGroup.classList.add('selected');
            } else {
                input.classList.remove('selected');
                inputGroup.classList.remove('selected');
            }
        }

        function _selectAllClicked(e) {
            var target = e.currentTarget,
                category = target ? target.closest('.category') : null,
                thisSelectAll = target ? category.querySelector('.select-all') : null,
                selectAllText = target ? thisSelectAll.getAttribute('data-select-text') : null,
                deselectAllText = target ? thisSelectAll.getAttribute('data-deselect-text') : null,
                inputs,
                allTitles = element.querySelectorAll('.category-title'),
                categoryLi;

            if (target) {
                if (target.classList.contains('clicked')) {
                    target.classList.remove('clicked');
                } else {
                    target.classList.add('clicked');
                }

                if (thisSelectAll.textContent === selectAllText) {
                    thisSelectAll.textContent = deselectAllText;
                } else if (thisSelectAll.textContent === deselectAllText) {
                    thisSelectAll.textContent = selectAllText;
                }
            }

            if (target && category) {
                inputs = category.querySelectorAll('input[type="checkbox"]');
                if (target.getAttribute('data-deselect-text') === target.textContent) {
                    inputs.forEach(function (input) {
                        if (!input.closest('.checkbox-group').classList.contains('selected')) {
                            input.checked = true;
                            _checkUncheckInput(element.querySelector('.search-results ul li[data-value="' + input.value + '"]'), input.closest('.checkbox-group'), true);
                        }
                    });

                    element.querySelector('.search-results ul li[data-category="' +
                        category.querySelector('input').getAttribute('data-category') +
                        '"][data-value="category"]').classList.add('selected');
                } else if (target.getAttribute('data-select-text') === target.textContent) {
                    inputs.forEach(function (input) {
                        if (input.closest('.checkbox-group').classList.contains('selected')) {
                            input.checked = false;
                            _checkUncheckInput(element.querySelector('.search-results ul li[data-value="' + input.value + '"]'), input.closest('.checkbox-group'), false);
                        }
                    });
                }
                categoryLi = searchResults.querySelector('li[data-value="category"][data-category="' + target.closest('.category').getAttribute('data-section-category') + '"]');
                _checkCounter(category, categoryLi, target);
                _toggleCategory(category, 'open');
            } else {
                allTitles.forEach(function (title) {
                    var currentSelectAll = title.closest('.title-section').querySelector('.select-all');

                    if (title.textContent === e) {
                        if (currentSelectAll.textContent === currentSelectAll.getAttribute('data-select-text')) {
                            currentSelectAll.click();
                        } else {
                            _toggleCategory(title.closest('.category'), 'open');
                        }
                    }
                });
            }

            element.classList.remove('error');
            _trackEvent(e, 'select-all');
        }

        function _tagsRemove(e) {
            var target = e.currentTarget,
                parentTag = target.closest('.result-tag'),
                tagValue = parentTag.getAttribute('data-value'),
                category = parentTag.getAttribute('data-category'),
                inputsToClick = tagValue === 'category' ?
                    element.querySelectorAll('.category input[data-category="' + category + '"]') :
                    element.querySelectorAll('.category input[value="' + tagValue + '"]');

            if (inputsToClick.length > 0) {
                inputsToClick.forEach(function (input) {
                    input.click();
                });
            }

            if (parentTag && parentTag.parent) {
                parentTag.parent.removeChild(parentTag);
            }

            if (element.querySelectorAll('.result-tag').length === 0) {
                element.querySelector('.results-tags').classList.add('no-border');
            }
        }

        function _addTagsForMobile() {
            var resultsTags = element.querySelector('.results-tags');

            resultsTags.innerHTML = '';
            resultsList.forEach(function (li) {
                var div,
                    text,
                    closeIcon,
                    isCategory,
                    category = li.getAttribute('data-category'),
                    categoryCount = element.querySelector('.category[data-section-category="' + category + '"] .count-section .count').textContent,
                    currentResults = resultsTags.querySelectorAll('.result-tag'),
                    fullCategorySelected = false;

                currentResults.forEach(function (result) {
                    if (category === result.getAttribute('data-category') && result.getAttribute('is-category') === 'true') {
                        fullCategorySelected = true;
                    }
                });

                if (li.classList.contains('selected') && !fullCategorySelected) {
                    div = document.createElement('div');
                    text = document.createElement('span');
                    closeIcon = document.createElement('span');
                    closeIcon.classList.add('tags-close');
                    text.classList.add('tags-text');
                    div.classList.add('result-tag');
                    div.setAttribute('data-category', category);
                    div.appendChild(text);
                    div.appendChild(closeIcon);
                    isCategory = li.getAttribute('data-value') === 'category';
                    text.textContent = isCategory ? category + ' (' + categoryCount + ')' : li.textContent;
                    div.setAttribute('is-category', isCategory);
                    div.setAttribute('data-value', li.getAttribute('data-value'));
                    EDC.utils.attachEvents(closeIcon, 'click', _tagsRemove);
                    resultsTags.appendChild(div);
                    resultsTags.classList.remove('no-border');
                }
            });
        }

        function _focusOnSearch() {
            searchInput.focus();
        }

        function _resultSelected(e) {
            var target = e.currentTarget,
                isInput = target.tagName.toLowerCase() === 'input',
                checkboxGroup,
                category,
                input,
                value,
                categoryLi,
                currentSelectAll;

            element.classList.remove('error');
            if (!isInput) {
                category = target.getAttribute('data-category');
                value = target.getAttribute('data-value');

                if (value === 'category') {
                    _selectAllClicked(category);
                } else {
                    input = element.querySelector('.category input[type="checkbox"][value="' + value + '"]');
                    if (!input.checked) {
                        input.click();
                    }
                }
                target.classList.add('selected');
            } else {
                checkboxGroup = target.closest('.checkbox-group');
                value = target.value;
                input = element.querySelector('.search-results ul li[data-value="' + value + '"]');
                category = target.closest('.category');
                categoryLi = searchResults.querySelector('li[data-value="category"][data-category="' + input.getAttribute('data-category') + '"]');
                currentSelectAll = target.closest('.content-section').querySelector('.select-all');

                if (target.checked) {
                    _checkUncheckInput(input, checkboxGroup, true);
                    _trackEvent(e, 'selection');
                } else {
                    _checkUncheckInput(input, checkboxGroup, false);
                }

                _checkCounter(category, categoryLi, currentSelectAll);
                _toggleCategory(category, 'open');
            }

            _addTagsForMobile();
            _closeResults();
            _focusOnSearch();
        }

        function _resetToInitialState(e) {
            var categories = element.querySelectorAll('.category');

            _closeResults();
            checkboxes.forEach(function (checkbox) {
                if (checkbox.checked) {
                    _checkUncheckInput(element.querySelector('.search-results ul li[data-value="' + checkbox.value + '"]'), checkbox.closest('.checkbox-group'), false);
                    checkbox.checked = false;
                }
            });

            categories.forEach(function (cat) {
                var categoryLi = searchResults.querySelector('li[data-value="category"][data-category="' + cat.getAttribute('data-section-category') + '"]');

                _checkCounter(cat, categoryLi, cat.querySelector('.select-all'));
                cat.classList.remove('open');
            });

            element.classList.remove('error');
            _focusOnSearch();
            _trackEvent(e, 'reset');
        }

        function _prepopulate() {
            if (prepopulateValues) {
                prepopulateValues.split('::').forEach(function (value) {
                    var el = searchResults.querySelector('li[data-value="' + value + '"]');

                    if (el) {
                        el.click();
                    }
                });
            }
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(displayBtns, 'click', _toggleCategory);
            EDC.utils.attachEvents(searchInput, 'input', _searchArgumentEntered);
            EDC.utils.attachEvents(closeBtn, 'click', _closeResults);
            EDC.utils.attachEvents(resultsList, 'click', _resultSelected);
            EDC.utils.attachEvents(checkboxes, 'click', _resultSelected);
            EDC.utils.attachEvents(selectAll, 'click', _selectAllClicked);
            EDC.utils.attachEvents(searchIcon, 'click', _focusOnSearch);
            EDC.utils.attachEvents(resetBtn, 'click', _resetToInitialState);
            EDC.utils.attachEvents(optionsSection, 'scroll', _optionsSectionScrolled);
        }

        _attachEvents();
        _prepopulate();
    }

    // Public methods
    function init() {
        var multiSelect = document.querySelectorAll('.c-multi-select:not([data-component-state="initialized"])');

        if (multiSelect) {
            multiSelect.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', MultiSelect.init);