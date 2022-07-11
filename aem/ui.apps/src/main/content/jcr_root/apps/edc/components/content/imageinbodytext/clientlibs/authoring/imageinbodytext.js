(function ($window, $document, Granite, $) {
    'use strict';

    $document.on('dialog-ready', function () {
        var dialogImageInBodyText = $('coral-dialog[open]').find('.cq-dialog-content[data-dialog-id="dialog-imageinbodytext"]'),
            rteRequired,
            imagePosition,
            typeTextBackground,
            enlargeImage;

        if (dialogImageInBodyText.length) {
            rteRequired = $('.richtext-container .rte-validation-required').siblings('.coral-RichText');
            imagePosition = $('.coral3-Radio-input[name="./imagePosition"]');
            typeTextBackground = $('.coral3-Radio-input[name="./typeTextBackground"]');
            enlargeImage = $('.coral-Checkbox-input[name="./enlargeImage"]');

            function showHideHeading(state) {
                var heading = $('[data-validation="textbackground.field.title"]');

                if (state === 'show') {
                    heading.parent().removeClass('hide');
                } else {
                    heading.parent().addClass('hide');
                    heading.val('');
                }
            }

            function showHideFullResolution(state) {
                var fullResolution = $('[data-validation="imageinbodytext.field.fullResolution"]'),
                    fullResolutionAlt = $('[data-validation="imageinbodytext.field.fullResolutionAlt"]');

                if (state === 'show') {
                    fullResolution.parent().removeClass('hide');
                    fullResolutionAlt.parent().removeClass('hide');
                } else {
                    fullResolution.parent().addClass('hide');
                    fullResolution.val('');

                    fullResolutionAlt.parent().addClass('hide');
                    fullResolutionAlt.val('');
                }
            }

            function verifyField(field) {
                var validation = $(field).adaptTo('foundation-validation');

                if (validation) {
                    setTimeout(function () {
                        validation.checkValidity();
                        validation.updateUI();
                    }, 20);
                }
            }

            function showErrorRTE (el, message) {
                var field = $(el),
                    fieldErrorEl = $('<span class="coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS" data-init="quicktip" data-quicktip-type="error" />'),
                    arrow = field.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";

                this.clear();

                field.parent().find('.coral-RichText').addClass('is-invalid');

                fieldErrorEl.clone()
                    .attr('data-quicktip-arrow', arrow)
                    .attr('data-quicktip-content', message)
                    .insertAfter(field);
            }

            function clearErrorRTE (el) {
                var field = $(el);

                field.nextAll('.coral-Form-fielderror').tooltip('hide').remove();
                field.parent().find('.coral-RichText').removeClass('is-invalid');
            }

            function verifyRTE() {
                var fieldTextBackground = $('[data-validation="textbackground.field.textBackground"]'),
                    component = $('coral-select input[name="./componentType"]');

                if (component.val() === 'textbackground' && fieldTextBackground.length) {
                    verifyField(fieldTextBackground);
                }
            }

            /**
             * Image in Body Text Validations
             *
             */
            enlargeImage.on('click', function () {
                if ($(this).is(':checked')) {
                    showHideFullResolution('show');
                } else {
                    showHideFullResolution('hide');
                }
            });

            rteRequired.on('blur', verifyRTE);
            imagePosition.on('click', verifyRTE);

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="imageinbodytext.field.fileReference"]',
                validate: function (el) {
                    var component = $('coral-select input[name="./componentType"]');

                    if (component.val() === 'imageinbodytext' && el.value === '') {
                        return Granite.I18n.get('The image is required.');
                    }
                }
            });

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="imageinbodytext.field.imageAlt"]',
                validate: function (el) {
                    var component = $('coral-select input[name="./componentType"]');

                    if (component.val() === 'imageinbodytext' && el.value === '') {
                        return Granite.I18n.get('The image alternate text is required.');
                    }

                }
            });

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="imageinbodytext.field.fullResolution"]',
                validate: function (el) {
                    var component = $('coral-select input[name="./componentType"]'),
                        isEnlargeImage = $('.coral-Checkbox-input[name="./enlargeImage"]').is(':checked');

                    if (component.val() === 'imageinbodytext' && isEnlargeImage && el.value === '') {
                        return Granite.I18n.get('The Full Resolution image is required.');
                    }
                }
            });

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="imageinbodytext.field.fullResolutionAlt"]',
                validate: function (el) {
                    var component = $('coral-select input[name="./componentType"]'),
                        isEnlargeImage = $('.coral-Checkbox-input[name="./enlargeImage"]').is(':checked');

                    if (component.val() === 'imageinbodytext' && isEnlargeImage && el.value === '') {
                        return Granite.I18n.get('The Full Resolution image alternate text is required.');
                    }
                }
            });

            setTimeout(function () {
                var component = $('coral-select input[name="./componentType"]');
 
                if (component.val() === 'imageinbodytext' && enlargeImage.is(':checked')) {
                        showHideFullResolution('show');
                    } else {
                        showHideFullResolution('hide');
                    }
            }, 100);

            /**
             * Text Background Validations
             *
             */
            typeTextBackground.on('click', function () {
                if (this.value === 'lc') {
                    showHideHeading('show');
                } else {
                    showHideHeading('hide');
                }
            });

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="textbackground.field.title"]',
                validate: function (el) {
                    var component = $('coral-select input[name="./componentType"]'),
                        typeTextBackground = $('.coral3-Radio-input[name="./typeTextBackground"]:checked').val();

                    if (component.val() === 'textbackground' && typeTextBackground === 'lc' && el.value === '') {
                        return Granite.I18n.get('The heading is required for light-charcoal background.');
                    }
                }
            });

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="textbackground.field.textBackground"]',
                validate: function (el) {
                    var component = $('coral-select input[name="./componentType"]');

                    if (component.val() === 'textbackground' && $(el).text() === '') {
                        return Granite.I18n.get('The text background is required.');
                    }
                },
                show: showErrorRTE,
                clear: clearErrorRTE
            });

            /* Verify the type of background */
            setTimeout(function () {
                var component = $('coral-select input[name="./componentType"]');

                if (component.val() === 'textbackground') {
                    if (typeTextBackground.filter(':checked').val() === 'lc') {
                        showHideHeading('show');
                    } else {
                        showHideHeading('hide');
                    }
                }
            }, 20);

            /**
             * Product Feature Validations
             *
             */

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="productfeature.field.fcFileReference"]',
                validate: function (el) {
                    var component = $('coral-select input[name="./componentType"]');

                    if (component.val() === 'productfeature' && el.value === '') {
                        return Granite.I18n.get('The image is required.');
                    }
                }
            });

            $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
                selector: '[data-validation="productfeature.field.fcAlt"]',
                validate: function (el) {
                    var component = $('coral-select input[name="./componentType"]');

                    if (component.val() === 'productfeature' && el.value === '') {
                        return Granite.I18n.get('The image alternate text is required.');
                    }
                }
            });
        }
    });
})($(window), $(document), Granite, $);
