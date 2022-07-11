var filterCountriesJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private Variables
        var filtersBody = document.querySelector('body'),
            filterTags = element.querySelectorAll('.filters ul'),
            countries = element.querySelectorAll('.country'),
            countryFilters = element.querySelector('.country-filters'),
            filterCheckboxs = element.querySelectorAll('.filter-criteria'),
            countryInitials = element.querySelectorAll('.country-list .country-initial'),
            noMatch = element.querySelector('.no-match'),
            tagFilters = element.querySelector('.filters'),
            sideBar,
            timerId = null,
            timeout = 5,
            filterCountriesModalSize = 0;

        // Tracking purposes
        function _trackEvent(e) { // already clicked
            var el = e.target.closest('.c-filter-countries'),
                anchor = e.target.closest('a'),
                filterRegion = function () {
                    var array = [],
                        checkbox = el.querySelectorAll('li.region input[type=checkbox]:checked'),
                        i;

                    for (i = 0; i < checkbox.length; i++) {
                        array.push(checkbox[i].value);
                    }
                    return array;
                },
                filterRating = function () {
                    var array = [],
                        checkbox = el.querySelectorAll('li.rating input[type=checkbox]:checked'),
                        i;

                    for (i = 0; i < checkbox.length; i++) {
                        array.push(checkbox[i].value);
                    }
                    return array;
                },
                filterPosition = function () {
                    var array = [],
                        checkbox = el.querySelectorAll('li.position input[type=checkbox]:checked'),
                        i;

                    for (i = 0; i < checkbox.length; i++) {
                        array.push(checkbox[i].value);
                    }
                    return array;
                },
                filterFTA = function () {
                    var array = [],
                        checkbox = el.querySelectorAll('li.fta input[type=checkbox]:checked'),
                        i;

                    for (i = 0; i < checkbox.length; i++) {
                        array.push(checkbox[i].value);
                    }
                    return array;
                },
                obj = {
                    eventInfo: {
                        eventComponent: el.dataset.eventComponent,
                        eventType: el.dataset.eventType,
                        eventName: 'link click - country',
                        eventAction: 'main body',
                        eventText: anchor.textContent.toLowerCase(),
                        eventValue: filterRegion().join('; ').split('edc:region/').join(''), // region
                        eventValue2: filterRating().join('; ').split('edc:rating/').join(''), // rating
                        eventValue3: filterPosition().join('; ').split('edc:position/').join(''), // position
                        eventValue4: filterFTA().join('; ').split('edc:other/').join(''), // fta
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: anchor.getAttribute('href').toLowerCase(),
                        engagementType: el.dataset.eventEngagement
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

        // Helper function to show or hide elements
        function _showHideElements(el, elementAction, elementClass) {
            var i;

            if (el) {
                for (i = 0; i < el.length; i++) {
                    switch (elementAction) {
                        case 'add':
                            el[i].classList.add(elementClass);
                            break;

                        case 'remove':
                            el[i].classList.remove(elementClass);
                            break;
                    }
                }
            }
        }

        // Reset All
        function _resetAll(e) {
            var deleteTags = filterTags[0].querySelectorAll('li'),
                i;

            if (e) {
                e.preventDefault();
            }

            _showHideElements(countries, 'remove', 'hide');
            _showHideElements(countryInitials, 'remove', 'hide');

            for (i = 0; i < (deleteTags.length - 1); i++) {
                deleteTags[i].parentNode.removeChild(deleteTags[i]);
            }

            for (i = 0; i < filterCheckboxs.length; i++) {
                filterCheckboxs[i].checked = false;
            }

            tagFilters.classList.add('hide');
            noMatch.classList.add('hide');
        }

        // Funtcion to Stop Scroll on Body
        function _onFiltersScroll() {
            if (countryFilters.scrollTop < 1) {
                countryFilters.scrollTop = 1;
            } else if (countryFilters.scrollHeight - countryFilters.scrollTop - countryFilters.clientHeight < 1) {
                countryFilters.scrollTop--;
            }
        }

        // Show Filters
        function _showFilter(e) {
            e.preventDefault();

            filterCountriesModalSize = window.innerWidth;
            countryFilters.classList.add('show');

            if (sideBar) {
                sideBar.style.display = 'none';
            }

            filtersBody.style.overflow = 'hidden';
            EDC.utils.attachEvents(countryFilters, 'scroll', _onFiltersScroll);
        }

        function _hideFilter(e) {
            e.preventDefault();

            filterCountriesModalSize = 0;
            countryFilters.classList.remove('show');

            if (sideBar) {
                sideBar.style.display = 'block';
            }

            filtersBody.style.overflow = 'initial';
            EDC.utils.unAttachEvents(countryFilters, 'scroll', _onFiltersScroll);
        }

        // Sets tags on of the event filters
        function _setTags(el) {
            var availableTags,
                listItem = document.createElement('li'),
                spanCountry = document.createElement('span'),
                spanIcon = document.createElement('span'),
                action = el.checked,
                deleteTag;

            if (action) {
                listItem.setAttribute('class', 'filter-tag');
                listItem.setAttribute('data-tag-filter', el.value);
                spanCountry.setAttribute('class', 'country-tag');
                spanIcon.setAttribute('class', 'remove-tag');

                spanCountry.appendChild(document.createTextNode(el.dataset.value));
                listItem.appendChild(spanCountry);
                listItem.appendChild(spanIcon);
                filterTags[0].insertBefore(listItem, filterTags[0].lastElementChild);
            } else {
                deleteTag = filterTags[0].querySelector('li[data-tag-filter="' + el.value + '"]');
                deleteTag.parentNode.removeChild(deleteTag);
            }

            availableTags = filterTags[0].querySelectorAll('li');
            if (availableTags.length <= 1) {
                tagFilters.classList.add('hide');
            } else {
                tagFilters.classList.remove('hide');
            }
        }

        // Filters the country navigation items.
        function _filterCountryInitials() {
            var initials = [],
                i,
                j;

            for (i = 0; i < countryInitials.length; i++) {
                for (j = 0; j < countries.length; j++) {
                    if ((countries[j].innerText.charAt(0) === countryInitials[i].dataset.initial) && !(countries[j].classList.contains('hide'))) {
                        initials.push(countryInitials[i].dataset.initial);
                        break;
                    }
                }
            }

            for (i = 0; i < countryInitials.length; i++) {
                if (initials.indexOf(countryInitials[i].dataset.initial) === -1) {
                    countryInitials[i].classList.add('hide');
                } else {
                    countryInitials[i].classList.remove('hide');
                }
            }
        }

        // Display query data
        function _showFilteredData(filteredCountriesByRegion, filteredCountriesByRating, filteredCountriesByPosition, filteredCountriesByFTA) {
            var filtersData = [],
                filteredCountries,
                i = 0,
                filterActivated = false;

            if (filteredCountriesByRegion.length > 0 && filteredCountriesByRating.length > 0 && filteredCountriesByPosition.length > 0 && filteredCountriesByFTA.length > 0) {
                if (filteredCountriesByRegion.length > 0) {
                    filtersData.push(filteredCountriesByRegion);
                }

                if (filteredCountriesByRating.length > 0) {
                    filtersData.push(filteredCountriesByRating);
                }

                if (filteredCountriesByPosition.length > 0) {
                    filtersData.push(filteredCountriesByPosition);
                }

                if (filteredCountriesByFTA.length > 0) {
                    filtersData.push(filteredCountriesByFTA);
                }
            }

            while (i < filterCheckboxs.length && !filterActivated) {
                if (filterCheckboxs[i].checked) {
                    filterActivated = true;
                }
                i++;
            }

            if (filtersData.length > 0 && countries) {
                filteredCountries = filtersData.shift().reduce(function (res, v) {
                    if (res.indexOf(v) === -1 && filtersData.every(function (a) {
                        return a.indexOf(v) !== -1;
                    })) {
                        res.push(v);
                    }
                    return res;
                }, []);


                if (filteredCountries.length === 0) {
                    noMatch.classList.remove('hide');
                } else {
                    noMatch.classList.add('hide');
                }

                _showHideElements(countries, 'add', 'hide');
                _showHideElements(filteredCountries, 'remove', 'hide');

            } else if (filterActivated && countries) {
                _showHideElements(countries, 'add', 'hide');
                noMatch.classList.remove('hide');
            } else {
                _showHideElements(countries, 'remove', 'hide');
            }

            _filterCountryInitials();
        }

        // Helper function to set country filters
        function _setCountryFilter(filterValue, filterDataAttribute, filterArray) {
            var i,
                j;

            if (filterValue.length > 0) {
                for (i = 0; i < filterValue.length; i++) {
                    for (j = 0; j < countries.length; j++) {
                        if ((countries[j].getAttribute(filterDataAttribute) === filterValue[i])) {
                            filterArray.push(countries[j]);
                        }
                    }
                }
            } else {
                for (i = 0; i < countries.length; i++) {
                    filterArray.push(countries[i]);
                }
            }
        }

        // Querys the countries to display
        function _filteredData(region, rating, position, fta) {
            var filteredCountriesByRegion = [],
                filteredCountriesByRating = [],
                filteredCountriesByPosition = [],
                filteredCountriesByFTA = [];

            if (countries && (region.length > 0 || rating.length > 0 || position.length > 0 || fta.length > 0)) {
                _setCountryFilter(region, 'data-region', filteredCountriesByRegion);
                _setCountryFilter(rating, 'data-rating', filteredCountriesByRating);
                _setCountryFilter(position, 'data-position', filteredCountriesByPosition);
                _setCountryFilter(fta, 'data-fta', filteredCountriesByFTA);
            }

            _showFilteredData(filteredCountriesByRegion, filteredCountriesByRating, filteredCountriesByPosition, filteredCountriesByFTA);
        }

        // Helper Function to Filter Data
        function _setFilters(e) {
            var filterRegion = [],
                filterRating = [],
                filterPosition = [],
                filterFTA = [],
                selection = e.target || e,
                i;

            for (i = 0; i < filterCheckboxs.length; i++) {
                if (filterCheckboxs[i].checked === true) {
                    switch (filterCheckboxs[i].getAttribute('data-filter')) {
                        case 'region':
                            filterRegion.push(filterCheckboxs[i].value);
                            break;
                        case 'rating':
                            filterRating.push(filterCheckboxs[i].value);
                            break;
                        case 'position':
                            filterPosition.push(filterCheckboxs[i].value);
                            break;
                        case 'fta':
                            filterFTA.push(filterCheckboxs[i].value);
                            break;
                    }
                }
            }

            _filteredData(filterRegion, filterRating, filterPosition, filterFTA);
            _setTags(selection);
        }

        // Removes tags when close icon is clicked
        function _removeTags(el) {
            var tag,
                i;

            el.preventDefault();

            if (filterCheckboxs && el.target.localName !== ('ul')) {
                if (el.target.classList.contains('remove-tag')) {
                    tag = el.target.closest('li').dataset.tagFilter;
                    for (i = 0; i < filterCheckboxs.length; i++) {
                        if (filterCheckboxs[i].value === tag) {
                            filterCheckboxs[i].checked = false;
                            _setFilters(filterCheckboxs[i]);
                        }
                    }
                }

                if (el.target.closest('span') && el.target.closest('span').classList.contains('clear-tag')) {
                    _resetAll();
                }
            }
        }

        // Sets visibility of the Country filters on load
        function _showHideCountryFilter() {
            if (window.innerWidth >= EDC.props.media.md) {
                countryFilters.classList.add('show');
            } else if (window.innerWidth !== filterCountriesModalSize) {
                countryFilters.classList.remove('show');
            }
        }

        // Attach events
        function _attachEvents() {
            var filters = element.querySelectorAll('.country-filters input'),
                countryName = element.querySelectorAll('.country-name a'),
                filterBtn = element.querySelectorAll('.filter-data .show-filters'),
                closeBtn = element.querySelectorAll('.close-filters .close-button'),
                clearBtn = element.querySelectorAll('.close-filters .clear-text');

            EDC.utils.attachEvents(filters, 'change', _setFilters);
            EDC.utils.attachEvents(countryName, 'click', _trackEvent);
            EDC.utils.attachEvents(filterBtn, 'click', _showFilter);
            EDC.utils.attachEvents(closeBtn, 'click', _hideFilter);
            EDC.utils.attachEvents(clearBtn, 'click', _resetAll);

            EDC.utils.attachEvents(filterTags, 'click', _removeTags);

            EDC.utils.attachEvents(window, 'resize', _showHideCountryFilter);
        }

        _showHideCountryFilter();
        _sideBarInit();
        _attachEvents();
    }

    // Public methods
    function init() {
        var filterCountries = document.querySelectorAll('.c-filter-countries:not([data-component-state="initialized"])');

        if (filterCountries) {
            filterCountries.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', filterCountriesJS.init);