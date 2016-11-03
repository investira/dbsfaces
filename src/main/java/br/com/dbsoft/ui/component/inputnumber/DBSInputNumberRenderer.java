package br.com.dbsoft.ui.component.inputnumber;

import java.io.IOException;

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
		if (xInputNumber.getStyleClass() != null) {
			xClass += xInputNumber.getStyleClass();
		}

		xWriter.startElement("div", xInputNumber);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xInputNumber.getStyle());
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
						+ " var xInputNumberId = dbsfaces.util.jsid('"
						+ getInputDataClientId(xInputNumber) + "'); \n "
						+ " dbs_inputNumber(xInputNumberId,"
						+ pvGetMaskParm(xInputNumber) + "); \n" + "}); \n";
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
		String xStyle = DBSFaces.getCSSStyleWidthFromInputSize(xSize);
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

		if (pInputNumber.getReadOnly()) {
			DBSFaces.encodeInputDataReadOnly(pInputNumber, pWriter, xClientId, false, xValue, xSize, null, xStyle);
		} else {
			// Se for somente leitura, gera código como <Span>
			pWriter.startElement("input", pInputNumber);
				DBSFaces.encodeAttribute(pWriter, "id", xClientId);
				DBSFaces.encodeAttribute(pWriter, "name", xClientId);
				if (pInputNumber.getSecret()) {
					DBSFaces.encodeAttribute(pWriter, "type", "password");
				} else {
					DBSFaces.encodeAttribute(pWriter, "type", "text");
				}
				DBSFaces.encodeAttribute(pWriter, "pattern", "[0-9]*");
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
	
	/**
	 * Retorma tamanho máximo de caracteres que sertão exibidos, considerando o valor máximo e respectivos pontos/virgulas
	 * @param pInputNumber
	 * @return
	 */
	private Integer pvGetSize(DBSInputNumber pInputNumber){
		Integer xSize = pInputNumber.getSize(); 
		if (xSize == 0){
			//Utiliza maxlength se tiver sido informado
			if (pInputNumber.getMaxLength()!=0){
				xSize = pInputNumber.getMaxLength();
			}else{
			//Utiliza tamanho do valor mínimo e/ou máximo se não tiver sido informado.
				Integer xMax = pInputNumber.getMaxValue().length();
				Integer xMin = pInputNumber.getMinValue().length();
				xSize = (xMax > xMin ? xMax: xMin);
			}
		}else if (pInputNumber.getMaxLength()!=0
			   && pInputNumber.getMaxLength() < xSize){
			xSize = pInputNumber.getMaxLength();
		}
//		//Ajusta tamanho considerando o valor máximo e respectivos pontos/virgulas		
//		String xFoo;
//		//String somente com a parte inteira, já que a aprte decimal, quando houver, é obrigatória a exibição.
//		xFoo = "-" + DBSString.repeat("1", xSize -  pInputNumber.getDecimalPlaces());
//		if (DBSNumber.toDouble(pInputNumber.getMinValue())<0){
//			xSize = DBSFormat.getFormattedNumber(DBSNumber.toDouble(xFoo), NUMBER_SIGN.MINUS_PREFIX, pvGetNumberMask(pInputNumber)).length();
//		}else{
//			xSize = DBSFormat.getFormattedNumber(DBSNumber.toDouble(xFoo), NUMBER_SIGN.NONE, pvGetNumberMask(pInputNumber)).length();
//		}
		//Define valor de um caracter como tamanho mínimo
		if (xSize < 1){
			xSize = 1;
		}
		return xSize;
	}

}
