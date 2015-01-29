dbs_fileUpload = function(pId, pFileUploadServlet) {
	dbsfaces.fileUpload.setBean(pFileUploadServlet);
};

dbsfaces.fileUpload = {
	wXHR: 0,
	wFileUpload: "",
	wBtStop: "",
	wBtStart: "",
	wMessage: "",
	wFile: "",
	wFileUploadServlet: "",
		
	setBean: function(pFileUploadServlet){
		wFileUploadServlet = pFileUploadServlet;
		if (pFileUploadServlet == ""){
			dbsfaces.fileUpload.showMessageError("Caminho do fileUploadServlet não informado");
		}
	},

	select: function(e) {
		wFileUpload = $(e).closest(".dbs_fileUpload");
		wFile = wFileUpload.find("input[type='file']");
		wBtStop = wFileUpload.find("[id*='btStop']");
		wBtStart = wFileUpload.find("[id*='btStart']");
		wMessage = wFileUpload.find(".-message");
		wFile.click();
		wFile.off("change");
		wFile.on("change", dbsfaces.fileUpload.start);
		wBtStop.children('.-progress').remove();
		wBtStop.prepend("<" + "div class='-progress'/>");
		dbsfaces.fileUpload.hideMessage();
		return;
	},

	start: function() {
		var xFormdata = new FormData();
		xFormdata.append(wFile.get(0).id, wFile.get(0).files[0]);
		wXHR = new XMLHttpRequest();       
		wXHR.upload.addEventListener('loadstart', dbsfaces.fileUpload.onloadstartHandler, false);
		wXHR.upload.addEventListener('progress', dbsfaces.fileUpload.onprogressHandler, false);
		wXHR.upload.addEventListener('load', dbsfaces.fileUpload.onloadHandler, false);
		wXHR.upload.addEventListener('abort', dbsfaces.fileUpload.onabort, false);
		wXHR.upload.addEventListener('error', dbsfaces.fileUpload.onerror, false);
		wXHR.upload.addEventListener('timeout', dbsfaces.fileUpload.ontimeout, false);
		wXHR.addEventListener('readystatechange', dbsfaces.fileUpload.onreadystatechangeHandler, false);

		wXHR.open("POST", wFileUploadServlet, true);
		wXHR.send(xFormdata);
		wBtStart.hide();
		wBtStop.show();
		dbsfaces.fileUpload.hideMessage();
		return;
	},

	stop: function() {
		wXHR.abort();
		wBtStop.children('.-progress').remove();
		wFile.off("change");
		wFile.get(0).value = "";
		wBtStart.show();
		wBtStop.hide();
		return;
	},

	// Handle the start of the transmission
	onloadstartHandler: function(evt) {
//	 		var div = document.getElementById('upload-status');
//	 		div.innerHTML = 'Upload started.';
	},
	// Handle the end of the transmission
	onloadHandler: function(evt) {
		dbsfaces.fileUpload.showMessage("Finalizado com sucesso");
		dbsfaces.fileUpload.stop();
	},
	// Handle the start of the transmission
	ontimeout: function(evt) {
		dbsfaces.fileUpload.showMessageError("Erro de timeout");
		dbsfaces.fileUpload.stop();
	},
	// Handle the progress
	onprogressHandler: function(evt) {
//	 		var div = document.getElementById('progress');
//	 		div.innerHTML = 'Progress: ' + percent + '%';
		var percent = evt.loaded/evt.total*100;
		wBtStop.children('.-progress').css("height", percent + '%');
	},
	// Handle error
	onerror: function(evt) {
		dbsfaces.fileUpload.showMessageError("Erro na transmissão");
		dbsfaces.fileUpload.stop();
	},
	// Handle abort
	onabort: function(evt) {
		dbsfaces.fileUpload.showMessageError("Transferencia interrompida");
		dbsfaces.fileUpload.stop();
	},
	// Handle the response from the server
	onreadystatechangeHandler: function(evt) {
		var status, text, readyState;
		try {
			readyState = evt.target.readyState;
			text = evt.target.responseText;
			status = evt.target.status;
		}catch(e) {
			return;
		}
//	 		if (readyState == 4 && status == '200' && evt.target.responseText) {
//	 			var status = document.getElementById('upload-status');
//	 			status.innerHTML += '<' + 'br>Success!';
//	 			var result = document.getElementById('result');
//	 			result.innerHTML = '<p>The server saw it as:</p><pre>' + evt.target.responseText + '</pre>';
//	 		}
	},

	showMessage: function(pMessage) {
		wMessage.text(pMessage);
		wMessage.removeClass("-error");
		wMessage.fadeIn(200).delay(6000).fadeOut(200);
	},
	
	showMessageError: function(pMessage) {
		wMessage.text(pMessage);
		wMessage.addClass("-error");
		wMessage.fadeIn(200).delay(12000).fadeOut(200);
	},

	hideMessage: function() {
		wMessage.hide();
	}
	
};
