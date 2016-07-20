package br.com.dbsoft.ui.component.charts;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.chart.DBSChart;
import br.com.dbsoft.ui.component.charts.DBSCharts.TYPE;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSCharts.RENDERER_TYPE)
public class DBSChartsRenderer extends DBSRenderer {

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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		
		DBSCharts 		xCharts = (DBSCharts) pComponent;
		//Tipo de gráficos não informado
		if (xCharts.getType()==null){return;}
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClass = CSS.CHARTS.MAIN + CSS.MODIFIER.NOT_SELECTABLE;
		boolean			xPreRender = pvIsPreRender(pContext, xCharts);

		if (xCharts.getStyleClass()!=null){
			xClass += xCharts.getStyleClass();
		}

		//Configura altura e largura a partir dos valores enviados na chamada ajax do preRender
		if (!xPreRender){
			String xParams = pContext.getExternalContext().getRequestParameterMap().get("params");
			if (xParams != null){
				String[] xDimensions = xParams.split(",");
				xCharts.setWidth(DBSNumber.toInteger(xDimensions[0]));
				xCharts.setHeight(DBSNumber.toInteger(xDimensions[1]));
				xCharts.setFontSize(DBSNumber.toInteger(xDimensions[2]));
//				System.out.println(xCharts.getId() + "\t Reseted");
				DBSFaces.initializeChartsValues(xCharts);
			}
		}
//		System.out.println(xCharts.getId() + "\t" + xPreRender + "\t" + xCharts.getWidth() + "\t" + xCharts.getHeight() + "\t" + xCharts.getFontSize());
		//Inicializa valores de controle 


		String xClientId = xCharts.getClientId(pContext);
		xWriter.startElement("div", xCharts);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xCharts.getStyle());
			DBSFaces.setAttribute(xWriter, "type", xCharts.getType());
			DBSFaces.setAttribute(xWriter, "groupid", xCharts.getGroupId());
			if (xCharts.getShowLabel()){
				DBSFaces.setAttribute(xWriter, "showlabel", xCharts.getShowLabel());
			}
			DBSFaces.setAttribute(xWriter, "diameter", xCharts.getDiameter());
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCharts, DBSPassThruAttributes.getAttributes(Key.CHARTS));
			
			//Força para que o encode deste componente seja efetuado após, via chamada ajax.
			//para que a altura e largura seja cálculada via js e enviada no request ajax.
//			if (pvEncodeLater(pContext, xCharts)){
			if (xPreRender){
	    		xWriter.startElement("div", xCharts);
	    			DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.LOADING);
				xWriter.endElement("div");
			}else{
				pvEncodeContainer(pContext, xCharts, xWriter);
			}
			pvEncodeJS(xClientId, xWriter, xPreRender);
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter, boolean pPreRender) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartsId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_charts(xChartsId," + pPreRender + "); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	private void pvEncodeContainer(FacesContext pContext, DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		encodeClientBehaviors(pContext, pCharts);
		//CONTAINER--------------------------
		pWriter.startElement("div", pCharts);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);

			//CAPTION--------------------------
			if (pCharts.getCaption() !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CAPTION + CSS.THEME.CAPTION + CSS.NOT_SELECTABLE);
					pWriter.write(pCharts.getCaption());
				pWriter.endElement("div");
			}

			//DATA--------------------------
			pWriter.startElement("div", pCharts);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.DATA);
				//CONTAINER--------------------------
				pWriter.startElement("svg", pCharts);
					DBSFaces.setAttribute(pWriter, "xmlns", "http://www.w3.org/2000/svg");
					DBSFaces.setAttribute(pWriter, "xmlns:xlink", "http://www.w3.org/1999/xlink");
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
					DBSFaces.setAttribute(pWriter, "width", pCharts.getWidth());
					DBSFaces.setAttribute(pWriter, "height", pCharts.getHeight() - pCharts.getCaptionHeight() - pCharts.getFooterHeight());
					//Defs
					pvEncodeDefs(pCharts, pWriter);
					
					//CONTENT--------------------------
					pWriter.startElement("g", pCharts);
						DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
						//LABELS QUANDO HOUVER MAIS DE UM GRÁFICO--------------------------
						boolean xHasLabel= pvEncodeChartCaptions(pCharts, pWriter);
						//LEFT--------------------------
						pWriter.startElement("g", pCharts);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.LEFT);
						pWriter.endElement("g");
						
						//VALUE--------------------------
						pWriter.startElement("g", pCharts);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.VALUE);
							//Linhas do grid
							pWriter.startElement("g", pCharts);
								//ATENÇÃO:Não retirar os trim() desta class( CSS.CHARTS.MAIN.trim() + CSS.MODIFIER.GRID.trim())
								DBSFaces.setAttribute(pWriter, "class", CSS.CHARTS.MAIN.trim() + CSS.MODIFIER.GRID.trim());
								if (pCharts.getShowDelta()){ 
									DBSFaces.setAttribute(pWriter, "showdelta", pCharts.getShowDelta());
								}
								pvEncodeLines(pCharts, pWriter, !xHasLabel);
							pWriter.endElement("g");
							//Gráficos--------------------------
							DBSFaces.renderChildren(pContext, pCharts);
						pWriter.endElement("g");
						
						//RIGHT--------------------------
						pWriter.startElement("g", pCharts);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.RIGHT);
						pWriter.endElement("g");
					pWriter.endElement("g");
				pWriter.endElement("svg");
			pWriter.endElement("div");

			//FOOTER--------------------------
			if (pCharts.getFooter() !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.FOOTER);
					pWriter.write(pCharts.getFooter());
				pWriter.endElement("div");
			}
		pWriter.endElement("div");
	}
	/**
	 * Labels para selecionar cada um dos gráficos
	 * @param pCharts
	 * @param pWriter
	 * @return
	 * @throws IOException
	 */
	private Boolean pvEncodeChartCaptions(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		boolean xHasLabels = false;
		if (pCharts.getItensCount() > 1){
			pWriter.startElement("g", pCharts);
			DBSFaces.setAttribute(pWriter, "class", "-captions");
			for (UIComponent xObject:pCharts.getChildren()){
				if (xObject instanceof DBSChart){
					DBSChart xChart = (DBSChart) xObject;
					if (TYPE.get(pCharts.getType()) == TYPE.LINE){
						pvEncodeLabel(pCharts, xChart, pWriter);
						xHasLabels = true;
					}
				}
			}
			pWriter.endElement("g");
		}
		return xHasLabels;
	}
	
	private void pvEncodeLabel(DBSCharts pCharts, DBSChart pChart, ResponseWriter pWriter) throws IOException{
		String xLabel = DBSObject.getNotEmpty(pChart.getLabel(), pChart.getId());
		Double xWidth = DBSNumber.divide(pCharts.getWidth(), pCharts.getChildCount()).doubleValue();
		Double xX = xWidth * (pChart.getIndex() - 1);
		pWriter.startElement("g", pCharts);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
			DBSFaces.setAttribute(pWriter, "chartid", pChart.getClientId());
				DBSFaces.encodeSVGRect(pCharts, pWriter, xX.toString(), "0", xWidth.toString(), "1.7em", null, null, "fill=url(#" + pChart.getClientId() + "_linestroke); stroke=url(#" + pChart.getClientId() + "_linestroke)");
				DBSFaces.encodeSVGText(pCharts, pWriter, xX.toString(), "1.2em", xLabel, null, null, "transform:translateY(1.7em)");
		pWriter.endElement("g");
	}
	
	/**
	 * Linhas horizontais dos valores
	 * @param pCharts
	 * @param pWriter
	 * @param pEncodeTopLine
	 * @throws IOException
	 */
	private void pvEncodeLines(DBSCharts pCharts, ResponseWriter pWriter, boolean pEncodeTopLine) throws IOException{
		if (pCharts.getCaption()!=null){
			//Linha top
			if (pEncodeTopLine){
				DBSFaces.encodeSVGLine(pCharts, pWriter, 0D, 0D, pCharts.getWidth().doubleValue(), 0D, CSS.MODIFIER.LINE, null, null);
			}
			//Linha bottom
			DBSFaces.encodeSVGLine(pCharts, pWriter, 0D, pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2), pCharts.getWidth().doubleValue(), pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2), CSS.MODIFIER.LINE, null, null);
		}
		//Linha base
		if (pCharts.getShowGrid()){
			pvEncodeLinhaDeValores(pCharts, pWriter);
		}
	}
	
	/**
	 * Linhas horizontais dos valores
	 * @param pCharts
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeLinhaDeValores(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		Double xIncremento = DBSNumber.divide(pCharts.getChartHeight(), pCharts.getNumberOfGridLines()).doubleValue();
		Double xPosicao = (xIncremento / 2);
		Double xPosicaoText;
		Double xPosicaoInvertida;
		Double xValorTmp;
		String xFormatedValue;
		for (int i=0; i < pCharts.getNumberOfGridLines(); i++){
			xPosicaoInvertida = pCharts.getChartHeight() - xPosicao + pCharts.getPadding();
			//Encode da linha do grid até o inicio do texto do valor
			DBSFaces.encodeSVGLine(pCharts, pWriter, pCharts.getPadding(), xPosicaoInvertida.doubleValue(), pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 1), xPosicaoInvertida.doubleValue(), CSS.MODIFIER.LINE, null, null);
			if (pCharts.getShowGridValue()
			&& !pCharts.getShowDelta()){ 
//			if (pCharts.getShowGridValue()){ 
				xPosicaoText = xPosicao;
				//Encode do texto do valor
				xValorTmp = DBSNumber.toDouble(DBSFormat.getFormattedNumber(pCharts.convertYPxToValue(xPosicaoInvertida), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()));
				xFormatedValue = DBSFormat.getFormattedNumber(xValorTmp, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
				DBSFaces.encodeSVGText(pCharts, pWriter, pCharts.getWidth().doubleValue() -pCharts.getPadding(), xPosicaoText.doubleValue(), xFormatedValue, CSS.MODIFIER.LABEL, null, null);
			}
			xPosicao += xIncremento;
		}
		if (pCharts.getMinValue() < 0){
			//Encode da linha ZERO
			xPosicaoInvertida = pCharts.getChartHeight() - pCharts.getZeroPosition().doubleValue() + pCharts.getPadding();
			xFormatedValue = DBSFormat.getFormattedNumber(0, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
			xPosicaoText = xPosicaoInvertida + pCharts.getFontSize() / 2;
			DBSFaces.encodeSVGLine(pCharts, pWriter, pCharts.getPadding(), xPosicaoInvertida.doubleValue(), pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 1), xPosicaoInvertida.doubleValue(), CSS.MODIFIER.LINE, "stroke-dasharray: 2,5; stroke-width: 1px;", null);
		}
	}

	//Tag padrão onde serão inseridos as definições para posterior reutilização
	private void pvEncodeDefs(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("defs", pCharts);
//			pvEncodeDefsMarker(pCharts, pWriter);
		pWriter.endElement("defs");
	}

	/**
	 * Retorna se é uma chamada que deverá efetivamente efetuar o encode co componente ou se. 
	 * fará uma chamada ajax para que posteriormente seja efetuado o encode.<br/>
	 * 
	 * @param pContext
	 * @param pCharts
	 * @return
	 */
	private boolean pvIsPreRender(FacesContext pContext, DBSCharts pCharts){
		 if (pContext.getPartialViewContext().getRenderIds().contains(pCharts.getClientId())
		  && pContext.getExternalContext().getRequestParameterMap().get("params") != null){
			return false;
		}
		return true;
	}
}
