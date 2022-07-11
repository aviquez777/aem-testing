(function ($, $document) {
    'use strict';

    function getDialog() {
        var $form = $('form.cq-dialog'),
            resourceType = $form.find('[name="./sling:resourceType"]').val(),
            index,
            data = null;

        if (!$form.length || !resourceType) {
            $form = $('form#cq-sites-properties-form');
            resourceType = $form.find('foundation-autocomplete[name="./cq:tags"]').data('cq-ui-tagfield-create-action') || '';
            index = resourceType.indexOf('/cq:dialog');

            if (index < 0) {
                index = resourceType.indexOf('/cq%3adialog');
            }

            if (index >= 0) {
                resourceType = resourceType.substring(0, index);
            }

            resourceType = resourceType.replace('/mnt/override/apps/', '');
        }

        if ($form.length && resourceType) {
            data = {
                $form: $form,
                resourceType: resourceType
            };
        }

        return data;
    }

    $.fn.onDialogReady = function (resourceType, callback) {
        $document.ready(function () {
            var dialog = getDialog(),
                event;

            if (dialog && dialog.$form.length && dialog.resourceType === resourceType) {
                event = $.Event('dialog-ready', dialog);
                $document.trigger(event);
            }
        });

        $document.on('dialog-ready', function (e) {
            var dialog = (e.$form && e.$form.length && e.resourceType) ? {
                    $form: e.$form,
                    resourceType: e.resourceType
                } : getDialog();

            if (dialog && dialog.$form.length && dialog.resourceType === resourceType) {
                callback(e, dialog.$form);
            }
        });
    };
})($, $(document));
