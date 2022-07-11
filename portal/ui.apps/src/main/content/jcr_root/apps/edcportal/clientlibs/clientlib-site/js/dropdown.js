/**
* Component initialization
* Behaviors:
*       allowReselection: true // Allows to fire on click / on change event on selected item
*       action: activate // Default value for single select
*       action: custom // Override action for multi select dropdown.
*
* Settings:
*       onShow: Custom behavior on dropdown click to open options
*       onHide: Custom behavior on dropdown option click, blur or closed
*       onChange: Custom behavior only for single dropdown to avoid closing the element on blur
*       duration: 0 // Modifies transition duration for user friendly experience on mobile
*/

var dropdownJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var elemStyles = window.getComputedStyle(element),
            select = element.querySelector('select'),
            selectClassName = select.className,
            options = select.querySelectorAll('option'),
            overlay = element.querySelector('.dropdown-overlay'),
            overlayStyles = window.getComputedStyle(overlay),
            dropdown = element.querySelector('.ui.selection.dropdown'),
            isMultiple = element.querySelector('.ui-multiple'),
            textOption = element.querySelector('p.selection'),
            confirmBtn = element.querySelector('.edc-primary-btn'),
            dropdownContainer = element.querySelector('.dropdown-container'),
            singularOption = textOption ? textOption.getAttribute('data-singular-text') : '',
            pluralOption = textOption ? textOption.getAttribute('data-plural-text') : '',
            selectedCount = 0,
            selectionLimit = select.getAttribute('data-limit'),
            isDevice,
            isClosed,
            isConfirmed,
            dropdownText,
            dropdownDefaultValue,
            selectionContainer,
            menu,
            timerId,
            overlayHeight,
            label,
            selectionHeight;

        // Helper function to determine display width and return if is a mobile device
        function _isDevice() {
            return window.innerWidth < EDC.props.media.lg;
        }

        // Private methods
        function _isIE() {
            var ua = window.navigator.userAgent,
                msie = ua.indexOf('MSIE ');

            return msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./);
        }

        // Helper function to remove blur action on dropdown when needed.
        function _setClosedState() {
            isClosed = true;
        }

        // Helper function to confirm the selected values and assign them to the select
        function _setConfirmedState(e) {
            if (e) {
                e.preventDefault();
            }

            element.querySelectorAll('.selected-item').forEach(function (option) {
                option.setAttribute('data-selected', true);
            });

            isConfirmed = true;

            _setClosedState();
        }

        function _getPropertyValue(property) {
            var value;

            if (property) {
                value = parseInt(property, 10);
            }

            return isNaN(value) ? 0 : value;
        }

        // Helper function to create the label for multi select dropdown
        function _createLabel(elem, value) {
            var selectionOption;

            if (value >= 0) {
                if (value === 1) {
                    selectionOption = singularOption;
                } else {
                    selectionOption = pluralOption;
                }

                elem.innerHTML = '(' + value + ') ' + selectionOption;
            } else {
                elem.innerHTML = dropdownDefaultValue;
            }
        }

        function _multipleDropdownPrepopulationFromEloqua() {
            var valuesStr = element.dataset.selectedValues,
                menuItems = element.querySelectorAll('.dropdown.multiple .menu .item'),
                dropdownLabel = element.querySelector('.default.text'),
                valuesArr = [];

            if (valuesStr) {
                valuesArr = valuesStr.split('::');
                menuItems.forEach(function (item, index) {
                    valuesArr.forEach(function (value) {
                      
                        if (item.dataset.value === value) {
                            options[index + 1].selected = true;
                            item.click();
                        }
                    });
                });
                _createLabel(dropdownLabel, valuesArr.length);
            }
        }

        function _clearAllSelectedItems() {
            var selectedItems = element.querySelectorAll('.selected-item');

            selectedItems.forEach(function (item) {
                item.classList.remove('selected-item');
            });

            select.selectedIndex = 0;
        }

        function _setSelectedValues() {
            var confirmCount = 0;

            element.querySelectorAll('.selected-item').forEach(function (item) {
                // Removes blue color of uncorfirmed elements
                if (!item.getAttribute('data-selected')) {
                    item.classList.remove('selected-item');
                } else {
                    options.forEach(function (option) {
                        if (option.value === item.getAttribute('data-value')) {
                            option.selected = true;
                            confirmCount++;
                        }
                    });
                }
            });

            // Remove any leftover items
            element.querySelectorAll('.item[data-removed="true"]').forEach(function (item) {
              console.warn(item.getAttribute('data-value'))
              options.forEach(function (option) {
                if (option.value === item.getAttribute('data-value')) {
                    option.selected = false;
                    item.removeAttribute('data-removed');
                }
                });
            });

            if (!isConfirmed && confirmCount > 0) {
                element.querySelectorAll('.item[data-selected="true"]').forEach(function (item) {
                    if (!item.classList.contains('selected-item')) {
                        item.classList.add('selected-item');
                        confirmCount++;
                    }
                });
            } else {
                element.querySelectorAll('.item[data-selected="true"]').forEach(function (item) {
                    if (!item.classList.contains('selected-item')) {
                        item.removeAttribute('data-selected');
                    }
                });
            }

            return confirmCount;
        }

        function _hideDropdown() {
            if ($(dropdown).dropdown('is visible')) {
                $(dropdown).dropdown('hide');
            }
        }

        function _showDropdown() {
            if ($(dropdown).dropdown('is visible')) {
                clearInterval(timerId);
                timerId = 0;

                selectionContainer = element.querySelector('.selection-container');
                menu = element.querySelector('.menu');
                label = element.querySelector('.dropdown-title');

                // Gets the container availabe space on window
                overlayHeight = overlay.clientHeight - _getPropertyValue(overlayStyles.getPropertyValue('padding-top')) - _getPropertyValue(overlayStyles.getPropertyValue('padding-bottom'));

                // Gets the height of the rest of the elements
                selectionHeight = selectionContainer ? selectionContainer.offsetHeight : 0;

                // Sets the height of the menu and the dropdown container
                if (overlayHeight < menu.offsetHeight + label.offsetHeight + selectionHeight) {
                    dropdownContainer.style.height = overlayHeight.toString() + 'px';
                    // 15px extra to account for the selectionContainer drop shadow
                    menu.style.height = (overlayHeight - label.offsetHeight - selectionHeight - 15).toString() + 'px';
                }

                dropdownContainer.classList.add('ready');
                // Disables scrolling on body and html elements on all devices
                bodyScrollLock.disableBodyScroll(menu);
            }
        }

        // Helper function to migrate classes from Select to Dropdown
        function _migrateClasses() {
            var selectOptions = select.options,
                dropdownOptions = element.querySelector('.menu').querySelectorAll('.item');

            if (selectOptions.length === dropdownOptions.length) {
                dropdownOptions.forEach(function (option, index) {
                    if (selectOptions[index].classList.length) {
                        option.classList = option.classList + ' ' + selectOptions[index].classList;
                    }
                });
            } else {
                dropdownOptions.forEach(function (option, index) {
                    if (index + 1 <= dropdownOptions.length) {
                        if (selectOptions[index + 1].classList.length) {
                            option.classList = option.classList + ' ' + selectOptions[index + 1].classList;
                        }
                    }
                });
            }
        }

        // Function to initialize dropdown based on device width (Desktop or Tablet / Mobile)
        function _initializeDropdown() {
            var defaultSelection = false,
                defaultTransitionDuration = 200;

            isDevice = _isDevice();

            if (isDevice) {
                defaultSelection = true;
                defaultTransitionDuration = 0;
            }

            // Initializes the dropdown with actions and setting defined above
            $(dropdown).dropdown({
                // Behaviors
                allowReselection: defaultSelection,
                action: !isMultiple ? 'activate' : function (text, value, item) {
                    if (item.getAttribute('data-value') === 'clear-all') {
                        _clearAllSelectedItems();
                        selectedCount = 0;
                    } else if (item.classList.contains('selected-item')) {
                        item.classList.remove('selected-item');
                        // Set a flag to remove leftover items later
                        item.setAttribute('data-removed', true);
                        selectedCount--;
                    } else if (!selectionLimit || (selectedCount + 1) <= selectionLimit) {
                        item.classList.add('selected-item');
                        selectedCount++;
                    }

                    if (dropdownText && textOption) {
                        if (selectedCount === 0) {
                            select.selectedIndex = 0;
                            if (isDevice) {
                                confirmBtn.disabled = true;
                            } else {
                                dropdownText.classList.remove('selected');
                            }
                        } else if (isDevice) {
                            confirmBtn.disabled = false;
                        } else {
                            dropdownText.classList.add('selected');
                        }

                        if (isDevice) {
                            _createLabel(textOption, selectedCount);
                        } else {
                            _createLabel(dropdownText, selectedCount);
                        }
                    }
                },

                // Settings
                duration: defaultTransitionDuration,
                transition: 'none'
            });

            // Helper functions to Change behavior of the dropdown
            $(dropdown).dropdown('setting', 'onChange', function () {
                _setClosedState();
            });

            $(dropdown).dropdown('setting', 'onShow', function () {
                element.style.height = elemStyles.height;
                if (isDevice) {
                    isClosed = false;
                    isConfirmed = false;
                    overlay.classList.add('active');
                    timerId = setInterval(_showDropdown, 100);
                } else {
                    dropdownContainer.classList.add('open');
                }

                if (isMultiple) {
                    dropdownText = element.querySelector('.default.text');
                    dropdownDefaultValue = select.getAttribute('data-default-value');
                    selectedCount = element.querySelectorAll('.selected-item').length;

                    if (isDevice) {
                        _createLabel(textOption, selectedCount);
                    } else if (selectedCount === 0) {
                        _createLabel(dropdownText);
                    } else {
                        _createLabel(dropdownText, selectedCount);
                    }
                }
            });

            $(dropdown).dropdown('setting', 'onHide', function () {
                var itemsSelected = 0;

                if (isDevice) {
                    if (isClosed) {
                        // Validates if the confirm button is clicked to assign values on multi select dropdown
                        if (isMultiple) {
                            itemsSelected = _setSelectedValues();
                        }
                        overlay.classList.remove('active');
                        dropdownContainer.classList.remove('ready');
                        dropdownContainer.style.height = 'auto';
                        // Enables scrolling on body and html elements on all devices
                        bodyScrollLock.enableBodyScroll(menu);
                    } else {
                        return false;
                    }
                } else {
                    _setConfirmedState();
                    itemsSelected = _setSelectedValues();
                    dropdownContainer.classList.remove('open');
                }

                element.style.height = 'auto';

                // If confirmed elements creates the label for the dropdown and assign values to the select
                if (isMultiple) {
                    if (itemsSelected > 0) {
                        dropdownText.classList.add('selected');
                        select.setAttribute('selected', true);
                        _createLabel(dropdownText, itemsSelected);
                    } else {
                        dropdownText.classList.remove('selected');
                        select.removeAttribute('selected');
                        _createLabel(dropdownText);
                    }

                    EDC.forms.validateInputs(select);
                }

                return true;
            });

            _migrateClasses();
        }

        // Destroys dropdown and sets default select on resize
        function _destroyDropdown() {
            var dropdownLabel = element.querySelector('.text'),
                isSelected = dropdownLabel.classList.contains('selected'),
                labelText = dropdownLabel.innerHTML;

            $(dropdown).dropdown('destroy');
            element.removeAttribute('style');
            overlay.classList.remove('active');
            dropdownContainer.classList.remove('ready');
            dropdownContainer.classList.remove('open');
            dropdownContainer.removeAttribute('style');
            select.className = selectClassName;
            dropdownContainer.insertBefore(select, dropdownContainer.querySelector('span.error'));
            dropdownContainer.removeChild(dropdownContainer.querySelector('div.ui'));
            bodyScrollLock.enableBodyScroll(menu);
            // Initialize dropdown again
            _initializeDropdown();

            if (isMultiple && isSelected) {
                dropdownLabel = element.querySelector('.default.text');
                dropdownLabel.innerHTML = labelText;
                dropdownLabel.classList.add('selected');

                element.querySelector('.menu').querySelectorAll('.item').forEach(function (item) {
                    item.classList.remove('selected');
                    item.classList.remove('active');
                    if (item.classList.contains('filtered')) {
                        item.classList.remove('filtered');
                        item.classList.add('selected-item');
                        item.setAttribute('data-selected', true);
                    }
                });
            }
        }

        // Attach events
        function _attachEvents() {
            var closeBtn = element.querySelector('.close-btn'),
                elementBlur = element.querySelector('.menu');

            EDC.utils.attachEvents(closeBtn, 'click', _setClosedState);
            EDC.utils.attachEvents(confirmBtn, 'click', _setConfirmedState);
            EDC.utils.attachEvents(elementBlur, 'blur', _hideDropdown);

            EDC.utils.attachEvents(window, 'resize', function () {
                if (isDevice !== _isDevice()) {
                    _destroyDropdown();
                }
            });

            if (_isIE() && elementBlur) {
                elementBlur.classList.add('is-ie');
                elementBlur.addEventListener('mousedown', function (e) {
                    if (elementBlur.clientWidth <= e.clientX) {
                        e.preventDefault();
                    }
                });
            }
            
        }

        _initializeDropdown();
        _attachEvents();

        if (isMultiple) {
            _multipleDropdownPrepopulationFromEloqua();
        }
    }

    // Public methods
    function init() {
        var dropdown = document.querySelectorAll('.c-dropdown:not([data-component-state="initialized"])');

        if (dropdown) {
            dropdown.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', dropdownJS.init);