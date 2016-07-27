package br.com.dbsoft.ui.component.chart;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.google.gson.Gson;
import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
import br.com.dbsoft.ui.component.div.DBSDiv;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.charts.DBSCharts;
import br.com.dbsoft.ui.component.charts.DBSCharts.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSColor;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChart.RENDERER_TYPE)
public class DBSChartRenderer extends DBSRenderer {

	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}

	@Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSChart xChart = (DBSChart) pComponent;
		ResponseWriter 			xWriter = pContext.getResponseWriter();
		String 					xClass = CSS.CHART.MAIN;
		TYPE					xType;
		List<IDBSChartDelta>	xDeltaList = null;
		DBSCharts	xCharts;
		if (!(xChart.getParent() instanceof DBSCharts)){
			return;
		}
		xCharts =  (DBSCharts) xChart.getParent();
		xType = TYPE.get(xCharts.getType());
		
		if (xChart.getStyleClass()!=null){
			xClass += xChart.getStyleClass() + " ";
		}

		String xClientId = xChart.getClientId(pContext);
		//Força configuração de cor caso não tenha sido informada.
		if (xChart.getColor() == null){
			xChart.setColor(DBSFaces.calcChartFillcolor(xChart.getDBSColor(), xCharts.getItensCount(), xChart.getItensCount(), xChart.getIndex(), xChart.getItensCount()));
		}
		
		xWriter.startElement("g", xChart);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xChart.getStyle());
			DBSFaces.setAttribute(xWriter, "index", xChart.getIndex());
			DBSFaces.setAttribute(xWriter, "type", xCharts.getType());
			DBSFaces.setAttribute(xWriter, "cs", xChart.getColumnScale());
			DBSFaces.setAttribute(xWriter, "bc", xChart.getColor());
//			DBSFaces.setAttribute(xWriter, "bc", DBSFaces.calcChartFillcolor(xChart.getDBSColor(), xCharts.getItensCount(), xChart.getItensCount(), xChart.getIndex(), xChart.getItensCount()));
			if (xType == TYPE.LINE
			 || xType == TYPE.PIE){
				if (xChart.getShowDelta()){ //Artificio para padronizar o false como não existindo o atributo(comportamento do chrome)
					DBSFaces.setAttribute(xWriter, "showdelta", xChart.getShowDelta());
					if(xChart.getShowDeltaList()){
						DBSFaces.setAttribute(xWriter, "showdeltalist", xChart.getShowDeltaList());
					}
				}
			}
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChart, DBSPassThruAttributes.getAttributes(Key.CHART));
			
			encodeClientBehaviors(pContext, xChart);
			
			if (xType == TYPE.LINE
			 || xType == TYPE.BAR){
				//Divisão onde serão desenhadas as linhas que ligam os pontos no gráfico por linha.
				pvEncodePathGroup(xCharts, xChart, xWriter);
			}
			//Divisão para exibição do delta   
			if (xChart.getShowDelta()){
				xWriter.startElement("g", xChart);
					DBSFaces.setAttribute(xWriter, "class", "-delta");
					if (xType == TYPE.PIE){
						pvEncodePieDeltaTextPaths(xCharts, xChart, xWriter);
					}else if (xType == TYPE.LINE){
					//Divisão onde serão desenhadas as linhas que ligam os pontos no gráfico por linha.
						xDeltaList = pvEncodeDeltaList(xCharts, xChart, pContext, xWriter);
					}
				xWriter.endElement("g");
			}
			//Valores-------------------------
			//Se não foi informado DBSResultSet
			if (DBSObject.isEmpty(xChart.getVar())
			 || DBSObject.isEmpty(xChart.getValueExpression("value"))){
				pvEncodeChartValue(pContext, xChart);
			}else{
				pvEncodeResultSetChartValue(pContext, xChart, xWriter);
			}

			pvEncodeJS(xClientId, xDeltaList, xWriter);
			
		xWriter.endElement("g");
	}
	
	/**
	 * Divisão onde serão desenhadas as linhas que ligam os pontos no gráfico por linha.
	 * O desenho é efetuado via JS no chartValue
	 * @param pCharts
	 * @param pChart
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodePathGroup(DBSCharts pCharts, DBSChart pChart, ResponseWriter pWriter) throws IOException{
		Integer		xChartsWidth;
		Integer 	xChartsHeight;
		xChartsWidth = pCharts.getChartWidth() + pCharts.getPadding();
		xChartsHeight = pCharts.getChartHeight() + pCharts.getPadding();
		pWriter.startElement("g", pChart);
			DBSFaces.setAttribute(pWriter, "class", "-path");
			//Area que irá captura o mousemove
			DBSFaces.encodeSVGRect(pChart, pWriter, pCharts.getPadding() / 2D, pCharts.getPadding() / 2D, xChartsWidth.toString(), xChartsHeight.toString(), CSS.MODIFIER.MASK, null, null);
		pWriter.endElement("g");
	}

	/**
	 * Encodo dos botões de seleção rápida do delta
	 * @param pCharts
	 * @param pChart
	 * @param pContext
	 * @param pWriter
	 * @return
	 * @throws IOException
	 */
	private List<IDBSChartDelta> pvEncodeDeltaList(DBSCharts pCharts, DBSChart pChart, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		if (!pChart.getShowDeltaList()){return null;}
		//Lista com os valores dos deltas
		List<IDBSChartDelta> xDeltaList = pChart.getDeltaList();
		//Define largura e cor dos botões
		float xWidth = DBSNumber.divide(pCharts.getChartWidth(), xDeltaList.size()).floatValue();
		DBSColor.RGB xColor = pChart.getDBSColor().toRGB();
		String xStyle = "width:" + xWidth + "px; background-color:" + new DBSColor(xColor.getRed(), xColor.getGreen(), xColor.getBlue(), .2f).toHSLA() + ";";
		//Facet com os botões
		DBSDiv xDeltaListContent = (DBSDiv) pChart.getFacet("deltalist");
		//Se não informado o facet, cria contendo botões a partir da lista com os valores dos deltas e
		if (xDeltaListContent == null){
			xDeltaListContent = (DBSDiv) pContext.getApplication().createComponent(DBSDiv.COMPONENT_TYPE);
			xDeltaListContent.setId("deltalist");
			xDeltaListContent.setStyleClass("-content");
			pChart.getFacets().put("deltalist", xDeltaListContent);
			//Adiciona botões com as opções dos deltas pré definidos
			for (IDBSChartDelta xChartDelta: xDeltaList){
				DBSButton xDeltaButton = (DBSButton) pContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
				xDeltaButton.setId(xChartDelta.getId());
				xDeltaButton.setLabel(xChartDelta.getLabel());
				xDeltaButton.setIconClass(xChartDelta.getIconClass());
				xDeltaButton.setTooltip(xChartDelta.getTooltip());
				xDeltaButton.setonclick("null");
				xDeltaButton.setStyle(xStyle);
				xDeltaListContent.getChildren().add(xDeltaButton);
				//Força que id atribuido ao botão seja efetivamente o gerado/configurado 
				xChartDelta.setId(xDeltaButton.getClientId());
			}
		}
		//Encode do foreignObject que conterá os botões
		pWriter.startElement("foreignObject", pChart);
			DBSFaces.setAttribute(pWriter, "xmlns","http://www.w3.org/1999/xhtml");
			DBSFaces.setAttribute(pWriter, "id", pChart.getClientId() + "_deltalist");
			DBSFaces.setAttribute(pWriter, "class", "-deltaList -foreignobject");
			DBSFaces.setAttribute(pWriter, "x", pCharts.getPadding() + "px");
			DBSFaces.setAttribute(pWriter, "y", ((pCharts.getPadding() * 2) + pCharts.getChartHeight()) + "px");
			DBSFaces.setAttribute(pWriter, "width", pCharts.getChartWidth());
			DBSFaces.setAttribute(pWriter, "height", "1.7em");
			DBSFaces.setAttribute(pWriter, "width", ".5");
			DBSFaces.setAttribute(pWriter, "height", ".5");
			pWriter.startElement("div", pChart);
				DBSFaces.setAttribute(pWriter, "class", "-container");
				xDeltaListContent.encodeAll(pContext);
			pWriter.endElement("div");
		pWriter.endElement("foreignObject");
		return xDeltaList;
	}
	
	/**
	 * Paths que serão utilizados para posicionar os valores do delta/somatório
	 * @param pCharts
	 * @param pChart
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodePieDeltaTextPaths(DBSCharts pCharts, DBSChart pChart, ResponseWriter pWriter) throws IOException{
		Double xMiddleRadius = (pCharts.getPieChartWidth() / 2) + pChart.getPieChartRelativeRadius(pCharts) - 1;
		Point2D xPathPoint1 = new Point2D.Double();
		Point2D xPathPoint2 = new Point2D.Double();
		//Arco a esquerda
		xPathPoint1 = DBSNumber.circlePoint(pCharts.getCenter(), xMiddleRadius, 51 * DBSNumber.PIDiameterFactor);
		xPathPoint2 = DBSNumber.circlePoint(pCharts.getCenter(), xMiddleRadius, 99 * DBSNumber.PIDiameterFactor);
		pvEncodePieDeltaTextPath(pChart, pWriter, "_l",  xMiddleRadius, xPathPoint1, xPathPoint2);
		//Arco a direita
		xPathPoint1 = DBSNumber.circlePoint(pCharts.getCenter(), xMiddleRadius, 1 * DBSNumber.PIDiameterFactor);
		xPathPoint2 = DBSNumber.circlePoint(pCharts.getCenter(), xMiddleRadius, 49 * DBSNumber.PIDiameterFactor);
		pvEncodePieDeltaTextPath(pChart, pWriter, "_r", xMiddleRadius, xPathPoint1, xPathPoint2);
	}

	/**
	 * Encode do path
	 * @param pChart
	 * @param pWriter
	 * @param pSide
	 * @param pMiddleRadius
	 * @param pPathPoint1
	 * @param pPathPoint2
	 * @throws IOException
	 */
	private void pvEncodePieDeltaTextPath(DBSChart pChart, ResponseWriter pWriter, String pSide, Double pMiddleRadius, Point2D pPathPoint1, Point2D pPathPoint2) throws IOException{
		StringBuilder xPath = new StringBuilder();
	    xPath.append("M" + pPathPoint1.getX() + "," + pPathPoint1.getY()); //Ponto inicial do arco 
		xPath.append("A" + pMiddleRadius + "," + pMiddleRadius + " 0 0 1 " + pPathPoint2.getX() + "," + pPathPoint2.getY()); //Arco externo até o ponto final  
		DBSFaces.encodeSVGPath(pChart, pWriter, xPath.toString(), "-path" + pSide, null, "id=" + pvGetDeltaPathId(pChart, pSide) + "; fill=none;");
	}
	
	private void pvEncodeJS(String pClientId, List<IDBSChartDelta> pDeltaList, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		Gson xDeltaListJson = new Gson();
//		xDeltaListJson.toJson(pChart.getDeltaList());
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_chart(xChartId, " + xDeltaListJson.toJsonTree(pDeltaList, List.class) + "); \n" +
                     "}); \n"; 
		pWriter.write(xJS); 
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	/**
	 * Encode do corpo da tabela contendo as linhas com os dados
	 * @param pContext
	 * @param pChart
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeResultSetChartValue(FacesContext pContext, DBSChart pChart, ResponseWriter pWriter) throws IOException {
        int xRowCount = pChart.getRowCount(); 
		pChart.setRowIndex(-1);
		//Loop por todos os registros lidos
		//Lido de forma decrescentes por o saveState e restoreState invertou
		//a ordem da consulta
		for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {
        	pChart.setRowIndex(xRowIndex);
        	//Loop no componente filho contendo as definições dos valores
			for (UIComponent xC : pChart.getChildren()){
				if (xC instanceof DBSChartValue){
					DBSChartValue xChartValue = (DBSChartValue) xC;
					if (xChartValue.isRendered()){
						xChartValue.restoreState(FacesContext.getCurrentInstance(), xChartValue.getSavedState());
						xChartValue.encodeAll(pContext);
					}
				}
			}
        }
        pChart.setRowIndex(-1);
	}
	
	private void pvEncodeChartValue(FacesContext pContext, DBSChart pChart) throws IOException {
		DBSFaces.renderChildren(pContext, pChart);
	}
	
	private String pvGetDeltaPathId(DBSChart pChart,String pSide){
		return pChart.getClientId() + "_deltapath" + pSide;
	}


}
