dbs_push = function(pId, pUrl) {
	var xH = 0;
	var xEventSource = new EventSource(pUrl);
	//Recebe evento update e faz chamada ajax para atualiza-los
	xEventSource.addEventListener('update', function(e) {
		dbsfaces_push.showUpdate(pId);
		setTimeout(function(){
			var xData = dbsfaces_push.validIds(pId, e.data);
			if (xData.length > 0){
				if ($(pId).length == 0){
					console.log(pId + " não existente.");
				}else{
					jsf.ajax.request($(pId).get(0), 'update', {render:xData, execute:'@none', onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror});
				}
			}
		}, 0);	
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
		$(pId + " > .-content").attr("class", "-i_bullet_green");
	},
	showUpdate: function(pId){
		$(pId + " > .-content").attr("class", "-i_bullet_blue");
	},
	showError: function(pId){
		$(pId + " > .-content").attr("class", "-i_bullet_red");
	},
	showClose: function(pId){
		$(pId + " > .-content").attr("class", "");
	},									

	//Retorna somente os componentes que estão momentaneamente com o push suspenso
	validIds: function(pId, pData){
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
