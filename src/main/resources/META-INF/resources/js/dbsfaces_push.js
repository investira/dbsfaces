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
		var xData = dbsfaces_push.validIds(xPush, e.data);
		if (xData.length > 0){
			dbsfaces.ajax.request(xPush[0].id, null, xData, dbsfaces_push.status, dbsfaces_push.status, null, pDelay);
		}
		return false;
	}, false);

	xEventSource.addEventListener('error', function(e) {
		dbsfaces_push.state(xPush, this.readyState);
//		console.log("DBSPush Error\t" + e.data);
	}, false);

	xEventSource.addEventListener('open', function(e) {
		dbsfaces_push.state(xPush, this.readyState);
//		console.log("open\t" + e.data);
	}, false);

	xEventSource.addEventListener('message', function(e) {
		dbsfaces_push.state(xPush, this.readyState);
//		console.log("message\t" + e.data);
	}, false);
	
}

dbsfaces_push = {
	state: function(pPush, pReadyState){
		var xDomIcon = pPush.data("icon");
		if (pReadyState == 0){
			dbsfaces_push.showConnecting(xDomIcon);
		}else if (pReadyState == 1){
			dbsfaces_push.showOpen(xDomIcon);
		}else if (pReadyState == 2){
			dbsfaces_push.showClosed(xDomIcon);
		}
	},
	status: function(e){
		var xDomIcon = $(e.source).data("icon");
		if (e.status == "success"){
			dbsfaces_push.showOpen(xDomIcon);
		}else if (e.status == "statechange"){
			dbsfaces_push.showUpdate(xDomIcon);
		}else{
			dbsfaces_push.showError(xDomIcon);
			console.log(e.status);
		}
	},
	showConnecting: function(pDomIcon){
		dbsfaces_push.pvShowIcon(pDomIcon, "-i_dot -yellow");
	},
	showOpen: function(pDomIcon){
		dbsfaces_push.pvShowIcon(pDomIcon, "-i_dot -green");
	},
	showClosed: function(pDomIcon){
		dbsfaces_push.pvShowIcon(pDomIcon, "-i_dot -white");
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
	validIds: function(pPush, pData){
		//Componentes com class -pushDisabled, serão ignorados pelo push 
		$(".-pushDisabled").each(function(index) {
			  pData = pDatas.replaceAll($(this).attr("id"),"").toLowerCase();
		});
		var xPushId = pPush[0].id.toLowerCase();
		//Cria lista somente com os componentes que existem na tela
		//para evitar requests desnecessários
		
		var xIds = pData.split(/[\s,]+/);
		var xData = "";
		for (var i = 0; i < xIds.length; i++) {
			var xId = xIds[i];
			//Ignora o próprio componente push
			if (xId.toLowerCase() != xPushId){
				//Verica se componente existe na tela
			    if ($(dbsfaces.util.jsid(xId)).length != 0){
			    	//Adiciona a lista se existir
			    	xData += xIds[i].replace(/^(:)/,"") + " ";
			    }
			}
		}
		return xData.trim();
	}									
}
