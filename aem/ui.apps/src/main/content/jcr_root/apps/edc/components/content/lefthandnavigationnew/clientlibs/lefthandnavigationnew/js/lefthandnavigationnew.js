var leftHandNavNewJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var modal = document.querySelector('.showMobile .modal'),
            focusableElements = modal.querySelectorAll('a.link, a.sublink, .edc-btn-icon'),
            desktopFocusableElements = element.querySelectorAll('a.link, a.sublink'),
            visibleFocusableElements,
            currentFocusedElementIndex = 0,
            currentFocusedElement,
            isModalClosed = true,
            closeModalButton = modal.querySelector('.edc-btn-icon');

        // Private methods

        // Checks if screen size is mobile
        function _isMobile() {
            return window.innerWidth < EDC.props.media.xl;
        }

        // Validate which of the focused elements are currently visible
        function _validateFocusableElements(elementList) {
            var validatedFocusableElements = [],
                hasButton,
                index;

            elementList.forEach(function (elem) {
                if (elem.classList.contains('link')) {
                    validatedFocusableElements.push(elem);
                }

                if (elem.className === 'link has-subitems expanded') {
                    elementList.forEach(function (child) {
                        if (child.getAttribute('sublink-key') === elem.getAttribute('parent-key')) {
                            validatedFocusableElements.push(child);
                        }
                    });
                }

                if (elem.classList.contains('edc-btn-icon')) {
                    hasButton = elem;
                }
            });

            if (hasButton) {
                validatedFocusableElements.push(closeModalButton);
            }

            visibleFocusableElements = validatedFocusableElements;
            index = Array.prototype.indexOf.call(visibleFocusableElements, currentFocusedElement);
            currentFocusedElementIndex = index !== -1 ? index : currentFocusedElementIndex;
        }

        // Recalculates which element was clicked when expanding or closing a link, to set the correct focusedElementIndex
        function _handleButtonExpanded(buttonIndex) {
            var elementClicked;

            if (buttonIndex) {
                elementClicked = Array.prototype.find.call(visibleFocusableElements, function (elem) {
                    return elem.getAttribute('parent-key') === buttonIndex;
                });

                currentFocusedElementIndex = Array.prototype.indexOf.call(visibleFocusableElements, elementClicked);
            }
        }

        // Event listener that triggers when a link is expanded or closed
        function _handleButtonExpandedEventListener(event) {
            var target = event.target;

            if (visibleFocusableElements.includes(closeModalButton)) {
                _validateFocusableElements(focusableElements);
            } else {
                _validateFocusableElements(desktopFocusableElements);
            }

            _handleButtonExpanded(target.getAttribute('button-key'));
        }

        // Event listener that triggers the focus trap when the user press the TAB key
        function _handleTabKeyPressedEventListener(event) {
            if (_isMobile() && isModalClosed) {
                return;
            }

            if (event.key !== 'Tab') {
                return;
            }

            if (isModalClosed) {
                if (event.shiftKey) { // TAB + SHIFT
                    if (currentFocusedElementIndex - 1 > -1) {
                        event.preventDefault();
                        currentFocusedElementIndex -= 1;
                    }
                } else if (currentFocusedElementIndex + 1 < visibleFocusableElements.length) { // TAB
                    event.preventDefault();
                    currentFocusedElementIndex += 1;
                }
            }

            if (!isModalClosed) {
                event.preventDefault();

                if (event.shiftKey) { // TAB + SHIFT
                    currentFocusedElementIndex = currentFocusedElementIndex > 0 ? currentFocusedElementIndex - 1 : visibleFocusableElements.length - 1;
                } else { // TAB
                    currentFocusedElementIndex = currentFocusedElementIndex + 1 < visibleFocusableElements.length ? currentFocusedElementIndex + 1 : 0;
                }
            }

            visibleFocusableElements[currentFocusedElementIndex].focus();
            currentFocusedElement = visibleFocusableElements[currentFocusedElementIndex];
        }

        // Function that enables the focus trap event listeners
        function _enableFocusTrapEventListeners() {
            element.addEventListener('click', _handleButtonExpandedEventListener);
            element.addEventListener('keydown', _handleTabKeyPressedEventListener);
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

            if (parentLink && parentLink.classList.contains('selected')) {
                selectedParent.classList.add('blue-light');
            }

            _chevronArrow();
        }

        // Helper function to display sublinks of selected item
        function _displaySublinks(e) {
            var parentLink = e.currentTarget.parentElement,
                link = parentLink.querySelector('a');

            e.preventDefault();

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

        // Helper function to simulate the click of a link on the nav
        function _navigate(e) {
            var selectedValue = e.target.options[e.target.selectedIndex].value,
                selectedLink = element.querySelector('.main-nav a[href="' + selectedValue + '"]');

            if (selectedLink) {
                EDC.utils.simulateClick(selectedLink);
            }
        }

        // Function that clean the index when opening and closing the modal
        function _cleanFocusedElementIndex(indexNum) {
            currentFocusedElement = null;
            currentFocusedElementIndex = indexNum;
        }

        // Helper function to Open Modal
        function _openModal() {
            modal.classList.remove('hide');
            _validateFocusableElements(focusableElements);
            _cleanFocusedElementIndex(-1);
            isModalClosed = false;
        }

        // Helper function to Close Modal
        function _closeModal() {
            modal.classList.add('hide');
            _validateFocusableElements(desktopFocusableElements);
            _cleanFocusedElementIndex(0);
            isModalClosed = true;
        }

        // Helper function to Close Modal Shade
        function _closeModaShadel() {
            modal.classList.add('hide');
            _validateFocusableElements(desktopFocusableElements);
            _cleanFocusedElementIndex(0);
            isModalClosed = true;
        }

        // Attach events
        function _attachEvents() {
            var navSelect = element.querySelector('select'),
                itemsWithSubItems = element.querySelectorAll('a.has-subitems'),
                closeModal = document.querySelector('.showMobile .modal-close'),
                closeModalShade = document.querySelector('.showMobile .modal-shade'),
                openModal = document.querySelector('.showMobile button');

            EDC.utils.attachEvents(navSelect, 'change', _navigate);
            EDC.utils.attachEvents(itemsWithSubItems, 'click', _displaySublinks);
            EDC.utils.attachEvents(closeModal, 'click', _closeModal);
            EDC.utils.attachEvents(closeModalShade, 'click', _closeModaShadel);
            EDC.utils.attachEvents(openModal, 'click', _openModal);
        }

        _bgChildLink();
        _expandParentLink();
        _attachEvents();
        _validateFocusableElements(desktopFocusableElements);
        _enableFocusTrapEventListeners();
    }

    // Public methods
    function init() {
        var leftHandNavNew = document.querySelectorAll('.c-left-hand-nav-new:not([data-component-state="initialized"])');

        if (leftHandNavNew) {
            leftHandNavNew.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', leftHandNavNewJS.init);