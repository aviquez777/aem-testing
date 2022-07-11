var questionnaireJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var qJsonUrl = element.getAttribute('data-json-url') || '',
            isDynamic = element.getAttribute('data-is-dynamic'),
            qData = [],
            qHeader = element.querySelector('.q-header'),
            qBody = element.querySelector('.q-body'),
            qFooter = element.querySelector('.q-footer'),
            qContent = qBody.querySelector('.q-content'),
            qSummary = qBody.querySelector('.q-summary'),
            qResults = qBody.querySelector('.q-results'),
            qAnchor = qBody.querySelector('.anchor-container'),
            qLoading = qBody.querySelector('.q-loading'),
            resetBtn = qHeader.querySelector('button'),
            rSpacer = qHeader.querySelector('.r-spacer'),
            qOptions = qContent.querySelector('.q-options'),
            qDropdown = qContent.querySelector('.q-dropdown'),
            qMultiDropdown = qContent.querySelector('.q-multi-dropdown'),
            qNumberContainer = qHeader.querySelector('.q-number'),
            qNumberText = qNumberContainer.querySelector('span.q-number-text'),
            qNumberNumber = qNumberContainer.querySelector('span.q-number-number'),
            qFullText = qContent.querySelector('.q-text'),
            qText = qFullText.querySelector('span'),
            qSubText = qContent.querySelector('.q-subtext'),
            dropdown = qDropdown.querySelector('.ui.dropdown'),
            multiDropdown = qMultiDropdown.querySelector('.ui.dropdown'),
            answerBtns = qOptions.querySelectorAll('.q-options button'),
            resultsTitle = qResults.querySelector('.results-title'),
            resultsMsg = qResults.querySelector('.results-msg'),
            nextQBtn = qFooter.querySelector('button.btn-next'),
            submitBtn = qFooter.querySelector('button.btn-submit'),
            approvedMsg = element.getAttribute('data-approval'),
            rejectedMsg = element.getAttribute('data-rejection'),
            helperModal = element.querySelector('.c-more-information-modal'),
            helperModalTitle = helperModal.querySelector('.modal-title h2'),
            helperModalText = helperModal.querySelector('.modal-text p'),
            realQNumber = 1,
            parentForm = element.closest('form'),
            questionnaireActivator = parentForm ? parentForm.querySelector('[data-activate-questionnaire="true"]') : null;

        // Private functions
        // Tracking
        function _trackBtn(target) {
            var userSegment = {},
                borrowerStatus = element.querySelector('input[name="q-user-status"]') ? element.querySelector('input[name="q-user-status"]') : null,
                eligibilityStatus = element.querySelector('input[name="q-result"]'),
                btnText = target.value.toLowerCase(),
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent.toLowerCase(),
                        eventType: element.dataset.eventType.toLowerCase(),
                        eventName: element.dataset.eventName + (btnText === 'submit' ? (' - ' + btnText) : ' - question ' + element.querySelector('.q-number span.q-number-number').textContent),
                        eventAction: element.dataset.eventAction.toLowerCase(),
                        eventText: btnText,
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: element.dataset.destinationPage,
                        engagementType: element.dataset.engagementType,
                        eventLevel: element.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            if (borrowerStatus && borrowerStatus.value !== '') {
                userSegment.borrowerStatus = borrowerStatus.value.toLowerCase();
            }
            if (eligibilityStatus && eligibilityStatus.value !== '') {
                userSegment.eligibilityStatus = eligibilityStatus.value.toLowerCase();
            }

            EDC.utils.dataLayerTracking(obj);
            EDC.utils.userSegmentTracking(userSegment, true);
        }

        function _toggleEl(el, ev) {
            if (!ev) {
                if (!el.classList.contains('hide')) {
                    el.classList.add('hide');
                } else if (el.classList.contains('hide')) {
                    el.classList.remove('hide');
                }
            } else if (ev === 'hide' && !el.classList.contains('hide')) {
                el.classList.add('hide');
            } else if (ev === 'show' && el.classList.contains('hide')) {
                el.classList.remove('hide');
            }
        }

        function _toggleNextBtn(todo) {
            if (todo === 'show') {
                _toggleEl(resetBtn, 'show');
                _toggleEl(rSpacer, 'hide');
                nextQBtn.removeAttribute('disabled');
            } else if (todo === 'hide') {
                _toggleEl(resetBtn, 'hide');
                _toggleEl(rSpacer, 'show');
                nextQBtn.setAttribute('disabled', 'disabled');
            }
        }

        function _answerSelected(e) {
            var el = e.target,
                tag = el.tagName.toLowerCase(),
                multiDD,
                multiDDItems,
                maxSelections,
                noneEl;

            if (tag === 'select') {
                nextQBtn.setAttribute('data-go-to', el.querySelectorAll('option')[el.selectedIndex].getAttribute('data-next-question'));
                _toggleNextBtn('show');
            } else if (tag === 'div') {
                noneEl = el.closest('.ui.dropdown.multiple').querySelector('[data-value="NONE"]');
                if (el.getAttribute('data-value') === 'NONE') {
                    el.closest('.ui.dropdown.multiple').querySelectorAll('.item.selected-item').forEach(function (item) {
                        if (item.getAttribute('data-value') !== 'NONE') {
                            item.click();
                        }
                    });
                    nextQBtn.setAttribute('data-go-to', el.getAttribute('data-next-question'));
                    _toggleNextBtn('show');
                } else if (noneEl && noneEl.classList.contains('selected-item') && el.getAttribute('data-value') !== 'clear-all') {
                    el.click();
                } else {
                    setTimeout(function () {
                        multiDD = el.closest('.ui.dropdown.multiple');
                        multiDDItems = multiDD.querySelectorAll('.item.selected-item');
                        maxSelections = multiDD ? multiDD.getAttribute('data-max-selections') : null;
                        if (multiDDItems.length > 0) {
                            if (maxSelections && multiDDItems.length > parseInt(maxSelections, 10)) {
                                el.click();
                            } else {
                                nextQBtn.setAttribute('data-go-to', el.getAttribute('data-next-question'));
                                _toggleNextBtn('show');
                            }
                        } else {
                            _toggleNextBtn('hide');
                        }
                    }, 0);
                }
            } else {
                answerBtns.forEach(function (btn) {
                    if (btn.classList.contains('selected')) {
                        btn.classList.remove('selected');
                        btn.classList.add('primary-outline');
                    }
                });

                if (el.classList.contains('primary-outline')) {
                    el.classList.remove('primary-outline');
                    el.classList.add('selected');
                }

                if (el.getAttribute('data-user-status')) {
                    nextQBtn.setAttribute('data-user-status', el.getAttribute('data-user-status'));
                }

                if (el.getAttribute('data-special-declaration')) {
                    nextQBtn.setAttribute('data-special-declaration', el.getAttribute('data-special-declaration'));
                }

                nextQBtn.setAttribute('data-go-to', el.getAttribute('data-next-question'));
                _toggleNextBtn('show');
            }
        }

        function _createElem(elType, attrs, text) {
            var el = document.createElement(elType);

            attrs.forEach(function (attr) {
                el.setAttribute(attr.attr, attr.value);
            });

            el.innerHTML = text;

            return el;
        }

        function _cleanDD(dd, str, isMulti) {
            dd.querySelectorAll(str).forEach(function (item, index) {
                if (!isMulti || index > 0) {
                    item.parentNode.removeChild(item);
                }
            });
        }

        function _setDDOptions(question) {
            var ddPlaceholder = question.answersPlaceholder ? question.answersPlaceholder : 'Select',
                isMulti = question.isMulti,
                dd = (isMulti ? qMultiDropdown : qDropdown).querySelector('.ui.dropdown'),
                ddSelect = dd.querySelector('select'),
                ddMenu = dd.querySelector('.menu'),
                ddParent;

            _toggleEl(qOptions, 'hide');
            _toggleEl(qDropdown, 'hide');
            _toggleEl(qMultiDropdown, 'hide');

            if (dd) {
                if (isMulti && question.maxSelections) {
                    dd.setAttribute('data-max-selections', question.maxSelections);
                }
                ddParent = dd.closest('.c-dropdown');
                _cleanDD(ddMenu, '.item', isMulti);
                _cleanDD(ddSelect, 'option', isMulti);
                ddSelect.setAttribute('data-default-value', ddPlaceholder);
                ddSelect.appendChild(_createElem('option',
                    [{ attr: 'value', value: '' }, { attr: 'selected', value: '' }, { attr: 'disabled', value: '' }, { attr: 'hidden', value: '' }],
                    ddPlaceholder));

                question.answers.forEach(function (item) {
                    ddMenu.appendChild(_createElem('div', [
                        { attr: 'class', value: 'item' },
                        { attr: 'data-value', value: item.value },
                        { attr: 'data-next-question', value: item.goTo }
                    ], item.label));
                    ddSelect.appendChild(_createElem('option', [
                        { attr: 'value', value: item.value },
                        { attr: 'data-next-question', value: item.goTo }
                    ], item.label));
                });

                dd.querySelector('.text').innerHTML = ddPlaceholder;

                if (isMulti) {
                    EDC.dropdown.init(ddParent);
                    _toggleEl(qMultiDropdown, 'show');
                } else {
                    _toggleEl(qDropdown, 'show');
                }
            }
        }

        function _fillQuestion(numb) {
            var qNumb = Number(numb),
                question,
                answersLength;

            if (qNumb > 0) {
                qData.forEach(function (q) {
                    if (q.number === qNumb) {
                        question = q;
                    }
                });

                if (question) {
                    answersLength = question.answers.length;
                    qNumberNumber.textContent = realQNumber;
                    qText.innerHTML = question.mainText;
                    qSubText.textContent = question.secondaryText ? question.secondaryText : '';

                    if (qSubText.textContent === '') {
                        _toggleEl(qSubText, 'hide');
                    } else {
                        _toggleEl(qSubText, 'show');
                    }

                    if (question.helperTitle && question.helperText) {
                        helperModalTitle.innerHTML = question.helperTitle;
                        helperModalText.innerHTML = question.helperText;

                        _toggleEl(helperModal, 'show');
                    } else {
                        _toggleEl(helperModal, 'hide');

                        helperModalTitle.innerHTML = '';
                        helperModalText.innerHTML = '';
                    }

                    qContent.classList.remove('save-answer');

                    if (question.saveName && question.saveName !== '') {
                        qContent.classList.add('save-answer');
                        qContent.setAttribute('data-save-name', question.saveName);
                    } else {
                        qContent.classList.remove('save-answer');
                        qContent.removeAttribute('data-save-name');
                    }

                    if (answersLength === 2) {
                        answerBtns.forEach(function (btn, index) {
                            btn.textContent = question.answers[index].label;
                            btn.setAttribute('data-answer-value', question.answers[index].value);
                            btn.setAttribute('data-next-question', question.answers[index].goTo);

                            if (btn.classList.contains('selected')) {
                                btn.classList.remove('selected');
                                btn.classList.add('primary-outline');
                            }

                            if (question.answers[index].userStatus) {
                                btn.setAttribute('data-user-status', question.answers[index].userStatus);
                            } else {
                                btn.removeAttribute('data-user-status');
                            }

                            if (question.answers[index].specialDeclaration) {
                                btn.setAttribute('data-special-declaration', question.answers[index].specialDeclaration);
                            } else {
                                btn.removeAttribute('data-special-declaration');
                            }
                        });

                        _toggleEl(qOptions, 'show');
                        _toggleEl(qDropdown, 'hide');
                        _toggleEl(qMultiDropdown, 'hide');
                    } else if (answersLength > 2) {
                        _setDDOptions(question);
                    }

                    if (!nextQBtn.getAttribute('disabled')) {
                        nextQBtn.setAttribute('disabled', 'disabled');
                    }
                }
            } else if (qNumb <= 0) {
                if (qSummary.classList.contains('hide') && !qContent.classList.contains('hide') && !qBody.classList.contains('summary') && qAnchor.classList.contains('hide')) {
                    qBody.classList.add('summary');
                    _toggleEl(qContent, 'hide');
                    _toggleEl(qSummary, 'show');
                    _toggleEl(qAnchor, 'show');

                    qNumberText.textContent = qNumberContainer.getAttribute('data-review-answers-text');
                    qNumberNumber.textContent = '';
                    qBody.classList.add('shadow-after');
                    qSummary.scrollTop = 0;

                    if (qNumb === 0) {
                        submitBtn.setAttribute('data-result', 'approved');
                        resultsTitle.innerHTML = resultsTitle.getAttribute('data-approved');
                        resultsMsg.innerHTML = resultsMsg.getAttribute('data-approved');
                    } else if (qNumb === -1) {
                        submitBtn.setAttribute('data-result', 'rejected');
                        resultsTitle.innerHTML = resultsTitle.getAttribute('data-rejected');
                        resultsMsg.innerHTML = resultsMsg.getAttribute('data-rejected');
                    }
                }

                _toggleEl(nextQBtn, 'hide');
                _toggleEl(submitBtn, 'show');
            }
        }

        function _addAnswerToSummary() {
            var answerContainer = document.createElement('div'),
                question = document.createElement('h4'),
                answer = document.createElement('p'),
                answersListLength = qSummary.querySelectorAll('.q-answer-container').length,
                hr,
                answerText = '',
                multiDropdownArray;

            if (answersListLength > 0) {
                hr = document.createElement('hr');
                hr.classList.add('lg');
                qSummary.appendChild(hr);
            }

            answerContainer.classList.add('q-answer-container');
            question.classList.add('q-question');
            answer.classList.add('q-answer');
            question.textContent = qText.textContent;

            if (!qOptions.classList.contains('hide')) {
                answerText = qOptions.querySelector('button.selected').textContent;
                answer.textContent = answerText;
            } else if (!qMultiDropdown.classList.contains('hide')) {
                multiDropdownArray = qMultiDropdown.querySelectorAll('.item.selected-item');
                multiDropdownArray.forEach(function (item, index) {
                    answerText +=
                        (multiDropdownArray.length > 1 && index + 1 === multiDropdownArray.length ? ' & ' : '') +
                        item.textContent +
                        (index + 1 === multiDropdownArray.length - 1 || index === multiDropdownArray.length - 1 ? '' : ', ');
                });
                answer.textContent = answerText;
            } else if (!qDropdown.classList.contains('hide')) {
                answerText = qDropdown.querySelectorAll('select option')[qDropdown.querySelector('select').selectedIndex].textContent;
                answer.textContent = answerText;
            }

            answerContainer.appendChild(question);
            answerContainer.appendChild(answer);
            qSummary.appendChild(answerContainer);
        }

        function _nextQuestion(e) {
            var target = e.currentTarget,
                answerToSave = '',
                multiDropdownArray;

            if (target.getAttribute('data-user-status')) {
                element.setAttribute('data-user-status', target.getAttribute('data-user-status'));
            }

            if (target.getAttribute('data-special-declaration')) {
                element.setAttribute('data-special-declaration', target.getAttribute('data-special-declaration'));
            }

            if (qContent.classList.contains('save-answer')) {
                if (!qOptions.classList.contains('hide')) {
                    answerToSave = qOptions.querySelector('button.selected').textContent;
                } else if (!qMultiDropdown.classList.contains('hide')) {
                    multiDropdownArray = qMultiDropdown.querySelectorAll('.item.selected-item');
                    multiDropdownArray.forEach(function (item, index) {
                        answerToSave += item.getAttribute('data-value') + (index + 1 === multiDropdownArray.length ? '' : '::');
                    });
                } else if (!qDropdown.classList.contains('hide')) {
                    answerToSave = qDropdown.querySelector('.text').textContent;
                }
                element.setAttribute('data-to-save-' + qContent.getAttribute('data-save-name'), answerToSave);
            }

            _addAnswerToSummary();
            _trackBtn(target);
            realQNumber++;
            _fillQuestion(target.getAttribute('data-go-to'));
        }

        function _removeDomEls(parent, childrensClass) {
            parent.querySelectorAll(childrensClass).forEach(function (el) {
                el.remove();
            });
        }

        function _removeClasses(el, cls) {
            if (typeof cls === 'object') {
                cls.forEach(function (cl) {
                    el.classList.remove(cl);
                });
            } else if (typeof cls === 'string') {
                el.classList.remove(cls);
            }
        }

        function _resetQuestionnaire(e) {
            var target = e.target,
                elAttrs = element.attributes,
                i;

            realQNumber = 1;
            qNumberText.textContent = qNumberContainer.getAttribute('data-question-text');
            qNumberNumber.textContent = realQNumber;

            for (i = 0; i < elAttrs.length; i++) {
                if (elAttrs[i].nodeName.indexOf('data-to-save-') > -1) {
                    element.removeAttribute(elAttrs[i].nodeName);
                }
            }

            _fillQuestion(1);
            _toggleEl(target, 'hide');
            _toggleEl(rSpacer, 'show');
            _toggleEl(qSummary, 'hide');
            _toggleEl(qResults, 'hide');
            _toggleEl(qAnchor, 'hide');
            _toggleEl(qContent, 'show');
            _toggleEl(qFooter, 'show');
            _toggleEl(nextQBtn, 'show');
            _toggleEl(submitBtn, 'hide');

            _removeClasses(qBody, ['result', 'summary', 'shadow-before', 'shadow-after']);
            _removeDomEls(qSummary, '.q-answer-container');
            _removeDomEls(qSummary, 'hr.lg');
            element.removeAttribute('data-user-status');
            element.removeAttribute('data-special-declaration');
            nextQBtn.removeAttribute('data-user-status');
            nextQBtn.removeAttribute('data-special-declaration');
            submitBtn.removeAttribute('data-result');
        }

        function _submitBtnClicked(e) {
            var resultInput = element.querySelector('input[name="q-result"]'),
                target = e.currentTarget,
                elAttrs = element.attributes,
                attrsToReturn = [],
                i;

            qSummary.scrollTop = qSummary.scrollHeight;
            qNumberText.textContent = qNumberContainer.getAttribute('data-end-text');
            qNumberNumber.textContent = '';

            if (element.getAttribute('data-user-status')) {
                attrsToReturn.push('data-user-status');
            }

            for (i = 0; i < elAttrs.length; i++) {
                if (elAttrs[i].nodeName.indexOf('data-to-save-') > -1) {
                    attrsToReturn.push(elAttrs[i].nodeName);
                }
            }

            attrsToReturn.forEach(function (attr) {
                var input = document.createElement('input'),
                    name = attr.replace('data-', '').replace('to-save-', '');

                input.setAttribute('type', 'hidden');
                input.setAttribute('name', 'q-' + name);
                input.value = element.getAttribute(attr);
                resultInput.insertAdjacentElement('afterend', input);
            });

            if (submitBtn.getAttribute('data-result') === 'approved') {
                resultInput.value = approvedMsg;
            } else if (submitBtn.getAttribute('data-result') === 'rejected') {
                resultInput.value = rejectedMsg;
            }

            qBody.classList.remove('summary');
            qBody.classList.add('result');
            _toggleEl(qSummary, 'hide');
            _toggleEl(qResults, 'show');
            _toggleEl(qFooter, 'hide');
            _toggleEl(qAnchor, 'hide');
            _toggleEl(resetBtn, 'hide');
            _toggleEl(rSpacer, 'show');

            _trackBtn(target);
        }

        function _summaryScrolled(e) {
            var target = e.target,
                scrollSize = target.scrollHeight - target.offsetHeight,
                newScrollTop = Math.round(target.scrollTop);

            if (newScrollTop < scrollSize) {
                _toggleEl(qAnchor, 'show');
            } else {
                _toggleEl(qAnchor, 'hide');
            }

            if (newScrollTop > 0 && newScrollTop < scrollSize) {
                qBody.classList.add('shadow-before');
                qBody.classList.add('shadow-after');
            } else if (newScrollTop === 0) {
                qBody.classList.add('shadow-after');
                qBody.classList.remove('shadow-before');
            } else if (newScrollTop >= scrollSize) {
                qBody.classList.remove('shadow-after');
                qBody.classList.add('shadow-before');
            }
        }

        function _externalQuestionnaireActivation() {
            EDC.utils.fetchJSON('GET', element.getAttribute('data-dynamic-json'), '', function (data) {
                if (data && data.questions.length > 0) {
                    qData = data.questions;
                    PubSub.publish('qData', true);
                }
            });
        }

        // Attach events
        function _attachEvents() {
            EDC.utils.attachEvents(answerBtns, 'click', _answerSelected);
            EDC.utils.attachEvents(nextQBtn, 'click', _nextQuestion);
            EDC.utils.attachEvents(resetBtn, 'click', _resetQuestionnaire);
            EDC.utils.attachEvents(submitBtn, 'click', _submitBtnClicked);
            EDC.utils.attachEvents(qSummary, 'scroll', _summaryScrolled);
            EDC.utils.attachEvents(dropdown, 'change', _answerSelected);
            EDC.utils.attachEvents(multiDropdown.querySelector('.menu'), 'click', _answerSelected);

            EDC.utils.attachEvents(qAnchor.querySelector('.circle-button'), 'click', function () {
                qSummary.scrollTop = qSummary.scrollHeight;
            });
        }

        _attachEvents();
        qText.classList.remove('has-more-info-btn');

        if (!isDynamic || isDynamic === 'false') {
            EDC.utils.fetchJSON('GET', qJsonUrl, '', function (data) {
                if (data && data.questions.length > 0) {
                    qData = data.questions;
                    PubSub.publish('qData', true);
                }
            });
        } else if (isDynamic === 'true' && questionnaireActivator) {
            EDC.utils.attachEvents(questionnaireActivator, 'click', _externalQuestionnaireActivation);
        }

        PubSub.subscribe('qData', function (ready) {
            if (ready && qData.length > 0) {
                _fillQuestion(1);
                _toggleEl(qContent, 'show');
                _toggleEl(qLoading, 'hide');
            }
        });
    }

    // Public methods
    function init() {
        var questionnaires = document.querySelectorAll('.c-questionnaire:not([data-component-state="initialized"])');

        if (questionnaires) {
            questionnaires.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', questionnaireJS.init);