var onlinePaymentsJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            form = element.querySelector('form'),
            formRows = element.querySelectorAll('.form-row'),
            formElements = form ? form.elements : null,
            levelsContainer = element.querySelector('.levels'),
            noParamsContainer = element.querySelector('.no-params'),
            levels = element.querySelectorAll('.level'),
            pageLevel = levels ? levels.length : null,
            transactionResults = element.querySelector('.transaction-results'),
            transactionContext = transactionResults ? transactionResults.querySelector('.transaction-context') : null,
            confirmationNumber = transactionResults ? transactionResults.querySelector('.confirmation-number') : null,
            confirmationNumberText = confirmationNumber ? confirmationNumber.querySelector('.confirmation-number-text') : null,
            transactionStatus = transactionResults ? transactionResults.querySelector('.transaction-status') : null,
            statusApprovedText = transactionStatus ? transactionStatus.querySelector('.status-approved-text') : null,
            statusDeclinedText = transactionStatus ? transactionStatus.querySelector('.status-declined-text') : null,
            resultActions = transactionResults ? transactionResults.querySelector('.result-actions') : null,
            goBackBtn = resultActions ? resultActions.querySelector('.button.go-back') : null,
            resultTitle = transactionContext ? transactionContext.querySelector('.result-title') : null,
            resultTextApproved = transactionContext ? transactionContext.querySelector('.result-text.result-text-approved') : null,
            resultTextDeclined = transactionContext ? transactionContext.querySelector('.result-text.result-text-declined') : null,
            status = element.querySelector('.status'),
            statusBar = status ? status.querySelector('.status-bar') : null,
            myNextBtn = element.querySelector('.form-actions button'),
            paymentAmountField = element.querySelector('input[name="paymentAmount"]'),
            originalAmount = paymentAmountField ? paymentAmountField.value : null,
            amountAlert = element.querySelector('.amount-alert'),
            ticketServlet = element.dataset.ticketServlet,
            receiptServlet = element.dataset.receiptServlet,
            monerisMode = element.dataset.monerisMode,
            hasParams = element.dataset.hasParams,
            approvedAttr = 'data-transaction-approved',
            declinedAttr = 'data-transaction-declined',
            messageBanner = d.querySelector('.c-message-banner'),
            transactionApproved = false,
            messageBannerText = messageBanner ? messageBanner.querySelector('.load-error') : null;

        // Tracking
        function _trackPayNowOnStep2() {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent.toLowerCase(),
                    eventType: element.dataset.eventType.toLowerCase(),
                    eventName: element.dataset.eventName,
                    eventAction: element.dataset.eventAction.toLowerCase(),
                    eventValue: '',
                    eventText: 'moneris pay now',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _trackGoBackBtn(e) {
            var el = e.currentTarget,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent.toLowerCase(),
                        eventType: el.dataset.eventType.toLowerCase(),
                        eventName: el.dataset.eventName,
                        eventAction: element.dataset.eventAction.toLowerCase(),
                        eventText: el.getAttribute('data-english-text'),
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName()
                    }
                };

            EDC.utils.dataLayerTracking(obj);
        }

        function _trackErrorMessageBanner() {
            var obj = {
                eventInfo: {
                    eventComponent: 'message banner',
                    eventType: 'error',
                    eventName: 'error – moneris online payment',
                    eventAction: element.dataset.eventAction.toLowerCase(),
                    eventText: element.dataset.englishErrorMessage.toLowerCase(),
                    eventValue: '',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _trackAmountField() {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent.toLowerCase(),
                    eventType: element.dataset.eventType.toLowerCase(),
                    eventName: 'form – moneris online payment',
                    eventAction: 'payment details',
                    eventText: 'update amount',
                    eventValue: '',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            EDC.utils.dataLayerTracking(obj);
        }

        function _trackScreensLoad(level) {
            var transactionStatusForStep3,
                pageCategory = {
                    pageType: 'Moneris – Online Payment'
                },
                pageInfo = {
                    primaryCategory: 'form',
                    subCategory2: ''
                };

            switch (level) {
                case 1:
                    pageCategory.subCategory1 = 'payment detail';
                    pageInfo.pageName = 'form:moneris online payment:payment detail';
                    break;

                case 2:
                    pageCategory.subCategory1 = 'credit card details';
                    pageInfo.pageName = 'form:moneris online payment:credit card details';
                    break;

                case 3:
                    transactionStatusForStep3 = transactionApproved ? 'success' : 'declined';
                    pageCategory.subCategory1 = 'payment confirmation';
                    pageCategory.subCategory2 = transactionStatusForStep3;
                    pageInfo.pageName = 'form:moneris online payment:payment confirmation:' + transactionStatusForStep3;
                    break;
            }

            window[window.EDC.datalayerObj].events = [];
            EDC.utils.pageCategoryTracking(pageCategory, true);
            EDC.utils.pageInfoTracking(pageInfo, true);

            if (typeof _satellite !== 'undefined') {
                _satellite.track('dl-event tracking');
            }
        }

        // Private methods
        function _setErrorBannerDefaultMessage() {
            var text = element.dataset.errorBannerDefaultMessage;

            if (messageBannerText && text) {
                messageBannerText.textContent = text;
            }
        }

        function _showLevel(level) {
            levels.forEach(function (thisLevel) {
                thisLevel.classList.add('hide');
            });

            element.classList.remove('processing');
            status.classList.remove('hide');
            levels[level - 1].classList.remove('hide');
            _trackScreensLoad(level);
        }

        function _showErrorBanner() {
            _showLevel(1);
            _trackErrorMessageBanner();
            EDC.forms.disableSubmit(myNextBtn);

            if (messageBanner) {
                messageBanner.classList.remove('hide');
            }
        }

        function _showProcessingScreen() {
            levels.forEach(function (thisLevel) {
                thisLevel.classList.add('hide');
            });

            EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y - 100);
            status.classList.add('hide');
            element.classList.add('processing');
        }

        function _fillHtmlWithResults(resultsStatus, attr) {
            transactionResults.classList.add(resultsStatus);
            resultTitle.textContent = resultTitle.getAttribute(attr);
        }

        function _setResults(receiptData) {
            var result = receiptData.receipt.result,
                referenceNumber = receiptData.receipt.referenceNo;

            statusApprovedText.classList.add('hide');
            statusDeclinedText.classList.add('hide');

            if (result === 'a') {
                transactionApproved = true;
                statusApprovedText.classList.remove('hide');
                _fillHtmlWithResults('approved', approvedAttr);
                confirmationNumberText.textContent = referenceNumber;
                confirmationNumber.classList.remove('hide');
                resultTextApproved.classList.remove('hide');
            } else if (result === 'd') {
                statusDeclinedText.classList.remove('hide');
                _fillHtmlWithResults('declined', declinedAttr);
                resultActions.classList.remove('hide');
                resultTextDeclined.classList.remove('hide');
            }

            EDC.forms.setStatusBar(statusBar, 3, pageLevel);
            _showLevel(3);
        }

        function _loadMoneris(ticketData) {
            var ticket = ticketData.ticket,
                ticketFormField = form.querySelector('input[name="ticket"]'),
                successfulTransaction = false,
                myCheckout = new monerisCheckout(); // eslint-disable-line

            if (ticket) {
                if (ticketFormField) {
                    ticketFormField.value = ticket;
                }

                myCheckout.setMode(monerisMode);
                myCheckout.setCheckoutDiv('moneris-checkout');
                myCheckout.startCheckout(ticket);

                myCheckout.setCallback('cancel_transaction', function () {
                    _showLevel(1);
                });

                myCheckout.setCallback('payment_submitted', function () {
                    _trackPayNowOnStep2();
                    _showProcessingScreen();

                    setTimeout(function () {
                        if (!successfulTransaction) {
                            _showLevel(2);
                        }
                    }, 4000);
                });

                myCheckout.setCallback('payment_receipt', function () {
                    successfulTransaction = true;
                    EDC.utils.fetchJSON('POST', receiptServlet, EDC.forms.getFormData(form), function (receiptData) {
                        var result,
                            error;

                        if (receiptData) {
                            result = receiptData.receipt.result;
                            error = receiptData.error;
                            myCheckout.closeCheckout();

                            if (error || !result || (result !== 'a' && result !== 'd')) {
                                _showErrorBanner();
                            } else if (result) {
                                _setResults(receiptData);
                            } else {
                                _showErrorBanner();
                            }
                        } else {
                            _showErrorBanner();
                        }
                    }, function () {
                        // Error on server communication, delay or related issues
                        _showErrorBanner();
                    });
                });

                myCheckout.setCallback('page_loaded', function () {
                    _showLevel(2);
                });
            } else {
                _showErrorBanner();
            }
        }

        function _myNextBtnClicked(e) {
            var errorField;

            e.preventDefault();
            EDC.forms.validateForm(formElements);
            EDC.forms.setStatusBar(statusBar, 2, pageLevel);

            if (!form.querySelectorAll('.form-control.error, input.error').length) {
                _showProcessingScreen();

                EDC.utils.fetchJSON('POST', ticketServlet, EDC.forms.getFormData(form), function (data) {
                    if (data) {
                        if (data.error) {
                            _showErrorBanner();
                        } else if (data.ticket) {
                            _loadMoneris(data);
                        } else {
                            _showErrorBanner();
                        }
                    } else {
                        _showErrorBanner();
                    }
                }, function () {
                    // Error on server communication, delay or related issues
                    _showErrorBanner();
                });
            } else {
                errorField = element.querySelector('.form-control.error');

                if (errorField) {
                    errorField.focus();
                }
            }
        }

        function _goBackBtnClicked(e) {
            transactionResults.classList.remove('approved');
            transactionResults.classList.remove('declined');
            _showLevel(1);
            _trackGoBackBtn(e);
        }

        function _checkIfHasParams() {
            if (hasParams === 'true') {
                if (levelsContainer) {
                    levelsContainer.classList.remove('hide');
                }

                if (noParamsContainer) {
                    noParamsContainer.classList.add('hide');
                }
            } else {
                if (levelsContainer) {
                    levelsContainer.classList.add('hide');
                }

                if (noParamsContainer) {
                    noParamsContainer.classList.remove('hide');
                }
            }
        }

        function _amountFieldchanged(e) {
            var el = e.target,
                elValue = el ? el.value : null;

            _trackAmountField();

            if (originalAmount !== elValue) {
                amountAlert.classList.remove('hide');
            } else {
                amountAlert.classList.add('hide');
            }
        }

        // Attach events
        function _attachEvents() {
            if (form) {
                EDC.forms.validateChange(formElements);
                EDC.utils.attachEvents(myNextBtn, 'click', _myNextBtnClicked);
                EDC.utils.attachEvents(goBackBtn, 'click', _goBackBtnClicked);
                EDC.utils.attachEvents(paymentAmountField, 'input', _amountFieldchanged);

                EDC.utils.attachEvents(window, 'resize', function () {
                    EDC.forms.setHeightLabels(formRows);
                });
            }
        }

        _checkIfHasParams();
        _setErrorBannerDefaultMessage();
        _attachEvents();

        if (form) {
            _showLevel(1);
        }
    }

    // Public methods
    function init() {
        var onlinePayments = document.querySelectorAll('.c-online-payments:not([data-component-state="initialized"])');

        if (onlinePayments) {
            onlinePayments.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', onlinePaymentsJS.init);