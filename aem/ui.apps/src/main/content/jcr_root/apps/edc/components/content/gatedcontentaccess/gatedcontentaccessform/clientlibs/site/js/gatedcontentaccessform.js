var GatedContentAccessForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var eSectionForm = element.querySelector('form.email-section'),
            fSectionForm = element.querySelector('form.form-section'),
            eSectionFormElements = eSectionForm ? eSectionForm.elements : null,
            fSectionFormElements = fSectionForm ? fSectionForm.elements : null,
            submitFailedMessage = element.querySelector('.submit-failed-message'),
            errorText = submitFailedMessage ? submitFailedMessage.querySelector('p').getAttribute('data-default-text') : null,
            processingScreen = element.querySelector('.c-processing-screen'),
            eSectionBtn = eSectionForm ? eSectionForm.querySelector('button[type="submit"]') : null,
            fSectionBtn = fSectionForm ? fSectionForm.querySelector('button[type="submit"]') : null,
            eSectionSubmitUrl = eSectionForm ? eSectionForm.getAttribute('action') : null,
            fSectionSubmitUrl = fSectionForm ? fSectionForm.getAttribute('action') : null,
            eEmailAddressField = eSectionForm ? eSectionForm.querySelector('input[name="emailAddress"]') : null,
            fEmailAddressField = fSectionForm ? fSectionForm.querySelector('input[name="emailAddress"]') : null;

        // Private methods

        // Data Layer
        function _trackEvent(e, type) {
            var el = e ? e.target : null,
                form = el ? el.closest('form') : null,
                submitButton = form ? form.querySelector('button[type="submit"]') : null,
                buttonTextAtt = submitButton ? submitButton.getAttribute('data-english-text') : null,
                obj = {
                    eventInfo: {
                        eventComponent: form ? form.dataset.eventComponent : null,
                        eventType: form ? form.dataset.eventType : null,
                        eventName: form ? form.dataset.eventName : null,
                        eventAction: form ? form.dataset.eventAction : null,
                        eventText: buttonTextAtt ? buttonTextAtt.toLowerCase() : null,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            if (type === 'emailSubmit') {
                obj.eventInfo.destinationPage = EDC.props.pageNameURL;
            } else if (type === 'formSubmit') {
                obj.eventInfo.engagementType = form ? form.dataset.engagementType : null;
                obj.eventInfo.eventLevel = form ? form.dataset.eventLevel : null;
                if (form && form.dataset.destinationPage) {
                    obj.eventInfo.destinationPage = form ? form.dataset.destinationPage : null;
                }
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // Pre-populate the email field if the user is Logged in
        function _prePopulateEmailFields() {
            if (eEmailAddressField && fEmailAddressField) {
                EDC.utils.addLoggedInUserEmailToField(eEmailAddressField);
                EDC.utils.addLoggedInUserEmailToField(fEmailAddressField);
            }
        }

        function _handleForm(data) {
            var fieldsCounter = 0,
                fSectionFormItems = fSectionForm ? fSectionForm.querySelectorAll('.form-group .form-control') : null;

            if (data) {
                if (data.fields && data.fields.length > 0 && fSectionFormItems && fSectionFormItems.length > 0) {
                    fSectionFormItems.forEach(function (formControl) {
                        var isDD = formControl.classList.contains('dropdown'),
                            field = isDD ? formControl.querySelector('select') : formControl,
                            parent = field ? field.closest('.form-group.hide') : null,
                            grandParent = parent ? parent.closest('.form-row') : null;

                        data.fields.forEach(function (name) {
                            if (field.name === name && parent) {
                                parent.classList.remove('hide');
                                fieldsCounter++;
                            }
                        });

                        if (grandParent) {
                            grandParent.classList[fieldsCounter <= 1 ? 'add' : 'remove']('single-element');
                        }

                        if (parent && parent.parentNode && parent.classList.contains('hide')) {
                            parent.parentNode.removeChild(parent);
                        }
                    });
                } else if (data.redirect) {
                    window.location.href = data.redirect;
                }
            }

            if (fieldsCounter === 0 && data.redirect) {
                window.location.href = data.redirect;
            }
        }

        function _handleFormSubmitError(form, btn, error) {
            var pElement = submitFailedMessage ? submitFailedMessage.querySelector('p') : null;

            if (submitFailedMessage && form && btn && error && pElement) {
                pElement.textContent = error;
                submitFailedMessage.classList.remove('hide');
                form.classList.remove('hide');
                btn.removeAttribute('disabled');
                EDC.utils.scrollTo('top', EDC.utils.getPosition(submitFailedMessage).y - 400);
            }
        }

        function _showProcessingScreen(form, btn) {
            if (form && btn && submitFailedMessage && processingScreen) {
                EDC.forms.disableSubmit(btn);
                form.classList.add('hide');
                submitFailedMessage.classList.add('hide');
                element.classList.add('processing');
                EDC.utils.scrollTo('top', EDC.utils.getPosition(processingScreen).y - 200);
            }
        }

        function _errorFieldFocus() {
            var field = element.querySelector('.form-control.error, .radio-group.error');

            if (field) {
                if (field.classList.contains('radio-group')) {
                    field.querySelector('input').focus();
                } else {
                    field.focus();
                }
            }
        }

        function _checkEmailAndGetFormData(e) {
            var formErrors;

            e.preventDefault();

            if (eSectionFormElements) {
                EDC.forms.validateForm(eSectionFormElements);
            }

            formErrors = element.querySelectorAll('.form-control.error');

            if (eSectionSubmitUrl && formErrors && !formErrors.length && eSectionForm && eSectionBtn) {
                _showProcessingScreen(eSectionForm, eSectionBtn);
                EDC.utils.fetchJSON('GET', eSectionSubmitUrl, EDC.forms.getFormData(eSectionForm), function (data) {
                    if (data) {
                        if (!data.errorText && fSectionForm) {
                            _handleForm(data);
                            fSectionForm.classList.remove('hide');
                            fSectionForm.querySelector('input[name="emailAddress"]').value = eSectionForm.querySelector('input[name="emailAddress"]').value;
                            _trackEvent(e, 'emailSubmit');
                        } else {
                            _handleFormSubmitError(eSectionForm, eSectionBtn, data.errorText);
                        }
                    }
                    element.classList.remove('processing');
                }, function () {
                    _handleFormSubmitError(eSectionForm, eSectionBtn, errorText);
                });
            } else {
                _errorFieldFocus();
            }
        }

        function _validateSubmit(e) {
            var formErrors;

            e.preventDefault();

            if (fSectionFormElements) {
                EDC.forms.validateForm(fSectionFormElements);
            }

            formErrors = element.querySelectorAll('.form-group:not(.hide) .form-control.error, .form-group:not(.hide) .radio-group.error');

            if (formErrors && !formErrors.length && fSectionForm && fSectionBtn && fSectionSubmitUrl) {
                _showProcessingScreen(fSectionForm, fSectionBtn);
                EDC.utils.fetchJSON('POST', fSectionSubmitUrl, EDC.forms.getFormData(fSectionForm), function (data) {
                    if (data) {
                        if (data.errorText) {
                            _handleFormSubmitError(fSectionForm, fSectionBtn, data.errorText);
                            element.classList.remove('processing');
                        } else if (data.redirect) {
                            fSectionForm.setAttribute('data-destination-page', data.redirect);
                            _trackEvent(e, 'formSubmit');
                            window.location.href = data.redirect;
                        }
                    }
                }, function () {
                    _handleFormSubmitError(fSectionForm, fSectionBtn, errorText);
                });
            } else {
                _errorFieldFocus();
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(eSectionForm, 'submit', _checkEmailAndGetFormData);
            EDC.utils.attachEvents(fSectionForm, 'submit', _validateSubmit);
        }

        _prePopulateEmailFields();
        _attachEvents();

        if (eSectionFormElements) {
            EDC.forms.validateChange(eSectionFormElements);
        }

        if (fSectionFormElements) {
            EDC.forms.validateChange(fSectionFormElements);
        }

        EDC.forms.fillHiddenFields(element);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var gatedContentAccessForms = document.querySelectorAll('.c-gated-content-access-form:not([data-component-state="initialized"])');

        if (gatedContentAccessForms) {
            gatedContentAccessForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', GatedContentAccessForm.init);