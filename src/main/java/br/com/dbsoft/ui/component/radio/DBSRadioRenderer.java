package br.com.dbsoft.ui.component.radio;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSRadio.RENDERER_TYPE)
public class DBSRadioRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSRadio xRadio = (DBSRadio) pComponent;
        if(xRadio.getReadOnly()) {return;}
        
        decodeBehaviors(pContext, xRadio);
        
		String xClientId = pvGetItemClientId(pContext, xRadio);
//        if (DBSObject.isEmpty(xRadio.getGroup())){
//        	xClientId = xRadio.getClientId(pContext);
//        }else{
//			if (xRadio.getNamingContainer() == null){
//				xClientId = xRadio.getGroup();
//			}else{
//				xClientId = xRadio.getNamingContainer().getClientId() + ":" + xRadio.getGroup();
//			}        	
//        }
		Object xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientId);
        if(xSubmittedValue != null) {
            xRadio.setSubmittedValue(xSubmittedValue);
        }
	}	
	
    @Override
	public boolean getRendersChildren() {
		return false; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSRadio xRadio = (DBSRadio) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xRadio.getClientId(pContext);
		String xClass = CSS.RADIO.MAIN;
//		String xOnChange = null;

		if (xRadio.getStyleClass()!=null){
			xClass += xRadio.getStyleClass();
		}
		
		
//		if (xRadio.getUpdate()!=null){
//			xOnChange = DBSFaces.getSubmitString(xRadio, DBSFaces.HTML.EVENTS.ONCHANGE, xRadio.getExecute(), xRadio.getUpdate());
//		}
		//HtmlInputText xInput = (HtmlInputText) FacesContext.getCurrentInstance().getApplication().createComponent(HtmlInputText.COMPONENT_TYPE);
		//xRadio.getChildren().add(xInput);
		//xInput.setValueExpression("value", pSelect.getValueExpression("value"));
		xWriter.startElement("div", xRadio);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
//			if (xOnChange!=null){
//				DBSFaces.encodeAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCHANGE, xOnChange); 
//			}
			DBSFaces.encodeAttribute(xWriter, "style", xRadio.getStyle());
			if (xRadio.getChildren().size()>0){
				pvEncodeItens(pContext, xRadio, xWriter);
			}
			DBSFaces.encodeTooltip(pContext, xRadio, xRadio.getTooltip());
		xWriter.endElement("div");
	}


	private void pvEncodeItens(FacesContext pContext, DBSRadio pRadio, ResponseWriter pWriter) throws IOException{
		String xClientId = pRadio.getClientId(pContext);
		Integer xI = 0;
		for (UIComponent xItem : pRadio.getChildren()) {
			if (xItem.getClass().equals(UISelectItem.class)){
				UISelectItem xS = (UISelectItem) xItem;
				pWriter.startElement("div", pRadio);
					String xClass = CSS.THEME.INPUT + CSS.THEME.FLEX;
					if (pRadio.getReadOnly()){
						DBSFaces.encodeAttribute(pWriter, "class", xClass + CSS.MODIFIER.DISABLED);
						DBSFaces.encodeAttribute(pWriter, "disabled", "disabled");
					}else{
						DBSFaces.encodeAttribute(pWriter, "class", xClass);
					}
					if (pRadio.getFloatLeft()){
						DBSFaces.encodeAttribute(pWriter, "style", "float:left;");
					}
					pWriter.startElement("input", pRadio);
						DBSFaces.encodeAttribute(pWriter, "id", xClientId + xI);
//						DBSFaces.encodeAttribute(pWriter, "id", xItem.getClientId());
						DBSFaces.encodeAttribute(pWriter, "type", "radio");
						DBSFaces.encodeAttribute(pWriter, "name", pvGetItemClientId(pContext, pRadio));
//						if (DBSObject.isEmpty(pRadio.getGroup())){
//							DBSFaces.encodeAttribute(pWriter, "name", xClientId);
//						}else{
//							if (pRadio.getNamingContainer() == null){
//								DBSFaces.encodeAttribute(pWriter, "name", pRadio.getGroup());
//							}else{
//								DBSFaces.encodeAttribute(pWriter, "name", pRadio.getNamingContainer().getClientId() + ":" + pRadio.getGroup());
//							}
//						}
						xClass = DBSFaces.getInputDataClass(pRadio) + CSS.THEME.FLEX_COL;
						if (pRadio.getReadOnly()){
							DBSFaces.encodeAttribute(pWriter, "class", xClass + CSS.MODIFIER.DISABLED);
							DBSFaces.encodeAttribute(pWriter, "disabled", "disabled");
						}else{
							DBSFaces.encodeAttribute(pWriter, "class", xClass);
						}			
						DBSFaces.encodeAttribute(pWriter, "value", xS.getItemValue());
						if (pRadio.getValue()!=null){
							if (pRadio.getValue().toString().equals(xS.getItemValue())){
								DBSFaces.encodeAttribute(pWriter, "checked", "checked");
							}
						}
						encodeClientBehaviors(pContext, pRadio);
					pWriter.endElement("input");
					//Encode do label do radio caso tenha sido informado
					if (xS.getItemLabel() != null && !xS.getItemLabel().equals("")){
						pWriter.startElement("label", pRadio);
							DBSFaces.encodeAttribute(pWriter, "for", xClientId + xI);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.INPUT_LABEL + CSS.NOT_SELECTABLE + CSS.THEME.FLEX_COL);
							if (!DBSObject.isEmpty(pRadio.getLabelWidth())) {
								DBSFaces.encodeAttribute(pWriter, "style","width:" + pRadio.getLabelWidth() + ";");
							}
							pWriter.write(" " + xS.getItemLabel());
						pWriter.endElement("label");
					}
				pWriter.endElement("div");
				xI++;
			}
		}
	}
	
	private String pvGetItemClientId(FacesContext pContext, DBSRadio pRadio){
		String xItemClientid = null;
		if (DBSObject.isEmpty(pRadio.getGroup())){
			xItemClientid = pRadio.getClientId(pContext);
		}else{
			if (pRadio.getNamingContainer() == null){
				xItemClientid = pRadio.getGroup();
			}else{
				xItemClientid = pRadio.getNamingContainer().getClientId() + ":" + pRadio.getGroup();
			}
		}
		return xItemClientid;
	}

}






