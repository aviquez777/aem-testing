var modalSearchJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            triggerersInit = d.querySelectorAll('button.trigger-modal-search'),
            triggerers = triggerersInit ? Array.from(triggerersInit) : null,
            modalCloseBtn = d.querySelector('button.modal-close'),
            inputContainer = element.querySelector('.quick-search'),
            inputSearch = inputContainer.querySelector('.cmp-search__input'),
            inputErrorMessage = inputContainer.querySelector('[data-cmp-hook-search="noResults"]'),
            icon = inputContainer.querySelector('.cmp-search__icon'),
            accessibleElements = [];

        function _trackResult(e, numofResults) {
            var el = e.currentTarget,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventAction: element.dataset.eventAction,
                        eventText: el.querySelector('span').textContent,
                        searchQuery: inputSearch.value,
                        destinationPage: el.getAttribute('href'),
                        engagementType: element.dataset.engagementType,
                        eventLevel: element.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName(),
                        eventType: element.dataset.eventType,
                        eventName: element.dataset.eventNameResultClick
                    }
                };

            if (numofResults > 0) {
                obj.eventInfo.numofResults = numofResults;
            } else if (inputErrorMessage) {
                obj.eventInfo.numofResults = 0;
                inputErrorMessage.classList.remove('hide');
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _trackSearchBar() {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventAction: element.dataset.eventAction,
                    eventText: '',
                    engagementType: element.dataset.engagementType,
                    eventLevel: element.dataset.eventLevel,
                    eventPageName: EDC.utils.getPageName(),
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventName
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _adjustModal() {
            var allElements = element.querySelectorAll('input, button, a.cmp-search__item');

            accessibleElements = [];

            allElements.forEach(function (el) {
                if (el.offsetWidth > 0 && el.offsetHeight > 0) {
                    accessibleElements.push(el);
                }
            });
        }

        function _isMenuOpen() {
            return EDC.utils.elContainsCl(element, 'show');
        }

        function _refreshInput() {
            inputErrorMessage.classList.add('hide');

            setTimeout(function () {
                _adjustModal();
            }, 1000);
        }

        // Attach events
        function _attachEvents() {
            // Handle the input
            EDC.utils.attachEvents(inputSearch, 'input', _refreshInput);
            EDC.utils.attachEvents(inputSearch, 'focus', _refreshInput);
            EDC.utils.attachEvents(window, 'resize', _adjustModal);
            EDC.utils.attachEvents(element, 'click', _trackSearchBar);

            EDC.utils.attachEvents(window, 'keydown', function (e) {
                EDC.utils.preventingKeydownForAccessibility(e, _isMenuOpen());
            });

            EDC.utils.attachEvents(modalCloseBtn, 'keydown', function (e) {
                var keyCode = e.keyCode || e.which;

                if (keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode) {
                    element.classList.remove('show');
                    d.querySelector('body').classList.remove('modal-is-open');

                    triggerers.some(function (triggerer) {
                        var trg = triggerer.offsetWidth > 0 && triggerer.offsetHeight > 0;

                        if (trg) {
                            triggerer.focus();
                        }

                        return trg;
                    });
                }
            });

            EDC.utils.attachEvents(element, 'keydown', function (e) {
                EDC.utils.enableFocusTrap(e, accessibleElements, triggerers);
            });
        }

        function _mutations() {
            var observerSearch,
                numofResults = 0,
                results;

            // Observe the attributes changes
            observerSearch = new MutationObserver(function (mutations) {
                mutations.forEach(function (mutation) {
                    if (mutation.attributeName === 'aria-hidden' && mutation.target.getAttribute('aria-hidden') === 'false') {
                        results = inputContainer.querySelectorAll('.cmp-search__results .cmp-search__item');
                        results.forEach(function (data) {
                            var url = data.href;

                            EDC.utils.attachEvents(data, 'click', function (e) {
                                _trackResult(e, numofResults);
                            });

                            if (url) {
                                data.href = url.replace('/content/edc', '');
                            }
                        });
                        numofResults = results.length;

                        if (numofResults === 0 && inputErrorMessage) {
                            inputErrorMessage.classList.remove('hide');
                        }
                    }
                });
            });

            observerSearch.observe(icon, {
                attributes: true // configure it to listen to attribute changes
            });
        }

        function _pubSubs() {
            PubSub.subscribe('search-modal', function (data) {
                if (data) {
                    _adjustModal();
                }
            });
        }

        EDC.utils.addModalSearchListeners();
        _mutations();
        _attachEvents();
        _pubSubs();
    }

    // Public methods
    function init() {
        var modals = document.querySelectorAll('.c-modal-search:not([data-component-state="initialized"])');

        if (modals) {
            modals.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', modalSearchJS.init);