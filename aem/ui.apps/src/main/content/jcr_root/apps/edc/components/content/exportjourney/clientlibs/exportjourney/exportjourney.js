var exportJourney = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var nav = element.querySelector('.nav-bar'),
            tabsContainer = element.querySelector('.tabs'),
            tabLabelsContainer = nav.querySelector('.tab-labels-container'),
            arrowButtonsContainer = nav.querySelector('.actions-nav'),
            tabLabels = nav.querySelectorAll('.tab-label'),
            lastStep = tabLabels[tabLabels.length - 1],
            body = document.querySelector('body'),
            stickyOffsetTop,
            stickyOffsetBottom,
            megaNav = document.querySelector('.header .main-header');

        // All private methods
        function _handleClicks(e) {
            var tabLabel = (e.target).closest('button'),
                mobileOffSetTop;

            e.preventDefault();
            tabLabels.forEach(function (label) {
                label.parentNode.classList.remove('active');
            });

            tabLabel.parentNode.classList.add('active');

            if (lastStep.parentNode.classList.contains('active')) {
                element.classList.add('last-step');
            } else {
                element.classList.remove('last-step');
            }
            if (element.classList.contains('journey-stick')) {
                mobileOffSetTop = megaNav && (window.innerWidth < window.EDC.props.media.lg) ? 65 : 20;
                EDC.utils.scrollTo('top', EDC.utils.getPosition(tabsContainer).y - mobileOffSetTop);
            }
        }

        function _handleButtonClick() {
            var nextTab = nav.querySelector('li.active').nextElementSibling,
                tabButtons = nav.querySelectorAll('li button'),
                nextTabButton = nextTab ? nextTab.querySelector('button') : '';

            if (nextTab && nextTabButton) {
                tabButtons.forEach(function (item) {
                    item.classList.remove('triggered');
                });
                nextTabButton.classList.add('triggered');
                EDC.utils.simulateClick(nextTabButton);

                if (window.innerWidth <= EDC.utils.getPosition(nextTabButton).x + nextTabButton.offsetWidth) {
                    EDC.utils.scrollContent(element, 'right');
                } else if (EDC.utils.getPosition(nextTabButton).x <= 0) {
                    EDC.utils.scrollContent(element, 'left');
                }
            }
        }

        function _initializeComponent() {
            var firstStep = tabLabels && tabLabels.length ? '#' + tabLabels[0].getAttribute('aria-controls') : '.c-persona-content';

            // Show first step container
            document.querySelector(firstStep).classList.add('show');

            // Set Initial Position Sticky Navigation Element
            setTimeout(function () {
                stickyOffsetTop = element.offsetTop;
                stickyOffsetBottom = stickyOffsetTop + element.offsetHeight - nav.offsetHeight;
            }, 1000);
        }

        function _observer() {
            var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver,
                elementObserved = megaNav ? megaNav.querySelector('.top-nav') : null,
                observer;

            if (elementObserved) {
                observer = new MutationObserver(function (mutations) {
                    mutations.forEach(function (mutation) {
                        if (mutation.type === 'attributes') {
                            if (elementObserved.classList.contains('scrolldown') || EDC.utils.getDeviceViewPort() === 'desktop') {
                                nav.classList.remove('stick-nav-displayed');
                            } else {
                                nav.classList.add('stick-nav-displayed');
                            }
                        }
                    });
                });

                observer.observe(elementObserved, {
                    attributes: true // configure it to listen to attribute changes
                });
            }
        }

        function _fixNav() {
            tabsContainer.style.paddingTop = nav.offsetHeight + 'px';
            element.classList.add('journey-stick');
            tabLabelsContainer.classList.add('container');
            arrowButtonsContainer.classList.add('container');
        }

        function _unFixNav() {
            element.classList.remove('journey-stick');
            tabsContainer.style.paddingTop = 0;
            tabLabelsContainer.classList.remove('container');
            arrowButtonsContainer.classList.remove('container');
        }

        function _handleScroll() {
            var posY = window.pageYOffset || document.documentElement.scrollTop || body.scrollTop || 0;

            if (element.classList.contains('sticky')) {
                stickyOffsetTop = element.offsetTop + tabsContainer.offsetTop;
                stickyOffsetBottom = stickyOffsetTop + element.offsetHeight - nav.offsetHeight;

                if (megaNav && (window.innerWidth < window.EDC.props.media.lg)) {
                    stickyOffsetTop += 65;
                }

                if (!element.classList.contains('journey-stick') && (posY >= stickyOffsetTop && posY < stickyOffsetBottom)) {
                    _fixNav();
                } else if (element.classList.contains('journey-stick') && (posY < stickyOffsetTop || posY >= stickyOffsetBottom)) {
                    _unFixNav();
                }
            }
        }

        function _verifyNav(e) {
            EDC.utils.checkTabsScroll(e);
            EDC.utils.verifyScrollPos(nav.querySelector('.tab-labels-container'));
        }

        function _attachEvents() {
            var buttons = element.querySelectorAll('button.content-button');

            EDC.utils.attachEvents(tabLabels, 'click', _handleClicks);
            EDC.utils.attachEvents(buttons, 'click', _handleButtonClick);
            EDC.utils.attachEvents(window, 'scroll', _handleScroll);
            EDC.utils.attachEvents(nav.querySelector('.tab-labels'), 'scroll', _verifyNav);
        }

        _observer();
        _attachEvents();
        _initializeComponent();
    }

    // Public methods
    function init() {
        var exportJourneys = document.querySelectorAll('.c-export-journey:not([data-component-state="initialized"])');

        if (exportJourneys) {
            exportJourneys.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', exportJourney.init);