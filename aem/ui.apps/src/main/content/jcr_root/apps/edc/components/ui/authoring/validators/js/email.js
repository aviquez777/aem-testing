(function(Granite, $) {
    'use strict';

    function isValidEmail(url) {
        var urlPattern = new RegExp(
                '^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})');

        return (url ? urlPattern.test(url) : false);
    }

    $(window)
            .adaptTo('foundation-registry')
            .register(
                    'foundation.validation.validator',
                    {
                        selector : '[data-validation="email"],[data-validation$=",email"],[data-validation*=",email,"],[data-foundation-validation="email"],[data-foundation-validation$=",email"],[data-foundation-validation*=",email,"]',
                        validate : function(el) {
                            var url = $(el).val();

                            if (url && !isValidEmail(url)) {
                                return Granite.I18n.get('Enter a valid email');
                            }
                        }
                    });
}(Granite, jQuery));
