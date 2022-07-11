(function ($window, $document, Granite, $) {
    "use strict";
    $document.on("dialog-ready", function () {
        /**
         * Show the docId and asset tier fields when the mode is 'mode_ebook_regular'
         */
        function showHideOptions() {
            var mode = $('coral-select input[name="./mode"]').val(),
                docId = $('[data-validation="progressiveprofilig.field.docId"]'),
                assetTier = $('[data-validation="progressiveprofilig.field.assetTier"]'),
                knowledgeCustomer = $('.progressiveProfilingFieldKnowledgeCustomer');

            if(mode === 'mode_ebook_regular') {
                docId.closest('.coral-Form-fieldwrapper').removeClass('hide');
                assetTier.closest('.coral-Form-fieldwrapper').removeClass('hide');
                knowledgeCustomer.addClass('hide');

            } else if (mode === 'mode_knowledge') {
                docId.closest('.coral-Form-fieldwrapper').addClass('hide');
                assetTier.closest('.coral-Form-fieldwrapper').addClass('hide');
                knowledgeCustomer.removeClass('hide');
            } else if (mode === 'mode_crg') {
                docId.closest('.coral-Form-fieldwrapper').addClass('hide');
                assetTier.closest('.coral-Form-fieldwrapper').addClass('hide');
            } else {
                docId.closest('.coral-Form-fieldwrapper').addClass('hide');
                assetTier.closest('.coral-Form-fieldwrapper').addClass('hide');
                knowledgeCustomer.addClass('hide');
            }
        }

        /**
         * Events
         */
        $document.on('change', 'coral-select[name="./mode"]', showHideOptions);

        $document.on('blur', '.coral3-Textfield[name="./peakLabel"]', function() {
            var validation = $('[data-validation="progressiveprofilig.field.docId"]').adaptTo('foundation-validation');

            if (validation) {
                setTimeout(function () {
                    validation.checkValidity();
                    validation.updateUI();
                }, 20);
            }
        });

        /**
         * Validations for Regular Ebook - docId is required only for sneak peek version and regular ebook mode
         */
         $window.adaptTo('foundation-registry').register('foundation.validation.selector', {
             submittable: '[data-validation="progressiveprofilig.field.docId"]',
             candidate: '[data-validation="progressiveprofilig.field.docId"]',
             exclusion: '[data-validation="progressiveprofilig.field.docId"] *'
         });

         $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
             selector: '[data-validation="progressiveprofilig.field.docId"]',
             validate: function (el) {
                 var sneakPeakLabel = $('.coral3-Textfield[name="./peakLabel"]').val(),
                     mode = $('coral-select input[name="./mode"]').val();

                 if (mode === 'mode_ebook_regular' && sneakPeakLabel !== '' && $(el).val() === '') {
                     return Granite.I18n.get('The docID is required.');
                 }
             }
         });

        setTimeout(showHideOptions, 500) ;
    });
})($(window), $(document), Granite, $);