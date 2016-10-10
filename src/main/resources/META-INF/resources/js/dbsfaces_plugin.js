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
          		this.get(0).setAttributeNS(null, pAttribute, pValue);
      		}
      		return this;
      	}
  	},
  	
  	//Retorna o total de segmentos de um path
  	$.fn.svgGetPathTotalSegs = function () {
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
   }


})(jQuery);

