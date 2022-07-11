(function($) {
    'use strict';

    // Force the country value to the saveName Variable if it is an exportCountry, if other name is entered questionnaire will not work properly.
    $(window)
        .adaptTo('foundation-registry')
        .register(
            'foundation.validation.validator',
            {
                selector: '[data-validation="checkcountrysaveval"]',
                validate: function(el) {
                    var saveNameField = $(el).parent().parent().find('input[name*="./saveName"]'),
                        saveNameFieldVal = $(saveNameField).val(),
                        forcedValue = null;

                    switch ($(el).val()) {
                        case 'exportCountry':
                            forcedValue = 'country';
                            break;

                        case 'mmEntryCountryOfOperation':
                            forcedValue = 'countryofoperation';
                            break;

                        case 'mmEntryCountrySanction':
                            forcedValue = 'countrysanction';
                            break;

                        case 'mmEntrySector':
                            forcedValue = 'entrysector';
                            break;

                        default:
                            break;
                    }

                    if (forcedValue && forcedValue !== saveNameFieldVal) {
                        $(saveNameField).val(forcedValue);
                    }

                }
            });
}(jQuery));