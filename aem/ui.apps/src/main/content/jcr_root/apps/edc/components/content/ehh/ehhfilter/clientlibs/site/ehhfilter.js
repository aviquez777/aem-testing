var exportHelpHubFilterJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var actionURL = element.getAttribute('data-action') ? element.getAttribute('data-action') : '',
            searchURL = element.getAttribute('data-search-url') ? element.getAttribute('data-search-url') : '#',
            linkTarget = element.getAttribute('data-link-target') ? element.getAttribute('data-link-target') : '_blank',
            linkText = element.getAttribute('data-link-text') ? element.getAttribute('data-link-text') : '',
            showMore = element.getAttribute('data-show-more') ? element.getAttribute('data-show-more') : '',
            showLess = element.getAttribute('data-show-less') ? element.getAttribute('data-show-less') : '',
            catContainer = element.querySelector('.categories'),
            mrktDropdown = element.querySelector('.markets-dropdown'),
            marktSelect = mrktDropdown ? mrktDropdown.querySelector('select') : '',
            catDropdown = element.querySelector('.categories-dropdown'),
            filterSelect = catDropdown ? catDropdown.querySelector('select') : '',
            filterMenuList = element.querySelector('.categories-list-menu'),
            categoriesTitle = element.querySelector('.categories-title'),
            mainTitle = element.querySelector('.main-title'),
            clearAllButton = element.querySelector('.clear-filter'),
            srNotifications = element.querySelector('.sr-only.notifications'),
            otherMarket = element.querySelector('.other-market'),
            d = document,
            obj = {},
            filterValue,
            filterOptions,
            selectedMarket,
            categories,
            marketCategories,
            categoryCookie = EDC.utils.getCookie('MarketFilterCategory');

        // Private methods

        // Data Layer
        function _trackEvent(dataLayerObj) {
            EDC.utils.dataLayerTracking(dataLayerObj);
        }

        function _trackCategoryLinksClick(e) {
            var elem = e.target.tagName.toLowerCase() === 'span' ? e.target.parentElement : e.target,
                category = elem.closest('.category').querySelector('.category-name'),
                elemText = elem.querySelector('span').innerHTML.toLowerCase(),
                categoryName = category ? category.innerHTML.toLowerCase() : '',
                eventName = elem.dataset.eventName,
                eventType = elem.dataset.eventType,
                eventText;

            if (elem.classList.contains('button') || elem.classList.contains('show-more')) {
                eventText = elemText + ' - ' + categoryName;

                if (elem.classList.contains('show-more')) {
                    eventName = elem.parentElement.dataset.eventName;
                    eventType = elem.parentElement.dataset.eventType;
                }
            } else {
                eventText = elemText;
            }

            obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: eventType,
                    eventName: eventName,
                    eventAction: element.dataset.eventAction,
                    eventText: eventText,
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: '',
                    engagementType: element.dataset.eventEngagement,
                    eventLevel: '',
                    eventStage: ''
                }
            };

            if (category.classList.contains('filtered')) {
                obj.eventInfo.eventValue = categoryName;
            }

            _trackEvent(obj);

        }

        // Helper function to set the selected value on resize
        function _setSelectedValue() {
            var dropdownElement = filterSelect.parentElement,
                dropdownMenu = dropdownElement.querySelector('.menu'),
                dropdownLabel = dropdownElement.querySelector('.text');

            if (filterValue) {
                filterOptions.forEach(function (option) {
                    if (option.value === filterValue) {
                        option.checked = true;
                    }
                });

                dropdownLabel.classList.remove('default');
                dropdownMenu.classList.add('transition');
                dropdownMenu.classList.add('hidden');

                dropdownElement.querySelectorAll('.item').forEach(function (item) {
                    if (item.getAttribute('data-value') === filterValue) {
                        item.classList.add('active');
                        item.classList.add('selected');
                        dropdownLabel.innerHTML = item.innerHTML;
                    }
                });

                filterSelect.value = filterValue;
            }
        }

        // Helper function that shows and hides the questions on landing page
        function _showHideMoreQuestions(e, action) {
            var elem = e.target.classList.contains(action) ? e.target : e.target.parentElement,
                elemCategory = elem.closest('.category'),
                hiddenQuestions = elemCategory.querySelectorAll('.question:not(.landing)');

            hiddenQuestions.forEach(function (question) {
                if (action === 'show-more' && question.getAttribute('data-market') === selectedMarket && !question.classList.contains('duplicate')) {
                    question.classList.remove('hide');
                } else if (action === 'show-less') {
                    question.classList.add('hide');
                }
            });

            elem.classList.add('hide');

            if (action === 'show-more') {
                elemCategory.querySelector('button.show-less').classList.remove('hide');
            } else if (action === 'show-less') {
                elemCategory.querySelector('button.show-more').classList.remove('hide');
            }
        }

        // Helper function to reset the filter
        function _clearCategories() {
            var filterDropdown = filterSelect.parentElement,
                filterDropdownText = filterDropdown.querySelector('div.text'),
                visibleCounter = 0;

            clearAllButton.classList.add('hide');
            filterValue = '';

            // Resets Accecibily text
            srNotifications.innerHTML = '';

            // Clears the radio button options
            filterOptions.forEach(function (option) {
                option.checked = false;
            });

            // Clears the dropdown
            filterSelect.selectedIndex = 0;
            filterDropdownText.classList.add('default');
            filterDropdownText.innerHTML = filterSelect.getAttribute('data-default-value');

            filterDropdown.querySelectorAll('.item').forEach(function (option) {
                option.classList.remove('active');
                option.classList.remove('selected');
            });

            // Resets the filtered view
            categories.forEach(function (category) {
                var visibleQuestions = 0,
                    hiddenCount = 0;

                category.querySelector('.category-name').classList.remove('filtered');

                category.querySelectorAll('.sub-category .sub-category-title').forEach(function (title) {
                    title.classList.add('hide');
                });

                category.querySelectorAll('.question.landing').forEach(function (question) {
                    question.classList.remove('filtered');
                });

                // Shows hides all que questions that are not landing
                category.querySelectorAll('.question:not(.landing)').forEach(function (question) {
                    question.classList.add('hide');
                });

                visibleQuestions = category.querySelectorAll('.question:not(.hide)').length;

                // Count hidden questions
                category.querySelectorAll('.question:not(.landing)').forEach(function (question) {
                    if (question.getAttribute('data-market') === selectedMarket && !question.classList.contains('duplicate')) {
                        hiddenCount++;
                    }
                });

                if (visibleQuestions > 0) {
                    category.classList.remove('hide');

                    if (visibleCounter === 0) {
                        category.classList.add('first-visible');
                        visibleCounter++;
                    } else {
                        category.classList.remove('first-visible');
                    }
                } else {
                    category.classList.remove('first-visible');
                    category.classList.add('hide');
                }

                // Show More button will show only if there are hidden questions
                if (visibleQuestions >= 3 && hiddenCount > 0) {
                    category.querySelector('.show-more').classList.remove('hide');
                }

                category.querySelector('.show-less').classList.add('hide');
            });
        }

        // Helper function that filters all categories based on filter criteria
        function _filterCategories(value) {
            var visibleCounter = 0;

            clearAllButton.classList.remove('hide');
            categories.forEach(function (category) {
                var categoryQuestions = category.querySelectorAll('.question').length,
                    categoryName;

                // Validates if the category contains any questions
                if (value !== category.getAttribute('data-id')) {
                    category.classList.remove('first-visible');
                    category.classList.add('hide');
                } else if (categoryQuestions) {
                    categoryName = category.querySelector('.category-name');
                    categoryName.classList.add('filtered');
                    category.classList.remove('hide');

                    if (visibleCounter === 0) {
                        category.classList.add('first-visible');
                        visibleCounter++;
                    } else {
                        category.classList.remove('first-visible');
                    }
                    category.classList.add('fade-in');

                    // Accesibily update to anounce category change
                    srNotifications.innerHTML = srNotifications.getAttribute('data-text-start') + ' ' +
                        categoryName.innerHTML + ' ' + srNotifications.getAttribute('data-text-middle') + ' ' +
                        categoryQuestions + ' ' + srNotifications.getAttribute('data-text-end');

                    // Validates to show each category if it contains any questions
                    category.querySelectorAll('.sub-category').forEach(function (subCategory) {
                        var subCategoryQuestions = subCategory.querySelectorAll('.question'),
                            questionLanding = subCategory.querySelector('.question.landing'),
                            questionCount = 0;

                        if (subCategoryQuestions.length > 0) {

                            if (subCategory.classList.contains('general') && questionLanding) {
                                questionLanding.classList.add('filtered');
                            }

                            subCategoryQuestions.forEach(function (question) {
                                var questionMarket = question.getAttribute('data-market');

                                if (questionMarket === selectedMarket || questionMarket === 'general') {
                                    questionCount++;
                                    question.classList.remove('hide');
                                }
                            });

                            if (questionCount) {
                                subCategory.querySelector('.sub-category-title').classList.remove('hide');
                            }
                        }
                    });

                    // Hides all buttons from category
                    category.querySelectorAll('button').forEach(function (button) {
                        button.classList.add('hide');
                    });

                    // Sets up the dataLayer object
                    obj = {
                        eventInfo: {
                            eventComponent: element.dataset.eventComponent,
                            eventType: element.dataset.eventType,
                            eventName: element.dataset.eventName,
                            eventAction: element.dataset.eventAction,
                            eventText: categoryName.innerHTML.toLowerCase(),
                            eventPage: EDC.props.pageNameURL,
                            eventPageName: EDC.utils.getPageName(),
                            destinationPage: '',
                            engagementType: element.dataset.eventEngagement,
                            eventLevel: '',
                            eventStage: ''
                        }
                    };

                    _trackEvent(obj);
                }
            });
        }

        // Helper function to filter data
        function _setFilterCriteria(e) {
            filterValue = e.target.value;

            if (filterValue) {
                _filterCategories(filterValue);
                _setSelectedValue();
            }
        }

        // Helper function to create filter markup
        function _setCategoriesFilter(filters) {
            var filterDropdownMenu = catDropdown ? catDropdown.querySelector('.menu') : '',
                dropdownPlaceholder = catDropdown && catDropdown.getAttribute('data-placeholder') ? catDropdown.getAttribute('data-placeholder') : '';

            // Resets the HTML of the dropdown and menu
            filterDropdownMenu.innerHTML = '';
            filterMenuList.innerHTML = '';
            filterSelect.innerHTML = '<option value="" hidden="">' + dropdownPlaceholder + '</option>';

            // Validation added as per Vinnet's feedback on 9/25
            if (filters) {
                filters.forEach(function (filter, index) {
                    var id = filter.categoryId,
                        name = filter.categoryName,
                        item = d.createElement('li'),
                        input = d.createElement('input'),
                        label = d.createElement('label'),
                        menuItem = d.createElement('div'),
                        selectItem = d.createElement('option'),
                        inputAttrs = {
                            id: 'category-filter-' + index,
                            type: 'radio',
                            name: 'category-filter',
                            value: id,
                            'aria-labelledby': 'categories-title'
                        };

                    // Creates the markup for the Mobile filter options
                    menuItem.classList.add('item');
                    menuItem.setAttribute('data-value', id);
                    menuItem.innerText = name;

                    filterDropdownMenu.appendChild(menuItem);

                    selectItem.setAttribute('value', id);
                    selectItem.innerText = name;

                    filterSelect.appendChild(selectItem);

                    // Creates the markup for the Desktop filter options
                    item.classList.add('category-filter-option');

                    input.classList.add('filter-criteria');
                    EDC.utils.setAttributes(input, inputAttrs);

                    label.setAttribute('for', 'category-filter-' + index);
                    label.innerText = name;

                    item.appendChild(input);
                    item.appendChild(label);

                    filterMenuList.appendChild(item);
                });
            }

            filterOptions = element.querySelectorAll('.filter-criteria');

            EDC.utils.attachEvents(filterOptions, 'change', _setFilterCriteria);
            EDC.utils.attachEvents(filterSelect, 'change', _setFilterCriteria);
        }

        function _setCategoryCookie() {
            var market = selectedMarket,
                selectedCategory = element.querySelector('.filter-criteria[name="category-filter"]:checked'),
                category = selectedCategory ? selectedCategory.value : null,
                cookieContent;

            if (market !== null) {
                cookieContent = category !== null ? market + ',' + category : market;

                EDC.utils.setCookie('MarketFilterCategory', cookieContent);
            }
        }

        // Helper function to create categories markup
        function _setCategoriesContent(category) {
            var item = d.createElement('div'),
                title = d.createElement('h3'),
                descriptionContainer = d.createElement('div'),
                categoryButtons = d.createElement('div'),
                showMoreButton = d.createElement('button'),
                showLessButton = d.createElement('button'),
                categoryBtnAttrs = {
                    'data-event-type': 'link',
                    'data-event-name': 'link click - help hub'
                };

            // Sets div's attributes
            item.classList.add('category');
            item.setAttribute('data-id', category.categoryId);
            descriptionContainer.classList.add('description-container');

            // Sets title's attributes
            title.classList.add('category-name');
            title.setAttribute('id', category.categoryId);
            title.innerText = category.categoryName;

            // Get values from markets
            category.markets.forEach(function (market) {
                var description = d.createElement('div'),
                    paragraph = d.createElement('p'),
                    button,
                    buttonAttrs;

                // Sets description based on market
                description.classList.add('description-content');
                description.setAttribute('data-market', market.value);
                paragraph.classList.add('category-description');
                paragraph.innerText = market.description;
                description.appendChild(paragraph);

                // Sets link based on market
                if (market.categoryURL) {
                    button = d.createElement('a');
                    buttonAttrs = {
                        target: linkTarget,
                        'aria-labelledby': category.categoryId,
                        'data-event-type': 'button',
                        'data-event-name': 'button click - help hub',
                        href: market.categoryURL
                    };
                    EDC.utils.setClasses(button, ['button', 'edc-tertiary-btn', 'category-btn']);
                    EDC.utils.setAttributes(button, buttonAttrs);
                    button.innerHTML = '<span>' + linkText + '</span>';
                    description.appendChild(button);
                }

                descriptionContainer.appendChild(description);
            });

            // Adds the elements to the div
            item.appendChild(title);
            item.appendChild(descriptionContainer);

            // Adds the subcategories and questions
            category.subCategories.forEach(function (subcategory) {
                var subcategoryItem = d.createElement('div'),
                    subcategoryTitle = d.createElement('h3'),
                    questionList = d.createElement('ul'),
                    isGeneral = subcategory.general === 'yes';

                // Sets div's attributes
                subcategoryItem.classList.add('sub-category');
                subcategoryItem.setAttribute('data-id', subcategory.subCategoryId);

                if (isGeneral) {
                    subcategoryItem.classList.add('general');
                }

                // Sets title's attributes
                EDC.utils.setClasses(subcategoryTitle, ['sub-category-title', 'hide']);
                subcategoryTitle.innerText = subcategory.subCategoryName;

                // Creates the question list
                questionList.classList.add('question-list');
                subcategory.questions.forEach(function (question) {
                    var questionItem = d.createElement('li'),
                        questionLink = d.createElement('a'),
                        englishQuestion = question.metadata_english_question !== '' ? question.metadata_english_question : question.questions[0],
                        questionAttrs = {
                            'data-market': question.metadata_market,
                            'data-country': question.metadata_country
                        },
                        questionLinkAttrs = {
                            'data-event-type': 'link',
                            'data-event-name': 'link click - help hub',
                            'data-english-text': englishQuestion,
                            href: searchURL + '?query=' + encodeURIComponent(question.questions[0])
                                .replace(/\(/g, '%28')
                                .replace(/\)/g, '%29')
                                .replace(/\%26/g, '&')
                                .replace(/\%24/g, '$')
                                .replace(/\%22/g, '”'),
                            target: linkTarget
                        };

                    // href encode and replace parentheses and other symbols to avoid the resquest being blocked by the dispatcher,

                    EDC.utils.setAttributes(questionItem, questionAttrs);
                    questionItem.classList.add('question');

                    EDC.utils.setAttributes(questionLink, questionLinkAttrs);
                    questionLink.innerHTML = '<span>' + question.questions[0] + '</span>';

                    questionItem.appendChild(questionLink);
                    questionList.appendChild(questionItem);

                    EDC.utils.attachEvents(questionLink, 'click', _setCategoryCookie);
                });

                // Adds the elements to the div
                subcategoryItem.appendChild(subcategoryTitle);
                subcategoryItem.appendChild(questionList);

                // Adds the elements to the parent div
                item.appendChild(subcategoryItem);
            });

            categoryButtons.classList.add('category-buttons');
            EDC.utils.setAttributes(categoryButtons, categoryBtnAttrs);

            EDC.utils.setClasses(showMoreButton, ['edc-btn-unstyled', 'show-more']);
            showMoreButton.innerHTML = '<span>' + showMore + '</span>';
            categoryButtons.appendChild(showMoreButton);

            EDC.utils.setClasses(showLessButton, ['edc-btn-unstyled', 'show-less', 'hide']);
            showLessButton.innerHTML = '<span>' + showLess + '</span>';
            categoryButtons.appendChild(showLessButton);

            item.appendChild(categoryButtons);

            // Adds the div to the last position
            catContainer.insertBefore(item, catContainer.lastElementChild);
        }

        // Helper funcion to display the categories for selected market
        function _changeSelectedMarket() {
            var marketDescriptions = element.querySelectorAll('.description-content'),
                categoryQuestions = element.querySelectorAll('.question'),
                visibleCounter = 0;

            mrktDropdown.classList.remove('hidden-text');

            // Sets the categories filter menu based on selected market
            marketCategories.forEach(function (market) {
                if (market.value === selectedMarket) {
                    _setCategoriesFilter(market.categories);
                }
            });

            // Changes the market description
            marketDescriptions.forEach(function (description) {
                if (selectedMarket === description.getAttribute('data-market')) {
                    description.classList.remove('hide');
                } else {
                    description.classList.add('hide');
                }
            });

            // Hides the questions based on selected market
            categoryQuestions.forEach(function (question) {
                var questionMarket = question.getAttribute('data-market');

                if (questionMarket === selectedMarket || questionMarket === 'general') {
                    question.classList.remove('hide');
                } else {
                    question.classList.add('hide');
                }
            });

            // Hides categories if they have no questions
            categories = element.querySelectorAll('.category');
            categories.forEach(function (category) {
                var visibleQuestions = category.querySelectorAll('.question:not(.hide)'),
                    hiddenCount = 0,
                    categoryButton,
                    questionsTextArr = [],
                    questionExists;

                if (visibleQuestions.length > 0) {
                    category.classList.remove('hide');
                    if (visibleCounter === 0) {
                        category.classList.add('first-visible');
                        visibleCounter++;
                    } else {
                        category.classList.remove('first-visible');
                    }
                    visibleQuestions.forEach(function (question) {
                        // bug 108130 compare array of questions text to current question
                        questionExists = false;
                        questionsTextArr.forEach(function (text) {
                            if (question.innerText === text) {
                                questionExists = true;
                            }
                        });
                        if (questionsTextArr.length < 3 && !questionExists) {
                            question.classList.add('landing');
                            questionsTextArr.push(question.innerText);
                        } else if (questionsTextArr.length >= 3 || questionExists) {
                            // bug 90110 adding "general" to the count, add only if selected market
                            if (questionExists) {
                                question.classList.add('duplicate');
                            } else if (marktSelect.value === question.getAttribute('data-market')) {
                                questionsTextArr.push(question.innerText);
                                hiddenCount++;
                            }
                            question.classList.add('hide');
                        }
                    });

                    categoryButton = category.querySelector('button.show-more');

                    if (hiddenCount > 0) {
                        categoryButton.classList.remove('hide');
                        categoryButton.querySelector('span').innerHTML = showMore + ' (' + hiddenCount + ')';
                    } else {
                        categoryButton.classList.add('hide');
                    }
                } else {
                    category.classList.remove('first-visible');
                    category.classList.add('hide');
                }
            });
        }

        // Helper function to display other market EHH Form
        function _displayOtherMarket() {
            _clearCategories();

            if (element.getAttribute('data-question-submitted')) {
                EDC.forms.resetEHHForm(otherMarket);
            }

            catDropdown.classList.add('hide');
            catContainer.classList.add('hide');
            otherMarket.classList.remove('hide');
            mrktDropdown.classList.add('large');
            filterMenuList.classList.add('hide');
            categoriesTitle.classList.add('hide');
            mainTitle.classList.add('hide');
        }

        // Helper function to display markets
        function _displayMarkets() {
            catDropdown.classList.remove('hide');
            catContainer.classList.remove('hide');
            otherMarket.classList.add('hide');
            mrktDropdown.classList.remove('large');
            filterMenuList.classList.remove('hide');
            categoriesTitle.classList.remove('hide');
            mainTitle.classList.remove('hide');
            _clearCategories();
            _changeSelectedMarket();
        }

        // Attach events
        function _attachEvents() {
            var showMoreButton = element.querySelectorAll('button.show-more'),
                showLessButton = element.querySelectorAll('button.show-less'),
                categoryLinks = element.querySelectorAll('.category-btn, button.show-more');

            EDC.utils.attachEvents(clearAllButton, 'click', _clearCategories);
            EDC.utils.attachEvents(categoryLinks, 'click', _trackCategoryLinksClick);

            EDC.utils.attachEvents(marktSelect, 'change', function (e) {
                var market = e.target.value;

                if (market === 'other') {
                    _displayOtherMarket();
                } else {
                    selectedMarket = e.target.value;
                    _displayMarkets();
                }
            });

            EDC.utils.attachEvents(showMoreButton, 'click', function (e) {
                _showHideMoreQuestions(e, 'show-more');
            });

            EDC.utils.attachEvents(showLessButton, 'click', function (e) {
                _showHideMoreQuestions(e, 'show-less');
            });
        }

        // Helper function to get the JSON data
        function _getData() {
            // Calls JSON file from servlet to get the requested questions
            EDC.utils.fetchJSON('GET', actionURL, '', function (data) {
                var ddSelectedValue = element.querySelectorAll('select[name="markets"] option')[element.querySelector('select[name="markets"]').selectedIndex].value,
                    marketValue = element.getAttribute('data-default-market'),
                    marketAtrr = marketValue === ddSelectedValue ? marketValue : ddSelectedValue,
                    cookieItem,
                    marketEvent = new CustomEvent('change', { bubbles: true });

                if (data.markets && data.categories) {

                    if (categoryCookie) {
                        cookieItem = categoryCookie.split(',');
                        marktSelect.querySelectorAll('option').forEach(function (option) {

                            if (option.value === cookieItem[0]) {
                                option.selected = true;
                            }
                        });
                        selectedMarket = cookieItem[0];
                        marktSelect.dispatchEvent(marketEvent);
                    } else {
                        selectedMarket = marketAtrr !== null && marketAtrr !== '' ? marketAtrr : data.markets[0].value;
                    }
                    marketCategories = data.markets;

                    data.categories.forEach(function (category) {
                        _setCategoriesContent(category);
                    });

                    _changeSelectedMarket();
                    _attachEvents();
                }

                if (categoryCookie) {
                    setTimeout(function () {
                        cookieItem = categoryCookie.split(',');
                        if (cookieItem[1] !== null) {
                            element.querySelectorAll('.filter-criteria[name="category-filter"]').forEach(function (category) {
                                if (category.value === cookieItem[1]) {
                                    category.click();
                                }
                            });
                        }
                    }, 750);
                }
            });
        }

        _getData();
    }

    // Public methods
    function init() {
        var ehhFilter = document.querySelectorAll('.c-ehh-filter:not([data-component-state="initialized"])');

        if (ehhFilter) {
            ehhFilter.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', exportHelpHubFilterJS.init);