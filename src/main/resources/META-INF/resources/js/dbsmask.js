(function($){
	var $chk = function(obj){
		return !!(obj || obj === 0);
	};

	var dbsmask = function(){
		this.initialize.apply(this, arguments);
	};
	
	dbsmask.prototype = {

		options: {
			parentDom: null,
			type: null,
			mask: null, 
			maskEmptyChr: "",
			decimalPlaces: 2, 
			separateThousand: true,
			maxLength: null,
			minValue: null,
			maxValue: null
		},
		
		editKeys : {
			 8 : 1, // backspace
			40 : 1, // down
			38 : 1, // up
			46 : 1, // delete
			109: 1, // MENOS
			173: 1, // MENOS
			189: 1 // MENOS
		},

		bypassKeys : {
			 9 : 1, // tab
			13 : 1, // enter
			35 : 1, // end
			36 : 1, // home
			37 : 1, // left
			39 : 1, // right
			91 : 1 // command (mac)
		},
		
		minusKeys: {
			109: 1, // MENOS
			173: 1, // MENOS
			189: 1 // MENOS
		},
		
//		regexNumber: "0-9";
		regex: {
			number: "0-9-",
			fixed: "0-9A-Za-z",
		},

		initialize: function(pInput, pOptions) {
			//Inicializa valores locais
			this.options = $.extend({}, this.options, this.options[pOptions.type] || {}, pOptions);
			this.input = pInput;
			this.oldValue = null;
			this.cursorPosition = null;
			
			if (this.isNumber()){
				this.initializeNumber();
			}else if(this.isFixed()){
				this.initializeFixed();
			}

			//Valor atual
			this.pvSetValue(this.input.val());

			//Eventos
			var xdbsmask = this;
			this.input.on("keydown.dbsmask", function(e){
				xdbsmask.onKeyDown(e);
			});
			this.input.on("paste.dbsmask", function(e){
				xdbsmask.onPaste(e);
			});
			this.input.on("drop.dbsmask", function(e){
//				xdbsmask.onDrop(e);
				xdbsmask.pvIgnoreEvent(e);
			});
			this.input.on("focus.dbsmask", function(e){
				setTimeout(function(e){
					//Posiciona no inicio do número
					if (dbsfaces.util.isMobile()){
						xdbsmask.pvSetCursorPosition(0);
					}else{
						dbsfaces.ui.selectAll(xdbsmask.input);
					}
				},1);
			});
		},

		isNumber : function(){ return this.options.type == 'number'; },
		isFixed  : function(){ return this.options.type == 'fixed'; },
		
		initializeNumber : function(){
			//Regex
			var xRegexString = this.regex.number;
			if (this.options.minValue < 0){
				xRegexString += "-";
			}
			this.regex = new RegExp("([^" + xRegexString + this.options.maskEmptyChr + "]+)" , "g");
		},
		
		initializeFixed : function(){
			//Regex
			if (this.options.maskEmptyChr == ""){
				this.options.maskEmptyChr = "_";
			}
			var xRegexString = this.regex.fixed;
			this.regex = new RegExp("([^" + xRegexString + this.options.maskEmptyChr + "]+)" , "g");
		},

		onKeyDown : function(e){
			this.pvEditValue(e);
		},

//		onDrop: function(e) {
//			var xData = e.dataTransfer || e.originalEvent.dataTransfer || window.dataTransfer;
//			var xValueIn = xData.getData('text');
//			this.pvIgnoreEvent(e);
//			if (!this.pvIsMaskValidKey(xValueIn)){
//				return;
//			}
//			var xSplit = this.pvGetSplitValue();
//			this.pvUpdateValue(xSplit, xValueIn);
//		},

		onPaste: function(e) {
			var xData = e.clipboardData || e.originalEvent.clipboardData || window.clipboardData;
			var xValueIn = xData.getData('text');
			this.pvIgnoreEvent(e);
			if (!this.pvIsMaskValidKey(xValueIn)){
				return;
			}
			var xSplit = this.pvGetSplitValue();
			this.pvUpdateValue(xSplit, xValueIn);
		},

		pvEditValue: function(e){
			var xKey = e.which || e.keyCode;
			//Bypass
			if (this.bypassKeys[xKey]
			 || e.originalEvent.metaKey){return;}
			this.pvIgnoreEvent(e);
			//Caracter
			var xChar = "";
			if (!this.editKeys[xKey]){
				xChar = this.pvGetCharFromKey(xKey);
			}
			if (xChar == null){return;}


			var xSplit = this.pvGetSplitValue();
			//Backspace
			if (xKey == 8){
				this.pvEditValueDeletePrevious(xSplit);
			//Delete
			}else if (xKey == 46){
				this.pvEditValueDeleteNext(xSplit);
			}
			
			if (this.isNumber()){
				this.pvEditValueNumber(xSplit, xKey);
			}else{
				this.pvSetCursorPosition(this.input[0].selectionStart);
				if (!this.pvIsMaskValidKey(xChar)){
					return;
				}
				this.pvEditValueFixed(xSplit, xKey);
			}
			
			this.pvUpdateValue(xSplit, xChar);
		},

		//-----------------------------------
		pvEditValueNumber: function(pSplit, pKey){
			//Up or Down
			if ((pKey == 38
			  || pKey == 40)
			  && pSplit.begin.length > 0){
				pSplit.begin = dbsfaces.number.parseFloat(pSplit.beginWithoutMask);
				//Down
				if (pKey == 40){
					pSplit.begin--;
				//Up
				}else{
					pSplit.begin++;
				}
				pSplit.begin = (pSplit.begin).toString();
			//Menos
			}else if (this.minusKeys[pKey]){
				pSplit.begin = dbsfaces.number.parseFloat(pSplit.beginWithoutMask) * -1;
				pSplit.begin = (pSplit.begin).toString();
			}
			this.pvSetCursorPosition(pSplit.end.length);
		},
		
		pvEditValueFixed: function(pSplit, pKey){
			if (pKey == 38
			 || pKey == 40
			 || this.minusKeys[pKey]){
				return;
			}else if (pKey == 8//Backspace
			       || pKey == 46){//Delete
				this.pvSetCursorPosition(pSplit.begin.length);
			}else{
				if (!this.pvIsSelected()){
					this.pvEditValueDeleteNext(pSplit);
				}else{
					this.pvSetCursorPosition(this.input[0].selectionStart);
				}
				//Pula pra o próximo campo
				this.pvSetCursorPosition(this.input[0].selectionStart + 1);
			}
		},
		
		pvIsMaskValidKey: function(pKey){
			if (this.isNumber()){return true;}
			var xN = this.input[0].selectionStart;
			var xMaskChar;
			while (xN < this.input[0].value.length){
				xMaskChar = this.options.mask.charAt(xN).toUpperCase();
				if (xMaskChar == "9" || xMaskChar == "A" || xMaskChar == "X"){
					break;
				}
				xN++;
			}
			var xRegexString = "";
			if (xMaskChar == "9" || xMaskChar == "X"){
				xRegexString = "0-9";
			}else if (xMaskChar == "A" || xMaskChar == "X"){
				xRegexString = "A-Za-z";
			}else{
				return true;
			}
			var xRegex = new RegExp("([^" + xRegexString + "]+)", "");
			return !xRegex.test(this.pvGetValueWithoutMask(pKey));
		},
		
		//-----------------------------------
		pvEditValueDeletePrevious: function(pSplit){
			var xCount = 0;
			while(pSplit.begin.length > 0){
				var xN = pSplit.begin.length - 1;
				if (this.pvGetValueWithoutMask(pSplit.begin.charAt(xN)) != ""){
					xCount++;
					if (xCount == 2){
						break;
					}
				}
				//Exclui anterior
				pSplit.begin = pSplit.begin.substring(0, xN);
			}
		},

		pvEditValueDeleteNext: function(pSplit){
			var xCount = 0;
			while(pSplit.end.length > 0){
				if (this.pvGetValueWithoutMask(pSplit.end.charAt(0)) != ""){
					xCount++;
					if (xCount == 2){
						break;
					}
				}
				//Exclui anterior
				pSplit.end = pSplit.end.substring(1);
			}
		},

		pvUpdateValue: function(pSplitValue, pValueIn){
			this.pvSetValue(this.pvGetValueWithoutMask(pSplitValue.begin + pValueIn + pSplitValue.end));
		},
		

		pvGetCharFromKey: function(pKey){
			var xChar = "";
			//Shift number-pad 
			if (this.minusKeys[pKey]){
				xChar = "-"
			}else{
				if (pKey >= 96 && pKey <= 105){
					pKey -= 48;
				}
				xChar = String.fromCharCode(pKey);
			}    
			//Se não for caracter permitido
	        if (this.pvGetValueWithoutMask(xChar) == ""){
	        	xChar = null;
	        }
			return xChar;
		},
		
		pvSetValue: function(pValue){
			var xFormattedValue = this.pvGetValueWithoutMask(pValue);
			if (this.isNumber()){
				xFormattedValue = this.pvSetValueNumber(xFormattedValue);
			}else if(this.isFixed()){
				xFormattedValue = this.pvSetValueFixed(xFormattedValue);
			}
			if (xFormattedValue == null){return;}
			//Somente dispara change se houver mudança de valor
			if (xFormattedValue != this.oldValue){
				this.pvSaveCursorPosition();
				this.input.val(xFormattedValue);
				this.input.attr("value", xFormattedValue);
				this.pvRestoreCursorPosition();
				this.oldValue = xFormattedValue;
				if (this.options.parentDom != null){
					this.options.parentDom.val(xFormattedValue);
				}
				this.input.trigger("change");
			}
		},

		pvSetValueNumber: function(pValue){
			var xFormattedValue = "";
			//Converte string com número(sem pontuação) para valor numérico
			if (pValue == ""){
				pValue = 0;
			}else{
				pValue = dbsfaces.number.parseFloat(pValue);
			}
			//Calcula número com as casas decimais
			if (this.options.decimalPlaces > 0){
				pValue /= Math.pow(10, this.options.decimalPlaces);
			}
			xFormattedValue = dbsfaces.format.number(pValue, this.options.decimalPlaces, this.options.separateThousand);
			if (this.options.maxLength != null && xFormattedValue.length > this.options.maxLength){
				return;
			}
			if (this.options.minValue != null && pValue < this.options.minValue){
				return;
			}
			if (this.options.maxValue != null && pValue > this.options.maxValue){
				return;
			}
			if (!this.options.separateThousand && this.options.maskEmptyChr != ""){
				var xZeros = dbsfaces.string.repeat(this.options.maskEmptyChr, this.options.maxLength - xFormattedValue.length);
				xFormattedValue = xZeros + xFormattedValue;  
			}
			return xFormattedValue;
		},
		
		pvSetValueFixed: function(pValue){
			var xFormattedValue = "";
			xFormattedValue = dbsfaces.format.mask(pValue, this.options.mask, this.options.maskEmptyChr);
			return xFormattedValue;
		},

		pvSaveCursorPosition: function(){
			if (this.isNumber()){
				this.cursorPosition = this.input[0].value.length - this.input[0].selectionEnd;  
			}else{
				this.cursorPosition = this.input[0].selectionStart;  
			}
		},
		
		pvRestoreCursorPosition: function(){
			if (this.cursorPosition > this.input[0].value.length){
				this.cursorPosition = this.input[0].value.length;
			}
			if (this.isNumber()){
				this.input[0].selectionStart = this.input[0].value.length - this.cursorPosition;
			}else{
				this.input[0].selectionStart = this.cursorPosition;
			}
			this.input[0].selectionEnd = this.input[0].selectionStart;
		},


		pvSetCursorPosition: function(pPosition){
			if (pPosition < 0 || pPosition > this.input[0].value.length){return;}
			if (this.isNumber()){
				this.input[0].selectionStart = this.input[0].value.length - pPosition;
			}else{
				this.input[0].selectionStart = pPosition;
			}
			this.input[0].selectionEnd = this.input[0].selectionStart;
			if (this.pvGetValueWithoutMask(this.input[0].value.charAt(this.input[0].selectionStart)) == ""){
				if (this.isNumber()){
					this.pvSetCursorPosition(pPosition - 1);
				}else{
					this.pvSetCursorPosition(pPosition + 1);
				}
			}
		},
		
		pvIsSelected: function(){
			return this.input[0].selectionStart != this.input[0].selectionEnd; 
			
		},
		
		pvIgnoreEvent: function(e){
			e.preventDefault();
        	e.stopPropagation();
		},
		
		pvGetSplitValue: function(){
			var xSplit = {begin:null, end:null, beginWithMask:null, endWithMask:null};
			var xValue = this.input.val();
			//Recupera somente os valores
			xSplit.begin = xValue.substring(0, this.input[0].selectionStart);
			xSplit.end = xValue.substring(this.input[0].selectionEnd);
			xSplit.beginWithoutMask = this.pvGetValueWithoutMask(xSplit.begin);
			xSplit.endWithoutMask = this.pvGetValueWithoutMask(xSplit.end);
			return xSplit;
		},
		
		pvGetValueWithoutMask: function(pValue){
			this.pvResetRegex();
			var xString = pValue.replace(this.regex, '');
			this.pvResetRegex();
			return xString;
		},
		
		pvResetRegex: function(){
			this.regex.lastIndex=0;
//			this.regexError.lastIndex=0;
		}
		

	};

	$.fn.dbsmask = function(pOptions){
		return new dbsmask($(this), pOptions);
	};
})(jQuery);