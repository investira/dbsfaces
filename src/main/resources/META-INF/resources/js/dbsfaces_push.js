dbs_push = function(pId, pUrl) {
	var xH = 0;
	var xEventSource = new EventSource(pUrl);

	xEventSource.addEventListener('update', function(e) {
		dbsfaces_push.showUpdate(pId);
		var xData = dbsfaces_push.validIds(pId, e.data);
		jsf.ajax.request($(pId), 'update', {render:xData, execute:'@none', onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror});
		return false;
	}, false);

	xEventSource.addEventListener('error', function(e) {
		//Não faz nada
	}, false);

	xEventSource.addEventListener('message', function(e) {
		//Não faz nada
	}, false);
	
}

dbsfaces_push = {
	showOpen: function(pId){
		$(pId).attr("class", "dbs_icon -i_bullet_green");
	},
	showUpdate: function(pId){
		$(pId).attr("class", "dbs_icon -i_bullet_blue");
	},
	showError: function(pId){
		$(pId).attr("class", "dbs_icon -i_bullet_red");
	},
	showClose: function(pId){
		$(pId).attr("class", "");
	},									

	//Retorna somente os componentes que estão momentaneamente com o push suspenso
	validIds: function(pId, pData){
		//Componentes com class -pushSuspended, serão ignorados pelo push 
		$(".-pushDisabled").each(function(index) {
			  pData = pDatas.replaceAll($(this).attr("id"),"");
		});
		return pData;
	}									
}
