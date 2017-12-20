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
			onFocusSelectAll: false,
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
			189: 1, // MENOS
			110: 1, // VIRGULA
			188: 1, // VIRGULA
			190: 1// PONTO
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
		
		decimalKeys: {
			110: 1, // VIRGULA
			188: 1, // VIRGULA
			190: 1// PONTO
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
			this.split = null;
			this.isAndroid = dbsfaces.util.isAndroid();
			this.isMobile = dbsfaces.util.isMobile();
			this.refresh = false;
			
			if (this.isNumber()){
				this.initializeNumber();
			}else if(this.isFixed()){
				this.initializeFixed();
			}

			//Confirma valor atual
			this.pvRefresh();

//			this.input.on("touchstart.dbsmask", function(e){
//				xdbsmask.input.attr('type', 'number');
//				$("body").append("<div>1-Trocou para number</div>");
//			});
			//Eventos
			var xdbsmask = this;
			this.input.on("keydown.dbsmask", function(e){
//				$("body").append("<div>6-Down\t" + (e.which || e.keyCode) + "</div>");
//				console.log("keydowm");
				xdbsmask.onKeyDown(e);
			});
			if (this.isAndroid){
				this.input.on("input.dbsmask", function(e){
//					$("body").append("<div>Input\t" + (e) + "</div>");
					if (xdbsmask.refresh){
						xdbsmask.pvRefresh();
					}
				});
			}
			this.input.on("paste.dbsmask", function(e){
				xdbsmask.onPaste(e);
			});
			this.input.on("drop.dbsmask", function(e){
//				xdbsmask.onDrop(e);
				xdbsmask.pvIgnoreEvent(e);
			});

			this.input.on("blur.dbsmask", function(e){
//				console.log("xxblur");
				xdbsmask.pvRefresh();
			});

			this.input.on("focus.dbsmask", function(e){
//				console.log("xxfocus");
				setTimeout(function(e){
					//Posiciona no inicio do número
					if (xdbsmask.options.onFocusSelectAll){
						xdbsmask.input.select();
					}else if (xdbsmask.isMobile){
						xdbsmask.pvSetCursorPosition(0);
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
			if (this.options.decimalPlaces < 0){
				this.options.decimalPlaces = 0;
			}
			if (this.isAndroid){
				this.pvSetPhoneKeyboard();
			}
		},
		
		initializeFixed : function(){
			//Regex
			if (this.options.maskEmptyChr == ""){
				this.options.maskEmptyChr = "_";
			}
			var xRegexString = this.regex.fixed;
			this.regex = new RegExp("([^" + xRegexString + this.options.maskEmptyChr + "]+)" , "g");
			this.options.decimalPlaces = 0;
			//Ativa teclado númerico se máscara for somente númerica
			var xRegex = new RegExp("([AX]+)", "");
			if (!xRegex.test(this.options.mask)){
				this.pvSetPhoneKeyboard();
			}
		},

		pvSetPhoneKeyboard: function(){
			this.input.attr('type', 'tel');
		},
		
		onKeyDown : function(e){
			this.pvEditValue(e);
		},

//		onDrop: function(e) {
//			var xData = e.dataTransfer || e.originalEvent.dataTransfer || window.dataTransfer;
//			var xValueIn = xData.getData('text');
//			this.pvIgnoreEvent(e);
//		},

		onPaste: function(e) {
			var xData = e.clipboardData || e.originalEvent.clipboardData || window.clipboardData;
			var xValueIn = xData.getData('text');
			this.pvIgnoreEvent(e);
			
			//Se valor é válido considerando a máscara
			if (this.isFixed() 
			&& !this.pvIsValidMaskValue(xValueIn)){
				return;
			}
			if (this.isMobile){
				this.input.select();
			}
			this.pvUpdateSplit();
			this.pvUpdateValue(xValueIn);
		},

		pvEditValue: function(e){
			var xKey = e.which || e.keyCode;
			//Bypass
			if (this.bypassKeys[xKey]
			 || e.originalEvent.metaKey){return;}
			this.pvIgnoreEvent(e);
			//Caracter
			var xChar = "";
			//Se não for uam tecla de edição permitida, recuperao caracter
			if (!this.editKeys[xKey]){
				xChar = this.pvGetCharFromKey(xKey);
			}
			//Se não for um caracter válido 
			if (xChar == null){
				//Se for android, liga flag para indicar que valor deverá ser reexibido. 
				if (this.isAndroid){
					this.refresh = true;
				}
				return;
			}

			//Atualiza split
			
			this.pvUpdateSplit();
			var xPosition = 0;
			if (xKey == 8 //Backspace
			 || xKey == 46){ //Delete
				if (!this.pvIsSelected()){
					//Backspace
					if (xKey == 8){
						this.pvEditValueDeleteBegin();
						if (this.isNumber()){
						}else{
							this.pvSetCursorPosition(this.split.begin.length);
						}
					//Delete
					}else if (xKey == 46){
						this.pvEditValueDeleteEnd();
						if (this.isNumber()){
							this.pvSetCursorPosition(this.split.end.length);
						}else{
							this.pvSetCursorPosition(this.split.begin.length);
						}
					}
				}
			}else{
				if (this.isNumber()){
					if (!this.isMobile
					 && this.options.decimalPlaces > 0
					 && this.input[0].selectionStart == 0 
					 && this.input[0].selectionEnd == this.input[0].value.length){
						this.pvSetValue(xChar + dbsfaces.string.repeat("0",this.options.decimalPlaces));
						this.pvSetCursorPosition(this.options.decimalPlaces + 1);
						return;
					}else{
						this.pvEditValueNumber(xKey);
						this.pvSetCursorPosition(this.split.end.length);
					}
				}else{
					if (xKey == 38
					 || xKey == 40
					 || this.minusKeys[xKey]
					 || this.decimalKeys[xKey]
					 || !this.pvIsValidMaskValue(xChar)){
						return;
					}
					this.pvEditValueFixed(xKey);
					if (this.pvIsSelected()){
						this.pvSetCursorPosition(this.split.begin.length + 1);
					}else{
						this.pvSetCursorPosition(this.input[0].value.length - this.split.end.length);
					}
				}
			}
			this.pvUpdateValue(xChar);
		},
		
		//-----------------------------------
		pvEditValueDeleteBegin: function(){
			var xCount = 0;
			while(this.split.begin.length > 0){
				var xN = this.split.begin.length - 1;
				//Se for caracter válido
				if (this.pvIsValidValue(this.split.begin.charAt(xN))){
					xCount++;
					if (xCount == 2){
						break;
					}
				}
				//Exclui anterior
				this.split.begin = this.split.begin.substring(0, xN);
			}
		},

		pvEditValueDeleteEnd: function(){
			var xCount = 0;
			while(this.split.end.length > 0){
				//Se for caracter válido
				if (this.pvIsValidValue(this.split.end.charAt(0))){
					xCount++;
					if (xCount == 2){
						break;
					}
				}
				//Exclui próximo
				this.split.end = this.split.end.substring(1);
			}
		},
		
		//-----------------------------------
		pvEditValueNumber: function(pKey){
			//Up or Down
			if (pKey == 38
			 || pKey == 40){
				if (this.split.begin.length == 0){
					if (this.options.minValue <= 0){
						this.split.begin = 0
						this.split.beginWithoutMask = 0;
					}
				}
				var xSign = Math.sign(this.split.begin) || 1;
				if (this.split.beginWithoutMask != "-"){
					this.split.begin = dbsfaces.number.parseFloat(this.split.beginWithoutMask);
					//Down
					if (pKey == 40){
						this.split.begin--;
					//Up
					}else{
						this.split.begin++;
					}
					if (xSign == -1 && this.split.begin == 0 && this.split.endWithoutMask.length > 0){
						this.split.begin = "-";
					}else{
						this.split.begin = (this.split.begin).toString();
					}
				}
			//Menos
			}else if (this.minusKeys[pKey]){
				if (this.split.beginWithoutMask == ""){
					this.split.begin = "-";
				}else{
					this.split.begin = dbsfaces.number.parseFloat(this.split.beginWithoutMask) * -1;
				}
				this.split.begin = (this.split.begin).toString();
			}
			if (this.options.decimalPlaces > 0){
				//Posiciona no ponto de digitou tecla de pontuação
				if (this.decimalKeys[pKey]){
					this.pvSetCursorPosition(this.options.decimalPlaces);
					this.pvUpdateSplit();
					return;
				}
				//Exclui próximo caracter se extiver na porção decimal do número
				if (this.pvIsDecimalPosition()){
					this.pvEditValueDeleteEnd();
				}
			}
		},
		
		pvEditValueFixed: function(pKey){
			this.pvEditValueDeleteEnd();
		},
		

		pvUpdateValue: function(pValueIn){
			this.pvSetValue(this.pvGetValidValue(this.split.begin + pValueIn + this.split.end));
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
	        if (!this.pvIsValidValue(xChar)){
	        	xChar = null;
	        }
			return xChar;
		},
		
		pvSetValue: function(pValue){
			var xFormattedValue = this.pvGetValidValue(pValue);
			if (xFormattedValue == "-"){return;}
			if (this.isNumber()){
//				if (xFormattedValue == ""){
//					xFormattedValue = 0;
//				}
				xFormattedValue = this.pvSetValueNumber(xFormattedValue);
			}else if(this.isFixed()){
				xFormattedValue = this.pvSetValueFixed(xFormattedValue);
			}
			//Somente dispara change se houver mudança de valor or for refresh
			if (this.refresh 
			 || (xFormattedValue != null && xFormattedValue != this.oldValue)){
				if (!this.refresh){
					this.pvSaveCursorPosition();
				}
				this.input.val(xFormattedValue);
				this.input.attr("value", xFormattedValue);
				this.oldValue = xFormattedValue;
				if (this.options.parentDom != null){
					this.options.parentDom.val(xFormattedValue);
				}
				//Refresh não dispara change, pois é somente para força a reformatação
				if (!this.refresh){
					this.pvRestoreCursorPosition();
					this.input.trigger("change");
				}
				this.refresh = false;
				this.pvUpdateSplit();
			}
		},
		
		pvSetValueNumber: function(pValue){
			//Converte string com número(sem pontuação) para valor numérico
			var xFormattedValue;
			if (String(pValue) == ""){
				return "";
			}else{
				pValue = dbsfaces.number.parseFloat(pValue);
			}

			//Calcula número com as casas decimais
			if (this.options.decimalPlaces > 0){
			   pValue /= Math.pow(10, this.options.decimalPlaces);
			}
			//Se valor for superior ao máximo
			if (this.options.maxValue != null && pValue > this.options.maxValue){
				pValue = this.options.maxValue;
			}
			//Se valor for inferior ao mínimo
			if (this.options.minValue != null && pValue < this.options.minValue){
				pValue = this.options.minValue;
			}
			//Formata número
			xFormattedValue =  dbsfaces.format.number(pValue, this.options.decimalPlaces, this.options.separateThousand);
			//Se tamanho for superior ao permitido
			if (this.options.maxLength != null && xFormattedValue.length > this.options.maxLength){
				return;
			}
			//Leading zeros
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
			this.cursorPosition = this.pvGetCurrentPosition();  
		},
		
		pvRestoreCursorPosition: function(){
			if (this.cursorPosition > this.input[0].value.length){
				this.cursorPosition = this.input[0].value.length;
			}
			this.pvSetCursorPosition(this.cursorPosition);
		},

  
		pvSetCursorPosition: function(pPosition){
			if (pPosition < 0 || pPosition > this.input[0].value.length){return;}
			var xPosition = pPosition;
			var xDirection;
			if (this.isNumber()){
				xPosition = this.input[0].value.length - pPosition;
			}else{
				if (!this.pvIsValidValue(this.input[0].value.charAt(xPosition))){
					this.pvSetCursorPosition(xPosition + 1);
				}
			}
			this.input[0].selectionStart = xPosition;
			this.input[0].selectionEnd = xPosition;
//			if (this.input[0].selectionStart == 0){return;}
//			if (!this.pvIsDecimalPosition()){
//				if (!this.pvIsValidValue(this.input[0].value.charAt(this.input[0].selectionStart))){
//					this.pvSetCursorPosition(pPosition + 1);
//				}
//			}
		},
		
		pvGetCurrentPosition: function(){
			if (this.isNumber()){
				return this.input[0].value.length - this.input[0].selectionEnd;  
			}else{
				return this.input[0].selectionStart;  
			}
		},

		pvIsDecimalPosition: function(){
			if (this.options.decimalPlaces == 0){return false;}
			var xCurrentPosition = this.pvGetCurrentPosition();
			var xIsDecimalPosition = xCurrentPosition > 0 
								  && xCurrentPosition <= this.options.decimalPlaces;
			return xIsDecimalPosition;
		},

		pvIsSelected: function(){
			return this.input[0].selectionStart != this.input[0].selectionEnd; 
		},
		
		pvIgnoreEvent: function(e){
			e.preventDefault();
        	e.stopPropagation();
		},
		
		pvUpdateSplit: function(){
			this.split = {begin:null, end:null, beginWithMask:null, endWithMask:null};
			var xValue = this.input.val();
			//Recupera somente os valores
			this.split.begin = xValue.substring(0, this.input[0].selectionStart);
			this.split.end = xValue.substring(this.input[0].selectionEnd);
			this.split.beginWithoutMask = this.pvGetValidValue(this.split.begin);
			this.split.endWithoutMask = this.pvGetValidValue(this.split.end);
		},

		pvIsValidValue: function(pValue){
			this.regex.lastIndex=0;
			var xOk = !this.regex.test(pValue);
			this.regex.lastIndex=0;
			return xOk;
			
		},
		
		pvIsValidMaskValue: function(pValue){
			var xN = this.input[0].selectionStart;
			var xMaskChar;
			//Procura dentro da máscara qual o tipo de caracter que deve ser aceito na posição atual 
			while (xN < this.input[0].value.length){
				xMaskChar = this.options.mask.charAt(xN).toUpperCase();
				if (xMaskChar == "9" 
				 || xMaskChar == "A" 
				 || xMaskChar == "X"){
					break;
				}
				xN++;
			}
			//Cria regex com o caracter que pode ser aceito
			var xRegexString = "";
			if (xMaskChar == "9" 
			 || xMaskChar == "X"){
				xRegexString = "0-9";
			}
			if (xMaskChar == "A" 
			 || xMaskChar == "X"){
				xRegexString = "A-Za-z";
			}
			if (xRegexString == ""){
				return false;
			}else{
				var xRegex = new RegExp("([^" + xRegexString + "]+)", "");
				return !xRegex.test(this.pvGetValidValue(pValue));
			}
		},
		
		pvGetValidValue: function(pValue){
			this.regex.lastIndex=0;
			var xString = pValue.replace(this.regex, '');
			if (xString == "-"){
				if (this.options.minValue <= 0){
					xString = "0";
				}
			}
			this.regex.lastIndex=0;
			return xString;
		},
		
		pvRefresh: function(){
			this.refresh = true;
			this.pvSetValue(this.input[0].value);
		}
		

	};

	$.fn.dbsmask = function(pOptions){
		return new dbsmask($(this), pOptions);
	};
})(jQuery);