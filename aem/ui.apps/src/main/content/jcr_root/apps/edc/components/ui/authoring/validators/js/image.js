(function(Granite, $) {
    'use strict';

    $(window)
            .adaptTo('foundation-registry')
            .register(
                    'foundation.validation.validator',
                    {
                        selector : '[data-validation="image"],[data-validation$=",image"],[data-validation*=",image,"],[data-foundation-validation="image"],[data-foundation-validation$=",image"],[data-foundation-validation*=",image,"]',
                        validate : function(el) {

                            if (!$(el).hasClass('is-filled')) {
                                return Granite.I18n
                                        .get('Please Drag and Drop an image.');
                            }

                        }
                    });
}(Granite, jQuery));
