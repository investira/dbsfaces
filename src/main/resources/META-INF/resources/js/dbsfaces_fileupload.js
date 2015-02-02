dbs_fileUpload = function(pId, pFileUploadServlet) {
	var wFile = $(pId).find("input[type='file']");
	var wBtCancel = $(pId).find("[id*='btCancel']");
	var wBtStart = $(pId).find("[id*='btStart']");
	var wMessage = $(pId).find(".-message");
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
		var xFormdata = new FormData();
		
		for (var i = 0; i < wFile.get(0).files.length; i++) {
			xFormdata.append(wFile.get(0).files[i].name, wFile.get(0).files[i]);
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
		hideMessage();
	};

	function cancel() {
		//Artificio utlizado para cancelar, pois o abort interrompia todos upload
		wXHR.open("POST", pFileUploadServlet, true);
		reset();
	};
	
	function reset() {
		wBtCancel.children('.-progress').remove();
		wFile.get(0).value = "";
		wBtStart.show();
		wBtCancel.hide();
	};
	
	function onloadstartHandler(evt) {
	};
	
	function onloadHandler(evt) {
		showMessage("Finalizado com sucesso");
		reset();
	};
	
	function ontimeout(evt) {
		showMessageError("Erro de timeout");
		reset();
	};

	function onprogressHandler(evt) {
		var percent = Math.round(evt.loaded/evt.total*100);
		wBtCancel.children('.-progress').css("height", percent + '%');
	};

	function onerror(evt) {
		showMessageError("Erro na transmissão");
		reset();
	};

	function onabort(evt) {
		showMessageError("Transferencia interrompida");
		reset();
	};

	function onreadystatechangeHandler(evt) {
		var status, text, readyState;
		try {
			readyState = evt.target.readyState;
			text = evt.target.responseText;
			status = evt.target.status;
			statusText = evt.target.statusText;
//			console.log("------------StateChange");
//			console.log(readyState);
//			console.log(text);
//			console.log(status);
//			console.log(statusText);
		}catch(e) {
			return;
		}
 		if (readyState == 4 && status != '200' && statusText) {
 			showMessageError(statusText);
 		}
	};

	function showMessage(pMessage) {
		wMessage.text(pMessage);
		wMessage.removeClass("-error");
		wMessage.fadeIn(200).delay(6000).fadeOut(200);
	};
	
	function showMessageError(pMessage) {
		wMessage.text(pMessage);
		wMessage.addClass("-error");
		wMessage.fadeIn(200).delay(6000).fadeOut(200);
	};

	function hideMessage() {
		wMessage.hide();
	};
		

};

