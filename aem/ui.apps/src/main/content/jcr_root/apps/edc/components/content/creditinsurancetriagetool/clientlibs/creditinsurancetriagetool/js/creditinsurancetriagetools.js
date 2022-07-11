function InsuranceProductSelector(labelsStr) {
    'use strict';

    function _initialize(element) {
        // Private methods
        var labels = JSON.parse(labelsStr),
            currentStepIndex = 0,
            container = element,
            steps = [],
            lastVal,
            dtmcall = false,
            progress = container.querySelector('progress'),
            stepHistory = [0];

        if (typeof window.insuranceProductSelectorInitializedEvent === 'undefined') {
            window.insuranceProductSelectorInitializedEvent = document.createEvent('Event');
            insuranceProductSelectorInitializedEvent.initEvent('insuranceProductSelectorInitialized', true, true);
        }

        // Data Layer helper event
        function _trackEvent(el, selector) {
            var ips = el.target.closest('.insurance-product-selector'),
                elem = document.querySelector(selector),
                obj = {
                    eventInfo: {
                        eventComponent: ips.dataset.eventComponent,
                        eventType: elem.dataset.eventType,
                        eventName: elem.dataset.eventName,
                        eventAction: ips.dataset.eventAction,
                        eventText: elem.dataset.eventText,
                        eventValue: elem.dataset.eventValue,
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: elem.dataset.eventDestination,
                        engagementType: elem.dataset.eventEngagement,
                        eventStage: elem.dataset.eventStage,
                        eventStage2: elem.dataset.eventStage2,
                        eventLevel: elem.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _setAttributes(el, attrs) {
            var key;

            for (key in attrs) {
                if (key) {
                    el.setAttribute(key, attrs[key]);
                }
            }
        }

        function _updateProgress() {
            var stepHistoryStr = stepHistory.toString(),
                progressPaths = {
                    '0,2': 40,
                    '0,2,3': 60,
                    '0,2,3,4': 80,
                    '0,2,3,4,7': 100,
                    '0,2,3,4,5': 100,
                    '0,2,7': 100,
                    '0,2,3,7': 100,
                    '0,1': 10
                };

            progress.setAttribute('value', progressPaths[stepHistoryStr]);
        }

        function _makeNode(html) {
            var elem = document.createElement('div');

            elem.innerHTML = html;

            return elem.firstElementChild;
        }

        function _initRadioButtons(step) {
            var radioLabels = step.querySelectorAll('.radio-buttons label'),
                i;

            function _updateCheckedState() {
                var label,
                    input;

                for (i = 0; i < radioLabels.length; i++) {
                    label = radioLabels[i];
                    input = label.querySelector('input');
                    if (input.checked) {
                        radioLabels[i].classList.add('checked');
                        step.querySelector('.btn-next').classList.add('no-animate');
                        step.querySelector('.btn-next').classList.remove('disabled');
                        window.setTimeout(function () {
                            step.querySelector('.btn-next').classList.remove('no-animate');
                        }, 500);
                    } else {
                        radioLabels[i].classList.remove('checked');
                    }
                }
            }

            for (i = 0; i < radioLabels.length; i++) {
                radioLabels[i].addEventListener('click', _updateCheckedState);
            }
        }

        function _buildStepsHtml() {
            var step2,
                step3,
                step4,
                step5,
                step6,
                step7,
                step7Elem,
                bars,
                numBars,
                heightIncrement,
                i,
                bar,
                step8;

            step2 = '<div class="step step2">' +
                '<h2>' + labels.step2.heading + '</h2>' +
                '<div class="message">' + labels.step2.message + '</div>' +
                '<div class="message">' +
                '<button class="edc-primary-btn btn-sign-me-up">' +
                '<span>' + labels.step2.cta1 + '</span>' +
                '</button>' +
                '</div>' +
                '<div class="cta">' +
                '<button class="btn-back-to-start no-btn">' + labels.global.ctaBackToStart + '</button>' +
                '</div>' +
                '</div>';
            container.appendChild(_makeNode(step2));
            step3 = '<div class="step step3">' +
                '<h3>' + labels.step3.question + '</h3>' +
                '<div class="response">' +
                '<div class="radio-buttons">' +
                '<label>' +
                '<input name="step3-response" type="radio" value="yes" /> ' + labels.global.yes +
                '</label>' +
                '<label>' +
                '<input name="step3-response" type="radio" value="no" /> ' + labels.global.no +
                '</label>' +
                '</div>' +
                '</div>' +
                '<div class="cta">' +
                '<button class="btn-back no-btn">' + labels.global.ctaBackAStep + '</button>' +
                '<button class="edc-primary-btn btn-next disabled">' +
                '<span>' + labels.global.ctaNextQuestion + '</span>' +
                '</button>' +
                '</div>' +
                '</div>';
            container.appendChild(_makeNode(step3));
            step4 = '<div class="step step4">' +
                '<h3 class="question">' + labels.step4.question + '</h3>' +
                '<div class="response">' +
                '<div class="radio-buttons">' +
                '<label>' +
                '<input name="step4-response" type="radio" value="yes" /> ' + labels.global.yes +
                '</label>' +
                '<label>' +
                '<input name="step4-response" type="radio" value="no" /> ' + labels.global.no +
                '</label>' +
                '</div>' +
                '</div>' +
                '<div class="cta">' +
                '<button class="btn-back no-btn">' + labels.global.ctaBackAStep + '</button>' +
                '<button class="edc-primary-btn btn-next disabled">' +
                '<span>' + labels.global.ctaNextQuestion + '</span>' +
                '</button>' +
                '</div>' +
                '</div>';
            container.appendChild(_makeNode(step4));
            step5 = '<div class="step step5">' +
                '<h3 class="question">' + labels.step5.question + '</h3>' +
                '<div class="response">' +
                '<div class="radio-buttons">' +
                '<label>' +
                '<input name="step5-response" type="radio" value="a" />' + labels.step5.responseA +
                '</label>' +
                '<label>' +
                '<input name="step5-response" type="radio" value="b" />' + labels.step5.responseB +
                '</label>' +
                '<label>' +
                '<input name="step5-response" type="radio" value="c" />' + labels.step5.responseC +
                '</label>' +
                '</div>' +
                '</div>' +
                '<div class="cta">' +
                '<button class="btn-back no-btn">' + labels.global.ctaBackAStep + '</button>' +
                '<button class="edc-primary-btn btn-next disabled">' +
                '<span>' + labels.global.ctaNextQuestion + '</span>' +
                '</button>' +
                '</div>' +
                '</div>';
            container.appendChild(_makeNode(step5));
            step6 = '<div class="step step6">' +
                '<div class="product-recommendation">' +
                '<h2 class="recommended-product">' + labels.step6.heading + '</h2>' +
                '<div class="message">' + labels.step6.message + '</div>' +
                '<button class="edc-primary-btn btn-tell-me-more">' +
                '<span>' + labels.step6.linkText + '</span>' +
                '</button>' +
                '</div>' +
                '<div class="cta">' +
                '<button class="btn-back-to-start no-btn">' + labels.global.ctaBackToStart + '</button>' +
                '</div>' +
                '</div>';
            container.appendChild(_makeNode(step6));
            step7 = '<div class="step step7">' +
                '<h3 class="question">' + labels.step7.question + '</h3>' +
                '<div class="response">' +
                '<div class="range-slider">' +
                '<p class="value"></p>' +
                '<input id="q7-range-slider" name="q7-responses" type="range" min="0" max="2000000" step="10000"/>' +
                '<div class="labels"><p>' + labels.step7.label1 + '</p><p>' + labels.step7.label2 + '</p><p>' + labels.step7.label3 + '</p></div>' +
                '</div>' +
                '</div>' +
                '<div class="cta">' +
                '<button class="btn-back no-btn">' + labels.global.ctaBackAStep + '</button>' +
                '<button class="edc-primary-btn btn-next disabled">' +
                '<span>' + labels.global.ctaNextQuestion + '</span>' +
                '</button>' +
                '</div>' +
                '</div>';
            step7Elem = _makeNode(step7);
            bars = document.createElement('ul');
            bars.classList.add('bars');
            numBars = 33;
            heightIncrement = 90 / numBars;

            for (i = 0; i < numBars; i++) {
                bar = document.createElement('li');
                // bug 84437
                bar.style.height = (heightIncrement * (i + 1)) + 'px';
                bars.appendChild(bar);
            }

            step7Elem.querySelector('.range-slider').appendChild(bars);
            container.appendChild(step7Elem);
            step8 = '<div class="step step8">' +
                '<div class="product-recommendation">' +
                '<h2 class="recommended-product">' + labels.step8.heading + '</h2>' +
                '<div class="message">' + labels.step8.message + '</div>' +
                '<button class="edc-primary-btn btn-tell-me-more">' +
                '<span>' + labels.step8.linkText + '</span>' +
                '</button>' +
                '</div>' +
                '<div class="cta">' +
                '<button class="btn-back-to-start no-btn">' + labels.global.ctaBackToStart + '</button>' +
                '</div>' +
                '</div>';
            container.appendChild(_makeNode(step8));

            for (i = 0; i < 8; i++) {
                steps[i] = container.querySelector('.step' + (i + 1));
                _initRadioButtons(steps[i]);
            }
        }

        function _showStep(step) {
            var i;

            for (i = 0; i < steps.length; i++) {
                if (step === i) {
                    steps[i].classList.add('show');
                } else {
                    steps[i].classList.remove('show');
                }
            }

            if (stepHistory[currentStepIndex] !== step) {
                currentStepIndex++;
                stepHistory[currentStepIndex] = step;
            }

            _updateProgress();

            return steps[step];
        }

        function _goToPreviousStep() {
            var previousStep;

            if (currentStepIndex > 0) {
                stepHistory.splice(currentStepIndex);
                currentStepIndex--;
                previousStep = stepHistory[currentStepIndex];
                _showStep(previousStep);
            }
        }

        function _backToStart() {
            currentStepIndex = 0;
            stepHistory.splice(1);
            _showStep(0);
        }

        function _showStep8(n) {
            var step = _showStep(7);

            // _analyticsEventHandler({ step: 7, stepType: 'answer', response: 'pci' });
            lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue;

            step.querySelector('.btn-back-to-start').outerHTML = step.querySelector('.btn-back-to-start').outerHTML;
            step.querySelector('.btn-back-to-start').addEventListener('click', function (e) {
                e.preventDefault();
                _backToStart();
            });

            step.querySelector('h2').outerHTML = step.querySelector('h2').outerHTML;
            step.querySelector('h2').addEventListener('click', function (e) {
                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click - product',
                    'data-event-text': labels.global.ctaTellMeMore,
                    'data-event-destination': labels.step8.ctaURL,
                    'data-event-value': 'ps-a' + n + '-tell-me-more',
                    'data-event-engagement': '2',
                    'data-event-stage': '',
                    'data-event-stage2': 'A' + n,
                    'data-event-level': 'primary'
                });
                if (lastVal !== 'ps-a' + n + '-tell-me-more') {
                    _trackEvent(e, '.step8 h2');
                }
                window.open(labels.step8.ctaURL, labels.step8.linkTarget);
            });

            step.querySelector('.btn-tell-me-more').outerHTML = step.querySelector('.btn-tell-me-more').outerHTML;
            step.querySelector('.btn-tell-me-more').addEventListener('click', function (e) {
                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click - product',
                    'data-event-text': labels.global.ctaTellMeMore,
                    'data-event-destination': labels.step8.ctaURL,
                    'data-event-value': 'ps-a' + n + '-tell-me-more',
                    'data-event-engagement': '2',
                    'data-event-stage': '',
                    'data-event-stage2': 'A' + n,
                    'data-event-level': 'primary'
                });

                if (lastVal !== 'ps-a' + n + '-tell-me-more') {
                    _trackEvent(e, '.step8 .btn-tell-me-more');
                }

                window.open(labels.step8.ctaURL, labels.step8.linkTarget);
            });
        }

        function _showStep6() {
            var step = _showStep(5);

            lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue;
            // _analyticsEventHandler({ step: 6, stepType: 'answer', response: 'tp' });

            step.querySelector('.btn-back-to-start').outerHTML = step.querySelector('.btn-back-to-start').outerHTML;
            step.querySelector('.btn-back-to-start').addEventListener('click', function (e) {
                e.preventDefault();
                _backToStart();
            });

            step.querySelector('h2').outerHTML = step.querySelector('h2').outerHTML;
            step.querySelector('h2').addEventListener('click', function (e) {
                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click – product',
                    'data-event-text': this.innerText,
                    'data-event-value': 'ps-q4.1-tell me more',
                    'data-event-destination': labels.step6.ctaURL,
                    'data-event-engagement': '2',
                    'data-event-stage': '',
                    'data-event-stage2': 'A4.1',
                    'data-event-level': 'primary'
                });

                if (lastVal !== 'ps-q4.1-tell me more') {
                    _trackEvent(e, '.step6 h2');
                }

                window.open(labels.step6.ctaURL, labels.step6.linkTarget);
            });

            step.querySelector('.btn-tell-me-more').outerHTML = step.querySelector('.btn-tell-me-more').outerHTML;
            step.querySelector('.btn-tell-me-more').addEventListener('click', function (e) {
                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click – product',
                    'data-event-text': this.innerText,
                    'data-event-value': 'ps-q4.1-tell me more',
                    'data-event-destination': labels.step6.ctaURL,
                    'data-event-engagement': '2',
                    'data-event-stage': '',
                    'data-event-stage2': 'A4.1',
                    'data-event-level': 'primary'
                });

                if (lastVal !== 'ps-q4.1-tell me more') {
                    _trackEvent(e, '.step6 .btn-tell-me-more');
                }
                window.open(labels.step6.ctaURL, labels.step6.linkTarget);
            });
        }

        function _showStep5() {
            var step = _showStep(4);

            step.querySelector('.btn-next').outerHTML = step.querySelector('.btn-next').outerHTML;
            step.querySelector('.btn-next').addEventListener('click', function (e) {
                // A -> Step 8
                // B -> Step 6
                // C -> Step 8
                // D -> Step 8

                var response = step.querySelector('input[name=step5-response]:checked').value;

                lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue;
                // _analyticsEventHandler({ step: 5, stepType: 'question', response: response });

                if (response === 'a') { // within 180 days
                    _setAttributes(this, {
                        'data-event-type': 'button',
                        'data-event-name': 'button click - q4 next',
                        'data-event-text': labels.global.ctaNextQuestion,
                        'data-event-value': 'ps-q4-within 180',
                        'data-event-destination': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'Q4',
                        'data-event-stage2': 'A4.1',
                        'data-event-level': 'primary'
                    });

                    if (lastVal !== 'ps-q4-within 180') {
                        _trackEvent(e, '.step5 .btn-next');
                    }

                    _showStep6();
                } else if (response === 'b') {
                    _setAttributes(this, {
                        'data-event-type': 'button',
                        'data-event-name': 'button click - q4 next',
                        'data-event-text': labels.global.ctaNextQuestion,
                        'data-event-value': 'ps-q4-after 180',
                        'data-event-destination': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'Q4',
                        'data-event-stage2': 'A4.2',
                        'data-event-level': 'primary'
                    });

                    if (lastVal !== 'ps-q4-after 180') {
                        _trackEvent(e, '.step5 .btn-next');
                    }

                    _showStep8('4.2');
                } else if (response === 'c') {
                    _setAttributes(this, {
                        'data-event-type': 'button',
                        'data-event-name': 'button click - q4 next',
                        'data-event-text': labels.global.ctaNextQuestion,
                        'data-event-value': 'ps-q4-other',
                        'data-event-destination': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'Q4',
                        'data-event-stage2': 'A4.3',
                        'data-event-level': 'primary'
                    });

                    if (lastVal !== 'ps-q4-other') {
                        _trackEvent(e, '.step5 .btn-next');
                    }

                    _showStep8('4.3');
                }
            });

            step.querySelector('.btn-back').outerHTML = step.querySelector('.btn-back').outerHTML;
            step.querySelector('.btn-back').addEventListener('click', function (e) {
                e.preventDefault();
                _goToPreviousStep();
            });
        }

        function _showStep4() {
            var step = _showStep(3);

            step.querySelector('.btn-next').outerHTML = step.querySelector('.btn-next').outerHTML;
            step.querySelector('.btn-next').addEventListener('click', function (e) {
                // A Yes -> Step 8
                // B No -> Step 5

                var response = step.querySelector('input[name=step4-response]:checked').value;

                lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue;
                // _analyticsEventHandler({ step: 4, stepType: 'question', response: response });

                if (response === 'yes') {
                    _setAttributes(this, {
                        'data-event-type': 'button',
                        'data-event-name': 'button click - q3 next',
                        'data-event-text': labels.global.ctaNextQuestion,
                        'data-event-value': 'ps-q3-' + response,
                        'data-event-destination': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'Q3',
                        'data-event-stage2': 'A3',
                        'data-event-level': 'primary'
                    });

                    if (lastVal !== 'ps-q3-yes') {
                        _trackEvent(e, '.step4 .btn-next');
                    }

                    _showStep8('3');
                } else if (response === 'no') {
                    _setAttributes(this, {
                        'data-event-type': 'button',
                        'data-event-name': 'button click - q3 next',
                        'data-event-text': labels.global.ctaNextQuestion,
                        'data-event-value': 'ps-q3-' + response,
                        'data-event-destination': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'Q3',
                        'data-event-stage2': '',
                        'data-event-level': 'primary'
                    });

                    if (lastVal !== 'ps-q3-no') {
                        _trackEvent(e, '.step4 .btn-next');
                    }

                    _showStep5();
                }
            });

            step.querySelector('.btn-back').outerHTML = step.querySelector('.btn-back').outerHTML;
            step.querySelector('.btn-back').addEventListener('click', function (e) {
                e.preventDefault();
                _goToPreviousStep();
            });
        }

        function _showStep3() {
            var step = _showStep(2);

            step.querySelector('.btn-next').outerHTML = step.querySelector('.btn-next').outerHTML;
            step.querySelector('.btn-next').addEventListener('click', function (e) {
                /*
                A No -> Step 4?
                B Yes -> Step 8
                */

                var response = step.querySelector('input[name=step3-response]:checked').value;

                lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue;
                // _analyticsEventHandler({ step: 3, stepType: 'question', response: response });

                if (response === 'yes') {
                    // Q2 Yes
                    _setAttributes(this, {
                        'data-event-type': 'button',
                        'data-event-name': 'button click - q2 next',
                        'data-event-text': labels.global.ctaNextQuestion,
                        'data-event-value': 'ps-q2-yes',
                        'data-event-destination': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'Q2',
                        'data-event-stage2': 'A2',
                        'data-event-level': 'primary'
                    });

                    if (lastVal !== 'ps-q2-yes') {
                        _trackEvent(e, '.step3 .btn-next');
                    }

                    _showStep8('2');
                } else if (response === 'no') {
                    _setAttributes(this, {
                        'data-event-type': 'button',
                        'data-event-name': 'button click - q2 next',
                        'data-event-text': labels.global.ctaNextQuestion,
                        'data-event-value': 'ps-q2-no',
                        'data-event-destination': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'Q2',
                        'data-event-stage2': '',
                        'data-event-level': 'primary'
                    });

                    if (lastVal !== 'ps-q2-no') {
                        _trackEvent(e, '.step3 .btn-next');
                    }

                    _showStep4();
                }
            });

            step.querySelector('.btn-back').outerHTML = step.querySelector('.btn-back').outerHTML;
            step.querySelector('.btn-back').addEventListener('click', function (e) {
                e.preventDefault();
                _goToPreviousStep();
            });
        }

        function _showStep2() {
            var step = _showStep(1);
            // _analyticsEventHandler({ step: 2, stepType: 'answer', response: 'about' });

            step.querySelector('.btn-sign-me-up span').outerHTML = step.querySelector('.btn-sign-me-up span').outerHTML;
            step.querySelector('.btn-sign-me-up span').addEventListener('click', function (e) {
                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'comms sign-up link click',
                    'data-event-text': labels.step2.cta1,
                    'data-event-value': 'ps-a1-sign up',
                    'data-event-destination': labels.step2.cta1Url,
                    'data-event-engagement': '2',
                    'data-event-stage': '',
                    'data-event-stage2': 'A1',
                    'data-event-level': 'primary'
                });
                lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue;

                if (lastVal !== 'ps-a1-sign up') {
                    _trackEvent(e, '.btn-sign-me-up span');
                }

                window.open(labels.step2.cta1Url, labels.step2.linkTarget ? labels.step2.linkTarget : '_new');
            });

            step.querySelector('.btn-back-to-start').outerHTML = step.querySelector('.btn-back-to-start').outerHTML;
            step.querySelector('.btn-back-to-start').addEventListener('click', function (e) {
                e.preventDefault();
                _backToStart();
            });
        }

        function _showStep1() {
            var step = _showStep(0),
                options = step.querySelectorAll('datalist option'),
                labelsDiv = document.createElement('div'),
                i,
                option,
                label;

            labelsDiv.classList.add('labels');

            for (i = 0; i < options.length; i++) {
                option = options[i];
                label = document.createElement('p');
                label.innerHTML = option.innerHTML;
                labelsDiv.appendChild(label);
            }

            step.querySelector('.range-slider').appendChild(labelsDiv);
            rangeSlider.create(container.querySelector('#q1-range-slider'));
            step.querySelector('.btn-next').addEventListener('click', function (e) {
                var response = step.querySelector('#q1-range-slider').value,
                    responseInt = parseInt(step.querySelector('#q1-range-slider').value, 10) + 1,
                    sub = '#q1-tickmarks option:nth-child(' + responseInt + ')',
                    responsetext = step.querySelector(sub).innerHTML;

                // _analyticsEventHandler({ step: 1, stepType: 'question', response: response });

                if (response === '0') {
                    _setAttributes(step.querySelector('#q1-range-slider'), {
                        'data-event-type': 'button',
                        'data-event-name': 'button click - q1 next',
                        'data-event-text': labels.global.ctaNextQuestion,
                        'data-event-value': 'ps-q1-' + responsetext.toLowerCase(),
                        'data-event-destination': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'Q1',
                        'data-event-stage2': 'A1',
                        'data-event-level': 'primary'
                    });
                    _showStep2();
                } else if (response === '1' || response === '2') { // difference is stage 2
                    _setAttributes(step.querySelector('#q1-range-slider'), {
                        'data-event-type': 'button',
                        'data-event-name': 'button click - q1 next',
                        'data-event-text': labels.global.ctaNextQuestion,
                        'data-event-value': 'ps-q1-' + responsetext.toLowerCase(),
                        'data-event-destination': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'Q1',
                        'data-event-stage2': '',
                        'data-event-level': 'primary'
                    });
                    _showStep3();
                }
                if (!dtmcall) {
                    _trackEvent(e, '#q1-range-slider');
                    dtmcall = true;
                } else {
                    lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue;

                    if (lastVal !== 'ps-q1-' + responsetext.toLowerCase()) {
                        _trackEvent(e, '#q1-range-slider');
                    }
                }
            });
        }

        _buildStepsHtml();
        _showStep1();
    }

    // Public methods
    function init() {
        var insuranceProductSelector = document.querySelectorAll('section.insurance-product-selector:not([data-component-state="initialized"])');

        if (insuranceProductSelector) {
            insuranceProductSelector.forEach(function (element) {
                _initialize(element);
                element.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    init();
}

if (typeof insuranceProductSelectorLoadedEvent !== 'undefined') {
    document.dispatchEvent(insuranceProductSelectorLoadedEvent);
}