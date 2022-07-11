(function ($window, $document, Granite, $) {
    "use strict";
    $document.on("dialog-ready", function () {
        // if there is already an inital value make sure the according target element becomes visible
        setTimeout(function(){
            $(".cq-insights-check-showhide").each( function() {
                showHide($(this));
            });
        }, 500)
    });

    $document.on("change", ".cq-insights-check-showhide", function(e) {
        showHide($(this));
    });

    $document.on('click', 'button[coral-multifield-add]', function (e) {
        setTimeout(function(){
            $(".cq-insights-check-showhide").each( function() {
                showHide($(this));
            });
        }, 500);
    });

    function showHide(el){
        // get the selector to find the target elements. its stored as data-.. attribute
        var target = el.data("cqInsightsCheckShowhideTarget");
        var elementIndex = el.closest('coral-multifield-item').index();

        // get the selected value
        var checked = el.prop('checked');
        var value = checked ? el.val() : '';
        $(target).each(function(index){
            var tarIndex = $(this).closest('coral-multifield-item').index();
            if (elementIndex == tarIndex) {
                $(this).not(".hide").addClass("hide");
                $(this).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");
            }
        })
   }

})($(window), $(document), Granite, $);