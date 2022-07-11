var inclusiveTradeMeasureQuestionsJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            parentForm = element.closest('form'),
            submitBtn = parentForm ? parentForm.querySelector('button[type="submit"]:not([data-action="next"])') : null,
            checkboxes = element.querySelectorAll('.questions-checkboxes input[type="checkbox"]'),
            otherTextFields = element.querySelectorAll('input[data-other-text-field]'),
            extraQuestionsContainers = element.querySelectorAll('.extra-question[data-show-extra-question]'),
            extraQuestions = element.querySelectorAll('.extra-question input[type="checkbox"]'),
            valueInput = d.querySelector('input[name="ITMValue"]'),
            valueInputValue = valueInput && valueInput.value !== '' && valueInput.value !== 'true' && valueInput.value !== 'false' ? valueInput.value : null,
            otherInput = d.querySelector('input[name="ITMOther"]'),
            otherInputValue = otherInput && otherInput.value !== '' && otherInput.value !== 'true' && otherInput.value !== 'false' ? otherInput.value : null;

        // Data Layer
        function _trackEvent() {
            var valueInputVal = valueInput ? valueInput.value : '',
                values = '',
                valuesArray,
                userSegment = {};

            if (valueInputVal !== '') {
                valuesArray = valueInputVal.split(' ');

                if (valuesArray && valuesArray.length > 1) {
                    values = valuesArray.join(':');
                }
            }

            userSegment.itmSelection = values;
            EDC.utils.userSegmentTracking(userSegment, true);
        }

        // Private methods
        function _updateHiddenValueField(values) {
            var finalValue = '';

            if (values.length) {
                values.forEach(function (value) {
                    finalValue += value.toUpperCase() + ' ';
                });

                valueInput.value = finalValue.trim();
            } else if (valueInput.name) {
                valueInput.value = '';
            }
        }

        function _updateHiddenOtherField() {
            var finalValue = '';

            otherTextFields.forEach(function (field) {
                var fieldParent = field.closest('.extra-question'),
                    parentId = fieldParent ? fieldParent.getAttribute('data-section-id') : null;

                if (field.value !== '' && parentId) {
                    finalValue += field.value.trim() + '-' + parentId.toUpperCase() + ' ';
                }
            });

            if (finalValue !== '') {
                otherInput.value = finalValue.trim();
            } else if (otherInput) {
                otherInput.value = '';
            }
        }

        function _majorityMinorityExclusivity(el, checked, name) {
            var parent = el.closest('.extra-question'),
                parentId = parent ? parent.getAttribute('data-section-id') : null,
                questionId = name[1],
                majorityId = 'm',
                minorityId = 'n',
                thisQuestionId,
                parallelInput;

            if (parentId !== 'l' && questionId !== 'o') {
                if (parentId === majorityId) {
                    thisQuestionId = minorityId;
                } else if (parentId === minorityId) {
                    thisQuestionId = majorityId;
                }

                if (thisQuestionId && questionId) {
                    parallelInput = element.querySelector('.extra-question input[name="' + thisQuestionId + questionId + '"]');
                }

                if (parallelInput) {
                    el.classList.remove('disabled');

                    if (checked) {
                        parallelInput.classList.add('disabled');
                        parallelInput.checked = false;
                    } else {
                        parallelInput.classList.remove('disabled');
                    }
                }
            }
        }

        function _checkboxesChanged(e) {
            var el = e.target,
                checked = el.checked,
                activateTopGroup = el.getAttribute('data-handle-checkbox') === 'top-group',
                checkboxesChecked = element.querySelectorAll('.questions-checkboxes input[type="checkbox"]:checked').length,
                atLeastOneChecked = checkboxesChecked > 0,
                showExtraQuestion = el.getAttribute('data-show-extra-question'),
                extraQuestion = showExtraQuestion ? element.querySelector('.extra-question[data-show-extra-question="' + showExtraQuestion + '"]') : null,
                finalValues = [],
                otherFieldToClear,
                finalChecked = [];

            function _uncheckCheckbox(checkbox) {
                checkbox.classList.add('disabled');
                checkbox.checked = false;
            }

            function _emptyOtherTextFields() {
                otherTextFields.forEach(function (otherTextField) {
                    otherTextField.value = '';
                });
            }

            if (atLeastOneChecked) {
                checkboxes.forEach(function (checkbox) {
                    var thisActivateTopGroup = checkbox.getAttribute('data-handle-checkbox') === 'top-group';

                    if (activateTopGroup) {
                        if (thisActivateTopGroup) {
                            checkbox.classList.remove('disabled');
                        } else {
                            _uncheckCheckbox(checkbox);
                        }
                    } else if (thisActivateTopGroup) {
                        _uncheckCheckbox(checkbox);

                        extraQuestionsContainers.forEach(function (question) {
                            question.classList.add('hide');
                        });
                    } else {
                        checkbox.classList.remove('disabled');
                        _emptyOtherTextFields();

                        extraQuestions.forEach(function (question) {
                            question.checked = false;
                        });

                        if (checkbox.checked) {
                            finalValues.push(checkbox.value);
                        }
                    }
                });
            } else {
                _emptyOtherTextFields();

                checkboxes.forEach(function (checkbox) {
                    checkbox.classList.remove('disabled');
                });

                extraQuestions.forEach(function (question) {
                    question.classList.remove('disabled');
                });
            }

            if (extraQuestion) {
                if (checked) {
                    extraQuestion.classList.remove('hide');
                } else {
                    extraQuestion.classList.add('hide');
                    otherFieldToClear = element.querySelector('[data-section-id="' + el.name + '"] input[data-other-text-field]');

                    if (otherFieldToClear) {
                        otherFieldToClear.value = '';
                    }

                    extraQuestion.querySelectorAll('input[type="checkbox"]').forEach(function (input) {
                        input.checked = false;
                    });
                }
            }

            extraQuestions.forEach(function (question) {
                if (question.checked) {
                    finalValues.push(question.value);
                }
            });

            if (checkboxesChecked === 1) {
                extraQuestions.forEach(function (question) {
                    question.classList.remove('disabled');

                    if (question.checked) {
                        finalChecked.push(question);
                    }
                });

                finalChecked.forEach(function (question) {
                    _majorityMinorityExclusivity(question, question.checked, question.name);
                });
            }

            _updateHiddenValueField(finalValues);
            _updateHiddenOtherField();
        }

        function _collectFinalValuesForExtraQuestions() {
            var finalValues = [];

            extraQuestions.forEach(function (answer) {
                if (answer.checked) {
                    finalValues.push(answer.value);
                }
            });

            return finalValues;
        }

        function _extraQuestionsChanged(e) {
            var el = e.target,
                elChecked = el.checked,
                elName = el.name,
                checkboxType = el.getAttribute('data-checkbox-type'),
                textField = element.querySelector('input[data-other-text-field="' + elName + '"]');

            if (checkboxType === 'other' && !elChecked) {
                textField.value = '';
            }

            _majorityMinorityExclusivity(el, elChecked, elName);
            _updateHiddenValueField(_collectFinalValuesForExtraQuestions());
        }

        function _otherTextFieldsChanged(e) {
            var el = e.target,
                val = el.value,
                other = el.getAttribute('data-other-text-field'),
                otherToChange = other ? element.querySelector('[name="' + other + '"]') : null;

            if (otherToChange) {
                otherToChange.checked = val;
            }

            _updateHiddenOtherField();
            _updateHiddenValueField(_collectFinalValuesForExtraQuestions());
        }

        function _prepareForModal() {
            d.querySelector('body').classList.add('on-measure-questions');
        }

        function _prepopulateCheckboxes() {
            var valueArr,
                otherArr = [],
                length = 0,
                cutedStr;

            function _checkInputForClick(el) {
                if (el && !el.checked) {
                    el.click();
                }
            }

            function _prepareOtherArr() {
                if (otherInputValue && otherInputValue.length > 0) {
                    length = otherInputValue.indexOf('-') + 2;
                    cutedStr = otherInputValue.substring(0, length);
                    otherInputValue = otherInputValue.replace(cutedStr, '');
                    otherArr.push(cutedStr.trim());
                    _prepareOtherArr();
                }
            }

            if (valueInputValue) {
                valueArr = valueInputValue.split(' ');

                valueArr.forEach(function (item) {
                    var qLvl1Val,
                        qLvl2Val,
                        qLvl1,
                        qLvl2;

                    if (item[0]) {
                        qLvl1Val = item[0] ? item[0].toLowerCase() : null;
                    }

                    if (item) {
                        qLvl2Val = item.toLowerCase();
                    }

                    qLvl1 = qLvl1Val ? element.querySelector('input[name="' + qLvl1Val + '"]') : null;
                    qLvl2 = qLvl2Val ? element.querySelector('input[name="' + qLvl2Val + '"]') : null;
                    _checkInputForClick(qLvl1);
                    _checkInputForClick(qLvl2);
                });

                if (otherInputValue) {
                    _prepareOtherArr();

                    otherArr.forEach(function (item) {
                        var itemArr = item.split('-'),
                            str = itemArr[0],
                            inputName = itemArr[1] ? itemArr[1].toLowerCase() : null,
                            input = inputName ? element.querySelector('input[name="' + inputName + 'oText"]') : null;

                        if (input && str) {
                            input.value = str;
                        }
                    });
                }
            }
        }

        function _formSubmited() {
            var formErrors,
                formErrorsLength,
                formIsDone = submitBtn.getAttribute('data-action') !== 'q-done';

            setTimeout(function () {
                formErrors = parentForm.querySelectorAll('.form-control.error, input.error');
                formErrorsLength = formErrors ? formErrors.length : null;

                if (!formErrorsLength && formIsDone) {
                    _trackEvent();
                }
            }, 0);
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(checkboxes, 'change', _checkboxesChanged);
            EDC.utils.attachEvents(extraQuestions, 'change', _extraQuestionsChanged);
            EDC.utils.attachEvents(otherTextFields, 'input', _otherTextFieldsChanged);

            if (parentForm && submitBtn) {
                EDC.utils.attachEvents(submitBtn, 'click', _formSubmited);
            }
        }

        _prepareForModal();
        _attachEvents();
        _prepopulateCheckboxes();
    }

    // Public methods
    function init() {
        var inclusiveTradeMeasureQuestions = document.querySelectorAll('.c-inclusive-trade-measure-questions:not([data-component-state="initialized"])');

        if (inclusiveTradeMeasureQuestions) {
            inclusiveTradeMeasureQuestions.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', inclusiveTradeMeasureQuestionsJS.init);