(function($){
	var $chk = function(obj){
		return !!(obj || obj === 0);
	};

	var dbsmask = function(){
		this.initialize.apply(this, arguments);
	};

	dbsmask.prototype = {
		options: {
			maskEmptyChr   : ' ',

			validNumbers   : "1234567890",
			validAlphas    : "abcdefghijklmnopqrstuvwxyz",
			validAlphaNums : "abcdefghijklmnopqrstuvwxyz1234567890",

			groupDigits    : 3,
			decDigits      : 2,
			currencySymbol : '',
			groupSymbol    : ',',
			decSymbol      : '.',
			showMask       : true,
			stripMask      : false,

			lastFocus      : 0,
			oldValue	   : '',

			number : {
				stripMask : false,
				showMask  : false
			}
		},

		initialize: function(node, options) {
			this.node    = node;
			this.domNode = node[0];
			this.options = $.extend({}, this.options, this.options[options.type] || {}, options);
			var self     = this;

			this.node
//				.bind( "mousedown click", function(ev){ ev.stopPropagation(); ev.preventDefault(); } )
				.bind( "mouseup",  function(){ self.onMouseUp .apply(self, arguments); } )
				.bind( "keypress", function(){ self.onKeyPress.apply(self, arguments); } )
				.bind( "keydown",  function(){ self.onKeyDown .apply(self, arguments); } )
				.bind( "focus",    function(){ self.onFocus   .apply(self, arguments); } )
				.bind( "blur",     function(){ self.onBlur    .apply(self, arguments); } );
		},

		isFixed  : function(){ return this.options.type == 'fixed';  },
		isNumber : function(){ return this.options.type == 'number'; },

		onMouseUp: function( ev ) {
			ev.stopPropagation();
			ev.preventDefault();
//
			if( this.isFixed() ) {
				var p = this.getSelectionStart();
				this.setSelection(p, (p + 1));
			} else if(this.isNumber() ) {
//				this.setEnd();
			}
		},

		onKeyDown: function(ev) {
			if (ev.dbs){return;}
			if (ev.altKey && ev.which == 18){ //Option + Delete
				ev.preventDefault(); 
				return;
			}else if(ev.ctrlKey || ev.altKey || ev.metaKey) {
				return;

			} else if(ev.which == 13) { // enter
//				this.node.blur(); Comentado em 6/ago/2013 para evitar salto do campo e o submit
//				this.submitForm(this.node);

			} else if(!(ev.which == 9)) { // se não tab
				if(this.options.type == "fixed") {
					ev.preventDefault();

					var p = this.getSelectionStart();
					switch(ev.which) {
						case 8: // Backspace
							this.updateSelection( this.options.maskEmptyChr );
							this.selectPrevious();
							break;
						case 36: // Home
							this.selectFirst();
							break;
						case 35: // End
							this.selectLast();
							break;
						case 37: // Left
						case 38: // Up
							this.selectPrevious();
							break;
						case 39: // Right
						case 40: // Down
							this.selectNext();
							break;
						case 46: // Delete
							this.updateSelection( this.options.maskEmptyChr );
							this.selectNext();
							break;
						case 109: // MENOS
						case 173: // MENOS
						case 189: // MENOS
							this.setSignal(ev);
							break;
						default:
							var chr = this.chrFromEv(ev);
							if( this.isViableInput( p, chr ) ) {
								this.updateSelection( ev.shiftKey ? chr.toUpperCase() : chr );
								this.node.trigger("valid", ev, this.node);
								this.selectNext();
							} else {
								this.node.trigger("invalid", ev, this.node);
							}
							break;
					}
				} else if(this.options.type == "number") {
					switch(ev.which) {
						case 16: // END
						case 35: // END
						case 36: // HOME
						case 37: // LEFT
						case 38: // UP
						case 39: // RIGHT
						case 40: // DOWN
							break;
						case 8:  // backspace
						case 46: // delete
							var xStart = this.getSelectionStart();
							var xEnd = this.getSelectionEnd();
							if (ev.which == 8){ //Se for backspace
								//Se não houver seleção manual
								if (xStart == xEnd){
									//Se caracter anterior for pontuação, inclui seleção da pontuação para ser excluido também
									var xChar = this.domNode.value.charAt(xStart - 1);
									if (xChar == this.options.groupSymbol){
										xStart-=2;
										this.setSelection(xStart, xEnd);
									}else if (xChar == this.options.decSymbol){
										xStart-=1;
										this.setSelection(xStart, xStart);
									}
								}
							}
							//Inclui zeros a direita se estiver na digitação das casas decimais
							if (this.isInputDecimals(ev)){
								var xZeros = "0";
								for(var len = this.getSelectionStart() + 1, i = this.getSelectionEnd(); len < i; len++) {
									xZeros += "0";
								}
								this.domNode.value += xZeros;
								this.setSelection(xStart, xEnd);
							}
							var self = this;
							setTimeout(function(){
								self.formatNumber();
							}, 1);
							break;
						case 109: // MENOS
						case 173: // MENOS
						case 189: // MENOS
							this.setSignal(ev);
							break;
						case 110: // VIRGULA
						case 188: // VIRGULA
						case 190: // PONTO
							this.moveToDecimalPosition(ev);
							break;
						default:
							ev.preventDefault();
							var chr = this.chrFromEv(ev);
							//Se campo inteiro estiver selecionado, apaga conteúdo e posiciona na parte inteira
							if (this.domNode.value.length == (this.getSelectionEnd() - this.getSelectionStart())){
								this.domNode.value = "";
								this.formatNumber();
								this.moveToIntegerPosition(ev);
							}
							//Valida tamanho máximo
							chr = this.validateLength(ev, chr);
							if (chr != ''){
								if (this.isInputDecimals(ev)){
									//Seleciona digito anterior para digitação caminha para a direita
									var curpos = this.getSelectionStart();
									if (curpos == this.getSelectionEnd()){
										this.setSelection(curpos, curpos + 1);
									}
								}
								
								if( this.isViableInput( p, chr ) ) {
									var range = new Range( this )
									 ,    val = this.sanityTest( range.replaceWith( chr ) );

									if(val !== false){
										this.updateSelection( chr );
										this.formatNumber();
									}
									this.node.trigger( "valid", ev, this.node );
								} else {	
									this.node.trigger( "invalid", ev, this.node );
								}
							}
							break;
							
					}
				}
			}
		},
		
		onKeyPress: function(ev) {
			if (ev.altKey && ev.which == 18){ //Option + Delete
				ev.preventDefault(); 
				return;
			}
			var key = ev.which || ev.keyCode;

			if(
				!( this.allowKeys[ key ] )
				&& !(ev.ctrlKey || ev.altKey || ev.metaKey)
			) {
				ev.preventDefault();
				ev.stopPropagation();
			}
		},
		
		allowKeys : {
			   8 : 1 // backspace
			,  9 : 1 // tab
			, 13 : 1 // enter
			, 35 : 1 // end
			, 36 : 1 // home
			, 37 : 1 // left
			, 38 : 1 // up
			, 39 : 1 // right
			, 40 : 1 // down
			, 46 : 1 // delete
		},

		moveToDecimalPosition: function(ev){
			ev.preventDefault();
			if (this.options.decDigits > 0){
				var curpos = this.domNode.value.indexOf(this.options.decSymbol) + 1;
				this.setSelection(curpos, curpos);
			} 
		},

		moveToIntegerPosition: function(ev){
			ev.preventDefault();
			if (this.options.decDigits > 0){
				var curpos = this.domNode.value.indexOf(this.options.decSymbol);
				this.setSelection(curpos, curpos);
			} 
		},
		
		isInputDecimals: function(ev){
			if (this.options.decDigits > 0){
				var curpos = this.domNode.value.indexOf(this.options.decSymbol);
				if (curpos < this.getSelectionStart()){
					return true;
				}else{
					return false;
				}
			} 
		},
		
		setSignal: function(ev){
			ev.preventDefault();
			ev.stopPropagation();
			if ($(this.node).attr("minValue") < 0){
				if ($(this.node).attr("n") == "-"){
					$(this.node).attr("n","");
				}else{
					$(this.node).attr("n","-");
				}
				this.formatNumber();
			}
		},


		onFocus: function(ev) {
			//Salva valor atual para comparar com o novo
			this.options.oldValue = this.domNode.value;
			
			ev.stopPropagation();
			ev.preventDefault();

			this.options.showMask && (this.domNode.value = this.wearMask(this.domNode.value));
			this.sanityTest( this.domNode.value );

			var self = this;
			
			setTimeout( function(){
				self[ self.options.type === "fixed" ? 'selectFirst' : 'selectAll' ]();
			}, 1 );
		},

		onBlur: function(ev) {
			ev.stopPropagation();
			ev.preventDefault();

			if(this.options.stripMask){
				this.domNode.value = this.stripMask();
			}
			
			//Dispara evento se valor foi alterado
			if (this.options.oldValue != this.domNode.value){
				$(this.node).trigger("change");
			}
		},

		selectAll: function() {
			this.setSelection(0, this.domNode.value.length);
		},

		selectFirst: function() {
			for(var i = 0, len = this.options.mask.length; i < len; i++) {
				if(this.isInputPosition(i)) {
					this.setSelection(i, (i + 1));
					return;
				}
			}
		},

		selectLast: function() {
			for(var i = (this.options.mask.length - 1); i >= 0; i--) {
				if(this.isInputPosition(i)) {
					this.setSelection(i, (i + 1));
					return;
				}
			}
		},

		selectPrevious: function(p) {
			if( !$chk(p) ){ p = this.getSelectionStart(); }

			if(p <= 0) {
				this.selectFirst();
			} else {
				if(this.isInputPosition(p - 1)) {
					this.setSelection(p - 1, p);
				} else {
					this.selectPrevious(p - 1);
				}
			}
		},

		selectNext: function(p) {
			if( !$chk(p) ){ p = this.getSelectionEnd(); }

			if( this.isNumber() ){
				this.setSelection( p+1, p+1 );
				return;
			}

			if( p >= this.options.mask.length) {
				this.selectLast();
			} else {
				if(this.isInputPosition(p)) {
					this.setSelection(p, (p + 1));
				} else {
					this.selectNext(p + 1);
				}
			}
		},

		setSelection: function( a, b ) {
			a = a.valueOf();
			if( !b && a.splice ){
				b = a[1];
				a = a[0];
			}

			if(this.domNode.setSelectionRange) {
				this.domNode.focus();
				this.domNode.setSelectionRange(a, b);
			} else if(this.domNode.createTextRange) {
				var r = this.domNode.createTextRange();
				r.collapse();
				r.moveStart("character", a);
				r.moveEnd("character", (b - a));
				r.select();
			}
		},

		updateSelection: function( chr ) {
			var value = this.domNode.value
			 ,  range = new Range( this )
			 , output = range.replaceWith( chr );

			this.domNode.value = output;
			if( range[0] === range[1] ){
				this.setSelection( range[0] + 1, range[0] + 1 );
			}else{
				this.setSelection( range );
			}
		},

	 	setEnd: function() {
			var len = this.domNode.value.length - this.options.decDigits;
			if (this.options.decDigits > 0){
				len--;
			}
			this.setSelection(len, len);
		},

		getSelectionRange : function(){
			return [ this.getSelectionStart(), this.getSelectionEnd() ];
		},

		getSelectionStart: function() {
			var p = 0,
			    n = this.domNode.selectionStart;

			if( n ) {
				if( typeof( n ) == "number" ){
					p = n;
				}
			} else if( document.selection ){
				var r = document.selection.createRange().duplicate();
				r.moveEnd( "character", this.domNode.value.length );
				p = this.domNode.value.lastIndexOf( r.text );
				if( r.text == "" ){
					p = this.domNode.value.length;
				}
			}
			return p;
		},

		getSelectionEnd: function() {
			var p = 0,
			    n = this.domNode.selectionEnd;

			if( n ) {
				if( typeof( n ) == "number"){
					p = n;
				}
			} else if( document.selection ){
				var r = document.selection.createRange().duplicate();
				r.moveStart( "character", -this.domNode.value.length );
				p = r.text.length;
			}
			return p;
		},

		isInputPosition: function(p) {
			var mask = this.options.mask.toLowerCase();
			var chr = mask.charAt(p);
			return !!~"9ax".indexOf(chr);
		},

		sanityTest: function( str, p ){
			var sanity = this.options.sanity;

			if(sanity instanceof RegExp){
				return sanity.test(str);
			}else if($.isFunction(sanity)){
				var ret = sanity(str, p);
				if(typeof(ret) == 'boolean'){
					return ret;
				}else if(typeof(ret) != 'undefined'){
					if( this.isFixed() ){
						var p = this.getSelectionStart();
						this.domNode.value = this.wearMask( ret );
						this.setSelection( p, p+1 );
						this.selectNext();
					}else if( this.isNumber() ){
						var range = new Range( this );
						this.domNode.value = ret;
						this.setSelection( range );
						this.formatNumber();
					}
					return false;
				}
			}
		},

		isViableInput: function() {
			return this[ this.isFixed() ? 'isViableFixedInput' : 'isViableNumericInput' ].apply( this, arguments );
		},

		isViableFixedInput : function( p, chr ){
			var mask   = this.options.mask.toLowerCase();
			var chMask = mask.charAt(p);

			var val = this.domNode.value.split('');
			val.splice( p, 1, chr );
			val = val.join('');

			var ret = this.sanityTest( val, p );
			if(typeof(ret) == 'boolean'){ return ret; }

			if(({
				'9' : this.options.validNumbers,
				'a' : this.options.validAlphas,
				'x' : this.options.validAlphaNums
			}[chMask] || '').indexOf(chr) >= 0){
				return true;
			}

			return false;
		},

		isViableNumericInput : function( p, chr ){
			return !!~this.options.validNumbers.indexOf( chr );
		},

		wearMask: function(str) {
			var   mask = this.options.mask.toLowerCase()
			 ,  output = ""
			 , chrSets = {
				  '9' : 'validNumbers'
				, 'a' : 'validAlphas'
				, 'x' : 'validAlphaNums'
			};

			for(var i = 0, u = 0, len = mask.length; i < len; i++) {
				switch(mask.charAt(i)) {
					case '9':
					case 'a':
					case 'x':
						output += 
							((this.options[ chrSets[ mask.charAt(i) ] ].indexOf( str.charAt(u).toLowerCase() ) >= 0) && ( str.charAt(u) != ""))
								? str.charAt( u++ )
								: this.options.maskEmptyChr;
						break;

					default:
						output += mask.charAt(i);
						if( str.charAt(u) == mask.charAt(i) ){
							u++;
						}

						break;
				}
			}
			return output;
		},

		stripMask: function() {
			var value = this.domNode.value;
			if("" == value) return "";
			var output = "";

			if( this.isFixed() ) {
				for(var i = 0, len = value.length; i < len; i++) {
					if((value.charAt(i) != this.options.maskEmptyChr) && (this.isInputPosition(i)))
						{output += value.charAt(i);}
				}
			} else if( this.isNumber() ) {
				for(var i = 0, len = value.length; i < len; i++) {
					if(this.options.validNumbers.indexOf(value.charAt(i)) >= 0)
						{output += value.charAt(i);}
				}
			}

			return output;
		},

		chrFromEv: function(ev) {
			//Limita o tamanho de digitos
			var chr = '', key = ev.which;

			if(key >= 96 && key <= 105){ key -= 48; }     // shift number-pad numbers to corresponding character codes
			chr = String.fromCharCode(key).toLowerCase(); // key pressed as a lowercase string
			return chr;
		},

		validateLength: function(ev, chr) {
			if (this.getSelectionEnd() == this.getSelectionStart() //Não há seleção
			&& (this.getSelectionStart() == this.domNode.value.length //Cursos na última posição 
			 || !this.isInputDecimals(ev))){ //Não é casa decimal
				var xL = parseFloat($(this.node).attr("maxlength"));
				if (xL!="NaN"){
					//Com tamanho máximo de 1 caracter, permite a digitação sobreescrevendo o valor corrente
					if (xL==1){
						//Seleciona o valor corrente para que possa ser sobreescrito
						this.selectAll();
						return chr;
					//Inibe o valor digitado
					}else if (this.domNode.value.length >= xL){
						ev.preventDefault();
						ev.stopPropagation();
						ev.stopImmediatePropagation();
						return '';
					}
				}
			}
			return chr;
		},

		formatNumber: function() {
			// stripLeadingZeros
			var olen = this.domNode.value.length
			 ,  str2 = this.stripMask()
			 ,  str1 = str2.replace( /^0+/, '' )
			 , range = new Range(this)
			 , neg = ""
			 , decsymb = this.options.decSymbol
			 , curpos = olen - range["1"];

			//apaga sinal se houver
			str1 = str1.replace('-', '');
			//Impeder a exibição do sinal quando o valor for vázio
			if (str1 == ""){
				$(this.node).attr("n","");
			}
			//Configura sinal se houver
			if ($(this.node).attr("n") == "-"){
				neg = "-";
			}
			// wearLeadingZeros
			str2 = str1;
			str1 = "";
			for(var len = str2.length, i = this.options.decDigits; len <= i; len++) {
				str1 += "0";
			}
			str1 += str2;

			// decimalSymbol

			str2 = str1.substr(str1.length - this.options.decDigits);
			str1 = str1.substring(0, (str1.length - this.options.decDigits));

			//Verifica intervalo dos valores
			var xValue = parseFloat(neg + str1 + this.options.decSymbol + str2);
			var xMinValue = parseFloat($(this.node).attr("minValue"));
			var xMaxValue = parseFloat($(this.node).attr("maxValue"));
			if (xValue > xMaxValue ||
				xValue < xMinValue){
				$(this.node).addClass("-error");
			}else{
				$(this.node).removeClass("-error");
			}
			// groupSymbols
			if (this.options.groupDigits != 0){
				var re = new RegExp("(\\d+)(\\d{"+ this.options.groupDigits +"})");
				while(re.test(str1)) {
					str1 = str1.replace(re, "$1"+ this.options.groupSymbol +"$2");
				}
			}
			if (this.options.decDigits == 0){
				decsymb = "";
			}

			this.domNode.value = this.options.currencySymbol + neg + str1 + decsymb + str2;
//			this.setSelection( range );
			curpos = this.domNode.value.length - curpos;
			//posiciona após o ponto decimal
			this.setSelection(curpos, curpos);
		},

		getObjForm: function() {
			return this.node.getClosest('form');
		},

		submitForm: function() {
			var form = this.getObjForm();
			form.trigger('submit');
		}
	};

	function Range( obj ){
		this.range = obj.getSelectionRange();
		this.len   = obj.domNode.value.length
		this.obj   = obj;

		this['0']  = this.range[0];
		this['1']  = this.range[1];
	}
	Range.prototype = {
		valueOf : function(){
			var len = this.len - this.obj.domNode.value.length;
			return [ this.range[0] - len, this.range[1] - len ];
		},
		replaceWith : function( str ){
			var  val = this.obj.domNode.value
			 , range = this.valueOf();

			return val.substr( 0, range[0] ) + str + val.substr( range[1] );
		}
	};

	$.fn.dbsmask = function(options){
		this.each(function(){
			new dbsmask($(this), options);
		});
	};
})(jQuery);