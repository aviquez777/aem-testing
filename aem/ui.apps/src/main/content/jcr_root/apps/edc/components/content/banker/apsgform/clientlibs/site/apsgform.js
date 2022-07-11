var apsgForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var formElements = element.elements,
            formRows = element.querySelectorAll('.form-row'),
            addEmailInput = element.querySelector('.c-multi-input .input-group input'),
            radios = [],
            finalList = [],
            processingScreen = element.querySelector('.c-processing-screen');

        // Data Layer
        // Calculate date difference in days
        function _dateDiff(elem) {
            var today = new Date(),
                hours = today.getHours(),
                minutes = today.getMinutes(),
                seconds = today.getSeconds(),
                reqDueDateField = elem.querySelector('input[name="requestedDueDate"]'),
                reqDueDateVal = reqDueDateField ? reqDueDateField.value : null,
                reqDueDate = reqDueDateVal ? new Date(reqDueDateVal) : null;

            if (reqDueDate) {
                reqDueDate.setHours(hours);
                reqDueDate.setMinutes(minutes);
                reqDueDate.setSeconds(seconds);
                return Math.round((reqDueDate - today) / 86400000) || '';
            }

            return false;
        }

        function _trackEvent(e) {
            var el = e.target,
                bankField = el.querySelector('select[name="bank"]'),
                bankName = bankField ? bankField.value : null,
                reqForCoverField = el.querySelector('input[name="requestforCover"]'),
                reqForCover = reqForCoverField ? reqForCoverField.value : null,
                expiryRequestField = el.querySelector('input[name="expiryRequest"]'),
                expiryRequest = expiryRequestField ? expiryRequestField.value : null,
                value3 = bankName && reqForCover && expiryRequest ? (bankName + ' | ' + reqForCover + ' | ' + _dateDiff(el) + ' | ' + expiryRequest) : null,
                obj,
                submitBtn = el.querySelector('button[type="submit"]'),
                docId = el.querySelector('input[name="docID"]'),
                newslettersCheckbox = el.querySelector('input.receive-newsletters');

            if (el) {
                obj = {
                    eventInfo: {
                        eventComponent: el.dataset.eventComponent ? el.dataset.eventComponent : '',
                        eventType: el.dataset.eventType ? el.dataset.eventType : '',
                        eventName: el.dataset.eventName ? el.dataset.eventName : '',
                        eventAction: el.dataset.eventAction ? el.dataset.eventAction : '',
                        eventText: submitBtn ? submitBtn.textContent : '',
                        eventValue: docId ? docId.value : '',
                        eventValue2: newslettersCheckbox && newslettersCheckbox.checked ? 'casl consent provided' : 'casl consent not provided',
                        eventValue3: value3 ? value3.toLowerCase() : '',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: el.getAttribute('action'),
                        engagementType: el.dataset.eventEngagement ? el.dataset.eventEngagement : '',
                        eventLevel: el.dataset.eventLevel ? el.dataset.eventLevel : ''
                    }
                };

                EDC.utils.dataLayerTracking(obj);
            }
        }

        // Private methods
        function _validateSubmit(e) {
            var submitURL = '',
                submitBtn = element.querySelector('button[type="submit"]'),
                submitFailedMessage = element.querySelector('.submit-failed-message'),
                errorField,
                inputsToSubmit = [],
                formData;

            e.preventDefault();

            formElements.forEach(function (input) {
                if (!input.classList.contains('discard-input') && !input.classList.contains('no-btn')) {
                    inputsToSubmit.push(input);
                }
            });

            if (inputsToSubmit.length) {
                EDC.forms.validateForm(inputsToSubmit);
            }

            if (!element.querySelectorAll('.form-control.error, input.error, select.error, textarea.error').length) {
                // Handling valid form
                submitURL = element.getAttribute('action');
                formData = new FormData(element);

                if (submitBtn) {
                    EDC.forms.disableSubmit(submitBtn);
                }

                if (processingScreen) {
                    element.classList.add('processing');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(processingScreen).y - 200);
                }

                EDC.utils.fetchJSON('POST', submitURL, formData, function () {
                    var contentEl = element.querySelector('.content');

                    // Removes the error message from screen if exists
                    if (submitFailedMessage) {
                        submitFailedMessage.classList.add('hide');
                    }

                    if (processingScreen) {
                        element.classList.remove('processing');
                        element.classList.add('processed');
                    }

                    if (contentEl) {
                        contentEl.classList.add('hide');
                    }

                    _trackEvent(e);
                }, function () {
                    // Error on server communication, delay or related issues
                    if (submitFailedMessage) {
                        if (processingScreen) {
                            element.classList.remove('processing');
                        }

                        if (submitFailedMessage) {
                            submitFailedMessage.classList.remove('hide');
                        }

                        if (submitBtn) {
                            EDC.forms.enableSubmit(submitBtn);
                        }
                    }
                }, true);
            } else {
                // Handling invalid form
                addEmailInput.value = '';
                errorField = element.querySelector('.form-control.error');

                if (errorField) {
                    errorField.focus();
                }
            }
        }

        function _dropdownChanged(e) {
            var otherBankInput = element.querySelector('.other-bank'),
                eTarget = e.target,
                eTargetValue = eTarget ? eTarget.value : null,
                otherBankInputField = otherBankInput.querySelector('input');

            if (otherBankInput && eTargetValue && otherBankInputField) {
                if (eTargetValue === 'Other') {
                    otherBankInput.classList.add('show');
                    otherBankInputField.setAttribute('required', 'required');
                    otherBankInputField.setAttribute('capitalize', 'true');
                } else if (eTargetValue !== 'Other') {
                    if (otherBankInput.classList.contains('show')) {
                        otherBankInput.classList.remove('show');
                        otherBankInputField.removeAttribute('required');
                        otherBankInputField.removeAttribute('capitalize');
                    }
                }
            }
        }

        function _radiosChanged(e) {
            var radioChanged = e.target,
                elToToggle = element.querySelector('.' + radioChanged.dataset.showValue) ? element.querySelector('.' + radioChanged.dataset.showValue) : null;

            e.preventDefault();

            if (elToToggle) {
                if (radioChanged && radioChanged.value === 'yes') {
                    if (!elToToggle.classList.contains('show')) {
                        elToToggle.classList.add('show');
                        if (elToToggle.querySelector('input')) {
                            elToToggle.querySelector('input').setAttribute('required', 'required');
                        }
                    }
                } else if (radioChanged && radioChanged.value === 'no') {
                    if (elToToggle.classList.contains('show')) {
                        elToToggle.classList.remove('show');
                        if (elToToggle.querySelector('input')) {
                            elToToggle.querySelector('input').removeAttribute('required');
                        }
                    }
                }
            }
        }

        // -------------------------------------------
        // STARTS - Date Picker Section
        // -------------------------------------------
        function _compareDate() {
            var currentDate = new Date(),
                etOffset = (EDC.forms.isDLS(currentDate) ? -4 : -5) * 60,
                utcOffset = currentDate.getTimezoneOffset();

            currentDate.setMinutes(currentDate.getMinutes() + etOffset);
            currentDate.setMinutes(currentDate.getMinutes() + utcOffset);
        }

        function _initializeLibraries() {
            var datePickerCreated = [],
                currentLanguage = element.getAttribute('data-lang');

            if (currentLanguage) {
                element.querySelectorAll('.preferredDate input[name="requestedDueDate"]').forEach(function (date, index) {
                    datePickerCreated[index] = new datepicker('dp' + index, date, true, currentLanguage, _compareDate);
                    EDC.utils.attachEvents(date, ['click', 'focus'], function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                        datePickerCreated[index].showDlg();
                    });
                });
            }
        }
        // -------------------------------------------
        // ENDS - Date Picker Section
        // -------------------------------------------

        function _attachEvents() {
            var bankDd = element.querySelector('.bank-section .dropdown');

            radios.forEach(function (input) {
                EDC.utils.attachEvents(input, 'change', _radiosChanged);
            });

            if (finalList) {
                EDC.forms.validateChange(finalList);
            }

            if (bankDd) {
                EDC.utils.attachEvents(bankDd, 'change', _dropdownChanged); // Dropdown event attachment
            }

            if (formRows) {
                EDC.utils.attachEvents(window, 'resize', function () {
                    EDC.forms.setHeightLabels(formRows);
                });
            }

            EDC.utils.attachEvents(element, 'submit', _validateSubmit); // Submit event attachment
        }

        function _initialCode() {
            formElements.forEach(function (input) {
                if (!input.classList.contains('discard-input') && !input.classList.contains('no-btn')) {
                    finalList.push(input);
                }
            });

            finalList.forEach(function (input) {
                if (input.type === 'radio') {
                    radios.push(input);
                }
            });
        }

        _initialCode();
        _initializeLibraries();
        _attachEvents();
        EDC.forms.fillHiddenFields(element);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var apsgForms = document.querySelectorAll('form.c-apsg-form:not([data-component-state="initialized"])');

        if (apsgForms) {
            apsgForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', apsgForm.init);