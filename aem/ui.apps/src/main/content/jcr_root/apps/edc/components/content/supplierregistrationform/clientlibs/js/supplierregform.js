var supplierRegistrationFormJS = (function () {
    'use strict';

    // Initialize the component
    function _initialize(element) {
        var d = document,
            form = element.querySelector('form'),
            formElements = form.elements,
            successMessage = element.querySelector('.success-message'),
            trackingNumber = successMessage.querySelector('.tracking-number'),
            submitFailedMessage = element.querySelector('.submit-failed-message'),
            otherBusinessServicesCheckbox,
            otherCorporateStatusCheckbox,
            otherDiverseSupplierCheckbox,
            otherCheckboxesTimer,
            serviceURL = form.getAttribute('action'),
            currentLevel = 1,
            pageLevel = form.querySelectorAll('.level').length,
            statusBar = element.querySelector('.status-bar'),
            level1 = form.querySelector('.level-1'),
            level2 = form.querySelector('.level-2'),
            level3 = form.querySelector('.level-3'),
            sin = level2.querySelector('[name="canSinNumber"]'),
            tin = level2.querySelector('.tin'),
            companyAddressSection = level3.querySelector('.company-address-section'),
            remitAddressSection = level3.querySelector('.remit-address-section'),
            countryField = companyAddressSection.querySelector('.country'),
            usaCode = countryField.getAttribute('data-usa-code'),
            canCode = countryField.getAttribute('data-can-code'),
            selectCountry = countryField.querySelector('select'),
            countryLabel = countryField.querySelector('.dropdown .default.text'),
            countryPlaceholder = countryLabel ? countryLabel.textContent : null,
            supplierType,
            telsToMask = element.querySelectorAll('input[data-mask]'),
            diverseSupplierSection = level3.querySelector('.certified-diverse-supplier-section'),
            messageBanner = d.querySelector('.c-message-banner'),
            canValue = 'canadian',
            usaValue = 'american',
            intValue = 'international';

        // Private methods
        // Tracking
        function _trackEvent(action) {
            var obj,
                level,
                currentEl = element.querySelector('.level:not(.hide)'),
                els = element.querySelectorAll('.level'),
                btn = form.querySelector('[data-action="' + action + '"]'),
                level1Checkboxes;

            els.forEach(function (el, index) {
                if (el === currentEl) {
                    level = index + 1;
                }
            });

            obj = {
                eventInfo: {
                    eventComponent: element.dataset.eventComponent.toLowerCase(),
                    eventType: element.dataset.eventType.toLowerCase(),
                    eventName: element.dataset.eventComponent.toLowerCase() + ' - Step ' + level,
                    eventAction: element.dataset.eventAction.toLowerCase(),
                    eventPage: EDC.props.pageNameURL,
                    destinationPage: element.dataset.destinationPage,
                    engagementType: element.dataset.engagementType,
                    eventLevel: element.dataset.eventLevel,
                    eventPageName: EDC.utils.getPageName()
                }
            };

            if (btn && btn.tagName && btn.textContent) {
                obj.eventInfo.eventText = btn.tagName.toLowerCase() === 'button' ? btn.textContent.toLowerCase() : '';
            }

            switch (level) {
                case 1:
                    obj.eventInfo.eventValue = level1.querySelector('.supplier-information input[type="radio"]:checked').value;
                    obj.eventInfo.eventValue2 = level1.querySelector('[name="caslConsent"]:checked') ? 'check' : 'uncheck';
                    obj.eventInfo.eventValue3 = level1.querySelector('[data-form-field="corporate-status"] input[type="radio"]:checked').value;
                    obj.eventInfo.eventValue4 = '';
                    level1Checkboxes = level1.querySelectorAll('.multi-input-section input[type="checkbox"]:checked');
                    level1Checkboxes.forEach(function (checkbox, index) {
                        obj.eventInfo.eventValue4 += checkbox.value + (level1Checkboxes.length > index + 1 && level1Checkboxes.length > 1 ? ',' : '');
                    });
                    break;
                case 2:
                    if (level2.querySelector('[data-form-field="supplier-type"] input[type="radio"]:checked')) {
                        obj.eventInfo.eventValue = level2.querySelector('[data-form-field="supplier-type"] input[type="radio"]:checked').value;
                    }
                    break;
                case 3:
                    obj.eventInfo.eventValue = level3.querySelector('[name="legalName"]').value;
                    obj.eventInfo.eventValue2 = '';
                    if (level3.querySelector('[name="bcorp-certification"]:checked')) {
                        obj.eventInfo.eventValue2 += level3.querySelector('[name="bcorp-certification"]:checked').value;
                    }
                    if (level3.querySelector('[name="diverse-supplier"]:checked')) {
                        obj.eventInfo.eventValue2 += ':' + level3.querySelector('[name="diverse-supplier"]:checked').value;
                    }
                    break;
            }
            EDC.utils.dataLayerTracking(obj);
        }

        function _toggleItems(item) {
            var displays = item.getAttribute('data-display').split(',');

            if (!item.classList.contains('hide')) {
                item.classList.add('hide');
            }

            displays.forEach(function (display) {
                if (display === supplierType) {
                    item.classList.remove('hide');
                    item.querySelectorAll('input:not(.discard-input):not(.disabled)').forEach(function (input) {
                        if (!input.classList.contains('optional')) {
                            input.setAttribute('required', 'required');
                        }
                    });
                }
            });
        }

        function _setCountryLabel(c) {
            var countryDropdown = countryField.querySelector('.ui.dropdown'),
                thisCountryLabel = countryField.querySelector('.dropdown .text'),
                activeItem = countryDropdown.querySelector('.item.active.selected');

            if (c === 'empty') {
                if (countryDropdown && countryDropdown.classList.contains('disabled')) {
                    countryDropdown.classList.remove('disabled');
                }

                if (thisCountryLabel && countryPlaceholder) {
                    thisCountryLabel.innerHTML = countryPlaceholder;
                }

                if (countryDropdown.querySelector('select')) {
                    countryDropdown.querySelector('select').value = '';
                }

                if (activeItem) {
                    activeItem.classList.remove('active');
                    activeItem.classList.remove('selected');
                }
            } else {
                countryDropdown.classList.add('disabled');

                selectCountry.querySelectorAll('option').forEach(function (item, index) {
                    if (c === item.value) {
                        countryDropdown.querySelectorAll('.item')[index - 1].click();

                        if (!countryDropdown.classList.contains('disabled')) {
                            countryDropdown.classList.add('disabled');
                        }
                    }
                });
            }
        }

        function _displaySupplierTypeSections() {
            var subtext = element.querySelectorAll('.level-3 .subtext'),
                level2Items = level2.querySelectorAll('[data-display]'),
                postalCodeLabels = element.querySelectorAll('.company-postal label'),
                remitPostalCodeLabels = element.querySelectorAll('.remit-company-postal label'),
                remitInformationSection = element.querySelector('.remit-information-section');

            switch (supplierType) {
                case canValue:
                    _setCountryLabel(canCode);
                    break;
                case usaValue:
                    _setCountryLabel(usaCode);
                    break;
                case intValue:
                    _setCountryLabel('empty');
                    break;
                default:
                    break;
            }

            level2Items.forEach(function (item) {
                _toggleItems(item);
            });

            subtext.forEach(function (item) {
                _toggleItems(item);
            });

            postalCodeLabels.forEach(function (item) {
                _toggleItems(item);
            });

            remitPostalCodeLabels.forEach(function (item) {
                _toggleItems(item);
            });

            _toggleItems(remitInformationSection);

            remitInformationSection.querySelectorAll('.remit-information-specific').forEach(function (item) {
                _toggleItems(item);
            });
        }

        // Helper function to show and hide form pages
        function _showHideLevels(current, following) {
            var formFields = [];

            // Hides data from current level page
            EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + current), 'display');
            if (following) {
                EDC.forms.toggleFormFields(element.querySelectorAll('.level-' + following), 'display');
                if (supplierType && element.querySelector('.level.show.level-2')) {
                    _displaySupplierTypeSections();
                }
                element.querySelectorAll('.level-' + following + ' .form-control:not(.disabled):not(.error)').forEach(function (el) {
                    formFields.push(el);
                });
                formFields.some(function (field) { // NOSONAR
                    var validField = !field.closest('.hide'),
                        fieldToFocus;

                    if (validField) {
                        if (field.tagName.toLowerCase() === 'input' || field.classList.contains('dropdown')) {
                            fieldToFocus = field;
                        } else if (field.querySelector('input:not(.hide)')) {
                            fieldToFocus = field.querySelector('input:not(.hide)');
                        } else if (field.querySelector('button')) {
                            fieldToFocus = field.querySelector('button');
                        }

                        if (fieldToFocus) {
                            fieldToFocus.focus(); //NOSONAR
                            window.scrollTo(0, EDC.utils.getPosition(fieldToFocus).y - 200);
                        }
                    }

                    return validField;
                });
            }
        }

        function _paginationBack(currentLevelEl) {
            element.querySelectorAll('input, .dropdown').forEach(function (elem) {
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
                EDC.forms.toggleFormFields(element.querySelectorAll('.privacy-policy'), 'display');
                element.querySelector('.bottom-ctas').classList.add('space-end');
                element.querySelector('.bottom-ctas').classList.remove('space-center');
            }

            if (currentLevelEl.classList.contains('level-2')) {
                if (sin.getAttribute('required')) {
                    sin.removeAttribute('required');
                }
                if (tin.getAttribute('required')) {
                    tin.removeAttribute('required');
                }
            }
        }

        function _showErrorOnSubmit() {
            submitFailedMessage.classList.remove('hide');
            EDC.forms.enableSubmit(submitFailedMessage.closest('form').querySelector('button:not(.hide)[disabled]'));
        }

        function _mapSuppliertype(supType) {
            switch (supType) {
                case 'CAN':
                    return canValue;
                case 'USA':
                    return usaValue;
                case 'INT':
                    return intValue;
                default:
                    return '';
            }
        }

        function _pagination(e) {
            var pageField = element.querySelectorAll('.level-' + currentLevel + ' .form-group:not(.hide) .form-control, .level-' + currentLevel + ' .form-row:not(.hide) .form-control, input[type=hidden]'),
                errorFields = [],
                submitBtn = form.querySelector('button[type="submit"]:not(.hide)'),
                action = e.type === 'submit' ? submitBtn.getAttribute('data-action') : e.target.getAttribute('data-action'),
                supplierTypeChecked = element.querySelector('.supplier-information input[type="radio"]:checked'),
                currentLevelEl = element.querySelector('.level.show'),
                errorInputs,
                hasError = false,
                fieldsToDiscard = form.querySelectorAll('input.discard-input'),
                fileUploaderFilesToRemove = form.querySelectorAll('.c-file-uploader li:not(.valid-file)'),
                fileUploaderFilesToKeep = form.querySelectorAll('.c-file-uploader li.valid-file'),
                initialLevelNumber = currentLevel,
                formData;

            supplierType = supplierTypeChecked ? _mapSuppliertype(supplierTypeChecked.value) : null;
            e.preventDefault();

            fieldsToDiscard.forEach(function (input) {
                input.classList.remove('error');
            });

            if (action === 'back') {
                _trackEvent(action);
                _paginationBack(currentLevelEl);
                submitFailedMessage.classList.add('hide');
            } else {
                EDC.forms.validateForm(pageField);
                errorInputs = currentLevelEl.querySelectorAll('.form-group:not(.hide) .form-control.error, .form-group:not(.hide) .radio-group.error, input.error:not(.discard-input)');
                if (errorInputs.length > 0) {
                    errorInputs.forEach(function (input) {
                        if (!input.closest('.hide')) {
                            hasError = true;
                        }
                        return hasError;
                    });
                }
                if (!hasError && serviceURL) {
                    if (action === 'done' && submitBtn) {
                        EDC.forms.disableSubmit(submitBtn);
                        formData = new FormData(form);
                        EDC.utils.fetchJSON('POST', serviceURL, formData, function (data) {
                            if (data) {
                                if (data.ErrorMessage && submitFailedMessage) {
                                    submitFailedMessage.querySelector('p').innerHTML = data.ErrorMessage;
                                    _showErrorOnSubmit();
                                } else if (data.ConfirmationNumber) {
                                    trackingNumber.textContent = data.ConfirmationNumber;
                                    form.classList.add('hide');
                                    successMessage.classList.remove('hide');
                                    EDC.utils.scrollTo('top', EDC.utils.getPosition(element).y);
                                    _trackEvent(action);
                                } else if (submitFailedMessage) {
                                    _showErrorOnSubmit();
                                }
                            }
                        }, function () {
                            // Error on server communication, delay or related issues
                            if (submitFailedMessage) {
                                submitFailedMessage.classList.remove('hide');
                                EDC.forms.enableSubmit(submitBtn);
                            }
                        }, true);
                    } else if (action === 'next') {
                        _trackEvent(action);
                        if (currentLevel < pageLevel) {
                            _showHideLevels(currentLevel, (currentLevel + 1));

                            if (element.querySelector('.back').classList.contains('hide')) {
                                EDC.forms.toggleFormFields(element.querySelectorAll('.back'), 'display');
                                EDC.forms.toggleFormFields(element.querySelectorAll('.privacy-policy'), 'hide-all');
                            }

                            currentLevel++;
                            EDC.forms.setStatusBar(statusBar, currentLevel, pageLevel);

                            if (currentLevel === pageLevel) {
                                EDC.forms.toggleFormFields(element.querySelectorAll('.done-btn'), 'display');
                                EDC.forms.toggleFormFields(element.querySelectorAll('.next-btn'), 'display');
                            }

                            element.querySelectorAll('.level-' + currentLevel + ' .form-group .form-control.error').forEach(function (input) {
                                input.classList.remove('error');
                            });
                        }
                    }
                } else {
                    element.querySelectorAll('.level-' + currentLevel + ' .form-group:not(.hide) .form-control.error, .level-' + currentLevel + ' .radio-group.error .form-control').forEach(function (field) {
                        errorFields.push(field);
                    });

                    errorFields.some(function (field) { // NOSONAR
                        var validField = !field.closest('.hide');

                        if (validField) {
                            if (field.tagName.toLowerCase() === 'input' || field.classList.contains('dropdown')) {
                                field.focus();
                            } else if (field.querySelector('input:not(.hide)')) {
                                field.querySelector('input:not(.hide)').focus();
                            } else if (field.querySelector('button')) {
                                field.querySelector('button').focus();
                            }
                        }

                        return validField;
                    });
                }
            }

            if (initialLevelNumber !== currentLevel) {
                fileUploaderFilesToRemove.forEach(function (file) {
                    var btn = file.querySelector('button');

                    if (btn) {
                        btn.click();
                    }
                });

                fileUploaderFilesToKeep.forEach(function (file) {
                    var error = file.querySelector('.li-error');

                    if (error) {
                        error.parentNode.removeChild(error);
                    }
                });
            }
        }

        function _identifierRadios(e) {
            var el = e.target,
                parent = el.closest('.form-group'),
                radioGroup = parent.closest('.radio-group'),
                inputs = radioGroup.querySelectorAll('input[type="text"]'),
                thisInput = parent.querySelector('input[type="text"]');

            inputs.forEach(function (input) {
                input.classList.add('disabled');
                input.classList.remove('error');
                input.setAttribute('disabled', 'disabled');
                input.removeAttribute('required');
                input.value = '';
            });
            thisInput.classList.remove('disabled');
            thisInput.removeAttribute('disabled');
            thisInput.setAttribute('required', 'required');
            thisInput.focus();
        }

        function _ulContentChanged(e, multiInputUl, multiInputFieldToSend) {
            var el = e.target;

            if (el && el.nodeName === 'IMG' && multiInputUl.querySelectorAll('li').length === 0) {
                multiInputFieldToSend.value = '';
            }
        }

        function _toggleOtherCheckbox(e) {
            var el = e.target,
                parent = el.closest('.multi-input-section'),
                multiInput = parent.querySelector('.c-multi-input'),
                multiInputField = multiInput.querySelector('.discard-input'),
                multiInputUl = multiInput.querySelector('ul'),
                multiInputFieldToSend = multiInput.querySelector('input.inputs-list');

            if (el.checked) {
                multiInputField.removeAttribute('disabled');
                multiInputField.classList.remove('disabled');
                multiInput.setAttribute('required', 'required');
                EDC.utils.attachEvents(multiInputUl, 'click', function (ev) {
                    _ulContentChanged(ev, multiInputUl, multiInputFieldToSend);
                });
            } else {
                multiInputField.setAttribute('disabled', 'disabled');
                multiInputField.classList.add('disabled');
                multiInputUl.classList.remove('show');
                multiInput.removeAttribute('required');
                multiInput.classList.remove('error');
                multiInputFieldToSend.value = '';
                multiInputUl.innerHTML = '';
            }
        }

        // Level 1 specific functions
        function _otherRadiosClicked() {
            var other = form.querySelector('.other-corporate-status-input');

            if (this.classList.contains('other-radio')) {
                other.removeAttribute('disabled');
                other.setAttribute('required', 'required');
                other.setAttribute('data-min-length', '1');
                other.setAttribute('data-max-length', '100');
            } else {
                other.value = '';
                other.setAttribute('disabled', 'disabled');
                other.removeAttribute('data-min-length');
                other.removeAttribute('data-max-length');
                if (other.getAttribute('required') !== null) {
                    other.removeAttribute('required');
                }
                if (other.classList.contains('error')) {
                    other.classList.remove('error');
                    other.closest('div.form-group').querySelector('span.error').innerHTML = '';
                }
            }
        }

        // Level 2 specific functions
        function _level2RadiosChanged(e) {
            var el = e.target,
                parent = el.closest('.company-identifier'),
                item;

            if (parent) {
                parent.querySelectorAll('input.form-control[type="text"]').forEach(function (input) {
                    input.removeAttribute('validation-rule');
                    input.removeAttribute('data-min-length');
                    input.removeAttribute('data-max-length');
                });
            }

            if (supplierType === canValue) {
                if (el.value === 'canGstHstNumber') {
                    item = parent.querySelector('input[name="' + el.value + '"]');
                    item.setAttribute('validation-rule', 'numbersOnly');
                    item.setAttribute('data-min-length', '9');
                    item.setAttribute('data-max-length', '9');
                } else if (el.value === 'canSinNumber') {
                    item = parent.querySelector('input[name="' + el.value + '"]');
                    item.setAttribute('validation-rule', 'numbersHyphen');
                }
            } else if (supplierType === intValue) {
                item = parent.querySelector('input[name="' + el.value + '"]');
                item.setAttribute('validation-rule', '');
                item.setAttribute('data-min-length', '1');
                item.setAttribute('data-max-length', '30');
            }
        }

        // Level 3 specific functions
        function _sameAddressChecked(e) {
            var checked = e.target.checked,
                address = remitAddressSection.querySelector('.company-address input'),
                address2 = remitAddressSection.querySelector('.company-address2 input'),
                city = remitAddressSection.querySelector('.company-city input'),
                province = remitAddressSection.querySelector('.province input'),
                postal = remitAddressSection.querySelector('.company-postal input'),
                dds = remitAddressSection.querySelectorAll('.ui.dropdown');

            if (checked) {
                remitAddressSection.classList.add('hide');

                remitAddressSection.querySelectorAll('.country .c-dropdown:not(.hide) .menu .item').forEach(function (item) {
                    if (item.getAttribute('data-value') === companyAddressSection.querySelector('.country .c-dropdown:not(.hide) select').value) {
                        item.click();
                    }
                });

                if (supplierType === canValue || supplierType === usaValue) {
                    remitAddressSection.querySelectorAll('.province .c-dropdown:not(.hide) .menu .item').forEach(function (item) {
                        if (item.getAttribute('data-value') === companyAddressSection.querySelector('.province .c-dropdown:not(.hide) select').value) {
                            item.click();
                        }
                    });
                } else {
                    province.value = companyAddressSection.querySelector('.province input').value;
                }

                address.value = companyAddressSection.querySelector('.company-address input').value;
                address2.value = companyAddressSection.querySelector('.company-address2 input').value;
                city.value = companyAddressSection.querySelector('.company-city input').value;
                postal.value = companyAddressSection.querySelector('.company-postal input').value;
            } else {
                address.value = '';
                address2.value = '';
                city.value = '';
                postal.value = '';

                dds.forEach(function (dd) {
                    var items = dd.querySelectorAll('.menu .item.active.selected'),
                        select = dd.querySelector('select'),
                        label = dd.querySelector('.text');

                    items.forEach(function (item) {
                        item.classList.remove('active');
                        item.classList.remove('selected');
                        label.innerHTML = select.getAttribute('data-default-value');
                        label.classList.add('default');
                        select.querySelector('option').click();
                    });
                });

                remitAddressSection.classList.remove('hide');
            }
        }

        function _diversitySupplierRadiosChanged(e) {
            var el = e.target,
                yesSection = diverseSupplierSection.querySelector('.certified-diverse-supplier-yes'),
                noSection = diverseSupplierSection.querySelector('.certified-diverse-supplier-no');

            if (yesSection && el.value === 'Yes') {
                noSection.classList.add('hide');
                yesSection.classList.remove('hide');
            } else if (noSection && el.value === 'No') {
                yesSection.classList.add('hide');
                noSection.classList.remove('hide');
            }
        }

        function _diverseSupplierYesInputs(e) {
            var el = e.target,
                parent = el.closest('.form-group'),
                allCheckboxes = parent.querySelectorAll('input[type="checkbox"]');

            if (el.checked || parent.querySelector('input[type="checkbox"]:checked')) {
                allCheckboxes.forEach(function (input) {
                    input.classList.remove('error');
                });
            } else {
                allCheckboxes.forEach(function (input) {
                    input.classList.add('error');
                });
            }
        }

        function _APIunavailable() {
            var supplierInformationInputs = element.querySelectorAll('input[name="supplierInformation"]').length,
                corporateStatusInputs = element.querySelectorAll('input[name="corporateStatus"]').length,
                businessServicesInputs = element.querySelectorAll('input[name="businessServices"]').length,
                supplierTypeInputs = element.querySelectorAll('input[name="supplierType"]').length,
                cerifiedDiverseSupplierYesInputs = element.querySelectorAll('input[name="cerifiedDiverseSupplierYes"]').length;

            if (messageBanner && (supplierInformationInputs === 0 || corporateStatusInputs === 0 || businessServicesInputs === 0 || supplierTypeInputs === 0 || cerifiedDiverseSupplierYesInputs === 0)) {
                messageBanner.classList.remove('hide');
            }
        }

        function _setOtherCheckboxes() {
            otherCheckboxesTimer = setInterval(function () {
                otherCorporateStatusCheckbox = element.querySelector('input[name="corporateStatus"][value="OTH"]');
                otherBusinessServicesCheckbox = element.querySelector('input[name="businessServices"][value="OTH"]');
                otherDiverseSupplierCheckbox = diverseSupplierSection.querySelector('.certified-diverse-supplier-yes input[name="cerifiedDiverseSupplierYes"][value="OTH"]');
                if (otherBusinessServicesCheckbox !== null && otherCorporateStatusCheckbox !== null && otherDiverseSupplierCheckbox !== null) {
                    clearInterval(otherCheckboxesTimer);
                    otherCorporateStatusCheckbox.classList.add('other-radio');
                    EDC.utils.attachEvents(otherBusinessServicesCheckbox, 'click', _toggleOtherCheckbox);
                    EDC.utils.attachEvents(otherDiverseSupplierCheckbox, 'click', _toggleOtherCheckbox);
                }
            }, 500);
            setTimeout(function () {
                clearInterval(otherCheckboxesTimer);
            }, 5000);
        }

        function _countryDropdownState() {
            var countryDropdown = countryField.querySelector('.ui.dropdown');

            if (supplierType === canValue || supplierType === usaValue) {
                countryDropdown.classList.add('disabled');
            } else if (supplierType === intValue) {
                countryDropdown.classList.remove('disabled');
            }
        }

        // Attach events
        function _attachEvents() {
            var ctas = form.querySelectorAll('.cta'),
                country = companyAddressSection.querySelector('.country select'),
                provinceSelect = companyAddressSection.querySelector('.province .can select'),
                stateSelect = companyAddressSection.querySelector('.province .usa select'),
                provinceInput = companyAddressSection.querySelector('.province input'),
                provinceLabel = companyAddressSection.querySelector('.province > label'),
                remitCountry = remitAddressSection.querySelector('.country select'),
                remitProvinceSelect = remitAddressSection.querySelector('.province .can select'),
                remitStateSelect = remitAddressSection.querySelector('.province .usa select'),
                remitProvinceInput = remitAddressSection.querySelector('.province input'),
                remitProvinceLabel = remitAddressSection.querySelector('.province > label'),
                otherRadios = form.querySelectorAll('.other-radio-section input[type="radio"]'),
                sameAddressCheckbox = form.querySelector('.same-address input'),
                identifierRadios = form.querySelectorAll('.company-identifier input[type="radio"], .account-number input[type="radio"]'),
                diversitySupplierRadios = diverseSupplierSection.querySelectorAll('.certified-diverse-supplier input[type="radio"]'),
                diverseSupplierYesInputs = diverseSupplierSection.querySelectorAll('.certified-diverse-supplier-yes input[type="checkbox"]');

            EDC.utils.attachEvents(ctas, 'click', _pagination);
            EDC.utils.attachEvents(form, 'submit', _pagination);
            EDC.utils.attachEvents(otherRadios, 'click', _otherRadiosClicked);
            EDC.utils.attachEvents(diverseSupplierYesInputs, 'click', _diverseSupplierYesInputs);
            EDC.utils.attachEvents(sameAddressCheckbox, 'click', _sameAddressChecked);
            _setOtherCheckboxes();
            EDC.utils.attachEvents(country, 'change', function () {
                EDC.forms.changeProvince(country.value, provinceSelect, stateSelect, provinceInput, provinceLabel);
            });

            EDC.utils.attachEvents(remitCountry, 'change', function () {
                EDC.forms.changeProvince(remitCountry.value, remitProvinceSelect, remitStateSelect, remitProvinceInput, remitProvinceLabel);
            });

            EDC.utils.attachEvents(identifierRadios, 'change', _identifierRadios);
            EDC.utils.attachEvents(diversitySupplierRadios, 'change', _diversitySupplierRadiosChanged);
            EDC.utils.attachEvents(level2.querySelectorAll('.company-identifier [type="radio"]'), 'change', _level2RadiosChanged);
            EDC.forms.validateChange(formElements);
            EDC.utils.attachEvents(window, 'resize', _countryDropdownState);
        }

        _attachEvents();
        telsToMask.forEach(function (telToMask) {
            EDC.forms.applyHyphenMask(element, telToMask);
        });

        _APIunavailable();
    }

    // Public methods
    function init() {
        var supplierRegistrationForm = document.querySelectorAll('.c-supplier-registration-form:not([data-component-state="initialized"])');

        if (supplierRegistrationForm) {
            supplierRegistrationForm.forEach(function (elem) {
                _initialize(elem);
                elem.setAttribute('data-component-state', 'initialized');
            });
        }
    }

    return {
        init: init
    };
})();

document.addEventListener('DOMContentLoaded', supplierRegistrationFormJS.init);