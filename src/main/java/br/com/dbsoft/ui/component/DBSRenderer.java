package br.com.dbsoft.ui.component;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.apache.log4j.Logger;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer.Param;

import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSString;

public class DBSRenderer extends Renderer {
	
	protected static Logger wLogger =  Logger.getLogger(DBSRenderer.class);
	
	protected static final Param[] EMPTY_PARAMS = new Param[0];
	
	protected void renderChildren(FacesContext pFacesContext, UIComponent pComponent) throws IOException {
		UIComponent xLastComponent = null;
		try{
			for (UIComponent xChild:pComponent.getChildren()) {
				xLastComponent = xChild;
				xChild.encodeAll(pFacesContext);
			}
		}catch(Exception e){
			if (xLastComponent!=null){
				wLogger.error("renderChildren:" + pFacesContext.getCurrentPhaseId().toString() + ":" + xLastComponent.getClass().getSimpleName() + ":" + xLastComponent.getClientId(),e);
			}
			throw e;
		}
	}
	
	protected void renderChildren(FacesContext pFacesContext, UIComponent pComponent, int pNivel) throws IOException {
		if (pNivel!=-1){  //pNivel = -1, desabilita a exibição da árvore do render no system.out
			System.out.println(DBSString.repeat(" ", pNivel) + pComponent.getClass().getSimpleName() + ":"  + "render          INI:" + pComponent.getClientId() + "----------------------------------");
		}
		for (Iterator<UIComponent> xIterator = pComponent.getChildren().iterator(); xIterator.hasNext();) {
			UIComponent xChild = xIterator.next();
			renderChild(pFacesContext, xChild, pNivel);
		}
		if (pNivel!=-1){
			System.out.println(DBSString.repeat(" ", pNivel) + pComponent.getClass().getSimpleName() + ":"  + "render          END:" + pComponent.getClientId() + "----------------------------------");
		}
	}

	
	
//	protected void renderChild(FacesContext pFacesContext, UIComponent pChild) throws IOException {
//		renderChild(pFacesContext, pChild, 0);
//	}

	protected void renderChild(FacesContext pFacesContext, UIComponent pChild, int pNivel) throws IOException {
		if (!pChild.isRendered()) {
			return;
		}

		//Faz o encode begin
		if (pNivel!=-1){
			System.out.println(DBSString.repeat(" ", pNivel) +  pChild.getClass().getSimpleName() + ":" +  "encodeBegin     INI:" + pChild.getClientId());
		}
		pChild.encodeBegin(pFacesContext); //<<BEGIN
		if (pNivel!=-1){
			System.out.println(DBSString.repeat(" ", pNivel) + pChild.getClass().getSimpleName() + ":" +  "encodeBegin     END:" + pChild.getClientId());
		}
		
		if (pChild.getRendersChildren()) { 
			if (pNivel!=-1){
				System.out.println(DBSString.repeat(" ", pNivel) + pChild.getClass().getSimpleName() + ":"  + "encodeChildren  INI:" + pChild.getClientId());
			}
			//Chama o encodeChildren e ignora a busca por novos filhos
			pChild.encodeChildren(pFacesContext);
			if (pNivel!=-1){
				System.out.println(DBSString.repeat(" ", pNivel) + pChild.getClass().getSimpleName() + ":"  +  "encodeChildren  END:" + pChild.getClientId());
			}
		} else {
			if (pNivel!=-1){
				//Busca os filhos 
				renderChildren(pFacesContext, pChild, pNivel + 1);
			}else{
				//Busca os filhos 
				renderChildren(pFacesContext, pChild, pNivel);
			}
		}
		
		if (pNivel!=-1){
			System.out.println(DBSString.repeat(" ", pNivel) + pChild.getClass().getSimpleName() + ":"  + "encodeEnd       INI:" + pChild.getClientId());
		}
		//Faz o encode end
		pChild.encodeEnd(pFacesContext);
		if (pNivel!=-1){
			System.out.println(DBSString.repeat(" ", pNivel) + pChild.getClass().getSimpleName() + ":"  + "encodeEnd       END:" + pChild.getClientId());
		}
	}
	
	

	/**
	 * Retorna a URL do caminho da página incluindo o ela mesma
	 * @param facesContext
	 * @return
	 */
	protected String getActionURL(FacesContext pFacesContext) {
		String xActionURL = pFacesContext.getApplication().getViewHandler().getActionURL(pFacesContext, pFacesContext.getViewRoot().getViewId());
		
		return pFacesContext.getExternalContext().encodeActionURL(xActionURL);
	}
	
    /**
     * Retornar URL do caminho local onde se encontra os resources
     * @param facesContext
     * @param value
     * @return
     */
    protected String getResourceURL(FacesContext pFacesContext, String pValue) {
        if (pValue.contains(ResourceHandler.RESOURCE_IDENTIFIER)) {
            return pValue;
        } else {
            String xUrl = pFacesContext.getApplication().getViewHandler().getResourceURL(pFacesContext, pValue);

            return pFacesContext.getExternalContext().encodeResourceURL(xUrl);
        }
    }
    
//    protected String getResourceRequestPath(FacesContext pFacesContext, String pResourceName) {
//		Resource xResource = pFacesContext.getApplication().getResourceHandler().createResource(pResourceName, "primefaces");
//
//        return xResource.getRequestPath();
//	}
    
    protected void encodeClientParameters(FacesContext pContext, UIComponent pComponent) throws IOException{
    	if (pComponent.getChildCount() > 0){
    		for (UIComponent xC: pComponent.getChildren()){
    			if (xC instanceof UIParameter){
    				xC.encodeAll(pContext);
    			}
    		}
    	}
    }
    
    protected void encodeClientBehaviors(FacesContext pContext, ClientBehaviorHolder pComponent) throws IOException {
        ResponseWriter xWriter = pContext.getResponseWriter();
        
        Map<String,List<ClientBehavior>> xBehaviorEvents = pComponent.getClientBehaviors();

        if(!xBehaviorEvents.isEmpty()) {
            String xClientId = ((UIComponent) pComponent).getClientId(pContext);
            List<ClientBehaviorContext.Parameter> xParams = Collections.emptyList();

            for(Iterator<String> xEventIterator = xBehaviorEvents.keySet().iterator(); xEventIterator.hasNext();) {
                String xEvent = xEventIterator.next();
                String xDomEvent = xEvent;

                if(xEvent.equalsIgnoreCase("valueChange"))       //editable value holders
                    xDomEvent = "change";
                else if(xEvent.equalsIgnoreCase("action"))       //commands
                    xDomEvent = "click";

                for(Iterator<ClientBehavior> xBehaviorIter = xBehaviorEvents.get(xEvent).iterator(); xBehaviorIter.hasNext();) {
                    ClientBehavior xBehavior = xBehaviorIter.next();
                    ClientBehaviorContext xCBC = ClientBehaviorContext.createClientBehaviorContext(pContext, (UIComponent) pComponent, xEvent, xClientId, xParams);
                    String xScript = xBehavior.getScript(xCBC);    //could be null if disabled

                    if(xScript != null) {
                    	xWriter.writeAttribute("on" + xDomEvent, xScript, null);
                    }
                }
            }
        }
    }
    
    
    protected void decodeBehaviors(FacesContext pContext, UIComponent pComponent)  {

        if(!(pComponent instanceof ClientBehaviorHolder)) {
            return;
        }

        Map<String, List<ClientBehavior>> xBehaviors = ((ClientBehaviorHolder) pComponent).getClientBehaviors();
        if(xBehaviors.isEmpty()) {
            return;
        }

        Map<String, String> xParams = pContext.getExternalContext().getRequestParameterMap();
        String xBehaviorEvent = xParams.get("javax.faces.behavior.event");

        if(null != xBehaviorEvent) {
            List<ClientBehavior> xBehaviorsForEvent = xBehaviors.get(xBehaviorEvent);

            if(xBehaviorsForEvent != null && !xBehaviorsForEvent.isEmpty()) {
               String xBehaviorSource = xParams.get("javax.faces.source");
               String xClientId = pComponent.getClientId();

               if(xBehaviorSource != null && xClientId.startsWith(xBehaviorSource)) {
                   for(ClientBehavior xBehavior: xBehaviorsForEvent) {
                       xBehavior.decode(pContext, pComponent);
                   }
               }
            }
        }
    }
    
//    protected void renderRecursive(FacesContext context, UIComponent component)
//            throws IOException {
//
//          // suppress rendering if "rendered" property on the component is
//          // false.
//          if (!component.isRendered()) {
//              return;
//          }
//
//          // Render this component and its children recursively
//          component.encodeBegin(context);
//          if (component.getRendersChildren()) {
//              component.encodeChildren(context);
//          } else {
//              Iterator<UIComponent> kids = getChildren(component);
//              while (kids.hasNext()) {
//                  UIComponent kid = kids.next();
//                  renderRecursive(context, kid);
//              }
//          }
//          component.encodeEnd(context);
//
//      }
    
    /**
     * @param component <code>UIComponent</code> for which to extract children
     *
     * @return an Iterator over the children of the specified
     *  component, selecting only those that have a
     *  <code>rendered</code> property of <code>true</code>.
     */
    protected Iterator<UIComponent> getChildren(UIComponent component) {

        int childCount = component.getChildCount();
        if (childCount > 0) {
            return component.getChildren().iterator();
        } else {
            return Collections.<UIComponent>emptyList().iterator();
        }

    } 
    
    /**
     * Retorna nome do form em que está contido o componente informado
     * @param context
     * @param component
     * @return
     */
    public static String getFormId(FacesContext pContext, UIComponent pComponent) {
        UIComponent xParent = pComponent;
        while (!(xParent instanceof UIForm)){
           xParent = xParent.getParent();
           if (xParent==null){
        	   return "";
           }        
        }
    	return xParent.getClientId(pContext);
     }
    

	public char getSeparatorChar(FacesContext pContext){
		return UINamingContainer.getSeparatorChar(pContext);
	}

	/**
	 * Retorna clienteId para parte do componente onde estão os dados
	 * @param pComponent
	 * @return
	 */
	public String getInputDataClientId(UIComponent pComponent){
		return pComponent.getClientId() + DBSFaces.CSS.MODIFIER.DATA.trim();
	}
	
    /**
     * @param component the component of interest
     *
     * @return true if this renderer should render an id attribute.
     */
    protected boolean shouldWriteIdAttribute(UIComponent component) {

        // By default we only write the id attribute if:
        //
        // - We have a non-auto-generated id, or...
        // - We have client behaviors.
        //
        // We assume that if client behaviors are present, they
        // may need access to the id (AjaxBehavior certainly does).

        String id;
        return (null != (id = component.getId()) &&
                    (!id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX) ||
                        ((component instanceof ClientBehaviorHolder) &&
                          !((ClientBehaviorHolder)component).getClientBehaviors().isEmpty())));
    }
    
    
    protected Param[] getParamList(UIComponent command) {

        if (command.getChildCount() > 0) {
            ArrayList<Param> parameterList = new ArrayList<Param>();

            for (UIComponent kid : command.getChildren()) {
                if (kid instanceof UIParameter) {
                    UIParameter uiParam = (UIParameter) kid;
                    if (!uiParam.isDisable()) {
                        Object value = uiParam.getValue();
                        Param param = new Param(uiParam.getName(),
                                                (value == null ? null :
                                                 value.toString()));
                        parameterList.add(param);
                    }
                }
            }
            return parameterList.toArray(new Param[parameterList.size()]);
        } else {
            return EMPTY_PARAMS;
        }


    }
  
	/**
	 * Encode do id se tiver sido informado
	 * @param component
	 * @param pWriter
	 * @param pClientid
	 * @throws IOException
	 */
	protected void writeIdAttribute(ResponseWriter pWriter, UIComponent component, String pClientid) throws IOException {
		if (shouldWriteIdAttribute(component)) {
			pWriter.writeAttribute("id", pClientid, "id");
			pWriter.writeAttribute("name", pClientid, "name");
		}
	}

    // --------------------------------------------------------- Private Methods

    protected boolean wasClicked(FacesContext context,
                                 UIComponent component,
                                 String clientId) {

        Map<String,String> requestParamMap =
              context.getExternalContext().getRequestParameterMap();

        if (clientId == null) {
            clientId = component.getClientId(context);
        }

        // Fire an action event if we've had a traditional (non-Ajax)
        // postback, or if we've had a partial or behavior-based postback.
        return (requestParamMap.containsKey(clientId) ||
                RenderKitUtils.isPartialOrBehaviorAction(context, clientId));
    }
    
    /**
     * Collections parameters for use with Behavior script rendering.
     * Similar to getParamList(), but returns a collection of 
     * ClientBehaviorContext.Parameter instances.
     *
     * @param command the command which may have parameters
     *
     * @return a collection of ClientBehaviorContext.Parameter instances.
     */
    protected Collection<ClientBehaviorContext.Parameter> getBehaviorParameters(
        UIComponent command) {

        ArrayList<ClientBehaviorContext.Parameter> params = null;
        int childCount = command.getChildCount();

        if (childCount > 0) {

            for (UIComponent kid : command.getChildren()) {
                if (kid instanceof UIParameter) {
                    UIParameter uiParam = (UIParameter) kid;
                    String name = uiParam.getName();
                    Object value = uiParam.getValue();

                    if ((name != null) && (name.length() > 0)) {

                        if (params == null) {
                            params = new ArrayList<ClientBehaviorContext.Parameter>(childCount);
                        }

                        params.add(new ClientBehaviorContext.Parameter(name, value));
                    }
                }
            }
        }

        return (params == null) ? Collections.<ClientBehaviorContext.Parameter>emptyList() : params;

    }
    
        
	
    
//	protected void renderPassThruAttributes(FacesContext pFacesContext, UIComponent pComponent, String pVar, String[] pAttrs) throws IOException {
//		ResponseWriter xWriter = pFacesContext.getResponseWriter();
//		
//		for(String event : pAttrs) {			
//			String xEventHandler = (String) pComponent.getAttributes().get(event);
//			
//			if(xEventHandler != null)
//				xWriter.write(pVar + ".addListener(\"" + event.substring(2, event.length()) + "\", function(e){" + xEventHandler + ";});\n");
//		}
//	}
	
//	protected void renderPassThruAttributes(FacesContext pFacesContext, UIComponent pComponent, String[] pAttrs) throws IOException {
//		ResponseWriter xWriter = pFacesContext.getResponseWriter();
//		
//		for(String xAttribute : pAttrs) {
//			Object xValue = pComponent.getAttributes().get(xAttribute);
//			
//			if(shouldRenderAttribute(xValue))
//				xWriter.writeAttribute(xAttribute, xValue.toString(), xAttribute);
//		}
//	}
//	
//	protected void renderPassThruAttributes(FacesContext pFacesContext, UIComponent pComponent, String[] pAttrs, String[] pIgnoredAttrs) throws IOException {
//		ResponseWriter xWriter = pFacesContext.getResponseWriter();
//		
//		for(String xAttribute : pAttrs) {
//			if(isIgnoredAttribute(xAttribute, pIgnoredAttrs)) {
//				continue;
//			}
//			
//			Object xValue = pComponent.getAttributes().get(xAttribute);
//			
//			if(shouldRenderAttribute(xValue))
//				xWriter.writeAttribute(xAttribute, xValue.toString(), xAttribute);
//		}
//	}	
//	
//	private boolean isIgnoredAttribute(String pAttribute, String[] pIgnoredAttrs) {
//		for(String xIgnoredAttribute : pIgnoredAttrs) {
//			if(pAttribute.equals(xIgnoredAttribute)) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
//	
//    protected boolean shouldRenderAttribute(Object pValue) {
//        if(pValue == null)
//            return false;
//      
//        if(pValue instanceof Boolean) {
//            return ((Boolean) pValue).booleanValue();
//        }
//        else if(pValue instanceof Number) {
//        	Number number = (Number) pValue;
//        	
//            if (pValue instanceof Integer)
//                return number.intValue() != Integer.MIN_VALUE;
//            else if (pValue instanceof Double)
//                return number.doubleValue() != Double.MIN_VALUE;
//            else if (pValue instanceof Long)
//                return number.longValue() != Long.MIN_VALUE;
//            else if (pValue instanceof Byte)
//                return number.byteValue() != Byte.MIN_VALUE;
//            else if (pValue instanceof Float)
//                return number.floatValue() != Float.MIN_VALUE;
//            else if (pValue instanceof Short)
//                return number.shortValue() != Short.MIN_VALUE;
//        }
//        
//        return true;
//    }
//    	
}



