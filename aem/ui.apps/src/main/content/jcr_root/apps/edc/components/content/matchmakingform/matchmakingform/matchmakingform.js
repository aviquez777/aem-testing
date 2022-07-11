var matchmakingEventsFormJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private methods

        var form = element.querySelector('form'),
            formElements = form.elements,
            currentLevel = 1,
            pageLevel = form.querySelectorAll('.level').length,
            statusBar = element.querySelector('.status-bar');

        // Data Layer
        function _trackEvent() {
            var obj = {
                    eventInfo: {
                        eventComponent: form.dataset.eventComponent,
                        eventType: form.dataset.eventType,
                        eventName: form.dataset.eventName + ' - ' + form.getAttribute('name') + ' step ' + currentLevel,
                        eventAction: form.dataset.eventAction,
                        eventText: form.querySelector('button[type="submit"]:not(.hide)').textContent,
                        eventValue: form.querySelector('input[name="docID"]').value,
                        eventValue2: form.querySelector('input[name="eventID"]').value,
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: '',
                        engagementType: form.dataset.eventEngagement,
                        eventLevel: form.dataset.eventLevel
                    }
                },
                userSegment = {
                    annualSales: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="annualSales"]')),
                    employees: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="employees"]')),
                    industry: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="industry"]')),
                    industrySvd: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="industrySvd"]')),
                    subindustrySvd: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="subindustrySvd"]')),
                    tradeStatus: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="tradeStatus"]')),
                    marketsInt: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="marketsInt-large"]')),
                    painPoint: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="painPoint"]')),
                    marketsExport: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="marketsExport"]')),
                    exportSales: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="exportSales"]'))
                };

            EDC.utils.userSegmentTracking(userSegment, true);
            EDC.utils.dataLayerTracking(obj);
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
                focusField.focus();

            }
        }

        function _matchmakingFormPagination(e) {
            var pageField,
                errorField,
                successMessage = element.querySelector('.success-message'),
                serviceURL = form.getAttribute('action'),
                submitBtn = form.querySelector('button[type="submit"]:not(.hide)'),
                action = e.type === 'submit' ? submitBtn.getAttribute('data-action') : e.target.getAttribute('data-action'),
                submitFailedMessage = element.querySelector('.submit-failed-message');

            pageField = element.querySelectorAll('.level-' + currentLevel + ' .form-group .form-control, input[type=hidden]');

            e.preventDefault();

            if (action === 'back') {
                element.querySelectorAll('.level-' + currentLevel + ' .form-control').forEach(function (elem) {
                    elem.classList.remove('error');
                });

                _showHideLevels(currentLevel, (currentLevel - 1));

                if (currentLevel === pageLevel) {
                    EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                    EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                }

                currentLevel--;

                EDC.forms.setStatusBar(statusBar, currentLevel, pageLevel);

                if (currentLevel === 1) {
                    EDC.forms.toggleFormFields(element.querySelectorAll('.back'), 'display');
                    element.querySelector('.bottom-ctas').classList.add('space-end');
                    element.querySelector('.bottom-ctas').classList.remove('space-center');
                }
            } else {
                EDC.forms.validateForm(pageField);

                if (!form.querySelectorAll('.form-control.error, input.error').length && serviceURL) {
                    _trackEvent();
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
                                element.querySelector('.bottom-ctas').classList.add('space-center');
                                element.querySelector('.bottom-ctas').classList.remove('space-end');
                            }

                            currentLevel++;

                            EDC.forms.setStatusBar(statusBar, currentLevel, pageLevel);

                            if (currentLevel === pageLevel) {
                                EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                                EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                            }
                        }
                    }
                } else {
                    errorField = element.querySelector('.level-' + currentLevel + ' .form-control.error');
                    errorField.focus();
                }
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

            EDC.utils.attachEvents(ctas, 'click', _matchmakingFormPagination);
            EDC.utils.attachEvents(form, 'submit', _matchmakingFormPagination);

            EDC.utils.attachEvents(country, 'change', function () {
                EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel);
            });

            EDC.forms.validateChange(formElements);
        }

        _attachEvents();
        EDC.forms.fillHiddenFields(form);

        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(form);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var matchmakingEvents = document.querySelectorAll('.c-matchmaking-events:not([data-component-state="initialized"])');

        if (matchmakingEvents) {
            matchmakingEvents.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', matchmakingEventsFormJS.init);