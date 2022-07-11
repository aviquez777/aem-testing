(function (Granite, $) {
    'use strict';

    $(window).adaptTo('foundation-registry').register('foundation.validation.validator', {
        selector: '[data-validation|="multifield-limit"],[data-validation*=",multifield-limit"]',
        validate: function (el) {
            var elem = $(el),
                value = elem.data('validation'),
                items = elem.find('> coral-multifield-item'),
                minItems = parseInt(value.match(/min:([0-9]*)/)[1]) || 0,
                maxItems = parseInt(value.match(/max:([0-9]*)/)[1]) || 9999,
                msg,
                button = elem.find('button:last');

            if (items.length >= maxItems) {
                button.hide();
            } else {
                button.show();
            }

            if (!(items.length >= minItems && items.length <= maxItems)) {
                elem.addClass('is-invalid');
                msg = Granite.I18n.get('The number of items must be from {0} to {1}', [minItems, maxItems]);
            } else {
                elem.removeClass('is-invalid');
            }
            return msg;
        }
    });
}(Granite, jQuery));
