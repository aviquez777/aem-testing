(function( $document, $) {
    'use strict';
    //validate on load
    $document.onDialogReady('wcm/core/components/policy/policy', function (e) {
        var $elem = $(e.target),
            multifield = $elem.find(".gatedleadgenform-cq-dialog-configured-multi-field");
`       `// Do not allow author to move the questions around, otherwise the next deployment merges data into item0, item1, item2
        if (multifield.length) {
            var removeButtons = multifield.find('.coral3-Multifield-remove').not('.gatedleadgenform-cq-dialog-lov-multi-field .coral3-Multifield-remove'),
                moveButtons = multifield.find('.coral3-Multifield-move').not('.gatedleadgenform-cq-dialog-lov-multi-field .coral3-Multifield-move'),
                addButton = multifield.find('button:last');

            removeButtons.remove();
            moveButtons.remove();
            addButton.remove();
            // remove duplicate LOVs multifields
            setTimeout(function(){ 
                multifield.find('.hide.lov-hide-show').remove();
            }, 500);
       }
    });
}($(document), jQuery));
