(function ($window, $document, Granite, $) {
    'use strict';

    $document.on('dialog-ready', function () {
        var currentdialog = $('.cq-dialog-grc'),
            grcDisplayType = currentdialog.find('coral-select input[name="./displaytype"]').val();

        /**
         * Show/hide the banner fields according to the author selection on display type dropdown
         */
        function showHideOptions() {
            var tabs = currentdialog.find('.coral-TabList > .coral-Tab'),
                errors = currentdialog.find('.coral-PanelStack > .coral-Panel').eq(0).find('.is-invalid');

            grcDisplayType = currentdialog.find('coral-select input[name="./displaytype"]').val();

            if (grcDisplayType === 'inbanner' || grcDisplayType === 'guide') {
                tabs.not(':eq(0)').addClass('hide');
                if (errors.length) {
                    tabs.eq(0).addClass('is-invalid');
                }
            } else {
                tabs.not(':eq(0)').removeClass('hide');
                tabs.eq(0).removeClass('is-invalid');
            }
        }

        /**
         * Check if required field is empty
         *
         * @param field - element to evaluate
         * @param position - displayType (incontent, in banner)
         */
        function validateField(field, position) {
            var elementValue = field.is('input') || field.is('textarea') ? field.val() : field.is('foundation-autocomplete') ? findAutocompleteValue(field) : field.find('input').val();

            return (grcDisplayType === position && elementValue === '')
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

        /**
         * Events
         */
        currentdialog.on('change', 'coral-select[name="./displaytype"]', showHideOptions);

        /**
         * Recheck RTE in blur event
         */
        currentdialog.on('blur', '.richtext-container .cq-RichText-editable', function () {
            var requiredElem = $(this).parent().find('[type=hidden][data-validation="grcsearch.field.incontent"]');

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

        $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
            selector: '[data-validation="grcsearch.field.incontent"], [data-foundation-validation="grcsearch.field.incontent"]',
            validate: function (el) {
                var error = validateField($(el), 'incontent');

                if (error) {
                    return Granite.I18n.get('This field is required for Search results page view.');
                }
            }
        });

        $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
            selector: '[data-validation="grcsearch.field.inbanner"], [data-foundation-validation="grcsearch.field.inbanner"]',
            validate: function (el) {
                var error = validateField($(el), 'inbanner');

                if (error) {
                    return Granite.I18n.get('This field is required for Banner (landing page).');
                }
            }
        });

        $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
            selector: '[data-validation="grcsearch.field.guide"], [data-foundation-validation="grcsearch.field.guide"]',
            validate: function (el) {
                var error = validateField($(el), 'guide');

                if (error) {
                    return Granite.I18n.get('This field is required for Guide landing page view.');
                }
            }
        });

        setTimeout(showHideOptions, 500);
    });
})($(window), $(document), Granite, $);