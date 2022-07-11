(function($window, $document, Granite, $) {
    "use strict";

    $document.on("dialog-ready", function() {
        /**
         * Validator for target field
         */
        var targetPattern = /^[a-z0-9_\-]+$/,
            targetIdPattern = /^[a-z]+[a-z0-9_\-]+$/;
        $window.adaptTo("foundation-registry").register("foundation.validation.validator", {
            selector: '[data-validation~="field.target"]',
            validate: function(el) {
                var valid = true,
                    msg = "This field must only contain lowercase letters, numbers, dashes, and underscores.";

                if ($(el).hasClass("fieldid")) {
                    valid = targetIdPattern.test(el.value) || el.value === "";
                    msg = "This field must only contain lowercase letters, numbers, dashes, underscores, and it should not start with numbers.";
                } else {
                    valid = targetPattern.test(el.value) || el.value === "";
                }

                if (!valid) {
                    return Granite.I18n.get(msg);
                }
            }
        });
    });

})($(window), $(document), Granite, $);