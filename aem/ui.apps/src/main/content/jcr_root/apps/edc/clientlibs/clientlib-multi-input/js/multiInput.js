var multiInputJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var addItemInput = element.querySelector('.input-group input'),
            maxItemsAllowed = parseInt(element.dataset.itemsAllowed, 10),
            isItemsLimitExcedeed = false,
            isEmail = element.querySelector('input[type="email"]'),
            error = element.querySelector('span.error'),
            delimeter = element.getAttribute('data-delimeter') || ',';

        function _cleanItem() {
            if (addItemInput.value === '') {
                error.innerHTML = '';
                if (element.classList.contains('error')) {
                    element.classList.remove('error');
                }
                if (addItemInput.classList.contains('error')) {
                    addItemInput.classList.remove('error');
                }
            }
        }

        function _removeItem(e) {
            var ul = e.target.closest('ul'),
                li = e.target.closest('li'),
                inputToSubmit = element.querySelector('.inputs-list'),
                itemsList,
                finalListValues = '';

            ul.removeChild(li);
            itemsList = ul.querySelectorAll('li');
            if (itemsList.length > 0) {
                itemsList.forEach(function (item, key) {
                    if (key > 0) {
                        finalListValues += delimeter;
                    }
                    finalListValues += item.innerHTML.substring(0, item.innerHTML.indexOf('<button'));
                });
                inputToSubmit.value = finalListValues;
            }
            if (ul.querySelectorAll('li').length === 0) {
                ul.classList.remove('show');
            }
            error.innerHTML = '';
        }

        function _triggerMultiInput(e) {
            if (e.keyCode === EDC.props.enterKeyCode) {
                e.preventDefault();
                element.querySelector('.no-btn').click();
            }
        }

        function _addItem(e) {
            var addedSection = element.querySelector('ul'),
                inputToSubmit = element.querySelector('.inputs-list'),
                itemsList = addedSection.querySelectorAll('li'),
                finalListValues = '',
                li,
                buttonImg = addedSection.getAttribute('data-button-image'),
                btn = '<button type="button" class="no-btn"><span class="remove-item"><img src="' + buttonImg + '"/></span></button>';

            e.preventDefault();
            isItemsLimitExcedeed = itemsList.length >= maxItemsAllowed;

            function _checkIfExists(inputValue) {
                var validValue = true;

                if (itemsList.length > 0) {
                    itemsList.forEach(function (item) {
                        if (item.innerHTML.substring(0, item.innerHTML.indexOf('<button')) === inputValue) {
                            validValue = false;
                        }
                    });
                }
                return validValue;
            }

            if (e.type === 'click' || (e.type === 'keyup') && e.keyCode === EDC.props.enterKeyCode) {
                if (isItemsLimitExcedeed || addItemInput.value === '' || !_checkIfExists(addItemInput.value) || (isEmail && !EDC.forms.validationRules.email(addItemInput.value))) {
                    if (isItemsLimitExcedeed) {
                        error.innerHTML = error.getAttribute('data-max-items');
                    } else if (addItemInput.value === '') {
                        error.innerHTML = error.getAttribute('data-req-message');
                    } else if (!_checkIfExists(addItemInput.value)) {
                        error.innerHTML = error.getAttribute('data-already-added-message');
                    } else if (!EDC.forms.validationRules.email(addItemInput.value)) {
                        error.innerHTML = error.getAttribute('data-email-message');
                        addItemInput.classList.add('error');
                    }
                    if (!element.classList.contains('error')) {
                        element.classList.add('error');
                    }
                    addItemInput.classList.add('error');
                } else {
                    li = document.createElement('li');
                    error.innerHTML = '';
                    if (element.classList.contains('error')) {
                        element.classList.remove('error');
                    }
                    if (addItemInput.classList.contains('error')) {
                        addItemInput.classList.remove('error');
                    }
                    li.innerHTML = addItemInput.value + btn;
                    EDC.utils.attachEvents(li.querySelector('button'), 'click', _removeItem);
                    addedSection.appendChild(li);
                    itemsList = addedSection.querySelectorAll('li');
                    itemsList.forEach(function (item, key) {
                        if (key > 0 && itemsList.length > key) {
                            finalListValues += delimeter;
                        }
                        finalListValues += item.innerHTML.substring(0, item.innerHTML.indexOf('<button'));
                    });
                    inputToSubmit.value = finalListValues;
                    addItemInput.value = '';
                    _cleanItem();
                    if (!addedSection.classList.contains('show')) {
                        addedSection.classList.add('show');
                    }
                }
                addItemInput.focus();
            }
        }

        // Attach events
        function _attachEvents() {
            var addItemBtn = element.querySelector('.no-btn');

            EDC.utils.attachEvents(addItemBtn, 'click', _addItem);
            EDC.utils.attachEvents(addItemBtn, 'keypress', _triggerMultiInput);
            EDC.utils.attachEvents(addItemInput, 'keypress', _triggerMultiInput);
            EDC.utils.attachEvents(addItemInput, 'blur', _cleanItem);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var multiInput = document.querySelectorAll('.c-multi-input:not([data-component-state="initialized"])');

        if (multiInput) {
            multiInput.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', multiInputJS.init);