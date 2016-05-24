package br.com.dbsoft.ui.component.div;

import java.io.IOException;

import javax.faces.component.UIComponent;
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
//		System.out.println("----------------");
//		System.out.println(pContext.isPostback());
//		System.out.println(pContext.getCurrentPhaseId());
//		System.out.println(pContext.getPartialViewContext().isAjaxRequest());
//		System.out.println(pContext.getPartialViewContext().isRenderAll());
//		System.out.println(pContext.getPartialViewContext().isPartialRequest());
//		System.out.println(pContext.getPartialViewContext().getRenderIds());
    	if (!pvEncodeLater(pContext, xDiv)){
    		DBSFaces.renderChildren(pContext, pComponent);
    	}
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSDiv xDiv = (DBSDiv) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.DIV.MAIN + " ";
		if (xDiv.getStyleClass()!=null){
			xClass += xDiv.getStyleClass() + " ";
		}
		if (xDiv.getSelectable()!=null){
			if (!xDiv.getSelectable()){
				xClass += DBSFaces.CSS.NOT_SELECTABLE.trim() + " ";
			}
		}
		String xClientId = xDiv.getClientId(pContext);
		xWriter.startElement(xDiv.getTagName(), xDiv);
			if (xDiv.getAjaxLoading() 
			 || shouldWriteIdAttribute(xDiv)){
				DBSFaces.setAttribute(xWriter, "id", xClientId, null);
				DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			}

			if (xDiv.getAjaxLoading()){
				xClass += " dbs_div_loading";
			}

			if (xClass!=""){
				DBSFaces.setAttribute(xWriter, "class", xClass.trim(), null);
			}
			DBSFaces.setAttribute(xWriter, "style", xDiv.getStyle(), null);

			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xDiv, DBSPassThruAttributes.getAttributes(Key.DIV));
			
			//Força para que o encode deste componente seja efetuado após, via chamada ajax. 
			if (pvEncodeLater(pContext, xDiv)){
	    		xWriter.startElement("div", xDiv);
	    			if (xDiv.getAjaxLoadingSmallIcon()){
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.LOADING, null);
	    			}else{
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.LOADING + DBSFaces.CSS.MODIFIER.LARGE, null);
	    			}
				xWriter.endElement("div");
				DBSFaces.encodeJavaScriptTagStart(xWriter);
				String xJS = "setTimeout(function(){" +
										"jsf.ajax.request('" + xDiv.getClientId() + "', 'update', {render:'" + xDiv.getClientId() + "', onevent:dbsfaces.ui.ajaxTriggerLoaded,  onerror:dbsfaces.ui.showLoadingError('" + xClientId + "')});" +
												   "}, 0);";
				xWriter.write(xJS);
				DBSFaces.encodeJavaScriptTagEnd(xWriter);	
			}

	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException{
		if (!pComponent.isRendered()){return;}
		DBSDiv xDiv = (DBSDiv) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		//Fim do componente
		xWriter.endElement(xDiv.getTagName());
	}
	
	/**
	 * Retorna se é uma chamada que deverá efetivamente efetuar o encode co componente ou se. 
	 * fará uma chamada ajax para que posteriormente seja efetuado o encode.<br/>
	 * 
	 * @param pContext
	 * @param pDiv
	 * @return
	 */
	private boolean pvEncodeLater(FacesContext pContext, DBSDiv pDiv){
		if (pDiv.getAjaxLoading()
		 && !pContext.getPartialViewContext().getRenderIds().contains(pDiv.getClientId())){
			return true;
		}
		return false;
	}
}
