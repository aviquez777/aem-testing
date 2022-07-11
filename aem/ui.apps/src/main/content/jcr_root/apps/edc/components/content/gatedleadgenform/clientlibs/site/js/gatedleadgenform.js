var GatedLeadGenForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var eSectionForm = element.querySelector('form.email-section'),
            fSectionForm = element.querySelector('form.form-section'),
            eSectionFormElements = eSectionForm.elements,
            fSectionFormElements = fSectionForm.elements,
            submitFailedMessage = element.querySelector('.submit-failed-message'),
            errorText = submitFailedMessage.querySelector('p').getAttribute('data-default-text'),
            processingScreen = element.querySelector('.c-processing-screen'),
            eSectionBtn = eSectionForm.querySelector('button[type="submit"]'),
            fSectionBtn = fSectionForm.querySelector('button[type="submit"]'),
            eSectionSubmitUrl = eSectionForm.getAttribute('action'),
            fSectionSubmitUrl = fSectionForm.getAttribute('action'),
            emailAddressField = eSectionForm ? eSectionForm.querySelector('input[name="emailAddress"]') : null;

        // Private methods

        // Data Layer
        function _trackEvent(e, type) {
            var el = e.target,
                form = el.closest('form'),
                obj = {
                    eventInfo: {
                        eventComponent: form.dataset.eventComponent,
                        eventType: form.dataset.eventType,
                        eventName: form.dataset.eventName,
                        eventAction: form.dataset.eventAction,
                        eventText: form.querySelector('button[type="submit"]').getAttribute('data-english-text').toLowerCase(),
                        destinationPage: EDC.props.pageNameURL,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            if (type === 'formSubmit') {
                obj.eventInfo.engagementType = form.dataset.engagementType;
                obj.eventInfo.eventLevel = form.dataset.eventLevel;
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // Pre-populate the email field if the user is Logged in
        function _prePopulateEmailField() {
            EDC.utils.addLoggedInUserEmailToField(emailAddressField);
        }

        function _handleForm(data) {
            var fieldsCounter = 0;

            if (data.fields && data.fields.length > 0) {
                fSectionForm.querySelectorAll('.form-group .form-control').forEach(function (formControl) {
                    var isDD = formControl.classList.contains('dropdown'),
                        field = isDD ? formControl.querySelector('select') : formControl,
                        nestedField = field.querySelector('input[name]'),
                        fieldName = nestedField ? nestedField.name : field.name,
                        parent = field.closest('.form-group.hide'),
                        grandParent = parent ? parent.closest('.form-row') : null;

                    data.fields.forEach(function (name) {
                        if (fieldName === name && parent) {
                            parent.classList.remove('hide');
                            fieldsCounter++;
                        }
                    });

                    if (parent && parent.parentNode && parent.classList.contains('hide')) {
                        parent.parentNode.removeChild(parent);
                    }

                    if (grandParent) {
                        grandParent.classList[grandParent.querySelectorAll('.form-group').length <= 1 ? 'add' : 'remove']('single-element');
                    }

                    if (grandParent && grandParent.querySelectorAll('.form-group').length === 0) {
                        grandParent.parentNode.removeChild(grandParent);
                    }
                });
            } else if (data.redirect) {
                window.location.href = data.redirect;
            }

            if (fieldsCounter === 0 && data.redirect) {
                window.location.href = data.redirect;
            }
        }

        function _handleFormSubmitError(form, btn, error) {
            submitFailedMessage.querySelector('p').textContent = error;
            submitFailedMessage.classList.remove('hide');
            form.classList.remove('hide');
            btn.removeAttribute('disabled');
            EDC.utils.scrollTo('top', EDC.utils.getPosition(submitFailedMessage).y - 400);
        }

        function _showProcessingScreen(form, btn) {
            EDC.forms.disableSubmit(btn);
            form.classList.add('hide');
            submitFailedMessage.classList.add('hide');
            element.classList.add('processing');
            EDC.utils.scrollTo('top', EDC.utils.getPosition(processingScreen).y - 200);
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
            e.preventDefault();
            EDC.forms.validateForm(eSectionFormElements);

            if (eSectionSubmitUrl && !element.querySelectorAll('.form-control.error').length) {
                _showProcessingScreen(eSectionForm, eSectionBtn);
                EDC.utils.fetchJSON('GET', eSectionSubmitUrl, EDC.forms.getFormData(eSectionForm), function (data) {
                    if (data && !data.errorText) {
                        _handleForm(data);
                        fSectionForm.classList.remove('hide');
                        fSectionForm.querySelector('input[name="emailAddress"]').value = eSectionForm.querySelector('input[name="emailAddress"]').value;
                        _trackEvent(e, 'emailSubmit');
                    } else {
                        _handleFormSubmitError(eSectionForm, eSectionBtn, data.errorText);
                    }

                    element.classList.remove('processing');
                }, function () {
                    _handleFormSubmitError(eSectionForm, eSectionBtn, errorText);
                    element.classList.remove('processing');
                });
            } else {
                _errorFieldFocus();
            }
        }

        function _validateSubmit(e) {
            e.preventDefault();
            EDC.forms.validateForm(fSectionFormElements);

            if (!element.querySelectorAll('.form-group:not(.hide) .form-control.error, .form-group:not(.hide) .radio-group.error').length) {
                _showProcessingScreen(fSectionForm, fSectionBtn);

                EDC.utils.fetchJSON('POST', fSectionSubmitUrl, EDC.forms.getFormData(fSectionForm), function (data) {
                    if (data.errorText) {
                        _handleFormSubmitError(fSectionForm, fSectionBtn, data.errorText);
                        element.classList.remove('processing');
                    } else if (data.redirect) {
                        fSectionForm.setAttribute('data-destination-page', data.redirect);
                        _trackEvent(e, 'formSubmit');
                        window.location.href = data.redirect;
                    }
                }, function () {
                    _handleFormSubmitError(fSectionForm, fSectionBtn, errorText);
                });
            } else {
                _errorFieldFocus();
            }
        }

        function _singleDropdownChanged(e) {
            var el = e.target,
                name = el.name,
                value = el.querySelectorAll('option')[el.selectedIndex].value.toLowerCase(),
                parent = el.closest('.form-group'),
                otherSection = parent.querySelector('.other-section'),
                otherInput = otherSection.querySelector('input');

            if (value === 'other') {
                otherInput.name = name;

                if (!el.getAttribute('original-name')) {
                    el.setAttribute('original-name', name);
                }

                otherInput.setAttribute('required', 'required');
                el.name = name + 'Old';
                otherSection.classList.add('show');
            } else if (el.getAttribute('original-name')) {
                otherSection.querySelector('input').name = '';
                el.name = el.getAttribute('original-name');
                otherSection.classList.remove('show');
                otherInput.removeAttribute('required');
            }
        }

        function _attachEvents() {
            var country = element.querySelector('.country select'),
                provinceSelect = element.querySelector('.province .can select'),
                stateSelect = element.querySelector('.province .usa select'),
                provinceInput = element.querySelector('.province input'),
                provinceLabel = element.querySelector('.province > label'),
                otherDropdowns = fSectionForm.querySelectorAll('.form-group.has-other select');

            EDC.utils.attachEvents(eSectionForm, 'submit', _checkEmailAndGetFormData);
            EDC.utils.attachEvents(fSectionForm, 'submit', _validateSubmit);
            EDC.utils.attachEvents(otherDropdowns, 'change', _singleDropdownChanged);

            if (country) {
                EDC.utils.attachEvents(country, 'change', function () {
                    EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel);
                });
            }
        }

        _prePopulateEmailField();
        _attachEvents();
        EDC.forms.validateChange(eSectionFormElements);
        EDC.forms.validateChange(fSectionFormElements);
        EDC.forms.fillHiddenFields(element);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var gatedLeadGenForms = document.querySelectorAll('.c-gated-lead-gen-form:not([data-component-state="initialized"])');

        if (gatedLeadGenForms) {
            gatedLeadGenForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', GatedLeadGenForm.init);