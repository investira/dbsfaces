dbs_sideNav = function(pId, pDefaultLocation, pWidth, pHeight) {
	var xSideNav = $(pId);
	
	//ABRIR
	$(pId +" > .-th_action").on("click", function(e){
		dbsfaces.sideNav.open(xSideNav, pDefaultLocation, pWidth, pHeight);
		//Desabilita inputs atrás do dialog
	    dbsfaces.dialog.disableBackgroundInputs(pId);
	});
	
	//FECHAR
	$(pId +" > .dbs_sidebar > .-th_action").on("click", function(e){
		dbsfaces.sideNav.close(xSideNav, pDefaultLocation);
		dbsfaces.dialog.enableForegroundInputs($("body"));
	});
	//FECHAR
	$(pId +" > .dbs_hiddennav").on("click", function(e){
		dbsfaces.sideNav.close(xSideNav, pDefaultLocation);
		dbsfaces.dialog.enableForegroundInputs($("body"));
	});
}

dbsfaces.sideNav = {
	open: function(pId, pDefaultLocation, pWidth, pHeight) {
		var xSideNav = $(pId);

		if (pDefaultLocation == 1 || pDefaultLocation == 3) {
			document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.width = pWidth;
		} else {
			document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.height = pHeight;
			if (pDefaultLocation == 2) {
				document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.top = '0px';
			} else if (pDefaultLocation == 4) {
				document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.bottom = '0px';
			}
		}
		document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.opacity = '1';
		document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_hiddennav')[0].style.width = '100vw';
		document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_hiddennav')[0].style.opacity = '0.48';
	    var xSideBarItens = document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].getElementsByClassName('dbs_div -content');
	    for(var i = 0; i < xSideBarItens.length; ++i){
	    	xSideBarItens[i].style.opacity = '1';
		}
	    var xIconeCentral = document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].getElementsByClassName('dbs_label');
	    for(var i = 0; i < xIconeCentral.length; ++i){
	    	xIconeCentral[i].style.opacity = '1';
		}
	},

	close: function(pId, pDefaultLocation) {
		var xSideNav = $(pId);
		
		if (pDefaultLocation == 1 || pDefaultLocation == 3) {
			document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.width = '0px';
		} else {
			document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.height = '0px';
			if (pDefaultLocation == 2) {
				document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.top = '-65px';
			} else if (pDefaultLocation == 4) {
				document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.bottom = '-65px';
			}
		}
		document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].style.opacity = '0';
		document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_hiddennav')[0].style.width = '0';
		document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_hiddennav')[0].style.opacity = '0';
	    var xSideBarItens = document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].getElementsByClassName('dbs_div -content');
	    for(var i = 0; i < xSideBarItens.length; ++i){
	    	xSideBarItens[i].style.opacity = '0';
		}
	    var xIconeCentral = document.getElementById(xSideNav.attr("id")).getElementsByClassName('dbs_sidebar')[0].getElementsByClassName('dbs_label');
	    for(var i = 0; i < xIconeCentral.length; ++i){
	    	xIconeCentral[i].style.opacity = '0';
		}
	}
}