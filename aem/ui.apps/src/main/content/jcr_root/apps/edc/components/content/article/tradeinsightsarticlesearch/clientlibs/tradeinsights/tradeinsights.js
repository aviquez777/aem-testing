var tradeInsightsJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var filtersBody = document.querySelector('body'),
            filterContainer = element.querySelector('.trade-insights-filters'),
            filterCategories = filterContainer.querySelectorAll('.trade-insights-filter'),
            articlesList = element.querySelector('.article-list'),
            filterTitle = element.querySelector('.filter-title'),
            closeBtn = element.querySelector('.close-button'),
            applyAll = element.querySelector('.apply-filter'),
            filterTags = element.querySelector('.filter-tags'),
            filterTagsList = filterTags ? filterTags.querySelector('ul') : '',
            noMatch = element.querySelector('.no-match'),
            initialArticles = element.querySelectorAll('.trade-insight-article'),
            categoryFilter = element.querySelector('.trade-insights-filter.category'),
            trendingFilter = element.querySelector('.trade-insights-filter.trending-topic'),
            industryFilter = element.querySelector('.trade-insights-filter.industry'),
            regionFilter = element.querySelector('.trade-insights-filter.region'),
            formatFilter = element.querySelector('.trade-insights-filter.format'),
            openLink = element.querySelector('.trade-insights-info').getAttribute('data-open-link'),
            keepScrolling = true,
            pageNum = 1,
            modalSize = 0,
            timerId = null,
            timeout = 5,
            selectedFilters = [],
            lastKnownScrollPosition,
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
            var target = e ? e.target : '',
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent.toLowerCase(),
                        eventType: element.dataset.eventType.toLowerCase(),
                        eventName: element.dataset.eventName.toLowerCase(),
                        eventAction: element.dataset.eventAction,
                        eventText: target ? target.closest('.trade-insight-article').getAttribute('data-page-name').toLowerCase() : noMatch.getAttribute('data-no-results'),
                        eventValue: _getFilterValues(categoryFilter),
                        eventValue2: _getFilterValues(trendingFilter),
                        eventValue3: _getFilterValues(industryFilter),
                        eventValue4: _getFilterValues(regionFilter),
                        eventValue5: _getFilterValues(formatFilter),
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: target ? target.getAttribute('href') : '',
                        engagementType: target ? element.dataset.eventEngagement : '',
                        eventLevel: element.dataset.eventEngagement
                    }
                };

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

        // Helper function to set article border
        function _setBorders(articles) {
            articles.forEach(function (article, index) {
                var nextArticle = articles[index + 1];

                // Only adds borders if the articles is not premium and if the next article is not premium
                if (nextArticle !== undefined && !nextArticle.classList.contains('premium') && !article.classList.contains('premium')) {
                    article.classList.add('has-border');
                } else if (nextArticle === undefined) {
                    article.classList.add('has-border');
                }
            });
        }

        // Helper function to render the given articles
        function _renderArticles(articles) {
            var articlePage,
                pageArticles,
                previousPage = element.querySelector('.article-page[data-page="' + (pageNum - 1) + '"]');

            if (articles.length) {
                articlePage = document.createElement('ul');
                articlePage.classList.add('article-page');
                articlePage.setAttribute('data-page', pageNum);

                articles.forEach(function (article) {
                    var card = document.createElement('li'),
                        imgSrc;

                    card.classList.add('trade-insight-article');
                    card.setAttribute('data-page-name', article.pagename);

                    if (article.ispremium) {
                        card.classList.add('premium');
                        imgSrc = article.tabletFileReference;
                    } else {
                        imgSrc = article.teaserimage;
                    }

                    card.innerHTML = '<div class="metainfo"><span class="category-tag ' +
                    article.contenttypeclassname + ' inline">' + article.contenttypetitletext + '</span><span class="date">' +
                    article.displaydate + '</span></div><a href="' +
                    article.url + '" target="' + openLink + '" class="card-img-anchor"><div class="card-img ' +
                    article.imagealignment + '"><img src="' + imgSrc + '" alt="' +
                    article.imagealttext + '"></div></a><div class="description"><h3><a class="title" href="' +
                    article.url + '" target="' + openLink + '">' + article.pageTitle + '</a></h3><p>' + article.articlesynopsis + '</p></div>';

                    EDC.utils.attachEvents(card.querySelectorAll('.card-img-anchor, .title'), 'click', _trackEvent);

                    articlePage.appendChild(card);
                });

                pageArticles = articlePage.querySelectorAll('.trade-insight-article');

                // Checks if the last child of the previous page contains a border to remove it
                if (articlePage.firstChild.classList.contains('premium') && previousPage) {
                    previousPage.lastElementChild.classList.remove('has-border');
                }

                _setBorders(pageArticles);
                articlesList.appendChild(articlePage);
            } else {
                keepScrolling = false;
            }
        }

        // Helper function to load articles
        function _loadArticles() {
            // If Tags is empy it will bring all articles
            EDC.components.TradeInsights.getFilteredData(pageNum, selectedFilters, function (articles) {
                if (articles.length) {
                    if (!noMatch.classList.contains('hide')) {
                        noMatch.classList.add('hide');
                    }

                    articlesList.classList.remove('hide');
                    _renderArticles(articles);
                    pageNum++;
                    keepScrolling = true;
                } else if (pageNum === 0) {
                    noMatch.classList.remove('hide');
                    articlesList.classList.add('hide');
                    _trackEvent();
                }
            });
        }

        // Loads the next page of articles based on the query
        function _loadNextPage() {
            // Checks if the scroll position is near the bottom of the filter
            if (articlesList.clientHeight - 300 < lastKnownScrollPosition && EDC.components.TradeInsights) {
                // Disables the infinte scroll if until the articles are rendered
                keepScrolling = false;
                _loadArticles();
            }
        }

        // Helper function to load first page (not filtered)
        function _loadFirstPage() {
            // Removes all childs of the List to be replaced with new ones
            while (articlesList.firstChild) {
                articlesList.removeChild(articlesList.firstChild);
            }

            if (EDC.components.TradeInsights) {
                pageNum = 0;
                _loadArticles();
            }
        }

        // Helper function to check when the scroll bar is close to the end of the page and load the next set of articles
        function _infiniteScroll() {
            // Checkes the scrollbar position
            lastKnownScrollPosition = window.pageYOffset;

            // Loads the next page only if the previous page was rendered correctly
            if (keepScrolling) {
                window.requestAnimationFrame(function () {
                    _loadNextPage();
                });
            }
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
                filterSection = target.closest('.trade-insights-filter'),
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

        // Shows / hides more options if available
        function _showMoreOptions(e) {
            var target = e.target.classList.contains('show-more') ? e.target : e.target.parentElement,
                targetSpan = target ? target.querySelector('span') : '',
                filterSection = e.target.closest('.trade-insights-filter'),
                hiddenOptions = filterSection ? filterSection.querySelectorAll('.filter.hide') : '',
                filterOptions;

            if (hiddenOptions.length) {
                hiddenOptions.forEach(function (option) {
                    option.classList.remove('hide');
                });

                if (targetSpan) {
                    targetSpan.innerText = target.getAttribute('data-less');
                }

            } else {
                filterOptions = filterSection ? (filterSection.querySelectorAll('.filter')) : '';
                filterOptions.forEach(function (option, index) {
                    if (index > 2) {
                        option.classList.add('hide');
                    }
                });

                if (targetSpan) {
                    targetSpan.innerText = target.getAttribute('data-more');
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

            // Gets the tag value and removes it from the filter.
            if (tagValue && !isClearAllDevice) {
                selectedInput = element.querySelector('.filter-option[value="' + tagValue + '"]');

                if (selectedInput) {
                    EDC.utils.simulateClick(selectedInput);
                }
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

                // Loads initial content
                selectedFilters = [];
                _loadFirstPage();
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
            } else {
                deleteTag = filterTagsList.querySelector('.filter-tag[data-tag-filter="' + el.value + '"]');
                deleteTag.parentNode.removeChild(deleteTag);
            }

            _checkFilterTags();
        }

        // Helper function to get the selected tags
        function _getselectedFilters() {
            selectedFilters = [];

            // Iterates on each filter filter to get the selected values
            filterCategories.forEach(function (filter) {
                var checkedFilters = [],
                    filterList = filter.querySelector('.filters'),
                    selectedItems = filterList ? filterList.querySelectorAll('.filter > .filter-option:checked') : '';

                // For each filter checks if the filter is selected and if it contains subcategories
                selectedItems.forEach(function (item) {
                    var subFilters = item.parentElement.querySelectorAll('.subfilter .filter-option:checked');

                    // If the filter contains subcategories gets the selected values, if not it will get the value of the main category
                    if (subFilters.length) {
                        subFilters.forEach(function (subFilter) {
                            checkedFilters.push(subFilter.value);
                        });
                    } else {
                        checkedFilters.push(item.value);
                    }
                });

                if (checkedFilters.length) {
                    selectedFilters.push(checkedFilters);
                }
            });
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

            // Enables infinite scroll if it was disabled
            keepScrolling = true;
            _loadFirstPage();
        }

        // Attach events
        function _attachEvents() {
            var filterBtn = element.querySelector('.show-filters'),
                chevronBtn = element.querySelectorAll('.filter-title .chevron'),
                showMoreBtn = element.querySelectorAll('.show-more'),
                clearAll = element.querySelectorAll('.clear-filter, .clear-tag'),
                filterInputs = element.querySelectorAll('.filter-option'),
                articleLinks = element.querySelectorAll('.trade-insight-article .title, .trade-insight-article .card-img-anchor');


            EDC.utils.attachEvents(filterInputs, 'change', _setFilters);
            EDC.utils.attachEvents(articleLinks, 'click', _trackEvent);
            EDC.utils.attachEvents(filterBtn, 'click', _showFilter);
            EDC.utils.attachEvents(chevronBtn, 'click', _expandCollapseFilter);
            EDC.utils.attachEvents(showMoreBtn, 'click', _showMoreOptions);
            EDC.utils.attachEvents(closeBtn, 'click', _hideFilter);
            EDC.utils.attachEvents(applyAll, 'click', _hideFilter);
            EDC.utils.attachEvents(clearAll, 'click', _removeSelections);
            EDC.utils.attachEvents(window, 'scroll', _infiniteScroll);

            EDC.utils.attachEvents(window, 'resize', function () {
                if (isDevice !== _isDevice()) {
                    isDevice = _isDevice();
                    _showHideFilter();
                }
            });
        }

        EDC.components.TradeInsights.initialize();
        isDevice = _isDevice();
        _showHideFilter();
        _sideBarInit();
        _setBorders(initialArticles);
        _attachEvents();
    }

    // Public methods
    function init() {
        var tradeInsights = document.querySelectorAll('.c-trade-insights-filter:not([data-component-state="initialized"])');

        if (tradeInsights) {
            tradeInsights.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', tradeInsightsJS.init);