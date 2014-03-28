dbs_crudForm = function(pId) {
	var xH = 0;
    xH = $(pId + "> .-caption").outerHeight();
    xH = xH + $(pId + "> .-toolbar").outerHeight();
    
    $(pId).css("padding-bottom", xH + "px");
    
    /*
    
    $(pId).siblings().find('*').attr("tabindex", "-1");
    $(pId + " input").prop('disabled', 'disabled');
	$(pId + " button").prop('disabled', 'disabled');
	*/
}
