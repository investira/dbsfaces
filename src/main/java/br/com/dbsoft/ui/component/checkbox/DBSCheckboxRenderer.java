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
		String xClass = CSS.CHECKBOX.MAIN + CSS.THEME.INPUT;
		if (xCheckbox.getStyleClass()!=null){
			xClass += xCheckbox.getStyleClass();
		}
		
		//HtmlInputText xInput = (HtmlInputText) FacesContext.getCurrentInstance().getApplication().createComponent(HtmlInputText.COMPONENT_TYPE);
		//xCheckbox.getChildren().add(xInput);
		//xInput.setValueExpression("value", pCheckbox.getValueExpression("value"));

		xWriter.startElement("div", xCheckbox);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xCheckbox.getStyle());
			//Container
			xWriter.startElement("div", xCheckbox);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
				if (!xCheckbox.getInvertLabel()){
					DBSFaces.encodeLabel(pContext, xCheckbox, xWriter);
				}
				pvEncodeInput(pContext, xCheckbox, xWriter);
				if (xCheckbox.getInvertLabel()){
					DBSFaces.encodeLabel(pContext, xCheckbox, xWriter);
				}
				DBSFaces.encodeRightLabel(pContext, xCheckbox, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xCheckbox, xCheckbox.getTooltip());
		xWriter.endElement("div");
		//Javascript
		if (!xCheckbox.getReadOnly()){
			pvEncodeJS(xCheckbox, xWriter);
		}
	}

	private void pvEncodeInput(FacesContext pContext, DBSCheckbox pCheckbox, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pCheckbox);
		//System.out.println("CHECKBOX=" + pCheckbox.getValue());
		String xOnChange = null;
		
		if (pCheckbox.getUpdate()!=null){
			xOnChange = DBSFaces.getSubmitString(pCheckbox, DBSFaces.HTML.EVENTS.ONCHANGE, pCheckbox.getExecute(), pCheckbox.getUpdate());
		}

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
			if (xOnChange!=null){
				DBSFaces.encodeAttribute(pWriter, DBSFaces.HTML.EVENTS.ONCHANGE, xOnChange); 
			}
			if(pvIsChecked(pCheckbox.getValue())) {
				DBSFaces.encodeAttribute(pWriter, "checked", "checked");
			}

			encodeClientBehaviors(pContext, pCheckbox);
		pWriter.endElement("input");
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
}






