var blueBackgroundTabsJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var tablistButtons = element.querySelector('.tablist-buttons'),
            tabButtons = element.querySelectorAll('.tab-button'),
            tabButtonsLength = tabButtons ? tabButtons.length : '',
            tablistDescription = element.querySelector('.tablist-description'),
            tabDescriptions = element.querySelectorAll('.tab-description'),
            tabPanels = element.querySelectorAll('.tab-panel'),
            backBtn = element.querySelector('.cta-tab.back'),
            nextBtn = element.querySelector('.cta-tab.next'),
            isMobile,
            delimiter = '_';

        // Private functions

        // Data Layer
        function _trackEvent(tabText) {
            var eventComponent = element.dataset.eventComponent.toLowerCase(),
                obj = {
                    eventInfo: {
                        eventComponent: eventComponent,
                        eventType: eventComponent + ' ' + element.dataset.eventType.toLowerCase(),
                        eventName: eventComponent + ' ' + element.dataset.eventName.toLowerCase(),
                        eventAction: element.dataset.eventAction,
                        eventText: tabText.toLowerCase(),
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: '',
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: ''
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        // Checks if screen size is mobile
        function _isMobile() {
            return window.innerWidth < EDC.props.media.md;
        }

        function _toggleClass(elem, selectedClass) {
            elem.classList.remove('end');
            elem.classList.remove('middle');
            elem.classList.remove('start');

            if (selectedClass) {
                elem.classList.add(selectedClass);
            }
        }

        // Helper function to determine visible button
        function _getSelectedTab(targetId) {
            var targetIndex = 0;

            tabButtons.forEach(function (button, index) {
                if (button.getAttribute('id') === targetId) {
                    targetIndex = index;
                }
            });

            return targetIndex;
        }

        // Helper function to display content on swipe
        function _moveToContent(nextIndex) {
            tabButtons.forEach(function (button, index) {
                if (index === nextIndex) {
                    if (_isMobile() && index > 0 && index < tabButtonsLength - 1) {
                        button.classList.add('middle');
                    } else {
                        button.classList.remove('middle');
                    }

                    EDC.utils.simulateClick(button);
                }
            });
        }

        // Helper function to determine direccion of the tab siwtch
        function _setNextTabDirection(direction, index) {
            var nextIndex = index;

            switch (direction) {
                case 'left':
                    // Handle Swipe Left
                    if (index + 1 <= tabButtonsLength - 1) {
                        nextIndex = index + 1;
                        _moveToContent(nextIndex);
                    }
                    break;
                case 'right':
                    // Handle Swipe Right
                    if (index - 1 >= 0) {
                        nextIndex = index - 1;
                        _moveToContent(nextIndex);
                    }
                    break;
                default:
                    break;
            }

            return nextIndex;
        }

        // Helper function to display next and back button on Mobile
        function _showNavButtons(index) {
            var currentIndex = index + 1;

            if (currentIndex < tabButtonsLength && currentIndex > 1) {
                if (_isMobile()) {
                    _toggleClass(tablistDescription, 'middle');
                }

                nextBtn.setAttribute('aria-hidden', false);
                backBtn.setAttribute('aria-hidden', false);
            }

            // Hides next button and hides back button
            if (currentIndex === tabButtonsLength) {
                if (_isMobile()) {
                    _toggleClass(tablistButtons, 'end');
                    _toggleClass(tablistDescription, 'end');
                }

                nextBtn.setAttribute('aria-hidden', true);

                // BUG 223730 Handle special case when there are 2 tabs
                if (tabButtonsLength === 2) {
                    backBtn.setAttribute('aria-hidden', false);
                }
            }

            // Hides back button and shows next
            if (currentIndex === 1) {
                if (_isMobile()) {
                    _toggleClass(tablistButtons, 'start');
                    _toggleClass(tablistDescription, 'start');
                }

                nextBtn.setAttribute('aria-hidden', false);
                backBtn.setAttribute('aria-hidden', true);
            }
        }

        // Helper function to enable swype on tab description
        function _initializeSwipe() {
            tabDescriptions.forEach(function (description) {
                new window.EDC.utils.Swipe(description, function (e, direction) {
                    var target = e.target.classList.contains('.tab-description') ? e.target : e.target.closest('.tab-description'),
                        targetId = target ? target.getAttribute('data-tab') : '',
                        swipeIndex = 0,
                        nextIndex = 0;

                    e.preventDefault();

                    swipeIndex = _getSelectedTab(targetId);
                    nextIndex = _setNextTabDirection(direction, swipeIndex);
                    _showNavButtons(nextIndex);
                });
            });
        }

        function _showNextTab(e) {
            var target = e.target.classList.contains('cta-tab') ? e.target : e.target.parentElement,
                action = target.getAttribute('data-action'),
                selectedButton = element.querySelector('.tab-button[aria-selected="true"]'),
                buttonId = selectedButton ? selectedButton.getAttribute('id') : '',
                buttonIndex = 0,
                nextIndex = 0;

            buttonIndex = _getSelectedTab(buttonId);
            nextIndex = _setNextTabDirection(action, buttonIndex);

            _showNavButtons(nextIndex);

        }

        // Helper function to show selected tab content
        function _showSelectedTab(e) {
            var selectedButton = e.target.classList.contains('tab-button') ? e.target : e.target.parentElement,
                buttonText = selectedButton ? selectedButton.getAttribute('data-text') : '',
                selectedId = selectedButton ? selectedButton.getAttribute('id') : '',
                selectedPanel = selectedButton ? selectedButton.getAttribute('aria-controls') : '',
                tabDescription = selectedId ? element.querySelector('.tab-description[data-tab="' + selectedId + '"]') : '',
                tabPanel = selectedPanel ? element.querySelector('.tab-panel[id="' + selectedPanel + '"]') : '';

            tabButtons.forEach(function (button) {
                button.setAttribute('aria-selected', false);
            });

            tabDescriptions.forEach(function (description) {
                description.setAttribute('aria-hidden', true);
            });

            tabPanels.forEach(function (panel) {
                panel.setAttribute('aria-hidden', true);
            });

            if (selectedButton) {
                selectedButton.setAttribute('aria-selected', true);
            }

            if (tabDescription) {
                tabDescription.setAttribute('aria-hidden', false);
            }

            if (tabPanel) {
                tabPanel.setAttribute('aria-hidden', false);
            }

            EDC.utils.setHashTag(element.getAttribute('data-component-target'), selectedButton.getAttribute('data-link-target'), null, delimiter);

            _trackEvent(buttonText);
        }

        // Helper function to reset tabs when resize
        function _setTabs() {
            var selectedButton,
                selectedButtonId,
                selectedButtonIndex;

            if (_isMobile()) {
                tabButtons.forEach(function (button) {
                    var btnSpan = button.querySelector('span');

                    if (button.getAttribute('data-mobile-text') && button.getAttribute('data-mobile-text') !== '') {
                        btnSpan.innerText = button.getAttribute('data-mobile-text');
                    } else if (button.getAttribute('data-text')) {
                        btnSpan.innerText = button.getAttribute('data-text');
                    }
                });

                selectedButton = element.querySelector('.tab-button[aria-selected="true"]');
                selectedButtonId = selectedButton ? selectedButton.getAttribute('id') : '';

                selectedButtonIndex = selectedButtonId ? _getSelectedTab(selectedButtonId) : '';

                // If selected tab is on the middle
                if (selectedButtonIndex > 0 && selectedButtonIndex < (tabButtonsLength - 1)) {
                    nextBtn.setAttribute('aria-hidden', false);
                    backBtn.setAttribute('aria-hidden', false);

                    _toggleClass(tablistDescription, 'middle');
                    _toggleClass(selectedButton, 'middle');
                } else if (selectedButtonIndex === (tabButtonsLength - 1)) {
                    // If selected tab is the last one
                    _toggleClass(tablistButtons, 'end');
                    _toggleClass(tablistDescription, 'end');

                    nextBtn.setAttribute('aria-hidden', true);
                    backBtn.setAttribute('aria-hidden', false);
                } else {
                    // If is on the first one
                    _toggleClass(tablistButtons, 'start');
                    _toggleClass(tablistDescription, 'start');

                    nextBtn.setAttribute('aria-hidden', false);
                    backBtn.setAttribute('aria-hidden', true);
                }
            } else {
                tabButtons.forEach(function (button) {
                    var btnSpan = button.querySelector('span');

                    if (button.getAttribute('data-text')) {
                        btnSpan.innerText = button.getAttribute('data-text');
                    }
                });

                _toggleClass(tablistDescription, 'start');
                _toggleClass(tablistButtons, 'start');
            }
        }

        // Helper function to handle the premium cta click
        function _handlePremiumCTA(e) {
            var target = e.target.classList.contains('button') ? e.target : e.target.parentElement,
                href = target ? target.getAttribute('href') : '';

            if (!href || href === '/' || href === '#') {
                e.preventDefault();
                EDC.utils.simulateClick(nextBtn);
                EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);
            }
        }

        // Attach events
        function _attachEvents() {
            var premiumCTAs = element.querySelectorAll('.premiumcta a');

            EDC.utils.attachEvents(tabButtons, 'click', _showSelectedTab);
            EDC.utils.attachEvents(nextBtn, 'click', _showNextTab);
            EDC.utils.attachEvents(backBtn, 'click', _showNextTab);
            EDC.utils.attachEvents(premiumCTAs, 'click', _handlePremiumCTA);

            EDC.utils.attachEvents(window, 'resize', function () {
                if (isMobile !== _isMobile()) {
                    isMobile = _isMobile();
                    _setTabs();
                }
            });
        }

        _attachEvents();
        isMobile = _isMobile();

        setTimeout(function () {
            var initialButton,
                ariaControls,
                initalTab;

            EDC.utils.scrollToSelectedTab(element, tabButtons, delimiter);
            initialButton = element.querySelector('.tab-button[aria-selected="true"]');
            ariaControls = initialButton ? initialButton.getAttribute('aria-controls') : '';
            initalTab = ariaControls ? element.querySelector('.tab-panel[id="' + ariaControls + '"]') : '';

            if (initalTab) {
                initalTab.setAttribute('aria-hidden', false);
            }

            _setTabs();

            _initializeSwipe();
        }, 1000);
    }

    // Public methods
    function init() {
        var tabSet = document.querySelectorAll('.c-blue-background-tabs:not([data-component-state="initialized"])');

        if (tabSet) {
            tabSet.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', blueBackgroundTabsJS.init);