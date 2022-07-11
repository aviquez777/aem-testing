var inquiryFormJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var form = element.querySelector('form'),
            formRows = form.querySelectorAll('.form-row'),
            formElements = form.elements,
            currentLevel = 1,
            lastAccessedLevel = 1,
            levels = form.querySelectorAll('.level'),
            pageLevel = levels.length,
            country = element.querySelector('.country select'),
            successMessage = element.querySelector('.success-message'),
            painPointDropdown = element.querySelector('select[name="painPoint"]').closest('.dropdown'),
            painPointDescriptionsInput = element.querySelector('input[name="painPointDescriptions"]'),
            painPointRoutingInput = element.querySelector('input[name="routing"]'),
            provinceSelect = element.querySelector('.province .can select'),
            stateSelect = element.querySelector('.province .usa select'),
            provinceInput = element.querySelector('.province input'),
            provinceLabel = element.querySelector('.province > label'),
            nav = element.querySelector('.nav-bar'),
            tabLabels = nav.querySelectorAll('.tab-label'),
            actionsNav = element.querySelector('.actions-nav'),
            arrowRight = actionsNav.querySelector('.icon-arrow-right'),
            arrowLeft = actionsNav.querySelector('.icon-arrow-left'),
            submitBtn = form.querySelector('button[type="submit"][data-action="done"]');

        // Target for next/submit buttons
        function _buttonTarget(target) {
            var buttonTarget = target;

            if (target.dataset.eventType && target.dataset.eventType === 'form') {
                buttonTarget = currentLevel === 3 ? form.querySelector('button[type="submit"][data-action="done"]') : form.querySelector('button[type="submit"][data-action="next"]');
            }
            return buttonTarget;
        }

        // Get Markets of interest value
        function _marketsInt() {
            var selected = form.querySelectorAll('.c-multi-select .checkbox-group.selected [type="checkbox"]'),
                markets = '';

            selected.forEach(function (check) {
                if (markets !== '') {
                    markets += ';';
                }
                markets += check.value ? check.value.toLowerCase() : '';
            });
            return markets;
        }

        // Data Layer
        function _trackEvent(target) {
            var buttonTarget = _buttonTarget(target),
                obj = {
                    eventInfo: {
                        eventComponent: form.dataset.eventComponent,
                        eventType: form.dataset.eventType,
                        eventName: form.dataset.eventName + ' - ' + form.getAttribute('name') + ' - tap step ' + currentLevel,
                        eventText: buttonTarget.dataset.eventText.toLowerCase(),
                        eventAction: form.dataset.eventAction,
                        eventValue: form.querySelector('input[name="docID"]').value,
                        eventPage: EDC.props.pageNameURL,
                        destinationPage: '',
                        engagementType: form.dataset.eventEngagement,
                        eventLevel: form.dataset.eventLevel,
                        eventPageName: EDC.utils.getPageName(),
                        poFuture: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="poFuture"]')),
                        tradeStatus: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="tradeStatus"]'))
                    }
                },
                userSegment = {
                    annualSales: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="annualSales"]')),
                    tradeStatus: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="tradeStatus"]')),
                    marketsInt: _marketsInt()
                };

            switch (currentLevel) {
                case 1:
                    obj.eventInfo.eventValue2 = EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="painPoint"]'));
                    obj.eventInfo.eventValue3 = form.querySelector('input[name="year"]').value;
                    break;
                case 2:
                    obj.eventInfo.eventValue2 = EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="painPoint"]'));
                    obj.eventInfo.eventValue3 = EDC.forms.getOptionsSelected(form.querySelector('input[name="industry"]'));
                    break;
                case 3:
                    obj.eventInfo.eventValue2 = form.querySelector('input[name="caslConsent"]').checked ? 'checked' : 'not';
                    break;
            }

            EDC.utils.userSegmentTracking(userSegment, true);
            EDC.utils.dataLayerTracking(obj);
        }

        // Private methods
        function _isBtnClickable(el) {
            return parseInt(el.getAttribute('data-step'), 10) <= lastAccessedLevel;
        }

        // Helper function to show and hide form pages
        function _showHideLevels(current, following) {
            var formFields,
                focusField;

            // Hides data from current level page
            EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + current), 'display');
            if (following) {
                // Shows data from following level page
                formFields = element.querySelectorAll('.level-' + following + ' .form-group');
                focusField = formFields[0].querySelector('.form-control');

                EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + following), 'display');

                if (focusField) {
                    focusField.focus();
                }
            }
        }

        function _toggleActionBtns() {
            element.querySelector('.done-btn').classList[currentLevel === pageLevel ? 'remove' : 'add']('hide');
            element.querySelector('.done-btn').classList[currentLevel === pageLevel ? 'add' : 'remove']('show');
            element.querySelector('.next-btn').classList[currentLevel === pageLevel ? 'add' : 'remove']('hide');
            element.querySelector('.next-btn').classList[currentLevel === pageLevel ? 'remove' : 'add']('show');
            element.querySelector('.back').classList[currentLevel === 1 ? 'add' : 'remove']('hide');
            element.querySelector('.back').classList[currentLevel === 1 ? 'remove' : 'add']('show-flex');
        }

        function _handleButtonClick(e) {
            var el = e.type === 'submit' ? e.currentTarget.querySelector('button[type="submit"]') : e.currentTarget,
                elType = el.getAttribute('type'),
                dataStep,
                backStep;

            e.preventDefault();
            if (el.tagName.toLowerCase() === 'button') {
                if (elType === 'submit' && el.getAttribute('data-action') === 'next') {
                    dataStep = element.querySelector('.tab-label[data-step="' + currentLevel + '"]');

                    if (dataStep) {
                        dataStep.click();
                    }
                } else if (el.getAttribute('role') === 'tab' && _isBtnClickable(el)) {
                    el.classList.add('dirty');
                    tabLabels.forEach(function (label, index) {
                        label.parentNode.classList.remove('active');
                        label.classList.remove('selected');
                        if (parseInt(el.getAttribute('data-step'), 10) === index + 1) {
                            label.parentNode.classList.add('active');
                        }
                    });
                    el.classList.add('selected');
                    _showHideLevels(currentLevel, el.getAttribute('data-step'));
                    currentLevel = parseInt(el.getAttribute('data-step'), 10);
                    _toggleActionBtns();
                }
            } else if (el.tagName.toLowerCase() === 'a') {
                dataStep = parseInt(element.querySelector('.tab-labels li.active button').getAttribute('data-step'), 10) - 1;

                if (dataStep) {
                    backStep = element.querySelector('.tab-labels li button[data-step="' + dataStep + '"]');

                    if (backStep) {
                        backStep.click();
                    }
                }
            }
        }

        function _formPagination(e) {
            var pageField,
                pageFormRows,
                errorField,
                serviceURL = form.getAttribute('action'),
                action = e.type === 'submit' ? element.querySelector('button[type="submit"]:not(.hide)').getAttribute('data-action') : e.target.getAttribute('data-action'),
                submitFailedMessage = element.querySelector('.submit-failed-message'),
                target = e.target;

            pageField = element.querySelectorAll('.level-' + currentLevel + ' .form-group .form-control, input[type=hidden]');
            e.preventDefault();

            if (action === 'back') {
                _trackEvent(target);
                _handleButtonClick(e);
            } else {
                EDC.forms.validateForm(pageField);

                if (!form.querySelectorAll('.level-' + currentLevel + ' .form-control.error, .level-' +
                    currentLevel + ' input.error, .level-' + currentLevel + ' .radio-group.error').length && serviceURL) {
                    _trackEvent(target);
                    if (action === 'done' && submitBtn) {
                        EDC.forms.disableSubmit(submitBtn);

                        EDC.forms.submitFormData('POST', serviceURL, EDC.forms.getFormData(form), function () {
                            form.classList.add('hide');
                            successMessage.classList.remove('hide');
                            EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);
                        }, submitFailedMessage);
                    }

                    if (action === 'next') {
                        if (currentLevel < pageLevel) {
                            _showHideLevels(currentLevel, (currentLevel + 1));

                            if (element.querySelector('.back').classList.contains('hide')) {
                                EDC.forms.toggleFormFields(element.querySelectorAll('.back'), 'display');
                            }

                            currentLevel++;
                            element.querySelector('.level-' + currentLevel).setAttribute('data-dirty-li', 'true');
                            lastAccessedLevel = element.querySelectorAll('.level[data-dirty-li="true"]').length;
                            pageFormRows = element.querySelectorAll('.level-' + currentLevel + ' .form-row');
                            EDC.forms.setHeightLabels(pageFormRows);

                            if (currentLevel === pageLevel) {
                                EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                                EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                            }
                            _handleButtonClick(e);
                        }
                    }
                } else {
                    errorField = element.querySelector('.level-' +
                        currentLevel + ' .form-control.error, .level-' +
                        currentLevel + ' .radio-group.error .form-control, .level-' +
                        currentLevel + ' .form-control.error input.search-input');
                    errorField.focus();
                }
            }
        }

        function _checkWidthForArrows() {
            var navWidth = nav.querySelector('.tab-labels-container').offsetWidth,
                tabLabelsWidth = 0,
                difference = 0,
                comparative = 3;

            tabLabels.forEach(function (label) {
                tabLabelsWidth += label.offsetWidth;
            });

            difference = tabLabelsWidth - navWidth;

            if (difference < comparative && difference > -comparative) {
                actionsNav.classList.add('hide');
                arrowRight.classList.remove('show');
            } else {
                actionsNav.classList.remove('hide');
                arrowRight.classList.add('show');
            }
        }

        function _resetForm() {
            var bubble1 = form.querySelectorAll('.tab-labels li')[0],
                bubble1btn = bubble1.querySelector('button');

            EDC.forms.resetForm(form);
            successMessage.classList.add('hide');
            levels.forEach(function (level) {
                level.classList.remove('show');
                level.classList.add('hide');
            });

            levels[0].classList.remove('hide');
            levels[0].classList.add('show');
            currentLevel = 1;
            _toggleActionBtns();
            form.classList.remove('hide');

            tabLabels.forEach(function (tab) {
                tab.classList.remove('dirty');
                tab.classList.remove('selected');
                tab.parentNode.classList.remove('active');
            });

            bubble1.classList.add('active');
            bubble1btn.classList.add('selected');
            bubble1btn.classList.add('dirty');

            form.querySelectorAll('.c-multi-select').forEach(function (el) {
                var btn = el.querySelector('.reset-options button');

                if (btn) {
                    btn.click();
                }
            });

            submitBtn.removeAttribute('disabled');
            submitBtn.classList.remove('disabled');
            form.reset();
            levels[0].querySelector('.form-control').focus();
        }

        function _handlePainPointDropdown() {
            setTimeout(function () {
                var selectedItems = painPointDropdown.querySelectorAll('.menu .item.selected-item'),
                    painPointDescription = '',
                    painPointRouting = '',
                    counter = 0,
                    hasEOG = false;

                selectedItems.forEach(function (item) {
                    var value = item.getAttribute('data-value'),
                        option = painPointDropdown.querySelector('select option[value="' + value + '"]'),
                        routing = option.getAttribute('data-routing');

                    painPointDescription += counter === 0 ? option.getAttribute('data-english-name') : '::' + option.getAttribute('data-english-name');
                    counter++;

                    if (!hasEOG) {
                        if (routing === 'EH') {
                            painPointRouting = 'EH';
                        } else if (routing === 'EOG') {
                            painPointRouting = 'EOG';
                            hasEOG = true;
                        }
                    }
                });

                painPointDescriptionsInput.value = painPointDescription;
                painPointRoutingInput.value = painPointRouting;
            }, 0);
        }

        function _fixOverflowInsideAccordion() {
            var panelContainer = element.closest('.panel-inner');

            if (panelContainer) {
                panelContainer.classList.add('remove-overflow');
            }
        }

        // Attach events
        function _attachEvents() {
            var ctas = form.querySelectorAll('.cta');

            EDC.utils.attachEvents(ctas, 'click', _formPagination);
            EDC.utils.attachEvents(form, 'submit', _formPagination);

            if (country) {
                EDC.utils.attachEvents(country, 'change', function () {
                    var dd = element.querySelector('.c-dropdown.can .dropdown.disabled');

                    EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel, false);

                    if (dd) {
                        dd.classList.remove('disabled');
                    }
                });
            }

            EDC.forms.validateChange(formElements);

            EDC.utils.attachEvents(window, 'resize', function () {
                EDC.forms.setHeightLabels(formRows);
                _checkWidthForArrows();
            });

            EDC.utils.attachEvents(successMessage.querySelector('button'), 'click', _resetForm);
            EDC.utils.attachEvents(tabLabels, 'click', _handleButtonClick);
            EDC.utils.attachEvents(painPointDropdown.querySelectorAll('.menu .item'), 'click', _handlePainPointDropdown);
            EDC.utils.attachEvents(arrowRight, 'click', function () {
                EDC.utils.scrollTabs(element, 'right', true);
            });
            EDC.utils.attachEvents(arrowLeft, 'click', function () {
                EDC.utils.scrollTabs(element, 'left', true);
            });
        }

        EDC.forms.fillHiddenFields(form);
        EDC.forms.setHeightLabelsOnLoad(formRows);
        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(form);
        EDC.forms.elqQPush();
        _attachEvents();
        _fixOverflowInsideAccordion();

        setTimeout(function () {
            _checkWidthForArrows();
        }, 1000);
    }

    // Public methods
    function init() {
        var tapForm = document.querySelectorAll('.c-inquiry:not([data-component-state="initialized"])');

        if (tapForm) {
            tapForm.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', inquiryFormJS.init);