var progressiveProfilingJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private methods

        var form = element.querySelector('form'),
            formElements = form.elements,
            pageTitle = element.getAttribute('data-page-title'),
            formRows = form.querySelectorAll('.form-row'),
            docID = form.querySelector('input[name="docID"]'),
            eloquaID = form.querySelector('input[name="elqCustomerGUID"]'),
            eloquaCookie = form.querySelector('input[name="elqCookieWrite"]'),
            statusBar = element.querySelector('.status-bar'),
            externalPremiumURL = form.getAttribute('data-external-premium-url'),
            premiumURL = form.getAttribute('data-premium-url'),
            refPremiumURL = form.getAttribute('data-ref-premium-url'),
            teaserURL = form.getAttribute('data-teaser-url'),
            dataMode = form.getAttribute('data-mode'),
            assetTier = form.getAttribute('data-asset-tier'),
            serviceURL = form.getAttribute('action'),
            successMessage = element.querySelector('.success-message'),
            submitFailedMessage = element.querySelector('.submit-failed-message'),
            dataTarget = form.getAttribute('data-target'),
            localSneakPeek = element.querySelector('.sneak-peek-container'),
            sneakPeekElement = document.querySelector('.c-progressive-profiling[data-sneak-peek="yes"]'), // Document use for MEA pages.
            sneakPeekForm,
            sneakPeek,
            sneakPeekCta = element.querySelector('.sneak-peek'),
            isSneakPeek = element.getAttribute('data-sneak-peek') ? element.getAttribute('data-sneak-peek') : 'no',
            countryGridCards = document.querySelectorAll('.country-grid .country-grid-item .link-country'),
            gridTitle = document.querySelector('.country-grid .grid-titles .title'),
            serverAction = 'POST',
            levels,
            userLevel = 0,
            progressLevel = 0,
            currentLevel = 1,
            nextLevel,
            documentLevel,
            pageLevel = 0,
            documentURL,
            response,
            meaResponse,
            timerId,
            allowCancel = false,
            processingScreen = element.querySelector('.c-processing-screen'),
            myedcContainer = element.querySelector('.myedc-container'),
            ctasSection = element.querySelector('.form-ctas'),
            formSection = element.querySelector('.level-1 .form-group'),
            isIos = navigator.userAgent.match(/(iPad|iPhone|iPod)/i);

        // Helper function to get persona id.
        function _isPersonaButton() {
            return document.querySelector('.c-persona-button');
        }

        // Helper function to get persona id.
        function _isPersonaActive() {
            var personaButton = _isPersonaButton();

            return personaButton ? personaButton.querySelector('li.active') : null;
        }

        // For persona ebook, need to check on the persona button for the asset tier
        function _isLevel5() {
            var personaButton = _isPersonaButton(),
                level5 = element.getAttribute('data-level5') === 'true';

            return personaButton ? personaButton.getAttribute('data-asset-tier') === '5' : level5;
        }

        // Data Layer
        function _trackUser() {
            if (window[EDC.datalayerObj]) {
                if (element.dataset.productType) {
                    window[EDC.datalayerObj].user.segment.productType = element.dataset.productType.toLowerCase();
                }
                if (element.dataset.productDesc) {
                    window[EDC.datalayerObj].user.segment.productDesc = element.dataset.productDesc.toLowerCase();
                }
            }
        }

        function _trackEvent(coreCustomer, knowledgeCustomer) {
            var accessLevelAchieved = progressLevel > currentLevel ? progressLevel : currentLevel,
                obj = {
                    eventInfo: {
                        eventComponent: form.dataset.eventComponent,
                        eventType: form.dataset.eventType,
                        eventName: form.dataset.eventName + ' - pp level ' + currentLevel,
                        eventAction: form.dataset.eventAction,
                        eventText: form.querySelector('button[type="submit"]:not(.hide)').textContent.trim(),
                        eventValue: docID.value,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: form.dataset.eventDestinationPage,
                        engagementType: form.dataset.eventEngagement,
                        eventLevel: form.dataset.eventLevel
                    }
                },
                userSegment = {
                    tradeStatus: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="tradeStatus"]')),
                    annualSales: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="annualSales"]')),
                    employees: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="employees"]')),
                    painPoint: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="painPoint"]')),
                    industry: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="industry"]')),
                    marketsInt: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="marketsInt-large"]'))
                },
                personaActive;

            if (_isPersonaButton()) {
                personaActive = _isPersonaActive();
                obj.eventInfo.eventValue2 = personaActive ? personaActive.getAttribute('data-persona') : '';
            }

            if (window[EDC.datalayerObj]) {
                window[EDC.datalayerObj].user.segment.accessLevelOriginal = userLevel;
                window[EDC.datalayerObj].user.segment.accessLevelAchieved = accessLevelAchieved;
                if (coreCustomer && knowledgeCustomer) {
                    window[EDC.datalayerObj].user.segment.coreCustomer = coreCustomer;
                    window[EDC.datalayerObj].user.segment.knowledgeCustomer = knowledgeCustomer;
                }
            }

            EDC.utils.userSegmentTracking(userSegment, true);
            EDC.utils.dataLayerTracking(obj);
        }

        function _toggleProcessScreen(elementToToggle) {
            if (elementToToggle.classList.contains('hide')) {
                elementToToggle.classList.remove('hide');
            } else {
                elementToToggle.classList.add('hide');
            }
            if (element.classList.contains('processing')) {
                element.classList.remove('processing');
            } else {
                element.classList.add('processing');
                EDC.utils.scrollTo('top', EDC.utils.getPosition(processingScreen).y - 100);
            }
        }

        // Helper function to display success data
        function _displaySuccess() {
            form.classList.add('hide');
            element.classList.remove('processing');
            element.classList.add('processed');
            successMessage.classList.remove('hide');
            EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);
        }

        // Helper function to handle success data
        function _handleSuccess() {
            var newWindow = window;

            if (response) {
                clearInterval(timerId);
                timerId = 0;

                // Normal, eBook or sneak peek behavior
                if (dataMode !== 'mode_mea' && documentURL) {
                    if (dataTarget === '_self' || isIos) {
                        window.location.href = documentURL;
                    } else {
                        _displaySuccess();
                        newWindow.open(documentURL);
                    }
                } else if (dataMode === 'mode_mea' && meaResponse === 'yes') {
                    _displaySuccess();

                    if (sneakPeekForm) {
                        if (!sneakPeekForm.classList.contains('hide')) {
                            sneakPeekForm.classList.add('hide');
                        } else {
                            sneakPeekElement.querySelector('.success-message').classList.add('hide');
                        }
                    }
                }
            }
        }

        function _showHideLevelOne(levelOneAction) {
            var level1 = element.querySelector('.level-1');

            if (levelOneAction === 'hide') {
                level1.classList.add('hide');
                level1.classList.remove('show');
                element.classList.add('email-closed');
            } else {
                element.classList.remove('email-closed');
                level1.classList.remove('hide');
                level1.classList.add('show');
            }
        }

        // Helper function to show and hide form pages
        function _showHideLevels(current, following) {
            // Hides data from current level page
            EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + current), 'display');
            EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + current + '-heading'), 'display');
            if (following) {
                // Shows data from following level page
                EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + following), 'display');
                EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + following + '-heading'), 'display');
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

            if (dataMode !== 'mode_knowledge') {
                focusField.focus();
            }
        }

        // Helper function to validate if the JSON data is correct and allows to complete the flow without issues
        function _validateProfilingData() {
            var userValid = false,
                emptyPage = 0,
                userLvl,
                userAfterlvl,
                allowedLevel;

            if (Object.keys(levels).length > 0) {
                userLvl = levels.levelInfo.userLevel;
                userAfterlvl = levels.levelInfo.userLevelAfter;
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
            }
            return userValid && emptyPage <= 0;
        }

        // Helper function to validated if Doc ID is present on PP and if there's a persona id to be retrieved.
        function _getInitialParams() {
            var personaButton = _isPersonaButton(),
                personaActive,
                initialParams = '';

            if (personaButton) {
                personaActive = _isPersonaActive();

                if (personaActive) {
                    initialParams = encodeURIComponent('personaId') + '=' + personaActive.getAttribute('data-persona') + '&';
                }

                assetTier = personaButton.getAttribute('data-asset-tier');
                docID.value = personaButton.getAttribute('data-docid');
            } else if (sneakPeekForm) {
                assetTier = sneakPeekForm.getAttribute('data-asset-tier');
                docID.value = sneakPeekForm.querySelector('input[name="docID"]').value;
            }

            return initialParams;
        }

        // Helper functions for Progressive Profiling Form Pagination and Submit
        function _progressiveCancel() {
            if (allowCancel) {
                allowCancel = false;
                EDC.forms.progressiveProfilingReset(element);
                form.classList.remove('accessed');

                if (myedcContainer) {
                    myedcContainer.classList.remove('hide');
                }
            }
        }

        function _progressiveBack() {
            element.querySelectorAll('.level-' + currentLevel + ' .form-control').forEach(function (elem) {
                elem.classList.remove('error');
            });

            _showHideLevels(currentLevel, (currentLevel - 1));

            if (currentLevel === documentLevel) {
                EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
            } else {
                nextLevel--;
            }

            currentLevel--;
            pageLevel--;

            if (currentLevel <= 2) {
                _showHideLevelOne('show');
            }

            EDC.forms.setStatusBar(statusBar, pageLevel, levels.profilingLevels.length);

            if ((currentLevel - 1) === userLevel) {
                EDC.forms.toggleFormFields(element.querySelectorAll('.back'), 'hide-all');
                element.querySelector('.bottom-ctas').classList.add('space-end');
                element.querySelector('.bottom-ctas').classList.remove('space-center');
            }
        }

        function _progressiveEmailSubmit(action, submitBtn) {
            var emailAddress = form.querySelector('.email-submit'),
                fromPage = form.querySelector('input[name="fromPage"]'),
                fullFromPage = form.querySelector('input[name="fullFromPage"]'),
                refPage = form.querySelector('input[name="refPage"]'),
                inquiry = form.querySelector('input[name="inquiryID"]'),
                params = '',
                errorField,
                coreCustomer,
                knowledgeCustomer,
                fieldSection = element.querySelector('.fields-section');

            // Resetting values for form re-submission
            userLevel = 0;
            progressLevel = 0;
            currentLevel = 1;
            nextLevel = 0;
            documentLevel = 0;
            pageLevel = 0;

            EDC.forms.validateField(emailAddress);

            if (!form.querySelectorAll('.form-control.error').length && serviceURL && (premiumURL || dataMode !== 'mode_normal') && teaserURL) {

                if ((dataMode === 'mode_ebook_persona' || dataMode === 'mode_ebook_regular') && docID.value === '') {
                    params = _getInitialParams();
                }

                if (myedcContainer) {
                    myedcContainer.classList.add('hide');
                }

                if (fieldSection) {
                    _toggleProcessScreen(fieldSection);
                }

                params += encodeURIComponent('actionType') + '=' + encodeURIComponent(action) + '&';
                params += encodeURIComponent(emailAddress.name) + '=' + encodeURIComponent(emailAddress.value) + '&';
                params += encodeURIComponent(eloquaID.name) + '=' + encodeURIComponent(eloquaID.value) + '&';
                params += encodeURIComponent(eloquaCookie.name) + '=' + encodeURIComponent(eloquaCookie.value) + '&';
                params += encodeURIComponent(docID.name) + '=' + encodeURIComponent(docID.value) + '&';
                params += encodeURIComponent(fromPage.name) + '=' + encodeURIComponent(fromPage.value) + '&';
                params += encodeURIComponent(fullFromPage.name) + '=' + encodeURIComponent(fullFromPage.value) + '&';
                params += encodeURIComponent(refPage.name) + '=' + encodeURIComponent(refPage.value) + '&';
                params += encodeURIComponent('assetTier') + '=' + encodeURIComponent(assetTier) + '&';
                params += encodeURIComponent(inquiry.name) + '=' + encodeURIComponent(inquiry.value) + '&';
                params += encodeURIComponent('premiumUrl') + '=' + encodeURIComponent(premiumURL) + '&';
                params += encodeURIComponent('teaserUrl') + '=' + encodeURIComponent(teaserURL) + '&';
                params += encodeURIComponent('refPremiumURL') + '=' + encodeURIComponent(refPremiumURL) + '&';
                params += encodeURIComponent('mode') + '=' + encodeURIComponent(dataMode) + '&';
                params += encodeURIComponent('pageTitle') + '=' + encodeURIComponent(pageTitle) + '&';
                params += encodeURIComponent('sneakPeek') + '=' + isSneakPeek;

                EDC.forms.disableSubmit(submitBtn);
                response = false;
                documentURL = null;

                EDC.forms.submitFormData(serverAction, serviceURL, params, function (data) {
                    levels = data;

                    // If servlet response contains valid levels data will show the next page and validate GUI changes
                    if (_validateProfilingData()) {
                        userLevel = levels.levelInfo.userLevel;
                        progressLevel = userLevel > 0 ? userLevel : 1;
                        documentLevel = levels.levelInfo.accessLevel;

                        if (docID.value === '' || docID.value !== levels.levelInfo.docID) {
                            docID.value = levels.levelInfo.docID;
                        }

                        coreCustomer = data.levelInfo.coreCustomer ? data.levelInfo.coreCustomer.toLowerCase() : 'no';
                        knowledgeCustomer = data.levelInfo.knowledgeCustomer ? data.levelInfo.knowledgeCustomer.toLowerCase() : 'no';

                        _trackEvent(coreCustomer, knowledgeCustomer);

                        form.classList.add('accessed');

                        EDC.forms.toggleFormFields(element.querySelectorAll('.headings'), 'display');
                        EDC.forms.toggleFormFields(element.querySelectorAll('.submit'), 'display');
                        EDC.forms.toggleFormFields(element.querySelectorAll('.cancel'), 'display');
                        EDC.forms.toggleFormFields(element.querySelectorAll('.email-submit'), 'read-only');
                        EDC.forms.toggleFormFields(element.querySelectorAll('.email-disclaimer'), 'display');
                        EDC.forms.toggleFormFields(element.querySelectorAll('.bottom-ctas'), 'display');
                        EDC.forms.toggleFormFields(element.querySelectorAll('.status'), 'display');

                        // New knowledge product version all fields in same page
                        if (dataMode === 'mode_knowledge' && levels.profilingLevels) {
                            EDC.forms.toggleFormFields(element.querySelectorAll('.level-2-heading'), 'display');
                            element.querySelector('.status').classList.add('full');

                            levels.profilingLevels.forEach(function (level) {
                                var selectedLevel = '.level-' + level.pageLevel;

                                if (level.pageFields.length > 0) {
                                    EDC.forms.toggleFormFields(element.querySelectorAll(selectedLevel), 'display');
                                    _displayLevelFields(level.pageLevel, level.pageFields);

                                    if (level.pageLevel !== '5') {
                                        element.querySelector(selectedLevel + ' .section-title').classList.remove('hide');
                                    }
                                }
                            });

                            EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                            EDC.forms.toggleFormFields(element.querySelectorAll('.bottom-ctas .email-disclaimer'), 'display');
                            element.querySelector('.bottom-ctas').classList.add('space-center');
                            element.querySelector('.bottom-ctas').classList.remove('space-end');

                            currentLevel = 5;
                        } else {
                            _showHideLevels(levels.profilingLevels[pageLevel].pageLevel);

                            if (levels.profilingLevels[pageLevel].pageLevel > 2) {
                                _showHideLevelOne('hide');
                            }

                            element.querySelector('.title').classList.toggle('title-color');
                            currentLevel = progressLevel + 1;

                            _displayLevelFields(levels.profilingLevels[pageLevel].pageLevel, levels.profilingLevels[pageLevel].pageFields);

                            pageLevel++;

                            if (levels.profilingLevels.length > 1) {
                                EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                                nextLevel = progressLevel + 2;
                            } else {
                                EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                            }

                            EDC.forms.setStatusBar(statusBar, pageLevel, levels.profilingLevels.length);
                        }
                    } else if (data.URL || data.meaUnlocked) {
                        // Full Normal PP unlocked or Sneak Peak unlocked
                        if (data.URL) {
                            documentURL = data.URL;
                        }

                        // Full MEA unlocked
                        if (data.meaUnlocked && data.meaUnlocked === 'yes') {
                            // PP will unlock Sneak Peek and Country Grid
                            meaResponse = data.meaUnlocked;
                            EDC.forms.ppUnlockFeatures(data.meaUnlocked, sneakPeek, form, countryGridCards, gridTitle);
                        }

                        if (data.levelInfo.userLevelAfter) {
                            currentLevel = data.levelInfo.userLevelAfter;
                            userLevel = data.levelInfo.userLevelAfter;
                        }

                        _trackEvent();
                    }

                    response = true;
                    timerId = setInterval(_handleSuccess, 100);
                    if (!documentURL && fieldSection) {
                        _toggleProcessScreen(fieldSection);
                        if (dataMode === 'mode_knowledge') {
                            element.querySelector('.level-2 .form-control').focus();
                        }
                    }
                }, submitFailedMessage);

            } else {
                errorField = element.querySelector('.level-' + currentLevel + ' .form-control.error');
                errorField.focus();
            }
        }

        function _progressiveNextSubmit(submitBtn) {
            _toggleProcessScreen(form);

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

                EDC.forms.setStatusBar(statusBar, pageLevel, levels.profilingLevels.length);

                if (currentLevel > 2) {
                    _showHideLevelOne('hide');
                }

                if (currentLevel === documentLevel) {
                    EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                    EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                } else {
                    nextLevel++;
                }
            }

            EDC.forms.enableSubmit(submitBtn);
            setTimeout(function () {
                _toggleProcessScreen(form);
            }, 500);
        }

        function _progressiveSubmit(action, submitBtn) {
            var pageField,
                params = '',
                errorField;

            if (dataMode === 'mode_knowledge') {
                // For the knowledge costumer version validate all the fields at once
                pageField = element.querySelectorAll('.form-group:not(.hide) .form-control, input[type=hidden]');
            } else {
                // For other versions do regular validation
                pageField = element.querySelectorAll('.level-' + currentLevel + ' .form-group:not(.hide) .form-control, input[type=hidden]');
            }

            EDC.forms.validateForm(pageField);

            if (!form.querySelectorAll('.form-control.error, input.error').length && serviceURL && (premiumURL || dataMode !== 'mode_normal') && teaserURL) {

                if ((dataMode === 'mode_ebook_regular' && docID.value === '') || dataMode === 'mode_ebook_persona') {
                    params = _getInitialParams();
                }

                params += encodeURIComponent('actionType') + '=' + encodeURIComponent(action) + '&';
                params += encodeURIComponent('assetTier') + '=' + encodeURIComponent(assetTier) + '&';
                params += encodeURIComponent('premiumUrl') + '=' + encodeURIComponent(premiumURL) + '&';
                params += encodeURIComponent('teaserUrl') + '=' + encodeURIComponent(teaserURL) + '&';
                params += encodeURIComponent(docID.name) + '=' + encodeURIComponent(docID.value) + '&';
                params += encodeURIComponent('refPremiumURL') + '=' + encodeURIComponent(refPremiumURL) + '&';
                params += encodeURIComponent('mode') + '=' + encodeURIComponent(dataMode) + '&';
                params += encodeURIComponent('pageTitle') + '=' + encodeURIComponent(pageTitle) + '&';
                params += encodeURIComponent('sneakPeek') + '=' + isSneakPeek + '&';

                EDC.forms.disableSubmit(submitBtn);
                response = false;
                documentURL = null;

                EDC.forms.submitFormData(serverAction, serviceURL, EDC.forms.getFormData(form, params), function (data) {
                    if (data && (data.URL || data.meaUnlocked)) {
                        // Full Normal PP unlocked or Sneak Peak unlocked
                        if (data.URL) {
                            pageLevel++;
                            documentURL = data.URL;
                        }

                        // Full MEA unlocked
                        if (data.meaUnlocked && data.meaUnlocked === 'yes') {
                            // PP will unlock Sneak Peek and Country Grid
                            meaResponse = data.meaUnlocked;
                            EDC.forms.ppUnlockFeatures(data.meaUnlocked, sneakPeek, form, countryGridCards, gridTitle);
                        }

                        _trackEvent();
                    } else if (action === 'next') {
                        _progressiveNextSubmit(submitBtn);
                    }

                    response = true;
                }, submitFailedMessage);

                if (action === 'form-submit' && submitBtn) {
                    _toggleProcessScreen(form);
                    timerId = setInterval(_handleSuccess, 100);
                }
            } else {
                if (dataMode === 'mode_knowledge') {
                    errorField = element.querySelector('.form-control.error');
                } else {
                    errorField = element.querySelector('.level-' + currentLevel + ' .form-control.error');
                }

                errorField.focus();
            }
        }

        function _progressiveFormSubmit(e) {
            var submitBtn = form.querySelector('button[type="submit"]:not(.hide)'),
                action = e.type === 'submit' ? submitBtn.getAttribute('data-action') : e.target.getAttribute('data-action');

            e.preventDefault();

            switch (action) {
                case 'email-cancel':
                    _progressiveCancel();
                    break;

                case 'back':
                    _progressiveBack();
                    break;

                case 'email-submit':
                    allowCancel = true;
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

        // If sneak peek is unlocked the link takes to the premiumURL. If not the form is displayed
        function _sneakPeekPreview(e) {
            if (!localSneakPeek.classList.contains('unlocked') || sneakPeekCta.getAttribute('href') === '#') {
                e.preventDefault();
            }

            if (!localSneakPeek.classList.contains('unlocked')) {
                myedcContainer.classList.remove('hide');
                form.classList.remove('hide');
                localSneakPeek.classList.add('hide');
            }
        }

        function _initializeProfilingMode() {
            // Checks if progressive profiling is MEA and checks if there's any PP sneak peek component on the page
            // Then assigns the variables of the PP sneak peek component found
            if (sneakPeekElement && (dataMode === 'mode_mea' || dataMode === 'mode_ebook_regular' || dataMode === 'mode_ebook_persona')) {
                sneakPeekForm = sneakPeekElement.querySelector('form');
                sneakPeek = sneakPeekElement.querySelector('.sneak-peek-container');
            }

            // Checks if element contains sneak peek data attribute and show/hide the sneak peek elements
            // Sneak Peek element doesn't load the full markup.
            if (isSneakPeek === 'yes') {
                form.classList.add('hide');
                element.classList.add('email-closed');

                if (externalPremiumURL && externalPremiumURL !== '' && externalPremiumURL !== '#') {
                    sneakPeekCta.setAttribute('href', externalPremiumURL);
                }
            }
        }

        function _noData() {
            if (element.classList.contains('logged-in')) {
                element.classList.remove('logged-in');
            }

            if (_isLevel5()) {
                ctasSection.querySelector('button').classList.remove('show');
                ctasSection.querySelector('button').classList.add('hide');
                myedcContainer.querySelector('.secondary-anchor').classList.add('hide');
                formSection.querySelector('label').classList.add('hide');
                formSection.querySelector('input').classList.add('hide');
                formSection.querySelector('.email-disclaimer').classList.add('hide');
                ctasSection.querySelector('a.level5').classList.remove('hide');
            } else {
                element.classList.add('l1-4');
                ctasSection.querySelector('a').classList.add('hide');

                // show only if button not shown
                if (ctasSection.querySelector('button.submit').classList.contains('hide')) {
                    ctasSection.querySelector('a.in').classList.remove('hide');
                }

            }
            // Show hide only if not myedc logged in
            if(localSneakPeek) {
                localSneakPeek.classList.remove('hide');
            }

            ctasSection.classList.remove('hide');
        }

        if (Object.keys(EDC.props.userData).length > 1 ) {
            if (!element.classList.contains('logged-in')) {
                element.classList.add('logged-in');
            }

            if (element.querySelector('.title span.out')) {
                element.querySelector('.title span.out').classList.add('hide');
            }

            if (element.querySelector('.title span.in')) {
                element.querySelector('.title span.in').classList.remove('hide');
            }

            if (element.querySelector('p.description span.out')) {
                element.querySelector('p.description span.out').classList.add('hide');
            }

            if (element.querySelector('p.description span.in')) {
                element.querySelector('p.description span.in').classList.remove('hide');
            }

            ctasSection.querySelector('button').classList.remove('show');
            ctasSection.querySelector('button').classList.add('hide');
            ctasSection.querySelector('a.in').classList.remove('hide');
            formSection.querySelector('label').classList.add('hide');
            formSection.querySelector('input').classList.add('hide');
            formSection.querySelector('.email-disclaimer').classList.add('hide');
            ctasSection.classList.remove('hide');
        } else {
            _noData();
        }

        function _specialMyedcTracking(e) {
            var obj,
                btnClicked = e.currentTarget,
                classes = btnClicked.classList;

            obj = {
                eventInfo: {
                    eventAction: element.dataset.eventAction,
                    eventText: btnClicked.textContent.trim().toLowerCase(),
                    eventValue2: element.dataset.productType ? element.dataset.productType.toLowerCase() : '',
                    eventValue3: element.dataset.productDesc ? element.dataset.productDesc.toLowerCase() : '',
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: form.dataset.eventDestinationPage,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            if (classes.contains('submit')) {
                obj.eventInfo.eventComponent = element.dataset.eventComponent.toLowerCase();
                obj.eventInfo.eventType = 'form';
                obj.eventInfo.eventName = 'form submit - pp level ' + currentLevel;
                obj.eventInfo.eventValue = docID.value;
                obj.eventInfo.engagementType = '3';
            } else {
                obj.eventInfo.eventComponent = 'progressive profiling';
                obj.eventInfo.eventType = 'button';
                obj.eventInfo.eventName = 'button click - ' + btnClicked.textContent.trim().toLowerCase();
                obj.eventInfo.eventLevel = 'primary';

                if (classes.contains('edc-primary-btn')) {
                    obj.eventInfo.engagementType = '2';
                } else {
                    obj.eventInfo.engagementType = '1';
                }
            }

            EDC.utils.dataLayerTracking(obj);
        }

        // Attach events
        function _attachEvents() {
            var ctas = form.querySelectorAll('.cta'),
                country = form.querySelector('.country select'),
                provinceSelect = form.querySelector('.province .can select'),
                stateSelect = form.querySelector('.province .usa select'),
                provinceInput = form.querySelector('.province input'),
                provinceLabel = form.querySelector('.province > label'),
                myedcBtns = myedcContainer.querySelectorAll('a'),
                formAnchor = form.querySelectorAll('.form-ctas a'),
                formBtn = form.querySelectorAll('.form-ctas button');

            EDC.utils.attachEvents(ctas, 'click', _progressiveFormSubmit);
            EDC.utils.attachEvents(sneakPeekCta, 'click', _sneakPeekPreview);
            EDC.utils.attachEvents(form, 'submit', _progressiveFormSubmit);

            EDC.utils.attachEvents(country, 'change', function () {
                EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel);
            });

            EDC.utils.attachEvents(window, 'resize', function () {
                EDC.forms.setHeightLabels(formRows);
            });

            EDC.utils.attachEvents(myedcBtns, 'click', _specialMyedcTracking);
            EDC.utils.attachEvents(formAnchor, 'click', _specialMyedcTracking);
            EDC.utils.attachEvents(formBtn, 'click', _specialMyedcTracking);
        }

        _initializeProfilingMode();
        _attachEvents();
        _trackUser();

        EDC.forms.fillHiddenFields(form);
        EDC.forms.validateChange(formElements);
        EDC.forms.setHeightLabelsOnLoad(formRows);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(form);
        EDC.forms.elqQPush();
    }
 
    // Public methods
    function init() {
        var progressiveProfiling = document.querySelectorAll('.c-progressive-profiling:not([data-component-state="initialized"])');

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

document.addEventListener('DOMContentLoaded', progressiveProfilingJS.init);