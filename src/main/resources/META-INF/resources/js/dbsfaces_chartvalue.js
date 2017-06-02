dbs_chartValue = function(pId) {
	var xChartValue = $(pId);
};



dbsfaces.chartValue = {
	initialize: function(pChartData, pLabel, pRelationalGroupIndex, pOriginalValue){
		var xChartValueData = dbsfaces.chartValue.pvInitializeData(pChartData, pLabel, pRelationalGroupIndex, pOriginalValue);
		return xChartValueData;
	},

	pvInitializeData: function(pChartData, pLabel, pRelationalGroupIndex, pOriginalValue){
		var xChartValueData = {
			dom : {
				self : null, // o próprio chartvalue
				parent : pChartData.dom.self,  //o pai(chart)
				point : null, //elemento point
				pointLink : null, //elemento de liga point ao centro
				pointDif: null, //Ponto com a parte que é diferente dos outros chartvalue(com mesmo index) de outros chart
				info : null, //elemento que contém infos
				infoValues : null, //elemento que contém os elementos dos textos do info(somente usado no chartpie)
				infoLabel : null, //elemento que contém o label
				infoLabelBox : null, //elemento que contém o box do Label
				infoValue : null, //elemento que contém o value
				infoValueBox : null, //elemento que contém o box do Value
				infoPath : null, //elemento que contém o caminho do label e value até o point
				infoPerc : null, //elemento que contém o valor percentual no chartpie
				infoPercInt : null, //elemento que contém o inteiro do valor percentual no chartpie
				infoPercDec : null, //elemento que contém o decimal do valor percentual no chartpie
				infoPercBox : null //elemento que contém o box do perc
			},
			key : pChartData.dom.childrenData.length,
			index : null, //Index - //Chave sequencial será atribuita após o sort 
			value : 0, //somatótio dos valores que possuem o mesmo label
			displayValue: "", //Valor que será exibido no lugar do value
			label : pLabel.trim(), //Label já desmembrado do group label
			relationalGroupIndex: pRelationalGroupIndex, //Index em relação ao label quando houver mais de uma
			originalValues : [], //valores originals que agrupados neste chartvalue
			style: pOriginalValue.style,
			styleClass: pOriginalValue.styleClass,
			x : null, //posição X no gráfico (dentro da escala)
			y : null, //posição Y no gráfico (dentro da escala)
			color: pOriginalValue.color, //Cor do valor,
			colorInverted: null, //Cor com luminancia invertida
			perc: null, //Percentual que valor representa sobre o total
			totalValue: 0, //Total até este chartvalue
			globalSequence: 0, //Número sequencial do item do chartValue, considerando todos os gráficos 
			arcInfo: null //Informacoes do arco quando for gráfico pie
		}
		if (typeof xChartValueData.color == "undefined"){
			xChartValueData.color = null;
		}
		if (typeof xChartValueData.style == "undefined"){
			xChartValueData.style = null;
		}
		if (typeof xChartValueData.styleClass == "undefined"){
			xChartValueData.styleClass = null;
		}
		return xChartValueData;
	},
	
	initializeLayoutCreateDom: function(pChartData, pChartValueData){
		//Cria ChartValue
		pChartValueData.dom.self = dbsfaces.svg.g(pChartData.dom.values, "dbs_chartValue -" + pChartData.type, null, {index: pChartValueData.index, relationalGroupIndex: pChartValueData.relationalGroupIndex});
		pChartValueData.dom.self.addClass(pChartValueData.styleClass);
		pChartValueData.dom.self.attr("style", pChartValueData.style);
		//Salva data
		pChartValueData.dom.self.data("data", pChartValueData);
		//Cria Elemento que contém infos
		pChartValueData.dom.info = dbsfaces.svg.g(pChartValueData.dom.self, "-info", null, null);

		if (pChartData.type == "line"){
			//Ponto
			pChartValueData.dom.point = dbsfaces.svg.circle(pChartValueData.dom.self, null, null, null, "-point", null, {r:".3em"}); //'r' precisa ser um atributo por problema no FIREFOX
			//Path
			pChartValueData.dom.infoPath = dbsfaces.svg.path(pChartValueData.dom.info, null, "-path", null, null);
			//LabelBox
			pChartValueData.dom.infoLabelBox = dbsfaces.svg.rect(pChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-labelBox", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//valueBox
			pChartValueData.dom.infoValueBox = dbsfaces.svg.rect(pChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-valueBox", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Texto do Label
			pChartValueData.dom.infoLabel = dbsfaces.svg.text(pChartValueData.dom.info, null, null, pChartValueData.label, "-label", null, null);
			//Texto do Valor
			pChartValueData.dom.infoValue = dbsfaces.svg.text(pChartValueData.dom.info, null, null, pChartValueData.displayValue, "-value", null, null);
		}else if (pChartData.type == "bar"){
			//Ponto
//			pChartValueData.dom.point = dbsfaces.svg.rect(pChartValueData.dom.self, null, null, null, null, null, null, "-point", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			pChartValueData.dom.point = dbsfaces.svg.path(pChartValueData.dom.self, null, "-point", null, null);
			//Ponto com a parte que é diferente dos outros chartvalue(com mesmo index) de outros chart  
			pChartValueData.dom.pointDif = dbsfaces.svg.path(pChartValueData.dom.self, null, "-pointDif", null, null);
			//Path
			pChartValueData.dom.infoPath = dbsfaces.svg.path(pChartValueData.dom.info, null, "-path", null, null);
			//LabelBox
			pChartValueData.dom.infoLabelBox = dbsfaces.svg.rect(pChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-labelBox", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//valueBox
			pChartValueData.dom.infoValueBox = dbsfaces.svg.rect(pChartValueData.dom.info, null, null, null, null, ".2em", ".2em", "-valueBox", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Texto do Label
			pChartValueData.dom.infoLabel = dbsfaces.svg.text(pChartValueData.dom.info, null, null, pChartValueData.label, "-label", null, null);
			//Texto do Valor
			pChartValueData.dom.infoValue = dbsfaces.svg.text(pChartValueData.dom.info, null, null, pChartValueData.displayValue, "-value", null, null);
		}else if (pChartData.type == "pie"){
			//Ponto
			pChartValueData.dom.point = dbsfaces.svg.path(pChartValueData.dom.self, null, "-point", null, {stroke:"currentColor", fill:"none"});
			pChartValueData.dom.pointLink = dbsfaces.svg.path(pChartValueData.dom.self, null, "-pointLink", null, {stroke:"currentColor", fill:"none"});
			//Path que liga o point ao label
//			pChartValueData.dom.infoPath = dbsfaces.svg.path(pChartValueData.dom.self, null, "-path", null, null);
			//Container do value
			pChartValueData.dom.infoValues = dbsfaces.svg.g(pChartValueData.dom.info, "-values", null, null);
			//Texto do Label
			pChartValueData.dom.infoLabel = dbsfaces.svg.text(pChartValueData.dom.infoValues, "1", null, pChartValueData.label, "-label", null, null);
			//Texto do Valor
			pChartValueData.dom.infoValue = dbsfaces.svg.text(pChartValueData.dom.infoValues, "0.1em", ".8em", pChartValueData.displayValue, "-value", null, null);
			if (pChartData.dom.childrenData.length == 1){
				pChartValueData.dom.infoValues.addClass("-hide");
			}
			//BoxPerc
			pChartValueData.dom.infoPercBox = dbsfaces.svg.rect(pChartValueData.dom.infoValues, "7em", "-1em", "1em", "1em", ".2em", ".2em", "-percBox", null, null); //'r' precisa ser um atributo por problema no FIREFOX
			//Texto do Perc
			pChartValueData.dom.infoPerc = dbsfaces.svg.text(pChartValueData.dom.infoValues, "7.4em", ".2em", null, "-perc", null, null);
			//Texto dos Inteiros do Perc
			pChartValueData.dom.infoPercInt = dbsfaces.svg.tspan(pChartValueData.dom.infoPerc, null, "-int", null, null);
			//Texto dos Decimais do Perc
			pChartValueData.dom.infoPercDec = dbsfaces.svg.tspan(pChartValueData.dom.infoPerc, null, "-dec", null, null);
		}
		dbsfaces.ui.moveToBack(pChartValueData.dom.point);

	}
	
};

