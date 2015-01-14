package br.com.dbsoft.ui.component.chart;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSChart xChart = (DBSChart) pComponent;
		if (xChart.getType()==null){return;}		

		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.CHART.MAIN + " ";
		String xStyle = "width:" + xChart.getWidth() + "px; height:" + xChart.getHeight() + "px;";
//		if (xChart.getStyle()!=null){
//			xStyle += " " + xChart.getStyle();
//		}

		if (xChart.getStyleClass()!=null){
			xClass = xClass + xChart.getStyleClass() + " ";
		}

		pvCalcularValores(xChart);
		
		String xClientId = xChart.getClientId(pContext);
		xWriter.startElement("div", xChart);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xChart.getStyle(), null);
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChart, DBSPassThruAttributes.getAttributes(Key.CHART));
			
			encodeClientBehaviors(pContext, xChart);
			//CONTAINER--------------------------
			xWriter.startElement("span", xChart);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);

				//CAPTION--------------------------
				if (xChart.getCaption() !=null){
					xWriter.startElement("span", xChart);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CAPTION, null);
						xWriter.write(xChart.getCaption());
					xWriter.endElement("span");
				}
	
				//DATA--------------------------
				xWriter.startElement("span", xChart);
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.DATA, null);
					//CONTAINER--------------------------
					xWriter.startElement("svg", xChart);
						DBSFaces.setAttribute(xWriter, "xmlns", "http://www.w3.org/2000/svg", null);
						DBSFaces.setAttribute(xWriter, "xmlns:xlink", "http://www.w3.org/1999/xlink", null);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
						DBSFaces.setAttribute(xWriter, "style", xStyle, null);
						
						//CONTENT--------------------------
						xWriter.startElement("g", xChart);
							DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
							//LEFT--------------------------
							xWriter.startElement("g", xChart);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.LEFT, null);
							xWriter.endElement("g");
							
							//DATA--------------------------
							xWriter.startElement("g", xChart);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.VALUE, null);
								xWriter.startElement("g", xChart);
									DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.GRID, null);
									//Linhas de marcação
									pvEncodeLines(xChart, xWriter);
								xWriter.endElement("g");
								
								renderChildren(pContext, xChart);
							xWriter.endElement("g");
							
							//RIGHT--------------------------
							xWriter.startElement("g", xChart);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.RIGHT, null);
							xWriter.endElement("g");
						xWriter.endElement("g");
					xWriter.endElement("svg");
				xWriter.endElement("span");

				//FOOTER--------------------------
				if (xChart.getFooter() !=null){
					xWriter.startElement("span", xChart);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.FOOTER, null);
						xWriter.write(xChart.getFooter());
					xWriter.endElement("span");
				}
			xWriter.endElement("span");

			pvEncodeJS(xClientId, xWriter);
			
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_chart(xChartId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	private void pvCalcularValores(DBSChart pChart){
		Double xMinValue = 0D;
		Double xMaxValue = 0D;
		Integer xZeroPosition = 0;
		Integer xCount = 0;
		Integer xWhiteSpace = 0;
		for (UIComponent xChild:pChart.getChildren()){
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
				if (!pChart.getShowLabel()){
					if (!DBSObject.isEmpty(xChartValue.getLabel())){
						pChart.setShowLabel(true);
					}
				}
			}
		}

		//Largura da coluna dos valores caso seja para exibi-la
		if (pChart.getShowGrid() 
		 && pChart.getShowGridValue()){
			pChart.setFormatMaskWidth(DBSNumber.multiply(pChart.getValueFormatMask().length(), 5.5D).intValue());
		}else{
			pChart.setFormatMaskWidth(0);
		}
		//Valor Mínimo
		pChart.setMinValue(xMinValue);
		//Valor Máximo
		pChart.setMaxValue(xMaxValue);
		if (pChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)
		 || pChart.getType().equalsIgnoreCase(DBSChart.TYPE.LINE)){
			//Calcula posição da linha zero
			xZeroPosition = DBSNumber.multiply(pChart.getChartHeight(),
					   						   DBSNumber.divide(DBSNumber.abs(xMaxValue), 
					   								   			pChart.getTotalValue())).intValue();
			//Distribui o espaço que sobra total, entre as cada coluna
			if (xCount>1){
				xWhiteSpace = DBSNumber.divide(DBSNumber.subtract(pChart.getWidth(), 
												 				  DBSNumber.multiply(xCount, pChart.getLineWidth()),
												 				  pChart.getFormatMaskWidth()),
											   xCount-1).intValue();
			}
		}
		//Posição da linha do zero
		pChart.setZeroPosition(xZeroPosition);
		//Espaço entre as colunas
		pChart.setWhiteSpace(xWhiteSpace);
		//Quantidade de linhas. 3 é a quantidade mínima de linhas
		pChart.setNumberOfGridLines(3 + DBSNumber.divide(pChart.getChartHeight(), 60).intValue());
	}
	
	private void pvEncodeLines(DBSChart pChart, ResponseWriter pWriter) throws IOException{
		if (pChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)
		 || pChart.getType().equalsIgnoreCase(DBSChart.TYPE.LINE)){
			if (pChart.getCaption()!=null){
				//Linha top
				DBSFaces.encodeSVGLine(pChart, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, 0, pChart.getWidth().intValue(), 0);
				//Linha bottom
				DBSFaces.encodeSVGLine(pChart, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, pChart.getChartHeight().intValue()-1, pChart.getWidth().intValue(), pChart.getChartHeight().intValue()-1);
			}
			//Linha base
//			DBSFaces.encodeSVGLine(pChart, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, pChart.getZeroPosition(), pChart.getWidth().intValue(), pChart.getZeroPosition());
			if (pChart.getShowGrid()){
				pvEncodeLinhaDeValores(pChart, pWriter);
			}
		}
	}

	private void pvEncodeLinhaDeValores(DBSChart pChart, ResponseWriter pWriter) throws IOException{
		Double xIncrementoValor = DBSNumber.divide(pChart.getTotalValue(), pChart.getNumberOfGridLines()).doubleValue();
		Double xIncrementoPosicao = DBSNumber.divide(pChart.getChartHeight() - (DBSChart.Padding * 2), pChart.getNumberOfGridLines()).doubleValue();
		Double xPosicao = DBSNumber.toBigDecimal(pChart.getZeroPosition()).remainder(DBSNumber.toBigDecimal(xIncrementoPosicao)).doubleValue();
		Double xValor = (DBSNumber.divide(pChart.getMaxValue() / xIncrementoValor).intValue()) *  xIncrementoValor;
		Integer xAjustePadrao = DBSChart.FontSize / 2;
		Integer xAjuste = xAjustePadrao;
		String xFormatedValue;
		Double xValorTmp;
		if (xPosicao < xAjustePadrao){
			xAjuste = DBSChart.FontSize;
		}
		for (int i=0; i < pChart.getNumberOfGridLines(); i++){
			//Encode da linha do grid até o inicio do texto do valor
			DBSFaces.encodeSVGLine(pChart, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, xPosicao.intValue(), pChart.getWidth().intValue() - pChart.getFormatMaskWidth(), xPosicao.intValue());
			if (pChart.getShowGridValue()){
				//Encode do texto do valor
				//Artifício para excluir o sinal (-) do valor zero quando o valor for por exemplo -0.002
				xValorTmp = DBSNumber.toDouble(DBSFormat.getFormattedNumber(xValor, NUMBER_SIGN.MINUS_PREFIX, pChart.getValueFormatMask()));
				xFormatedValue = DBSFormat.getFormattedNumber(xValorTmp, NUMBER_SIGN.MINUS_PREFIX, pChart.getValueFormatMask());
				DBSFaces.encodeSVGText(pChart, pWriter,  DBSFaces.CSS.MODIFIER.LABEL, "text-anchor:end", pChart.getWidth().intValue(), xPosicao.intValue() + xAjuste, xFormatedValue);
			}
			xValor -= xIncrementoValor;
			xPosicao += xIncrementoPosicao;
			if (xPosicao > pChart.getChartHeight() - (DBSChart.Padding * 2) - xAjustePadrao){
				xAjuste = 0;
			}else{
				xAjuste = xAjustePadrao;
			}
		}
	}


}
