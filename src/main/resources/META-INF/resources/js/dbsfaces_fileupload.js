dbs_fileUpload = function(pId, pFileUploadServlet) {
	var wFile = $(pId).find("input[type='file']");
	var wBtCancel = $(pId).find("[id$='_btCancel']");
	var wBtStart = $(pId).find("[id$='_btStart']");
	var wMessage = $(pId).find(".-message");
	var wFilesWithError = new Array();
	var wXHR = 0;
	
	wFile.on("change.fileUpload", function(e){
		start(e);
	});
	wBtCancel.on("click.fileUpload", function(e){
		cancel();
	});

	wBtStart.on("click.fileUpload", function(e){
		reset();
		wBtCancel.children('.-progress').remove();
		wBtCancel.prepend("<" + "div class='-progress'/>");
		hideMessage();
		wFile.click();
	});

	function start(e){
		if (wFile.get(0).value == ""){return;}
		hideMessage();
		wFilesWithError.length = 0;
		var xFormdata = new FormData();
		var xLimite = wFile.attr("maxSize");
		if (typeof(xLimite) != 'undefined'){
			xLimite = dbsfaces.number.sizeInBytes(xLimite);
		}else{
			xLimite = -1;
		}
		
		for (var i = 0; i < wFile.get(0).files.length; i++){
			if (xLimite < 0 
			 || wFile.get(0).files[i].size <= xLimite){
				xFormdata.append(wFile.get(0).files[i].name, wFile.get(0).files[i]);
			}else{
				wFilesWithError.push(wFile.get(0).files[i].name);
			}
		}

		wXHR = new XMLHttpRequest();       
		wXHR.upload.addEventListener('loadstart', onloadstartHandler, false);
		wXHR.upload.addEventListener('progress', onprogressHandler, false);
		wXHR.upload.addEventListener('load', onloadHandler, false);
		wXHR.upload.addEventListener('abort', onabort, false);
		wXHR.upload.addEventListener('error', onerror, false);
		wXHR.upload.addEventListener('timeout', ontimeout, false);
		wXHR.addEventListener('readystatechange', onreadystatechangeHandler, false);

		wXHR.open("POST", pFileUploadServlet, true);
		wXHR.send(xFormdata);
		wBtStart.hide();
		wBtCancel.show();
	};

	function cancel() {
//		console.log("UPLOAD CANCEL");
		//Artificio utlizado para cancelar, pois o abort interrompia todos upload
		wXHR.open("POST", pFileUploadServlet, true);
		reset();
		showMessageError("Upload cancelado!");
		$(pId).trigger("cancel");

	};
	
	function reset() {
//		console.log("UPLOAD RESET");
		wBtCancel.children('.-progress').remove();
		wBtCancel.removeClass("-disabled");
		wFile.get(0).value = "";
		wBtStart.show();
		wBtCancel.hide();
	};
	
	function onloadstartHandler(evt) {
//		console.log("UPLOAD START");
		wBtCancel.hide();
	};
	
	function onloadHandler(evt) {
		wBtCancel.addClass("-disabled");
//		console.log("UPLOAD LOAD");
	};
	
	function ontimeout(evt) {
//		console.log("UPLOAD TIMEOUT");
		showMessageError("Erro de timeout");
		reset();
		$(pId).trigger("timeout");
	};

	function onprogressHandler(evt) {
		var percent = Math.round(evt.loaded/evt.total*100);
		wBtCancel.children('.-progress').css("height", percent + '%');
	};

	function onerror(evt) {
//		console.log("UPLOAD ERROR");
		showMessageError("Erro na transmissão");
		reset();
		$(pId).trigger("error");
	};

	function onabort(evt) {
//		console.log("UPLOAD ABORT");
		showMessageError("Transferencia interrompida");
		reset();
		$(pId).trigger("abort");
	};

	function onreadystatechangeHandler(evt) {
		var status, text, readyState;
		try {
			readyState = evt.target.readyState;
//			text = evt.target.responseText;
			status = evt.target.status;
			statusText = evt.target.statusText;
//			console.log("------------StateChange");
//			console.log(readyState);
////			console.log(text);
//			console.log(status);
//			console.log(statusText);
		}catch(e) {
			return;
		}
		if (readyState == 4){
			if (status != '200'){
	 			showMessageError(statusText);
			}else{
				statusText = "Upload finalizado";
				if (wFilesWithError.length == 0){
					statusText += " com sucesso!";
				} else{
					if (wFilesWithError.length == 1){ 
						statusText += "! O arquivo " + wFilesWithError[0] + " não foi baixado por exceder";
					}else{
						statusText += "! Os arquivos ";
						for (i = 0; i < wFilesWithError.length; i++){
							if (i !=0){
								statusText += ",";
							}
							statusText += wFilesWithError[i];
						}
						statusText += " não foram baixados por excederem";
					}
					statusText += " o tamanho máximo permitido."; 
				}
	 			showMessage(statusText);
			}
			reset();
			$(pId).trigger("load");
		}
// 		console.log("UPLOAD READY STATE CHANGE");
	};

	function showMessage(pMessage) {
		wMessage.text(pMessage);
		wMessage.removeClass("-error");
		wMessage.fadeIn(200).delay(dbsfaces.ui.getDelayFromTextLength(pMessage)).fadeOut(200);
	};
	
	function showMessageError(pMessage) {
		wMessage.text(pMessage);
		wMessage.addClass("-error");
		wMessage.fadeIn(200).delay(dbsfaces.ui.getDelayFromTextLength(pMessage)).fadeOut(200);
	};

	function hideMessage() {
		wMessage.hide();
	};
	
};

