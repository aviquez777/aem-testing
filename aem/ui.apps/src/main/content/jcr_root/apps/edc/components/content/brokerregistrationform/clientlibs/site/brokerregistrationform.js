var brokerRegistrationForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var step1 = element.querySelector('.step-1'),
            step2 = element.querySelector('.step-2'),
            step1SubmitBtn = step1 ? step1.querySelector('.actions button') : null,
            step2SubmitBtn = step2 ? step2.querySelector('.actions button') : null,
            step2Form = step2 ? step2.querySelector('form') : null,
            formElements = step2Form ? step2Form.elements : null,
            formRows = step2 ? step2.querySelectorAll('.form-row') : null,
            processingScreen = element.querySelector('.c-processing-screen');

        // Data Layer
        function _trackEvent() {
            var form = element.querySelector('.step-2 form'),
                formBtnSubmit = form ? form.querySelector('button[type="submit"]') : null,
                formInputDocID = form ? form.querySelector('input[name="docID"]') : null,
                formInputCheckbox = form ? form.querySelector('input#caslConsent') : null,
                isFormInputCheckboxChecked = formInputCheckbox ? formInputCheckbox.checked : false,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: element.dataset.eventName,
                        eventAction: element.dataset.eventAction,
                        eventText: formBtnSubmit ? formBtnSubmit.textContent : '',
                        eventValue: formInputDocID ? formInputDocID.value : '',
                        eventValue2: isFormInputCheckboxChecked ? 'casl consent provided' : 'casl consent not provided',
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: element.dataset.eventDestinationPage,
                        engagementType: element.dataset.eventEngagement,
                        eventLevel: element.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        // Private methods
        function _validateSubmit(e) {
            var submitURL = '',
                errorField,
                formStep1 = step1 ? step1.querySelector('form') : null,
                form = step2 ? step2.querySelector('form') : null,
                pageField = element.querySelectorAll('.form-group .form-control, .form-row .form-control, input[type=hidden]'),
                submitFailedMessage = form ? form.querySelector('.submit-failed-message') : null,
                formErrorStep1,
                formErrorStep2,
                formContent = form ? form.querySelector('.content') : null;

            if (e) {
                e.preventDefault();
            }

            if (pageField) {
                EDC.forms.validateForm(pageField);
                EDC.forms.validateChange(pageField);
            }

            if (formStep1) {
                formErrorStep1 = formStep1.querySelectorAll('input.error, select.error, textarea.error, checkbox.error');
            }

            if (form) {
                formErrorStep2 = form.querySelectorAll('input.error, select.error, textarea.error, checkbox.error');
            }

            if (formErrorStep1 && !formErrorStep1.length && pageField) {
                EDC.forms.validateChange(pageField);
                if (formErrorStep2 && !formErrorStep2.length && form) {
                    submitURL = form.getAttribute('action');

                    if (formContent) {
                        formContent.classList.add('hide');
                    }

                    element.classList.add('processing');

                    if (processingScreen) {
                        EDC.utils.scrollTo('top', EDC.utils.getPosition(processingScreen).y - 200);
                    }

                    if (submitURL) {
                        EDC.utils.fetchJSON('POST', submitURL, EDC.forms.getFormData(form), function () {
                            // Removes the error message from screen if exists
                            if (submitFailedMessage && !submitFailedMessage.classList.contains('hide')) {
                                submitFailedMessage.classList.add('hide');
                            }

                            element.classList.remove('processing');
                            element.classList.add('processed');

                            // Tracking
                            _trackEvent();
                        }, function () {
                            // Error on server communication, delay or related issues
                            if (submitFailedMessage && step2SubmitBtn && formContent) {
                                element.classList.remove('processing');
                                submitFailedMessage.classList.remove('hide');
                                formContent.classList.remove('hide');
                                EDC.forms.enableSubmit(step2SubmitBtn);
                                EDC.utils.scrollTo('top', EDC.utils.getPosition(submitFailedMessage).y - 400);
                            }
                        }, false);
                    }
                } else {
                    // Handling invalid form
                    errorField = element.querySelector('.form-control.error');
                    if (errorField) {
                        errorField.focus();
                    }
                }
            }
        }

        function _showStep2() {
            var pageField = step1 ? step1.querySelectorAll('.form-group .form-control, .form-row .form-control, input[type=hidden]') : null,
                errorMessage = step1 ? step1.querySelector('.error') : null,
                form = step1 ? step1.querySelector('form') : null,
                formErrors;

            if (pageField) {
                EDC.forms.validateForm(pageField);
                EDC.forms.validateChange(pageField);
            }

            if (form) {
                formErrors = form.querySelectorAll('input.error');
            }

            if (formErrors && !formErrors.length && step1 && step2) {
                step1.classList.add('hide');
                step2.classList.remove('error');
                if (step2.classList.contains('hide')) {
                    if (errorMessage && !errorMessage.classList.contains('hide')) {
                        errorMessage.classList.add('hide');
                    }
                    step2.classList.remove('hide');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(step2).y - 200);
                }
            }
        }

        function _attachEvents() {
            if (step1SubmitBtn && step2SubmitBtn) {
                EDC.utils.attachEvents(step2SubmitBtn, 'click', _validateSubmit);
                EDC.utils.attachEvents(step1SubmitBtn, 'click', _showStep2);
            }

            if (formRows) {
                EDC.utils.attachEvents(window, 'resize', function () {
                    EDC.forms.setHeightLabels(formRows);
                });
            }

            if (formElements) {
                EDC.forms.validateChange(formElements);
            }
        }

        _attachEvents();
        EDC.forms.fillHiddenFields(element);

        if (formRows) {
            EDC.forms.setHeightLabelsOnLoad(formRows);
        }

        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var brokerRegistrationForms = document.querySelectorAll('.c-broker-registration:not([data-component-state="initialized"])');

        if (brokerRegistrationForms) {
            brokerRegistrationForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', brokerRegistrationForm.init);