package br.com.dbsoft.ui.component.checkbox;


import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSCheckbox.RENDERER_TYPE)
public class DBSCheckboxRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSCheckbox xCheckbox = (DBSCheckbox) pComponent;
        if(xCheckbox.getReadOnly()) {return;}
        
        decodeBehaviors(pContext, xCheckbox);
    	
		String xClientIdAction = getInputDataClientId(xCheckbox);
		String xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientIdAction);
        if(xSubmittedValue != null && pvIsChecked(xSubmittedValue)) {
        		xCheckbox.setSubmittedValue(true);
        }else{
	    		xCheckbox.setSubmittedValue(false);
        }
	}	

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSCheckbox xCheckbox = (DBSCheckbox) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xCheckbox.getClientId(pContext);
		String xClass = CSS.CHECKBOX.MAIN;
		if (xCheckbox.getStyleClass()!=null){
			xClass += xCheckbox.getStyleClass();
		}

		xWriter.startElement("div", xCheckbox);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xCheckbox.getStyle());
			//Container
			xWriter.startElement("div", xCheckbox);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.THEME.INPUT + CSS.NOT_SELECTABLE);
				if(pvIsChecked(xCheckbox.getValue())) {
					DBSFaces.encodeAttribute(xWriter, "checked", "checked");
				}
				if (xCheckbox.getReadOnly()){
					DBSFaces.encodeAttribute(xWriter, "disabled","disabled");
				}
				
				if (!xCheckbox.getInvertLabel()){
					pvEncodeLabel(pContext, xCheckbox, xWriter);
				}
				pvEncodeInput(pContext, xCheckbox, xWriter);
				if (xCheckbox.getInvertLabel()){
					pvEncodeLabel(pContext, xCheckbox, xWriter);
				}
				pvEncodeRightLabel(pContext, xCheckbox, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xCheckbox, xCheckbox.getTooltip());
			//Javascript
			if (!xCheckbox.getReadOnly()){
				pvEncodeJS(xCheckbox, xWriter);
			}
		xWriter.endElement("div");
	}

	private void pvEncodeInput(FacesContext pContext, DBSCheckbox pCheckbox, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pCheckbox);
		
		pWriter.startElement("div", pCheckbox);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.INPUT);
				pWriter.startElement("span", pCheckbox);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON);
				pWriter.endElement("span");			
				pWriter.startElement("input", pCheckbox); 
					DBSFaces.encodeAttribute(pWriter, "id", xClientId);
					DBSFaces.encodeAttribute(pWriter, "name", xClientId);
					DBSFaces.encodeAttribute(pWriter, "type", "checkbox");
					if (pCheckbox.getReadOnly()){
						DBSFaces.encodeAttribute(pWriter, "class", DBSFaces.getInputDataClass(pCheckbox) + CSS.MODIFIER.DISABLED);
						DBSFaces.encodeAttribute(pWriter, "disabled","disabled");
					}else{
						DBSFaces.encodeAttribute(pWriter, "class", DBSFaces.getInputDataClass(pCheckbox));
					}
					if(pvIsChecked(pCheckbox.getValue())) {
						DBSFaces.encodeAttribute(pWriter, "checked", "checked");
					}	
					encodeClientBehaviors(pContext, pCheckbox);
				pWriter.endElement("input");
		pWriter.endElement("div");
	}
	
    private static boolean pvIsChecked(Object pValue) {
    	if (pValue==null){ 
    		return false;
    	}else{
    		String xValue = pValue.toString().trim();
	        return "-1".equalsIgnoreCase(xValue) ||
	        	    "1".equalsIgnoreCase(xValue) ||
	        		"on".equalsIgnoreCase(xValue) || 
	               "yes".equalsIgnoreCase(xValue) || 
	               "true".equalsIgnoreCase(xValue);
	    }
    }
	
	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xCheckboxId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_checkbox(xCheckboxId); \n" +
                     "}); \n";
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}
	
	
	/**
	 * Gera HTML padrão do label à esquerda do campo, já incluindo o sinal ":"
	 * @param pContext
	 * @param pInput
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeLabel(FacesContext pContext, DBSCheckbox pCheckbox, ResponseWriter pWriter) throws IOException{
		if (pCheckbox.getLabel()!=null){
			String xStyle = "";
			if (!pCheckbox.getLabelWidth().equals("")){
				xStyle += " width:" + pCheckbox.getLabelWidth() + ";";
			}
			pWriter.startElement("label", pCheckbox);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.INPUT_LABEL + CSS.NOT_SELECTABLE);
				DBSFaces.encodeAttribute(pWriter, "style", xStyle);
			pWriter.write(pCheckbox.getLabel().trim());
			pWriter.endElement("label");
		}
	}
	
	/**
	 * Gera HTML padrão do label à direita do campo
	 * @param pContext
	 * @param pInput
	 * @param pWriter
	 * @throws IOException
	 */
	public static void pvEncodeRightLabel(FacesContext pContext, DBSCheckbox pCheckbox, ResponseWriter pWriter) throws IOException{
		if (pCheckbox.getRightLabel()!=null){
			pWriter.startElement("label", pCheckbox);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.INPUT_LABEL + CSS.NOT_SELECTABLE);
				DBSFaces.encodeAttribute(pWriter, "style", "margin:0 3px 0 3px; vertical-align: middle; display:inline-block;");
			pWriter.write(pCheckbox.getRightLabel().trim());
			pWriter.endElement("label");
		}
	}
	
}






