var telpForm2JS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            form = element.querySelector('form'),
            formElements = form.elements,
            allLevels = element.querySelectorAll('.level'),
            serviceURL = form.getAttribute('action'),
            submitFailedMessage = element.querySelector('.submit-failed-message'),
            currentLevel = 1,
            ctasSection = form.querySelector('.form-ctas'),
            ctas = ctasSection.querySelectorAll('.cta'),
            pageLevel = form.querySelectorAll('.level').length,
            statusSection = element.querySelector('.c-segmented-progress-bar'),
            backBtn = form.querySelector('a.back'),
            proceedBtn = form.querySelector('button.done-btn'),
            questionnaire = form.querySelector('.c-questionnaire'),
            qSubmit = questionnaire.querySelector('button.btn-submit'),
            questionnaireFooter = element.querySelector('.questionnaire-footer'),
            finalStep = element.querySelector('.final-step'),
            qEligible = false,
            finalStatus = false,
            questionnaireCompleted = false,
            successMessage = element.querySelector('.success-message'),
            failMessage = element.querySelector('.fail-message'),
            messageBanner = d.querySelector('.c-message-banner'),
            space = /\s/g,
            comma = /,/g,
            dot = /\./g,
            specialMoneyRgx = /(\d+)(\d{3})/,
            isSpecialDDDirty = false,
            formDescription = element.querySelector('.form-description'),
            bankSelect = element.querySelector('select[name="bank"]'),
            bankSelectLength = bankSelect ? bankSelect.length : 0,
            measureQuestions = element.querySelector('.c-inclusive-trade-measure-questions'),
            allMeasureQuestions = measureQuestions ? measureQuestions.querySelectorAll('input[data-handle-checkbox]') : null,
            allMeasureQuestionsAndSubquestions = measureQuestions ? measureQuestions.querySelectorAll('input[type="checkbox"]') : null,
            allMeasureExtraQuestionsNodes = measureQuestions ? measureQuestions.querySelectorAll('.extra-question input[type="checkbox"]') : null,
            allMeasureExtraQuestions = allMeasureExtraQuestionsNodes ? Array.from(allMeasureExtraQuestionsNodes) : null,
            allMeasureQuestionsReviews = element.querySelectorAll('.final-step .measure-questions-review .c-form-field-info, .final-step .extra-measure-questions-review .c-form-field-info'),
            allOtherTextFields = measureQuestions ? measureQuestions.querySelectorAll('input[type="text"]') : null,
            allOtherBoxFields = measureQuestions ? measureQuestions.querySelectorAll('input[data-checkbox-type="other"]') : null,
            emptyField = '-';

        // Tracking
        function _trackBtn(action) {
            var level = '',
                obj,
                currentEl = element.querySelector('.level:not(.hide)'),
                els = element.querySelectorAll('.level'),
                thisLvl = element.querySelector('.level-' + currentLevel),
                bank = thisLvl ? thisLvl.querySelector('[name="bank"]') : null,
                lastAnnualSales = thisLvl ? thisLvl.querySelector('input[name="lastAnnualSales"]') : null,
                financialYearEndMonth = thisLvl ? thisLvl.querySelector('select[name="financialYearEndMonth"]') : null,
                val1 = bank ? bank.value : '',
                val2 = lastAnnualSales ? lastAnnualSales.value : '',
                val3 = financialYearEndMonth ? financialYearEndMonth.value : '';

            els.forEach(function (el, index) {
                if (el === currentEl) {
                    if (index < 4) {
                        level = index + 1;
                    } else {
                        level = 'final';
                    }
                }
            });

            obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent.toLowerCase(),
                    eventType: element.dataset.eventType.toLowerCase(),
                    eventAction: element.dataset.eventAction,
                    eventText: ctasSection.querySelector('button:not(.hide)').getAttribute('data-english-text').toLowerCase(),
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: element.dataset.destinationPage,
                    engagementType: element.dataset.engagementType,
                    eventLevel: element.dataset.eventLevel,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            if (action === 'next' || (action === 'q-done' && qEligible)) {
                obj.eventInfo.eventName = element.dataset.eventName + ' - step ' + level;
                if (val1 !== '') {
                    obj.eventInfo.eventValue = val1.toLowerCase();
                }
                if (val2 !== '') {
                    obj.eventInfo.eventValue2 = val2.toLowerCase();
                }
                if (val3 !== '') {
                    obj.eventInfo.eventValue3 = val3.toLowerCase();
                }
                EDC.utils.dataLayerTracking(obj);
            } else if (action === 'q-done' && !qEligible) {
                obj.eventInfo.eventName = element.dataset.eventName + ' - final - non eligible';
                EDC.utils.dataLayerTracking(obj);
            } else if (action === 'done' && qEligible) {
                obj.eventInfo.eventName = element.dataset.eventName + ' - final - eligible';
                EDC.utils.dataLayerTracking(obj);
            }
        }

        function _trackApiError() {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent.toLowerCase(),
                    eventType: 'error',
                    eventName: 'error – TELP form',
                    eventAction: element.dataset.eventAction,
                    eventText: messageBanner.getAttribute('data-english-load-error-text') || messageBanner.querySelector('.load-error').textContent,
                    eventValue: '3',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        // Private methods
        function _addSpaces(nStr) {
            var x = nStr.split('.'),
                x1 = x[0],
                x2 = x.length > 1 ? '.' + x[1] : '';

            while (specialMoneyRgx.test(x1)) {
                x1 = x1.replace(specialMoneyRgx, '$1 $2');
            }
            return x1 + x2;
        }

        function _addCommas(nStr) {
            var x = nStr.split('.'),
                x1 = x[0],
                x2 = x.length > 1 ? '.' + x[1] : '';

            while (specialMoneyRgx.test(x1)) {
                x1 = x1.replace(specialMoneyRgx, '$1,$2');
            }
            return x1 + x2;
        }

        function _transformMoneyField(amount) {
            var result;

            if (d.documentElement.attributes.lang && d.documentElement.attributes.lang.value === 'fr') {
                amount = amount.replace(space, '');
                result = _addSpaces(amount);
            } else {
                amount = amount.replace(comma, '');
                result = _addCommas(amount);
            }
            return result;
        }

        function _specialMoneyField(e) {
            var target = e.target,
                amount = target.value ? target.value.replace(comma, '').replace(space, '').replace(dot, '') : '';

            if (isNaN(amount)) {
                target.value = '';
            } else if (amount !== '') {
                target.value = _transformMoneyField(amount);
            }
        }

        // Helper function to show and hide form pages
        function _showHideLevels(current, following) {
            var formFields,
                focusField;

            // Hides data from current level page
            EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + current), 'display');
            if (following) {
                // Shows data from following level page
                formFields = element.querySelectorAll('.level-' + following + ' .form-group');

                if (formFields.length > 0) {
                    focusField = formFields[0].querySelector('.form-control');
                }

                EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + following), 'display');

                if (focusField && !focusField.classList.contains('dropdown')) {
                    focusField.focus();
                } else {
                    window.scroll(0, form.offsetTop - 75);
                }

                backBtn.classList[following === 1 ? 'add' : 'remove']('hide');
            }
        }

        function _cdiaRadioChanged(e) {
            var target = e.target,
                elToToggle = element.querySelector('.' + target.dataset.showValue) ? element.querySelector('.' + target.dataset.showValue) : null,
                elToToggleInput = elToToggle ? elToToggle.querySelectorAll('input') : [];

            e.preventDefault();
            if (elToToggle) {
                if (target.value === 'yes') {
                    elToToggle.querySelector('input[value="no"]').checked = true;
                    elToToggle.classList.remove('hide');
                    elToToggleInput.forEach(function (el) {
                        el.setAttribute('required', 'required');
                    });
                } else if (target.value === 'no') {
                    elToToggle.querySelector('input[value="no"]').checked = false;
                    elToToggle.classList.add('hide');
                    elToToggleInput.forEach(function (el) {
                        el.removeAttribute('required');
                    });
                }
            }
        }

        function _simpleMultiHide(els) {
            els.forEach(function (el) {
                el.classList.add('hide');
                el.classList.remove('show');
            });
        }

        function _toggleClass(el, action, cl) {
            el.classList[action](cl);
        }

        function _setCdia() {
            var cdiaRadio = form.querySelector('.cdia-radio'),
                forFinancing = form.querySelector('.for-financing'),
                cdiaRadioYes = cdiaRadio.querySelector('input[value="yes"]'),
                cdiaRadioNo = cdiaRadio.querySelector('input[value="no"]'),
                forFinancingRadioYes = forFinancing.querySelector('input[value="yes"]'),
                cdiaInput = form.querySelector('.cdia-section input[name="cdia"]'),
                reviewCdia = form.querySelector('.final-step [data-field-value="cdia"]');

            cdiaInput.value = cdiaRadioYes.checked && forFinancingRadioYes.checked ? cdiaRadioYes.value : cdiaRadioNo.value;
            reviewCdia.innerHTML = cdiaRadioYes.checked && forFinancingRadioYes.checked ? cdiaRadioYes.getAttribute('data-translated-text') : cdiaRadioNo.getAttribute('data-translated-text');
        }

        function _prepareFinalForm() {
            var lastAnnualSalesValue = form.querySelector('.last-annual-sales-container input.numeric').value,
                lastAnnualSalesValueToSubmit = lastAnnualSalesValue ? lastAnnualSalesValue.replace(comma, '').replace(space, '').replace(dot, '') : '';

            form.querySelector('.last-annual-sales-container input.numeric').value = lastAnnualSalesValueToSubmit;

            return EDC.forms.getFormData(form);
        }

        function _submitPost(isEligible) {
            var finalForm = _prepareFinalForm(),
                lastAnnualSales = form.querySelector('.last-annual-sales-container input.numeric');

            submitFailedMessage.classList.add('hide');
            EDC.utils.fetchJSON('POST', serviceURL, finalForm, function () {
                _toggleClass(questionnaireFooter, 'add', 'hide');
                _simpleMultiHide(allLevels);
                _toggleClass(ctasSection, 'add', 'hide');
                _toggleClass(statusSection, 'add', 'hide');
                _toggleClass(finalStep, 'add', 'hide');
                _toggleClass(isEligible ? successMessage : failMessage, 'remove', 'hide');
                EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y - 100);
            }, function () {
                // Error on server communication, delay or related issues
                if (submitFailedMessage) {
                    submitFailedMessage.classList.remove('hide');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(submitFailedMessage).y - 300);
                    proceedBtn.removeAttribute('disabled');
                }
            });

            lastAnnualSales.value = _transformMoneyField(lastAnnualSales.value);
        }

        function _nextBtnForPagination() {
            backBtn.querySelector('span').innerHTML = backBtn.getAttribute('data-back-text');
            backBtn.classList.remove('button');
            backBtn.classList.remove('edc-secondary-btn');
            ctasSection.classList.remove('align-center');
            ctasSection.classList.remove('fix-ctas');

            if (currentLevel < pageLevel) {
                _showHideLevels(currentLevel, (currentLevel + 1));
                element.querySelector('.level-1').classList.remove('show');
                currentLevel++;

                if (element.querySelector('.back').classList.contains('hide')) {
                    EDC.forms.toggleFormFields(element.querySelectorAll('.back'), 'display');
                }

                if (currentLevel === pageLevel) {
                    EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                    EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                }
            }

            if (currentLevel === 1 || currentLevel === 4) {
                _toggleClass(questionnaireFooter, 'remove', 'hide');

                if (!questionnaireCompleted) {
                    proceedBtn.classList.add('hide');
                    proceedBtn.classList.remove('show');
                }
            } else {
                _toggleClass(questionnaireFooter, 'add', 'hide');
            }
        }

        function _formDoneAction(action, submitBtn) {
            if (action === 'q-done') {
                if (qEligible) {
                    submitBtn.setAttribute('data-action', 'done');
                    backBtn.setAttribute('data-action', 'edit');
                    backBtn.querySelector('span').innerHTML = backBtn.getAttribute('data-edit-text');
                    backBtn.classList.add('button');
                    backBtn.classList.add('edc-secondary-btn');
                    ctasSection.classList.add('align-center');
                    ctasSection.classList.add('fix-ctas');
                    _toggleClass(statusSection, 'add', 'hide');
                    currentLevel = 1;
                    _simpleMultiHide(allLevels);
                    _toggleClass(finalStep, 'remove', 'hide');
                    EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y - 100);
                } else {
                    _submitPost(false);
                }
            } else if (action === 'done') {
                _submitPost(true);
            }
        }

        function _refreshReviewScreenFieldsForLoqate(fieldsToFillNames) {
            if (fieldsToFillNames.length > 0) {
                fieldsToFillNames.forEach(function (fieldToFillName) {
                    var fieldToFill = form.querySelector('.c-form-field-info .field-value[data-field-value="' + fieldToFillName + '"]'),
                        valueField = form.querySelector('input[name="' + fieldToFillName + '"]');

                    if (fieldToFill && valueField) {
                        fieldToFill.textContent = valueField.value === '' ? emptyField : valueField.value;
                    }
                });
            }
        }

        function _formPagination(e) {
            var pageField = form.querySelector('.final-step:not(.hide)') ?
                    element.querySelectorAll('.final-step .form-group .form-control') : element.querySelectorAll('.level-' + currentLevel + ' .form-group .form-control, input[type=hidden]'),
                errorField,
                submitBtn = form.querySelector('button[type="submit"]:not(.hide)'),
                secondaryBtn = form.querySelector('button[type="submit"].hide'),
                action = e.type === 'submit' ? submitBtn.getAttribute('data-action') : e.currentTarget.getAttribute('data-action');

            e.preventDefault();

            if (action === 'back' || action === 'edit') {
                finalStatus = false;
                _toggleClass(submitFailedMessage, 'add', 'hide');

                element.querySelectorAll('.level-' + currentLevel + ' .form-control').forEach(function (elem) {
                    _toggleClass(elem, 'remove', 'error');
                });

                if (currentLevel === pageLevel) {
                    EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                    EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                }

                _toggleClass(statusSection, 'remove', 'hide');

                if (action === 'back') {
                    _showHideLevels(currentLevel, (currentLevel - 1));
                    currentLevel--;
                } else if (action === 'edit') {
                    currentLevel = 1;
                    _toggleClass(element.querySelector('.level-' + currentLevel), 'remove', 'hide');
                    _toggleClass(finalStep, 'add', 'hide');
                    backBtn.setAttribute('data-action', 'back');
                    backBtn.classList.remove('button');
                    backBtn.classList.remove('edc-secondary-btn');
                    backBtn.classList.add('hide');
                    ctasSection.classList.remove('align-center');
                    ctasSection.classList.remove('fix-ctas');
                    _toggleClass(element, 'add', 'show');
                    _toggleClass(element, 'remove', 'hide');
                    _toggleClass(submitBtn, 'add', 'hide');
                    _toggleClass(submitBtn, 'remove', 'show');
                    _toggleClass(secondaryBtn, 'add', 'show');
                    _toggleClass(secondaryBtn, 'remove', 'hide');
                }

                proceedBtn.classList.add('hide');
                proceedBtn.classList.remove('show');
                questionnaireFooter.classList.add('hide');

                if (submitBtn && submitBtn.classList.contains('done-btn')) {
                    submitBtn.setAttribute('data-action', 'q-done');
                }

                EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y - 100);
            } else {
                EDC.forms.validateForm(pageField);

                if (!form.querySelectorAll('.form-control.error, input.error').length && serviceURL) {
                    _refreshReviewScreenFieldsForLoqate(['companyAddress1', 'companyAddress2', 'companyCity', 'companyPostal']);
                    _trackBtn(action);

                    if (submitBtn) {
                        finalStatus = true;
                        _formDoneAction(action, submitBtn);
                    }

                    if (action === 'next') {
                        finalStatus = false;
                        _nextBtnForPagination();
                    }
                } else {
                    errorField = element.querySelector('.level-' + currentLevel + ' .form-control.error') || element.querySelector('.final-step .form-control.error');
                    errorField.focus();
                }
            }

            if (formDescription) {
                if (currentLevel === 1 && !finalStatus) {
                    formDescription.classList.remove('hide');
                } else {
                    formDescription.classList.add('hide');
                }
            }
        }

        function _questionnaireCompleted() {
            var ineligibleCheckbox = element.querySelector('.ineligible-checkbox'),
                declarationCountry = element.querySelectorAll('.country-declaration'),
                qCountry = questionnaire.querySelector('input[name="q-country"]'),
                qUserStatus = questionnaire.querySelector('input[name="q-user-status"]') ? questionnaire.querySelector('input[name="q-user-status"]').value : null,
                declarationsSection = element.querySelector('.final-step .declarations'),
                declarationsText = declarationsSection.querySelectorAll('.declaration-text'),
                approvalMsg = questionnaire.getAttribute('data-approval'),
                qSpecialDeclaration = questionnaire.getAttribute('data-special-declaration');

            qEligible = questionnaire.querySelector('input[name="q-result"]').value.toLowerCase() === approvalMsg.toLowerCase();

            if (qEligible) {
                if (declarationsText) {
                    if (qSpecialDeclaration) {
                        declarationsText.forEach(function (el) {
                            if (el.getAttribute('data-user-status') === qUserStatus && el.getAttribute('data-special-declaration') === qSpecialDeclaration) {
                                el.classList.remove('hide');
                                declarationsSection.classList.remove('hide');
                            } else {
                                el.classList.add('hide');
                            }
                        });
                    } else {
                        declarationsText.forEach(function (el) {
                            if (el.getAttribute('data-user-status') === qUserStatus && !el.getAttribute('data-special-declaration')) {
                                el.classList.remove('hide');
                                declarationsSection.classList.remove('hide');
                            } else {
                                el.classList.add('hide');
                            }
                        });
                    }
                }
            } else {
                ineligibleCheckbox.classList.remove('hide');
                _toggleClass(questionnaireFooter, 'add', 'hide');
            }

            if (qCountry) {
                declarationCountry.forEach(function (el) {
                    el.textContent = qCountry.value;
                });
            }

            if (qUserStatus && qUserStatus === 'Domestic') {
                element.setAttribute('data-event-component', 'COVIDR-D Form');
            }

            proceedBtn.classList.remove('hide');
            proceedBtn.classList.add('show');
            questionnaireCompleted = true;
        }

        function _specialBankData(e) {
            var specialData = e.target.querySelectorAll('option')[e.target.selectedIndex].getAttribute('special-data'),
                bankEmail = form.querySelector('.form-group[data-form-field="managerEmailAddress"] input[type="email"]');

            if (bankEmail && specialData) {
                bankEmail.setAttribute('bank-email', specialData);
            } else {
                bankEmail.removeAttribute('bank-email');
                bankEmail.removeAttribute('data-bank-error-domain');
            }

            if (!isSpecialDDDirty) {
                isSpecialDDDirty = true;
            } else {
                bankEmail.focus();
                bankEmail.blur();
            }
        }

        function _unsetFixedCtas() {
            var checkEl = form.querySelector('.final-step:not(.hide)'),
                scrollPosition = window.pageYOffset,
                formBottomPosition = (form.scrollHeight + form.offsetTop) - window.innerHeight;

            if (checkEl) {
                if (scrollPosition >= (formBottomPosition)) {
                    ctasSection.classList.remove('fix-ctas');
                } else {
                    ctasSection.classList.add('fix-ctas');
                }
            } else {
                ctasSection.classList.remove('fix-ctas');
            }
        }

        function _apiError() {
            if (bankSelectLength <= 1 && messageBanner) {
                messageBanner.classList.remove('hide');
                _trackApiError();
            }
        }

        function _extraQuestionsClicked() {
            var someSelected = false,
                parent = element.querySelector('.final-step .measure-questions-review.section-to-toggle');

            allMeasureExtraQuestions.some(function (question) {
                var value = question.checked;

                if (value) {
                    someSelected = true;
                }

                return value;
            });

            if (parent) {
                if (someSelected) {
                    parent.classList.remove('hide');
                } else {
                    parent.classList.add('hide');
                }
            }
        }

        function _measureQuestionsClicked() {
            var currentlyChecked = [];

            setTimeout(function () {
                allMeasureQuestionsAndSubquestions.forEach(function (q) {
                    if (q.checked) {
                        currentlyChecked.push(q);
                    }
                });

                allMeasureQuestionsReviews.forEach(function (question) {
                    question.classList.add('hide');
                    question.querySelector('.field-value').textContent = emptyField;
                    question.closest('.section-to-toggle').classList.add('hide');
                });

                currentlyChecked.forEach(function (currentQ) {
                    var formFieldInfo = element.querySelector('.c-form-field-info .field-value[data-field-value="' + currentQ.name + '"]'),
                        formFieldInfoParent = formFieldInfo ? formFieldInfo.closest('.c-form-field-info') : null,
                        parent = formFieldInfoParent.closest('.section-to-toggle');

                    if (formFieldInfo) {
                        formFieldInfo.textContent = currentQ.closest('.checkbox-item').querySelector('label').textContent;
                        formFieldInfoParent.classList.remove('hide');
                        parent.classList.remove('hide');
                        _extraQuestionsClicked();
                    }
                });
            }, 0);
        }

        function _otherTextFieldsChanged(e) {
            var el = e.target,
                val = el ? el.value : null,
                elName = el.getAttribute('data-other-text-field'),
                relatedBox = measureQuestions.querySelector('input[name="' + elName + '"]'),
                relatedBoxParent = relatedBox ? relatedBox.closest('.checkbox-item') : null,
                relatedBoxLabel = relatedBoxParent ? relatedBoxParent.querySelector('label') : null,
                formFieldInfo = element.querySelector('.c-form-field-info .field-value[data-field-value="' + elName + '"]'),
                formFieldInfoParent = formFieldInfo ? formFieldInfo.closest('.c-form-field-info') : null;

            if (val !== '' && relatedBox.checked && formFieldInfo) {
                formFieldInfo.textContent = relatedBoxLabel.textContent;
                formFieldInfoParent.classList.remove('hide');
            } else {
                formFieldInfo.textContent = emptyField;
                formFieldInfoParent.classList.add('hide');
            }
        }

        function _otherBoxFieldsChanged(e) {
            var el = e.target,
                textField = measureQuestions.querySelector('input[data-other-text-field="' + el.name + '"]'),
                name = textField ? textField.name : null,
                formFieldInfo = element.querySelector('.c-form-field-info .field-value[data-field-value="' + name + '"]'),
                formFieldInfoParent = formFieldInfo ? formFieldInfo.closest('.c-form-field-info') : null,
                formFieldInfoGrandParent = formFieldInfoParent ? formFieldInfoParent.closest('.extra-measure-questions-review') : null;

            if (!el.checked) {
                formFieldInfo.textContent = emptyField;
                formFieldInfoParent.classList.add('hide');

                if (formFieldInfoGrandParent) {
                    if (formFieldInfoGrandParent.querySelectorAll('input[type="checkbox"]:checked').length === 0) {
                        formFieldInfoGrandParent.classList.add('hide');
                    }
                }
            }
        }

        // Attach events
        function _attachEvents() {
            var country = form.querySelector('[data-form-field="companyCountry"] select'),
                provinceSelect = form.querySelector('[data-form-field="companyProvince"] .can select'),
                stateSelect = form.querySelector('[data-form-field="companyProvince"] .usa select'),
                provinceInput = form.querySelector('[data-form-field="companyProvince"] input'),
                provinceLabel = form.querySelector('[data-form-field="companyProvince"] > label'),
                bankDD = form.querySelector('[data-form-field="bank"]'),
                specialMoneyField = form.querySelector('.last-annual-sales-container input.numeric'),
                cdiaRadio = form.querySelectorAll('.cdia-radio input[type="radio"]'),
                cdiaRadios = form.querySelectorAll('.cdia-section input[type="radio"]'),
                formInfoFieldToReInitialize = element.querySelector('.c-form-field-info .field-value[data-field-value="companyProvince"]'),
                formInfoFieldToReInitializeParent = formInfoFieldToReInitialize ? formInfoFieldToReInitialize.closest('.c-form-field-info') : null;

            EDC.utils.attachEvents(ctas, 'click', _formPagination);
            EDC.utils.attachEvents(form, 'submit', _formPagination);

            EDC.utils.attachEvents(country, 'change', function () {
                EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel);
                provinceSelect.parentElement.classList.remove('disabled');
                formInfoFieldToReInitializeParent.removeAttribute('data-component-state');
                EDC.forms.initializeFormReviewField(formInfoFieldToReInitializeParent);
            });

            EDC.utils.attachEvents(bankDD, 'change', _specialBankData);
            EDC.utils.attachEvents(qSubmit, 'click', _questionnaireCompleted);
            EDC.utils.attachEvents(specialMoneyField, 'input', _specialMoneyField);
            EDC.utils.attachEvents(cdiaRadio, 'change', _cdiaRadioChanged);
            EDC.utils.attachEvents(window, 'scroll', _unsetFixedCtas);
            EDC.utils.attachEvents(cdiaRadio, 'change', _cdiaRadioChanged);
            EDC.utils.attachEvents(cdiaRadios, 'change', _setCdia);
            EDC.forms.validateChange(formElements);
            EDC.utils.attachEvents(allMeasureQuestions, 'click', _measureQuestionsClicked);
            EDC.utils.attachEvents(allMeasureExtraQuestionsNodes, 'click', _extraQuestionsClicked);
            EDC.utils.attachEvents(allOtherTextFields, 'input', _otherTextFieldsChanged);
            EDC.utils.attachEvents(allOtherBoxFields, 'click', _otherBoxFieldsChanged);
        }

        _apiError();
        _attachEvents();
        EDC.forms.fillHiddenFields(form);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(form);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var telpForms = document.querySelectorAll('.c-telp-form-2:not([data-component-state="initialized"])');

        if (telpForms) {
            telpForms.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', telpForm2JS.init);