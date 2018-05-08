//JQUERY PLUGINS===================================================================

(function($) {
	$.fn.svgHasClass = function (pClassName) {
    	 return new RegExp('(\\s|^)' + pClassName + '(\\s|$)').test(this.get(0).getAttribute('class'));
    },
      
    $.fn.svgRemoveClass = function (pClassName) {
		this.each(function(){
	    	var xObject = this;
	    	if ($(this).svgHasClass(pClassName)) {
		    	var xRemovedClass = xObject.getAttribute('class').replace(new RegExp('(\\s|^)' + pClassName + '(\\s|$)', 'g'), '$2');
	    		xObject.setAttribute('class', xRemovedClass);
	    	}
		});
		return this;
    },

    $.fn.svgAddClass = function (pClassName) {
		this.each(function(){
	    	var xObject = this;
	    	if (!$(this).svgHasClass(pClassName)) {
	    		xObject.setAttribute('class', xObject.getAttribute('class') + ' ' + pClassName);
	    	}
		});
		return this;
    },
      
    $.fn.svgAttr = function (pAttribute, pValue) {
		if (typeof(pValue) == "undefined"){
      		return this.get(0).getAttributeNS(null, pAttribute);
      	}else{
      		if (pValue != null){
      			this.each(function(){
      				this.setAttributeNS(null, pAttribute, pValue);
      			});
      		}
      		return this;
      	}
  	},
  	
  	//Retorna o total de segmentos de um path
  	$.fn.svgGetPathTotalSegs = function () {
  		if (this.length == 0){return;}
      	if (typeof(this) == "undefined"){
      		return 0;
      	}
 		return this.get(0).getPathSegAtLength(this.get(0).getTotalLength() + 10) + 1;
  	},

    $.fn.hasScrollBar = function() {
        return this.get(0).scrollHeight > this.height();
    },

    $.fn.isParentFixed = function() {
    	if (this.closest("foreignObject").length == 0){
    		return dbsfaces.ui.pvIsParentFixed(this);
    	}
        return true;
    },
    
	//Encontra o parente mais próximo que possuir barra de rolagem
	$.fn.scrollParent = function( includeHidden ) {
		var position = this.css( "position" ),
			excludeStaticParent = position === "absolute",
			overflowRegex = includeHidden ? /(auto|scroll|hidden)/ : /(auto|scroll)/,
			scrollParent = this.parents().filter( function() {
				var parent = $( this );
				if ( excludeStaticParent && parent.css( "position" ) === "static" ) {
					return false;
				}
				return overflowRegex.test( parent.css( "overflow" ) + parent.css( "overflow-y" ) + parent.css( "overflow-x" ) );
			} ).eq( 0 );

		return position === "fixed" || !scrollParent.length ? $("body") : scrollParent;
//		return position === "fixed" || !scrollParent.length ? $( this[ 0 ].ownerDocument || document ) : scrollParent;
	},

	$.fn.focusNextInputField = function() {
	    return this.each(function() {
	        var xFields = $(this).parents('form:eq(0),body').find('button,input,textarea,select');
	        var xIndex = xFields.index(this);
	        if (xIndex > -1 && (xIndex + 1 ) < xFields.length) {
	            xFields.eq(xIndex).focus();
	        }
	        return false;
	    });
	},

    $.fn.touchwipe = function(settings) {
        var config = {
            min_move_x: 20,
            min_move_y: 20,
            wipeLeft: function() {},
            wipeRight: function() {},
            wipeUp: function() {},
            wipeDown: function() {},
            onMove: function(dx, dy) {},
            onStart: function() {},
            preventDefaultEvents: true
        };

        if (settings){
            $.extend(config, settings);
        }

        this.each(function() {
            var startX;
            var startY;
            var isMoving = false;

            function cancelTouch() {
                this.removeEventListener('touchmove', onTouchMove);
                startX = null;
                isMoving = false;
            }

            function onTouchMove(e){
//        		if ($(this).has(e.originalEvent.srcElement).length){
//            	console.log($(this).attr("class"));
//            	console.log($(this).has(e.srcElement).length);
            	var xPreventDefault = null;
                if (config.preventDefaultEvents) {
                    e.preventDefault();
                }
                var x = e.touches[0].pageX;
                var y = e.touches[0].pageY;
                var dx = startX - x;
                var dy = startY - y;
                if (isMoving) {
                    if (Math.abs(dx) >= config.min_move_x) {
                        cancelTouch();
                        if (dx > 0) {
                        	xPreventDefault = config.wipeLeft();
                        }else{
                        	xPreventDefault = config.wipeRight();
                        }
                    } else if (Math.abs(dy) >= config.min_move_y) {
                        cancelTouch();
                        if (dy > 0) {
                        	xPreventDefault = config.wipeUp();
                        }else{
                        	xPreventDefault = config.wipeDown();
                        }
                    }
//                  console.log(" retornou \t" + xPreventDefault);
                    if (xPreventDefault) {
                    	//Prevent return false
                        e.preventDefault();
                    }
                    e.stopImmediatePropagation();
                }
            	config.onMove(dx, dy);
            }

            function onTouchStart(e){
                if (e.touches.length == 1) {
                    startX = e.touches[0].pageX;
                    startY = e.touches[0].pageY;
                    isMoving = true;
                    config.onStart();
                    this.addEventListener('touchmove', onTouchMove, false);
                }
            }
            if ('ontouchstart' in document.documentElement) {
                this.addEventListener('touchstart', onTouchStart, false);
            }
        });

        return this;
    },
   
    $.fn.onView = function() {
    	dbsfaces.onView.initialize(this);
    	return this;
    },

    $.fn.fontSizeFit = function(pContainer) {
		this.each(function(){
	    	var xObject = $(this);
	    	var xPreVisibility = xObject.css("visibility");
	    	xObject.css("visibility", "hidden");
	    	xObject.css("font-size", "");
//	    	window.getComputedStyle(xObject[0], null);
//	    	var xStyle = window.getComputedStyle(xObject.parent()[0], null);
	    	var xStyle = window.getComputedStyle(xObject[0], null);
	    	var xOF = parseFloat(xStyle.getPropertyValue("font-size"));
	    	var xOH = xObject[0].getBoundingClientRect().height;
	    	var xOW = xObject[0].getBoundingClientRect().width;
	    	var xCH = pContainer[0].getBoundingClientRect().height;
	    	var xCW = pContainer[0].getBoundingClientRect().width;
	    	//Limite pela altura
	    	if (xCH < xCW){
	    		xOF *= xCH / xOH;
	    	//Limite pela largura
	    	}else{
	    		xOF *= xCW / xOW;
	    	}
	    	xObject.css("visibility", xPreVisibility);
	    	xObject.css("font-size", xOF);

	    	console.log(xOW + "\t" + xOW + "\t" + xCW);
	    	console.log(xOH + "\t" + xOH + "\t" + xCH);
	    	console.log(xOF + "\t");
//	    	console.log(xObject[0].getComputedTextLength() + "\t" + xObject[0].getBoundingClientRect().width + "\t" + xObject.width());
		});
		return this;
    },

    $.fn.getDim = function() {
		var xRect = {width:0, height:0, top:0, bottom:0, left:0, right:0, x:0, y:0};
 		var xEle = this[0];
//		$(xEle).height(); //Artifício para obrigar a atualização da dimensão do componente
		var xBCR = xEle.getBoundingClientRect();
		xRect.width = xBCR.width;
		xRect.height = xBCR.height;
		xRect.top = xBCR.top - $(window).scrollTop();
		xRect.bottom = xBCR.bottom;
		xRect.left = xBCR.left - $(window).scrollLeft();
		xRect.right = xBCR.right;
		xRect.x = xBCR.x;
		xRect.y = xBCR.y;
        return xRect;
    }

})(jQuery);

