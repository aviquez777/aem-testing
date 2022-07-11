var countryGridJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var tabContainer = element.querySelector('.tab-container'),
            tabLabelsContainer = tabContainer.querySelector('.tab-labels-container'),
            tabLabels = tabLabelsContainer.querySelector('.tab-labels'),
            arrowRight = tabContainer.querySelector('.icon-arrow-right'),
            arrowLeft = tabContainer.querySelector('.icon-arrow-left'),
            serviceURL = element.getAttribute('data-service-url'),
            serverAction = 'POST';

        function _getMEAStatus() {
            var params = 'name=accessGranted&value=MEA-*&operation=isInSessionList';

            EDC.forms.submitFormData(serverAction, serviceURL, params, function (data) {
                var sneakPeekForm = document.querySelector('.c-progressive-profiling .sneak-peek-container'),
                    meaForm = document.querySelector('.c-progressive-profiling form[data-mode="mode_mea"]'),
                    countryGridCards = element.querySelectorAll('.country-grid-item .link-country'),
                    gridTitle = element.querySelector('.grid-titles .title');

                if (data && data.response) {
                    EDC.forms.ppUnlockFeatures(data.response, sneakPeekForm, meaForm, countryGridCards, gridTitle, true);
                }
            });
        }

        function _checkTabsScroll(e) {
            var container = e.currentTarget,
                scrollDiff = container.scrollWidth - container.clientWidth;

            e.preventDefault();

            if (container.scrollLeft >= scrollDiff) {
                container.classList.remove('fade-shadow-right');
            } else if (container.scrollLeft === 0) {
                container.classList.remove('fade-shadow-left');
            } else {
                container.classList.add('fade-shadow-right');
                container.classList.add('fade-shadow-left');
            }
        }

        function _verifyScrollPos(elem) {
            var scrollDiff = tabLabels.scrollWidth - tabLabels.scrollLeft - elem.clientWidth;

            if (scrollDiff < elem.clientWidth && scrollDiff !== 0) {
                elem.classList.remove('fade-shadow-right');
                elem.classList.add('fade-shadow-left');
                arrowRight.classList.remove('show');
                arrowLeft.classList.add('show');
            } else if (tabLabels.scrollLeft < elem.clientWidth && tabLabels.scrollLeft !== 0) {
                elem.classList.remove('fade-shadow-left');
                elem.classList.add('fade-shadow-right');
                arrowLeft.classList.remove('show');
                arrowRight.classList.add('show');
            } else {
                elem.classList.add('fade-shadow-right');
                elem.classList.add('fade-shadow-left');
                arrowRight.classList.add('show');
                arrowLeft.classList.add('show');
            }
        }

        function _resizeWindow() {
            setTimeout(function () {
                if (tabLabelsContainer.clientWidth < tabLabels.scrollWidth) {
                    tabLabelsContainer.classList.add('fade-shadow-right');
                    arrowRight.classList.add('show');
                } else {
                    tabLabelsContainer.classList.remove('fade-shadow-right');
                    arrowRight.classList.remove('show');
                    arrowLeft.classList.remove('show');
                }
            }, 10);
        }

        function _scrollToHorizontal(elem, to, duration) {
            var difference,
                perTick;

            if (duration > 0) {
                difference = to - elem.scrollLeft;
                perTick = difference / duration * 10;

                setTimeout(function () {
                    elem.scrollLeft += perTick;

                    if (!(elem.scrollLeft >= to)) {
                        _scrollToHorizontal(elem, to, duration - 10);
                    }
                }, 10);
            }
        }

        function _scrollToHorizontalleft(elem, to, duration, difference) {
            var differencestep,
                perTick;

            if (duration > 0) {
                differencestep = difference;
                perTick = differencestep / duration * 10;

                setTimeout(function () {
                    elem.scrollLeft -= perTick;

                    if (!(elem.scrollLeft <= to)) {
                        _scrollToHorizontalleft(elem, to, duration - 10, difference);
                    }
                }, 10);
            }
        }

        function _scrollContentRight() {
            var scrollMove = tabLabelsContainer.clientWidth;

            _scrollToHorizontal(tabLabels, tabLabels.scrollLeft + scrollMove, 1000);
            _verifyScrollPos(tabLabelsContainer);
        }

        function _scrollContentleft() {
            var scrollMove = tabLabelsContainer.clientWidth;

            _scrollToHorizontalleft(tabLabels, tabLabels.scrollLeft - scrollMove, 1000, scrollMove);
            _verifyScrollPos(tabLabelsContainer);
        }

        function _attachEvents() {
            EDC.utils.attachEvents(tabLabelsContainer, 'scroll', _checkTabsScroll);
            EDC.utils.attachEvents(arrowRight, 'click', _scrollContentRight);
            EDC.utils.attachEvents(arrowLeft, 'click', _scrollContentleft);
            EDC.utils.attachEvents(window, 'resize', _resizeWindow);
        }

        _attachEvents();
        _resizeWindow();
        _getMEAStatus();
    }

    // Public methods
    function init() {
        var countryGrid = document.querySelectorAll('.country-grid:not([data-component-state="initialized"])');

        if (countryGrid) {
            countryGrid.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', countryGridJS.init);