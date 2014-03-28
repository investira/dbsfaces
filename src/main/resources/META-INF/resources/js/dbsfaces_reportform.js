dbs_reportForm = function(pId) {
	var xH = 0;
    /*ajuste para considerar a altura do filtro e do tool bar*/
    xH = $(pId + "> .-header").outerHeight();
    $(pId + "> .-content").css("margin-bottom", "-" + xH + "px")
                          .css("padding-bottom", xH + "px");
}

