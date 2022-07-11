var leftHandNavJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var isDevice;

        // Private methods
        // Helper function to determine screen width
        function _isDevice() {
            return window.innerWidth < EDC.props.media.lg;
        }

        // Add Chevron arrow to patent links
        function _chevronArrow() {
            var parentLink = document.querySelectorAll('.parent-link');

            parentLink.forEach(function (item) {
                if (item.querySelector('ul')) {
                    item.querySelector('span').classList.add('chevron');
                }
            });
        }

        // Add background child links
        function _bgChildLink() {
            var childSelected = element.querySelectorAll('.parent-link ul li.selected'),
                childBg = element.querySelector('.parent-link ul li.selected a.sublink');

            childSelected.forEach(function () {
                childBg.classList.add('blue-light');
            });
        }

        // Helper function to initialize parent element on load
        function _expandParentLink() {
            var mainNav = element.querySelector('.main-nav'),
                selectedLink = mainNav ? mainNav.querySelector('.selected') : null,
                parentLink,
                link,
                selectedParent;

            if (selectedLink) {
                if (selectedLink.closest('.parent-link')) {
                    parentLink = selectedLink.closest('.parent-link');
                } else {
                    parentLink = selectedLink;
                }

                link = parentLink ? parentLink.querySelector('a') : null;
                selectedParent = selectedLink.querySelector('a');
            }

            if (parentLink && link) {
                if (parentLink.querySelector('ul')) {
                    link.setAttribute('aria-expanded', true);
                    link.classList.add('expanded');
                }
            }

            if (parentLink.classList.contains('selected')) {
                selectedParent.classList.add('blue-light');
            }

            _chevronArrow();
        }

        // Helper function to display sublinks of selected item
        function _displaySublinks(e) {
            var parentLink = e.target.parentElement,
                link = parentLink.querySelector('a');

            if (parentLink.querySelector('ul')) {
                link.classList.toggle('expanded');

                if (link.classList.contains('expanded')) {
                    link.setAttribute('aria-expanded', true);
                } else {
                    link.setAttribute('aria-expanded', false);
                }
            } else if (link) {
                EDC.utils.simulateClick(link);
            }
        }

        // Helper function to set the label of the dropdown when page is loaded
        function _setDropdownLabel() {
            var dropdown = element.querySelector('.ui.selection.dropdown'),
                dropdownLabel = dropdown.querySelector('.text'),
                selectedOption = dropdown.querySelector('.active.selected');

            if (selectedOption) {
                dropdownLabel.innerText = selectedOption.innerText;
            }
        }

        // Helper function to simulate the click of a link on the nav
        function _navigate(e) {
            var selectedValue = e.target.options[e.target.selectedIndex].value,
                selectedLink = element.querySelector('.main-nav a[href="' + selectedValue + '"]');

            if (selectedLink) {
                EDC.utils.simulateClick(selectedLink);
            }
        }

        // Attach events
        function _attachEvents() {
            var navSelect = element.querySelector('select'),
                chevron = element.querySelectorAll('.chevron');

            EDC.utils.attachEvents(navSelect, 'change', _navigate);
            EDC.utils.attachEvents(chevron, 'click', _displaySublinks);

            EDC.utils.attachEvents(window, 'resize', function () {
                if (isDevice !== _isDevice()) {
                    isDevice = _isDevice();
                    _setDropdownLabel();
                }
            });
        }

        isDevice = _isDevice();
        _bgChildLink();
        _setDropdownLabel();
        _expandParentLink();
        _attachEvents();
    }

    // Public methods
    function init() {
        var leftHandNav = document.querySelectorAll('.c-left-hand-nav:not([data-component-state="initialized"])');

        if (leftHandNav) {
            leftHandNav.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', leftHandNavJS.init);