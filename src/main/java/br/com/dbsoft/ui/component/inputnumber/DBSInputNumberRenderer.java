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
		xWriter.endElement("div");
	}

	private String pvGetMaskParm(DBSInputNumber pInputNumber) {
		return pInputNumber.getDecimalPlaces().toString() + "," + 
			   pInputNumber.getSeparateThousand() + "," +
			   pInputNumber.getLeadingZero() + "," +
			   "'" + DBSFormat.getFormattedNumber(pInputNumber.getMinValue(), pInputNumber.getDecimalPlaces()) + "'," + 
			   "'" + DBSFormat.getFormattedNumber(pInputNumber.getMaxValue(), pInputNumber.getDecimalPlaces()) + "'," +
  			   getLocale();
	}

	private void pvEncodeInput(FacesContext pContext, DBSInputNumber pInputNumber, ResponseWriter pWriter) throws IOException {
		Integer xSize = pInputNumber.getSize();
		String xClientId = getInputDataClientId(pInputNumber);
		String xStyle = ""; // DBSFaces.getCSSStyleWidthFromInputSize(pInputNumber.getSize());
		String xStyleClass = "";
		String xValue = "";
		if (pInputNumber.getValueDouble() != null){
			if (pInputNumber.getMinValue()<0){
				//Exibe valor com sinal
				xValue = DBSFormat.getFormattedNumber(pInputNumber.getValueDouble(), NUMBER_SIGN.MINUS_PREFIX, pvGetNumberMask(pInputNumber));
			}else{
				//Exibe valor sem sinal
				xValue = DBSFormat.getFormattedNumber(pInputNumber.getValueDouble(), NUMBER_SIGN.NONE, pvGetNumberMask(pInputNumber));
			}
		}
//		if (!pInputNumber.getLeadingZero()){
			xStyle += " text-align:right;";
//		}
		if (pInputNumber.getValueDouble() > pInputNumber.getMaxValue() ||
			pInputNumber.getValueDouble() < pInputNumber.getMinValue()){
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
		//Limita valores até o tamanho máximo em caracteres permitido no campo.
		if (pInputNumber.getSize() != null){
			pInputNumber.setMaxValue(pvLimitBySize(pInputNumber, pInputNumber.getMaxValue()));
			pInputNumber.setMinValue(pvLimitBySize(pInputNumber, pInputNumber.getMinValue()));
		//Limita o tamanho do campo pela maior quantidade de caracteres dos valores máximo ou mínimo. O que for maior.
		}else{
			String xFormat;
			Double xValue;
			if (DBSNumber.toPlainString(pInputNumber.getMaxValue()).length() > DBSNumber.toPlainString(pInputNumber.getMinValue()).length()){
				xValue  = pInputNumber.getMaxValue();
			}else{
				xValue  = pInputNumber.getMinValue();
			}
			xFormat = DBSFormat.getFormattedNumber(xValue, NUMBER_SIGN.MINUS_PREFIX, pvGetNumberMask(pInputNumber));
			pInputNumber.setSize(xFormat.length());
		}
	}
	
	/**
	 * Limita valores até o tamanho máximo em caracteres permitido no campo.
	 * @param pInputNumber
	 * @param pValue
	 * @return
	 */
	private Double pvLimitBySize(DBSInputNumber pInputNumber, Double pValue){
		Integer xDif;
		String 	xFormat;
		xFormat = DBSFormat.getFormattedNumber(pValue, NUMBER_SIGN.MINUS_PREFIX, pvGetNumberMask(pInputNumber));
		xDif = xFormat.length() - pInputNumber.getSize();
		//Se valor limite for superior a quantidade permitida de caracteres
		//Limita valor a quantidade de caracteres possíveis
		if (xDif > 0){
			String xNewLimit = xFormat.substring(xDif);
			if (!DBSNumber.isNumber(xNewLimit.substring(0, 1))){
				xNewLimit = xNewLimit.substring(1);
			}
			if (pValue < 0){
				xNewLimit = "-" + xNewLimit;
			}
			return BigDecimal.valueOf(DBSNumber.toDouble(xNewLimit)).doubleValue();
		}
		return pValue;

	}


}
