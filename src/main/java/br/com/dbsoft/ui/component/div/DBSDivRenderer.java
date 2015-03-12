package br.com.dbsoft.ui.component.div;

import java.io.IOException;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSDiv.RENDERER_TYPE)
public class DBSDivRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}

	@Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
		DBSDiv xDiv = (DBSDiv) pComponent;
    	if (xDiv.getAjaxLoading()){
    		ResponseWriter xWriter = pContext.getResponseWriter();
			for (UIComponent xChild:pComponent.getChildren()) {
				if (UIData.class.isAssignableFrom(xChild.getClass()) 
				 || UIInput.class.isAssignableFrom(xChild.getClass())
				 || UIOutput.class.isAssignableFrom(xChild.getClass())
				 || UICommand.class.isAssignableFrom(xChild.getClass())
				 || DBSDiv.class.isAssignableFrom(xChild.getClass())){
		    		xWriter.startElement("div", xDiv);
						DBSFaces.setAttribute(xWriter, "id", xChild.getClientId(), null);
						DBSFaces.setAttribute(xWriter, "class", "dbs_div_loading", null);
			    		xWriter.startElement("div", xDiv);
							DBSFaces.setAttribute(xWriter, "class", "-loading", null);
						xWriter.endElement("div");
						DBSFaces.encodeJavaScriptTagStart(xWriter);
						String xJS = "setTimeout(function(){" +
												"jsf.ajax.request('" + xChild.getClientId() + "', 'update', {render:'" + xChild.getClientId() + "', onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror});" +
														   "}, 0);";
						xWriter.write(xJS);
						DBSFaces.encodeJavaScriptTagEnd(xWriter);	
					xWriter.endElement("div");
				}else{
					xChild.encodeAll(pContext);
				}
			}
		}else{
       		renderChildren(pContext, pComponent);
    	}
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSDiv xDiv = (DBSDiv) pComponent;
		//xDiv.setTransient(true);
		//System.out.println("RENDER DIV #############################" + xDiv.getClientId(pContext) + ":" + xDiv.getStyleClass());		
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.DIV.MAIN + " ";
		if (xDiv.getStyleClass()!=null){
			xClass = xClass + xDiv.getStyleClass() + " ";
		}
		if (xDiv.getSelectable()!=null){
			if (!xDiv.getSelectable()){
				xClass = xClass + DBSFaces.CSS.NOT_SELECTABLE.trim() + " ";
			}
		}
		String xClientId = xDiv.getClientId(pContext);
		xWriter.startElement("div", xDiv);
			if (shouldWriteIdAttribute(xDiv)){
				DBSFaces.setAttribute(xWriter, "id", xClientId, null);
				DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			}
			if (xClass!=""){
				DBSFaces.setAttribute(xWriter, "class", xClass, null);
			}
			DBSFaces.setAttribute(xWriter, "style", xDiv.getStyle(), null);
			
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xDiv, DBSPassThruAttributes.getAttributes(Key.DIV));

	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException{
		if (!pComponent.isRendered()){return;}
		ResponseWriter xWriter = pContext.getResponseWriter();
//		DBSDiv xDiv = (DBSDiv) pComponent;
		//Fim do componente
		xWriter.endElement("div");
//		//Chamada alax para carregar o conteúdo
//		if (xDiv.getAjaxLoading()){
//			DBSFaces.encodeJavaScriptTagStart(xWriter);
//			String xJS = "setTimeout(function(){" +
//									"jsf.ajax.request('" + xDiv.getClientId() + "', 'update', {render:'" + xDiv.getClientId() + "', onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror});" +
//											   "}, 0);";
//			xWriter.write(xJS);
//			DBSFaces.encodeJavaScriptTagEnd(xWriter);	
//		}
	}
}
