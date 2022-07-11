var calculatorInsurance = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private methods
        var uid = element.getAttribute('data-uuid'),
            rows = element.querySelectorAll('.row'),
            inputs = [],
            isFrench = document.documentElement.attributes.lang && document.documentElement.attributes.lang.value === 'fr' || false,
            keypressed = false,
            maxCost = 0,
            calculatorTopLimit = parseInt(element.getAttribute('data-max-limit'), 10) || 500000,
            RE = {
                comma: /,/g,
                dot: /\./g,
                nonDigits: /[^0-9]+/g,
                space: /\s/g
            },
            lists = {
                error: ['coverage-error'],
                okay: ['coverage-success']
            },
            outputs = {
                salesWith: null,
                salesWo: null,
                profitWith: null,
                profitWo: null,
                cost: null,
                recovery: null,
                netWith: null,
                netWo: null
            },
            steps = {
                one: false,
                two: false,
                three: false,
                four: false
            };

        function _addClass(id, className) {
            var e;

            if (!id) {
                return false;
            }
            e = document.getElementById(id);
            if (e) {
                e.classList.add(className);
            }
            return true;
        }

        function _removeClass(id, className) {
            var e;

            if (!id) {
                return false;
            }

            e = document.getElementById(id);
            if (e) {
                e.classList.remove(className);
            }
            return true;
        }

        /**
         * Hide one or more elements given DOM id(s)
         *
         * @param {(string|string[])} id - ID or array of IDs of element(s) to hide
         * @param {string} suffix - Optional id suffix (component uid for uniquity)
         * @returns {boolean} True
         */
        function _hideElement(id, suffix) {
            var target;

            if (typeof id === 'object' && id.constructor === Array) {
                id.map(function (eid) {
                    target = eid + (suffix ? '-' + suffix : '');
                    return _removeClass(target, 'show');
                });
            } else {
                target = id + (suffix ? '-' + suffix : '');
                _removeClass(target, 'show');
            }
        }

        /**
         * Show one or more elements given DOM id(s)
         *
         * @param {(string|string[])} id - ID or array of IDs of element(s) to hide
         * @param {string} suffix - Optional id suffix (component uid for uniquity)
         * @returns {boolean} True
         */
        function _showElement(id, suffix) {
            var target;

            if (typeof id === 'object' && id.constructor === Array) {
                id.map(function (eid) {
                    target = eid + (suffix ? '-' + suffix : '');
                    return _addClass(target, 'show');
                });
            } else {
                target = id + (suffix ? '-' + suffix : '');
                _addClass(target, 'show');
            }
        }

        function _trackEvent(el, selector) {
            var calc = el.target.closest('.c-calculator-insurance'),
                elem = el.target.closest(selector),
                destPage,
                obj = {};

            function getdestination(s) {
                var first,
                    second,
                    result = '';

                if (s.getAttribute('href')) {
                    if (s.getAttribute('href').indexOf('#') === -1) {
                        result = s.getAttribute('href').toLowerCase();
                        return result;
                    }
                } else if (s.getAttribute('onclick')) {
                    first = s.getAttribute('onclick').indexOf('\'');
                    second = s.getAttribute('onclick').indexOf('\'', first + 1);
                    result = s.getAttribute('onclick').slice(first + 1, second);
                    return result;
                } else if (s.parentElement.getAttribute('onclick')) {
                    first = s.parentElement.getAttribute('onclick').indexOf('\'');
                    second = s.parentElement.getAttribute('onclick').indexOf('\'', first + 1);
                    result = s.parentElement.getAttribute('onclick').slice(first + 1, second);
                    return result;
                }
                return result;
            }

            destPage = getdestination(elem);
            obj.eventInfo = {
                eventComponent: calc.dataset.eventComponent,
                eventType: elem.dataset.eventType,
                eventName: elem.dataset.eventName,
                eventAction: calc.dataset.eventAction,
                eventText: elem.dataset.eventText, // get proper text
                eventValue: elem.dataset.eventValue,
                eventValue2: elem.dataset.eventValue2,
                eventValue3: elem.dataset.eventValue3,
                eventValue4: elem.dataset.eventValue4,
                eventPage: EDC.props.pageNameURL,
                destinationPage: destPage, // get proper destination page
                engagementType: elem.dataset.eventEngagement,
                eventStage: elem.dataset.eventStage,
                eventLevel: elem.dataset.eventLevel
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _resetSelect(selectEl) {
            var dropdownElement = selectEl.closest('.c-dropdown');

            selectEl.selectedIndex = 0;
            selectEl.value = '';

            dropdownElement.querySelector('.dropdown').classList.add('disabled');

            dropdownElement.querySelector('.text').innerHTML = selectEl.getAttribute('data-default-value');
            dropdownElement.querySelector('.text').classList.add('default');

            dropdownElement.querySelectorAll('.item').forEach(function (item) {
                item.classList.remove('active');
                item.classList.remove('selected');
            });
        }

        function _formatCurrency(cStr, hideSymbol) {
            var clean = String(cStr).replace(RE.comma, '').replace(RE.space, ''),
                pcs = clean.split('.'),
                dollars = pcs[0],
                cents = pcs[1] ? '.' + pcs[1] : '',
                digitTriplet = /(\d+)(\d{3})/,
                ret = '',
                sep = isFrench ? ' ' : ',';

            while (digitTriplet.test(dollars)) {
                dollars = dollars.replace(digitTriplet, '$1' + sep + '$2');
            }

            ret = dollars + cents;

            if (!hideSymbol) {
                return isFrench ? ret + ' $' : '$ ' + ret;
            }

            return ret;
        }

        function _searchPercentage(PaymentTermDays, CoveragePeriod) {
            var bracket,
                idx,
                premiumPercentages = [{
                    CoveragePeriod: '90',
                    PaymentTermDays: '0-90',
                    PremiumPercentageLowRisk: 0.554,
                    PremiumPercentageHighRisk: 0.698,
                    Risk: 'LOW'
                }, {
                    CoveragePeriod: '180',
                    PaymentTermDays: '0-90',
                    PremiumPercentageLowRisk: 1.108,
                    PremiumPercentageHighRisk: 1.396,
                    Risk: 'LOW'
                }, {
                    CoveragePeriod: '90',
                    PaymentTermDays: '91-180',
                    PremiumPercentageLowRisk: 0.969,
                    PremiumPercentageHighRisk: 1.22,
                    Risk: 'LOW'
                }, {
                    CoveragePeriod: '180',
                    PaymentTermDays: '91-180',
                    PremiumPercentageLowRisk: 1.938,
                    PremiumPercentageHighRisk: 2.44,
                    Risk: 'LOW'
                }];

            for (idx = 0; idx < premiumPercentages.length; idx++) {
                bracket = premiumPercentages[idx];
                if (bracket.PaymentTermDays === PaymentTermDays && bracket.CoveragePeriod === CoveragePeriod) {
                    return bracket;
                }
            }
            return false;
        }

        function _calculate() {
            var coverageDuration = inputs[uid].duration.value,
                coverageTime = inputs[uid].payment.value,
                percentages,
                sales = parseInt(inputs[uid].sales.value.replace(RE.nonDigits, ''), 10),
                profit = parseInt(inputs[uid].profit.value.replace(RE.nonDigits, ''), 10),
                recovery = parseInt((sales * 0.9), 10),
                display = {
                    sales: _formatCurrency(sales),
                    profit: _formatCurrency(profit),
                    recovery: _formatCurrency(recovery),
                    netWo: _formatCurrency(sales - profit)
                };

            percentages = _searchPercentage(coverageTime, coverageDuration);

            if (percentages) {
                maxCost = parseFloat((percentages.PremiumPercentageHighRisk * sales / 100).toFixed(2));
            }

            outputs[uid].salesWith.innerHTML = display.sales;
            outputs[uid].salesWo.innerHTML = display.sales;
            outputs[uid].profitWith.innerHTML = display.profit;
            outputs[uid].profitWo.innerHTML = display.profit;
            outputs[uid].recovery.innerHTML = display.recovery;
            outputs[uid].cost.innerHTML = _formatCurrency(maxCost);
            outputs[uid].netWo.innerHTML = display.netWo;
            outputs[uid].netWith.innerHTML = _formatCurrency((sales - (maxCost + recovery)).toFixed(2));
        }

        function _validate(el) {
            var amount = el.value.replace(RE.nonDigits, ''),
                input1 = inputs[uid].sales,
                input2 = inputs[uid].profit,
                nextEl,
                result,
                suffix = (el.id === input1.id ? '1' : '2'),
                errorEl = el.closest('.field').querySelector('.error-message');

            function _clearErrors() {
                el.classList.remove('error');
                errorEl.classList = 'error error-message';
            }

            // Check if input is a Number, clear if not
            if (isNaN(amount)) {
                el.value = '';
                return false;
            }

            if (suffix === '1') {
                nextEl = input2;
            } else {
                nextEl = inputs[uid].duration;
            }

            _clearErrors();
            if (input2.value !== '' && (parseInt(input2.value.replace(RE.nonDigits, ''), 10) >= parseInt(input1.value.replace(RE.nonDigits, ''), 10))) {
                _hideElement(lists.reset, uid);
                _resetSelect(inputs[uid].duration);
                _resetSelect(inputs[uid].payment);
                _addClass(errorEl.id, 'invalid');
                _showElement(errorEl.id);
                return false;
            }

            if (amount === '') {
                _clearErrors();
                _hideElement(lists.reset, uid);
                _resetSelect(inputs[uid].duration);
                _resetSelect(inputs[uid].payment);
                if (suffix === '1') {
                    // make sure second input is reset, too
                    input2.value = '';
                    input2.disabled = true;
                }
            } else if (amount <= 0) {
                if (amount.length === 1) {
                    _addClass(el.id, 'error');
                    _addClass(errorEl.id, 'zero');
                    _showElement(errorEl.id);
                    _resetSelect(inputs[uid].duration);
                    _resetSelect(inputs[uid].payment);
                    _hideElement(lists.reset, uid);
                } else {
                    _hideElement(lists.reset, uid);
                    return false;
                }
            } else {
                _removeClass(el.id, 'error');
                _clearErrors();

                if (amount > 0 && amount < calculatorTopLimit) {
                    if (nextEl.type === 'text') {
                        nextEl.disabled = false;
                    } else {
                        nextEl.parentNode.classList.remove('disabled');
                    }
                    _hideElement(lists.error, uid);

                } else if (amount > calculatorTopLimit) {
                    if (suffix === '1') {
                        // make sure second input is reset, too
                        input2.value = '';
                        input2.disabled = true;
                    }
                    _resetSelect(inputs[uid].duration);
                    _resetSelect(inputs[uid].payment);
                    _hideElement(lists.okay, uid);
                    _showElement(lists.error, uid);
                }
            }

            result = _formatCurrency(amount, true);
            el.value = result;

            if (inputs[uid].payment.value !== '' && inputs[uid].duration.value !== '') {
                _calculate();
                _showElement(lists.okay, uid);
            }

            return true;
        }

        function _setAttributes(el, attrs) {
            var key;

            for (key in attrs) {
                if (attrs.hasOwnProperty(key)) {
                    el.setAttribute(key, attrs[key]);
                }
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

        // Attach events
        function _attachEvents() {
            var attribs = {},
                listLinks = element.querySelectorAll('.invalid a'),
                defaultEvent = {
                    'data-event-type': '',
                    'data-event-name': '',
                    'data-event-text': '',
                    'data-event-value': '',
                    'data-event-value2': '',
                    'data-event-value3': '',
                    'data-event-value4': '',
                    'data-event-engagement': '',
                    'data-event-stage': '',
                    'data-event-level': ''
                };

            EDC.utils.attachEvents(inputs[uid].sales, 'keyup', function (e) {
                _validate(e.target);
                if (!keypressed) {
                    keypressed = true;
                    attribs.extend(defaultEvent, {
                        'data-event-type': 'text field',
                        'data-event-name': 'text field - start',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'start'
                    });
                    _setAttributes(this, attribs);
                    _trackEvent(e, '#' + e.target.id); // trigger only on first keypress
                }
            });

            EDC.utils.attachEvents(inputs[uid].sales, 'blur', function (e) {
                var sales = inputs[uid].sales.value.replace(RE.nonDigits, ''),
                    elem = e.target,
                    lastVal;

                if (sales <= calculatorTopLimit && sales !== '') {
                    lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue; // reduce duplicates
                    if (lastVal !== sales) {
                        attribs.extend(defaultEvent, {
                            'data-event-type': 'text field',
                            'data-event-name': 'text field input - =<500K',
                            'data-event-text': 'equal or less than 500K',
                            'data-event-engagement': '1.5',
                            'data-event-stage': 'Q1'
                        });
                        _setAttributes(elem, attribs);
                        if (!steps.one) {
                            _trackEvent(e, '#' + e.target.id);
                            steps.one = true;
                        }
                    }
                } else if (sales > calculatorTopLimit) {
                    lastVal = digitalData.events.slice(-1)[0].eventInfo.eventName;
                    if (lastVal !== 'text field input - >500K') {
                        attribs.extend(defaultEvent, {
                            'data-event-type': 'text field',
                            'data-event-name': 'text field input - >500K',
                            'data-event-text': 'greater than 500K',
                            'data-event-value': sales,
                            'data-event-engagement': '1.5',
                            'data-event-stage': 'Q1'
                        });
                        _setAttributes(elem, attribs);
                        if (!steps.one) {
                            _trackEvent(e, '#' + e.target.id);
                            steps.one = true;
                        }
                    }
                }
            });

            EDC.utils.attachEvents(inputs[uid].profit, 'keyup', function (e) {
                _validate(e.target);
                if (!keypressed) {
                    keypressed = true;
                    attribs.extend(defaultEvent, {
                        'data-event-type': 'text field',
                        'data-event-name': 'text field - start',
                        'data-event-engagement': '1.5',
                        'data-event-stage': 'start'
                    });
                    _setAttributes(this, attribs);
                    _trackEvent(e, '#' + e.target.id); // trigger only on first keypress
                }
            });

            EDC.utils.attachEvents(inputs[uid].profit, 'blur', function (e) {
                var profit = inputs[uid].profit.value.replace(RE.nonDigits, ''),
                    elem = e.target,
                    lastVal;

                if (profit <= calculatorTopLimit && profit !== '') {
                    lastVal = digitalData.events.slice(-1)[0].eventInfo.eventValue; // reduce duplicates
                    if (lastVal !== profit) {
                        attribs.extend(defaultEvent, {
                            'data-event-type': 'text field',
                            'data-event-name': 'text field input - =<500K',
                            'data-event-text': 'equal or less than 500K',
                            'data-event-engagement': '1.5',
                            'data-event-stage': 'Q2'
                        });
                        _setAttributes(elem, attribs);
                        if (!steps.two) {
                            _trackEvent(e, '#' + e.target.id);
                            steps.two = true;
                        }
                    }
                } else if (profit > calculatorTopLimit) {
                    lastVal = digitalData.events.slice(-1)[0].eventInfo.eventName;
                    if (lastVal !== 'text field input - >500K') {
                        attribs.extend(defaultEvent, {
                            'data-event-type': 'text field',
                            'data-event-name': 'text field input - >500K',
                            'data-event-text': 'greater than 500K',
                            'data-event-value': profit,
                            'data-event-engagement': '1.5',
                            'data-event-stage': 'Q2'
                        });
                        _setAttributes(elem, attribs);
                        if (!steps.two) {
                            _trackEvent(e, '#' + e.target.id);
                            steps.two = true;
                        }
                    }
                }
            });

            // Enable Payment Type Select after duration is selected
            // Clear form if 0 value is selected
            EDC.utils.attachEvents(inputs[uid].duration, 'change', function (e) {
                attribs.extend(defaultEvent, {
                    'data-event-type': 'drop-down',
                    'data-event-name': 'drop-down selection - step 3',
                    'data-event-engagement': '1.5',
                    'data-event-stage': 'Q3'
                });
                _setAttributes(e.target, attribs);
                if (!steps.three) {
                    _trackEvent(e, '#' + e.target.id);
                    steps.three = true;
                }

                if (this.value !== '0') {
                    inputs[uid].payment.parentNode.classList.remove('disabled');

                    if (inputs[uid].payment.value !== '') {
                        _calculate();
                    }
                } else {
                    _resetSelect(inputs[uid].payment);
                    _hideElement(lists.okay);
                }
            });

            // _calculate the coverage after select change
            // Clear form if 0 value is selected
            EDC.utils.attachEvents(inputs[uid].payment, 'change', function (e) {
                var sales = inputs[uid].sales.value.replace(RE.nonDigits, ''),
                    profit = inputs[uid].profit.value.replace(RE.nonDigits, ''),
                    duration = inputs[uid].duration,
                    payment = inputs[uid].payment;

                _calculate();

                if (this.value === '') {
                    _hideElement(lists.okay);
                } else {
                    _showElement(lists.okay, uid);
                }

                attribs.extend(defaultEvent, {
                    'data-event-type': 'drop-down',
                    'data-event-name': 'drop-down selection - final',
                    'data-event-text': maxCost,
                    'data-event-value': 'Q1.' + sales,
                    'data-event-value2': 'Q2.' + profit,
                    'data-event-value3': 'Q3.' + duration.options[duration.selectedIndex].innerHTML,
                    'data-event-value4': 'Q4.' + payment.options[payment.selectedIndex].innerHTML,
                    'data-event-engagement': '1.5',
                    'data-event-stage': 'Q4'
                });
                _setAttributes(payment, attribs);
                if (!steps.four) {
                    _trackEvent(e, '#' + e.target.id);
                    steps.four = true;
                }
            });

            EDC.utils.attachEvents(listLinks, 'click', function (e) {
                var tname = this.text.replace(RE.space, ' ');

                e.preventDefault();
                attribs.extend(defaultEvent, {
                    'data-event-type': 'link',
                    'data-event-name': 'link click - product',
                    'data-event-text': tname,
                    'data-event-engagement': '1',
                    'data-event-level': 'secondary'
                });
                _setAttributes(this, attribs);
                _trackEvent(e, 'a');
                _activateLink(this);
            });

            EDC.utils.attachEvents(window, 'resize', function () {
                EDC.forms.setHeightLabels(rows);
            });
        }

        function _setData() {
            inputs[uid] = {};
            inputs[uid].sales = element.querySelector('.calculator-amount-1');
            inputs[uid].profit = element.querySelector('.calculator-amount-2');
            inputs[uid].duration = element.querySelector('.duration select');
            inputs[uid].payment = element.querySelector('.payment select');

            outputs[uid] = {};
            outputs[uid].salesWith = element.querySelector('.sales-with');
            outputs[uid].salesWo = element.querySelector('.sales-wo');
            outputs[uid].profitWith = element.querySelector('.profit-with');
            outputs[uid].profitWo = element.querySelector('.profit-wo');
            outputs[uid].cost = element.querySelector('.insurance-cost');
            outputs[uid].recovery = element.querySelector('.insurance-recovery');
            outputs[uid].netWith = element.querySelector('.net-with');
            outputs[uid].netWo = element.querySelector('.net-wo');
            _resetSelect(inputs[uid].duration);
            _resetSelect(inputs[uid].payment);

            _attachEvents();
        }

        _setData();
        lists.reset = lists.okay.concat(lists.error);
        EDC.forms.setHeightLabelsOnLoad(rows);
    }

    // Public methods
    function init() {
        var calcs = document.querySelectorAll('.c-calculator-insurance:not([data-component-state="initialized"])');

        if (calcs) {
            calcs.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', calculatorInsurance.init);
