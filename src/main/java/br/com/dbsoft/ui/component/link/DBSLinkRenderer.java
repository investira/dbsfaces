package br.com.dbsoft.ui.component.link;

import java.io.IOException;
import java.net.URLEncoder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer.Param;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSString;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSLink.RENDERER_TYPE)
public class DBSLinkRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
        DBSLink xLink = (DBSLink) pComponent;
		String xClientId = xLink.getClientId(pContext);
		if (xLink.getReadOnly()) {return;}
		if (RenderKitUtils.isPartialOrBehaviorAction(pContext, xClientId) || /*Chamada Ajax*/
			pContext.getExternalContext().getRequestParameterMap().containsKey(xClientId)) { 	/*Chamada Sem Ajax*/
			xLink.queueEvent(new ActionEvent(xLink));
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
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSLink xLink = (DBSLink) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = CSS.NOT_SELECTABLE + xLink.getStyleClass();
		String xOnClick = null;
		String xExecute = "";
		if (xLink.getExecute() == null){
			xExecute = getFormId(pContext, pComponent); 
		}else{
			xExecute = xLink.getExecute();
		}
		
		if (xLink.getReadOnly()){
			xClass += CSS.MODIFIER.DISABLED;
		}

		if (xLink.getExecute() != null
		 || xLink.getUpdate() != null){
			xOnClick = DBSFaces.getSubmitString(xLink, DBSFaces.HTML.EVENTS.ONCLICK, xExecute, xLink.getUpdate());
		}else{
			
		}

		String xClientId = xLink.getClientId(pContext);
		if (xLink.getReadOnly()){
			xWriter.startElement("div", xLink);
		}else{
			xWriter.startElement("a", xLink);
		}
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass); 
			DBSFaces.setAttribute(xWriter, "style", xLink.getStyle());
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xLink, DBSPassThruAttributes.getAttributes(Key.LINK));
			
			if (xLink.getActionExpression() != null
	 		 && xOnClick != null ){				
//				xWriter.writeAttribute("ontouchstart", "", "ontouchstart"); //Para ipad ativar o css:ACTIVE
				DBSFaces.setAttribute(xWriter, "type", "submit"); 
				//if (xLink.getUpdate()!=null){
					DBSFaces.setAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCLICK, xOnClick); 
				//}
			}else{
				pvRenderHref(pContext, xWriter, xLink);
			}
			xWriter.write(DBSString.toString(xLink.getValue(), ""));
			DBSFaces.renderChildren(pContext, xLink);
			DBSFaces.encodeTooltip(pContext, xLink, xLink.getTooltip());
		if (xLink.getReadOnly()){
			xWriter.endElement("div");
		}else{
			xWriter.endElement("a");
		}
	}
	
	private void pvRenderHref(FacesContext pContext, ResponseWriter pWriter, DBSLink pLink) throws IOException{
		String hrefVal = pLink.getActionExpression().getExpressionString();
        // render an empty value for href if it is not specified
        if (null == hrefVal || 0 == hrefVal.length()) {
            hrefVal = "";
        }

        //Write Anchor attributes
        boolean namespaceParameters = false;
        Param paramList[] = getParamList(pLink);
        StringBuffer sb = new StringBuffer();
        sb.append(hrefVal);
        boolean paramWritten = (hrefVal.indexOf('?') > 0);
        String namingContainerId = null;
        if (namespaceParameters) {
            UIViewRoot viewRoot = pContext.getViewRoot();
            namingContainerId = viewRoot.getContainerClientId(pContext);
        }
        for (int i = 0, len = paramList.length; i < len; i++) {
            String pn = paramList[i].name;
            if (pn != null && pn.length() != 0) {
            	if (namingContainerId != null) {
            		pn = namingContainerId + pn;
            	}
                String pv = paramList[i].value;
                sb.append((paramWritten) ? '&' : '?');
                sb.append(URLEncoder.encode(pn,"UTF-8"));
                sb.append('=');
                if (pv != null && pv.length() != 0) {
                    sb.append(URLEncoder.encode(pv, "UTF-8"));
                }
                paramWritten = true;
            }
        }
        sb.append(getFragment(pLink));
        pWriter.writeURIAttribute("href",
        			pContext.getExternalContext()
                                       .encodeResourceURL(sb.toString()),
                                 "href");
	}

    protected String getFragment(UIComponent component) {

        String fragment = (String) component.getAttributes().get("fragment");
        fragment = (fragment != null ? fragment.trim() : "");
        if (fragment.length() > 0) {
            fragment = "#" + fragment;
        }
        return fragment;

    }
}
