var triageToolJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var qJsonUrl = element.getAttribute('data-json-url') || '',
            qData = [],
            qFlow = [],
            qSolutions = [],
            qFI10B = [],
            qAnswers = element.querySelector('.q-answers'),
            qHeader = element.querySelector('.q-header'),
            qBody = element.querySelector('.q-body'),
            qFooter = element.querySelector('.q-footer'),
            qContent = qBody.querySelector('.q-content'),
            qResults = qBody.querySelector('.q-results'),
            qLoading = qBody.querySelector('.q-loading'),
            qOptions = qContent.querySelector('.q-options'),
            qDropdown = qContent.querySelector('.q-dropdown'),
            qSlider = qContent.querySelector('.q-slider'),
            qTitle = qHeader.querySelector('.q-title'),
            qText = qContent.querySelector('.q-text'),
            qSubText = qContent.querySelector('.q-subtext'),
            singleDropdownContainer = qDropdown.querySelector('.single-dropdown'),
            singleDropdown = singleDropdownContainer.querySelector('.ui.dropdown'),
            multiDropdownContainer = qDropdown.querySelector('.multi-dropdown'),
            multiDropdown = multiDropdownContainer.querySelector('.ui.dropdown'),
            carousel = qResults.querySelector('.carousel'),
            answerBtns,
            nextQBtn = qFooter.querySelector('button.btn-next'),
            resetBtn = qResults.querySelector('.actions button.reset'),
            triageToolCounter = 1;

        // Private functions
        // Tracking
        function _trackEvent(event) {
            var userSegment = {},
                btnText,
                obj,
                allResults = element.querySelectorAll('.results-grid .c-triage-result-card:not(.hide) .card-header .title'),
                resultsText = '',
                answerSelectedText = '',
                currentQuestion = [],
                currentDD;

            obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent.toLowerCase(),
                    eventType: element.dataset.eventType.toLowerCase(),
                    eventAction: element.dataset.eventAction.toLowerCase(),
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: element.dataset.destinationPage,
                    engagementType: element.dataset.engagementType,
                    eventLevel: element.dataset.eventLevel,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            switch (event) {
                case 'resultsDisplayed':
                    allResults.forEach(function (title, index) {
                        resultsText += title.textContent + (index + 1 < allResults.length ? ',' : '');
                    });
                    obj.eventInfo.eventValue6 = resultsText;
                    obj.eventInfo.eventType = qResults.dataset.eventType.toLowerCase();
                    obj.eventInfo.eventName = qResults.dataset.eventName.toLowerCase();
                    obj.eventInfo.eventText = nextQBtn.textContent.toLowerCase();
                    break;

                case 'nextBtnClicked':
                    btnText = nextQBtn.textContent.toLowerCase();
                    currentQuestion = element.querySelector('.q-content .q-answer-type:not(.hide)');

                    if (currentQuestion.querySelector('button.selected')) {
                        currentQuestion.querySelectorAll('button.selected').forEach(function (item, index) {
                            answerSelectedText += item.getAttribute('data-answer-value') + (index + 1 < currentQuestion.querySelectorAll('button.selected').length ? ',' : '');
                        });
                    } else {
                        currentDD = currentQuestion.querySelector('.dropdown-option-container:not(.hide)');
                        currentDD.querySelectorAll('.menu .item.selected-item, .menu .item.selected').forEach(function (item, index) {
                            answerSelectedText += item.textContent + (index + 1 < currentQuestion.querySelectorAll('.menu .item.selected-item').length ? ',' : '');
                        });
                    }

                    obj.eventInfo.eventValue = answerSelectedText;
                    obj.eventInfo.eventName = element.dataset.eventName + (btnText === 'submit' ? (' - ' + btnText) : ' - step ' + triageToolCounter);
                    triageToolCounter++;
                    obj.eventInfo.eventText = btnText;
                    break;

                case 'triageToolReset':
                    triageToolCounter = 0;
                    obj.eventInfo.eventType = qResults.dataset.eventType.toLowerCase();
                    obj.eventInfo.eventName = qResults.dataset.eventName.toLowerCase();
                    obj.eventInfo.eventText = resetBtn.textContent.toLowerCase();
                    break;
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

        function _multiToggle(arr1, arr2) {
            arr1.forEach(function (item) {
                _toggleEl(item, 'hide');
            });

            arr2.forEach(function (item) {
                _toggleEl(item, 'show');
            });
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

        function _prepareDDOptions(question) {
            var ddPlaceholder = question.answersPlaceholder ? question.answersPlaceholder : 'Select',
                isMulti = question.multi,
                dd = qDropdown.querySelector('.' + (isMulti ? 'multi' : 'single') + '-dropdown .ui.dropdown'),
                ddSelect = dd.querySelector('select'),
                ddMenu = dd.querySelector('.menu'),
                ddParent;

            _toggleEl(singleDropdownContainer, 'hide');
            _toggleEl(multiDropdownContainer, 'hide');

            if (dd) {
                ddParent = dd.closest('.c-dropdown');
                _cleanDD(ddMenu, '.item', isMulti);
                _cleanDD(ddSelect, 'option', isMulti);
                ddSelect.appendChild(_createElem('option',
                    [{ attr: 'value', value: '' }, { attr: 'selected', value: '' }, { attr: 'disabled', value: '' }, { attr: 'hidden', value: '' }],
                    ddPlaceholder));

                question.answers.forEach(function (item) {
                    ddMenu.appendChild(_createElem('div', [
                        { attr: 'class', value: 'item' },
                        { attr: 'data-value', value: item.value },
                        { attr: 'data-mutually-exclusive', value: item.mutuallyExclusive ? item.mutuallyExclusive : false }
                    ], item.label));
                    ddSelect.appendChild(_createElem('option', [{ attr: 'value', value: item.value }], item.label));
                });

                dd.querySelector('.text').innerHTML = ddPlaceholder;

                if (isMulti) {
                    EDC.dropdown.init(ddParent);
                }

                _toggleEl(ddParent.parentElement, 'show');
            }
        }

        function _prepareButtons(question) {
            var i,
                answer;

            answerBtns.forEach(function (btn) {
                _toggleEl(btn, 'hide');
            });

            for (i = 0; i < question.answers.length; i++) {
                answer = question.answers[i];
                answerBtns[i].textContent = answer.label;
                answerBtns[i].setAttribute('data-answer-value', answer.value);
                answerBtns[i].setAttribute('data-mutually-exclusive', answer.mutuallyExclusive ? answer.mutuallyExclusive : false);

                if (answerBtns[i].classList.contains('selected')) {
                    answerBtns[i].classList.remove('selected');
                    answerBtns[i].classList.add('edc-secondary-btn');
                }

                _toggleEl(answerBtns[i], 'show');
            }
        }

        function _prepareSlider(question) {
            var sliderInput = qSlider.querySelector('.slider');

            sliderInput.min = question.range.min;
            sliderInput.max = question.range.max;
            sliderInput.step = question.range.step;
            sliderInput.defaultValue = question.range.defaultValue;
        }

        function _handleQuestionType(question) {
            var qType = question.type;

            switch (qType) {
                case 'buttons':
                    _prepareButtons(question);
                    _multiToggle([qDropdown, qSlider], [qOptions]);
                    break;

                case 'dropdown':
                    _prepareDDOptions(question);
                    _multiToggle([qOptions, qSlider], [qDropdown]);
                    break;

                case 'slider':
                    _prepareSlider(question);
                    _multiToggle([qOptions, qDropdown], [qSlider]);
                    break;
            }
        }

        function _fillQuestion(numb) {
            var qNumb = Number(numb),
                question;

            if (qNumb > 0) {
                if (qNumb === 1) {
                    element.classList.add('first-question');
                } else {
                    element.classList.remove('first-question');
                }

                qData.forEach(function (q) {
                    if (q.number === qNumb) {
                        question = q;
                    }
                });

                if (question) {
                    element.setAttribute('data-current-question', question.number);
                    qTitle.textContent = question.title;
                    qText.innerHTML = question.mainText;
                    qSubText.textContent = question.secondaryText ? question.secondaryText : '';

                    if (qSubText.textContent === '') {
                        _toggleEl(qSubText, 'hide');
                    } else {
                        _toggleEl(qSubText, 'show');
                    }

                    _handleQuestionType(question);
                    qContent.setAttribute('data-is-multi', question.multi ? 'true' : 'false');

                    if (!nextQBtn.getAttribute('disabled')) {
                        nextQBtn.setAttribute('disabled', 'disabled');
                    }
                }
            } else if (qNumb <= 0) {
                _toggleEl(qContent, 'hide');
                _toggleEl(nextQBtn, 'hide');
            }
        }

        function _toggleBtns(todo) {
            if (todo === 'enable') {
                _toggleEl(resetBtn, 'show');
                nextQBtn.removeAttribute('disabled');
            } else if (todo === 'disable') {
                _toggleEl(resetBtn, 'hide');
                nextQBtn.setAttribute('disabled', 'disabled');
            }
        }

        function _changeBtnsStatus(el, status) {
            if (status === 'selected') {
                el.classList.remove('edc-secondary-btn');
                el.classList.add('selected');
            } else if (status === 'unselected') {
                el.classList.add('edc-secondary-btn');
                el.classList.remove('selected');
            } else if (!status) {
                if (el.classList.contains('edc-secondary-btn')) {
                    el.classList.remove('edc-secondary-btn');
                    el.classList.add('selected');
                } else {
                    el.classList.add('edc-secondary-btn');
                    el.classList.remove('selected');
                }
            }
        }

        function _handleBtnsSelection(el) {
            var btnsCounter = 0,
                isMutuallyExclusive = el.getAttribute('data-mutually-exclusive') === 'true',
                mutuallyExclusiveActive = el.closest('.q-options').querySelector('[data-mutually-exclusive="true"].selected');

            if (qContent.getAttribute('data-is-multi') === 'false') {
                answerBtns.forEach(function (btn) {
                    if (btn.classList.contains('selected')) {
                        _changeBtnsStatus(btn, 'unselected');
                    }
                });
                if (el.classList.contains('edc-secondary-btn')) {
                    _changeBtnsStatus(el, 'selected');
                }

                _toggleBtns('enable');
            } else if (isMutuallyExclusive) {
                _changeBtnsStatus(el);

                answerBtns.forEach(function (btn) {
                    if (btn !== el) {
                        _changeBtnsStatus(btn, 'unselected');
                    }
                });
            } else if (el.classList.contains('edc-secondary-btn')) {
                if (!mutuallyExclusiveActive) {
                    _changeBtnsStatus(el, 'selected');
                    _toggleBtns('enable');
                }
            } else {
                _changeBtnsStatus(el, 'unselected');

                answerBtns.forEach(function (btn) {
                    if (btn.classList.contains('selected')) {
                        btnsCounter++;
                    }
                });

                if (btnsCounter > 0) {
                    _toggleBtns('enable');
                } else {
                    _toggleBtns('disable');
                }
            }
        }

        function _saveAnswer(val) {
            var input,
                answer = qAnswers.querySelector('input[data-question="' + element.getAttribute('data-current-question') + '"]');

            if (answer) {
                answer.value = val;
            } else {
                input = document.createElement('input');
                input.type = 'hidden';
                input.setAttribute('data-question', element.getAttribute('data-current-question'));
                input.value = val;
                qAnswers.appendChild(input);
            }
        }

        function _handleMultiDropdownSelection(el, isMutuallyExclusive, mutuallyExclusiveSelected, elsSelected) {
            var value = '',
                multiItemsSelected = [];

            if (isMutuallyExclusive) {
                if (!el.classList.contains('selected-item')) {
                    value = el.getAttribute('data-value');
                    elsSelected.forEach(function (item) {
                        if (item !== el) {
                            item.click();
                        }
                    });
                    _toggleBtns('enable');
                } else {
                    _toggleBtns('disable');
                }
            } else if (el.getAttribute('data-value') !== 'clear-all') {
                if (mutuallyExclusiveSelected) {
                    el.click();
                    value = mutuallyExclusiveSelected.getAttribute('data-value');
                } else {
                    elsSelected.forEach(function (item) {
                        if (item.getAttribute('data-value') !== el.getAttribute('data-value')) {
                            multiItemsSelected.push(item);
                        }
                    });

                    if (!el.classList.contains('selected-item')) {
                        multiItemsSelected.push(el);
                    }

                    if (multiItemsSelected.length > 0) {
                        multiItemsSelected.forEach(function (item, index) {
                            value += item.getAttribute('data-value') + (multiItemsSelected.length > 1 && (index + 1) < multiItemsSelected.length ? '::' : '');
                        });
                        _toggleBtns('enable');
                    } else {
                        _toggleBtns('disable');
                    }
                }
            } else {
                value = '';
                _toggleBtns('disable');
            }

            return value;
        }

        function _answerSelection(e) {
            var el = e.target,
                tagName = el.tagName.toLowerCase(),
                value = '',
                selectedBtns,
                isMutuallyExclusive = el.getAttribute('data-mutually-exclusive') === 'true',
                mutuallyExclusiveSelected,
                elsSelected;

            switch (tagName) {
                case 'select':
                case 'input':
                    value = el.value;
                    _toggleBtns('enable');
                    break;

                case 'button':
                    _handleBtnsSelection(el);
                    selectedBtns = qOptions.querySelectorAll('button.selected');

                    if (selectedBtns.length === 0) {
                        value = '';
                    } else {
                        selectedBtns.forEach(function (btn, index) {
                            value += btn.getAttribute('data-answer-value') + (selectedBtns.length > 1 && (index + 1) < selectedBtns.length ? '::' : '');
                        });
                    }
                    break;

                case 'div':
                    mutuallyExclusiveSelected = el.closest('.menu').querySelector('.item.selected-item[data-mutually-exclusive="true"]');
                    elsSelected = el.closest('.menu').querySelectorAll('.item.selected-item');
                    value = _handleMultiDropdownSelection(el, isMutuallyExclusive, mutuallyExclusiveSelected, elsSelected);
                    break;
            }

            _saveAnswer(value);
        }

        function _showResults() {
            var answersInputs = qAnswers.querySelectorAll('input[type="hidden"]'),
                mandatoryCodes = element.getAttribute('data-mandatory-codes');

            qSolutions.forEach(function (solution) {
                var solutionCounter = 0,
                    thisCard = qResults.querySelector('.results-grid .c-triage-result-card[data-solution-code="' + solution.solutionCode + '"]'),
                    q10Approved = false;

                answersInputs.forEach(function (answerInput) {
                    var answerInputValues = answerInput.value.split('::'),
                        questionInputNumber = parseInt(answerInput.getAttribute('data-question'), 10),
                        currentQuestion,
                        currentQuestionValues,
                        found2;

                    solution.questions.some(function (question) {
                        var found = question.question === questionInputNumber;

                        if (question.question === questionInputNumber) {
                            currentQuestion = question;
                        }

                        return found;
                    });

                    if (currentQuestion) {
                        if (currentQuestion.question === 10) {
                            solution.questions.some(function (solutionQuestions) {
                                var found = solutionQuestions.question === 10,
                                    solutionQuestionsValues = solutionQuestions.value.split('::');

                                if (found) {
                                    solutionQuestionsValues.some(function (solutionQuestionValue) {
                                        var specialValues = solutionQuestionValue.split('&'),
                                            found3 = false;

                                        specialValues.some(function (val) {
                                            if (val === 'any') {
                                                q10Approved = true;
                                            } else if (val[0] === '!') {
                                                if (val.slice(1) === '10B') {
                                                    qFI10B.forEach(function (fi) {
                                                        answerInputValues.every(function (thisA) {
                                                            found3 = thisA !== fi;
                                                            q10Approved = found3;
                                                            return found3;
                                                        });
                                                    });
                                                } else {
                                                    answerInputValues.every(function (thisA) {
                                                        found3 = thisA !== val.slice(1);
                                                        q10Approved = found3;
                                                        return found3;
                                                    });
                                                }
                                            } else if (val === '10B') {
                                                qFI10B.some(function (fi) {
                                                    answerInputValues.some(function (thisA) {
                                                        found3 = thisA === fi;
                                                        q10Approved = found3;
                                                        return found3;
                                                    });

                                                    return found3;
                                                });
                                            } else {
                                                answerInputValues.forEach(function (thisA) {
                                                    q10Approved = thisA === val;
                                                });
                                            }

                                            return found3;
                                        });

                                        return found3;
                                    });
                                }

                                return found;
                            });

                            if (q10Approved) {
                                solutionCounter++;
                            }
                        } else if (answerInputValues.length > 1) {
                            answerInputValues.some(function (thisA) {
                                var found = false;

                                currentQuestionValues = currentQuestion.value.split('::');
                                currentQuestionValues.some(function (v) {
                                    found = thisA.toLowerCase() === v.toLowerCase() || v.toLowerCase() === 'any';
                                    return found;
                                });

                                if (found) {
                                    solutionCounter++;
                                }

                                return found;
                            });
                        } else {
                            currentQuestionValues = currentQuestion.value.split('::');

                            currentQuestionValues.some(function (v) {
                                found2 = answerInputValues[0].toLowerCase() === v.toLowerCase() || v.toLowerCase() === 'any';
                                return found2;
                            });

                            if (found2) {
                                solutionCounter++;
                            }
                        }
                    }
                });

                if (solution.exceptions) {
                    solution.exceptions.some(function (solutionException) {
                        var exceptionCounter = 0,
                            exception = solutionException.exception,
                            found;

                        exception.forEach(function (item) {
                            var input = qAnswers.querySelector('input[data-question="' + item.question + '"]'),
                                forEvery = false;

                            if (item.value === 'any' || input === null) {
                                exceptionCounter++;
                            } else if (item.value === '10B') {
                                input.value.split('::').some(function (inputVal) {
                                    qFI10B.some(function (fi) {
                                        found = inputVal === fi;

                                        if (found) {
                                            exceptionCounter++;
                                        }

                                        return found;
                                    });

                                    return found;
                                });
                            } else if (item.value === '!10B') {
                                input.value.split('::').every(function (inputVal) {
                                    qFI10B.every(function (fi) {
                                        found = inputVal !== fi;
                                        forEvery = found;
                                        return found;
                                    });

                                    return found;
                                });

                                if (forEvery) {
                                    exceptionCounter++;
                                }
                            } else if (item.value[0] === '!') {
                                item.value.split('::').every(function (itemVal) {
                                    input.value.split('::').every(function (inputVal) {
                                        found = inputVal !== itemVal.slice(1);
                                        forEvery = found;
                                        return found;
                                    });

                                    return found;
                                });

                                if (forEvery) {
                                    exceptionCounter++;
                                }
                            } else {
                                item.value.split('::').some(function (itemVal) {
                                    input.value.split('::').some(function (inputVal) {
                                        found = inputVal === itemVal;

                                        if (found) {
                                            exceptionCounter++;
                                        }

                                        return found;
                                    });

                                    return found;
                                });
                            }
                        });

                        if (exceptionCounter === exception.length) {
                            solutionCounter = 0;
                        }

                        return exceptionCounter === exception.length;
                    });
                }

                if (solutionCounter === answersInputs.length && thisCard) {
                    thisCard.classList.remove('hide');
                }
            });

            if (mandatoryCodes) {
                mandatoryCodes.split('::').forEach(function (code) {
                    var card = qResults.querySelector('.results-grid .c-triage-result-card[data-solution-code="' + code + '"]');

                    if (card) {
                        card.classList.remove('hide');
                    }
                });
            }

            qTitle.textContent = qTitle.getAttribute('data-results-title');
            element.classList.add('results');
            _multiToggle([qContent, qLoading, qFooter], [qResults]);
            _trackEvent('resultsDisplayed');
        }

        function _getThisFlow(currentQ) {
            var thisFlow;

            qFlow.some(function (flow) {
                var flowFound = flow.question === currentQ;

                if (flowFound) {
                    thisFlow = flow;
                }
                return flowFound;
            });

            return thisFlow;
        }

        function _nextQuestion() {
            var currentQ = parseInt(element.getAttribute('data-current-question'), 10),
                currentA = qAnswers.querySelector('input[data-question="' + currentQ + '"]'),
                answersArray = currentA.value.split('::'),
                nextQ = 0,
                thisFlow = _getThisFlow(currentQ),
                mandatoryCodes = '';

            if (thisFlow) {
                if (thisFlow.defaultGoto) {
                    nextQ = thisFlow.defaultGoto;
                } else if (thisFlow.answers) {
                    if (answersArray.length > 1) {
                        if (qContent.getAttribute('data-is-multi') === 'false') {
                            answersArray.some(function (answer) {
                                var found = false;

                                thisFlow.answers.some(function (flowAnswer) {
                                    found = answer === flowAnswer.value && flowAnswer.priorityGoto;

                                    if (found) {
                                        nextQ = flowAnswer.defaultGoto;
                                    }

                                    return found;
                                });

                                return found;
                            });
                        } else {
                            answersArray.forEach(function (answer) {
                                thisFlow.answers.forEach(function (flowAnswer) {
                                    if (answer === flowAnswer.value) {
                                        if (flowAnswer.priorityGoto) {
                                            nextQ = flowAnswer.defaultGoto;
                                        }

                                        if (flowAnswer.mandatoryDisplay) {
                                            flowAnswer.mandatoryDisplay.forEach(function (code, j) {
                                                mandatoryCodes += code + (j + 1 < flowAnswer.mandatoryDisplay.length ? '::' : '');
                                            });
                                            element.setAttribute('data-mandatory-codes', mandatoryCodes);
                                        }
                                    }
                                });
                            });
                        }
                    } else {
                        thisFlow.answers.some(function (flowAnswer) {
                            var specialFlows = flowAnswer.specialFlows,
                                answerArray = answersArray[0],
                                flowCounter = 0,
                                found = answerArray === flowAnswer.value;

                            if (found) {
                                if (specialFlows) {
                                    specialFlows.forEach(function (specialFlow) {
                                        specialFlow.criterias.forEach(function (criteria) {
                                            var previousQuestionValue = qAnswers.querySelector('input[data-question="' + criteria.question + '"]').value;

                                            if (previousQuestionValue === criteria.value) {
                                                flowCounter++;
                                            }
                                        });

                                        if (flowCounter === specialFlow.criterias.length) {
                                            nextQ = specialFlow.goto;
                                        }
                                    });

                                    if (flowCounter === 0) {
                                        nextQ = flowAnswer.defaultGoto;
                                    }
                                } else {
                                    nextQ = flowAnswer.defaultGoto;
                                }
                            }

                            if (flowAnswer.mandatoryDisplay) {
                                flowAnswer.mandatoryDisplay.forEach(function (code, j) {
                                    mandatoryCodes += code + (j + 1 < flowAnswer.mandatoryDisplay.length ? '::' : '');
                                });
                                element.setAttribute('data-mandatory-codes', mandatoryCodes);
                            }

                            return found;
                        });
                    }
                }
            }

            _trackEvent('nextBtnClicked');

            if (nextQ !== 0) {
                _fillQuestion(nextQ);
            } else {
                element.classList.remove('first-question');
                _showResults();
                EDC.utils.initCarousel(carousel, 3, 4);
            }
        }

        function _resetTriageTool() {
            var cards = qResults.querySelectorAll('.c-triage-result-card:not(.hide)');

            cards.forEach(function (card) {
                card.classList.add('hide');
            });

            element.removeAttribute('data-mandatory-codes');
            element.classList.remove('results');
            qAnswers.innerHTML = '';
            _fillQuestion(1);
            _toggleEl(qResults, 'hide');
            _toggleEl(qContent, 'show');
            _toggleEl(qFooter, 'show');
            _toggleEl(nextQBtn, 'show');
            _trackEvent('triageToolReset');
        }

        function _createButtons() {
            var i,
                btn,
                answersQty = 2;

            qData.forEach(function (q) {
                if (q.type === 'buttons') {
                    if (q.answers.length > answersQty) {
                        answersQty = q.answers.length;
                    }
                }
            });

            for (i = 0; i < answersQty; i++) {
                btn = document.createElement('button');
                btn.setAttribute('type', 'button');
                btn.classList.add('edc-secondary-btn');
                btn.classList.add('hide');
                qOptions.appendChild(btn);
            }

            answerBtns = qOptions.querySelectorAll('button');
        }

        function _isIE() {
            var ua = window.navigator.userAgent,
                msie = ua.indexOf('MSIE ');

            return msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./);
        }

        function _sliderInit() {
            var el = qSlider.querySelector('.slider'),
                elValue = el.value,
                sliderValue = qSlider.querySelector('.slider-value span'),
                step = parseInt(elValue, 10) * 100 / parseInt(el.max, 10);

            sliderValue.textContent = el.value;
            el.style.background = 'linear-gradient(to right, #2f78c6 ' + step + '%, #ebebeb 0%)';
        }

        function _sliderChanged(e) {
            _sliderInit();
            _answerSelection(e);
        }

        // Attach events
        function _attachEvents() {
            var timeout,
                slider = qSlider.querySelector('.slider');

            EDC.utils.attachEvents(answerBtns, 'click', _answerSelection);
            EDC.utils.attachEvents(nextQBtn, 'click', _nextQuestion);
            EDC.utils.attachEvents(resetBtn, 'click', _resetTriageTool);
            EDC.utils.attachEvents(singleDropdown, 'change', _answerSelection);
            EDC.utils.attachEvents(multiDropdown.querySelector('.menu'), 'click', _answerSelection);

            if (_isIE()) {
                EDC.utils.attachEvents(slider, 'change', _sliderChanged);
            } else {
                EDC.utils.attachEvents(slider, 'input', _sliderChanged);
            }

            EDC.utils.attachEvents(window, 'resize', function () {
                clearTimeout(timeout);
                timeout = setTimeout(function () {
                    EDC.utils.attachEvents(element.querySelector('.q-dropdown .ui.dropdown'), 'change', _answerSelection);
                }, 500);
            });
        }

        EDC.utils.fetchJSON('GET', qJsonUrl, '', function (data) {
            if (data && data.questions.length > 0) {
                qData = data.questions;
                qFlow = data.flow;
                qSolutions = data.solutions;
                qFI10B = data.finantialInstitutionsList;
                PubSub.publish('qData', true);
            }
        });

        PubSub.subscribe('qData', function (ready) {
            if (ready && qData.length > 0) {
                _createButtons();
                _sliderInit();
                _fillQuestion(1);
                _toggleEl(qContent, 'show');
                _toggleEl(qLoading, 'hide');
                _attachEvents();
            }
        });
    }

    // Public methods
    function init() {
        var triageTools = document.querySelectorAll('.c-triage-tool:not([data-component-state="initialized"])');

        if (triageTools) {
            triageTools.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', triageToolJS.init);