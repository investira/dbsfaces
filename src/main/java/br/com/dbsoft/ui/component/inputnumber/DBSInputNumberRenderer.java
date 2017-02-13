package br.com.dbsoft.ui.component.inputnumber;

import java.io.IOException;
import java.math.BigDecimal;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSString;

/**
 * @author ricardo.villar
 *
 */
@FacesRenderer(componentFamily = DBSFaces.FAMILY, rendererType = DBSInputNumber.RENDERER_TYPE)
public class DBSInputNumberRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		DBSInputNumber xInputNumber = (DBSInputNumber) pComponent;
        if(xInputNumber.getReadOnly()) {return;}
    	
		decodeBehaviors(pContext, xInputNumber);

		String xClientIdAction = getInputDataClientId(xInputNumber);
		if (pContext.getExternalContext().getRequestParameterMap().containsKey(xClientIdAction)) {
			Object xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientIdAction);
			try {
				//Primeiramente converte para double para forçar um valor não nulo
				xSubmittedValue = DBSNumber.toDouble(xSubmittedValue);
				//Este submittedValue irá converter o valor para o tipo de dado do campo que o receberá
				xInputNumber.setSubmittedValue(xSubmittedValue);
			} catch (Exception xE) {
				wLogger.error("Erro na conversão do inputnumber", xE);
			}
		}
	}

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()) {
			return;
		}
		DBSInputNumber xInputNumber = (DBSInputNumber) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xInputNumber.getClientId(pContext);
		String xClass = CSS.INPUTNUMBER.MAIN + CSS.THEME.INPUT;
		
		pvInitialize(xInputNumber);
		
		if (xInputNumber.getStyleClass() != null) {
			xClass += xInputNumber.getStyleClass();
		}

		xWriter.startElement("div", xInputNumber);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xInputNumber.getStyle());
//			if (xInputNumber.getIncrement()){
//				DBSFaces.encodeAttribute(xWriter, "increment", "increment");
//			}
			//Container
			xWriter.startElement("div", xInputNumber);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
					DBSFaces.encodeLabel(pContext, xInputNumber, xWriter);
					pvEncodeInput(pContext, xInputNumber, xWriter);
					DBSFaces.encodeRightLabel(pContext, xInputNumber, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xInputNumber, xInputNumber.getTooltip());
		xWriter.endElement("div");

		//Não gera o JS quando for somente leitura
		if (!xInputNumber.getReadOnly()){
			DBSFaces.encodeJavaScriptTagStart(pComponent, xWriter);
			//Comentado o JS com $(document).ready por não inicializar corretamente o campo no IE
			
			String xJS = "$(document).ready(function() { \n"
						+ " var xInputNumberId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n "
						+ " var xInputNumberDataId = dbsfaces.util.jsid('" + getInputDataClientId(xInputNumber) + "'); \n "
						+ " dbs_inputNumber(xInputNumberId, xInputNumberDataId," + pvGetMaskParm(xInputNumber) + "); \n" + "}); \n";
			xWriter.write(xJS);
			DBSFaces.encodeJavaScriptTagEnd(xWriter);
		}
	}

	private String pvGetMaskParm(DBSInputNumber pInputNumber) {
		String xType;
		String xMask;
		String xMaskEmptyChr;
		String xDecDigits;
		String xGroupSymbol = "";
		String pDecSymbol = "";
		String pGroupDigits = "3";
		if (pInputNumber.getLeadingZero()) {
			xType = "fixed";
		} else {
			xType = "number";
		}
		if (pInputNumber.getLeadingZero()) {
			xMask = DBSString.repeat("9", pInputNumber.getSize());
			xMaskEmptyChr = "0";
		} else {
			xMask = "0";
			xMaskEmptyChr = " ";
		}
		xDecDigits = pInputNumber.getDecimalPlaces().toString();
		xGroupSymbol = DBSFormat.getGroupSeparator();
		if (!pInputNumber.getSeparateThousand()) {
			pGroupDigits = "0";
		}
		if (pInputNumber.getDecimalPlaces() > 0){
			pDecSymbol = DBSFormat.getDecimalSeparator();
		}
		return "'" + xType + "'," + 
			   "'" + xMask + "'," + 
			   "'" + xMaskEmptyChr + "'," + 
			   xDecDigits + "," + 
			   "'" + xGroupSymbol + "'," + 
			   "'" + pDecSymbol + "'," +
			   "'" + pGroupDigits + "'";
	}

	private void pvEncodeInput(FacesContext pContext, DBSInputNumber pInputNumber, ResponseWriter pWriter) throws IOException {
		Integer xSize = pvGetSize(pInputNumber); //Ajusta tamanho considerando os pontos e virgulas.
		String xClientId = getInputDataClientId(pInputNumber);
//		String xStyle = DBSFaces.getCSSStyleWidthFromInputSize(xSize);
		//Calcula o tamanho máximo sem a formatação
		Integer xMaxSize = (pInputNumber.getMaxValue().length() > pInputNumber.getMinValue().length() ? 
							pInputNumber.getMaxValue():pInputNumber.getMinValue()).replaceAll("\\D+", "").length();
//		Integer xMaxSize = DBSFormat.getFormattedNumber((pInputNumber.getMaxValue().length() > pInputNumber.getMinValue().length()?pInputNumber.getMaxValue():pInputNumber.getMinValue()), 
//				   NUMBER_SIGN.MINUS_PREFIX, 
//				   pvGetNumberMask(pInputNumber)).replaceAll("\\D+", "").length();
//		Integer xWidth  = DBSFormat.getFormattedNumber(pInputNumber.getMaxValue(), NUMBER_SIGN.MINUS_PREFIX, pvGetNumberMask(pInputNumber)).replaceAll("\\D+", "").length();
		String xStyle = DBSFaces.getCSSStyleWidthFromInputSize(xMaxSize + pInputNumber.getDecimalPlaces());
		String xStyleClass = "";
		String xValue = "";
		if (pInputNumber.getValueDouble() != null){
			if (DBSNumber.toDouble(pInputNumber.getMinValue())<0){
				//Exibe valor com sinal
				xValue = DBSFormat.getFormattedNumber(pInputNumber.getValueDouble(), NUMBER_SIGN.MINUS_PREFIX, pvGetNumberMask(pInputNumber));
			}else{
				//Exibe valor sem sinal
				xValue = DBSFormat.getFormattedNumber(pInputNumber.getValueDouble(), NUMBER_SIGN.NONE, pvGetNumberMask(pInputNumber));
			}
		}
		if (!pInputNumber.getLeadingZero()){
			xStyle += " text-align:right;";
		}
		if (pInputNumber.getValueDouble() > DBSNumber.toDouble(pInputNumber.getMaxValue()) ||
			pInputNumber.getValueDouble() < DBSNumber.toDouble(pInputNumber.getMinValue())){
			xStyleClass = CSS.MODIFIER.ERROR;
		}

		pWriter.startElement("div", pInputNumber);
			DBSFaces.encodeAttribute(pWriter, "class", "-input");
		
			if (pInputNumber.getReadOnly()) {
				// Se for somente leitura, gera código como <Span>
				DBSFaces.encodeInputDataReadOnly(pInputNumber, pWriter, xClientId, false, xValue, xSize, null, xStyle);
			} else {
				pWriter.startElement("input", pInputNumber);
					DBSFaces.encodeAttribute(pWriter, "id", xClientId);
					DBSFaces.encodeAttribute(pWriter, "name", xClientId);
					if (pInputNumber.getSecret()) {
						DBSFaces.encodeAttribute(pWriter, "type", "password");
					} else {
						DBSFaces.encodeAttribute(pWriter, "type", "text");
					}
					DBSFaces.encodeAttribute(pWriter, "pattern", "[0-9]*"); //Força a exibição do teclado númerico no mobile
					DBSFaces.encodeAttribute(pWriter, "inputmode", "numeric");
					DBSFaces.encodeAttribute(pWriter, "class", DBSFaces.getInputDataClass(pInputNumber) + xStyleClass);
					DBSFaces.encodeAttribute(pWriter, "style", xStyle);
					DBSFaces.encodeAttribute(pWriter, "placeHolder", pInputNumber.getPlaceHolder());
					DBSFaces.setSizeAttributes(pWriter, xSize, null);
					DBSFaces.encodeAttribute(pWriter, "minValue", pInputNumber.getMinValue()); 
					DBSFaces.encodeAttribute(pWriter, "maxValue", pInputNumber.getMaxValue());
					//Verifica se o sinal é negativo
					if (DBSNumber.toDouble(pInputNumber.getMinValue())<0){
						if (pInputNumber.getValueDouble()<0){
							DBSFaces.encodeAttribute(pWriter, "n", "-");
						}
					}
					if (!pInputNumber.getAutocomplete().toLowerCase().equals("on") &&
						!pInputNumber.getAutocomplete().toLowerCase().equals("true")){
						DBSFaces.encodeAttribute(pWriter, "autocomplete", "off");
					}
	
	
					DBSFaces.encodeAttribute(pWriter, "size", xSize);
					DBSFaces.encodeAttribute(pWriter, "maxlength", xSize);
					DBSFaces.encodeAttribute(pWriter, "value", xValue, "0");
					encodeClientBehaviors(pContext, pInputNumber);
				pWriter.endElement("input");
			}
			pWriter.endElement("div");
//				if (pInputNumber.getIncrement()){
//					//Encode do botão
//					pWriter.startElement("div", pInputNumber);
//						DBSFaces.encodeAttribute(pWriter, "class", "-buttons");
//						pWriter.startElement("div", pInputNumber);
//							DBSFaces.encodeAttribute(pWriter, "class", " -container -th_flex -not_selectable");
//							if (xMaxSize >= 114){
//								
//								pWriter.startElement("div", pInputNumber);
//									DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.ACTION + " -delete -i_delete -th_col"); //-i_navigate_down 
//								pWriter.endElement("div");
//								pWriter.startElement("div", pInputNumber);
//									if (pInputNumber.getValueDouble() != null){
//										if (pInputNumber.getValueDouble() < 0){
//											DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.ACTION + " -direction -down -th_col");
//										}else{
//											DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.ACTION + " -direction -up -th_col");
//										}
//									}
//								pWriter.endElement("div");
//								Integer xCount = 0;
//								if (xMaxSize >= 8){
//									pWriter.startElement("div", pInputNumber);
//										DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.ACTION + " -op_mm -th_col");
//									pWriter.endElement("div");
//									xCount++;
//								}
//								if (xMaxSize >= 7){
//									pWriter.startElement("div", pInputNumber);
//										DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.ACTION + " -op_cm -th_col");
//									pWriter.endElement("div");
//									xCount++;
//								}
//								if (xCount < 2){
//									if (xMaxSize >= 6){
//										pWriter.startElement("div", pInputNumber);
//											DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.ACTION + " -op_xm -th_col");
//										pWriter.endElement("div");
//										xCount++;
//									}
//									if (xCount < 2){
//										if (xMaxSize >= 5){
//											pWriter.startElement("div", pInputNumber);
//												DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.ACTION + " -op_m -th_col");
//											pWriter.endElement("div");
//											xCount++;
//										}
//									}
//								}
//								pWriter.startElement("div", pInputNumber);
//								DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.ACTION + " -close -i_cancel -th_col"); //-i_navigate_down 
//								pWriter.endElement("div");
//							}else{
////								pWriter.startElement("div", pInputNumber);
////									DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.ACTION + " -op_c -th_col");
////								pWriter.endElement("div");
////								pWriter.startElement("div", pInputNumber);
////									DBSFaces.encodeAttribute(pWriter, "class",CSS.THEME.ACTION + " -op_x -th_col");
////								pWriter.endElement("div");
//								pWriter.startElement("div", pInputNumber);
//									DBSFaces.encodeAttribute(pWriter, "class",CSS.THEME.ACTION + " -up -i_media_play -th_col");
//								pWriter.endElement("div");
//								pWriter.startElement("div", pInputNumber);
//									DBSFaces.encodeAttribute(pWriter, "class",CSS.THEME.ACTION + " -down -i_media_play -th_col");
//								pWriter.endElement("div");
//
//							}
//						pWriter.endElement("div");
//					pWriter.endElement("div");
	}
	
	private String pvGetNumberMask(DBSInputNumber pInputNumber) {
		Integer xLeadingZeroSize;
		Boolean xShowSeparator;

		if (pInputNumber.getLeadingZero()) {
			xLeadingZeroSize = pInputNumber.getSize() - pInputNumber.getDecimalPlaces();
			xShowSeparator = false;
		} else {
			xLeadingZeroSize = 1;
			xShowSeparator = pInputNumber.getSeparateThousand();
		}
		return DBSFormat.getNumberMask(pInputNumber.getDecimalPlaces(), xShowSeparator, xLeadingZeroSize);
	}
	
	private void pvInitialize(DBSInputNumber pInputNumber){
		pInputNumber.setMaxValue(pvLimitBySize(pInputNumber, pInputNumber.getMaxValue()));
		pInputNumber.setMinValue(pvLimitBySize(pInputNumber, pInputNumber.getMinValue()));
		//Se tamanho em caracteres do limite for inferior a quantidade permitida de caracteres
		//Reduz a quantidade permitida de caracteres
		String xFormat;
		Double xValue;
		if (pInputNumber.getMaxValue().length() > pInputNumber.getMinValue().length()){
			xValue  = DBSNumber.toDouble(pInputNumber.getMaxValue());
			xFormat = DBSFormat.getFormattedNumber(xValue, NUMBER_SIGN.MINUS_PREFIX, pvGetNumberMask(pInputNumber));
		}else{
			xValue  = DBSNumber.toDouble(pInputNumber.getMinValue());
			xFormat = DBSFormat.getFormattedNumber(xValue, NUMBER_SIGN.MINUS_PREFIX, pvGetNumberMask(pInputNumber));
		}
		if (xFormat.length() < pInputNumber.getSize()){
			pInputNumber.setSize(xFormat.length());
		}
	}
	
	/**
	 * Limita valores até o tamanho máximo em caracteres permitido no campo.
	 * @param pInputNumber
	 * @param pValue
	 * @return
	 */
	private String pvLimitBySize(DBSInputNumber pInputNumber, String pValue){
		Double  xValue;
		Integer xDif;
		String 	xFormat;
		xValue  = DBSNumber.toDouble(pValue);
		xFormat = DBSFormat.getFormattedNumber(xValue, NUMBER_SIGN.MINUS_PREFIX, pvGetNumberMask(pInputNumber));
		xDif = xFormat.length() -  pInputNumber.getSize();
		//Se valor limite for superior a quantidade permitida de caracteres
		//Limita valor a quantidade de caracteres possíveis
		if (xDif > 0){
			String xNewLimit = xFormat.substring(xDif);
			if (!DBSNumber.isNumber(xNewLimit.substring(0, 1))){
				xNewLimit = xNewLimit.substring(1);
			}
			if (xValue < 0){
				xNewLimit = "-" + xNewLimit;
			}
			return BigDecimal.valueOf(DBSNumber.toInteger(xNewLimit)).toPlainString();
		}
		return pValue;

	}
	/**
	 * Retorma tamanho máximo de caracteres que sertão exibidos, considerando o valor máximo e respectivos pontos/virgulas
	 * @param pInputNumber
	 * @return
	 */
	private Integer pvGetSize(DBSInputNumber pInputNumber){
		Integer xSize = pInputNumber.getSize(); 
		if (xSize < 1){
			xSize = 1;
		}
		return xSize;
	}


}
