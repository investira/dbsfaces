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
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
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
		DBSCharts xCharts = (DBSCharts) pComponent;

		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.CHARTS.MAIN + " ";
		String xStyle = "width:" + xCharts.getWidth() + "px; height:" + xCharts.getHeight() + "px;";

		if (xCharts.getStyleClass()!=null){
			xClass = xClass + xCharts.getStyleClass() + " ";
		}

		pvCalcularValores(xCharts);
		
		String xClientId = xCharts.getClientId(pContext);
		xWriter.startElement("div", xCharts);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xCharts.getStyle(), null);
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCharts, DBSPassThruAttributes.getAttributes(Key.CHARTS));
			
			encodeClientBehaviors(pContext, xCharts);
			//CONTAINER--------------------------
			xWriter.startElement("span", xCharts);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);

				//CAPTION--------------------------
				if (xCharts.getCaption() !=null){
					xWriter.startElement("span", xCharts);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CAPTION, null);
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
						DBSFaces.setAttribute(xWriter, "style", xStyle, null);
						
						//CONTENT--------------------------
						xWriter.startElement("g", xCharts);
							DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
							//LEFT--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.LEFT, null);
							xWriter.endElement("g");
							
							//DATA--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.VALUE, null);
								xWriter.startElement("g", xCharts);
									DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.GRID, null);
									//Linhas do grid
									pvEncodeLines(xCharts, xWriter);
								xWriter.endElement("g");
								
								renderChildren(pContext, xCharts);
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
	
	private void pvCalcularValores(DBSCharts pCharts){
		Double xMinValue = 0D;
		Double xMaxValue = 0D;
		Integer xZeroPosition = 0;
		Integer xChartValueCount = 0;
		Integer xCount = 0;
		Integer xWhiteSpace = 0;
		for (UIComponent xChart:pCharts.getChildren()){
			if (xChart instanceof DBSChart){
				xCount = 0;
				for (UIComponent xChild:xChart.getChildren()){
					if (xChild instanceof DBSChartValue){
						DBSChartValue xChartValue = (DBSChartValue) xChild;
						xCount++;
						Double xValue = xChartValue.getValue();
						if (xValue < 0 
						 && xValue < xMinValue){
							xMinValue = xValue; 
						}
						if (xValue > 0 
						 && xValue > xMaxValue){
							xMaxValue = xValue; 
						}
						//Verifica se label foi definida e seta indicador que há label a ser exibida
						if (!pCharts.getShowLabel()){
							if (!DBSObject.isEmpty(xChartValue.getLabel())){
								pCharts.setShowLabel(true);
							}
						}
					}
					//Quantidade de colunas(itens)
					if (xCount > xChartValueCount){
						xChartValueCount = xCount;
					}
				}
			}
		}

		//Largura da coluna dos valores caso seja para exibi-la
		if (pCharts.getShowGrid() 
		 && pCharts.getShowGridValue()){
			pCharts.setFormatMaskWidth(DBSNumber.multiply(pCharts.getValueFormatMask().length(), 5.5D).intValue());
		}else{
			pCharts.setFormatMaskWidth(0);
		}
		//Valor Mínimo
		if (pCharts.getMinValue() == null
		 || pCharts.getMinValue() < xMinValue){
			pCharts.setMinValue(xMinValue);
		}
		//Valor Máximo
		if (pCharts.getMaxValue() == null
		 || pCharts.getMaxValue() < xMaxValue){
			pCharts.setMaxValue(xMaxValue);
		}
		//Calcula posição da linha zero
		xZeroPosition = DBSNumber.multiply(pCharts.getChartHeight(),
				   						   DBSNumber.divide(DBSNumber.abs(pCharts.getMaxValue()), 
				   								   			pCharts.getTotalValue())).intValue();
		//Distribui o espaço que sobra total, entre as cada coluna
		if (xChartValueCount>1){
			xWhiteSpace = DBSNumber.divide(DBSNumber.subtract(pCharts.getWidth(), 
											 				  DBSNumber.multiply(xChartValueCount, pCharts.getLineWidth()),
											 				  pCharts.getFormatMaskWidth()),
										   xChartValueCount-1).intValue();
		}
		//Posição da linha do zero
		pCharts.setZeroPosition(xZeroPosition);
		//Espaço entre as colunas
		pCharts.setWhiteSpace(xWhiteSpace);
		//Quantidade de linhas. 3 é a quantidade mínima de linhas
		pCharts.setNumberOfGridLines(3 + DBSNumber.divide(pCharts.getChartHeight(), 60).intValue());
	}
	
	private void pvEncodeLines(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		if (pCharts.getCaption()!=null){
			//Linha top
			DBSFaces.encodeSVGLine(pCharts, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, 0, pCharts.getWidth().intValue(), 0);
			//Linha bottom
			DBSFaces.encodeSVGLine(pCharts, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, pCharts.getChartHeight().intValue()-1, pCharts.getWidth().intValue(), pCharts.getChartHeight().intValue()-1);
		}
		//Linha base
//			DBSFaces.encodeSVGLine(pCharts, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, pCharts.getZeroPosition(), pCharts.getWidth().intValue(), pCharts.getZeroPosition());
		if (pCharts.getShowGrid()){
			pvEncodeLinhaDeValores(pCharts, pWriter);
		}
	}

	private void pvEncodeLinhaDeValores(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		Double xIncrementoValor = DBSNumber.divide(pCharts.getTotalValue(), pCharts.getNumberOfGridLines()).doubleValue();
		Double xIncrementoPosicao = DBSNumber.divide(pCharts.getChartHeight() - (DBSCharts.Padding * 2), pCharts.getNumberOfGridLines()).doubleValue();
		Double xPosicao = DBSNumber.toBigDecimal(pCharts.getZeroPosition()).remainder(DBSNumber.toBigDecimal(xIncrementoPosicao)).doubleValue();
		Double xValor = (DBSNumber.divide(pCharts.getMaxValue() / xIncrementoValor).intValue()) *  xIncrementoValor;
		Integer xAjustePadrao = DBSCharts.FontSize / 2;
		Integer xAjuste = xAjustePadrao;
		String xFormatedValue;
		Double xValorTmp;
		if (xPosicao < xAjustePadrao){
			xAjuste = DBSCharts.FontSize;
		}
		for (int i=0; i < pCharts.getNumberOfGridLines(); i++){
			//Encode da linha do grid até o inicio do texto do valor
			DBSFaces.encodeSVGLine(pCharts, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, xPosicao.intValue(), pCharts.getWidth().intValue() - pCharts.getFormatMaskWidth(), xPosicao.intValue());
			if (pCharts.getShowGridValue()){
				//Encode do texto do valor
				//Artifício para excluir o sinal (-) do valor zero quando o valor for por exemplo -0.002
				xValorTmp = DBSNumber.toDouble(DBSFormat.getFormattedNumber(xValor, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()));
				xFormatedValue = DBSFormat.getFormattedNumber(xValorTmp, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
				DBSFaces.encodeSVGText(pCharts, pWriter,  DBSFaces.CSS.MODIFIER.LABEL, "text-anchor:end", pCharts.getWidth().intValue(), xPosicao.intValue() + xAjuste, xFormatedValue);
			}
			xValor -= xIncrementoValor;
			xPosicao += xIncrementoPosicao;
			if (xPosicao > pCharts.getChartHeight() - (DBSCharts.Padding * 2) - xAjustePadrao){
				xAjuste = 0;
			}else{
				xAjuste = xAjustePadrao;
			}
		}
	}


}
