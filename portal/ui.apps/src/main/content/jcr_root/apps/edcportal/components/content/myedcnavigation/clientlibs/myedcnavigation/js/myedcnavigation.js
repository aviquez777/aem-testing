var myEDCNavigation = (function () {
    'use strict';

    var stickyOffset,
        body = document.querySelector('body');

    // Initialize the component
    function _initialize(element) {
        var tabs = body.querySelectorAll('.tab-labels li a');

        function _fixNav() {
            element.classList.add('sticky');
        }

        function _unFixNav() {
            element.classList.remove('sticky');
        }

        function _getElementOffset(el) {
            var rect = el.getBoundingClientRect();

            return {
                top: rect.top + window.pageYOffset,
                left: rect.left + window.pageXOffset
            };
        }

        function _handleScroll() {
            var width = window.innerWidth || document.documentElement.clientWidth || body.clientWidth,
                posY = window.pageYOffset || document.documentElement.scrollTop || body.scrollTop || 0,
                delta = 30;

            posY += delta;

            if (width > window.EDC.props.media.lg) {
                // If not on mobile, add a listener to make the Navigation Fixed (stick)
                EDC.utils.attachEvents(window, 'scroll', _handleScroll);


                if (posY >= stickyOffset) {
                    _fixNav();
                } else {
                    _unFixNav();
                }
            } else {
                // If in mobile:
                // -- Unfix the Navigation
                _unFixNav();

                // -- Remove the Listener
                EDC.utils.unAttachEvents(window, 'scroll', _handleScroll);
            }
        }

        function _scrollTo(e) {
            var target = this.dataset.target || '',
                section = document.querySelector('.section-title a[data-target="' + target + '"]');

            e.preventDefault();
            EDC.utils.scrollTo('top', EDC.utils.getPosition(section).y);
        }

        function _initializeComponent() {
            // Set Initial Position Sticky Navigation Element
            setTimeout(function () {
                _unFixNav();
                stickyOffset = _getElementOffset(element).top;

                // Check on Page Load if there's a need to add Scroll Listener
                // Note: in mobile there's no need to add listener
                _handleScroll();
            }, 1000);
        }

        function _navigate(e) {
            var el = e.target,
                index = el.selectedIndex;

            tabs[index].click();
        }

        function _updateDropdown() {
            var links = document.body.querySelectorAll('.tab-labels li a'),
                            dropdown = element.querySelector('.c-dropdown select'),
                            location = window.location.pathname,
                            dropdown_cont = element.querySelector('.dropdown');

            links.forEach(function(e,i) {
                if (e.pathname == location) {
                    dropdown[i].setAttribute("selected","selected");
                    dropdown_cont.querySelector('.text').innerHTML = dropdown[i].innerHTML;
                    var menu_items = dropdown_cont.querySelectorAll('.menu .item');

                    menu_items.forEach(function(e, ii) {
                        e.classList.remove("active","selected");
                        if (ii == i) {
                            menu_items[i].classList.add("active","selected");
                        }
                    })
                }
            })


        }

        function _attachEvents() {
            var subNavLinks = element.querySelectorAll('ul ul li a'),
                dropdown = element.querySelector('.c-dropdown select');

            // Check on Resize if there's a need to add Scroll Listener
            // Note: in mobile there's no need to add listener
            EDC.utils.attachEvents(window, 'resize', _handleScroll);
            EDC.utils.attachEvents(subNavLinks, 'click', _scrollTo);
            EDC.utils.attachEvents(dropdown, 'change', _navigate);
        }

        _attachEvents();
        _initializeComponent();
        _updateDropdown();

    }

    // Public methods
    function init() {
        var myEdcNav = document.querySelectorAll('.c-myedc-nav[data-sticky="true"]:not([data-component-state="initialized"])');
        //var myEdcNav = document.querySelectorAll('.c-myedc-nav:not([data-component-state="initialized"])');


        if (myEdcNav) {
            myEdcNav.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', myEDCNavigation.init);