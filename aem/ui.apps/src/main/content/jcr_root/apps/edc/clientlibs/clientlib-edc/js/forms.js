window.EDC = window.EDC || {};

EDC.forms = EDC.forms || new function () {
    'use strict';

    /* Form Utilities: Form Validation / Form Submission to Eloqua or AEM */

    // Helper functions to set up hidden fields when country is selected
    this.showProvinceState = function (select, input, label, fieldName) {
        var dropdownElement,
            dropdownParent,
            inputParent = input.closest('.form-group'),
            selectId = select ? select.getAttribute('id') : 'companyProvince',
            inputAltId = input.getAttribute('alt-id') ? input.getAttribute('alt-id') : input.getAttribute('id');

        select.setAttribute('required', true);
        select.name = fieldName ? fieldName : 'companyProvince';
        select.id = selectId;

        if (select.parentElement.classList.contains('dropdown')) {
            dropdownElement = select.closest('.c-dropdown');
            dropdownParent = dropdownElement.closest('.form-group');
            dropdownElement.classList.remove('hide');
            dropdownElement.querySelector('label').setAttribute('for', 'companyProvince');

            if (dropdownParent.classList.contains('myedc-province')) {
                dropdownParent.classList.remove('hide');
            }
        } else {
            select.classList.remove('hide');
        }

        input.classList.add('hide');
        input.name = 'companyProvinceInput';
        input.removeAttribute('required');
        input.removeAttribute('alt-id');
        input.setAttribute('id', inputAltId);
        input.value = '';

        if (inputParent.classList.contains('myedc-province')) {
            inputParent.classList.add('hide');
        }

        if (label) {
            label.classList.add('hide');
            label.setAttribute('for', inputAltId);
        }
    };

    this.hideProvinceState = function (select) {
        var dropdownElement,
            dropdownParent,
            selectId = select ? select.getAttribute('id') : 'companyProvince-alt';

        select.removeAttribute('required');
        select.name = 'companyProvince-alt';
        select.id = selectId;
        select.selectedIndex = 0;

        if (select.parentElement.classList.contains('dropdown')) {
            dropdownElement = select.closest('.c-dropdown');
            dropdownParent = dropdownElement.closest('.form-group');
            dropdownElement.classList.add('hide');
            dropdownElement.querySelector('.text').innerHTML = select.getAttribute('data-default-value');
            dropdownElement.querySelector('.text').classList.add('default');
            dropdownElement.querySelector('label').setAttribute('for', selectId);

            if (dropdownParent.classList.contains('myedc-province')) {
                dropdownParent.classList.add('hide');
            }

            dropdownElement.querySelectorAll('.item').forEach(function (item) {
                item.classList.remove('active');
                item.classList.remove('selected');
            });
        } else {
            select.classList.add('hide');
        }
    };

    this.changePostalCodeLabelText = function (input, country) {
        var label = input.parentElement.querySelector('label'),
            textUsa = label.getAttribute('data-usa-label'),
            textDefault = label.getAttribute('data-default-label'),
            textCan = label.getAttribute('data-can-label');

        if (textUsa && textDefault) {
            if (country === 'usa') {
                label.textContent = textUsa;
            } else if (country === 'can' && textCan) {
                label.textContent = textCan;
            } else {
                label.textContent = textDefault;
            }
        }
    };

    // Helper functions to select province field
    this.changeProvince = function (value, provinceSelect, stateSelect, provinceInput, provinceLabel, postalIsLoaded) {
        var country = value ? value.toLowerCase().trim() : '',
            provinceInputParent = provinceInput.closest('.form-group'),
            mainId = provinceSelect ? provinceSelect.getAttribute('id') : 'companyProvince',
            provinceInputName = stateSelect.closest('.form-group').getAttribute('data-form-field') === 'remitCompanyProvince' ? 'remitCompanyProvince' : 'companyProvince',
            postalInsideAddressSection = provinceSelect.closest('.address-section') ?
                provinceSelect.closest('.address-section').querySelector('[validation-rule="postalCode"]') :
                provinceSelect.closest('form').querySelector('[validation-rule="postalCode"]'),
            postalCodeInput = provinceSelect ? postalInsideAddressSection : null;

        function _handlePostalCode(c) {
            if (postalCodeInput) {
                EDC.forms.changePostalCodeLabelText(postalCodeInput, c);
                postalCodeInput.setAttribute('required', 'required');
            }
        }

        function _toggleFields() {
            if (country === 'canada' || country === 'can') {
                EDC.forms.showProvinceState(provinceSelect, provinceInput, provinceLabel, provinceInputName);
                EDC.forms.hideProvinceState(stateSelect);
                _handlePostalCode('can');
            } else if (country === 'united states' || country === 'états-unis' || country === 'usa' || country === 'us') {
                EDC.forms.showProvinceState(stateSelect, provinceInput, provinceLabel, provinceInputName);
                EDC.forms.hideProvinceState(provinceSelect);
                _handlePostalCode('usa');
            } else {
                EDC.forms.hideProvinceState(provinceSelect);
                EDC.forms.hideProvinceState(stateSelect);
                provinceInput.classList.remove('hide');
                provinceInput.name = provinceInputName;
                provinceInput.setAttribute('alt-id', provinceInput.getAttribute('id'));
                provinceInput.setAttribute('id', mainId);

                if (provinceInputParent.classList.contains('myedc-province')) {
                    provinceInputParent.classList.remove('hide');
                }

                if (provinceLabel) {
                    provinceLabel.setAttribute('for', mainId);
                    provinceLabel.classList.remove('hide');
                    postalCodeInput.removeAttribute('required');
                }

                if (postalCodeInput) {
                    EDC.forms.changePostalCodeLabelText(postalCodeInput, 'other');
                }
            }
        }

        if (provinceSelect && provinceInput && stateSelect) {
            provinceSelect.classList.remove('error');
            stateSelect.classList.remove('error');
            provinceInput.classList.remove('error');

            if (value) {
                _toggleFields();

                if (postalCodeInput && !postalIsLoaded) {
                    postalCodeInput.value = '';
                    postalCodeInput.classList.remove('error');
                }
            }
        }
    };

    this.fillHiddenFields = function (el) {
        var refPage = el.querySelector('input[type="hidden"][name="refPage"]'),
            fromPage = el.querySelector('input[type="hidden"][name="fromPage"]'),
            fullFromPage = el.querySelector('input[type="hidden"][name="fullFromPage"]'),
            refPremiumURL = EDC.utils.getURLParams().refPremiumURL,
            referrer = document.referrer,
            urlParams,
            key,
            paramsArray = [];

        if (refPage) {
            if (refPremiumURL) {
                urlParams = EDC.utils.getURLParams();

                for (key in urlParams) {
                    if (urlParams.hasOwnProperty(key)) {
                        if (urlParams[key] !== refPremiumURL) {
                            paramsArray.push(key + '=' + urlParams[key]);
                        }
                    }
                }
                refPage.value = window.origin + refPremiumURL + '?' + paramsArray.join('&');
            } else {
                refPage.value = referrer;
            }
        }

        if (fromPage) {
            fromPage.value = EDC.utils.getCookie('trafficsrc') || null;
        }

        if (fullFromPage) {
            fullFromPage.value = window.location.href;
        }
    };

    // Changes validation min and max depending on the type of text input
    this.changeMinAndMax = function (valRule, country) {
        switch (valRule) {
            case 'phoneExt': // 0 - 10
                return {
                    max: 10
                };
            case 'mainPhone': // 10 - 11
            case 'specialPhone':
                if (country === 'can' || country === 'us') {
                    return {
                        min: 10
                    };
                }
                return false;
            case 'address1': // 1 - 50
                return {
                    min: 1
                };
            case 'name': // 2 - 50
                return {
                    min: 2
                };
            case 'companyName':
            case 'legalName': // 1 - 120
                return {
                    min: 1,
                    max: 120
                };
            case 'title': // 0 - 120
                return {
                    max: 120
                };
            case 'province': // 0 - 30
            case 'postalCode': // 0 - 30
                return {
                    max: 30
                };
        }

        return false;
    };

    this.applyHyphenMask = function (el, field) {
        var mask = field.dataset.mask,
            spaceChar = '_';

        // For now, this just strips everything that's not a number
        function stripMask(maskedData) {
            function isDigit(char) {
                return /\d/.test(char);
            }
            return maskedData.split('').filter(isDigit);
        }

        // Replace `_` characters with characters from `data`
        function applyMask(data) {
            return mask.split('').map(function (char) {
                if (char !== spaceChar) {
                    return char;
                }
                if (data.length === 0) {
                    return char;
                }
                return data.shift();
            }).join('');
        }

        function reapplyMask(data) {
            return applyMask(stripMask(data));
        }

        function moveSelection(oldStart, fieldValue, eType) {
            var newPos,
                firstSpacePos = fieldValue.indexOf(spaceChar),
                thisChar,
                isDigit;

            firstSpacePos = firstSpacePos && firstSpacePos >= 0 ? firstSpacePos : oldStart;
            thisChar = fieldValue[firstSpacePos - 1];
            isDigit = /\d/.test(thisChar);

            if (eType) {
                if (eType === 'insertText') {
                    newPos = firstSpacePos;
                } else if (eType === 'deleteContentBackward') {
                    newPos = thisChar ? (isDigit ? firstSpacePos : firstSpacePos - 1) : 0;
                } else if (eType === 'deleteContentForward') {
                    newPos = oldStart;
                }
            }

            return firstSpacePos >= 1 && thisChar !== spaceChar ? newPos : 0;
        }

        function changed(e) {
            var oldStart = field.selectionStart,
                newPos;

            field.value = reapplyMask(field.value);
            newPos = moveSelection(oldStart, field.value, e.inputType);
            field.selectionStart = newPos;
            field.selectionEnd = newPos;
        }

        function cursorOnStart() {
            var pos = 1,
                i;

            for (i = field.value.length; i > 0; i--) {
                if (Number(field.value[i])) {
                    pos = i + 1;
                    break;
                }
            }

            field.selectionStart = pos;
            field.selectionEnd = pos;
        }

        function _attachMaskEvents(e) {
            if (e) {
                field.value = '';
            }

            field.addEventListener('input', changed);
            field.addEventListener('click', cursorOnStart);
        }

        if (mask) {
            _attachMaskEvents();
        }
    };

    this.applyDataMask = function (el, field) {
        var mask = field.dataset.mask ? field.dataset.mask.split('') : '(___) ___-____'.split(''),
            lastValue = '';

        // For now, this just strips everything that's not a number
        function stripMask(maskedData) {
            function isDigit(char) {
                return /\d/.test(char);
            }
            return maskedData.split('').filter(isDigit);
        }

        // Replace `_` characters with characters from `data`
        function applyMask(data) {
            if (data[0] === '1') {
                mask = '_ (___) ___-____'.split('');
            } else {
                mask = field.dataset.mask ? field.dataset.mask.split('') : '(___) ___-____'.split('');
            }

            return mask.map(function (char) {
                if (char !== '_') {
                    return char;
                }
                if (data.length === 0) {
                    return char;
                }
                return data.shift();
            }).join('');
        }

        function reapplyMask(data) {
            return applyMask(stripMask(data));
        }

        function moveSelection(textInputed, val, oldStart, newPos) {
            if (textInputed && (Number(val) || val === '0')) {
                if (!Number(field.value[oldStart - 1]) && field.value[oldStart - 1] !== '0') {
                    newPos = oldStart + 1;
                    if (!Number(field.value[newPos - 1]) && field.value[newPos - 1] !== '0') {
                        newPos++;
                    }
                }
            } else if (val === null) {
                if (!Number(field.value[oldStart - 1]) && field.value[oldStart - 1] !== '0') {
                    newPos = oldStart - 1;
                    if (!Number(field.value[newPos - 1]) && field.value[newPos - 1] !== '0') {
                        newPos--;
                    }
                }
            } else {
                newPos--;
            }

            return newPos;
        }

        function replaceValue(v) {
            return v.replace(/[\_\-\(\) ]+/g, '');
        }

        function changed(e) {
            var oldStart = field.selectionStart,
                newPos = field.selectionStart,
                fieldValue = replaceValue(field.value),
                valForIE = replaceValue(e.target.value),
                textInputed = e.inputType && e.inputType === 'insertText' || lastValue < fieldValue,
                val = e.data ? e.data : !textInputed ? null : valForIE[valForIE.length - 1],
                isFirstCharANumber;

            field.value = reapplyMask(field.value);
            isFirstCharANumber = !isNaN(field.value[0]);
            newPos = moveSelection(textInputed, val, oldStart, newPos);
            newPos = newPos <= 0 ? 1 : newPos;
            field.selectionStart = isFirstCharANumber && oldStart <= 2 ? 3 : newPos;
            field.selectionEnd = isFirstCharANumber && oldStart <= 2 ? 3 : newPos;
            lastValue = fieldValue;
        }

        function cursorOnStart() {
            var pos = 1,
                i;

            for (i = field.value.length; i > 0; i--) {
                if (Number(field.value[i])) {
                    pos = i + 1;
                    break;
                }
            }

            field.selectionStart = pos;
            field.selectionEnd = pos;
        }

        function _attachMaskEvents(e) {
            var val = e ? e.target.value.toLowerCase() : el.querySelector('.country select option:checked').value.toLowerCase();

            if (e) {
                field.value = '';
            }
            if (val === 'can' || val === 'usa') {
                field.addEventListener('input', changed);
                field.addEventListener('click', cursorOnStart);
            } else {
                field.removeEventListener('input', changed);
                field.removeEventListener('click', cursorOnStart);
            }
        }

        EDC.utils.attachEvents(el.querySelector('.country select'), 'change', _attachMaskEvents);
        _attachMaskEvents();
    };

    // Add/Remove length-error attribute
    this.lengthErrorAttr = function (el, value, min, max) {
        if (value.length < min) {
            el.setAttribute('length-error', 'min');
        } else if (value.length > max) {
            el.setAttribute('length-error', 'max');
        } else {
            el.removeAttribute('length-error');
        }
    };

    // Validation code begins
    this.validateFieldLength = function (el, country) {
        var value = el.value,
            validationRule = el ? el.getAttribute('validation-rule') : null,
            elType = el.type,
            max = 50,
            min = null,
            dataMaxlength = el.getAttribute('data-max-length'),
            dataMinlength = el.getAttribute('data-min-length'),
            hiddenInput = el.type === 'hidden' && !dataMaxlength && !dataMinlength,
            minAndMax;

        if (validationRule) {
            minAndMax = this.changeMinAndMax(validationRule, country);
            if (minAndMax) {
                min = minAndMax.min ? minAndMax.min : min;
                max = minAndMax.max ? minAndMax.max : max;
            }
        } else if (elType === 'checkbox') {
            validationRule = 'none';
        } else if (elType === 'email') {
            max = 100;
        } else if (elType === 'tel') {
            max = 40;
        }

        max = dataMaxlength ? Number(dataMaxlength) : max;
        min = dataMinlength ? Number(dataMinlength) : !el.getAttribute('required') && value === '' ? null : min;

        if (!hiddenInput) {
            this.lengthErrorAttr(el, value, min, max);
        }

        return validationRule === 'none' || hiddenInput ? true : min ? value.length >= min && value.length <= max : value.length <= max;
    };

    this.validationRules = {
        required: function (el, country) {
            var validValue = false,
                parent,
                checkboxCounter = 0;

            if (el.type === 'checkbox') {
                parent = el.closest('[data-special-validation="oneCheckboxAtLeast"]');
                if (parent) {
                    parent.querySelectorAll('input[type="checkbox"]').forEach(function (checkbox) {
                        if (checkboxCounter === 0 && checkbox.checked) {
                            validValue = checkbox.checked;
                            checkboxCounter++;
                        }
                    });
                } else {
                    validValue = el.checked;
                }
            } else if (el.type === 'radio') {
                el.closest('.radio-group').querySelectorAll('input').forEach(function (radio) {
                    if (radio.checked) {
                        validValue = true;
                    }
                });
            } else {
                validValue = el.value.trim() !== '' || (country === 'other' && el.closest('.form-group.province')) || el.getAttribute('selected');
            }

            return validValue;
        },
        // eslint-disable-next-line complexity
        text: function (value, el, country) {
            var validValue = true,
                // Validates Only letters, dashes, commas, apostrophes, spaces, periods, accented letters
                regexText = /^([a-zA-Z\u00C0-\u017F\s ,.'"´_-]?)+$/m,
                regexTextAndNumbers = /^([a-zA-Z0-9\u00C0-\u017F\s ,.'"´_-]?)+$/m,
                regexNumbersOnly = /^([0-9]?)+$/im,
                regexNumbersDecimals = /^\s*(?=.*[1-9])\d*(?:[.,]\d{2})+\s*$/im,
                regexNumbersHyphen = /^([0-9-]?)+$/im,
                regexPostalCode = /^[a-zA-Z0-9- ]+$/m,
                regexCanPostCode = /^[A-Za-z]\d[A-Za-z][ -]?\d[A-Za-z]\d$/m,
                regesUSPostCode = /^[0-9]{5}(?:-[0-9]{4})?$/,
                regexSpecialAddress = /^(?=.*[0-9])(?=.*[a-zA-ZàâéêèëìïîôùûçæœÀÂÉÊÈËÌÏÎÔÙÛÇÆŒ])([a-zA-ZàâéêèëìïîôùûçæœÀÂÉÊÈËÌÏÎÔÙÛÇÆŒ0-9 ,.'"´_-]+)$/,
                validationRule = el.getAttribute('validation-rule');

            if (value !== '') {
                switch (validationRule) {
                    case null:
                    case 'city':
                    case 'name':
                    case 'title':
                    case 'province':
                    case 'bank':
                    case 'companyName':
                        validValue = regexText.test(value);
                        break;
                    case 'postalCode':
                        if (country === 'can') {
                            validValue = regexCanPostCode.test(value);
                        } else if (country === 'us') {
                            validValue = regesUSPostCode.test(value);
                        } else {
                            validValue = regexPostalCode.test(value);
                        }
                        break;
                    case 'postalCodeCan':
                        validValue = regexCanPostCode.test(value);
                        break;
                    case 'specialAddress':
                        if (country === 'can' || country === 'us') {
                            validValue = regexTextAndNumbers.test(value) && regexSpecialAddress.test(value);
                            if (!regexSpecialAddress.test(value) && !regexTextAndNumbers.test(value)) {
                                el.removeAttribute('pobox-error');
                            } else if (!regexSpecialAddress.test(value)) {
                                el.setAttribute('pobox-error', 'true');
                            } else {
                                el.removeAttribute('pobox-error');
                            }
                        }
                        break;
                    case 'numbersOnly':
                        validValue = regexNumbersOnly.test(value);
                        break;
                    case 'numbersDecimal':
                        validValue = regexNumbersDecimals.test(value);
                        break;
                    case 'numbersHyphen':
                        validValue = regexNumbersHyphen.test(value);
                        break;
                }
            }

            return validValue;
        },
        tel: function (value, el) {
            var validValue = true,
                // Validates Only numbers, periods, dashes, brackets, plus sign, space, letters allowed
                regexPhone = /^([\+]?[\(]?[\{]?[0-9a-zA-Z]+[\)]?[\}]?[\-\{\}\(\)\.\s]?)+$/im,
                // Validates Only numbers, periods, dashes, brackets, plus sign, space allowed
                regexPhoneNumbersAndCharacters = /^([\+]?[\(]?[\{]?[0-9]+[\)]?[\}]?[\-\{\}\(\)\.\s]?)+$/im,
                // Validates Only numbers
                regexPhoneNumbersOnly = /^([0-9]?)+$/im,
                validationRule = el.getAttribute('validation-rule');

            if (value !== '') {
                if (validationRule && (validationRule === 'mainPhone' || validationRule === 'phoneExt')) {
                    validValue = regexPhoneNumbersAndCharacters.test(value);
                } else if (validationRule && validationRule === 'numbersOnly') {
                    validValue = regexPhoneNumbersOnly.test(value) && value[0] !== '0';
                } else {
                    validValue = regexPhone.test(value);
                }
            }

            return validValue;
        },
        email: function (value) {
            var regexEmail = /^[^\.]([a-zA-Z0-9_+\-\.]+)[^\.]@([a-zA-Z0-9_\-\.\[\]]+)\.([a-zA-Z0-9\[\]]{2,6})$/;

            return regexEmail.test(value) && value.indexOf(' ') === -1 && value.indexOf('..') === -1 && value.indexOf('@-') === -1;
        },
        noURL: function (value) {
            var url = /([a-zA-Z]{2,}:\/{2})?(www\.|WWW\.)?[a-zA-z0-9]{2,}\.([a-zA-Z]{2,3}\b)/,
                ip = /(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/;

            return url.test(value) || ip.test(value);
        },
        hidden: function (value, el) {
            var validValue = true;

            if (value && el.name === 'lang') {
                validValue = (el.value === 'English' || el.value === 'French');
            }

            return validValue;
        },
        bankEmail: function (value, bankEmails, el) {
            var domains = bankEmails.split('::'),
                test = 0,
                errorMsg = '';

            domains.forEach(function (domain, index) {
                var pos = value.toLowerCase().indexOf(domain.toLowerCase());

                if (pos > -1 && (pos + domain.length) === value.length) {
                    test++;
                }

                if (domains[index + 1] && index + 1 === domains.length - 1) {
                    errorMsg += domain + ' ';
                } else if (domains[index + 1]) {
                    errorMsg += domain + ', ';
                } else if (domains.length > 1) {
                    errorMsg += '& ' + domain;
                } else {
                    errorMsg += domain;
                }
            });

            if (el && errorMsg) {
                el.setAttribute('data-bank-error-domain', errorMsg);
            }

            return test > 0;
        }
    };

    this.getValidationCountry = function (el) {
        var closestForm,
            closestFormSelect,
            countrySelect,
            countryValue,
            value;

        closestForm = el.closest('.address-section') ? el.closest('.address-section') : el.closest('form');
        if (closestForm) {
            closestFormSelect = closestForm.querySelector('[data-form-field="companyCountry"] select');
            countrySelect = closestFormSelect ? closestFormSelect : closestForm.querySelector('.country:not([data-form-field="marketsExport"])');
            if (countrySelect) {
                countryValue = countrySelect.value.toLowerCase().trim();
                if (countryValue) {
                    switch (countryValue) {
                        case 'canada':
                        case 'can':
                            value = 'can';
                            break;

                        case 'us':
                        case 'usa':
                        case 'united states':
                        case 'états-unis':
                            value = 'us';
                            break;

                        default:
                            value = 'other';
                            break;
                    }
                } else {
                    value = null;
                }
            }
        }

        return value;
    };

    this.isValidationNeeded = function (type, el, country) {
        var value;

        if (type === 'bankemail') {
            value = el.getAttribute('bank-email') === null ? true : EDC.forms.validationRules.bankEmail(el.value, el.getAttribute('bank-email'), el);
        } else if (type === 'required') {
            value = el.getAttribute('required') === null ? true : EDC.forms.validationRules.required(el, country);
        } else if (type === 'data-no-url') {
            value = el.getAttribute('data-no-url') === null ? false : EDC.forms.validationRules.noURL(el.value);
        }

        return value;
    };

    this.specialErrorMessages = function (validationRule, el, country, error, type) {
        if (validationRule && validationRule === 'postalCode') {
            switch (country) {
                case 'us':
                case 'can':
                    error.innerHTML = error.getAttribute('data-' + type + '-message');

                    break;
                case 'other':
                    error.innerHTML = error.getAttribute('data-' + type + '-secondary-message');
                    break;
            }
        } else if (validationRule && validationRule === 'specialAddress') {
            switch (country) {
                case 'us':
                case 'can':
                    if (el.getAttribute('pobox-error') !== 'true') {
                        error.innerHTML = error.getAttribute('data-' + type + '-message');
                    } else {
                        error.innerHTML = error.getAttribute('data-pobox-message');
                    }

                    break;
                case 'other':
                    error.innerHTML = error.getAttribute('data-' + type + '-secondary-message');
                    break;
            }
        } else {
            error.innerHTML = error.getAttribute('data-' + type + '-message');
        }
    };

    this.prepareError = function (el, country, requiredValidation, noURLValidation, bankemailVal, lengthValidation, validRules, error, type) {
        var validationRule = el.getAttribute('validation-rule');

        if (!requiredValidation && error.getAttribute('data-req-message')) {
            error.innerHTML = error.getAttribute('data-req-message');
        } else if (noURLValidation && error.getAttribute('data-no-url-message')) {
            error.innerHTML = error.getAttribute('data-no-url-message');
        } else if (error.getAttribute('data-checkbox-message')) {
            error.innerHTML = error.getAttribute('data-checkbox-message');
        } else if (error.getAttribute('data-special-message')) {
            error.innerHTML = error.getAttribute('data-special-message');
        } else if (validRules && error.getAttribute('data-' + type + '-message')) {
            this.specialErrorMessages(validationRule, el, country, error, type);
        } else if (!bankemailVal) {
            error.innerHTML = error.getAttribute('data-bank-email-message') + (el.getAttribute('data-bank-error-domain') ? ' ' + el.getAttribute('data-bank-error-domain') : '');
        } else if (!lengthValidation && error.getAttribute('data-length-max-message') && error.getAttribute('data-length-min-message')) {
            error.innerHTML = error.getAttribute('data-length-' + el.getAttribute('length-error') + '-message');
        }
    };

    this.validateInputs = function (e) {
        var el = e.target || e,
            type = el.getAttribute('validation-rule') && el.type === 'textarea' ? 'text' : el.type,
            value = el.value,
            parent = el.parentElement,
            country = EDC.forms.getValidationCountry(el),
            requiredValidation = EDC.forms.isValidationNeeded('required', el, country),
            noURLValidation = EDC.forms.isValidationNeeded('data-no-url', el),
            lengthValidation = EDC.forms.validateFieldLength(el, country),
            bankemailVal = EDC.forms.isValidationNeeded('bankemail', el),
            isDropdown = parent.classList.contains('ui') && parent.classList.contains('dropdown'),
            field = isDropdown ? parent : el.closest('.radio-group') && type === 'radio' ? el.closest('.radio-group') : el,
            error = type !== 'hidden' ? isDropdown || type === 'radio' ? field.parentElement.querySelector('span.error') :
                field.closest('div:not(.dropdown-container)').querySelector('span.error:not(.dropdown-error)') : '',
            validRules = EDC.forms.validationRules[type] && !EDC.forms.validationRules[type](value, el, country);

        if (!requiredValidation || !lengthValidation || validRules || noURLValidation || !bankemailVal) {
            field.classList.add('error');

            if (error) {
                this.prepareError(el, country, requiredValidation, noURLValidation, bankemailVal, lengthValidation, validRules, error, type);
            }
        } else {
            field.classList.remove('error');
        }
    };

    // Helper function to Validate Error states for Form Fields
    this.validateForm = function (el) {
        var multiSelect = el[0].closest('form').querySelector('.c-multi-select'),
            multiCheckboxes = el[0].closest('form').querySelector('.level.show [data-special-validation="oneCheckboxAtLeast"]'),
            multiInput = el[0].closest('form').querySelector('.level.show .c-multi-input'),
            fileUploader = el[0].closest('form').querySelector('.level.show .c-file-uploader'),
            fileUploaderTotalValidFiles = 0,
            errorMsg;

        el.forEach(function (elem) {
            if (!elem.closest('.c-multi-select') && !elem.closest('.c-multi-input') && !elem.closest('.c-file-uploader')) {
                if (elem.classList.contains('dropdown')) {
                    EDC.forms.validateInputs(elem.querySelector('select'));
                } else {
                    EDC.forms.validateInputs(elem);
                }
            }
        });

        if (multiSelect && multiSelect.getAttribute('required') !== null) {
            if (multiSelect.querySelectorAll('input[type="checkbox"]:checked').length === 0) {
                multiSelect.classList.add('error');
            } else {
                multiSelect.classList.remove('error');
            }
        }

        if (multiCheckboxes && multiCheckboxes.getAttribute('required') !== null) {
            errorMsg = multiCheckboxes.querySelector('span.error');
            if (multiCheckboxes.querySelectorAll('input[type="checkbox"]:checked').length === 0) {
                multiCheckboxes.classList.add('error');
                errorMsg.textContent = errorMsg.getAttribute('data-req-message');
            } else {
                multiCheckboxes.classList.remove('error');
                errorMsg.textContent = '';
            }
        }

        if (multiInput && multiInput.getAttribute('required') !== null) {
            errorMsg = multiInput.querySelector('span.error');
            if (multiInput.querySelector('input.inputs-list').value === '') {
                multiInput.classList.add('error');
                errorMsg.textContent = errorMsg.getAttribute('data-req-message');
            } else {
                multiInput.classList.remove('error');
                errorMsg.textContent = '';
            }
        }

        if (fileUploader && fileUploader.getAttribute('required') !== null) {
            errorMsg = fileUploader.querySelector('span.error');
            fileUploaderTotalValidFiles = fileUploader.querySelectorAll('input[type="file"]:not(.discard-input)').length;
            if (fileUploaderTotalValidFiles === 0) {
                fileUploader.classList.add('error');
                errorMsg.textContent = errorMsg.getAttribute('data-req-message');
            } else if (fileUploader.classList.contains('error')) {
                fileUploader.classList.add('error');
                errorMsg.textContent = errorMsg.getAttribute('data-big-size-file');
            } else {
                fileUploader.classList.remove('error');
                errorMsg.textContent = '';
            }
        }
    };

    // Helper function to Validate fields on blur / change, only when there's data on the field
    this.validateField = function (e) {
        var el = e.target || e;

        if ((el.value !== undefined) || el.classList.contains('email-submit')) {
            EDC.forms.validateInputs(el);
        }
    };

    // Helper function to initialize EDC.utils.validateField function
    this.validateChange = function (formElements) {
        formElements.forEach(function (elem) {
            if (elem.type !== 'hidden') {
                if (elem.type === 'select-one' || elem.type === 'select-multiple') {
                    EDC.utils.attachEvents(elem, 'change', EDC.forms.validateField);
                } else {
                    EDC.utils.attachEvents(elem, 'blur', EDC.forms.validateField);
                }
            }
        });
    };

    // Helper function to Toggle form fields or pages visibility and arrangement see Progressive Profiling or Matchmaking Events
    this.toggleFormFields = function (selector, toggleAction) {
        switch (toggleAction) {
            case 'display':
                selector.forEach(function (item) {
                    if ((item.classList.contains('bottom-ctas')) || (item.classList.contains('back'))) {
                        item.classList.toggle('show-flex');
                    } else if (item.classList.contains('status')) {
                        item.classList.toggle('show-block');
                    } else {
                        item.classList.toggle('show');
                    }
                    item.classList.toggle('hide');
                });
                break;

            case 'hide-all':
                selector.forEach(function (item) {
                    item.classList.add('hide');
                    item.classList.remove('show');
                    item.classList.remove('show-flex');
                    item.classList.remove('show-block');
                });
                break;

            case 'read-only':
                selector.forEach(function (item) {
                    if (item.disabled === true) {
                        item.disabled = false;
                    } else {
                        item.disabled = true;
                    }
                });
                break;
        }
    };

    // Helper function to Set status bar for Multi page forms.
    this.setStatusBar = function (statusBar, statusBarLevel, pageLevel) {
        var statusBarWidth = Math.floor(((statusBarLevel) / pageLevel) * 100);

        statusBar.style.width = statusBarWidth.toString() + '%';
    };

    this.elqQPush = function () {
        if ((typeof _elqQ !== 'undefined') && _elqQ !== undefined && _elqQ !== null) {
            _elqQ.push(['elqGetCustomerGUID']);
        }
    };

    this.elqGetGuidCookieValue = function () {
        var name,
            value,
            index,
            cookies = document.cookie.split(';'),
            i,
            l,
            subCookies,
            subCookie;

        for (i = 0; i < cookies.length; i++) {
            index = cookies[i].indexOf('=');
            if (index > 0 && cookies[i].length > index + 1) {
                name = cookies[i].substr(0, index).trim();
                if (name === 'ELOQUA') {
                    value = cookies[i].substr(index + 1);
                    subCookies = value.split('&');
                    for (l = 0; l < subCookies.length; l++) {
                        subCookie = subCookies[l].split('=');
                        if (subCookie.length === 2 && subCookie[0] === 'GUID') {
                            return subCookie[1];
                        }
                    }
                }
            }
        }
        return '';
    };

    // Helper function to call Eloqua Cookies
    this.WaitUntilCustomerGUIDIsRetrieved = function (elem) {
        var guidField,
            guidValue = '',
            timer,
            counter = 0,
            maxTries = 5,
            intervalSeconds = 1000;

        if (elem) {
            guidField = elem.querySelector('[type="hidden"][name="elqCustomerGUID"]');
        }

        if (guidField && guidField.value === '') {
            timer = setInterval(function () {
                counter++;

                // Check if the function is available, use it
                if (typeof window.GetElqCustomerGUID === 'function') {
                    guidValue = GetElqCustomerGUID();
                    counter = maxTries;
                }

                // No value yet, no function use cookie
                if (counter === maxTries && guidValue === '') {
                    guidValue = EDC.forms.elqGetGuidCookieValue();
                }

                // If value was successfully retrieved, then clear the interval and apply the value
                if (guidValue) {
                    clearInterval(timer);
                    guidField.value = guidValue;
                    window[EDC.datalayerObj].user.segment.guid = guidValue;
                    _satellite.track('guidCaller');
                }
            }, intervalSeconds);
        }
    };

    // Helper function to set height to form Labels when the there are two fields grouped on the same row
    this.setHeightLabels = function (rows) {
        var labels,
            heights,
            sameHeight = true,
            maxHeight,
            isMobile = window.innerWidth < EDC.props.media.md;

        rows.forEach(function (row) {
            labels = row.querySelectorAll('label');
            heights = [];
            maxHeight = 0;

            if (labels && labels.length >= 2) {
                labels.forEach(function (label, index) {
                    label.removeAttribute('style');
                    heights.push(label.offsetHeight);
                    if (index !== 0 && (heights[index] !== heights[index - 1])) {
                        sameHeight = false;
                    }
                });
            }

            if (!sameHeight && !isMobile) {
                maxHeight = Math.max.apply(null, heights).toString();

                labels.forEach(function (label) {
                    label.style.minHeight = maxHeight + 'px';
                });
            }
        });
    };

    // Helper function to create the string that will be sent to Database on Post.
    this.setHeightLabelsOnLoad = function (formRows) {
        setTimeout(function () {
            EDC.forms.setHeightLabels(formRows);
        }, 1000);
    };

    // Helper function to return the value of normal input types.
    this.getInputValue = function (elem) {
        var value;

        if (elem.name === 'companyName' || elem.name === 'title' || elem.name === 'companyCity' || elem.getAttribute('capitalize')) {
            value = elem.value.charAt(0).toUpperCase() + elem.value.slice(1);
        } else if ((elem.name === 'companyPostal' || elem.name === 'companyProvince') && elem.value === '') {
            value = 'DEFAULT';
        } else {
            value = elem.value;
        }

        return value;
    };

    this.getOptionsSelected = function (elem, separator) {
        var options,
            value = '',
            delimiter = separator ? separator : ';';

        if (elem) {
            if (elem.getAttribute('multiple') !== null) {
                options = elem.querySelectorAll('option');
                options.forEach(function (option) {
                    if (option.selected && option.value !== '') {
                        value += option.value + delimiter;
                    }
                });
                // Removes the last delimiter of the variable
                value = value.substr(0, value.length - 2);
            } else {
                value = EDC.forms.getInputValue(elem);
            }
        }

        return value;
    };

    // Helper function to create the string that will be sent to Database on Post.
    this.getFormData = function (form, initialData) {
        var formElements = form.elements,
            formData = initialData ? initialData : '';

        formElements.forEach(function (elem) {
            var options,
                value = '',
                validField = true,
                formGroup = elem.closest('.form-group'),
                includeItem = true,
                name = encodeURIComponent(elem.name),
                validName = name !== '' && name.indexOf('companyProvince-alt') === -1 && name.indexOf('companyProvinceInput') === -1;

            // Tests if parent div is not hidden applies for Progressive Profiling and Matchmaking Events
            if (formGroup && formGroup.classList.contains('hide')) {
                validField = false;
            }

            if (validName && validField) {
                if (elem.getAttribute('multiple') !== null) {
                    options = elem.querySelectorAll('option');
                    options.forEach(function (option) {
                        if (option.selected && option.value !== '') {
                            value += option.value + '::';
                        }
                    });
                    // Removes the last :: of the variable
                    value = value.substr(0, value.length - 2);
                } else if (elem.type === 'checkbox') {
                    value = elem.checked ? (elem.value ? elem.value : 'yes') : '';

                    if ((!elem.checked && elem.value === 'checked') || (elem.closest('.c-multi-select') && !elem.checked)) {
                        includeItem = false;
                    }
                } else if (elem.type === 'radio') {
                    value = elem.value;
                    if (!elem.checked) {
                        includeItem = false;
                    }
                } else {
                    value = EDC.forms.getInputValue(elem);
                }

                if (includeItem) {
                    formData += name + '=' + encodeURIComponent(value) + '&';
                }
            }
        });

        formData = formData.substr(-1, 1) === '&' ? formData.substr(0, formData.length - 1) : formData;

        return formData;
    };

    // Helper function that disables the Submit Button to prevent issues
    this.disableSubmit = function (element) {
        element.disabled = true;
    };

    // Helper function that disables the Submit Button to prevent issues
    this.enableSubmit = function (element) {
        setTimeout(function () {
            element.disabled = false;
        }, 2000);
    };

    // Helper function to submit data to AEM or Eloqua
    this.submitFormData = function (method, url, params, callback, submitFailedMessage) {
        EDC.utils.fetchJSON(method, url, params, function (data) {
            // Removes the error message from screen if exists
            if (submitFailedMessage && !submitFailedMessage.classList.contains('hide')) {
                submitFailedMessage.classList.add('hide');
            }

            if (callback) {
                callback(data);
            }
        }, function () {
            // Error on server communication, delay or related issues
            if (submitFailedMessage) {
                submitFailedMessage.classList.remove('hide');
                EDC.forms.enableSubmit(submitFailedMessage.closest('form').querySelector('button:not(.hide)[disabled]'));
            }
        });
    };

    this.isDLS = function (date) {
        // Return true if date is between [second Sunday of March and first Sunday of November[
        var currentMonth = date.getMonth(),
            utils = {
                findStartOfDLS: function (year) {
                    // DST begin: March second Sunday.
                    var myDate = new Date(year, 2, 7);

                    myDate.setDate(7 + (7 - myDate.getDay()));
                    return myDate.getDate();

                },
                findEndOfDLS: function (year) {
                    // DST finish: November first Sunday.
                    var myDate = new Date(year, 10, 7);

                    myDate.setDate(7 - myDate.getDay());
                    return myDate.getDate();
                }
            };

        if (currentMonth === 2) {
            return date.getDate() >= utils.findStartOfDLS(date.getFullYear());
        } else if (currentMonth === 10) {
            return date.getDate() < utils.findEndOfDLS(date.getFullYear());
        }

        return (currentMonth > 2 && currentMonth < 10);
    };

    this.ppUnlockFeatures = function (response, sneakPeek, form, countryGridCards, gridTitle, hideForm) {
        var pageLanguage,
            search,
            replace;

        if (form) {
            form.parentElement.classList.add('email-closed');
        }
        // Shows sneak peek Progressive Profiling form
        if (sneakPeek) {
            if (response === 'yes') {
                sneakPeek.classList.add('unlocked');
                // let progressive profiling hide and show sneak peek
                // progressive profiling.js line 703
                // if (sneakPeek.classList.contains('hide')) {
                //    sneakPeek.classList.remove('hide');
                // }
            }
        }

        if (response === 'yes') {
            // Hides the Progressive Profiling MEA form
            if (form && hideForm) {
                form.classList.add('hide');
            }

            // Changes the URL for the Premium URL
            if (countryGridCards && countryGridCards.length) {
                pageLanguage = document.documentElement.lang || 'en';
                search = '/' + pageLanguage;
                replace = search + '/premium';

                countryGridCards.forEach(function (cardLink) {
                    cardLink.setAttribute('href', cardLink.getAttribute('href').replace(search, replace));
                });
            }

            // Changes title
            if (gridTitle) {
                gridTitle.innerText = gridTitle.getAttribute('data-unlocked-title');
            }
        }
    };

    // Helper function to reset Progressive Profiling if accessed by another component
    this.progressiveProfilingReset = function (element) {
        var submitFailedMessage = element.querySelector('.submit-failed-message'),
            level1 = element.querySelector('.level-1'),
            successMessage = element.querySelector('.success-message'),
            disabledButtons = element.querySelectorAll('button[disabled]');

        // Show / hide initial Progressive Profiling elements.
        if (level1.classList.contains('hide')) {
            level1.classList.remove('hide');
            level1.classList.add('show');
        }

        element.querySelector('.title').classList.toggle('title-color');
        element.querySelector('.bottom-ctas').classList.remove('space-center');
        element.querySelector('.bottom-ctas').classList.add('space-end');
        EDC.forms.toggleFormFields(element.querySelectorAll('.submit'), 'display');
        EDC.forms.toggleFormFields(element.querySelectorAll('.cancel'), 'display');
        EDC.forms.toggleFormFields(element.querySelectorAll('.email-submit'), 'read-only');
        EDC.forms.toggleFormFields(element.querySelectorAll('.email-disclaimer'), 'display');
        EDC.forms.toggleFormFields(element.querySelectorAll('.bottom-ctas, .back, .next-btn, .done-btn'), 'hide-all');
        EDC.forms.toggleFormFields(element.querySelectorAll('.headings, .status'), 'hide-all');
        EDC.forms.toggleFormFields(element.querySelectorAll('.level-2 form-group, .level-2-heading, .level-2'), 'hide-all');
        EDC.forms.toggleFormFields(element.querySelectorAll('.level-3 form-group, .level-3-heading, .level-3'), 'hide-all');
        EDC.forms.toggleFormFields(element.querySelectorAll('.level-4 form-group, .level-4-heading, .level-4'), 'hide-all');
        EDC.forms.toggleFormFields(element.querySelectorAll('.level-5 form-group, .level-5-heading, .level-5'), 'hide-all');

        // Hides the success message if its vissible
        if (!successMessage.classList.contains('hide')) {
            successMessage.classList.add('hide');
        }

        // Resets values of single and multiple selection dropdowns
        element.querySelectorAll('select').forEach(function (elem) {
            elem.selectedIndex = null;
        });

        element.querySelectorAll('.form-control.dropdown').forEach(function (elem) {
            $(elem).dropdown('clear');
        });

        // Removes error messages from form
        element.querySelectorAll('.form-control').forEach(function (elem) {
            elem.classList.remove('error');
        });

        // Removes eloqua error message
        if (submitFailedMessage && !submitFailedMessage.classList.contains('hide')) {
            submitFailedMessage.classList.add('hide');
        }

        // Enables all the disabled buttons
        disabledButtons.forEach(function (button) {
            EDC.forms.enableSubmit(button);
        });

        // Resets the rest of the form
        element.querySelector('form').reset();
    };

    this.initializeFormReviewField = function (element) {
        var d = document,
            form = element.closest('form'),
            formEls = form ? form.elements : [],
            field = element.querySelector('.field-value'),
            fieldParent = field.closest('.c-form-field-info'),
            fieldValue = field.getAttribute('data-field-value'),
            emptyString = '-',
            sectionsToToggle = form.querySelectorAll('.section-to-toggle'),
            forcedLayout = fieldParent.getAttribute('data-forced-layout') === 'true';

        function _renderForForcedLayout(el) {
            var divEl = fieldParent.querySelector('.forced-layout-wrapper'),
                divEl2 = divEl ? divEl.querySelector('.input-description') : null,
                inputEl = divEl ? divEl.querySelector('input') : null,
                labelEl = divEl ? divEl.querySelector('label') : null,
                fieldInfo = fieldParent.querySelector('.info'),
                label = el.parentElement.querySelector('label').textContent;

            if (!divEl && !inputEl && !divEl2 && !labelEl) {
                divEl = d.createElement('div');
                divEl2 = d.createElement('div');
                divEl.classList.add('checkbox-item');
                divEl2.classList.add('input-description');
                inputEl = d.createElement('input');
                labelEl = d.createElement('label');
                inputEl.type = el.type;
                inputEl.checked = el.checked;
                inputEl.setAttribute('disabled', 'disabled');
                labelEl.textContent = label;
                divEl.appendChild(inputEl);
                divEl2.appendChild(labelEl);
                divEl.appendChild(divEl2);
                fieldParent.appendChild(divEl);
                fieldInfo.classList.add('hide');
            }
        }

        function _inputChanged(e) {
            var eTarget = e.currentTarget,
                eValue = eTarget.value,
                eType = e.type,
                ddItems,
                textForMultiDD = '',
                label = eTarget.parentElement.querySelector('label'),
                labelText = label ? label.textContent : emptyString,
                forcedLayoutInput;

            if (eValue !== '') {
                if (eTarget.tagName.toLowerCase() === 'div') {
                    ddItems = eTarget.querySelectorAll('.item.selected-item');
                    ddItems.forEach(function (item, index) {
                        textForMultiDD +=
                        (ddItems.length > 1 && index + 1 === ddItems.length ? ' & ' : '') +
                        item.textContent +
                        (index + 1 === ddItems.length - 1 || index === ddItems.length - 1 ? '' : ', ');
                    });
                    field.textContent = textForMultiDD;
                } else if (eTarget.type === 'checkbox') {
                    if (!forcedLayout) {
                        if (eTarget.checked) {
                            field.textContent = labelText;
                            element.classList.remove('hide');
                        } else {
                            field.textContent = emptyString;
                            element.classList.add('hide');
                        }
                    } else {
                        forcedLayoutInput = fieldParent.querySelector('input[type="checkbox"]');

                        if (forcedLayoutInput) {
                            forcedLayoutInput.checked = eTarget.checked;
                        }
                    }
                } else if (eTarget.type === 'radio') {
                    field.textContent = labelText;
                } else if (eType === 'change') {
                    field.textContent = eTarget.closest('select').querySelectorAll('option')[eTarget.closest('select').selectedIndex].textContent;
                } else if (eTarget.classList.contains('special-input')) {
                    setTimeout(function () {
                        field.textContent = eTarget.parentElement.querySelector('input[name="' + eTarget.name + '"').value;
                    }, 500);
                } else {
                    field.textContent = eValue;
                }
            } else {
                field.textContent = emptyString;
            }

            if (sectionsToToggle.length > 0) {
                if (field.closest('.section-to-toggle')) {
                    if (field.textContent === emptyString) {
                        fieldParent.classList.add('hide');
                    } else {
                        fieldParent.classList.remove('hide');
                    }
                }

                sectionsToToggle.forEach(function (section) {
                    var hiddenReviewItemsCounter = 0,
                        itemsToReview = section.querySelectorAll('.c-form-field-info');

                    itemsToReview.forEach(function (item) {
                        if (item.classList.contains('hide')) {
                            hiddenReviewItemsCounter++;
                        }
                    });

                    if (hiddenReviewItemsCounter === itemsToReview.length) {
                        section.classList.add('hide');
                    } else {
                        section.classList.remove('hide');
                    }
                });
            }
        }

        field.textContent = emptyString;

        formEls.forEach(function (el) {
            var elem = el.parentElement.querySelector('input[name="' + el.name + '"'),
                isMultiDD = el.getAttribute('multiple') !== null;

            if (el.name && el.name === fieldValue) {
                if (el.tagName.toLowerCase() === 'input' || el.tagName.toLowerCase() === 'textarea') {
                    if (el.type === 'checkbox') {
                        if (forcedLayout) {
                            _renderForForcedLayout(el);
                        }

                        EDC.utils.attachEvents(el, 'click', _inputChanged);
                    } else {
                        EDC.utils.attachEvents(el, 'input', _inputChanged);
                    }
                } else if (el.tagName.toLowerCase() === 'select') {
                    if (!isMultiDD) {
                        EDC.utils.attachEvents(el, 'change', _inputChanged);
                    } else {
                        EDC.utils.attachEvents(el.closest('.c-dropdown').querySelectorAll('.menu'), 'click', _inputChanged);
                    }
                }

                setTimeout(function () {
                    if (elem && elem.type !== 'radio' && elem.type !== 'checkbox' && elem.value !== '') {
                        field.textContent = elem.value;
                    }
                }, 1000);

                field.setAttribute('text-attached', 'true');
            }
        });

        if (!field.getAttribute('text-attached')) {
            field.textContent = fieldValue;
        }
    };

    // Helper function to reset the Contact Us Form and Inquiry Formif question is submitted
    this.resetForm = function (element) {
        var dds = element.querySelectorAll('.ui.dropdown');

        dds.forEach(function (dd) {
            var isMulti = dd.classList.contains('multiple'),
                items = isMulti ? dd.querySelectorAll('.menu .item.selected-item') : dd.querySelectorAll('.menu .item.active.selected'),
                select = dd.querySelector('select'),
                label = dd.querySelector('.text');

            items.forEach(function (item) {
                if (isMulti) {
                    item.classList.remove('selected-item');
                } else {
                    item.classList.remove('active');
                    item.classList.remove('selected');
                }
                label.innerHTML = select.getAttribute('data-default-value');
                label.classList.add('default');
                select.querySelector('option').click();
            });
        });
    };

    // Helper function to reset the EHH Form if question is submitted
    this.resetEHHForm = function (elem) {
        var otherMarketDropdown = elem ? elem.querySelector('.form-fields .ui.dropdown') : '',
            dropdownText = otherMarketDropdown ? otherMarketDropdown.querySelector('.text') : '';

        if (elem) {
            elem.querySelector('.form-success').classList.add('hide');
            elem.querySelector('.form-fields').classList.remove('hide');
            elem.querySelector('.form-info').classList.remove('hide');
            elem.querySelector('.form-submit button').disabled = false;
            elem.querySelector('form').reset();
            dropdownText.classList.add('default');
            dropdownText.innerText = otherMarketDropdown.querySelector('select').getAttribute('data-default-value');
            otherMarketDropdown.querySelectorAll('.item').forEach(function (item) {
                item.classList.remove('active');
                item.classList.remove('selected');
            });
        }
    };

    this.getUserProfileType = function (servletURL, canProfileFn, nonCanProfileFn) {
        // Calls JSON file from servlet to get the requested questions
        EDC.utils.fetchJSON('GET', servletURL, '', function (data) {
            var profile = data.profileType ? data.profileType : '';

            if (profile === 'canadian-company' || profile === 'edc') {
                canProfileFn();
            } else {
                nonCanProfileFn();
            }
        }, function () {
            nonCanProfileFn();
        });
    };

    this.cleanFormElements = function (els) {
        var finalEls;

        if (els) {
            finalEls = Array.from(els).filter(function (value) {
                var tag = value.tagName ? value.tagName.toLowerCase() : null;

                return tag !== 'fieldset';
            });
        }

        return finalEls;
    };
};
