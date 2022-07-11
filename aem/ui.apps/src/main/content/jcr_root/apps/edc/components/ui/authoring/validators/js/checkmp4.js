(function(Granite, $) {
    'use strict';

    $(window)
            .adaptTo('foundation-registry')
            .register(
                    'foundation.validation.validator',
                    {
                        selector : '[data-validation="checkmp4"],[data-validation$=",checkmp4"],[data-validation*=",checkmp4,"],[data-foundation-validation="checkmp4"],[data-foundation-validation$=",checkmp4"],[data-foundation-validation*=",checkmp4,"]',
                        validate : function(el) {
                            var curVal = $(el).val(),
                            mp4FieldName = './videomp4',
                            mp4File = $(el).parents('.coral-Well').eq(0).find('foundation-autocomplete[name="'+mp4FieldName+'"]'),
                            mp4FileValue = mp4File.find('.coral-Textfield.coral-InputGroup-input').val(),
                            webmFile =  $(el).parents('.coral-Well').eq(0).find('foundation-autocomplete[name="./videowebm"]'),
                            webmFileValue = webmFile.find('.coral-Textfield.coral-InputGroup-input').val(),
                            oggFile =  $(el).parents('.coral-Well').eq(0).find('foundation-autocomplete[name="./videoogg"]'),
                            oggFileValue = oggFile.find('.coral-Textfield.coral-InputGroup-input').val(),
                            fileExt = curVal.split('.').pop();

                            if( mp4FileValue == '' && (webmFileValue != '' || oggFileValue != '')) {
                                mp4File.attr("required",true);
                                return Granite.I18n.get('MP4 Video is required');
                            } else {
                                mp4File.removeAttr("required");
                            }

                            //validate extension only if non empty
                            if(curVal != '' && $(el).attr('name').indexOf(fileExt) < 1){
                                return Granite.I18n.get('Incorrect file type');
                                
                            }
                             
                        }
                    });
}(Granite, jQuery));
