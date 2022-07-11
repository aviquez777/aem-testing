(function ($document) {
    "use strict";

    $document.on("dialog-ready", function() {

        var selector = 'input[data-wrapperclass=campaign-variant]',
            clearChecksBtn = $('button.clear-checkboxes');

        $(selector).click(function(){
            $(selector).each(function(){
                $(this).prop('checked', false); 
            }); 
            $(this).prop('checked', true);
        });

        $(clearChecksBtn).click(function(){
            $(selector).each(function(){
                $(this).prop('checked', false); 
            });
        });

    });
})($(document));