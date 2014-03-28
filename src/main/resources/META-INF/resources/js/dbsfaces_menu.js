dbs_menu = function(pId) {
//	$(".dbs_menuitem").off("click.menu");
//	$(".dbs_menuitem:not(.-disabled)").on("click.menu", function(e){
//		dbsfaces.ui.showLoading("menu", true);
//	});
	
	$(".dbs_menu > li").off("mouseover.menu");
	$(".dbs_menu > li").on("mouseover.menu", function(e){
		$(this).children("ul").addClass("-hover");
	});

	$(".dbs_menu > li").off("mouseout.menu");
	$(".dbs_menu > li").on("mouseout.menu", function(e){
		$(this).children("ul").removeClass("-hover");
	});
	
	$(".dbs_menuitem").off("mouseover.menu");
	$(".dbs_menuitem").on("mouseover.menu", function(e){
		$(this).children(".dbs_submenu").addClass("-hover");
	});

	$(".dbs_menuitem").off("mouseout.menu");
	$(".dbs_menuitem").on("mouseout.menu", function(e){
		$(this).children(".dbs_submenu").removeClass("-hover");
	});

//	$(".dbs_menu > li").off("touchstart.menu");
//	$(".dbs_menu > li").on("touchstart.menu", function(e){
//		$(this).children("ul").addClass("-hover");
//	});
//
//	$(".dbs_menu > li").off("touchend.menu");
//	$(".dbs_menu > li").on("touchend.menu", function(e){
//		$(this).children("ul").removeClass("-hover");
//	});

//	$(".dbs_menuitem").off("touchstart.menu");
//	$(".dbs_menuitem").on("touchstart.menu", function(e){
//		$(this).children(".dbs_submenu").addClass("-hover");
//	});
//
//	$(".dbs_menuitem").off("touchend.menu");
//	$(".dbs_menuitem").on("touchend.menu", function(e){
//		$(this).children(".dbs_submenu").removeClass("-hover");
//	});
//
//	.dbs_menu > li:HOVER > ul, 
//	.dbs_menu > li:FOCUS > ul, 
//	.dbs_menuitem:HOVER > .dbs_submenu, 
//	.dbs_menuitem:FOCUS > .dbs_submenu {
//		display: block;
//	}

	
}
