(function ($, $document) {
    'use strict';


    $(document).on('change', 'coral-checkbox[name="./usebusinessenvironment"]',function(){
        var checkbox =$("input[name='./usebusinessenvironment']"),
            container = $(".business-environment-container"),
            fields = $('.cq-dialog-mtbs').find('[data-validation="fields.tabone"]');


        if(!checkbox.prop('checked')){
            container.removeClass('hide');

            fields.attr('required','true');
            fields.attr('aria-required', 'true');

        } else {
            container.addClass('hide');

            fields.removeAttr('required');
            fields.removeAttr('aria-required');


        }
    });

    $document.on('dialog-ready', function() {
        var checkbox =$("input[name='./usebusinessenvironment']"),
            container = $(".business-environment-container"),
            fields = $('.cq-dialog-mtbs').find('[data-validation="fields.tabone"]');


        if(!checkbox.prop('checked')){
            container.removeClass('hide');

            fields.attr('required','true');
            fields.attr('aria-required', 'true');

        } else {
            container.addClass('hide');

            fields.removeAttr('required');
            fields.removeAttr('aria-required');


        }
    });

})($, $(document));

