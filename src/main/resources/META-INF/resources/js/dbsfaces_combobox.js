dbs_combobox = function(pId) {
	$(pId).focusin(function(){
		$(pId + " > .-container > .dbs_input-data").addClass("dbs_input-data-FOCUS");
	});	

	$(pId).focusout(function(){
		$(pId + " > .-container > .dbs_input-data").removeClass("dbs_input-data-FOCUS");
	});
	
	$(pId + " > .-container > .dbs_input-data > select").keydown(function(e){
		dbsfaces.combobox.copyText(pId, this);
	});
	
	$(pId + " > .-container > .dbs_input-data > select").keyup(function(e){
		dbsfaces.combobox.copyText(pId, this);
	});	
	
	$(pId + " > .-container > .dbs_input-data > select").change(function(e){
		dbsfaces.combobox.copyText(pId, this);
	});	
}

dbsfaces.combobox = {
	copyText: function(pId, e){
		var xText = e.options[e.selectedIndex].text;
		$(pId + " > .-container > .dbs_input-data > .-data").get(0).firstChild.textContent = xText;
		return;
	}
}


