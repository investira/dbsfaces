package br.com.dbsoft.ui.component.charts;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
import br.com.dbsoft.util.DBSNumber;


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
		

		DBSCharts xCharts = (DBSCharts) pComponent;

		//		xCharts.setWidth(xCharts.getWidth());
//		xCharts.setHeight(xCharts.getHeight());
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.CHARTS.MAIN + " ";
		String xChartsStyle = "width:" + xCharts.getWidth() + "px; height:" + xCharts.getHeight() + "px;";

		if (xCharts.getStyleClass()!=null){
			xClass = xClass + xCharts.getStyleClass() + " ";
		}

		DBSFaces.initializeChartsValues(xCharts);


		String xClientId = xCharts.getClientId(pContext);
		xWriter.startElement("div", xCharts);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xCharts.getStyle(), null);
			DBSFaces.setAttribute(xWriter, "groupid", xCharts.getGroupId(), null);
			if (xCharts.getShowLabel()){
				DBSFaces.setAttribute(xWriter, "showlabel", xCharts.getShowLabel(), null);
			}
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCharts, DBSPassThruAttributes.getAttributes(Key.CHARTS));
			
			encodeClientBehaviors(pContext, xCharts);
			//CONTAINER--------------------------
			xWriter.startElement("span", xCharts);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER + DBSFaces.CSS.MODIFIER.CONTAINER, null);

				//CAPTION--------------------------
				if (xCharts.getCaption() !=null){
					xWriter.startElement("span", xCharts);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CAPTION + DBSFaces.CSS.THEME.CAPTION, null);
						xWriter.write(xCharts.getCaption());
					xWriter.endElement("span");
				}
	
				//DATA--------------------------
				xWriter.startElement("span", xCharts);
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.DATA, null);
					//CONTAINER--------------------------
					xWriter.startElement("svg", xCharts);
						DBSFaces.setAttribute(xWriter, "xmlns", "http://www.w3.org/2000/svg", null);
						DBSFaces.setAttribute(xWriter, "xmlns:xlink", "http://www.w3.org/1999/xlink", null);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
						DBSFaces.setAttribute(xWriter, "style", xChartsStyle, null);
						//Filters
						pvEncodeFilters(xWriter);
						
						//CONTENT--------------------------
						xWriter.startElement("g", xCharts);
							DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
							//LEFT--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.LEFT, null);
							xWriter.endElement("g");
							
							//VALUE--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.VALUE, null);
								//Linhas do grid
								xWriter.startElement("g", xCharts);
									DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.GRID, null);
									pvEncodeLines(xCharts, xWriter);
								xWriter.endElement("g");
								//Gráficos--------------------------
								DBSFaces.renderChildren(pContext, xCharts);
							xWriter.endElement("g");
							
							//RIGHT--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.RIGHT, null);
							xWriter.endElement("g");
						xWriter.endElement("g");
					xWriter.endElement("svg");
				xWriter.endElement("span");

				//FOOTER--------------------------
				if (xCharts.getFooter() !=null){
					xWriter.startElement("span", xCharts);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.FOOTER, null);
						xWriter.write(xCharts.getFooter());
					xWriter.endElement("span");
				}
			xWriter.endElement("span");

			pvEncodeJS(xClientId, xWriter);
			
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartsId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_charts(xChartsId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	

	private void pvEncodeLines(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		if (pCharts.getCaption()!=null){
			//Linha top
			DBSFaces.encodeSVGLine(pCharts, pWriter, 0D, 0D, pCharts.getWidth().doubleValue(), 0D, DBSFaces.CSS.MODIFIER.LINE, null);
			//Linha bottom
			DBSFaces.encodeSVGLine(pCharts, pWriter, 0D, pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2), pCharts.getWidth().doubleValue(), pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2), DBSFaces.CSS.MODIFIER.LINE, null);
		}
		//Linha base
		if (pCharts.getShowGrid()){
			pvEncodeLinhaDeValores(pCharts, pWriter);
		}
	}

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
			DBSFaces.encodeSVGLine(pCharts, pWriter, 0D, xPosicaoInvertida.doubleValue(), pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 2), xPosicaoInvertida.doubleValue(), DBSFaces.CSS.MODIFIER.LINE, null);
			if (pCharts.getShowGridValue()){
				xPosicaoText = xPosicao;
				//Encode do texto do valor
				xValorTmp = DBSNumber.toDouble(DBSFormat.getFormattedNumber(pCharts.convertYPxToValue(xPosicaoInvertida), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()));
				xFormatedValue = DBSFormat.getFormattedNumber(xValorTmp, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
				DBSFaces.encodeSVGText(pCharts, pWriter, pCharts.getWidth().doubleValue(), xPosicaoText.doubleValue(), xFormatedValue, DBSFaces.CSS.MODIFIER.LABEL, null, null);
			}
			xPosicao += xIncremento;
		}
		if (pCharts.getMinValue() < 0){
			//Encode da linha ZERO
			xPosicaoInvertida = pCharts.getChartHeight() - pCharts.getZeroPosition() + pCharts.getPadding();
			xFormatedValue = DBSFormat.getFormattedNumber(0, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
			xPosicaoText = xPosicaoInvertida + DBSCharts.FontSize.doubleValue() / 2;
			DBSFaces.encodeSVGLine(pCharts, pWriter, 0D, xPosicaoInvertida.doubleValue(), pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 2), xPosicaoInvertida.doubleValue(), DBSFaces.CSS.MODIFIER.LINE, "stroke-dasharray: 2,5; stroke-width: 0.5px;");
//			DBSFaces.encodeSVGText(pCharts, pWriter,  DBSFaces.CSS.MODIFIER.LABEL, "text-anchor:end", pCharts.getWidth().doubleValue(), xPosicaoText.doubleValue(), xFormatedValue);
		}
	}

	private void pvEncodeFilters(ResponseWriter pWriter) throws IOException{
	    StringBuilder xSB = new StringBuilder();
	    xSB.append("<filter id='blur-filter' x='-1' y='-1'>");
	    xSB.append("<feGaussianBlur in='SourceGraphic' stdDeviation='1' />");
	    xSB.append("</filter>");
	    pWriter.write(xSB.toString());
	}
}
