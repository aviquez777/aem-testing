var campaignStickyNavJS = (function () {
    'use strict';


    function _initialize(element) {
        var skipBtn = document.querySelector('#skip'),
            mainContent = document.querySelector('#maincontent'),
            menuItems = element.querySelector('.link-list-wrapper'),
            navToggle = element.getElementsByClassName('nav-toggle'),
            headerType = document.querySelector('.home-campaign'),
            closeMenuBtn = element.querySelector('.close'),
            currentPosition = 0,
            d = document,
            body = d.querySelector('body'),
            footer = d.getElementById('footer'),
            footerPosition,
            navLinks = element.querySelectorAll('.header-nav li a, .cta a[data-target]'),
            anchors = [],
            placeholderId = element.getAttribute('id').replace('campaign-sticky-nav', 'campaign-nav-position'),
            allAnchors,
            banner,
            bannerImg = document.querySelectorAll('.c-value-proposition-banner.campaign img')[0],
            bannerHeight,
            navHeight;

        function _initSkipBtn() {
            if (typeof (skipBtn) !== 'undefined') {
                EDC.utils.attachEvents(skipBtn, 'click', function (e) {
                    e.preventDefault();
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(mainContent).y);

                    // when focus leaves this element,
                    // remove the tabindex attribute
                    EDC.utils.attachEvents(mainContent, 'blur', function () {
                        mainContent.removeAttribute('tabindex');
                    });

                    // Setting 'tabindex' to -1 takes an element out of normal
                    // tab flow but allows it to be focused via javascript
                    mainContent.setAttribute('tabindex', -1);
                    mainContent.focus();
                });
            }
        }

        function _closeMenu() {
            menuItems.classList.remove('show');
        }

        function _openMenu() {
            body.classList.add('open-nav');

            if (menuItems) {
                menuItems.classList.add('show');
                body.style.height = menuItems.offsetHeight + 'px';
            }
        }

        function _initComponent() {
            var j;

            if (typeof (navToggle) !== 'undefined' && navToggle !== null && navToggle.length > 0) {
                navToggle[0].addEventListener('click', function () {
                    _openMenu();
                });
            }

            if (typeof (closeMenuBtn) !== 'undefined' && closeMenuBtn !== null && closeMenuBtn.length > 0) {
                for (j = 0; j < closeMenuBtn.length; j++) {
                    closeMenuBtn[j].addEventListener('click', function () {
                        EDC.components.headerNav.closeMenu();
                    });
                }
            }
        }

        function _getBannerHeight() {
            bannerHeight = banner.clientHeight;
            navHeight = d.querySelector('.top-nav').clientHeight;
        }

        function _calcFooterPos() {
            return footer !== null ? EDC.utils.getPosition(footer).y : 0;
        }

        function _calcAnchorsPos() {
            allAnchors = document.querySelectorAll('.section-title a.anchor, .panel[data-target]');
            anchors = [];

            allAnchors.forEach(function (anchor) {
                anchors.push(
                    {
                        target: anchor.getAttribute('data-target'),
                        position: EDC.utils.getPosition(anchor).y
                    }
                );
            });
        }

        function _handleLinkClick(e) {
            var el = e.currentTarget,
                value = el.getAttribute('data-target'),
                accordionElement,
                anchorTarget,
                upSideDownHeight = window.innerWidth < EDC.props.media.lg ? 85 : 10,
                extraHeight = 0;

            _closeMenu();
            _calcAnchorsPos();

            if (value && (value.indexOf('/') <= -1)) {
                e.preventDefault();
                accordionElement = d.querySelector('.panel[data-target="' + value + '"]');
                anchorTarget = accordionElement ? accordionElement : d.querySelector('a.anchor[data-target="' + value + '"]');
            }

            if (anchorTarget) {
                extraHeight = currentPosition < EDC.utils.getPosition(anchorTarget).y ? 0 : upSideDownHeight;

                // If targetted element is an accordion, expands the content
                if (accordionElement) {
                    accordionElement.setAttribute('aria-expanded', 'true');
                    accordionElement.querySelector('.panel-inner').setAttribute('aria-hidden', 'false');
                    _calcAnchorsPos();
                }

                EDC.utils.scrollTo('top', EDC.utils.getPosition(anchorTarget).y - extraHeight, 1000);
                currentPosition = EDC.utils.getPosition(anchorTarget).y;
            }
        }

        function _fixNav() {
            body.style.paddingTop = '0px';
            element.classList.add('sticky');
        }

        function _unFixNav() {
            body.style.paddingTop = '0px';
            element.classList.remove('sticky');
        }

        function _removeTopExtraPadding() {
            if (window.innerWidth >= EDC.props.media.lg) {
                body.classList.remove('stickynav-extra-pad');
                element.classList.remove('stickynav-extra-pad');
            }
        }

        function _createPlaceHolder() {
            var node = document.createElement('div');

            node.setAttribute('id', placeholderId);
            element.parentNode.insertBefore(node, element);

            footerPosition = _calcFooterPos();
            _calcAnchorsPos();

        }

        function _setActive(target) {
            var reachedElement = element.querySelector('.link-list a[data-target="' + target + '"]'),
                activeElements = element.querySelectorAll('li.active'),
                li;

            if (reachedElement) {
                li = reachedElement.closest('li');

                activeElements.forEach(function (elem) {
                    elem.classList.remove('active');
                });

                if (li) {
                    li.classList.add('active');
                }
            }
        }

        function _setActiveElementonScroll(windowScrollPosition) {
            var i;

            if (navLinks.length && anchors.length && footerPosition) {
                windowScrollPosition = window.pageYOffset + 20;
                if (windowScrollPosition >= anchors[0].position && windowScrollPosition < footerPosition) {
                    for (i = 0; i < anchors.length; i++) {
                        if (i + 1 < anchors.length) {
                            if (windowScrollPosition >= anchors[i].position && windowScrollPosition < anchors[i + 1].position) {
                                _setActive(anchors[i].target);
                            }
                        } else if (windowScrollPosition >= anchors[i].position && windowScrollPosition < footerPosition) {
                            _setActive(anchors[i].target);
                        }
                    }
                } else {
                    _setActive(anchors[0].target);
                }
            }
        }

        function _opacityScroll() {
            var opactiy = (window.pageYOffset / (bannerHeight - navHeight)).toFixed(2),
                nav = element.querySelector('.top-nav');

            if (headerType) {
                element.classList.add('contrast');
                nav.style.backgroundColor = 'rgba(255, 255, 255)';
            } else {
                if (opactiy > 0.65) {
                    element.classList.add('contrast');
                } else {
                    element.classList.remove('contrast');
                }

                nav.style.backgroundColor = 'rgba(255, 255, 255, ' + opactiy + ')';
            }
        }

        function _handleScroll() {
            var posY = window.pageYOffset || document.documentElement.scrollTop || body.scrollTop || 0;

            _calcAnchorsPos();
            _setActiveElementonScroll(posY);
            _opacityScroll();
            if (!headerType) {
                if (posY >= bannerHeight - navHeight) {
                    _fixNav();
                } else {
                    _unFixNav();
                }
            }
        }

        function _handleResize() {
            var isTablet = window.innerWidth >= window.EDC.props.media.md && window.innerWidth < window.EDC.props.media.lg,
                isMobile = window.innerWidth < window.EDC.props.media.md,
                cta = element.querySelector('.cta a[data-target]');

            // remove top extra padding
            _removeTopExtraPadding();
            body.style.paddingTop = '0px';

            // recalculate banner height
            _getBannerHeight();

            // recalculate footer position
            footerPosition = _calcFooterPos();

            // handle scroll position
            _handleScroll();

            if (cta) {
                if (isTablet && isMobile) {
                    cta.classList.add('no-btn');
                } else {
                    cta.classList.remove('no-btn');
                }
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(navLinks, 'click', _handleLinkClick);
            EDC.utils.attachEvents(closeMenuBtn, 'click', _closeMenu);
            EDC.utils.attachEvents(window, 'scroll', _handleScroll);
            EDC.utils.attachEvents(window, 'resize', _handleResize);
        }

        function _initBannerHeight() {
            // _fixPaddingTop();
            banner = document.querySelectorAll('.c-value-proposition-banner.campaign')[0];
            _getBannerHeight();
            _createPlaceHolder();
            _handleScroll();
        }

        // function _fixPaddingTop(){
        //            body.classList.add('campaign-header');
        //        }

        if (bannerImg && bannerImg.complete) {
            _initBannerHeight();
        } else {
            EDC.utils.attachEvents(bannerImg, 'load', _initBannerHeight);
        }

        _initSkipBtn();
        _initComponent();
        _attachEvents();
    }

    // Public methods
    function init() {
        var stickyNavs = document.querySelectorAll('.campaign-sticky-nav:not([data-component-state="initialized"])');

        if (stickyNavs) {
            stickyNavs.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }


    return {
        init: init
    };
})();

window.addEventListener('load', campaignStickyNavJS.init);