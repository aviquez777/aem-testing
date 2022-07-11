var ShareYourDetailsForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            formHeader = element.querySelector('.form-header'),
            eSectionForm = element.querySelector('form.email-section'),
            fSectionForm = element.querySelector('form.form-section'),
            eSectionFormElements = eSectionForm.elements,
            fSectionFormElements = fSectionForm.elements,
            fSectionTitle = fSectionForm.querySelector('h3'),
            submitFailedMessage = element.querySelector('.submit-failed-message'),
            submitSuccessMessage = element.querySelector('.submit-success-message'),
            errorText = submitFailedMessage.querySelector('p').getAttribute('data-default-text'),
            processingScreen = element.querySelector('.c-processing-screen'),
            eSectionBtn = eSectionForm.querySelector('button[type="submit"]'),
            fSectionBtn = fSectionForm.querySelector('button[type="submit"]'),
            eSectionSubmitUrl = eSectionForm.getAttribute('action'),
            fSectionSubmitUrl = fSectionForm.getAttribute('action'),
            sciCalculator = element.closest('.c-sci-quote-calculator'),
            disclaimerEl = sciCalculator ? sciCalculator.querySelector('.sci-quote-calculator-disclaimer') : null,
            validAmountEl = sciCalculator ? sciCalculator.querySelector('.valid-amount') : null,
            notificationsValid = sciCalculator ? sciCalculator.querySelector('.notifications.valid') : null,
            noteDisclaimer = validAmountEl ? validAmountEl.querySelector('.note.disclaimer') : null,
            actions = disclaimerEl ? disclaimerEl.querySelector('.actions') : null,
            divs = sciCalculator ? sciCalculator.querySelectorAll('.calculator-container .content > div') : null,
            dataVersion = 'a',
            sciResetBtn = sciCalculator ? sciCalculator.querySelector('.actions .reset') : null,
            formResetBtn = fSectionForm.querySelector('.actions button[type="button"]');

        // Private methods

        // Data Layer
        function _trackEvent(e, type) {
            var el = e.target,
                form = el.closest('form'),
                docID = form.querySelector('[name="docID"]'),
                dd1 = fSectionForm.querySelector('.dropdown [name="tradeStatus"]'),
                dd2 = fSectionForm.querySelector('.dropdown [name="painPoint"]'),
                dd3 = fSectionForm.querySelector('.dropdown [name="annualSales"]'),
                dd4 = fSectionForm.querySelector('.dropdown [name="salesTiming"]'),
                obj = {
                    eventInfo: {
                        eventComponent: sciCalculator ? sciCalculator.dataset.eventComponent : '',
                        eventType: sciCalculator ? sciCalculator.dataset.eventType : '',
                        eventName: form.dataset.eventName,
                        eventAction: sciCalculator ? sciCalculator.dataset.eventAction : '',
                        eventText: form.querySelector('button[type="submit"]').getAttribute('data-english-text').toLowerCase(),
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: EDC.props.pageNameURL
                    }
                };

            if (type === 'formSubmit') {
                obj.eventInfo.eventValue = docID ? docID.value : '';

                if (dd1) {
                    obj.eventInfo.eventValue2 = EDC.forms.getOptionsSelected(dd1);
                }

                if (dd2) {
                    obj.eventInfo.eventValue3 = EDC.forms.getOptionsSelected(dd2);
                }

                if (dd3) {
                    obj.eventInfo.eventValue4 = EDC.forms.getOptionsSelected(dd3);
                }

                if (dd4) {
                    obj.eventInfo.eventValue5 = EDC.forms.getOptionsSelected(dd4);
                }
            }

            EDC.utils.dataLayerTracking(obj);
        }

        function _handleForm(data) {
            var fieldsCounter = 0;

            if (data.fields && data.fields.length > 0) {
                fSectionForm.querySelectorAll('.form-group .form-control').forEach(function (formControl) {
                    var isDD = formControl.classList.contains('dropdown'),
                        field = isDD ? formControl.querySelector('select') : formControl,
                        parent = field.closest('.form-group.hide'),
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

                    if (parent && (field.name === 'annualSales' || field.name === 'salesTiming')) {
                        parent.classList.remove('hide');
                    }

                    if (parent && parent.parentNode && parent.classList.contains('hide')) {
                        parent.parentNode.removeChild(parent);
                    }
                });
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

        function toggleCalculatorFields(action) {
            var condition = action === 'hide';

            divs.forEach(function (div, index) {
                var span,
                    value,
                    dataPlacement,
                    dataValue;

                if (condition ? (div.getAttribute('class').indexOf('-selected') > -1) : (div.getAttribute('class').indexOf('-selected') === -1)) {
                    if (condition) {
                        span = div.querySelector('span');
                        value = divs[index - 1].querySelectorAll('input, select')[0].value;
                        dataPlacement = span.getAttribute('data-placement');
                        dataValue = span.getAttribute('data-value');
                        span.textContent = (dataPlacement === 'start' ? dataValue : '') + value + (dataPlacement === 'end' ? (' ' + dataValue) : '');

                        if (sciCalculator) {
                            sciCalculator.querySelector('.content').classList.add('items-selected');
                        }
                    }

                    div.classList.add('show');
                    sciResetBtn.classList.add('show');
                } else {
                    div.classList.remove('show');

                    if (sciCalculator) {
                        sciCalculator.querySelector('.content').classList.remove('items-selected');
                    }
                }
            });
        }

        function _checkEmailAndGetFormData(e) {
            e.preventDefault();
            EDC.forms.validateForm(eSectionFormElements);

            if (sciCalculator) {
                dataVersion = sciCalculator.getAttribute('data-version').toLowerCase();
            }

            if (eSectionSubmitUrl && !element.querySelectorAll('.form-control.error').length) {
                _showProcessingScreen(eSectionForm, eSectionBtn);
                EDC.utils.fetchJSON('GET', eSectionSubmitUrl, EDC.forms.getFormData(eSectionForm), function (data) {
                    var inquiryIdField = element.querySelector('input[name="inquiryID"]');

                    if (data && !data.errorText) {
                        _handleForm(data);
                        fSectionForm.classList.remove('hide');
                        fSectionForm.querySelector('input[name="emailAddress"]').value = eSectionForm.querySelector('input[name="emailAddress"]').value;
                        _trackEvent(e, 'emailSubmit');

                        if (sciCalculator) {
                            if (dataVersion === 'a') {
                                formHeader.classList.add('hide');
                                fSectionTitle.classList.add('show');
                            } else if (dataVersion === 'b') {
                                formResetBtn.classList.add('show');
                            }

                            if (inquiryIdField) {
                                inquiryIdField.value = fSectionForm.getAttribute('data-inquiryid-version-' + dataVersion);
                            }

                            toggleCalculatorFields('hide');
                        }
                    } else {
                        _handleFormSubmitError(eSectionForm, eSectionBtn, data.errorText);
                    }
                    element.classList.remove('processing');
                }, function () {
                    _handleFormSubmitError(eSectionForm, eSectionBtn, errorText);
                });
            } else {
                _errorFieldFocus();
            }
        }

        function _createSciInputs() {
            var amountToInsure = element.querySelector('input[name="amountToInsure"]'),
                amountToInsureText = sciCalculator.querySelector('.amount-selected span'),
                coverTimeRequested = element.querySelector('input[name="coverTimeRequested"]'),
                coverTimeRequestedText = sciCalculator.querySelector('.duration-selected span'),
                timePaidByCustomer = element.querySelector('input[name="timePaidByCustomer"]'),
                timePaidByCustomerText = sciCalculator.querySelector('.payment-time-selected span'),
                coverageEstimate = element.querySelector('input[name="coverageEstimate"]'),
                resultSpans = sciCalculator.querySelectorAll('#coverage-success h2 span');

            if (!amountToInsure) {
                amountToInsure = d.createElement('input');
                amountToInsure.setAttribute('type', 'hidden');
                amountToInsure.classList.add('form-control');
                amountToInsure.setAttribute('name', 'amountToInsure');
                fSectionForm.insertBefore(amountToInsure, fSectionForm.childNodes[0]);
            }

            if (!coverTimeRequested) {
                coverTimeRequested = d.createElement('input');
                coverTimeRequested.setAttribute('type', 'hidden');
                coverTimeRequested.classList.add('form-control');
                coverTimeRequested.setAttribute('name', 'coverTimeRequested');
                fSectionForm.insertBefore(coverTimeRequested, fSectionForm.childNodes[0]);
            }

            if (!timePaidByCustomer) {
                timePaidByCustomer = d.createElement('input');
                timePaidByCustomer.setAttribute('type', 'hidden');
                timePaidByCustomer.classList.add('form-control');
                timePaidByCustomer.setAttribute('name', 'timePaidByCustomer');
                fSectionForm.insertBefore(timePaidByCustomer, fSectionForm.childNodes[0]);
            }

            if (!coverageEstimate) {
                coverageEstimate = d.createElement('input');
                coverageEstimate.setAttribute('type', 'hidden');
                coverageEstimate.classList.add('form-control');
                coverageEstimate.setAttribute('name', 'coverageEstimate');
                fSectionForm.insertBefore(coverageEstimate, fSectionForm.childNodes[0]);
            }

            amountToInsure.value = amountToInsureText ? amountToInsureText.textContent : '';
            coverTimeRequested.value = coverTimeRequestedText ? coverTimeRequestedText.textContent : '';
            timePaidByCustomer.value = timePaidByCustomerText ? timePaidByCustomerText.textContent : '';

            if (resultSpans && resultSpans.length > 0) {
                coverageEstimate.value = resultSpans[0].textContent + ' - ' + resultSpans[1].textContent;
            }
        }

        function _validateSubmit(e) {
            e.preventDefault();
            EDC.forms.validateForm(fSectionFormElements);

            if (!element.querySelectorAll('.form-group:not(.hide) .form-control.error, .form-group:not(.hide) .radio-group.error').length) {
                if (sciCalculator) {
                    _createSciInputs();
                }

                _showProcessingScreen(fSectionForm, fSectionBtn);
                EDC.utils.fetchJSON('POST', fSectionSubmitUrl, EDC.forms.getFormData(fSectionForm), function (data) {
                    if (data.errorText) {
                        _handleFormSubmitError(fSectionForm, fSectionBtn, data.errorText);
                        element.classList.remove('processing');
                    } else {
                        _trackEvent(e, 'formSubmit');
                        element.classList.remove('processing');
                        fSectionForm.classList.add('hide');
                        submitFailedMessage.classList.add('hide');
                        submitSuccessMessage.classList.remove('hide');

                        if (dataVersion === 'b') {
                            notificationsValid.classList.add('show');
                            notificationsValid.classList.remove('hide');
                            actions.classList.remove('hide');
                            noteDisclaimer.classList.remove('hide');
                            formHeader.classList.add('hide');
                        }
                    }
                }, function () {
                    _handleFormSubmitError(fSectionForm, fSectionBtn, errorText);
                });
            } else {
                _errorFieldFocus();
            }
        }

        function _resetClicked() {
            toggleCalculatorFields('show');
            sciResetBtn.classList.remove('show');
            EDC.forms.resetForm(eSectionForm);
            EDC.forms.resetForm(fSectionForm);
            eSectionForm.reset();
            fSectionForm.reset();
            formHeader.classList.remove('hide');
            validAmountEl.classList.remove('show');
            notificationsValid.classList.remove('show');
            submitSuccessMessage.classList.add('hide');
            eSectionForm.querySelector('button[type="submit"]').removeAttribute('disabled');
            fSectionForm.querySelector('button[type="submit"]').removeAttribute('disabled');
            eSectionForm.classList.remove('hide');
            fSectionForm.classList.add('hide');
        }

        function _attachEvents() {
            EDC.utils.attachEvents(eSectionForm, 'submit', _checkEmailAndGetFormData);
            EDC.utils.attachEvents(fSectionForm, 'submit', _validateSubmit);
            EDC.utils.attachEvents(sciResetBtn, 'click', _resetClicked);
            EDC.utils.attachEvents(formResetBtn, 'click', _resetClicked);
        }

        _attachEvents();
        EDC.forms.validateChange(eSectionFormElements);
        EDC.forms.validateChange(fSectionFormElements);
        EDC.forms.fillHiddenFields(element);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var shareYourDetailsForms = document.querySelectorAll('.c-share-your-details-form:not([data-component-state="initialized"])');

        if (shareYourDetailsForms) {
            shareYourDetailsForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', ShareYourDetailsForm.init);