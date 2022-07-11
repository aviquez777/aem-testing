var sciQuoteCalculator = (function () {
    'use strict';

    function _initialize(element) {
        var space = /\s/g,
            comma = /,/g,
            dot = /\./g,
            keypressed = false,
            step1flag = false,
            step2flag = false,
            step3flag = false,
            show = 'show',
            hide = 'hide',
            d = document,
            resetBtn = element.querySelector('.actions button.reset'),
            alist = element.querySelector('#coverage-error a'),
            calculatorTopLimit = parseInt(element.getAttribute('data-max-limit'), 10) || 500000,
            amountEl = element.querySelector('#sci-quote-calculator-amount'),
            amountElZero = element.querySelector('#zero-amount'),
            coverageErrorEl = element.querySelector('#coverage-error'),
            disclaimerEl = element.querySelector('.sci-quote-calculator-disclaimer'),
            coverageSuccessEl = element.querySelector('#coverage-success'),
            wrongAmountEl = element.querySelector('.wrong-amount'),
            validAmountEl = element.querySelector('.valid-amount'),
            noteDisclaimer = validAmountEl.querySelector('.note.disclaimer'),
            notificationsValid = element.querySelector('.notifications.valid'),
            actions = disclaimerEl.querySelector('.actions'),
            startAppBtn = actions.querySelector('a.button'),
            sendQuestionBtn = validAmountEl.querySelector('a'),
            shareYourDetailsForm = element.closest('.c-sci-quote-calculator').querySelector('.c-share-your-details-form'),
            formResetBtn = shareYourDetailsForm.querySelector('.actions button[type="button"]'),
            dataVersion = 'a';

        // Private functions
        function _getdestination(s) {
            var first,
                second;

            if (s.getAttribute('href')) {
                if (s.getAttribute('href').indexOf('#') >= 0) {
                    return '';
                }
                return s.getAttribute('href').toLowerCase();
            } else if (s.getAttribute('onclick')) {
                first = s.getAttribute('onclick').indexOf('\'');
                second = s.getAttribute('onclick').indexOf('\'', first + 1);
                return s.getAttribute('onclick').slice(first + 1, second);
            } else if (s.parentElement.getAttribute('onclick')) {
                first = s.parentElement.getAttribute('onclick').indexOf('\'');
                second = s.parentElement.getAttribute('onclick').indexOf('\'', first + 1);
                return s.parentElement.getAttribute('onclick').slice(first + 1, second);
            }
            return '';
        }

        // Data Layer
        function _trackCalculatorEvents(el, selector) {
            var elem = el.target.closest(selector),
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: elem.dataset.eventType,
                        eventName: elem.dataset.eventName,
                        eventAction: element.dataset.eventAction,
                        eventText: elem.dataset.eventText,
                        eventValue: elem.dataset.eventValue,
                        eventValue2: elem.dataset.eventValue2,
                        eventValue3: elem.dataset.eventValue3,
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: _getdestination(elem),
                        engagementType: elem.dataset.eventEngagement,
                        eventStage: elem.dataset.eventStage,
                        eventLevel: elem.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _trackBtn(e) {
            var el = e.currentTarget,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent,
                        eventType: element.dataset.eventType,
                        eventName: el.dataset.eventName,
                        eventAction: element.dataset.eventAction,
                        eventText: el.dataset.englishText,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: _getdestination(el),
                        engagementType: element.dataset.engagementType,
                        eventLevel: element.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _resetSelect(selectId) {
            var select = element.querySelector('#' + selectId);

            select.disabled = true;
            select.value = 0;
        }

        function _addSpaces(nStr) {
            var x = nStr.split('.'),
                x1 = x[0],
                x2 = x.length > 1 ? '.' + x[1] : '',
                rgx = /(\d+)(\d{3})/;

            while (rgx.test(x1)) {
                x1 = x1.replace(rgx, '$1 $2');
            }
            return x1 + x2;
        }

        function _addCommas(nStr) {
            var x = nStr.split('.'),
                x1 = x[0],
                x2 = x.length > 1 ? '.' + x[1] : '',
                rgx = /(\d+)(\d{3})/;

            while (rgx.test(x1)) {
                x1 = x1.replace(rgx, '$1,$2');
            }
            return x1 + x2;
        }

        function _searchPercentage(PaymentTermDays, CoveragePeriod, myArray) {
            var i;

            for (i = 0; i < myArray.length; i++) {
                if (myArray[i].PaymentTermDays === PaymentTermDays && myArray[i].CoveragePeriod === CoveragePeriod) {
                    return myArray[i];
                }
            }
            return false;
        }

        function _calculateCoverage() {
            var dollarAmount = element.querySelector('#sci-quote-calculator-amount').value,
                coverageTime,
                coverageDuration,
                percentages,
                min,
                max,
                PREMIUM_PERCENTAGES = [
                    {
                        PaymentTermDays: '0-90',
                        Risk: 'LOW',
                        CoveragePeriod: '90',
                        PremiumPercentageLowRisk: 0.554,
                        PremiumPercentageHighRisk: 0.698
                    },
                    {
                        PaymentTermDays: '0-90',
                        Risk: 'LOW',
                        CoveragePeriod: '180',
                        PremiumPercentageLowRisk: 1.108,
                        PremiumPercentageHighRisk: 1.396
                    },
                    {
                        PaymentTermDays: '91-180',
                        Risk: 'LOW',
                        CoveragePeriod: '90',
                        PremiumPercentageLowRisk: 0.969,
                        PremiumPercentageHighRisk: 1.22
                    },
                    {
                        PaymentTermDays: '91-180',
                        Risk: 'LOW',
                        CoveragePeriod: '180',
                        PremiumPercentageLowRisk: 1.938,
                        PremiumPercentageHighRisk: 2.44
                    }
                ];

            if (d.documentElement.attributes.lang && d.documentElement.attributes.lang.value === 'fr') {
                dollarAmount = dollarAmount.replace(space, '');
            } else {
                dollarAmount = dollarAmount.replace(comma, '');
            }

            if (dollarAmount < 5000) {
                dollarAmount = 5000; // requested by the business: if they enter an amount less than $5000, use $5000 as the cover amount in the calculation, but don't show this in the UI
            }

            coverageTime = element.querySelector('#coverage-payment-time').value;
            coverageDuration = element.querySelector('#coverage-duration').value;
            percentages = _searchPercentage(coverageTime, coverageDuration, PREMIUM_PERCENTAGES);

            if (percentages) {
                min = (percentages.PremiumPercentageLowRisk * dollarAmount / 100).toFixed(2);
                max = (percentages.PremiumPercentageHighRisk * dollarAmount / 100).toFixed(2);

                if (d.documentElement.attributes.lang && d.documentElement.attributes.lang.value === 'fr') {
                    element.querySelector('#min-coverage-amount').innerHTML = _addSpaces(min) + ' $';
                    element.querySelector('#max-coverage-amount').innerHTML = _addSpaces(max) + ' $';
                } else {
                    element.querySelector('#min-coverage-amount').innerHTML = '$ ' + _addCommas(min);
                    element.querySelector('#max-coverage-amount').innerHTML = '$ ' + _addCommas(max);
                }
            }
        }

        function _validateAmount() {
            var amount = amountEl.value.replace(comma, '').replace(space, '').replace(dot, ''),
                result;

            // Check if input is a Number, clear if not
            if (isNaN(amount)) {
                element.querySelector('#sci-quote-calculator-amount').value = '';
                return;
            }

            amountElZero.classList.remove('show');

            if (amount === '') {
                amountEl.classList.remove(show);
                coverageErrorEl.classList.remove(show);
                disclaimerEl.classList.remove(show);
                coverageSuccessEl.classList.remove(show);
                wrongAmountEl.classList.remove(show);
                validAmountEl.classList.remove(show);
                _resetSelect('coverage-duration');
                _resetSelect('coverage-payment-time');
                amountEl.classList.remove('error');
            } else if (amount <= 0) {
                if (amount.length === 1) {
                    amountEl.classList.add('error');
                    amountElZero.classList.add('show');
                    amountEl.classList.add(show);
                    _resetSelect('coverage-duration');
                    _resetSelect('coverage-payment-time');
                    coverageErrorEl.classList.remove(show);
                    wrongAmountEl.classList.remove(show);
                    validAmountEl.classList.remove(show);
                    disclaimerEl.classList.remove(show);
                    coverageSuccessEl.classList.remove(show);
                } else {
                    coverageErrorEl.classList.remove(show);
                    wrongAmountEl.classList.remove(show);
                    validAmountEl.classList.remove(show);
                    disclaimerEl.classList.remove(show);
                    coverageSuccessEl.classList.remove(show);
                    return;
                }
            } else {
                amountEl.classList.remove('error');
                amountEl.classList.remove(show);

                if (amount > 0 && amount <= calculatorTopLimit) {
                    element.querySelector('#coverage-duration').disabled = false;
                    coverageErrorEl.classList.remove(show);
                    wrongAmountEl.classList.remove(show);
                    validAmountEl.classList.add(show);
                    disclaimerEl.classList.remove(show);
                } else if (amount > calculatorTopLimit) {
                    _resetSelect('coverage-duration');
                    _resetSelect('coverage-payment-time');
                    coverageErrorEl.classList.add(show);
                    wrongAmountEl.classList.add(show);
                    validAmountEl.classList.remove(show);
                    disclaimerEl.classList.add(show);
                    coverageSuccessEl.classList.remove(show);
                } else {
                    amountEl.classList.remove(show);
                    wrongAmountEl.classList.remove(show);
                    validAmountEl.classList.remove(show);
                }
            }

            // Check language and format the Number
            // If FR use blank space as thousand separator
            // If EN use comma as thousand separator

            if (d.documentElement.attributes.lang && d.documentElement.attributes.lang.value === 'fr') {
                amount = amount.replace(space, '');
                result = _addSpaces(amount);
            } else {
                amount = amount.replace(comma, '');
                result = _addCommas(amount);
            }
            element.querySelector('#sci-quote-calculator-amount').value = result;

            if (element.querySelector('#coverage-payment-time').value !== '0' && element.querySelector('#coverage-duration').value !== '0') {
                _calculateCoverage();
                disclaimerEl.classList.add(show);
                validAmountEl.classList.add(show);
                coverageSuccessEl.classList.add(show);
            }
        }

        function _setAttributes(el, attrs) {
            var i;

            for (i = 0; i < attrs.length; i++) {
                el.setAttribute(i, attrs[i]);
            }
        }

        function _activateLink(elem) {
            setTimeout(function () {
                if (elem.getAttribute('target')) {
                    if (elem.getAttribute('target') === '_blank') {
                        _satellite.track('dl-event tracking');
                    }
                    window.open(elem.getAttribute('href'), elem.getAttribute('target'));
                } else {
                    window.location = elem.getAttribute('href');
                }
            }, 500);
        }

        function _initListeners() {
            element.querySelector('#sci-quote-calculator-amount').addEventListener('keyup', function (e) {
                _validateAmount();
                if (!keypressed) {
                    keypressed = true;
                    _setAttributes(this, {
                        'data-event-type': 'text field',
                        'data-event-name': 'text field - start',
                        'data-event-text': '',
                        'data-event-value': '',
                        'data-event-value2': '',
                        'data-event-value3': '',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'start',
                        'data-event-level': ''
                    });
                    _trackCalculatorEvents(e, '#sci-quote-calculator-amount'); // trigger only on first keypress
                }
            });

            element.querySelector('#sci-quote-calculator-amount').addEventListener('blur', function (e) {
                var amount = element.querySelector('#sci-quote-calculator-amount').value.replace(comma, '').replace(space, '').replace(dot, ''),
                    elem = element.querySelector('#sci-quote-calculator-amount'),
                    lastVal;

                if (amount <= calculatorTopLimit && amount !== '') {
                    lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue; // reduce duplicates
                    if (lastVal !== amount) {
                        _setAttributes(elem, {
                            'data-event-type': 'text field',
                            'data-event-name': 'text field input - =<500K',
                            'data-event-text': 'equal or less than 500K',
                            'data-event-value': '',
                            'data-event-value2': '',
                            'data-event-value3': '',
                            'data-event-engagement': '1.5',
                            'data-event-stage': 'Q1',
                            'data-event-level': ''
                        });
                        if (!step1flag) {
                            _trackCalculatorEvents(e, '#sci-quote-calculator-amount');
                            step1flag = true;
                        }
                    }
                } else if (amount > calculatorTopLimit) {
                    lastVal = digitalData.events.slice(-1)[0].eventInfo.eventName;
                    if (lastVal !== 'text field input - >500K') {
                        _setAttributes(elem, {
                            'data-event-type': 'text field',
                            'data-event-name': 'text field input - >500K',
                            'data-event-text': 'greater than 500K',
                            'data-event-value': amount,
                            'data-event-value2': '',
                            'data-event-value3': '',
                            'data-event-engagement': '1.5',
                            'data-event-stage': 'Q1',
                            'data-event-level': ''
                        });
                        if (!step1flag) {
                            _trackCalculatorEvents(e, '#sci-quote-calculator-amount');
                            step1flag = true;
                        }
                    }
                }
            });

            // Enable Payment Type Select after duration is selected
            // Clear form if 0 value is selected
            element.querySelector('#coverage-duration').addEventListener('change', function (e) {
                var coveragePayTime = element.querySelector('#coverage-payment-time'),
                    coverageDuration = element.querySelector('#coverage-duration');

                _setAttributes(coverageDuration, {
                    'data-event-type': 'drop-down',
                    'data-event-name': 'drop-down selection - step 2',
                    'data-event-text': '',
                    'data-event-value': '',
                    'data-event-value2': '',
                    'data-event-value3': '',
                    'data-event-engagement': '1.5',
                    'data-event-stage': 'Q2',
                    'data-event-level': ''
                });

                if (!step2flag) {
                    _trackCalculatorEvents(e, '#coverage-duration');
                    step2flag = true;
                }

                if (this.value !== '0') {
                    coveragePayTime.disabled = false;
                    element.querySelector('#coverage-success-value').innerHTML = coverageDuration.options[coverageDuration.selectedIndex].innerHTML;

                    if (element.querySelector('#coverage-payment-time').value !== '0') {
                        _calculateCoverage();
                    }
                } else {
                    _resetSelect('coverage-payment-time');
                    coverageSuccessEl.classList.remove(show);
                    disclaimerEl.classList.remove(show);
                }
            });

            // Calculate the coverage after select change
            // Clear form if 0 value is selected
            element.querySelector('#coverage-payment-time').addEventListener('change', function (e) {
                var coverageDuration = element.querySelector('#coverage-duration'),
                    paymenttime = element.querySelector('#coverage-payment-time'),
                    amount = element.querySelector('#sci-quote-calculator-amount').value.replace(comma, '').replace(space, '').replace(dot, ''),
                    result;

                element.querySelector('#coverage-success-value').innerHTML = coverageDuration.options[coverageDuration.selectedIndex].innerHTML;
                _calculateCoverage();
                coverageSuccessEl.classList.add(show);


                disclaimerEl.classList.add(show);
                dataVersion = element.getAttribute('data-version').toLowerCase();

                if (dataVersion === 'b') {
                    notificationsValid.classList.remove(show);
                    notificationsValid.classList.add(hide);
                    actions.classList.add(hide);
                    noteDisclaimer.classList.add(hide);

                    if (shareYourDetailsForm) {
                        shareYourDetailsForm.classList.add('no-border');
                    }
                }


                if (this.value === '0') {
                    coverageSuccessEl.classList.remove(show);
                    disclaimerEl.classList.remove(show);
                }
                result = element.querySelector('#min-coverage-amount').innerHTML.replace('$', '').replace(space, '') + ';' +
                    element.querySelector('#max-coverage-amount').innerHTML.replace('$', '').replace(space, '');
                _setAttributes(paymenttime, {
                    'data-event-type': 'drop-down',
                    'data-event-name': 'drop-down selection - final',
                    'data-event-text': result,
                    'data-event-value': 'Q1.' + amount,
                    'data-event-value2': 'Q2.' + coverageDuration.options[coverageDuration.selectedIndex].innerHTML,
                    'data-event-value3': 'Q3.' + paymenttime.options[paymenttime.selectedIndex].innerHTML,
                    'data-event-engagement': '1.5',
                    'data-event-stage': 'Q3',
                    'data-event-level': ''
                });
                if (!step3flag) {
                    _trackCalculatorEvents(e, '#coverage-payment-time');
                    step3flag = true;
                }
            });
        }

        function _resetClicked(e) {
            step1flag = false;
            step2flag = false;
            step3flag = false;
            e.preventDefault();
            _trackBtn(e);
            element.querySelector('#sci-quote-calculator-amount').value = '';
            _validateAmount();
        }

        function _alistClicked(e) {
            e.preventDefault();
            _setAttributes(this, {
                'data-event-type': 'link',
                'data-event-name': 'link click - product',
                'data-event-text': this.text.replace(/\s/g, ' '),
                'data-event-value': '',
                'data-event-value2': '',
                'data-event-value3': '',
                'data-event-engagement': '1',
                'data-event-stage': '',
                'data-event-level': 'secondary'
            });
            _trackCalculatorEvents(e, 'a');
            _activateLink(this);
        }

        function _fixTitleLines() {
            var els = element.querySelectorAll('.content div.show label'),
                highestHeight = 0,
                isMobile = window.innerWidth < window.EDC.props.media.md;

            if (isMobile) {
                els.forEach(function (el) {
                    el.removeAttribute('style');
                });
            } else {
                els.forEach(function (el) {
                    var labelHeight;

                    el.removeAttribute('style');
                    labelHeight = el.offsetHeight;
                    highestHeight = highestHeight < labelHeight ? labelHeight : highestHeight;
                });

                els.forEach(function (el) {
                    el.style.height = highestHeight + 'px';
                });
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(resetBtn, 'click', _resetClicked);
            EDC.utils.attachEvents(formResetBtn, 'click', _resetClicked);
            EDC.utils.attachEvents(alist, 'click', _alistClicked);
            EDC.utils.attachEvents(startAppBtn, 'click', _trackBtn);
            EDC.utils.attachEvents(sendQuestionBtn, 'click', _trackBtn);
            EDC.utils.attachEvents(window, 'resize', _fixTitleLines);
        }

        setTimeout(function () {
            _fixTitleLines();
        }, 1000);

        _initListeners();
        _attachEvents();
    }

    function init() {
        var qc = document.querySelectorAll('.c-sci-quote-calculator:not([data-component-state="initialized"])');

        if (qc) {
            qc.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', sciQuoteCalculator.init);