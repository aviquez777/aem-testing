var handleTabSetNavigation = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var hashValue = EDC.utils.getHashValue(),
            windowWidth = window.innerWidth,
            tabSetNav = element.querySelector('.tabs'),
            tabLabelsContainer = tabSetNav.querySelector('.tab-labels-container'),
            tabLabels = tabLabelsContainer.querySelector('.tab-labels'),
            tabLabelsItems = tabLabels.querySelectorAll('.tab-label'),
            arrowRight = tabSetNav.querySelector('.icon-arrow-right'),
            arrowLeft = tabSetNav.querySelector('.icon-arrow-left'),
            touchTimer,
            anchorHref,
            windowOffset = window.pageYOffset;

        // Data Layer
        function _trackEvent(el) {
            var tab = el.target.closest('.tab-set'),
                isExportJourney = tab.classList.contains('c-export-journey'),
                isExportJourneyVariant = tab.querySelector('.style-variant'),
                titleExportJourneyVariant = tab.querySelector('.tab-panel.show .style-variant h3'),
                obj = {
                    eventInfo: {
                        eventComponent: tab.dataset.eventComponent,
                        eventType: isExportJourney ? tab.dataset.eventType : tab.dataset.eventComponent + ' ' + tab.dataset.eventType,
                        eventName: isExportJourney ? tab.dataset.eventType + ' ' + tab.dataset.eventName + ' - ' + el.target.textContent.toLowerCase() : tab.dataset.eventComponent + ' ' + el.type,
                        eventAction: tab.dataset.eventAction,
                        eventText: el.target.textContent.toLowerCase(),
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: '',
                        engagementType: tab.dataset.eventEngagement
                    }
                };

            if (isExportJourney) {
                obj.eventInfo.eventPageName = EDC.utils.getPageName();
                obj.eventInfo.eventLevel = tab.dataset.eventLevel;
                obj.eventInfo.eventStage = '';
                if (isExportJourneyVariant) {
                    obj.eventInfo.eventType = tab.dataset.eventTypeVariant;
                    obj.eventInfo.eventName = (tab.dataset.eventTypeVariant + ' - ' + tab.dataset.eventType).toLowerCase();
                    obj.eventInfo.eventText = obj.eventInfo.eventName.indexOf('button') > -1 ?
                        element.querySelector('.tab-panel.show .content-button').textContent.toLowerCase().trim() : el.target.textContent.toLowerCase().trim();
                    if (titleExportJourneyVariant) {
                        obj.eventInfo.eventValue = obj.eventInfo.eventName.indexOf('button') > -1 ?
                            titleExportJourneyVariant.innerHTML.substring(0, titleExportJourneyVariant.innerHTML.indexOf('<span')).toLowerCase().trim() :
                            element.querySelector('.tab-labels .active').textContent.toLowerCase().trim();
                    } else {
                        obj.eventInfo.eventValue = element.querySelector('.tab-labels .active').textContent.toLowerCase().trim();
                    }
                    obj.eventInfo.eventLevel = tab.dataset.eventLevelVariant;
                }
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _handleClicks(e) {
            var tabLabelSelected = element.querySelector('.tab-label.selected'),
                tabPanelShow = element.querySelector('.tab-panel.show'),
                buttonClicked = e.target.classList.contains('triggered'),
                isExportJourney = element.classList.contains('c-export-journey'),
                tabLabel = (e.target).closest('.tab-label');

            if (isExportJourney) {
                element.setAttribute('data-event-type', buttonClicked ? element.querySelector('.style-variant') ? 'button click' : 'button' :
                    element.querySelector('.style-variant') ? 'timeline click' : 'export stage');
            }
            e.preventDefault();
            _trackEvent(e);

            tabLabelSelected.setAttribute('aria-selected', 'false');
            tabLabelSelected.classList.toggle('selected');
            tabLabel.setAttribute('aria-selected', 'true');
            tabLabel.classList.toggle('selected');

            if (tabLabel.offsetLeft < 50) {
                tabLabels.scrollLeft = 0;
            }

            setTimeout(function () {
                EDC.utils.verifyScrollPos(tabLabelsContainer);
            }, 500);

            if (tabPanelShow) {
                tabPanelShow.classList.toggle('show');
            }

            document.getElementById(tabLabel.getAttribute('aria-controls')).classList.toggle('show');
        }

        function _resizeWindow(loading) {
            var tabLabelsTotalWidth = 0;

            if (windowWidth !== window.innerWidth || loading) {
                if (window.innerWidth < EDC.props.media.md && !element.classList.contains('c-export-journey')) {
                    tabLabelsItems.forEach(function (tabLabel) {
                        tabLabelsTotalWidth += tabLabel.clientWidth;
                    });

                    element.querySelector('.tab-labels').style.width = tabLabelsTotalWidth + 'px';
                } else {
                    element.querySelector('.tab-labels').style.width = 'auto';
                }

                EDC.utils.verifyScrollPos(tabLabelsContainer);
                windowWidth = window.innerWidth;
            }
        }

        // These 2 functions resolve the tap and scroll bug: the user is now able to scroll by tapping over an anchor.
        // _anchorTouchStart & _anchorTouchEnd
        function _anchorTouchStart(e) {
            var anchor = e.target.closest('a');

            anchorHref = anchor.getAttribute('href');
            touchTimer = setInterval(function () {
                if (windowOffset !== window.pageYOffset) {
                    if (anchor.getAttribute('href')) {
                        anchor.removeAttribute('href');
                    }
                }
            }, 50);
        }

        function _anchorTouchEnd(e) {
            var anchor = e.target.closest('a');

            clearInterval(touchTimer);
            if (!anchor.hasAttribute('href')) {
                setTimeout(function () {
                    anchor.setAttribute('href', anchorHref);
                }, 100);
            }
            windowOffset = window.pageYOffset;
        }

        function _attachEvents() {
            var tabItems = element.querySelectorAll('.tab-panel a');

            EDC.utils.attachEvents(tabItems, 'touchstart', _anchorTouchStart);
            EDC.utils.attachEvents(tabItems, 'touchend', _anchorTouchEnd);

            EDC.utils.attachEvents(tabLabelsItems, 'click', _handleClicks);
            EDC.utils.attachEvents(window, 'resize', _resizeWindow);
            EDC.utils.attachEvents(tabLabelsContainer, 'scroll', EDC.utils.checkTabsScroll);
            EDC.utils.attachEvents(arrowRight, 'click', function () {
                EDC.utils.scrollTabs(element, 'right', element.classList.contains('c-export-journey'));
            });
            EDC.utils.attachEvents(arrowLeft, 'click', function () {
                EDC.utils.scrollTabs(element, 'left', element.classList.contains('c-export-journey'));
            });
        }

        _attachEvents();

        setTimeout(function () {
            _resizeWindow(true);
            EDC.utils.initializeSwipe(element);
        }, 1000);

        tabLabelsItems.forEach(function (tabLabel) {
            if (hashValue !== '' && hashValue === tabLabel.getAttribute('aria-controls')) {
                tabLabel.dispatchEvent('click');
            }
        });
    }

    // Public methods
    function init() {
        var tabSets = document.querySelectorAll('.tab-set:not([data-component-state-tabset="initialized"])');

        if (tabSets) {
            tabSets.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state-tabset', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', handleTabSetNavigation.init);