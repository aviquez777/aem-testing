(function ($window, $document, Granite, $) {
    'use strict';

    $document.on('dialog-ready', function () {
        var currentDialog = $('.cq-dialog-yesno'),
            isSelected = currentDialog.find('.cq-dialog-checkbox-showhide').prop('checked');

        /**
        * Show/hide the follow up questions fields according to the author selection on the checkbox
        */
        function showHideOptions() {
            var tabs = currentDialog.find('coral-tablist > coral-tab'),
                errors = currentDialog.find('coral-panelstack > coral-panel').eq(0).find('.is-invalid'),
                submitfield = currentDialog.find("input[name='./submitText']"),
                confirmationfield = currentDialog.find("input[name='./confirmation']");

            isSelected = currentDialog.find('.cq-dialog-checkbox-showhide').prop('checked');

            if (isSelected) {
                tabs.not(':eq(0)').removeClass('hide');
                tabs.eq(0).removeClass('is-invalid');
                submitfield.closest('.coral-Form-fieldwrapper').removeClass('hide');
                confirmationfield.closest('.coral-Form-fieldwrapper').addClass('hide');
            } else {
                tabs.not(':eq(0)').addClass('hide');
                if (errors.length) {
                    tabs.eq(0).addClass('is-invalid');
                }
                submitfield.closest('.coral-Form-fieldwrapper').addClass('hide');
                confirmationfield.closest('.coral-Form-fieldwrapper').removeClass('hide');
            }
        }

        /**
         *  Validate RTE fields
         */
        function verifyFieldBody(e) {
            var target = $(e.target),
                fieldBody = target.closest('.cq-RichText').find('[data-validation="yesnoquestion.field.checkedRTE"], [data-validation="yesnoquestion.field.uncheckedRTE"]'),
                api = $(fieldBody).adaptTo('foundation-validation');

            if (api) {
                setTimeout(function() {
                    api.checkValidity();
                    api.updateUI();
                }, 20);
            }

        }

        /**
        * Check if required field is empty
        *
        * @param field - element to evaluate
        */
        function validateChecked(field) {
            var elementValue = field.is('input') || field.is('textarea') ? field.val() : field.is('foundation-autocomplete') ? findAutocompleteValue(field) : field.find('input').val(),
                isChecked = currentDialog.find('.cq-dialog-checkbox-showhide').prop('checked');

            return isChecked && elementValue == '';
        }

        /**
        * Check if required field is empty
        *
        * @param field - element to evaluate
        */
        function validateUnchecked(field) {
            var elementValue = field.is('input') || field.is('textarea') ? field.val() : field.is('foundation-autocomplete') ? findAutocompleteValue(field) : field.find('input').val(),
                isChecked = currentDialog.find('.cq-dialog-checkbox-showhide').prop('checked');

            return !isChecked && elementValue == '';
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

        function validatorShow (el, message) {
            var fieldBody = $(el),
                fieldErrorEl = $('<span class="coral-Form-fielderror coral3-Icon coral3-Icon--alert coral3-Icon--sizeS" data-init="quicktip" data-quicktip-type="error" />'),
                arrow = fieldBody.closest('form').hasClass('coral-Form--vertical') ? 'right' : 'top';

            this.clear();

            fieldBody.parent().find('.coral-RichText').addClass('is-invalid');

            fieldErrorEl.clone()
                .attr('data-quicktip-arrow', arrow)
                .attr('data-quicktip-content', message)
                .insertAfter(fieldBody);
        }

        function validatorClear (el) {
            var fieldBody = $(el);

            fieldBody.nextAll('.coral-Form-fielderror').tooltip('hide').remove();
            fieldBody.parent().find('.coral-RichText').removeClass('is-invalid');
        }

        /**
        * Events
        */
        currentDialog.on('change', '.cq-dialog-checkbox-showhide[name="./showFollowUpQuestion"]', showHideOptions);

        currentDialog.on('blur', '.coral-RichText', verifyFieldBody);

        /**
        * Validations for mandatory fields as per checkbox selection
        */
        $window.adaptTo('foundation-registry').register('foundation.validation.selector', {
            submittable: '[data-validation="yesnoquestion.field.checked"], [data-validation="yesnoquestion.field.unchecked"], [data-validation="yesnoquestion.field.checkedRTE"], [data-validation="yesnoquestion.field.uncheckedRTE"]',
            candidate: '[data-validation="yesnoquestion.field.checked"], [data-validation="yesnoquestion.field.unchecked"], [data-validation="yesnoquestion.field.checkedRTE"], [data-validation="yesnoquestion.field.uncheckedRTE"]',
            exclusion: '[data-validation="yesnoquestion.field.checked"] *, [data-validation="yesnoquestion.field.unchecked"] *, [data-validation="yesnoquestion.field.checkedRTE"] *, [data-validation="yesnoquestion.field.uncheckedRTE"] *'
        });

        $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
            selector: '[data-validation="yesnoquestion.field.checked"]',
            validate: function (el) {
                var error = validateChecked($(el));

                if (error) {
                    return Granite.I18n.get('This field is required.');
                }
            }
        });

        $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
            selector: '[data-validation="yesnoquestion.field.unchecked"]',
            validate: function (el) {
                var error = validateUnchecked($(el));

                if (error) {
                    return Granite.I18n.get('This field is required.');
                }
            }
        });

        $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
            selector: '[data-validation="yesnoquestion.field.checkedRTE"]',
            validate: function(el) {
                var isChecked = currentDialog.find('.cq-dialog-checkbox-showhide').prop('checked');

                if (isChecked && el.value === '') {
                    return Granite.I18n.get('This text is required.');
                }
            },
            show: validatorShow,
            clear: validatorClear
        });

        $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
            selector: '[data-validation="yesnoquestion.field.uncheckedRTE"]',
            validate: function(el) {
                var isChecked = currentDialog.find('.cq-dialog-checkbox-showhide').prop('checked');

                if (!isChecked && el.value === '') {
                    return Granite.I18n.get('This text is required.');
                }
            },
            show: validatorShow,
            clear: validatorClear
        });

        setTimeout(showHideOptions, 500);
    });
})($(window), $(document), Granite, $);