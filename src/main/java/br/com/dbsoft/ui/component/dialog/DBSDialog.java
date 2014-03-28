package br.com.dbsoft.ui.component.dialog;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSDialog.COMPONENT_TYPE)
public class DBSDialog extends DBSUIOutput implements NamingContainer{  


	protected static Logger			wLogger = Logger.getLogger(DBSDialog.class);
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIALOG;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	public static enum DIALOG_ICON
	{
		NENHUM 		("n"),
	    ATENCAO 	("a"),
	    CONFIRMAR	("c"),
	    ERRO 		("e"),
	    IGNORAR 	("g"),
	    INFORMACAO	("i"),
	    PROIBIDO 	("p"),
	    SOBRE 		("b"),
	    SUCESSO		("s");

	    String wString;

	    DIALOG_ICON (String pString){
	    	wString = pString;
	    }
	
	    @Override
		public String toString() {
	    	return wString;
	    }
	}
	
	protected enum PropertyKeys {
		caption,
		width,
		height,
		resizable,
		closeable,
		styleClass,
		okAction,
		yesAction,
		noAction,
		update,
		execute,
		style,
		messageIcon,
		tooltip,
		index;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}
	

	
    public DBSDialog(){
		setRendererType(DBSDialog.RENDERER_TYPE);

//		 FacesContext xContext = FacesContext.getCurrentInstance();
//		 xContext.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreValidateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PostValidateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRenderViewEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRenderComponentEvent.class,this);
//		 //-------------------------------------------------------------------------------
//		 xContext.getViewRoot().subscribeToViewEvent(PostConstructViewMapEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PostRestoreStateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreDestroyViewMapEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRemoveFromViewEvent.class,this);		
    }
	
    @Override
    public void decode(FacesContext pContext) {
        super.decode(pContext);
    }

	public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("caption", pCaption);
	}
	

	public Boolean isResizable() {
		return (Boolean) getStateHelper().eval(PropertyKeys.resizable, true);
	}
	
	public void setResizable(Boolean pResizable) {
		getStateHelper().put(PropertyKeys.resizable, pResizable);
		handleAttribute("resizable", pResizable);
	}
	

	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, "");
	}
	
	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	
	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}
		
	
	public void setCloseble(Boolean pCloseble) {
		getStateHelper().put(PropertyKeys.closeable, pCloseble);
		handleAttribute("closeable", pCloseble);
	}
	
	public Boolean getCloseble() {
		return (Boolean) getStateHelper().eval(PropertyKeys.closeable, true);
	}

	public void setWidth(Integer pWidth) {
		getStateHelper().put(PropertyKeys.width, pWidth);
		handleAttribute("width", pWidth);
	}
	
	public Integer getWidth() {
		return (Integer) getStateHelper().eval(PropertyKeys.width, 100);
	}

	public void setHeight(Integer pHeight) {
		getStateHelper().put(PropertyKeys.height, pHeight);
		handleAttribute("height", pHeight);
	}
	
	public Integer getHeight() {
		return (Integer) getStateHelper().eval(PropertyKeys.height, 100);
	}
	
	public void setIndex(Integer pIndex) {
		getStateHelper().put(PropertyKeys.index, pIndex);
		handleAttribute("index", pIndex);
	}
	
	public Integer getIndex() {
		return (Integer) getStateHelper().eval(PropertyKeys.index, 0);
	}
	
	public String getMessageIcon() {
		return (String) getStateHelper().eval(PropertyKeys.messageIcon, DBSDialog.DIALOG_ICON.NENHUM.toString());
	}
	
	public void setMessageIcon(String pMessageIcon) {
		getStateHelper().put(PropertyKeys.messageIcon, pMessageIcon);
		handleAttribute("messageIcon", pMessageIcon);
	}

	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, "");
	}
	
	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}

	public String getExecute() {
		return (String) getStateHelper().eval(PropertyKeys.execute, "");
	}
	
	public void setExecute(String pExecute) {
		getStateHelper().put(PropertyKeys.execute, pExecute);
		handleAttribute("execute", pExecute);
	}
// ################################################################

	public String getYesAction() {
		String xStr = DBSFaces.getELString(this, PropertyKeys.yesAction.toString());
		if (xStr==null){
			//Considera que action por ser um redirect para outra página
			xStr = (String) getStateHelper().eval(PropertyKeys.yesAction, null);
		}
		return xStr;

	}

	public void setYesAction(String pYesAction) {
    	getStateHelper().put(PropertyKeys.yesAction, pYesAction);
		handleAttribute("yesAction", pYesAction);
	}	
	
	public String getNoAction() {
		String xStr = DBSFaces.getELString(this, PropertyKeys.noAction.toString());
		if (xStr==null){
			//Considera que action por ser um redirect para outra página
			xStr = (String) getStateHelper().eval(PropertyKeys.noAction, null);
		}
		return xStr;
	}

	public void setNoAction(String pNoAction) {
    	getStateHelper().put(PropertyKeys.noAction, pNoAction);
		handleAttribute("noAction", pNoAction);
	}	

	public String getOkAction() {
		String xStr = DBSFaces.getELString(this, PropertyKeys.okAction.toString());
		if (xStr==null){
			//Considera que action por ser um redirect para outra página
			xStr = (String) getStateHelper().eval(PropertyKeys.okAction, null);
		}
		return xStr;
	}

	public void setOkAction(String pOkAction) {
    	getStateHelper().put(PropertyKeys.okAction, pOkAction);
		handleAttribute("okAction", pOkAction);
	}	
	
	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, "");
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}

//	@Override
//	public void processEvent(SystemEvent event) throws AbortProcessingException {
//		FacesContext xContext = FacesContext.getCurrentInstance();
//		UIComponent xComponent = (UIComponent) event.getSource();
//		System.out.println("=============================================================================");
//		System.out.println("| processEvent :" + event.getClass().getName() + ":" + xComponent.getClass().getName());
//		System.out.println("| UIComponentID:" + xComponent.getClientId());
//		System.out.println("| Children     :" + xComponent.getChildren().size());
////		if (xComponent.getParent().getChildren().size() > 0) {
////			DBSFaces.showComponentChildren(xContext, xComponent, 0);
////		}
//		System.out.println("=============================================================================");
//	}

	
//	@Override
//	public boolean isListenerForSource(Object source) {
////		 String xStr = "";
////		 if (source instanceof UIComponent){
////		 xStr = ((UIComponent) source).getClientId();
////		 }
////		 xStr = xStr + "\t\t:" + source.getClass().getName();
////		
////		 System.out.println("isListenerForSource:" + xStr);
//				
////		return (source instanceof UIViewRoot);
////		 return ((source instanceof UIViewRoot) || (source instanceof DBSCrudForm));
////		 return (source instanceof DBSCrudForm);
//		return false;
//	}

}