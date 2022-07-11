var exportHelpRequestFormJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private methods

        var form = element.querySelector('form'),
            formElements = form.elements,
            formRows = form.querySelectorAll('.form-row'),
            statusBar = element.querySelector('.status-bar'),
            serviceURL = form.getAttribute('action'),
            eloquaID = form.querySelector('input[name="elqCustomerGUID"]'),
            eloquaCookie = form.querySelector('input[name="elqCookieWrite"]'),
            successMessage = element.querySelector('.success-message'),
            submitFailedMessage = element.querySelector('.submit-failed-message'),
            step = element.querySelector('.step'),
            steps = element.querySelector('.steps'),
            dataMode = 'mode_export_help_request',
            serverAction = 'POST',
            disableSubmit = false,
            showSuccess = false,
            levelsLength,
            userLevel = 0,
            progressLevel = 0,
            currentLevel = 1,
            documentLevel,
            pageLevel = 0,
            nextLevel,
            levels,
            response,
            timerId;

        // Data Layer
        function _trackEvent() {
            var accessLevelAchieved = progressLevel > currentLevel ? progressLevel : currentLevel,
                obj = {
                    eventInfo: {
                        eventComponent: form.dataset.eventComponent,
                        eventType: form.dataset.eventType,
                        eventName: form.dataset.eventName + ' - export help level ' + currentLevel,
                        eventAction: form.dataset.eventAction,
                        eventText: form.querySelector('button[type="submit"]:not(.hide)').textContent.trim(),
                        eventValue: '',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: form.dataset.eventDestinationPage,
                        engagementType: form.dataset.eventEngagement,
                        eventLevel: form.dataset.eventLevel
                    }
                };

            if (currentLevel === 1) {
                obj.eventInfo.eventValue5 = form.querySelector('.trade-question').value;
            }

            if (window[EDC.datalayerObj]) {
                if (currentLevel === 1) {
                    window[EDC.datalayerObj].user.segment.accessLevelOriginal = userLevel;
                    window[EDC.datalayerObj].user.segment.accessLevelAchieved = accessLevelAchieved;
                } else {
                    window[EDC.datalayerObj].user.segment.accessLevelAchieved = accessLevelAchieved;
                }
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // Helper function to display success data
        function _displaySuccess() {
            form.classList.add('hide');
            successMessage.classList.remove('hide');
            EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);
        }

        // Helper function to handle success data
        function _handleSuccess() {
            if (response) {
                clearInterval(timerId);
                timerId = 0;
                if (showSuccess) {
                    _displaySuccess();
                }
            }
        }

        function _showHideLevelOne(levelOneAction) {
            var level1 = element.querySelector('.level-1');

            if (levelOneAction === 'hide') {
                level1.classList.add('hide');
                level1.classList.remove('show');
                element.classList.add('email-closed');
                element.querySelector('.bottom-ctas').classList.add('space-center');
                element.querySelector('.bottom-ctas').classList.remove('space-end');
                level1.querySelector('.submit.next').classList.add('hide');
            } else {
                disableSubmit = true;
                element.classList.remove('email-closed');
                level1.classList.remove('hide');
                level1.classList.add('show');
                level1.querySelector('.submit.next').classList.remove('hide');
                element.querySelector('.bottom-ctas').classList.remove('space-center');
                element.querySelector('.bottom-ctas').classList.add('space-end');
                EDC.forms.enableSubmit(level1.querySelector('.submit.next'));
                level1.querySelector('.form-control').focus();
            }

            level1.querySelector('.submit:not(.next)').classList.remove('show');
            level1.querySelector('.submit:not(.next)').classList.add('hide');
            EDC.forms.toggleFormFields(element.querySelectorAll('.back'), 'display');
            EDC.forms.toggleFormFields(element.querySelectorAll('.status'), 'display');
            EDC.forms.toggleFormFields(element.querySelectorAll('.bottom-ctas'), 'display');
            EDC.forms.toggleFormFields(element.querySelectorAll('.email-disclaimer'), 'display');
            EDC.forms.toggleFormFields(element.querySelectorAll('.headings'), 'display');
        }

        function _showHideLevelTitle(action) {
            var level5Heading = element.querySelector('.level-5-heading'),
                levelStepTitle = element.querySelector('.step-title');

            if (action === 'hide') {
                level5Heading.classList.add('title-color');
                levelStepTitle.classList.add('hide');
            } else {
                level5Heading.classList.remove('title-color');
                levelStepTitle.classList.remove('hide');
            }
        }

        // Helper function to show and hide form pages
        function _showHideLevels(current, following) {
            // Hides data from current level page
            var currentSubheading = element.querySelector('.level-' + current + '-subheading'),
                followingSubheading;

            if (current === '5') {
                _showHideLevelTitle('hide');
            } else {
                _showHideLevelTitle();
            }

            EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + current), 'display');
            element.querySelector('.level-' + current + '-heading').classList.toggle('hide');

            if (currentSubheading) {
                currentSubheading.classList.toggle('hide');
            }

            if (following) {
                followingSubheading = element.querySelector('.level-' + following + '-subheading');

                if (following === 5) {
                    _showHideLevelTitle('hide');
                } else {
                    _showHideLevelTitle();
                }
                // Shows data from following level page
                EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + following), 'display');
                element.querySelector('.level-' + following + '-heading').classList.toggle('hide');

                if (followingSubheading) {
                    followingSubheading.classList.toggle('hide');
                }
            }
        }

        // Helper function dislay form fields on selected level
        function _displayLevelFields(pageLevelField, displayLevel) {
            var formFields = element.querySelectorAll('.level-' + pageLevelField + ' .form-group'),
                inputGroup = element.querySelectorAll('.level-' + pageLevelField + ' .input-group'),
                mainAddress = element.querySelector('.level-' + pageLevelField + ' .main-address'),
                focusField = formFields[0].querySelector('.form-control'),
                grouppedFields;

            formFields.forEach(function (formField) {
                displayLevel.forEach(function (datafield) {
                    if (formField.getAttribute('data-form-field') === datafield) {
                        formField.classList.remove('hide');
                        if (formField.querySelector('label.hide') && mainAddress) {
                            if (mainAddress.classList.contains('hide')) {
                                formField.querySelector('label.hide').classList.remove('hide');
                            }
                        }
                    }
                });
            });

            inputGroup.forEach(function (formGroup) {
                grouppedFields = formGroup.querySelectorAll('.form-group:not(.hide)');
                if (grouppedFields) {
                    if (grouppedFields.length === 2) {
                        grouppedFields[0].classList.add('input-half', 'first');
                        grouppedFields[1].classList.add('input-half', 'last');
                        grouppedFields[0].classList.remove('input-full');
                        grouppedFields[1].classList.remove('input-full');
                    }
                    formGroup.classList.remove('hide');
                }
            });

            focusField.focus();
        }

        // Helper function to validate if the JSON data is correct and allows to complete the flow without issues
        function _validateProfilingData() {
            var userValid = false,
                emptyPage = 0,
                userLvl = levels.levelInfo.userLevel,
                userAfterlvl = levels.levelInfo.userLevelAfter,
                allowedLevel = userAfterlvl + 1;

            if (levels.profilingLevels && userLvl >= 0 && userAfterlvl) {
                if (userLvl <= userAfterlvl) {
                    userValid = true;
                }

                levels.profilingLevels.forEach(function (level) {
                    var pageLvl = parseInt(level.pageLevel, 10);

                    if (!level.pageFields.length || pageLvl <= 0 || pageLvl > allowedLevel) {
                        emptyPage++;
                    }
                });
            }

            return userValid && emptyPage <= 0;
        }

        function _progressiveBack() {
            element.querySelectorAll('.level-' + currentLevel + ' .form-control').forEach(function (elem) {
                elem.classList.remove('error');
            });

            if (currentLevel === documentLevel) {
                EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
            } else {
                nextLevel--;
            }

            if (currentLevel - 1 === userLevel || userLevel === 0) {
                _showHideLevelOne('show');
                _showHideLevels(currentLevel);
            } else {
                _showHideLevels(currentLevel, (currentLevel - 1));
                EDC.forms.setStatusBar(statusBar, pageLevel - 1, levelsLength);
            }

            pageLevel--;
            currentLevel--;

            if (pageLevel > 0) {
                step.innerHTML = pageLevel;
            }
        }

        function _progressiveEmailSubmit(action, submitBtn) {
            var emailAddress = form.querySelector('.email-submit'),
                tradeQuestion = form.querySelector('.trade-question'),
                inquiry = form.querySelector('input[name="inquiryID"]'),
                fromPage = form.querySelector('input[name="fromPage"]'),
                fullFromPage = form.querySelector('input[name="fullFromPage"]'),
                refPage = form.querySelector('input[name="refPage"]'),
                params = '',
                errorField;

            EDC.forms.validateField(emailAddress);
            EDC.forms.validateInputs(tradeQuestion);

            if (!form.querySelectorAll('.form-control.error').length && serviceURL) {

                params += encodeURIComponent('actionType') + '=' + encodeURIComponent(action) + '&';
                params += encodeURIComponent(eloquaID.name) + '=' + encodeURIComponent(eloquaID.value) + '&';
                params += encodeURIComponent(eloquaCookie.name) + '=' + encodeURIComponent(eloquaCookie.value) + '&';
                params += encodeURIComponent(fromPage.name) + '=' + encodeURIComponent(fromPage.value) + '&';
                params += encodeURIComponent(fullFromPage.name) + '=' + encodeURIComponent(fullFromPage.value) + '&';
                params += encodeURIComponent(refPage.name) + '=' + encodeURIComponent(refPage.value) + '&';
                params += encodeURIComponent(emailAddress.name) + '=' + encodeURIComponent(emailAddress.value) + '&';
                params += encodeURIComponent(tradeQuestion.name) + '=' + encodeURIComponent(tradeQuestion.value) + '&';
                params += encodeURIComponent(inquiry.name) + '=' + encodeURIComponent(inquiry.value) + '&';
                params += encodeURIComponent('mode') + '=' + encodeURIComponent(dataMode);

                EDC.forms.disableSubmit(submitBtn);
                response = false;
                showSuccess = false;

                if (!disableSubmit) {
                    // Resetting values for form re-submission
                    userLevel = 0;
                    progressLevel = 0;
                    currentLevel = 1;
                    nextLevel = 0;
                    documentLevel = 0;
                    pageLevel = 0;

                    EDC.forms.submitFormData(serverAction, serviceURL, params, function (data) {
                        levels = data;

                        // If servlet response contains valid levels data will show the next page and validate GUI changes
                        if (_validateProfilingData()) {
                            userLevel = levels.levelInfo.userLevel;
                            progressLevel = userLevel > 0 ? userLevel : 1;
                            documentLevel = levels.levelInfo.accessLevel;
                            levelsLength = levels.profilingLevels.length;
                            steps.innerHTML = levelsLength;

                            _trackEvent();

                            _showHideLevelOne('hide');
                            _showHideLevels(levels.profilingLevels[pageLevel].pageLevel);

                            currentLevel = progressLevel + 1;

                            if (levelsLength > 1) {
                                EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                                nextLevel = progressLevel + 2;
                            } else {
                                EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                            }

                            _displayLevelFields(levels.profilingLevels[pageLevel].pageLevel, levels.profilingLevels[pageLevel].pageFields);

                            pageLevel++;

                            step.innerHTML = pageLevel;

                            EDC.forms.setStatusBar(statusBar, pageLevel, levelsLength);
                        } else if (data.response === 'yes') {
                            showSuccess = true;
                            _trackEvent();
                        }
                        response = true;
                    }, submitFailedMessage);

                    timerId = setInterval(_handleSuccess, 100);
                } else {
                    _showHideLevelOne('hide');
                    _showHideLevels(levels.profilingLevels[pageLevel].pageLevel);

                    if (levelsLength === 1) {
                        EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                        EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                    }

                    currentLevel++;
                    pageLevel++;
                    nextLevel++;
                }
            } else {
                errorField = element.querySelector('.level-' + currentLevel + ' .form-control.error');
                if (errorField) {
                errorField.focus();
            }
        }
        }

        function _progressiveNextSubmit(submitBtn) {
            if (pageLevel < documentLevel) {
                _showHideLevels(currentLevel, nextLevel);
                // Show returned fields from callback.
                _displayLevelFields(nextLevel, levels.profilingLevels[pageLevel].pageFields);

                if (element.querySelector('.back').classList.contains('hide')) {
                    EDC.forms.toggleFormFields(element.querySelectorAll('.back'), 'display');
                    element.querySelector('.bottom-ctas').classList.add('space-center');
                    element.querySelector('.bottom-ctas').classList.remove('space-end');
                }

                _trackEvent();

                currentLevel++;
                pageLevel++;
                step.innerHTML = pageLevel;
                EDC.forms.setStatusBar(statusBar, pageLevel, levelsLength);

                if (currentLevel === documentLevel) {
                    EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                    EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                } else {
                    nextLevel++;
                }
            }

            EDC.forms.enableSubmit(submitBtn);
        }

        function _progressiveSubmit(action, submitBtn) {
            var pageField = element.querySelectorAll('.level-' + currentLevel + ' .form-group:not(.hide) .form-control, input[type=hidden]'),
                params = '',
                errorField;

            EDC.forms.validateForm(pageField);

            if (!form.querySelectorAll('.form-control.error, input.error').length && serviceURL) {
                params += encodeURIComponent('actionType') + '=' + encodeURIComponent(action) + '&';
                params += encodeURIComponent('mode') + '=' + encodeURIComponent(dataMode) + '&';

                EDC.forms.disableSubmit(submitBtn);
                response = false;
                showSuccess = false;

                EDC.forms.submitFormData(serverAction, serviceURL, EDC.forms.getFormData(form, params), function (data) {
                    if (data && data.response === 'yes') {
                        showSuccess = true;
                        _trackEvent();
                    } else if (action === 'next') {
                        _progressiveNextSubmit(submitBtn);
                    }

                    response = true;
                }, submitFailedMessage);

                if (action === 'form-submit' && submitBtn) {
                    timerId = setInterval(_handleSuccess, 100);
                }
            } else {
                errorField = element.querySelector('.level-' + currentLevel + ' .form-control.error');
                if (errorField) {
                errorField.focus();
            }
        }
        }

        function _progressiveFormSubmit(e) {
            var submitBtn = form.querySelector('button[type="submit"]:not(.hide)'),
                action = e.type === 'submit' ? submitBtn.getAttribute('data-action') : e.target.getAttribute('data-action');

            e.preventDefault();

            switch (action) {
                case 'back':
                    _progressiveBack();
                    break;

                case 'email-submit':
                    _progressiveEmailSubmit(action, submitBtn);
                    break;

                case 'next':
                case 'form-submit':
                    _progressiveSubmit(action, submitBtn);
                    break;

                default:
                    break;
            }
        }

        // Attach events
        function _attachEvents() {
            var ctas = form.querySelectorAll('.cta'),
                country = form.querySelector('.country select'),
                provinceSelect = form.querySelector('.province .can select'),
                stateSelect = form.querySelector('.province .usa select'),
                provinceInput = form.querySelector('.province input'),
                provinceLabel = form.querySelector('.province > label');

            EDC.utils.attachEvents(ctas, 'click', _progressiveFormSubmit);
            EDC.utils.attachEvents(form, 'submit', _progressiveFormSubmit);

            EDC.utils.attachEvents(country, 'change', function () {
                EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel);
            });

            EDC.utils.attachEvents(window, 'resize', function () {
                EDC.forms.setHeightLabels(formRows);
            });
        }

        _attachEvents();

        EDC.forms.fillHiddenFields(form);
        EDC.forms.validateChange(formElements);
        EDC.forms.setHeightLabelsOnLoad(formRows);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(form);
        EDC.forms.elqQPush();

    }

    // Public methods
    function init() {
        var progressiveProfiling = document.querySelectorAll('.c-export-help-request-form:not([data-component-state="initialized"])');

        if (progressiveProfiling) {
            progressiveProfiling.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', exportHelpRequestFormJS.init);