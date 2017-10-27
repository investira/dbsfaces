dbs_push = function(pId, pUrl, pDelay) {
	var xH = 0;
	var xPush = $(pId);
	if (xPush.length == 0){
		console.log(pId + " não existente.");
		return;
	}
	var xEventSource = new EventSource(pUrl);
	xPush.data("icon" , xPush.children("span"));
	//Recebe evento update e faz chamada ajax para atualiza-los
	xEventSource.addEventListener('update', function(e) {
		var xData = dbsfaces_push.validIds(e.data);
		if (xData.length > 0){
//			jsf.ajax.request($(pId)[0], 'update', {render:xData, execute:'@none', onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror});
			dbsfaces.ajax.request(xPush[0].id, null, xData, dbsfaces_push.status, dbsfaces.ui.showLoadingError(pId), null, pDelay);
		}
		return false;
	}, false);

	xEventSource.addEventListener('error', function(e) {
		console.log("DBSPush Error\t" + e.data);
	}, false);

	xEventSource.addEventListener('open', function(e) {
//		console.log("open\t" + e.data);
		//Não faz nada
	}, false);

	xEventSource.addEventListener('message', function(e) {
		dbsfaces_push.showOpen(xPush.data("icon"));
//		console.log("message\t" + e.data);
	}, false);
	
}

dbsfaces_push = {
	status: function(e){
		var xDomIcon = $(e.source).data("icon");
		if (e.status == "success"){
			dbsfaces_push.showOpen(xDomIcon);
		}else if (e.status == "statechange"){
			dbsfaces_push.showUpdate(xDomIcon);
		}else{
			console.log(e.status);
		}
	},
	showOpen: function(pDomIcon){
		dbsfaces_push.pvShowIcon(pDomIcon, "-i_dot -green");
	},
	showUpdate: function(pDomIcon){
		dbsfaces_push.pvShowIcon(pDomIcon, "-i_dot -blue");
	},
	showError: function(pDomIcon){
		dbsfaces_push.pvShowIcon(pDomIcon, "-i_dot -red");
	},
	showClose: function(pDomIcon){
		dbsfaces_push.pvShowIcon(pDomIcon, "");
	},									
	
	pvShowIcon: function(pDomIcon, pClass){
		if (pDomIcon.length > 0){
			pDomIcon.attr("class", pClass);
		}
	},

	//Retorna somente os componentes que estão momentaneamente com o push suspenso
	validIds: function(pData){
		//Componentes com class -pushDisabled, serão ignorados pelo push 
		$(".-pushDisabled").each(function(index) {
			  pData = pDatas.replaceAll($(this).attr("id"),"");
		});
		//Cria lista somente com os componentes que existem na tela
		//para evitar requests desnecessários
		var xIds = pData.split(/[\s,]+/);
		var xData = "";
		for (var i = 0; i < xIds.length; i++) {
			//Verica se componente existe na tela
		    if ($(dbsfaces.util.jsid(xIds[i])).length != 0){
		    	//Adiciona a lista se existir
		    	xData += xIds[i].replace(/^(:)/,"") + " ";
		    }
		}
		return xData.trim();
	}									
}
