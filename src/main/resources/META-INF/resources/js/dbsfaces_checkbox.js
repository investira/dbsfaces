dbs_checkbox = function(pId) {
	$(pId).off('click.checkbox')
		  .on('click.checkbox', function(e){
		$(e.target).focus();
	});
}

dbsfaces.checkbox = {
}


