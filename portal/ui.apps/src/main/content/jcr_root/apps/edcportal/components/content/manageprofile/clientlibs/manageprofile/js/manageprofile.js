var updateDetailedInformationForm = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var formElements = element.elements,
            country = element.querySelector('.country select'),
            provinceSelect = element.querySelector('.province .can select'),
            stateSelect = element.querySelector('.province .usa select'),
            provinceInput = element.querySelector('.province input'),
            provinceLabel = element.querySelector('.province > label'),
            itemsToCapitalize = element.querySelectorAll('input[name="title"], input[name="companyName"], input[name="companyCity"]'),
            inputs = element.querySelectorAll('input[type="radio"]'),
            telToMask = element.querySelector('input[type="tel"][name="mainPhone"]'),
            institutionText = element.querySelector('.institution-text'),
            institutionTextInput = institutionText ? institutionText.querySelector('input') : null,
            institutionAutocomplete = element.querySelector('.institution-autocomplete'),
            institutionAutocompleteOptions,
            institutionAutocompleteInput = institutionAutocomplete ? institutionAutocomplete.querySelector('.c-autocomplete-field input') : null,
            institutionAutocompleteInputHiddenInput = institutionAutocomplete ? institutionAutocomplete.querySelector('#search-result-field') : null,
            institutionAutocompleteOther = element.querySelector('.institution-autocomplete-other'),
            institutionAutocompleteOtherInput = institutionAutocompleteOther ? institutionAutocompleteOther.querySelector('input') : null,
            otherJobDropdown = element.querySelector('[data-forcing="other-job"] select'),
            isFi = element.classList.contains('fi'),
            currentSelectedValue = '';

        function _trackEvent(e, num) {
            var el = e.target,
                userSegment,
                selectedRadio,
                obj = {
                    eventInfo: {
                        eventComponent: element.dataset.eventComponent.toLowerCase(),
                        eventType: element.dataset.eventType.toLowerCase(),
                        eventName: element.querySelector('.page-' + num).dataset.eventName ? element.querySelector('.page-' + num).dataset.eventName.toLowerCase() : null,
                        eventAction: element.dataset.eventAction.toLowerCase(),
                        eventValue: element.dataset.eventValue.toLowerCase(),
                        eventPage: EDC.props.pageNameURL.toLowerCase(),
                        eventPageName: EDC.utils.getPageName().toLowerCase(),
                        engagementType: element.dataset.eventEngagement.toLowerCase()
                    }
                };

            if (typeof inputs !== 'undefined') {
                inputs.forEach(function (radio) {
                    if (radio.checked) {
                        selectedRadio = radio.value.toLowerCase();
                    }
                });
            }

            function _getMultiDropdownData(id) {
                var elements = el.querySelector('[data-form-field="' + id + '"]') ? el.querySelector('[data-form-field="' + id + '"]').querySelectorAll('.menu .selected-item') : [],
                    elementsValue = '';

                if (elements.length > 0) {
                    elements.forEach(function (item, index) {
                        elementsValue += item.textContent;
                        if ((index + 1) < elements.length) {
                            elementsValue += '|';
                        }
                    });
                }

                return elementsValue.toLowerCase();
            }

            if (num === 1) {
                obj.eventInfo.eventText = el.textContent.toLowerCase();
                userSegment = {
                    profileType: selectedRadio
                };
            } else if (num === 2) {
                obj.eventInfo.eventText = el.querySelector('button[type="submit"]').textContent.toLowerCase();
                obj.eventInfo.destinationPage = element.getAttribute('action').toLowerCase();
                userSegment = {
                    annualSales: el.querySelector('select[id^=annualSales]') ? el.querySelector('select[id^=annualSales]').value.toLowerCase() : '',
                    exportStatus: el.querySelector('select[id^=tradeStatus]') ? el.querySelector('select[id^=tradeStatus]').value.toLowerCase() : '',
                    exportChallenges: _getMultiDropdownData('painPoint'),
                    industrySector: el.querySelector('select[id^=industry]') ? el.querySelector('select[id^=industry]').value.toLowerCase() : '',
                    sellCountries: _getMultiDropdownData('marketsInt'),
                    buyCountries: _getMultiDropdownData('countriesToBuyFrom'),
                    marketsInterest: _getMultiDropdownData('marketsInt'),
                    profileType: selectedRadio
                };
            }

            EDC.utils.userSegmentTracking(userSegment);
            EDC.utils.dataLayerTracking(obj);
        }

        function _validateSubmit(e) {
            var submitBtn = element.querySelector('button[type="submit"]'),
                errorField;

            e.preventDefault();
            EDC.forms.validateForm(formElements);

            if (!element.querySelectorAll('.form-control.error, input.error:not(.discard-input), select.error, textarea.error').length) {
                _trackEvent(e, 2);
                EDC.forms.disableSubmit(submitBtn);
                element.submit();
            } else {
                _trackEvent(e, 2);
                errorField = element.querySelector('.form-control.error');

                if (errorField) {
                    errorField.focus();
                }
            }
        }

        function _capitalize() {
            itemsToCapitalize.forEach(function (item) {
                item.value = item.value.charAt(0).toUpperCase() + item.value.slice(1);
            });
        }

        function _otherJobDropdownChanged(e, manualValue) {
            var eTarget = e ? e.target : null,
                eValue = eTarget ? eTarget.value : null,
                sectionToShow = element.querySelector('[data-dependency="other-job"]'),
                sectionToShowField = sectionToShow ? sectionToShow.querySelector('input') : null,
                oldName = sectionToShowField.dataset.oldName;

            if ((manualValue && manualValue !== currentSelectedValue || (eValue && eValue !== currentSelectedValue))) {
                if (!e && manualValue && manualValue.toLowerCase() === 'other') {
                    currentSelectedValue = manualValue;
                    sectionToShow.classList.remove('hide');
                    sectionToShowField.setAttribute('name', otherJobDropdown.name);
                    sectionToShowField.dataset.oldName = 'fiOtherJobRole';
                    otherJobDropdown.dataset.oldName = 'fiJobRole';
                    otherJobDropdown.name = 'fiJobRoleSelect';
                } else if (eValue && eValue.toLowerCase() === 'other') {
                    currentSelectedValue = eValue;

                    if (sectionToShowField) {
                        sectionToShowField.setAttribute('required', 'required');
                        sectionToShowField.setAttribute('data-old-name', sectionToShowField.name);

                        if (eTarget) {
                            sectionToShowField.setAttribute('name', eTarget.name);
                            eTarget.setAttribute('data-old-name', eTarget.name);
                            eTarget.setAttribute('name', eTarget.name + 'Select');
                        }
                    }

                    if (sectionToShow) {
                        sectionToShow.classList.remove('hide');
                    }
                } else {
                    currentSelectedValue = eValue;

                    if (sectionToShow) {
                        sectionToShow.classList.add('hide');
                    }

                    if (sectionToShowField) {
                        sectionToShowField.value = '';
                        sectionToShowField.removeAttribute('required');
                        sectionToShowField.classList.remove('error');

                        if (oldName) {
                            sectionToShowField.setAttribute('name', oldName);
                            sectionToShowField.removeAttribute('data-old-name');

                            if (eTarget) {
                                eTarget.setAttribute('name', eTarget.dataset.oldName);
                                eTarget.removeAttribute('data-old-name');
                            }
                        }

                        EDC.forms.validateField(sectionToShowField);
                    }
                }
            }
        }

        function _countryListeners() {
            var provinceParent = provinceSelect ? provinceSelect.closest('.form-group.province') : null;

            if (country && country.value && provinceSelect && stateSelect && provinceInput && provinceLabel) {
                EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel, false);

                // US 321052 AC 7
                if (isFi && provinceParent && country.value.toLowerCase() === 'can') {
                    provinceParent.classList.add('hide');
                    provinceSelect.removeAttribute('required');
                }
            }

            if (institutionText && institutionAutocomplete && institutionAutocompleteOther && institutionTextInput && institutionAutocompleteOtherInput && country) {
                if (country.value.toLowerCase() === 'can') {
                    institutionText.classList.add('hide');
                    institutionTextInput.removeAttribute('required');
                    institutionTextInput.setAttribute('validation-rule', 'none');
                    institutionTextInput.setAttribute('name', 'companyNameNonCan');
                    institutionAutocompleteInputHiddenInput.setAttribute('name', 'companyName');
                    institutionAutocompleteInput.setAttribute('required', 'required');
                    EDC.forms.validateField(institutionTextInput);
                    institutionAutocomplete.classList.remove('hide');
                    institutionAutocompleteOther.classList.remove('hide');
                } else {
                    institutionText.classList.remove('hide');
                    institutionTextInput.setAttribute('required', 'required');
                    institutionTextInput.setAttribute('validation-rule', 'companyName');
                    institutionTextInput.setAttribute('name', 'companyName');
                    institutionAutocompleteInputHiddenInput.setAttribute('name', 'companyNameCan');
                    institutionAutocompleteInput.removeAttribute('required');
                    institutionAutocompleteInput.classList.remove('error');
                    institutionAutocomplete.classList.add('hide');
                    institutionAutocompleteOther.classList.add('hide');
                    institutionAutocompleteOtherInput.removeAttribute('required');
                    EDC.forms.validateField(institutionAutocompleteOtherInput);
                }
            }
        }

        function _optionSelected(e) {
            var eTarget = e ? e.target : null,
                eValue = eTarget ? eTarget.textContent : null;

            if (institutionAutocompleteOtherInput && eValue) {
                if (eValue.toLowerCase() === 'other' || eValue.toLowerCase() === 'autre') {
                    institutionAutocompleteOtherInput.setAttribute('required', 'required');
                    institutionAutocompleteOtherInput.removeAttribute('disabled');
                } else {
                    institutionAutocompleteOtherInput.value = '';
                    institutionAutocompleteOtherInput.removeAttribute('required');
                    institutionAutocompleteOtherInput.classList.remove('error');
                    institutionAutocompleteOtherInput.setAttribute('disabled', 'disabled');
                    institutionAutocompleteInput.removeAttribute('disabled');
                    EDC.forms.validateField(institutionAutocompleteOtherInput);
                }
            }
        }

        function _optionSelectedWithKeys(e) {
            var keyCode = e.keyCode || e.which;

            if (keyCode === EDC.props.enterKeyCode || keyCode === EDC.props.spaceKeyCode) {
                e.preventDefault();
                _optionSelected(e);
            }
        }

        function _checkIfPrepopulated() {
            var countryOptions = country ? country.querySelectorAll('option') : null,
                selectedCountry,
                jobOptions = otherJobDropdown ? otherJobDropdown.querySelectorAll('option') : null,
                selectedJob,
                companyInput = element.querySelector('.institution-text input'),
                companyAutocomplete = element.querySelector('.c-autocomplete-field input');

            if (countryOptions) {
                selectedCountry = countryOptions ? countryOptions[country.selectedIndex] : null;
            }

            if (jobOptions) {
                selectedJob = jobOptions ? jobOptions[otherJobDropdown.selectedIndex] : null;
            }

            if (selectedCountry && selectedCountry.value) {
                if (companyAutocomplete && companyInput) {
                    companyAutocomplete.value = companyInput.value;

                    if (companyAutocomplete.value.toLowerCase() === 'other' || companyAutocomplete.value.toLowerCase() === 'autre') {
                        companyAutocomplete.setAttribute('disabled', 'disabled');
                    }
                }

                _countryListeners();
            }

            if (companyAutocomplete && companyAutocomplete.value !== '' && companyAutocomplete.value !== 'Other' && institutionAutocompleteOtherInput) {
                institutionAutocompleteOtherInput.setAttribute('disabled', 'disabled');
            }

            if (selectedJob && selectedJob.value) {
                _otherJobDropdownChanged(null, selectedJob.value);
            }
        }

        function _attachEvents() {
            EDC.utils.attachEvents(itemsToCapitalize, 'blur', function (e) {
                e.target.value = e.target.value.charAt(0).toUpperCase() + e.target.value.slice(1);
            });

            EDC.utils.attachEvents(element, 'submit', _validateSubmit);
            EDC.utils.attachEvents(otherJobDropdown, 'change', _otherJobDropdownChanged);

            if (country) {
                EDC.utils.attachEvents(country, 'change', _countryListeners);
            }

            PubSub.subscribe('autocomplete-items-ready', function (msg, status) {
                if (status) {
                    institutionAutocompleteOptions = institutionAutocomplete ? institutionAutocomplete.querySelectorAll('.search-results button.item') : null;

                    EDC.utils.attachEvents(institutionAutocompleteOptions, 'mousedown', _optionSelected);
                    EDC.utils.attachEvents(institutionAutocompleteOptions, 'keydown', _optionSelectedWithKeys);
                }
            });

            EDC.utils.attachEvents(institutionAutocompleteOtherInput, 'input', function (e) {
                var eTarget = e.target,
                    eValue = eTarget ? eTarget.value : null;

                if (institutionAutocompleteInput) {
                    if (eValue === '') {
                        institutionAutocompleteInput.removeAttribute('disabled');
                        institutionAutocompleteInput.value = '';
                    } else {
                        institutionAutocompleteInput.setAttribute('disabled', 'disabled');
                        PubSub.publish('autocomplete-select-other', true);
                    }
                }
            });

            EDC.utils.attachEvents(institutionAutocompleteInput, 'input', function (e) {
                var eTarget = e.target,
                    eValue = eTarget ? eTarget.value : null;

                if (institutionAutocompleteOtherInput) {
                    if (eValue === '') {
                        institutionAutocompleteOtherInput.removeAttribute('disabled');
                        institutionAutocompleteOtherInput.value = '';
                    }
                }
            });
        }

        _capitalize();
        _attachEvents();
        _checkIfPrepopulated();

        EDC.forms.validateChange(formElements);

        if (telToMask) {
            EDC.forms.applyDataMask(element, telToMask);
        }
    }

    // Public methods
    function init() {
        var updateDetailedInformationForms = document.querySelectorAll('form.c-update-detailed-information:not([data-component-state="initialized"])');

        if (updateDetailedInformationForms) {
            updateDetailedInformationForms.forEach(function (elem) {
                // show the companyProvinceInput hidden by backend
                var provInput = elem.querySelector('[data-form-field="companyProvinceInput"]');

                if (provInput) {
                    provInput.classList.remove('hide');
                }

                // add no validate via JS
                elem.setAttribute('novalidate', 'novalidate');
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', updateDetailedInformationForm.init);