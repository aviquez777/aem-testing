(function ($window, $document, Granite, $) {
    'use strict';

    $document.on('dialog-ready', function () {
        var currentdialog = $('.cq-dialog-feedback-form').length ? $('.cq-dialog-feedback-form') : $('.cq-policy-feedback-form'),
            ffDisplayType = currentdialog.find('coral-select input[name="./displaytype"]').val();

        /**
         * Show/hide default/form fields according to the author selection on display type dropdown
         */
        function showHideOptions() {
            var tabs = currentdialog.find('> nav > .coral-TabPanel-tab');

            ffDisplayType = currentdialog.find('coral-select input[name="./displaytype"]').val();

            if (ffDisplayType === 'default') {
                tabs.filter(':eq(2), :eq(3)').addClass('hide');
                tabs.eq(1).removeClass('hide');
            } else if (ffDisplayType === 'form') {
                tabs.filter(':eq(2), :eq(3)').removeClass('hide');
                tabs.eq(1).addClass('hide');
            }
        }

        /**
         * Check if required field is empty
         * 
         * @param field - element to evaluate
         * @param position - displayType (form, default)
         */
        function validateField(field, position) {
            var elementValue = field.is('input') || field.is('textarea') ? field.val() : field.is('foundation-autocomplete') ? findAutocompleteValue(field) : field.find('input').val();

            return (ffDisplayType === position && elementValue === '');
        }

        /**
         * Get autocomplete value
         * 
         * @param field - element to evaluate
         * 
         */
        function findAutocompleteValue(field) {
            return field.find(".coral-InputGroup > input.coral3-Textfield").val();
        }

        if (currentdialog.length) {
            /**
             * Events
             */
            currentdialog.on('change', 'coral-select[name="./displaytype"]', showHideOptions);

            /**
             * Recheck RTE in blur event
             */
            currentdialog.on('blur', '.richtext-container .cq-RichText-editable', function () {
                var requiredElem = $(this).parent().find('[type=hidden][data-validation="feedback.field.form"]');

                if (requiredElem.length) {
                    var api = requiredElem.adaptTo('foundation-validation');

                    if (api) {
                        setTimeout(function () {
                            api.checkValidity();
                            api.updateUI();
                        }, 20);
                    }
                }
            });

            /**
             * Validations for mandatory fields as per display type selection
             */

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="feedback.field.form"], [data-foundation-validation="feedback.field.form"]',
                validate: function (el) {
                    var error = validateField($(el), 'form');

                    if (error) {
                        return Granite.I18n.get('This field is required for the form view.');
                    }
                }
            });

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="feedback.field.default"], [data-foundation-validation="feedback.field.default"]',
                validate: function (el) {
                    var error = validateField($(el), 'default');

                    if (error) {
                        return Granite.I18n.get('This field is required for default view.');
                    }
                }
            });

            setTimeout(showHideOptions, 500);
        }
    });
})($(window), $(document), Granite, $);