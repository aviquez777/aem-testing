(function() {
    'use strict';

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
                selector: '[data-validation="checkDuplicates"]',
                validate: function(el) {
                    var curVal = $(el).val(),
                        countryFields = 0,
                        duplicates = 0,
                        fullAddress = 'fullAddress',
                        companyCity = 'countryCityProvince',
                        otherSelects = $(el).parents('coral-panel-content').eq(0).find('coral-select input[type="hidden"]');

                    $(otherSelects).each(function(index, value) {
                        var thisVal = value.value;

                        if (thisVal === curVal) {
                            duplicates++;
                        }

                        if ((thisVal.indexOf(fullAddress) > 0 || thisVal.indexOf(companyCity) > 0)) {
                            countryFields++;
                        }

                    });

                    if (duplicates > 1) {
                        return 'This Question has been already used on the form';
                    }

                    if (countryFields > 1) {
                        return 'Full Address and Company City Cannot be repeated on the form';
                    }

                }
            });
    }()
);
