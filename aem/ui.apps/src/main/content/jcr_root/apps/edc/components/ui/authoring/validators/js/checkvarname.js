(function(Granite, $) {
    'use strict';

    function isValidVarName(string) {
        var urlPattern = new RegExp(
                '^[_a-z]\\w*$');

        return (string ? urlPattern.test(string) : false);
    }

    $(window)
            .adaptTo('foundation-registry')
            .register(
                    'foundation.validation.validator',
                    {
                        selector : '[data-validation="checkvarname"],[data-validation$=",checkvarname"],[data-validation*=",checkvarname,"],[data-foundation-validation="checkvarname"],[data-foundation-validation$=",checkvarname"],[data-foundation-validation*=",checkvarname,"]',
                        validate : function(el) {
                            var string = $(el).val();

                            if (string && !isValidVarName(string)) {
                                return Granite.I18n.get('Please enter a valid charater string');
                            }
                        }
                    });
}(Granite, jQuery));
