dbs_messageList = function(pId) {
	var xMouseenter = function(e) {
		if ($(pId + " > .-container > .-list").length == 0){
			return;
		}
		$(pId + " > .-container > .-list").addClass("-opened");
	}
	var xMouseleave = function(e) {
		if ($(pId + " > .-container > .-list").length == 0){
			return;
		}
		$(pId + " > .-container > .-list").removeClass("-opened");
	}
	

	$(pId + " > .-container > .-list > .-content > .-message > .-container > .-button > .dbs_button > .-i_delete").click(function(e){
		xMouseenter();
		$(pId).off('mouseleave.messagelist');
		dbsfaces.messageList.excluir(this);
	});
	
//	Desabilida atualização via push, quando o cursor estiver sobre o botão de exclusão, para evitar que seja excluido outro registro em caso de atualização automática
	$(pId + " > .-container > .-list > .-content > .-message > .-container > .-button > .dbs_button > .-i_delete").hover(function(e){
		$(this).closest(".dbs_messageList").toggleClass("-pushDisabled");
	});

	$(pId).off('mouseenter.messagelist');
	$(pId).off('mouseleave.messagelist');
	$(pId).on('mouseenter.messagelist', xMouseenter);
	$(pId).on('mouseleave.messagelist', xMouseleave);
	
	setTimeout(function(){
		xMouseleave();
	}, 200);
}

dbsfaces.messageList = {
	excluir: function(pBtn){
		var xMsg = $(pBtn).closest(".-message");
		var xId = $(pBtn).closest(".dbs_messageList").attr("id");
		var xInput = $(dbsfaces.util.jsid(xId + ":foo"))
		var xIndex = $(xMsg).attr("index");
		if ($(xInput).val() != xIndex){
			$(xInput).val(xIndex);
		}

		$(pBtn).closest(".-container").css("opacity","0.1");
		$(xMsg).animate({
			border: 0,
			margin: 0,
			padding: 0,
			height: 0},250,function() {
				xInput.click();
		});
	}
	
	

}