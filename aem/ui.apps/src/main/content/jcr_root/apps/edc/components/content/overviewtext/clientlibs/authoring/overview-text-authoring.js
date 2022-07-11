(function ($window, $document, Granite, $) {
    "use strict";

    $document.on("dialog-ready", function () {
        /**
         * Show the different fields according to the author selection on display type dropdown
         */
        function showHideOptions() {
            var displayType = $('coral-select input[name="./displaytype"]').val(),
                showTitleAfter = $('[data-validation="overviewtext.field.showtitleafter"]'),
                rtfText = $('[data-validation="overviewtext.field.rtftext"]'),
                showSeparator = $('[data-validation="overviewtext.field.showseparator"]'),
                showPageBreak = $('[data-validation="overviewtext.field.showpagebreak"]');

            if(displayType === 'overviewtext') {
                showTitleAfter.closest('.coral-Form-fieldwrapper').removeClass('hide');
                rtfText.closest('.coral-Form-fieldwrapper').addClass('hide');
                showSeparator.closest('.coral-Form-fieldwrapper').removeClass('hide');
                showPageBreak.closest('.coral-Form-fieldwrapper').removeClass('hide');
            } else if (displayType === 'travelbrief') {
                showTitleAfter.closest('.coral-Form-fieldwrapper').addClass('hide');
                rtfText.closest('.coral-Form-fieldwrapper').removeClass('hide');
                showSeparator.closest('.coral-Form-fieldwrapper').addClass('hide');
                showPageBreak.closest('.coral-Form-fieldwrapper').addClass('hide');
            } else if (displayType === 'aboutedc') {
                showTitleAfter.closest('.coral-Form-fieldwrapper').addClass('hide');
                rtfText.closest('.coral-Form-fieldwrapper').addClass('hide');
                showSeparator.closest('.coral-Form-fieldwrapper').addClass('hide');
                showPageBreak.closest('.coral-Form-fieldwrapper').addClass('hide');
            } else {
                // if nothing selected show all fields for support to old content
                showTitleAfter.closest('.coral-Form-fieldwrapper').removeClass('hide');
                rtfText.closest('.coral-Form-fieldwrapper').removeClass('hide');
                showSeparator.closest('.coral-Form-fieldwrapper').removeClass('hide');
                showPageBreak.closest('.coral-Form-fieldwrapper').removeClass('hide');
            }
        }

        $document.on('change', 'coral-select[name="./displaytype"]', showHideOptions);

         $window.adaptTo('foundation-registry').register('foundation.validation.validator', {
             selector: '[data-validation="overviewtext.field.rtftext"]',
             validate: function (el) {
                 var displayType = $('coral-select input[name="./displaytype"]').val(),
                 rteVal =  $(el).parent().find('[type=hidden][data-validation="overviewtext.field.rtftext"]').val().trim();

                 if (displayType === 'travelbrief' && rteVal === '') {
                     return Granite.I18n.get('This field is required.');
                 }
             }
         });

        setTimeout(showHideOptions, 500) ;
    });
})($(window), $(document), Granite, $);