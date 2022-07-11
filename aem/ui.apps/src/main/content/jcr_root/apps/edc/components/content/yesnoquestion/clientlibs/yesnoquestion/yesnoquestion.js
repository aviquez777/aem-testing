var binaryCtaJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var isFeedbackLoop = element.classList.contains('feedback'),
            prompt = element.querySelector('.cta-wrap'),
            returnEl = element.querySelector('.return'),
            promptTxt = element.querySelector('.prompt'),
            form = isFeedbackLoop ? element.querySelector('.feedback-content') : '',
            serviceURL = form ? form.getAttribute('action') : '',
            submitBtn = form ? form.querySelector('.submit-btn') : '',
            submitBtnText = submitBtn ? submitBtn.getAttribute('data-english-text') : '',
            charSet = form ? form.getAttribute('data-charset') : '',
            submitFailedMessage = isFeedbackLoop ? element.querySelector('.submit-failed-message') : '',
            feedbackTitle = isFeedbackLoop ? element.querySelector('.feedback-title') : '',
            answerYes = isFeedbackLoop ? element.querySelector('.answer-yes') : '',
            answersYes = answerYes ? answerYes.querySelectorAll('.answer-option') : '',
            answerNo = isFeedbackLoop ? element.querySelector('.answer-no') : '',
            answersNo = answerNo ? answerNo.querySelectorAll('.answer-option') : '',
            answerOther = isFeedbackLoop ? element.querySelector('.other') : '',
            otherOption = answerOther ? answerOther.querySelector('.answer-option') : '',
            otherInputContainer = isFeedbackLoop ? element.querySelector('.form-group') : '',
            otherInput = otherInputContainer ? otherInputContainer.querySelector('input') : '',
            dataYes = isFeedbackLoop ? element.querySelector('.button.yes') : '',
            dataNo = isFeedbackLoop ? element.querySelector('.button.no') : '',
            searchQuery,
            topResult,
            buttonAction;

        // Private methods

        // Data Layer
        function _trackEvent(buttonText, answers) {
            var obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent,
                    eventType: element.dataset.eventType,
                    eventName: element.dataset.eventName + ' - ' + buttonText,
                    eventAction: element.dataset.eventAction,
                    eventText: buttonText,
                    eventValue: promptTxt.innerText,
                    eventValue2: element.getAttribute('data-section-title') ? element.getAttribute('data-section-title').toLowerCase() : '',
                    eventPage: EDC.props.pageNameURL,
                    eventPageName: EDC.utils.getPageName(),
                    destinationPage: '',
                    engagementType: element.dataset.engagementType
                }
            };

            if (searchQuery !== null && topResult !== null) {
                obj.eventInfo.searchQuery = searchQuery;
                obj.eventInfo.topResult = topResult;
            }

            if (answers) {
                obj.eventInfo.eventValue3 = answers;
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // Helper function to display the feedback loop or the normal success message
        function _clickOption(e) {
            var target = e.target,
                button = target.classList.contains('button') ? target : target.parentElement,
                buttonText = button.querySelector('span').innerText;

            buttonAction = button.classList.contains('yes');
            prompt.classList.add('hide');
            searchQuery = element.getAttribute('data-search-query');
            topResult = element.getAttribute('data-top-result');
            _trackEvent(buttonText);

            if (isFeedbackLoop) {
                if (buttonAction) {
                    feedbackTitle.innerText = feedbackTitle.getAttribute('data-yes');
                    answerYes.classList.remove('hide');
                } else {
                    feedbackTitle.innerText = feedbackTitle.getAttribute('data-no');
                    answerNo.classList.remove('hide');
                }

                form.classList.remove('hide');
                answerOther.classList.remove('hide');
            } else {
                returnEl.classList.remove('hide');
            }
        }

        // Helper function to display the Other input option
        function _displayOtherInput() {
            otherInputContainer.classList.toggle('hide');

            if (otherInputContainer.classList.contains('hide')) {
                otherInput.value = '';
            }
        }

        // Helper function to get selected answers values
        function _getAnswers(section, answers) {
            answers.forEach(function (answer) {
                section.push(
                    {
                        option: answer.value,
                        answer: answer.checked ? answer.value : ''
                    }
                );
            });
        }

        // Helper function to get Data Layer answers
        function _getDataLayerAnswers(answers) {
            var dataLayerAnswers = '';

            answers.forEach(function (answer) {
                dataLayerAnswers += answer.checked ? answer.getAttribute('data-english-value') + ':' : '';
            });

            dataLayerAnswers += otherOption.checked ? otherOption.getAttribute('data-english-value') + ':' : '';
            dataLayerAnswers = dataLayerAnswers.substr(0, dataLayerAnswers.length - 1);

            return dataLayerAnswers;
        }

        // Build query string parameters
        function _addParam(query, key, value) {
            var join = query !== '' ? '&' : '';

            return query + join + key + '=' + encodeURIComponent(value);
        }

        // Helper function to send the feedback loop values to AEM
        function _formSubmit(e) {
            var userInput,
                answersYesArr = [],
                answersNoArr = [],
                queryParams = '';

            e.preventDefault();

            if (serviceURL) {
                EDC.forms.disableSubmit(submitBtn);

                userInput = [
                    {
                        option: dataYes ? dataYes.getAttribute('data-answer') : '',
                        answer: dataYes && buttonAction ? dataYes.getAttribute('data-answer') : ''
                    }, {
                        option: dataNo ? dataNo.getAttribute('data-answer') : '',
                        answer: dataNo && !buttonAction ? dataNo.getAttribute('data-answer') : ''
                    }
                ];

                _getAnswers(answersYesArr, answersYes);
                answersYesArr.push(
                    {
                        option: otherOption.value,
                        answer: buttonAction && otherOption.checked ? otherInput.value : ''
                    }
                );

                _getAnswers(answersNoArr, answersNo);
                answersNoArr.push(
                    {
                        option: otherOption.value,
                        answer: !buttonAction && otherOption.checked ? otherInput.value : ''
                    }
                );

                queryParams = _addParam(queryParams, 'name', EDC.props.userData.firstName ? EDC.props.userData.firstName : '');
                queryParams = _addParam(queryParams, 'lastName', EDC.props.userData.lastName ? EDC.props.userData.lastName : '');
                queryParams = _addParam(queryParams, 'email', EDC.props.userData.email ? EDC.props.userData.email : '');
                queryParams = _addParam(queryParams, 'externalId', EDC.props.userData.externalID ? EDC.props.userData.externalID : '');
                queryParams = _addParam(queryParams, 'searchQuery', searchQuery !== null ? searchQuery : '');
                queryParams = _addParam(queryParams, 'charSet', charSet);
                queryParams = _addParam(queryParams, 'question', promptTxt.innerText);
                queryParams = _addParam(queryParams, 'userInput', JSON.stringify(userInput));
                queryParams = _addParam(queryParams, 'questionYes', feedbackTitle.getAttribute('data-yes'));
                queryParams = _addParam(queryParams, 'answersYes', JSON.stringify(answersYesArr));
                queryParams = _addParam(queryParams, 'questionNo', feedbackTitle.getAttribute('data-no'));
                queryParams = _addParam(queryParams, 'answersNo', JSON.stringify(answersNoArr));

                // Submits the object to AEM
                EDC.utils.fetchJSON('POST', serviceURL, queryParams, function () {
                    form.classList.add('hide');
                    returnEl.classList.remove('hide');
                    submitFailedMessage.classList.add('hide');

                    // Data Layer tracking
                    _trackEvent(submitBtnText, buttonAction ? _getDataLayerAnswers(answersYes) : _getDataLayerAnswers(answersNo));

                    // Resets the form after submission
                    form.reset();
                    otherInputContainer.classList.add('hide');
                    answerYes.classList.add('hide');
                    answerNo.classList.add('hide');
                    EDC.utils.enableSubmit(submitBtn);
                }, function () {
                    // Error on server communication, delay or related issues
                    if (submitFailedMessage) {
                        form.classList.remove('hide');
                        submitFailedMessage.classList.remove('hide');
                        EDC.utils.enableSubmit(submitBtn);
                    }
                });
            }
        }

        // Attach events
        function _attachEvents() {
            var buttons = element.querySelectorAll('.button:not(.submit-btn)');

            EDC.utils.attachEvents(buttons, 'click', _clickOption);
            EDC.utils.attachEvents(otherOption, 'change', _displayOtherInput);
            EDC.utils.attachEvents(form, 'submit', _formSubmit);
        }

        _attachEvents();
    }

    // Public methods
    function init() {
        var binaryCta = document.querySelectorAll('.c-binary-cta:not([data-component-state="initialized"])');

        if (binaryCta) {
            binaryCta.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', binaryCtaJS.init);