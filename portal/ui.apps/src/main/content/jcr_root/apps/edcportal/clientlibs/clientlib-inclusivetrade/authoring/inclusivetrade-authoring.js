(function ($window, $document, Granite, $) {
    "use strict";
    $document.on("foundation-contentloaded", function () {
        // if there is already an inital value make sure the according target element becomes visible
        setTimeout(function(){
            $(".cq-hastooltip-check-showhide").each( function() {
                showHide($(this));
            });
            $(".cq-opensquestion-select-showhide").each( function() {
                showHideQuestion($(this));
            });
        }, 500)
    });

    $document.on("change", ".cq-hastooltip-check-showhide", function(e) {
        showHide($(this));
    });

    $document.on("change", ".cq-opensquestion-select-showhide", function(e) {
        showHideQuestion($(this));
    });

    $document.on('click', 'button[coral-multifield-add]', function (e) {
        setTimeout(function(){
            $(".cq-hastooltip-check-showhide").each( function() {
                showHide($(this));
            });
            $(".cq-opensquestion-select-showhide").each( function() {
                showHideQuestion($(this));
            });
        }, 500);
    });

    function showHide(el){
        // get the selector to find the target elements. its stored as data-.. attribute
        var target = el.data("cqHastooltipCheckShowhideTarget");
        showHideEach(el, target);
   }

   function showHideQuestion(el){
        // get the selector to find the target elements. its stored as data-.. attribute
        var target = el.data("cqOpensquestionSelectShowhideTarget");
        showHideEach(el, target);
    }

    function showHideEach(el, target){
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