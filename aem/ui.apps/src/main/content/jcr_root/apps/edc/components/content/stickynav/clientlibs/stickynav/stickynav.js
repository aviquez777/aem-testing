var stickyNavJS = (function () {
    'use strict';


    function _initialize(element) {
        var stickyPos,
            currentPosition = 0,
            barHeight,
            d = document,
            body = d.querySelector('body'),
            footer = d.getElementById('footer'),
            footerPosition,
            navLinks = element.querySelectorAll('.sticky-nav-inner li a, .cta a[data-target]'),
            select = element.querySelector('select'),
            anchors = [],
            placeholderId = element.getAttribute('id').replace('c-sticky-nav-wrapper', 'c-sticky-nav-position'),
            linkElement = element.querySelector('.link-list a[target]'),
            allAnchors;

        function _isIE() {
            var ua = window.navigator.userAgent,
                msie = ua.indexOf('MSIE ');

            return msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./);
        }

        function _fixIEanchors() {
            if (_isIE()) {
                element.closest('body').classList.add('is-ie');
            }
        }

        function _calcStickyNavHeight() {
            return element.offsetHeight;
        }

        function _calcFooterPos() {
            return footer !== null ? EDC.utils.getPosition(footer).y : 0;
        }

        // Tracking purposes
        function _trackEvent(el) {
            var destination = el.nodeName === 'A' ? el.getAttribute('data-target') : el.nodeName === 'SELECT' ? el.value : '',
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: el.dataset.eventName,
                        eventAction: element.dataset.eventAction, // order of link as it appears in the cluster
                        eventText: el.textContent.toLowerCase(),
                        eventValue: '',
                        eventPage: element.dataset.eventPage,
                        destinationPage: destination ? destination.toLowerCase() : '',
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
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
                isSelect = el.nodeName === 'SELECT',
                value = el.nodeName === 'A' ? el.getAttribute('data-target') : isSelect ? el.value : '',
                linkType = isSelect ? el.querySelector('[value="' + value + '"]').getAttribute('data-link-type') : null,
                accordionElement,
                anchorTarget,
                upSideDownHeight = window.innerWidth < EDC.props.media.lg ? 85 : 10,
                extraHeight = 0,
                hrefUrl = el.getAttribute('href'),
                hasUrl = el.hasAttribute('target'),
                linkLi = el.closest('li'),
                activeElements;

            _calcAnchorsPos();

            if (value && (!isSelect || linkType === 'data-target')) {
                e.preventDefault();
                accordionElement = d.querySelector('.panel[data-target="' + value + '"]');
                anchorTarget = accordionElement ? accordionElement : d.querySelector('a.anchor[data-target="' + value + '"]');
            } else if (linkType === 'href') {
                element.querySelector('a[href="' + value + '"]').click();
            }

            if (anchorTarget) {
                extraHeight = currentPosition < EDC.utils.getPosition(anchorTarget).y ? barHeight : barHeight + upSideDownHeight;

                // If targetted element is an accordion, expands the content
                if (accordionElement) {
                    accordionElement.setAttribute('aria-expanded', 'true');
                    accordionElement.querySelector('.panel-inner').setAttribute('aria-hidden', 'false');
                    _calcAnchorsPos();
                }

                EDC.utils.scrollTo('top', EDC.utils.getPosition(anchorTarget).y - extraHeight, 1000);
                currentPosition = EDC.utils.getPosition(anchorTarget).y;
            }

            if (hasUrl && window.location.href.includes(hrefUrl)) {
                e.preventDefault();
                EDC.utils.scrollTo('top', 0, 1000);
                setTimeout(function () {
                    activeElements = element.querySelectorAll('li.active');
                    activeElements.forEach(function (elem) {
                        elem.classList.remove('active');
                    });
                    linkLi.classList.add('active');
                }, 1000);
            }

            _trackEvent(el);
        }

        function _fixNav() {
            body.style.paddingTop = _calcStickyNavHeight() + 'px';
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

        function _calcStickyPlaceholderPos() {
            // recalculate sticky nav position
            stickyPos = d.getElementById(placeholderId).offsetTop;

            // if Tablet
            if (window.innerWidth >= EDC.props.media.sm && window.innerWidth < EDC.props.media.lg && d.querySelector('.top-nav') !== null) {
                stickyPos -= d.querySelector('.top-nav').offsetHeight - 1;
            }
        }

        function _createPlaceHolder() {
            var node = document.createElement('div');

            node.setAttribute('id', placeholderId);
            element.parentNode.insertBefore(node, element);

            // recalculate sticky nav position
            _calcStickyPlaceholderPos();

            barHeight = _calcStickyNavHeight();
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
                windowScrollPosition = window.pageYOffset + barHeight + 20;
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
                } else if (!linkElement) {
                    _setActive(anchors[0].target);
                }
            }
        }

        function _clearActiveDropdown() {
            var activeOptions = element.querySelectorAll('.dropdown-overlay div.item.selected.active');

            if (activeOptions.length > 1) {
                activeOptions[0].classList.remove('selected', 'active');
            }
        }

        function _handleScroll() {
            var posY = window.pageYOffset || document.documentElement.scrollTop || body.scrollTop || 0;

            _calcAnchorsPos();
            _setActiveElementonScroll(posY);

            if (posY >= stickyPos) {
                _fixNav();
            } else {
                _unFixNav();
            }
        }

        function _handleResize() {
            // remove top extra padding
            _removeTopExtraPadding();
            body.style.paddingTop = '0px';

            // recalculate sticky nav position
            _calcStickyPlaceholderPos();

            // recalculate footer position
            footerPosition = _calcFooterPos();

            // handle scroll position
            _handleScroll();
        }

        function _attachEvents() {
            EDC.utils.attachEvents(navLinks, 'click', _handleLinkClick);
            EDC.utils.attachEvents(select, 'change', _handleLinkClick);

            EDC.utils.attachEvents(window, 'scroll', _handleScroll);
            EDC.utils.attachEvents(window, 'resize', _handleResize);
        }

        _fixIEanchors()
        _createPlaceHolder();
        _attachEvents();
        _handleScroll();
        _clearActiveDropdown();

    }

    // Public methods
    function init() {
        var stickyNavs = document.querySelectorAll('.c-sticky-nav-wrapper:not([data-component-state="initialized"])');

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

window.addEventListener('load', stickyNavJS.init);