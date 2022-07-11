(function (Granite, $) {
    'use strict';

    function correctPath(url) {
        return (url.indexOf('/content/') === 0 || url.indexOf('http') === 0 || url.indexOf('ftp') === 0 || url.indexOf('mailto:') === 0);
    }

    function jcrPath(url) {
         var value = new RegExp(
         '^(\/content\/)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$');
         return value.test(url);
    }

    function httpPath(url) {
         var value = new RegExp(
         '^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$');
         return value.test(url);
    }

    $(window).adaptTo('foundation-registry').register('foundation.validation.validator', {
        selector: '[data-validation="pathurl"],[data-validation$=",pathurl"],[data-validation*=",pathurl,"],[data-foundation-validation="pathurl"],[data-foundation-validation$=",pathurl"],[data-foundation-validation*=",pathurl,"]',
        validate: function(el) {
            var url = $(el).val();

            if (url && !correctPath(url)) {
                return Granite.I18n.get('Invalid URL. Please enter a valid URL');
            } else if (url && (!httpPath(url) && !jcrPath(url))) {
                return Granite.I18n.get('Invalid URL. Please enter a valid URL');
            }
        }
    });
}(Granite, jQuery));