(function( $document, Granite, $) {
    'use strict';
    var cards,
    checkbox;

    function showHideCheckbox () {
      cards.each(function() {
        if($(this).val()  != 'pullfrommostrecent') {
          checkbox.hide();
          return false;
       } 
         checkbox.show();
     });
    }

    $(window)
            .adaptTo('foundation-registry')
            .register(
                    'foundation.validation.validator',
                    {
                        selector : '[data-validation="thought-leadership"],[data-validation$=",thought-leadership"],[data-validation*=",thought-leadership,"],[data-foundation-validation="thought-leadership"],[data-foundation-validation$=",thought-leadership"],[data-foundation-validation*=",thought-leadership,"]',
                        validate : function(el) {
                            var curVal = $(el).val(),
                            pathfield = $(el).parents('.coral-FixedColumn-column').eq(0).find('.js-coral-pathbrowser-input'),
                            overrideFields = $(el).parents('.coral-FixedColumn-column').eq(0).find('.coral-Form-field'),
                            checkbox = $(el).parents('.coral-TabPanel-content').eq(0).find('.datecontainer');

                            if(curVal == 'pullfrommostrecent'){
                              showHideCheckbox();
                            }

                            if(curVal == 'customauthor'){
                              checkbox.hide();
                               overrideFields.each(function() {
                                $(this).removeAttr('disabled');
                                //skip image
                                if(!$(this).hasClass('cq-FileUpload')) {
                                  $(this).attr('required',true);
                                }
                               });
                            } else {
                              overrideFields.each(function() {
                                $(this).attr('disabled',true);
                                $(this).removeAttr('required');
                              });
                            }

                            if(curVal == 'pagepath'){
                              checkbox.hide();
                              pathfield.removeAttr('disabled');
                              pathfield.attr('required',true);
                            } else {
                              pathfield.attr('disabled',true);
                              pathfield.removeAttr('required');
                            }
                            return false;
                        }
                    });

    //validate on load
    $document.onDialogReady('edc/components/content/homepage/featurededcthoughtleadership', function () {
        cards = $('coral-dialog[open]').find('.cq-dialog-dropdown-showhide select');
        checkbox = $('coral-dialog[open]').find('.datecontainer');
        showHideCheckbox(); 
    });
}($(document), Granite, jQuery));
