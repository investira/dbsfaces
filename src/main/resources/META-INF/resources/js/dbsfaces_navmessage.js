dbs_navMessage = function(pId) {
	var xNavMessage = $(pId);

	dbsfaces.navMessage.initialize(xNavMessage);
};

dbsfaces.navMessage = {
	initialize: function(pNavMessage){
		var xMessage = $(pNavMessage).find('> .dbs_nav > .-container > .-nav > .-container > .-content > div > nav > span');
		if (xMessage.length > 0) {
			setTimeout(function () {
				dbsfaces.nav.show($(pNavMessage).find(' > .dbs_nav'));
			}, 100);
		}
	}
};

