var quoteCalculator = (function () {
    'use strict';

    function _initialize(element) {
        var space = /\s/g,
            comma = /,/g,
            dot = /\./g,
            keypressed = false,
            step1flag = false,
            step2flag = false,
            step3flag = false,
            d = document,
            calculatorTopLimit = parseInt(element.getAttribute('data-max-limit'), 10) || 500000;

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

        function _trackEvent(el, selector) {
            var qc = el.target.closest('#quote-calculator'),
                elem = el.target.closest(selector),
                destPage = _getdestination(elem),
                obj = {
                    eventInfo: {
                        eventComponent: qc.dataset.eventComponent,
                        eventType: elem.dataset.eventType,
                        eventName: elem.dataset.eventName,
                        eventAction: qc.dataset.eventAction,
                        eventText: elem.dataset.eventText, // get proper text
                        eventValue: elem.dataset.eventValue,
                        eventValue2: elem.dataset.eventValue2,
                        eventValue3: elem.dataset.eventValue3,
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: destPage, // get proper destination page
                        engagementType: elem.dataset.eventEngagement,
                        eventStage: elem.dataset.eventStage,
                        eventLevel: elem.dataset.eventLevel
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _resetSelect(selectId) {
            var select = element.querySelector('#' + selectId);

            select.disabled = true;
            select.value = 0;
        }

        function _repositionError(el) {
            // Adjust Zero Amount Error Position based on Height
            el.style.bottom = '-' + el.clientHeight + 'px';
        }

        function _addClass(id, className) {
            element.querySelector('#' + id).classList.add(className);
        }

        function _removeClass(id, className) {
            element.querySelector('#' + id).classList.remove(className);
        }

        function _hideElement(id) {
            _removeClass(id, 'show');
        }

        function _showElement(id) {
            var el = element.querySelector('#' + id);

            _addClass(id, 'show');
            if (id === 'zero-amount' && typeof (el) !== 'undefined' && el !== null) {
                _repositionError(el);
            }
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
            var dollarAmount = element.querySelector('#quote-calculator-amount').value,
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
            var amount = element.querySelector('#quote-calculator-amount').value.replace(comma, '').replace(space, '').replace(dot, ''),
                result;

            // Check if input is a Number, clear if not
            if (isNaN(amount)) {
                element.querySelector('#quote-calculator-amount').value = '';
                return;
            }

            if (amount === '') {
                _hideElement('quote-calculator-amount');
                _hideElement('zero-amount');
                _hideElement('coverage-error');
                _hideElement('quote-calculator-disclaimer-wrong-amount');
                _hideElement('coverage-success');
                _hideElement('quote-calculator-disclaimer-success');
                _resetSelect('coverage-duration');
                _resetSelect('coverage-payment-time');
                _removeClass('quote-calculator-amount', 'error');
            } else if (amount <= 0) {
                if (amount.length === 1) {
                    _addClass('quote-calculator-amount', 'error');
                    _showElement('zero-amount');
                    _resetSelect('coverage-duration');
                    _resetSelect('coverage-payment-time');
                    _hideElement('coverage-error');
                    _hideElement('quote-calculator-disclaimer-wrong-amount');
                    _hideElement('coverage-success');
                    _hideElement('quote-calculator-disclaimer-success');
                } else {
                    _hideElement('coverage-error');
                    _hideElement('quote-calculator-disclaimer-wrong-amount');
                    _hideElement('coverage-success');
                    _hideElement('quote-calculator-disclaimer-success');
                    return;
                }
            } else {
                _removeClass('quote-calculator-amount', 'error');
                _hideElement('zero-amount');

                if (amount > 0 && amount <= calculatorTopLimit) {
                    element.querySelector('#coverage-duration').disabled = false;
                    _hideElement('coverage-error');
                    _hideElement('quote-calculator-disclaimer-wrong-amount');

                } else if (amount > calculatorTopLimit) {
                    _resetSelect('coverage-duration');
                    _resetSelect('coverage-payment-time');
                    _showElement('coverage-error');
                    _showElement('quote-calculator-disclaimer-wrong-amount');
                    _hideElement('coverage-success');
                    _hideElement('quote-calculator-disclaimer-success');

                } else {
                    _hideElement('quote-calculator-amount');
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
            element.querySelector('#quote-calculator-amount').value = result;

            if (element.querySelector('#coverage-payment-time').value !== '0' && element.querySelector('#coverage-duration').value !== '0') {
                _calculateCoverage();
                _showElement('coverage-success');
                _showElement('quote-calculator-disclaimer-success');
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
            var backlist,
                i,
                alist,
                j;

            element.querySelector('#quote-calculator-amount').addEventListener('keyup', function (e) {
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
                    _trackEvent(e, '#quote-calculator-amount');// trigger only on first keypress
                }
            });

            element.querySelector('#quote-calculator-amount').addEventListener('blur', function (e) {
                var amount = element.querySelector('#quote-calculator-amount').value.replace(comma, '').replace(space, '').replace(dot, ''),
                    elem = element.querySelector('#quote-calculator-amount'),
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
                            _trackEvent(e, '#quote-calculator-amount');
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
                            _trackEvent(e, '#quote-calculator-amount');
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
                    _trackEvent(e, '#coverage-duration');
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
                    _hideElement('coverage-success');
                    _hideElement('quote-calculator-disclaimer-success');
                }
            });

            // Calculate the coverage after select change
            // Clear form if 0 value is selected
            element.querySelector('#coverage-payment-time').addEventListener('change', function (e) {
                var coverageDuration = element.querySelector('#coverage-duration'),
                    paymenttime = element.querySelector('#coverage-payment-time'),
                    amount = element.querySelector('#quote-calculator-amount').value.replace(comma, '').replace(space, '').replace(dot, ''),
                    result;

                element.querySelector('#coverage-success-value').innerHTML = coverageDuration.options[coverageDuration.selectedIndex].innerHTML;
                _calculateCoverage();
                _showElement('coverage-success');
                _showElement('quote-calculator-disclaimer-success');

                if (this.value === '0') {
                    _hideElement('coverage-success');
                    _hideElement('quote-calculator-disclaimer-success');
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
                    _trackEvent(e, '#coverage-payment-time');
                    step3flag = true;
                }
            });

            // Adding Clear Form functionality to the BACK TO START buttons
            backlist = element.querySelectorAll('#quote-calculator .back button');

            function _backlistClicked(e) {
                var tname;

                step1flag = false;
                step2flag = false;
                step3flag = false;
                e.preventDefault();
                tname = this.textContent.trim();
                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click - ' + tname.toLowerCase(),
                    'data-event-text': tname,
                    'data-event-value': '',
                    'data-event-value2': '',
                    'data-event-value3': '',
                    'data-event-engagement': '1',
                    'data-event-stage': '',
                    'data-event-level': 'secondary'
                });
                _trackEvent(e, 'button');

                element.querySelector('#quote-calculator-amount').value = '';
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
                _trackEvent(e, 'a');
                _activateLink(this);
            }

            for (i = 0; i < backlist.length; i++) {
                backlist[i].addEventListener('click', _backlistClicked);
            }

            alist = element.querySelectorAll('#coverage-error a');

            for (j = 0; j < alist.length; j++) {
                alist[j].addEventListener('click', _alistClicked);
            }

            element.querySelector('#quote-calculator-disclaimer-success .right .edc-primary-btn').addEventListener('click', function (e) {
                var tname = this.textContent.trim();

                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click - ' + tname.toLowerCase(),
                    'data-event-text': tname,
                    'data-event-value': '',
                    'data-event-value2': '',
                    'data-event-value3': '',
                    'data-event-engagement': '2',
                    'data-event-stage': '',
                    'data-event-level': 'primary'
                });
                _trackEvent(e, '.edc-primary-btn');
            });

            element.querySelector('#quote-calculator-disclaimer-success .right a').addEventListener('click', function (e) {
                var tname = this.text.replace(/\s/g, ' ');

                e.preventDefault();
                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click - ' + tname.toLowerCase(),
                    'data-event-text': tname,
                    'data-event-value': '',
                    'data-event-value2': '',
                    'data-event-value3': '',
                    'data-event-engagement': '2',
                    'data-event-stage': '',
                    'data-event-level': 'secondary'
                });
                _trackEvent(e, 'a');
                _activateLink(this);
            });

            element.querySelector('#quote-calculator-disclaimer-wrong-amount .right .edc-primary-btn span').addEventListener('click', function (e) { // learn more
                var tname = this.text.replace(/\s/g, ' ');

                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click - ' + tname.toLowerCase(),
                    'data-event-text': tname,
                    'data-event-value': '',
                    'data-event-value2': '',
                    'data-event-value3': '',
                    'data-event-engagement': '2',
                    'data-event-stage': '',
                    'data-event-level': 'primary'
                });
                _trackEvent(e, '.edc-primary-btn span');
            });

            element.querySelector('#quote-calculator-disclaimer-wrong-amount .right a').addEventListener('click', function (e) { // learn more
                var tname = this.text.replace(/\s/g, ' ');

                e.preventDefault();
                _setAttributes(this, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click - ' + tname.toLowerCase(),
                    'data-event-text': tname,
                    'data-event-value': '',
                    'data-event-value2': '',
                    'data-event-value3': '',
                    'data-event-engagement': '2',
                    'data-event-stage': '',
                    'data-event-level': 'secondary'
                });
                _trackEvent(e, 'a');
                _activateLink(this);
            });
        }

        _initListeners();
    }

    function init() {
        var qc = document.querySelectorAll('#quote-calculator:not([data-component-state="initialized"])');

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

document.addEventListener('DOMContentLoaded', quoteCalculator.init);
