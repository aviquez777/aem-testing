var tapFormJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        // Private methods

        var form = element.querySelector('form'),
            formRows = form.querySelectorAll('.form-row'),
            formElements = form.elements,
            currentLevel = 1,
            pageLevel = form.querySelectorAll('.level').length,
            statusBar = element.querySelector('.status-bar'),
            headerNav = document.querySelector('.header-nav'),
            headerNavHeight = headerNav ? headerNav.offsetHeight : 0;

        // Data Layer
        function _trackEvent() {
            var caslConsent = form.querySelector('.casl'),
                caslValue = caslConsent.checked ? 'casl consent provided' : 'casl consent not provided',
                obj = {
                    eventInfo: {
                        eventComponent: form.dataset.eventComponent,
                        eventType: form.dataset.eventType,
                        eventName: form.dataset.eventName + ' - ' + form.getAttribute('name') + ' tap step ' + currentLevel,
                        eventAction: form.dataset.eventAction,
                        eventText: form.querySelector('button[type="submit"]:not(.hide)').getAttribute('data-event-text').toLowerCase(),
                        eventValue: form.querySelector('input[name="docID"]').value,
                        eventValue2: '',
                        eventPage: EDC.props.pageNameURL,
                        eventPageName: EDC.utils.getPageName(),
                        destinationPage: '',
                        engagementType: form.dataset.eventEngagement,
                        eventLevel: form.dataset.eventLevel
                    }
                },
                userSegment = {
                    legalForm: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="legalForm"]')),
                    companyProduct: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="companyProduct"]')),
                    companyService: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="companyService"]')),
                    industry: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="industry"]')),
                    channelsSell: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="channelsSell"]')),
                    annualSales: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="annualSales"]')),
                    employees: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="employees"]')),
                    currentlyExporting: EDC.forms.getOptionsSelected(form.querySelector('.radio-group [name="currentlyExporting"]:checked')),
                    tradeStatus: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="tradeStatus"]')),
                    onlineSales: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="onlineSales"]')),
                    exportingExperience: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="exportingExperience"]')),
                    marketsExport: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="marketsExport"]')),
                    marketingExpenses: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="marketingExpenses"]')),
                    staffingExpenses: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="staffingExpenses"]')),
                    recruitmentExpenses: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="recruitmentExpenses"]')),
                    randdExpenses: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="randdExpenses"]')),
                    professionalExpenses: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="professionalExpenses"]')),
                    businessExpenses: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="businessExpenses"]')),
                    painPoint: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="painPoint"]')),
                    marketsInt: EDC.forms.getOptionsSelected(form.querySelector('.dropdown [name="marketsInt-large"]'))
                };

            if (currentLevel === 4) {
                obj.eventInfo.eventValue2 = caslValue;
            }

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
                EDC.utils.scrollTo('top', (EDC.utils.getPosition(statusBar).y) - headerNavHeight);

                if (focusField) {
                    focusField.focus();
                }

            }
        }

        function _showExportingFields(e) {
            var exportingSections = form.querySelectorAll('.sub-section.exporting');

            exportingSections.forEach(function (section) {
                var sectionSelects = section.querySelectorAll('select'),
                    sectionInputs = section.querySelectorAll('input');

                if (e.target.value === 'yes') {
                    section.classList.remove('hide');

                    sectionInputs.forEach(function (field) {
                        field.setAttribute('required', true);
                    });

                    sectionSelects.forEach(function (field) {
                        field.setAttribute('required', true);
                    });
                } else {
                    section.classList.add('hide');

                    sectionInputs.forEach(function (field) {
                        field.removeAttribute('required');
                        field.classList.remove('error');
                        field.value = '';
                    });

                    sectionSelects.forEach(function (field) {
                        var dropdownElement = field.closest('.c-dropdown');

                        field.removeAttribute('required');
                        field.parentElement.classList.remove('error');

                        dropdownElement.querySelector('.text').innerHTML = field.getAttribute('data-default-value');
                        dropdownElement.querySelector('.text').classList.add('default');

                        dropdownElement.querySelectorAll('.item').forEach(function (item) {
                            item.classList.remove('active');
                            item.classList.remove('selected');
                        });
                    });
                }
            });
        }

        function _showOtherInput(e) {
            var elem = e.target,
                parentEl = elem ? elem.closest('.form-group') : '',
                input = parentEl ? parentEl.querySelector('input') : '';

            if (elem.value.toLowerCase() === 'other' || elem.value.toLowerCase() === 'other/none') {
                input.classList.remove('hide');
                input.setAttribute('required', true);
            } else {
                input.value = '';
                input.classList.add('hide');
                input.removeAttribute('required');
                input.nextElementSibling.classList.remove('error');
            }
        }

        function _tapFormPagination(e) {
            var pageField,
                pageFormRows,
                errorField,
                step = form.querySelector('.step'),
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

                step.innerText = currentLevel;
                EDC.forms.setStatusBar(statusBar, currentLevel, pageLevel);

                if (currentLevel === 1) {
                    EDC.forms.toggleFormFields(element.querySelectorAll('.back'), 'display');
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
                            }

                            currentLevel++;

                            step.innerText = currentLevel;
                            EDC.forms.setStatusBar(statusBar, currentLevel, pageLevel);
                            pageFormRows = element.querySelectorAll('.level-' + currentLevel + ' .form-row');
                            EDC.forms.setHeightLabels(pageFormRows);

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
                provinceLabel = form.querySelector('.province > label'),
                radioExporting = form.querySelectorAll('.radio-exporting'),
                showOtherInput = form.querySelectorAll('.show-other');

            EDC.utils.attachEvents(ctas, 'click', _tapFormPagination);
            EDC.utils.attachEvents(form, 'submit', _tapFormPagination);
            EDC.utils.attachEvents(radioExporting, 'change', _showExportingFields);
            EDC.utils.attachEvents(showOtherInput, 'change', _showOtherInput);

            EDC.utils.attachEvents(country, 'change', function () {
                EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel);
            });

            EDC.forms.validateChange(formElements);

            EDC.utils.attachEvents(window, 'resize', function () {
                EDC.forms.setHeightLabels(formRows);
            });
        }

        _attachEvents();
        EDC.forms.fillHiddenFields(form);
        EDC.forms.setHeightLabelsOnLoad(formRows);

        EDC.forms.WaitUntilCustomerGUIDIsRetrieved(form);
        EDC.forms.elqQPush();
    }

    // Public methods
    function init() {
        var tapForm = document.querySelectorAll('.c-tap-form:not([data-component-state="initialized"])');

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

document.addEventListener('DOMContentLoaded', tapFormJS.init);