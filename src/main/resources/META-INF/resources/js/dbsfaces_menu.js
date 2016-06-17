dbs_menu = function(pId) {
	dbsfaces.menu.initialize($(pId));

//	$(pId + " .dbs_menuitem > .-submenu > .-container").on(dbsfaces.EVENT.ON_TRANSITION_START, function(){
//		console.log("START\t" + this);
//	});
//	$(pId + " .dbs_menuitem:not(.-disabled) > .-submenu > .-container").on(dbsfaces.EVENT.ON_TRANSITION_END, function(e){
//		if (e.originalEvent.propertyName == "max-height"){
//			var xContainer = $(this);
//			var xMenuItem = xContainer.closest(".dbs_menuitem");
////			dbsfaces.menu.setOpened(xMenuItem, !(xMenuItem.hasClass("-opened")));
//			dbsfaces.menu.setOpened(xMenuItem, (xContainer.css("max-height") != "0px" && xContainer.css("max-height") != "none"));
//			console.log("END\t" + xContainer.css("max-height"));
//		}
//	});

	$(pId + "[type=float] .dbs_menuitem:not(.-disabled)").on("mouseenter", function(e){
		var xMenuItem = $(this);
//		console.log("enter\t" + $(this).find(" > .-caption > .-content > .-label").text());
		dbsfaces.menu.setOpened(xMenuItem, true);
//		return false;
	});
	$(pId + "[type=float] .dbs_menuitem:not(.-disabled)").on("mouseleave", function(e){
		var xMenuItem = $(this);
//		console.log("Leave\t" + $(this).find(" > .-caption > .-content > .-label").text());
		dbsfaces.menu.setOpened(xMenuItem, false);
//		return false;
	});
	$(pId + "[type=scroll] .dbs_menuitem:not(.-disabled)").on("click touchstart", function(e){
		var xMenuItem = $(this);
//		console.log("Click\t" + $(this).find(" > .-caption > .-content > .-label").text());
		dbsfaces.menu.setOpened(xMenuItem, !xMenuItem.hasClass("-opened"));
		return false;
	});

//	$(pId + " .dbs_menuitem:not(.-disabled)").on("mousedown", function(){
//		dbsfaces.menu.setOpened($(this), !$(this).hasClass("-selected"));
//	});
//	$(pId + " .dbs_menuitem:not(.-disabled)").on("mouvein", function(){
//		dbsfaces.menu.setOpened($(this), true);
//	});
//	$(pId + " .dbs_menuitem:not(.-disabled)").on("mouveout", function(){
//		dbsfaces.menu.setOpened($(this), false);
//	});
}

dbsfaces.menu = {
	initialize: function(pMenu){
		dbsfaces.menu.initializeLayout(pMenu);
	},
	
	initializeLayout: function(pMenu){
		dbsfaces.menu.initializeMenuItem(pMenu);
		dbsfaces.menu.initializeLayoutEraseOrphans(pMenu);
	},
	
	initializeMenuItem: function(pMenu){
		pMenu.find('.dbs_menuitem').each(function(){
			dbsfaces.menu.initializeMenuItemData(pMenu, $(this));
			dbsfaces.menu.pvSetChildrenIcon($(this));
			dbsfaces.menu.pvSetMenuItemHeight($(this));
			dbsfaces.menu.setOpened($(this), $(this).hasClass("-opened"));
		});
	},

	initializeMenuItemData: function(pMenu, pMenuItem){
		pMenuItem.data("menu", pMenu);
		pMenuItem.data("type", pMenu.attr("type"));
		pMenuItem.data("parent", pMenuItem.parent().closest(".dbs_menuitem"));
		pMenuItem.data("submenu", pMenuItem.children(".-submenu"));
		pMenuItem.data("container", pMenuItem.data("submenu").children(".-container"));
		pMenuItem.data("content", pMenuItem.data("container").children(".-content"));
		pMenuItem.data("childrenIcon", pMenuItem.find(" > .-caption > .-content > .-childrenIcon"));
		if (pMenuItem.data("type") == "scroll"){
			pMenuItem.data("delay", 100);
		}else{
			pMenuItem.data("delay", 0);
		}
	},
 
	//Retira os separadores seguidos ou quando for o último item
	initializeLayoutEraseOrphans: function(pMenu){
		pMenu.find('.dbs_menuitemSeparator').each(function(){
			var xMS = $(this);
			var xNext = xMS.next();
			if(xNext.length == 0){
				xMS.remove();
			}else if (xNext.hasClass("dbs_menuitemSeparator")){
				xMS.remove();
			}
		});
	},

	setOpened: function(pMenuItem, pOpened){
		if (pMenuItem.data("submenu").length == 0){return;}
		if (pOpened){
//			pMenuItem.data("container").css("max-height", pMenuItem.data("content")[0].getBoundingClientRect().height);
//			if (pMenuItem.data("parent").length != 0){
//				pMenuItem.data("parent").data("container").css("max-height", "");
//			}
			var xContainer = pMenuItem.data("container");
			var xContent = pMenuItem.data("content");
//			var xContent = pMenuItem.data("content");
			pMenuItem.addClass("-opened");
			xContainer.finish();
			xContainer.animate({"height": xContent[0].getBoundingClientRect().height}, 
					  pMenuItem.data("delay"), 
					  "linear", 
					  function(){
						xContainer.css("overflow","visible");
						if (pMenuItem.data("parent").length > 0){
							pMenuItem.data("parent").data("container").css("height", "auto");
						}
	  				  }
			);
		}else{
			var xContainer = pMenuItem.data("container");
			var xContent = pMenuItem.data("content");
			xContainer.finish();
			xContainer.animate({"height": 0}, 
					  pMenuItem.data("delay"), 
					  "linear", 
					  function(){
						xContainer.css("overflow", "");
						pMenuItem.removeClass("-opened");
	  				  }
			);			
		}
		dbsfaces.menu.pvShowChildrenIcon(pMenuItem, pOpened);
	},
	
	pvShowChildrenIcon: function(pMenuItem, pOpened){
		var xChildrenIcon = pMenuItem.data("childrenIcon");
		if (xChildrenIcon.length > 0){
			if (pOpened){
				xChildrenIcon.removeClass("-c")
							 .addClass("-o");
							
			}else{
				xChildrenIcon.removeClass("-o")
							 .addClass("-c");
							 
			}
			dbsfaces.menu.pvSetChildrenIcon(pMenuItem);
		}
	},
	
	pvSetChildrenIcon: function(pMenuItem){
		var xClassC;
		var xClassO;
		var xChildrenIcon = pMenuItem.data("childrenIcon");
		var xMenu = pMenuItem.data("menu");
		if (pMenuItem.data("type") == "scroll"){
			xClassC = "-i_navigate_next";
			xClassO = "-i_navigate_down";
		}else{
			xClassC = "-i_add";
			xClassO = "-i_subtract";
		}
		if (xChildrenIcon.hasClass("-c")){
			xChildrenIcon.removeClass(xClassO)
					 .addClass(xClassC);
		}else{
			xChildrenIcon.removeClass(xClassC)
			 		 .addClass(xClassO);
		}
	},

	pvSetMenuItemHeight: function(pMenuItem){
		if (pMenuItem.length == 0){return;}
		var xSubmenu = pMenuItem.data("submenu");
		var xContainer = pMenuItem.data("container");
		if (xContainer.length == 0){return;}
		var xContent = pMenuItem.data("content");
		var xHeight = xContent[0].getBoundingClientRect().height;
//		xSubmenu.css("max-height", xHeight);
	}

//	pvSetMenuItemHeight: function(pMenuItem){
//		var xSubmenu = pMenuItem.children(".-submenu");
//		var xContainer = xSubmenu.children(".-container");
//		if (xContainer.length == 0){return;}
//		var xContent = xContainer.children(".-content");
//		var xHeight = xContent[0].getBoundingClientRect().height;
//		xSubmenu.css("max-height", xHeight);
//	}
}