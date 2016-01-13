package br.com.dbsoft.ui.component.radio;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSRadio.RENDERER_TYPE)
public class DBSRadioRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSRadio xRadio = (DBSRadio) pComponent;
        if(xRadio.getReadOnly()) {return;}
        
        decodeBehaviors(pContext, xRadio);
        
		String xClientId = xRadio.getClientId(pContext);
		if (!xRadio.getReadOnly()){
			Object xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientId);
	        if(xSubmittedValue != null) {
	            xRadio.setSubmittedValue(xSubmittedValue);
	        }
		}
	}	
	
    @Override
	public boolean getRendersChildren() {
		return false; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
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
		DBSRadio xRadio = (DBSRadio) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xRadio.getClientId(pContext);
		String xClass = DBSFaces.CSS.RADIO.MAIN + " ";
		String xOnChange = null;

		if (xRadio.getStyleClass()!=null){
			xClass += xRadio.getStyleClass();
		}
		
		
		if (xRadio.getUpdate()!=null){
			xOnChange = DBSFaces.getSubmitString(xRadio, DBSFaces.HTML.EVENTS.ONCHANGE, xRadio.getExecute(), xRadio.getUpdate());
		}
		//HtmlInputText xInput = (HtmlInputText) FacesContext.getCurrentInstance().getApplication().createComponent(HtmlInputText.COMPONENT_TYPE);
		//xRadio.getChildren().add(xInput);
		//xInput.setValueExpression("value", pSelect.getValueExpression("value"));
		xWriter.startElement("div", xRadio);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			xWriter.writeAttribute("class", xClass, "class");
			if (xOnChange!=null){
				DBSFaces.setAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCHANGE, xOnChange, null); 
			}
			DBSFaces.setAttribute(xWriter, "style", xRadio.getStyle(), null);
			if (xRadio.getChildren().size()>0){
				pvEncodeInput(pContext, xRadio, xWriter);
			}
			DBSFaces.encodeTooltip(pContext, xRadio, xRadio.getTooltip());
		xWriter.endElement("div");
	}

	private void pvEncodeInput(FacesContext pContext, DBSRadio pRadio, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("table", pRadio);
			pWriter.startElement("tbody", pRadio);
				pWriter.startElement("tr", pRadio);
					pvEncodeItem(pContext, pRadio, pWriter);
				pWriter.endElement("tr");
			pWriter.endElement("tbody");
		pWriter.endElement("table");
	}

	private void pvEncodeItem(FacesContext pContext, DBSRadio pRadio, ResponseWriter pWriter) throws IOException{
		String xClientId = pRadio.getClientId(pContext);
		Integer xI = 0;
		for (UIComponent xItem : pRadio.getChildren()) {
			if (xItem.getClass().equals(UISelectItem.class)){
				UISelectItem xS = (UISelectItem) xItem;
				pWriter.startElement("td", pRadio);
					pWriter.writeAttribute("class", DBSFaces.CSS.INPUT.MAIN , "class");
					if (pRadio.getFloatLeft()){
						pWriter.writeAttribute("style", "float:left;", null);
					}
					pWriter.startElement("input", pRadio);
						DBSFaces.setAttribute(pWriter, "id", xClientId + xI, null);
						DBSFaces.setAttribute(pWriter, "type", "radio", null);
						DBSFaces.setAttribute(pWriter, "name", xClientId, null);
						if (pRadio.getReadOnly()){
							DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pRadio) + DBSFaces.CSS.MODIFIER.DISABLED, null);
							DBSFaces.setAttribute(pWriter, "disabled","disabled", null);
						}else{
							DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pRadio), null);
						}			
						DBSFaces.setAttribute(pWriter, "value", xS.getItemValue(), null);
						if (pRadio.getValue()!=null){
							if (pRadio.getValue().toString().equals(xS.getItemValue())){
								DBSFaces.setAttribute(pWriter, "checked", "checked", null);
							}
						}
						encodeClientBehaviors(pContext, pRadio);
					pWriter.endElement("input");
					//Encode do label do radio caso tenha sido informado
					if (xS.getItemLabel() != null && !xS.getItemLabel().equals("")){
						pWriter.startElement("label", pRadio);
							DBSFaces.setAttribute(pWriter, "for", xClientId + xI, null);
							DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUT.LABEL, null);
							DBSFaces.setAttribute(pWriter, "style","width:" + pRadio.getLabelWidth() + ";", null);
							pWriter.write(" " + xS.getItemLabel());
						pWriter.endElement("label");
					}
				pWriter.endElement("td");
				xI++;
			}
		}
	}

}






