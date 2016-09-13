package br.com.dbsoft.ui.component.inputmask;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSFormat;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSInputMask.RENDERER_TYPE)
public class DBSInputMaskRenderer extends DBSRenderer {
	
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSInputMask xInputMask = (DBSInputMask) pComponent;
        if(xInputMask.getReadOnly()) {return;}
        
    	decodeBehaviors(pContext, xInputMask);
    	
		String xClientIdAction = getInputDataClientId(xInputMask) ;
		if (pContext.getExternalContext().getRequestParameterMap().containsKey(xClientIdAction)){
			String xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientIdAction);
	        if(xSubmittedValue != null) {
    			xInputMask.setSubmittedValue(xSubmittedValue);
	        }
		}
	}	
	
    @Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }
    
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSInputMask xInputMask = (DBSInputMask) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xInputMask.getClientId(pContext);
		String xClass = CSS.INPUTMASK.MAIN + CSS.THEME.INPUT;
		if (xInputMask.getStyleClass()!=null){
			xClass = xClass + xInputMask.getStyleClass();
		}
		xWriter.startElement("div", xInputMask);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xInputMask.getStyle());
			DBSFaces.setAttribute(xWriter, "placeHolder", xInputMask.getPlaceHolder());
			//Container
			xWriter.startElement("div", xInputMask);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);

					DBSFaces.encodeLabel(pContext, xInputMask, xWriter);
					pvEncodeInput(pContext, xInputMask, xWriter);
					DBSFaces.encodeRightLabel(pContext, xInputMask, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xInputMask, xInputMask.getTooltip());
		xWriter.endElement("div");
		if (!xInputMask.getReadOnly()){
			DBSFaces.encodeJavaScriptTagStart(xWriter);
			String xJS = "$(document).ready(function() { \n" +
					     " var xInputMaskId = dbsfaces.util.jsid('" + getInputDataClientId(xInputMask) + "'); \n " + 
					     " dbs_inputMask(xInputMaskId," + pvGetMaskParm(xInputMask) + "); \n" +
	                     "}); \n"; 
			xWriter.write(xJS);
			DBSFaces.encodeJavaScriptTagEnd(xWriter);		
		}
	}
	
	private String pvGetMaskParm(DBSInputMask pInputMask){

		return "'" + pInputMask.getMask() + "'," +
			   "'" + pInputMask.getMaskEmptyChr() + "'," +
			   pInputMask.getStripMask();
	}

	
	private void pvEncodeInput(FacesContext pContext, DBSInputMask pInputMask, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputMask);
		String xStyle = DBSFaces.getCSSStyleWidthFromInputSize(pInputMask.getMask().length());
		String xValue = "";
		if (pInputMask.getValue() != null){
			xValue = DBSFormat.getFormattedMask(pInputMask.getValueString(), pInputMask.getMask(), pInputMask.getMaskEmptyChr());
		}
 
		if (pInputMask.getReadOnly()){
			DBSFaces.encodeInputDataReadOnly(pInputMask, pWriter, xClientId, false, xValue, pInputMask.getMask().length(), null, xStyle);
		}else{
			//Se for somente leitura, gera código como <Span>
			pWriter.startElement("input", pInputMask);
				DBSFaces.setAttribute(pWriter, "id", xClientId);
				DBSFaces.setAttribute(pWriter, "name", xClientId);
				if (pInputMask.getSecret()){
					DBSFaces.setAttribute(pWriter, "type", "password");
				}else{
					DBSFaces.setAttribute(pWriter, "type", "text");
				}
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pInputMask));
				DBSFaces.setAttribute(pWriter, "style", xStyle);
				DBSFaces.setSizeAttributes(pWriter, pInputMask.getMask().length(), null);
				//Grava a largura do campo
				DBSFaces.setAttribute(pWriter, "size", pInputMask.getMask().length());
				if (pInputMask.getMaxLength()!=0){
					if (pInputMask.getMask().length() >  pInputMask.getMaxLength()){
						DBSFaces.setAttribute(pWriter, "maxlength", pInputMask.getMaxLength());
					}else{
						DBSFaces.setAttribute(pWriter, "maxlength", pInputMask.getMask().length());
					}
				}
				DBSFaces.setAttribute(pWriter, "value",xValue, "0");
				encodeClientBehaviors(pContext, pInputMask);
			pWriter.endElement("input");

		}
	}
	
//	private String pvGetNumberMask(DBSInputMask pInputMask){
//		Integer xLeadingZeroSize;
//		Boolean xShowSeparator;
//		//String.format("%02d", DBSDate.getNowDateTime().getMonthOfYear())
//		
//		if (pInputMask.getLeadingZero()){
//			xLeadingZeroSize = pInputMask.getSize();
//			xShowSeparator = false;
//		}else{
//			xLeadingZeroSize = 1;
//			xShowSeparator = pInputMask.getShowSeparator();
//		}
//		return DBSFormat.getNumberMask(pInputMask.getDecimalPlaces(), xShowSeparator, xLeadingZeroSize);
//	}

}






