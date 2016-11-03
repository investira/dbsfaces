package br.com.dbsoft.ui.component.link;

import java.io.IOException;
import java.net.URLEncoder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer.Param;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSUICommandRenderer;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSString;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSLink.RENDERER_TYPE)
public class DBSLinkRenderer extends DBSUICommandRenderer {
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSLink xLink = (DBSLink) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = CSS.LINK.MAIN + getBasicStyleClass(xLink);
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
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass); 
			DBSFaces.encodeAttribute(xWriter, "style", xLink.getStyle());
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xLink, DBSPassThruAttributes.getAttributes(Key.LINK));
			
			if (xLink.getActionExpression() != null
	 		 && xOnClick != null ){				
//				xWriter.writeAttribute("ontouchstart", "", "ontouchstart"); //Para ipad ativar o css:ACTIVE
				DBSFaces.encodeAttribute(xWriter, "type", "submit"); 
				//if (xLink.getUpdate()!=null){
					DBSFaces.encodeAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCLICK, xOnClick); 
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
