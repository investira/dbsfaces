package br.com.dbsoft.ui.component.listbox;


import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSListbox.RENDERER_TYPE)
public class DBSListboxRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSListbox xListbox = (DBSListbox) pComponent;
        if(xListbox.getReadOnly()) {return;}
        
    	decodeBehaviors(pContext, xListbox);
    	
		String xClientIdAction = getInputDataClientId(xListbox);
		String xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientIdAction);
    	xListbox.setSubmittedValue(xSubmittedValue);
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
		DBSListbox xListbox = (DBSListbox) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xListbox.getClientId(pContext);
		String xClass = CSS.LISTBOX.MAIN + CSS.THEME.INPUT;
		if (xListbox.getStyleClass()!=null){
			xClass = xClass + xListbox.getStyleClass();
		}
		xWriter.startElement("div", xListbox);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xListbox.getStyle(), null);
			//Container
			xWriter.startElement("div", xListbox);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
					DBSFaces.encodeLabel(pContext, xListbox, xWriter);
					pvEncodeInput(pContext, xListbox, xWriter);
					DBSFaces.encodeRightLabel(pContext, xListbox, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xListbox, xListbox.getTooltip());
		xWriter.endElement("div");
	}

	
	private void pvEncodeInput(FacesContext pContext, DBSListbox pListbox, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pListbox);
		String xStyle = "";
		xStyle = DBSFaces.getCSSStyleWidthFromInputSize(pListbox.getSize());

		if (pListbox.getReadOnly()){
			DBSFaces.encodeInputDataReadOnly(pListbox, pWriter, xClientId, false, (String)pListbox.getList().get(pListbox.getValue().toString()), pListbox.getSize(), DBSNumber.toInteger(pListbox.getLines()), xStyle);
		}else{
		pWriter.startElement("select", pListbox);
			DBSFaces.setAttribute(pWriter, "id", xClientId);
			DBSFaces.setAttribute(pWriter, "name", xClientId);
			DBSFaces.setAttribute(pWriter, "size", pListbox.getLines());
			DBSFaces.setAttribute(pWriter, "style", xStyle);
			DBSFaces.setSizeAttributes(pWriter, pListbox.getSize(), DBSNumber.toInteger(pListbox.getLines()));
			if (pListbox.getReadOnly()){
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pListbox) + CSS.MODIFIER.DISABLED);
				DBSFaces.setAttribute(pWriter, "disabled", "disabled");
			}else{
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pListbox));
			}
			//Encode dos itens na lista
			if (pListbox.getList() != null){
				for (Map.Entry<String, Object> xListItem : pListbox.getList().entrySet()) {
					if (xListItem.getKey() != null){
						pWriter.startElement("option", pListbox);
							DBSFaces.setAttribute(pWriter, "value", xListItem.getKey());
							if (xListItem.getKey().toString().equals(pListbox.getValue().toString())){
								DBSFaces.setAttribute(pWriter, "selected", "");
							}
							pWriter.write((String) DBSObject.getNotEmpty(xListItem.getValue(), "") );
							
						pWriter.endElement("option");
					}
				}
			}
			encodeClientBehaviors(pContext, pListbox);
		pWriter.endElement("select");
		}
	}
	
}






