package br.com.dbsoft.ui.component.checkbox;


import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;

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
		DBSCheckbox xCheckbox = (DBSCheckbox) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xCheckbox.getClientId(pContext);
		String xClass = DBSFaces.CSS.CHECKBOX.MAIN + " " + DBSFaces.CSS.INPUT.MAIN + " ";
		if (xCheckbox.getStyleClass()!=null){
			xClass = xClass + xCheckbox.getStyleClass();
		}
		
		//HtmlInputText xInput = (HtmlInputText) FacesContext.getCurrentInstance().getApplication().createComponent(HtmlInputText.COMPONENT_TYPE);
		//xCheckbox.getChildren().add(xInput);
		//xInput.setValueExpression("value", pCheckbox.getValueExpression("value"));

		xWriter.startElement("div", xCheckbox);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			xWriter.writeAttribute("class", xClass, "class");
			DBSFaces.setAttribute(xWriter, "style", xCheckbox.getStyle(), null);
			//Container
			xWriter.startElement("div", xCheckbox);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, "class");
				if (!xCheckbox.getInvertLabel()){
					DBSFaces.encodeLabel(pContext, xCheckbox, xWriter);
				}
				pvEncodeInput(pContext, xCheckbox, xWriter);
				if (xCheckbox.getInvertLabel()){
					DBSFaces.encodeLabel(pContext, xCheckbox, xWriter, false);
				}
				DBSFaces.encodeRightLabel(pContext, xCheckbox, xWriter);
				DBSFaces.encodeTooltip(pContext, xCheckbox, xCheckbox.getTooltip());
			xWriter.endElement("div");
		xWriter.endElement("div");
		pvEncodeJS(xWriter, xClientId);
	}

	private void pvEncodeInput(FacesContext pContext, DBSCheckbox pCheckbox, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pCheckbox);
		//System.out.println("CHECKBOX=" + pCheckbox.getValue());
		String xOnChange = null;
		
		if (pCheckbox.getUpdate()!=null){
			xOnChange = DBSFaces.getSubmitString(pCheckbox, DBSFaces.HTML.EVENTS.ONCHANGE, pCheckbox.getExecute(), pCheckbox.getUpdate());
		}

		pWriter.startElement("input", pCheckbox); 
			DBSFaces.setAttribute(pWriter, "id", xClientId, null);
			DBSFaces.setAttribute(pWriter, "name", xClientId, null);
			DBSFaces.setAttribute(pWriter, "type", "checkbox", null);
			if (pCheckbox.getReadOnly()){
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pCheckbox) + DBSFaces.CSS.MODIFIER.DISABLED, null);
				DBSFaces.setAttribute(pWriter, "disabled","disabled", null);
			}else{
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pCheckbox), null);
			}
			if (xOnChange!=null){
				DBSFaces.setAttribute(pWriter, DBSFaces.HTML.EVENTS.ONCHANGE, xOnChange, null); 
			}
			if(pvIsChecked(pCheckbox.getValue())) {
				pWriter.writeAttribute("checked", "checked", null);
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
	private void pvEncodeJS(ResponseWriter pWriter, String pClientId) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xCheckboxId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_checkbox(xCheckboxId); \n" +
                     "}); \n";
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}
}






