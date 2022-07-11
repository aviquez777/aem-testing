var ehForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            form = element.querySelector('form'),
            formElements = form ? form.elements : null,
            formRows = form ? form.querySelectorAll('.form-row') : null,
            prevQuestionCheckbox = form ? form.querySelector('input[type="checkbox"][name="prevQuestion"]') : null,
            submitBtn = form ? form.querySelector('button[type="submit"]') : null,
            submitSuccessScreen = element.querySelector('.submit-success-screen'),
            processingScreen = form ? form.querySelector('.c-processing-screen') : null,
            messageBanner = d.querySelector('.c-message-banner.error'),
            questionAskedSection = element.querySelector('.question-asked-section'),
            timeBeforeTracking = 5000;

        // Data Layer
        function _trackUserSegment(componentContainer) {
            var userSegment = {
                inquiry: EDC.forms.getOptionsSelected(componentContainer.querySelector('.dropdown [name="inquiry"]')),
                tradeStatus: EDC.forms.getOptionsSelected(componentContainer.querySelector('.dropdown [name="tradeStatus"]'))
            };

            EDC.utils.userSegmentTracking(userSegment, true);
        }

        function _testDataSetItemForTracking(item, attr) {
            return item ? item.dataset[attr] : '';
        }

        function _testValueForTracking(item) {
            return item ? item.toLowerCase() : '';
        }

        function _trackEvent(e) {
            var commentsValueField = element.querySelector('[name="comments"]'),
                commentsValue = commentsValueField ? commentsValueField.value : null,
                commentsCheckboxField = element.querySelector('[name="prevQuestion"]'),
                commentsCheckbox = commentsCheckboxField ? commentsCheckboxField.checked : null,
                eTarget = e.target,
                componentContainer = eTarget ? eTarget.closest('.c-eh-form') : null,
                employees = element.querySelector('[name="employees"]'),
                employeesValue = employees ? employees.value : null,
                annualSales = element.querySelector('[name="annualSales"]'),
                annualSalesValue = annualSales ? annualSales.value : null,
                tradeStatus = element.querySelector('[name="tradeStatus"]'),
                tradeStatusValue = tradeStatus ? tradeStatus.value : null,
                painPoint = element.querySelector('[name="painPoint"]'),
                industry = element.querySelector('[name="industry"]'),
                industryValue = industry ? industry.value : null,
                marketsInt = element.querySelector('[name="marketsInt-large"]'),
                submitBtnValue = submitBtn ? submitBtn.textContent : null,
                obj = {
                    eventInfo: {
                        eventComponent: _testDataSetItemForTracking(componentContainer, 'eventComponent'),
                        eventType: _testDataSetItemForTracking(componentContainer, 'eventType'),
                        eventName: _testDataSetItemForTracking(componentContainer, 'eventName'),
                        eventAction: _testDataSetItemForTracking(componentContainer, 'eventAction'),
                        eventText: _testValueForTracking(submitBtnValue),
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: '',
                        engagementType: _testDataSetItemForTracking(componentContainer, 'eventEngagement'),
                        eventLevel: _testDataSetItemForTracking(componentContainer, 'eventLevel'),
                        eventPageName: EDC.utils.getPageName(),
                        eventValue: _testValueForTracking(employeesValue),
                        eventValue2: _testValueForTracking(annualSalesValue),
                        eventValue3: _testValueForTracking(tradeStatusValue),
                        eventValue4: painPoint ? EDC.utils.getMultiDropdownDataForTracking(painPoint) : '',
                        eventValue5: _testValueForTracking(industryValue),
                        eventValue6: marketsInt ? EDC.utils.getMultiDropdownDataForTracking(marketsInt) : '',
                        eventValue7: commentsValue ? commentsValue : commentsCheckbox
                    }
                };

            _trackUserSegment(componentContainer);
            EDC.utils.dataLayerTracking(obj);
        }

        function _trackPrepopEvent(prepop) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: submitBtn ? submitBtn.tagName.toLowerCase() : '',
                    eventName: 'Pre-populated',
                    eventAction: element.dataset.eventAction,
                    eventText: submitBtn ? submitBtn.getAttribute('data-english-text') : '',
                    eventValue: prepop,
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: '',
                    eventPageName: EDC.utils.getPageName()
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        // Private methods
        function _validateSubmit(e) {
            var submitURL,
                formContent = form ? form.querySelector('.form-content') : null;

            e.preventDefault();

            if (formElements) {
                EDC.forms.validateForm(formElements);
            }

            if (form && !form.querySelectorAll('.form-control.error').length) {
                element.classList.add('processing');

                if (formContent) {
                    formContent.classList.add('hide');
                }

                if (processingScreen) {
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(processingScreen).y - 200);
                }

                if (form && !form.querySelectorAll('.form-control.error').length) {
                    submitURL = form ? form.getAttribute('action') : null;

                    if (submitURL) {
                        EDC.utils.fetchJSON('POST', submitURL, EDC.forms.getFormData(form), function () {
                            element.classList.remove('processing');

                            if (submitSuccessScreen) {
                                submitSuccessScreen.classList.remove('hide');
                            }

                            EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);
                            _trackEvent(e);
                        }, function () {
                            element.classList.remove('processing');

                            if (submitSuccessScreen) {
                                submitSuccessScreen.classList.add('hide');
                            }

                            if (formContent) {
                                formContent.classList.remove('hide');
                            }

                            if (submitBtn) {
                                EDC.forms.enableSubmit(submitBtn);
                            }

                            if (messageBanner) {
                                messageBanner.classList.remove('hide');
                                EDC.utils.scrollTo('top', EDC.utils.getPosition(messageBanner).y - 200);
                            }
                        });
                    }
                }
            } else {
                // Handling invalid form
                formContent = element.querySelector('.form-control.error');

                if (formContent) {
                    formContent.focus();
                }
            }
        }

        function _enableBtnTests(elType, el, elValue, country, testsPassed) {
            if ((elType === 'checkbox' && el.checked) ||
                (elType === 'radio') ||
                (elType === 'select-one' && elValue !== '') ||
                (EDC.forms.validationRules[elType] &&
                    EDC.forms.validationRules[elType](elValue, el, country) &&
                    EDC.forms.validateFieldLength(el) &&
                    (elType === 'text' || elType === 'tel' || elType === 'email') && elValue !== '')) {
                testsPassed++;

                if ((elType === 'email' && el.getAttribute('bank-email') && !EDC.forms.validationRules.bankEmail(elValue, el.getAttribute('bank-email'))) ||
                    (el.getAttribute('data-no-url') && EDC.forms.validationRules.noURL(elValue))) {
                    testsPassed--;
                }
            }

            return testsPassed;
        }

        function _checkRequiredToEnableBtn() {
            var inputs = form.querySelectorAll('[required]'),
                testsPassed = 0,
                country;

            setTimeout(function () {
                inputs.forEach(function (el) {
                    var elType = el.type,
                        elTag = el.tagName.toLowerCase(),
                        elValue,
                        isMultiDD = el.closest('.multiple');

                    if (elTag === 'textarea') {
                        elValue = el.value ? el.value.trim() : null;

                        if (elValue !== '') {
                            testsPassed++;
                        }
                    } else if (!isMultiDD) {
                        elValue = el.value ? el.value.trim() : null;
                        country = EDC.forms.getValidationCountry(el);
                        testsPassed = _enableBtnTests(elType, el, elValue, country, testsPassed);
                    } else if (el.parentNode && el.parentNode.querySelectorAll('.menu .item.selected-item').length > 0) {
                        testsPassed++;
                    }
                });
            }, 0);
        }

        function _prepareRequiredElements() {
            if (formElements) {
                formElements.forEach(function (el) {
                    if (el.tagName.toLowerCase() === 'select' || el.type === 'checkbox') {
                        if (el.parentNode && el.getAttribute('multiple') !== null) {
                            EDC.utils.attachEvents(el.parentNode.querySelectorAll('.menu .item'), 'click', _checkRequiredToEnableBtn);
                        } else {
                            EDC.utils.attachEvents(el, 'change', _checkRequiredToEnableBtn);
                        }
                    } else {
                        EDC.utils.attachEvents(el, 'input', _checkRequiredToEnableBtn);
                    }
                    EDC.utils.attachEvents(el, 'blur', _checkRequiredToEnableBtn);
                });
            }
        }

        function _toggleRequiredAttribute(e) {
            var el = e.currentTarget,
                commentsFields = element.querySelector('textarea[name="comments"]');

            if (commentsFields) {
                if (el && el.checked) {
                    commentsFields.removeAttribute('required');
                } else {
                    commentsFields.setAttribute('required', 'required');
                }
            }
        }

        function _getQuestionStatus() {
            var params = EDC.utils.getURLParams(),
                checkbox = questionAskedSection ? questionAskedSection.querySelector('input[type="checkbox"]') : null;

            if (params.qAsked && questionAskedSection) {
                questionAskedSection.classList.add('hide');

                if (checkbox) {
                    checkbox.click();
                }
            }
        }

        function _activatePrepopTracking() {
            setTimeout(function () {
                var value = 'No';

                if (window.setElqContentFnCalled) {
                    value = 'Yes';
                }

                _trackPrepopEvent(value);
            }, timeBeforeTracking);
        }

        function _attachEvents() {
            var country = element.querySelector('[data-form-field="companyCountry"] select'),
                provinceSelect = element.querySelector('[data-form-field="companyProvince"] .can select'),
                stateSelect = element.querySelector('[data-form-field="companyProvince"] .usa select'),
                provinceInput = element.querySelector('[data-form-field="companyProvince"] input'),
                provinceLabel = element.querySelector('[data-form-field="companyProvince"] > label');

            if (formElements) {
                EDC.forms.validateChange(formElements);
            }

            EDC.utils.attachEvents(element, 'submit', _validateSubmit);

            if (prevQuestionCheckbox) {
                EDC.utils.attachEvents(prevQuestionCheckbox, 'change', _toggleRequiredAttribute);
            }

            if (country) {
                EDC.utils.attachEvents(country, 'change', function () {
                    EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel);
                });
            }

            if (formRows) {
                EDC.utils.attachEvents(window, 'resize', function () {
                    EDC.forms.setHeightLabels(formRows);
                });
            }
        }

        _attachEvents();
        _prepareRequiredElements();
        EDC.forms.fillHiddenFields(element);

        if (formRows) {
            EDC.forms.setHeightLabelsOnLoad(formRows);
        }

        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(element);
        EDC.forms.elqQPush();
        _getQuestionStatus();
        _activatePrepopTracking();
    }

    // Public methods
    function init() {
        var ehForms = document.querySelectorAll('.c-eh-form:not([data-component-state="initialized"])');

        if (ehForms) {
            ehForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', ehForm.init);