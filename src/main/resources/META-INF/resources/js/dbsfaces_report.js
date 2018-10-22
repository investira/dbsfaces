dbs_report = function(pId) {
	var xH = 0;
    /*ajuste para considerar a altura do filtro e do tool bar*/
    xH = $(pId + "> .-header").outerHeight();
    $(pId + "> .-content").css("margin-bottom", "-" + xH + "px")
                          .css("padding-bottom", xH + "px");

    //Captura evento ajax que é disparado ao selecionar a aba de 'Visualização' do relatório, para exibir indicador de processamento
    dbsfaces.ui.ajaxShowLoading(pId + " .dbs_tabPage.-report");

}

dbsfaces.report = {
}