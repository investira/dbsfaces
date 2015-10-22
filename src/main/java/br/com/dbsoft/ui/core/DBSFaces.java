package br.com.dbsoft.ui.core;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.render.RenderKit;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.facelets.FaceletContext;

import org.apache.log4j.Logger;
import org.jboss.weld.context.SerializableContextualInstanceImpl;

import br.com.dbsoft.core.DBSSDK;
import br.com.dbsoft.core.DBSSDK.ENCODE;
import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.DBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.bean.DBSBean;
import br.com.dbsoft.ui.bean.crud.DBSCrudBean;
import br.com.dbsoft.ui.bean.report.DBSReportBean;
import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.checkbox.DBSCheckbox;
import br.com.dbsoft.ui.component.datatable.DBSDataTable;
import br.com.dbsoft.ui.component.datatable.DBSDataTableColumn;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;
import br.com.dbsoft.ui.component.fileupload.DBSFileUpload;
import br.com.dbsoft.util.DBSBoolean;
import br.com.dbsoft.util.DBSDate;
import br.com.dbsoft.util.DBSIO;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;

import com.sun.faces.util.DebugUtil;
	
/**
 * @author ricardo.villar
 *
 */
/**
 * @author ricardo.villar
 *
 */
public class  DBSFaces {
	
	protected static 	Logger	wLogger = Logger.getLogger(DBSFaces.class);
	
//	public static final String DOMAIN = "br.com.dbsoft";
	public static final String DOMAIN_UI_COMPONENT = DBSSDK.DOMAIN + ".ui.component";
	public static final String FAMILY = "DBSoft_Family";
	public static final String OPTIMIZED_PACKAGE = DBSSDK.DOMAIN + ".ui";

	public static final String PARTIAL_REQUEST_PARAM = "javax.faces.partial.ajax"; 
    public static final String PARTIAL_UPDATE_PARAM = "javax.faces.partial.render";
    public static final String PARTIAL_PROCESS_PARAM = "javax.faces.partial.execute";
    public static final String PARTIAL_SOURCE_PARAM = "javax.faces.source"; /*recupera o nome do objeto que fez o submit*/
    public static final String PARTIAL_BEHAVIOR_EVENT_PARAM = "javax.faces.behavior.event";
    public static final String JAVAX_FACES_LOCATION_HEAD = "javax_faces_location_HEAD";
    public static final String JAVAX_FACES_LOCATION_BODY = "javax_faces_location_BODY";
    public static final String JAVAX_FACES_LOCATION_FORM = "javax_faces_location_FORM";
    public static final char SEPARATOR = UINamingContainer.getSeparatorChar(FacesContext.getCurrentInstance());
	
	public static class ID
	{
	    public static final String BUTTON = "button";
	    public static final String DATATABLE = "dataTable";
	    public static final String DATATABLECOLUMN = "dataTableColumn";
	    public static final String CRUDVIEW = "crudView";
	    public static final String CRUDDIALOG = "crudDialog";
	    public static final String CRUDTABLE = "crudTable";
	    public static final String DIALOG = "dialog";
	    public static final String FILEUPLOAD = "fileUpload";
	    public static final String INPUTTEXT = "inputText";
	    public static final String INPUTTEXTAREA = "inputTextArea";
	    public static final String INPUTDATE = "inputDate";
	    public static final String INPUTPHONE = "inputPhone";
	    public static final String INPUTNUMBER = "inputNumber";
	    public static final String INPUTMASK = "inputMask";
	    public static final String LABEL = "label";
	    public static final String LOADING = "loading";
	    public static final String IMG = "img";
	    public static final String DIV = "div";
	    public static final String MENU = "menu";
	    public static final String MENUITEM = "menuitem";
	    public static final String MENUITEMSEPARATOR = "menuitemSeparator";
	    public static final String LINK = "link";
	    public static final String TAB = "tab";
	    public static final String TABPAGE = "tabPage";
	    public static final String INCLUDE = "include";
	    public static final String ACCORDION = "accordion";
	    public static final String ACCORDIONSECTION = "accordionSection";
	    public static final String CALENDAR = "calendar";
	    public static final String CHECKBOX = "checkbox";
	    public static final String COMBOBOX = "combobox";
	    public static final String LISTBOX = "listbox";
	    public static final String RADIO = "radio";
	    public static final String PROGRESS = "progress";
	    public static final String STYLE = "style";
	    public static final String COMPONENTTREE = "componenttree";
	    public static final String TABLE = "table";
	    public static final String TOOLTIP = "tooltip";
	    public static final String UL = "ul";
	    public static final String LI = "li";
	    public static final String PARALLAX = "parallax";
	    public static final String PARALLAXSECTION = "parallaxSection";
	    public static final String FORM = "form";
	    public static final String REPORT = "report"; 
	    public static final String REPORTFORM = "reportForm"; 
	    public static final String BEANDIALOGMESSAGES = "beanDialogMessages";
	    public static final String BEANCRUDDIALOGMESSAGES = "beanCrudDialogMessages";
	    public static final String MESSAGES = "messages";
	    public static final String GROUP = "group";
	    public static final String PUSH = "push";
	    public static final String MESSAGELIST = "messageList";
	    public static final String QUICKINFO = "quickInfo";
	    public static final String CHARTS = "charts";
	    public static final String CHART = "chart";
	    public static final String CHARTVALUE = "chartValue";
	}

	public static class HTML
	{
		public static class EVENTS
		{
			public static final String ONCLICK = "onclick";
			public static final String ONCHANGE = "onchange";
			public static final String ONDBLCLICK = "ondblclick";
			public static final String ONKEYDOWN = "onkeydown";
			public static final String ONKEYPRESS = "onkeypress";
			public static final String ONKEYUP = "onkeyup";
			public static final String ONMOUSEDOWN = "onmousedown";
			public static final String ONMOUSEMOVE = "onmousemove";
			public static final String ONMOUSEOUT = "onmouseout";
			public static final String ONMOUSEOVER = "onmouseover";
			public static final String ONMOUSEUP = "onmouseup";
			public static final String ONCLOSE = "onclose";
			public static final String ONSELECT = "onselect";
		}
	}
	
	public static class CSS
	{
		public static final String CLASS_PREFIX = "dbs_";
		public static final String WINDOW_CENTER = DBSFaces.CSS.CLASS_PREFIX +  "window_center ";
		public static final String CHILD_CENTER = DBSFaces.CSS.CLASS_PREFIX +  "child_center ";
		public static final String NOT_SELECTABLE = DBSFaces.CSS.CLASS_PREFIX +  "not_selectable ";
		public static final String BACK_TEXTURE_BLACK = DBSFaces.CSS.CLASS_PREFIX +  "back_texture_black ";
		public static final String BACK_TEXTURE_BLACK_TRANSPARENT_GRADIENT = DBSFaces.CSS.CLASS_PREFIX +  "back_texture_black_transparent_gradient ";
		public static final String BACK_TEXTURE_BLACK_GRADIENT = DBSFaces.CSS.CLASS_PREFIX +  "back_texture_black_gradient ";
		public static final String BACK_TEXTURE_WHITE_GRADIENT = DBSFaces.CSS.CLASS_PREFIX +  "back_texture_white_gradient ";
		public static final String BACK_TEXTURE_WHITE_TRANSPARENT_GRADIENT = DBSFaces.CSS.CLASS_PREFIX +  "back_texture_white_transparent_gradient ";
		public static final String BACK_GRADIENT_WHITE = DBSFaces.CSS.CLASS_PREFIX +  "back_gradient_white ";
		public static final String PARENT_FILL = DBSFaces.CSS.CLASS_PREFIX +  "parent_fill ";
		public static final String ICON = DBSFaces.CSS.CLASS_PREFIX +  "icon ";
		public static final String HORIZONTAL_LINE = DBSFaces.CSS.CLASS_PREFIX +  "horizontal_line ";
		public static final String HORIZONTAL_LINE_WHITE = DBSFaces.CSS.CLASS_PREFIX +  "horizontal_line_white ";
		public static final String VERTICAL_LINE = DBSFaces.CSS.CLASS_PREFIX +  "vertical_line ";
		
		public static class THEME
		{
			public static final String ACTION = " -th_action ";
			public static final String BACKGROUND_COLOR = " -th_back_color ";
			public static final String FONT_COLOR = " -th_font_color ";
		}
		
		public static class MODIFIER
		{
			public static final String AJAX = " -ajax ";
			public static final String BUTTON = " -button ";
			public static final String CAPTION = " -caption ";
			public static final String CHECKBOX = " -checkbox ";
			public static final String OPENED = " -opened ";
			public static final String CLOSED = " -closed ";
			public static final String CLOSABLE = " -closable ";
			public static final String CONTAINER = " -container ";
			public static final String CONTENT = " -content ";
			public static final String COVER = " -cover ";
			public static final String DATA = " -data ";
			public static final String DISABLED = " -disabled ";
			public static final String ERROR = " -error ";
			public static final String EVEN = " -even ";
			public static final String ODD = " -odd ";
			public static final String EXTRAINFO = " -extrainfo ";
			public static final String FILTER = " -filter ";
			public static final String FORM = " -form ";
			public static final String HEADER = " -header ";
			public static final String FOOTER = " -footer ";
			public static final String GRID = " -grid ";
			public static final String ICON = " -icon ";
			public static final String ICONCLOSE = " -iconclose ";
			public static final String INVALID = " -invalid ";
			public static final String INPUT = " -input ";
			public static final String KEY = " -key ";
			public static final String LABEL = " -label ";
			public static final String LAST = " -last ";
			public static final String LARGE = " -large ";
			public static final String LEFT = " -left ";
			public static final String CENTER = " -center ";
			public static final String RIGHT = " -right ";
			public static final String LINE = " -line ";
			public static final String LOADING = " -loading ";
			public static final String MASK = " -mask ";
			public static final String MESSAGE = " -message ";
			public static final String NORMAL = " -normal ";
			public static final String PUSHDISABLED = " -pushDisabled ";
			public static final String READONLY = " -readOnly ";
			public static final String REQUIRED = " -required ";
			public static final String ROW = " -row ";
			public static final String NOT_SELECTABLE = " -not_selectable ";
			public static final String SELECTABLE = " -selectable ";
			public static final String SELECTED = " -selected ";
			public static final String SECTION = " -section ";
			public static final String SMALL = " -small ";
			public static final String SUBMIT = " -submit ";
			public static final String SUGGESTION = " -suggestion ";
			public static final String TITLE = " -title ";
			public static final String SUBTITLE = " -subtitle ";
			public static final String TOOLBAR = " -toolbar ";
			public static final String TOOLTIP = " -tooltip ";
			public static final String VALUE = " -value ";
		}

		public static class CRUDDIALOG
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.CRUDDIALOG;
		}

		public static class CRUDTABLE
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.CRUDTABLE;
		}
		
		public static class DATATABLE
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.DATATABLE;
		}

		public static class DATATABLECOLUMN
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.DATATABLECOLUMN;

		}
		
		public static class LABEL
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.LABEL;
		}
		
		public static class UL
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.UL;
		}

		public static class LI
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.LI;
		}
	
		public static class DIALOG
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.DIALOG;
			public static final String CONFIRMATION = DBSFaces.CSS.DIALOG.MAIN + "-confirmation ";
		}

		public static class FILEUPLOAD
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.FILEUPLOAD;
		}

		public static class INPUT
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  "input";
			public static final String DATA = DBSFaces.CSS.INPUT.MAIN + DBSFaces.CSS.MODIFIER.DATA.trim();
			public static final String LABEL = DBSFaces.CSS.INPUT.MAIN + DBSFaces.CSS.MODIFIER.LABEL.trim();
			public static final String SUBMIT = DBSFaces.CSS.INPUT.MAIN + DBSFaces.CSS.MODIFIER.SUBMIT.trim();
//			public static final String SUGGESTIONVALUE = DBSFaces.CSS.INPUT.MAIN + DBSFaces.CSS.MODIFIER.SUGGESTION.trim() + DBSFaces.CSS.MODIFIER.VALUE.trim();
			public static final String SUGGESTION = DBSFaces.CSS.INPUT.MAIN + DBSFaces.CSS.MODIFIER.SUGGESTION.trim();
			public static final String SUGGESTIONKEY = DBSFaces.CSS.INPUT.MAIN + DBSFaces.CSS.MODIFIER.SUGGESTION.trim() + DBSFaces.CSS.MODIFIER.KEY.trim();
		}
		
		public static class INPUTTEXT
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.INPUTTEXT;
		}

		public static class INPUTTEXTAREA
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.INPUTTEXTAREA;
		}

		public static class INPUTDATE
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.INPUTDATE;
			public static final String DAY = "-day";
			public static final String MONTH = "-month";
			public static final String YEAR = "-year";
			public static final String HOUR = "-hour";
			public static final String MINUTE = "-minute";
			public static final String SECOND = "-second";
		}
		
		public static class INPUTPHONE
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.INPUTPHONE;
			public static final String DDI = "-ddi";
			public static final String DDD = "-ddd";
			public static final String NUMBER = "-number";
		}

		public static class INPUTNUMBER
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.INPUTNUMBER;
		}
		public static class INPUTMASK
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.INPUTMASK;
		}
		
		public static class CHECKBOX
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.CHECKBOX;
		}
		public static class LISTBOX
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.LISTBOX;
		}
		public static class COMBOBOX
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.COMBOBOX;
		}

		public static class BUTTON
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.BUTTON;
		}
		public static class DIV
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.DIV;
		}
		
		public static class MENU
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.MENU;
		}

		public static class MENUITEM
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.MENUITEM;
		}
		
		public static class MENUITEMSEPARATOR
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.MENUITEMSEPARATOR;
		}
		
		public static class COMPONENTTREE
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.COMPONENTTREE;
		}
		
		
		public static class LINK
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.LINK;
		}
		
		public static class LOADING
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.LOADING;
			public static final String CONTENT = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.LOADING + "_content";
		}
		
		public static class TAB
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.TAB;
		}

		public static class TABPAGE
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.TABPAGE;
		}

		public static class ACCORDION
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.ACCORDION;
			public static final String SECTION = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.ACCORDION + "_section";
			public static final String SECTION_CAPTION = DBSFaces.CSS.ACCORDION.SECTION + DBSFaces.CSS.MODIFIER.CAPTION.trim();
			public static final String SECTION_CONTAINER = DBSFaces.CSS.ACCORDION.SECTION + DBSFaces.CSS.MODIFIER.CONTAINER.trim();
		}

		public static class CALENDAR
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.CALENDAR;
			public static final String DAYS = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.CALENDAR + "_days";
			public static final String MONTH = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.CALENDAR + "_month";
			public static final String YEAR = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.CALENDAR + "_year";
		}

		public static class RADIO
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.RADIO;
		}

		public static class PROGRESS
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.PROGRESS;
		}
		
		public static class TOOLTIP
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.TOOLTIP;
		}
		
		public static class REPORT
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.REPORT;
		}

		public static class REPORTFORM
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.REPORTFORM;
		}

		public static class MESSAGES
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.MESSAGES;
		}
		
		public static class GROUP
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.GROUP;
		}

		public static class PUSH
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.PUSH;
		}

		public static class MESSAGELIST
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.MESSAGELIST;
		}

		public static class QUICKINFO
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.QUICKINFO;
		}

		public static class CHARTS
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.CHARTS;
		}

		public static class CHART
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.CHART;
		}
		
		public static class CHARTVALUE
		{	
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX + DBSFaces.ID.CHARTVALUE;
		}

		public static class PARALLAX
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.PARALLAX;
		}
		public static class PARALLAXSECTION
		{
			public static final String MAIN = DBSFaces.CSS.CLASS_PREFIX +  DBSFaces.ID.PARALLAXSECTION;
		}
	}
	
	
	
    public static void setAttribute(ResponseWriter pWriter, String pAttribute, Object pValue, String pValueDefault) throws IOException{
	   	if (pValue != null){	
			pWriter.writeAttribute(pAttribute, pValue, null);
		}else{
			if (pValueDefault != null ){	
				pWriter.writeAttribute(pAttribute, pValueDefault, null);
			}
		}
     }
	
	public static Object getAttributeNotNull(UIComponent pComponent, String pAttributeName, Object pDefaultValue){
		if (pComponent.getAttributes().get(pAttributeName) != null){ 
			return pComponent.getAttributes().get(pAttributeName);
		}else{
			return pDefaultValue;
		}
	}

	
	/**
	 * Retornar qual o form é pai deste controle
	 * @param pComponent
	 * @return
	 */
	public static UIForm getForm(UIComponent pComponent) {
		UIComponent xParent = pComponent.getParent();
		while(xParent != null) {
			if(xParent instanceof UIForm) {
				return (UIForm) xParent;
            }
			xParent = xParent.getParent();
		}
		return null;
//		RenderKitUtils.getFormClientId(pComponent, pContext); //Anternativa ao código acima. Não testei. Ricardo
	}	
	
	public static void showComponentParent(UIComponent pComponent, Integer pI){
		if (pComponent.getParent()!=null){
			System.out.println(pI + ":" + pComponent.getParent());
			pI ++;
			showComponentParent(pComponent.getParent(), pI);
		}
	}
	
	/**
	 * Exibe os componentes filhos a partir de um componente mãe
	 * @param pContext
	 * @param pComponent
	 * @param pNivel
	 */
	public static void showComponentChildren(FacesContext pContext, UIComponent pComponent, int pNivel){
		System.out.println(DBSString.repeat(" ", pNivel) +  pComponent.getClass().toString() + " " + pComponent.getClientId());
		List<UIComponent> xC = pComponent.getChildren();
		if (xC != null){
			if (xC.size()>0){
				for (int xX = 0; xX<xC.size(); xX++){
					showComponentChildren(pContext, xC.get(xX), pNivel + 1);
				}
			}
		}else{
			System.out.println("filhos nulos");
		}
	}
	
	/**
	 * Exibe no console o conteúdo de objeto Map
	 * @param map
	 * @param pNivel
	 */
	
	public static void showMapContentComponent(Map<String, UIComponent> pMap, int pNivel){
	    for (Entry<String, UIComponent> xEntry : pMap.entrySet()) {
	        	System.out.println(DBSString.repeat(" ", pNivel) + 
	        			             "Key : " + xEntry.getKey() + 
	       		            	  " Value : " + xEntry.getValue());
        		showMapContentComponent(xEntry.getValue().getFacets(),pNivel + 1);
	    }
	}

	/**
	 * Exibe os componentes a partir da viewroot;
	 */
	public static void showViewRoot(){
		DebugUtil.printTree(FacesContext.getCurrentInstance().getViewRoot(),System.out);
	}

	/**
	 * Exibe os componentes a partir da lista de componentes filhos;
	 */
	public static void showViewRoot(List<UIComponent> pComponents, int pLevel){
		String xStr;

		for (UIComponent xC : pComponents){
			xStr = "Component:" + xC.getClass();
			if (xC.getClientId() != null){
				xStr = DBSString.repeat(" ", pLevel) + "[" + pLevel + "]"+ xStr + ": "  + xC.getClientId();
			}else{
				xStr = xStr + ": id é nulo";
			}
			System.out.println(xStr);
			showViewRoot(xC.getChildren(), pLevel + 1);
		}
	}

	public static void showViewRoot(Iterator<UIComponent> pComponents, int pLevel){
		String xStr;

		while (pComponents.hasNext()){
			UIComponent xC = pComponents.next();
			xStr = "Component:" + xC.getClass();
			if (xC.getClientId() != null){
				xStr = DBSString.repeat(" ", pLevel) + "[" + pLevel + "]"+ xStr + ": "  + xC.getClientId();
			}else{
				xStr = xStr + ": id é nulo";
			}
			System.out.println(xStr);
			showViewRoot(xC.getFacetsAndChildren(), pLevel + 1);
		}
	}
	//==============================================================================
	/**
	 * Retorna o componente que possuir o Id iqual ao Id solicitado.
	 * Não considera os Ids dos parent(Caso existam) como parte do Id do componente a ser pesquisado 
	 * @param pId
	 * @param pComponents
	 * @return
	 */
	public static UIComponent findComponent(String pId, Iterator<UIComponent>pComponents){
		return findComponent(pId, pComponents, null);
	}
	/**
	 * Retorna o componente que possuir o Id iqual ao Id solicitado.
	 * Não considera os Ids dos parent(Caso existam) como parte do Id do componente a ser pesquisado 
	 * @param pId
	 * @param pComponents
	 * @return
	 */
	public static UIComponent findComponent(String pId, Iterator<UIComponent>pComponents, Class<?> pIgnoreClass){
		UIComponent xComponent = null;
		while (pComponents.hasNext()){
			UIComponent xC = pComponents.next();
			if (pIgnoreClass == null ||
				!pIgnoreClass.equals(xC.getClass())){
				if (xC.getId() != null){
					if (xC.getId().toLowerCase().equals(pId.toLowerCase())){
						return xC;
					}
				}
				xComponent = findComponent(pId, xC.getFacetsAndChildren(), pIgnoreClass);
				if (xComponent != null){
					break;
				}
			}
		}
		return xComponent;
	}
	
	/**
	 * Retorna o componente que possuir o Id iqual ao Id solicitado.
	 * Não considera os Ids dos parent(Caso existam) como parte do Id do componente a ser pesquisado 
	 * @param pId
	 * @param pComponents
	 * @return
	 */
	public static UIComponent findComponent(String pId, List<UIComponent> pComponents){
		return findComponent(pId, pComponents, null);
	}
	
	/**
	 * Retorna o componente que possuir o Id iqual ao Id solicitado.
	 * Não considera os Ids dos parent(Caso existam) como parte do Id do componente a ser pesquisado 
	 * @param pId
	 * @param pComponents
	 * @param pIgnoreClass Classe que será ignorada quando encontrada
	 * @return
	 */
	public static UIComponent findComponent(String pId, List<UIComponent> pComponents, Class<?> pIgnoreClass){
		UIComponent xComponent = null;
		for (UIComponent xC : pComponents){
			if (pIgnoreClass == null ||
				!xC.getClass().equals(pIgnoreClass)){
				if (xC.getId() != null){
					if (xC.getId().toLowerCase().equals(pId.toLowerCase())){
						return xC;
					}
				}
				xComponent = findComponent(pId, xC.getChildren(), pIgnoreClass);
				if (xComponent != null){
					break;
				}
			}
		}
		return xComponent;
	}	
	
	
	/**
	 * Retorna o valor do parametro que foi definido dentro do componente utilizando <f:param>
	 * @param pParameterName
	 * @return
	 */
	public static String getComponentParameter(UIComponent pComponent, String pParameterName){
//		FacesContext.getCurrentInstance().getViewRoot().getCurrentComponent(FacesContext.getCurrentInstance()).getChildren().get(0);
//		UIComponent xInputText = FacesContext.getCurrentInstance().getViewRoot().getCurrentComponent(FacesContext.getCurrentInstance());
		String xValue = "";
		pParameterName = pParameterName.trim().toLowerCase();
		FacesContext.getCurrentInstance().getViewRoot();
		for (UIComponent xC: pComponent.getChildren()){ 
			if (xC instanceof UIParameter){
				UIParameter xP = (UIParameter) xC;
				if (xP.getName()!=null){
					if (xP.getName().trim().toLowerCase().equals(pParameterName)){
						if (xP.getValue()!=null){
							xValue = xP.getValue().toString();
						}else{
							xValue = "";
						}
						break;
					}
				}
			}
		}
		return xValue;
	}


	//===================================================
	@SuppressWarnings("unchecked")
	public static void showMapContentObject(Map<Object, Object> pMap, int pNivel){
	    for (Entry<Object, Object> xEntry : pMap.entrySet()) {
	        	System.out.println(DBSString.repeat(" ", pNivel) + 
	        			             "Key : " + xEntry.getKey() + 
	       		            	  " Value : " + xEntry.getValue());
        	if (xEntry.getClass().equals(Map.class)){
        		showMapContent((Map<String, Object>) xEntry,pNivel + 1);
        	}
	    }
	}
	
	@SuppressWarnings("unchecked")
	public static void showMapContent(Map<String, Object> pMap, int pNivel){
	    for (Entry<String, Object> xEntry : pMap.entrySet()) {
	        	System.out.println(DBSString.repeat(" ", pNivel) + 
	        			             "Key : " + xEntry.getKey() + 
	       		            	  " Value : " + xEntry.getValue());
        	if (xEntry.getClass().equals(Map.class)){
        		showMapContent((Map<String, Object>) xEntry,pNivel + 1);
        	}
	    }
	}
	
	@SuppressWarnings("unchecked")
	public static void showMapContentString(Map<String, String> pMap, int pNivel){
	    for (Entry<String, String> xEntry : pMap.entrySet()) {
	        	System.out.println(DBSString.repeat(" ", pNivel) + 
	        			             "Key : " + xEntry.getKey() + 
	       		            	  " Value : " + xEntry.getValue());
        	if (xEntry.getClass().equals(Map.class)){
        		showMapContent((Map<String, Object>) xEntry,pNivel + 1);
        	}
	    }
	}
	
	/**
	 * Converte id JSF para id JQuery
	 * @param pClientId
	 * @return
	 */
	public static String jsid(String pClientId){
		if (pClientId!=null){
			return pClientId.replaceAll(":", "\\\\\\\\:");
		}else{
			return null;
		}
	}
	
	/**
	 * Converte id JSF para id JQuery
	 * @param pClientId
	 * @return
	 */
	public static String convertToCSSId(String pClientId){
		if (pClientId!=null){
			return pClientId.replaceAll(":", "\\\\:");
		}else{
			return null;
		}
	}
	

	
	/**
	 * Algorithm works as follows;
	 * - If it's an input component, submitted value is checked first since it'd be the value to be used in case validation errors
	 * terminates jsf lifecycle
	 * - Finally the value of the component is retrieved from backing bean and if there's a converter, converted value is returned
	 * 
	 * - If the component is not a value holder, toString of component is used to support Facelets UIInstructions.
	 * 
	 * @param context			FacesContext instance
	 * @param component			UIComponent instance whose value will be returned
	 * @return					End text
	 */
	public static String getStringValueToRender(FacesContext facesContext, UIComponent component) {
		if(component instanceof ValueHolder) {
			
			if(component instanceof EditableValueHolder) {
				Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
				if (submittedValue != null) {
					return submittedValue.toString();
				}
			}

			ValueHolder valueHolder = (ValueHolder) component;
			Object value = valueHolder.getValue();
			if(value == null)
				return "";
			
			//first ask the converter
			if(valueHolder.getConverter() != null) {
				return valueHolder.getConverter().getAsString(facesContext, component, value);
			}
			//Try to guess
			else {
				ValueExpression expr = component.getValueExpression("value");
				if(expr != null) {
					Class<?> valueType = expr.getType(facesContext.getELContext());
					if(valueType != null) {
						Converter converterForType = facesContext.getApplication().createConverter(valueType);
					
						if(converterForType != null)
							return converterForType.getAsString(facesContext, component, value);
					}
				}
			}
			
			//No converter found just return the value as string
			return value.toString();
		} else {
			//This would get the plain texts on UIInstructions when using Facelets
			String value = component.toString();
			
			if(value != null)
				return value.trim();
			else
				return "";
		}
	}
	
	/**
	 * Resolves the end text to render by using a specified value
	 * 
	 * @param context			FacesContext instance
	 * @param component			UIComponent instance whose value will be returned
	 * @return					End text
	 */
	public static String getStringValueToRender(FacesContext facesContext, UIComponent component, Object value) {
		if(value == null)
			return null;
		
		ValueHolder valueHolder = (ValueHolder) component;
		
		Converter converter = valueHolder.getConverter();
		if(converter != null) {
			return converter.getAsString(facesContext, component, value);
		}
		else {
			ValueExpression expr = component.getValueExpression("value");
			if(expr != null) {
				Class<?> valueType = expr.getType(facesContext.getELContext());
				Converter converterForType = facesContext.getApplication().createConverter(valueType);
				
				if(converterForType != null)
					return converterForType.getAsString(facesContext, component, value);
			}
		}
		
		return value.toString();
	}
	

	
	/**
	 * Envia mensagem para a view
	 * @param pClientId Nome do componente ao aqual esta vinculado a mensagem
	 * @param pSeverity Tipo de severidade da mensagem
	 * @param pMessage texto da mensagem
	 */
	public static void sendMessage(String pClientId, FacesMessage.Severity pSeverity, String pMessage){
		if (pClientId == null
		 || pSeverity == null
		 || pMessage == null){return;}
		FacesContext.getCurrentInstance().addMessage( pClientId, new FacesMessage(pSeverity, pMessage, null));		
	}	

	/**
	 * Envia mensagem para a view
	 * @param pClientId Nome do componente ao aqual esta vinculado a mensagem
	 * @param pSeverity Tipo de severidade da mensagem
	 * @param pMessage texto da mensagem
	 */
	public static void sendMessage(String pClientId, MESSAGE_TYPE pMessageType, String pMessage){
		if (pClientId == null
		 || pMessageType == null
		 || pMessage == null){return;}
		FacesMessage.Severity xSeverity = null;
		if (pMessageType == MESSAGE_TYPE.SUCESS
		 || pMessageType == MESSAGE_TYPE.INFORMATION
		 || pMessageType == MESSAGE_TYPE.IMPORTANT){
			xSeverity = FacesMessage.SEVERITY_INFO;
		}else if (pMessageType == MESSAGE_TYPE.WARNING){
			xSeverity = FacesMessage.SEVERITY_WARN;
		}else if (pMessageType == MESSAGE_TYPE.ERROR){
			xSeverity = FacesMessage.SEVERITY_ERROR;
		}
		FacesContext.getCurrentInstance().addMessage( pClientId, new FacesMessage(xSeverity, pMessage, null));		
	}	

	/**
	 * Envia mensagem para a view
	 * @param pClientId Nome do componente ao aqual esta vinculado a mensagem
	 * @param pSeverity Tipo de severidade da mensagem
	 * @param pMessage texto da mensagem
	 */
	public static void sendMessage(String pClientId, DBSMessage pMessage){
		if (pClientId == null
		 || pMessage == null){return;}
		sendMessage(pClientId, pMessage.getMessageType(), pMessage.getMessageText());
	}	
	/**
	 * Exibe um dialog inserindo o arquivo informado, que deverá conter o componente DBSDialog, no componente dialog
	 * @param pDialogFileName
	 * @return
	 */
	public static boolean showDialogFile(String pDialogFileName){
		
		if (!pDialogFileName.equals("")){
			FacesContext xFC = FacesContext.getCurrentInstance();
			FaceletContext xFaceletContext = (FaceletContext) xFC.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
			UIComponent xDialog = xFC.getViewRoot().findComponent("dialog");
	        if (xDialog==null){
	        	System.out.println("componente <dbs:div id='dialog'/> não existe. É necessário que exista para o dialog ser exibido!");
	        }else{
	        	xDialog.getChildren().clear();
				HtmlPanelGroup xNew = (HtmlPanelGroup) xFC.getApplication().createComponent(HtmlPanelGroup.COMPONENT_TYPE);
				try {
					xDialog.getChildren().add(xNew);
		 			xFaceletContext.includeFacelet(xNew, pDialogFileName);
		 			return true;
				} catch (IOException e) {
					wLogger.error(e);
					return false;
		        }
			}
		}
		return false;
	}
	
	
	/**
	 * Retorna no nome da view corrente (exemplo.xhtml).
	 * @return
	 */
	public static final String getViewId(){
		try {
			if (FacesContext.getCurrentInstance() != null &&
				FacesContext.getCurrentInstance().getViewRoot() != null){
				String xViewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
				if (xViewId == null){
					return "";
				}
				return xViewId;
			}else{
				return "";
			}
		}catch(Exception e){
			wLogger.error("getViewId:", e);
			return "";
		}
	}

	/**
	 * Retorna página informada em <b>pPage</b> com <b>faces-redirect true ou false</b>.
	 * @param pPage
	 * @param pRedirect
	 * @return
	 */
	public static final String getViewRedirectString(String pPage, boolean pRedirect, boolean pIncludeParams){
		String xString = pPage;
		if (xString != null){
			if (pRedirect){
				xString += "?faces-redirect=true";
			}else{
				xString += "?faces-redirect=false";
			}
			if (pIncludeParams){
				xString += "&includeViewParams=true";
			}
			return xString;
		}else{
			return getViewId();
		}
	}

	/**
	 * Retorna página informada em <b>pPage</b> com <b>faces-redirect=true</b>.<br/>
	 * Isto força o carregamento integral da página.
	 * @param pPage
	 * @return
	 */
	public static final String getViewRedirectString(String pPage){
		return getViewRedirectString(pPage, true, false);
	}
	
	/**
	 * Retorna página corrente forçando o refresh(?faces-redirect=true).<br/>
	 * Isto força o carregamento integral da página corrente.
	 * @param pPage
	 * @return
	 */
	public static final String getCurrentViewRefresh(){
		return getViewRedirectString(getViewId(), true, false);
	}
	
	/**
	 * Retorna página corrente sem forçar o refresh<b>(?faces-redirect=false)</b>.<br/>
	 * Isto inibe o carregamento integral da página corrente. Artifício importante em chamadas Ajax.
	 * @param pPage
	 * @return
	 */
	public static final String getCurrentView(){
		return getViewRedirectString(getViewId(), false, false);
	}
	
	/**
	 * Retorna página corrente sem forçar o refresh<b>(?faces-redirect=false)</b> mas com os parametros definidos em <f:viewParam> caso existam.<br/>
	 * Isto inibe o carregamento integral da página corrente. Artifício importante em chamadas Ajax.
	 * @param pPage
	 * @return
	 */
	public static final String getCurrentViewWithParams(){
		return getViewRedirectString(getViewId(), false, true);
	}

	/**
	 * Redireciona para a URL indicada em <b>pUrl</b>.
	 * @param pLocalViewPath
	 * @throws DBSIOException
	 */
	public static final void redirectLocalUsingViewPath(String pLocalViewPath) throws DBSIOException{
		if (pLocalViewPath==null
		 || FacesContext.getCurrentInstance() == null){return;}
		try {
//			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
//			externalContext.redirect("http://stackoverflow.com");
			FacesContext.getCurrentInstance().getExternalContext().redirect(pLocalViewPath);
		} catch (IOException e) {
			DBSIO.throwIOException(e);
		}
	}

	/**
	 * Redireciona para página local, dentro da aplicação.<br/>
	 * Pode-se utilizar os nomes definidos no faces-config da aplicação.
	 * @param pOutcomeName
	 * @throws DBSIOException
	 */
	public static final void redirectLocalUsingOutcome(String pOutcomeName){
		FacesContext xContext = FacesContext.getCurrentInstance();
		NavigationHandler xNavigationHandler = xContext.getApplication().getNavigationHandler();
		//Configura a página que deverá ir. Neste caso é a página(ou outcome no faces-config) de login
		xNavigationHandler.handleNavigation(xContext, null, pOutcomeName);
		xContext.responseComplete();
	}
	
	
	/**
	 * Retorna somente a string do EL
	 * @param pAttributeName
	 * @return
	 */
	public static final String getELString(UIComponent pComponent, String pAttributeName){
		return getELString(pComponent, pAttributeName, false);	
	}
	
	/**
	 * Retorna somente a string do EL.
	 * @param pComponent
	 * @param pAttributeName
	 * @param pRemoveELSignals Indica se retornará string sem os caracteres #, $ e {}.
	 * @return
	 */
	public static final String getELString(UIComponent pComponent, String pAttributeName, boolean pRemoveELSignals){
		if (pAttributeName == null){
			return null;
		}
		ValueExpression xVE = pComponent.getValueExpression(pAttributeName); 
		String xVEString = "";
		if (xVE != null){
			xVEString = xVE.getExpressionString();
			if (pRemoveELSignals){
				xVEString = removeELSignals(xVEString);
			}
			return xVEString;
		}else{
			return null;
		}
	}
	
	/**
	 * Retorna string enviada no formato de EL(com os simbolos #{})  
	 * @param pAttributeName
	 * @return
	 */
	public static final String addELSignals(String pAttributeName){
		if (pAttributeName == null){
			return null;
		}
		return "#" + "{" + pAttributeName + "}";
	}
	
	/**
	 * Retorna a string sem os caracteres #, $ e {}.
	 * @param pELString
	 * @return
	 */
	public final static String removeELSignals(String pELString){
		pELString = DBSString.changeStr(pELString, "{", "");
		pELString = DBSString.changeStr(pELString, "}", "");
		pELString = DBSString.changeStr(pELString, "#", "");
		pELString = DBSString.changeStr(pELString, "$", "");
		return pELString;
	}
	
	/**
	 * Retorna texto com quebra<br/> de linha na proporção 4:3(aproximadamente);
	 * @param pMessageText
	 * @return
	 */
	public final static String getHtmlStringWithLineBreak(String pMessageText){
		if (pMessageText == null){
			return null;
		}
		if (!(pMessageText.length() > 0)){
			return "";
		}
		//Separa as palavras
		String xS[] = pMessageText.split("\\s+");
		//Não quebra linha caso a quantidade de palavras seja inferior a 4 ou a quantidade de caracteres seja inferior a 30.
		if (xS.length < 4
		|| pMessageText.length() < 40){
			return pMessageText.trim();
		}
		String xMessageText = "";
		Double xLarguraMax = (double) pMessageText.length();
		xLarguraMax = DBSNumber.exp(xLarguraMax, 0.70).doubleValue();
		xLarguraMax = DBSNumber.round(xLarguraMax, 0).doubleValue();
		int xI = 0;
		for (String xPalavra :xS){
			xI = xI + xPalavra.length() + 1;
			xMessageText = xMessageText + xPalavra +  " ";
			if (xI >= xLarguraMax){
				xMessageText = xMessageText.trim() + "<br/>";
				xI = 0;
			}
		}
		return xMessageText.trim();
	}
	
	/**
	 * Retorna o style com largura do campo em função da quantidade de caracteres de input
	 * @param pInputSize
	 * @return
	 */
	public final static String getStyleWidthFromInputSizeInternal(int pInputSize){
		if (pInputSize == 0){
			return "";
		}
		int xW = (pInputSize * 7);
		return "width:" + xW + "px;"; //min-height:19px; 
	}
	
	/**
	 * Retorna o style com largura do campo em função da quantidade de caracteres de input
	 * @param pInputSize
	 * @return
	 */
	public final static String getStyleWidthFromInputSize(int pInputSize){
		if (pInputSize == 0){
			return "";
		}
		int xW = (pInputSize * 7) + 5;
		return "width:" + xW + "px;"; //min-height:19px; 
	}

	/**
	 * Retorna o style com largura do campo em função da quantidade de caracteres de input
	 * @param pInputSize
	 * @return
	 */
	public final static String getStyleHeightFromInputSize(int pInputSize){
		if (pInputSize == 0){
			return "";
		}
		int xH = (pInputSize * 15) + 3;
		return "height:" + xH + "px; height:" + xH + "px; ";
	}
	
	
	/**
	 * Retorna o título padrão do cabeçalho em função do tipo do icone
	 * @param pMessageIcon
	 * @return
	 */
	public static String getCaptionFromIcon(DIALOG_ICON pMessageIcon){
		if (pMessageIcon.equals(DIALOG_ICON.ATENCAO)){
			return "Atenção";
		}else if (pMessageIcon.equals(DIALOG_ICON.CONFIRMAR)){
			return "Confirmar";
		}else if (pMessageIcon.equals(DIALOG_ICON.ERRO)){
			return "Erro";
		}else if (pMessageIcon.equals(DIALOG_ICON.IGNORAR)){
			return "Ignorar";
		}else if (pMessageIcon.equals(DIALOG_ICON.INFORMACAO)){
			return "Informação";
		}else if (pMessageIcon.equals(DIALOG_ICON.IMPORTANTE)){
			return "Importante";
		}else if (pMessageIcon.equals(DIALOG_ICON.PROIBIDO)){
			return "Atenção";
		}else if (pMessageIcon.equals(DIALOG_ICON.SOBRE)){
			return "Sobre";
		}else if (pMessageIcon.equals(DIALOG_ICON.SUCESSO)){
			return "Informação";
		}
		return null;
	}
	
	/**
	 * Retorna o título padrão do cabeçalho em função do tipo do icone
	 * @param pMessageIcon
	 * @return
	 */
	public static String getCaptionFromIcon(String pMessageIcon){
		return getCaptionFromIcon(toDIALOG_ICON(pMessageIcon));
	}
	
	/**
	 * Converte de String para DIALOG_ICON
	 * @param pMessageIcon
	 * @return
	 */
	public static DIALOG_ICON toDIALOG_ICON(String pMessageIcon){
    	for (DIALOG_ICON xDI : DIALOG_ICON.values()) {
    		if (xDI.toString().equals(pMessageIcon)){
    			return xDI;
    		}
    	}
    	return DIALOG_ICON.NENHUM;
	}
	
	/**
	 * Converte de MESSAGE_TYPE to DIALOG_ICON
	 * @param pMessageType
	 * @return
	 */
	public static DIALOG_ICON toDIALOG_ICON(MESSAGE_TYPE pMessageType){
		DIALOG_ICON xDialogIcon = DIALOG_ICON.INFORMACAO;
		if (pMessageType != null){
			if (pMessageType == MESSAGE_TYPE.ERROR){
				xDialogIcon = DIALOG_ICON.ERRO;
			}else if (pMessageType == MESSAGE_TYPE.INFORMATION){
				xDialogIcon = DIALOG_ICON.INFORMACAO;
			}else if (pMessageType == MESSAGE_TYPE.SUCESS){
				xDialogIcon = DIALOG_ICON.SUCESSO;
			}else if (pMessageType == MESSAGE_TYPE.IMPORTANT){
				xDialogIcon = DIALOG_ICON.IMPORTANTE;
			}else if (pMessageType == MESSAGE_TYPE.WARNING){
				xDialogIcon = DIALOG_ICON.ATENCAO;
			}
		}
		return xDialogIcon;
	}
	
	/**
	 * Calcula e retorna a largura da tela de mensagem
	 * @param pDialogMessageWidth
	 * @return
	 */
	public static Integer getDialogMessageWidth(Integer pMessageTextLenght){
		Integer xWidth = DBSNumber.add(DBSNumber.exp(DBSNumber.multiply(pMessageTextLenght, 10), 0.70), 150).intValue();
		return xWidth;
	}

	/**
	 * Calcula e retorna a altura da tela de mensagem
	 * @param pDialogMessageWidth
	 * @return
	 */
	public static Integer getDialogMessageHeight(Integer pDialogMessageWidth){
		Integer xHeight = DBSNumber.multiply(pDialogMessageWidth, 0.70).intValue();
		return xHeight;
	}
	
	//############################ COMPONENTS AUXS ENCODES ################################################
	/**
	 * Retorna string em JS com o submit
	 * @param pComponent
	 * @param pSourceEvent Evento a ser disparado
	 * @param pExecute
	 * @param pClientUpdate
	 * @return
	 */
	public static String getSubmitString(UIComponentBase pComponent, String pSourceEvent, String pExecute, String pClientUpdate){
		String xUserOnClick = (String) pComponent.getAttributes().get(pSourceEvent); 
		String xLocalOnClick = xUserOnClick;
		String xClientUpdate = pvRemoveFirstColon(pClientUpdate);
		
		//Ajax
		if (!DBSObject.isEmpty(xClientUpdate)){
			String xExecute = "";
			if (!DBSObject.isEmpty(pExecute)){
				xExecute = "execute:'" + pvRemoveFirstColon(pExecute) + "',";
			}
			xLocalOnClick = "jsf.ajax.request(this, event, {" + xExecute + " render:'" +  xClientUpdate +  "', onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror}); return false";
			
//			xExecute = "@this";
//			xLocalOnClick = "mojarra.ab(this,event,'click',0,'" + xClientUpdate + "');return false";
//			xLocalOnClick = "mojarra.ab(this,event,'action',0,'" + xClientUpdate + "',{'onevent':dbsfaces.onajax,'onerror':dbsfaces.onajaxerror});return false";

			if (xUserOnClick != null){
				xLocalOnClick = xLocalOnClick.replaceAll("'", "\\\\'");
				xUserOnClick = xUserOnClick.replaceAll("'", "\\\\'");
				xLocalOnClick = "jsf.util.chain(this,event,'" + xUserOnClick + "','" + xLocalOnClick + "');return false";
			}
		//Não Ajax
		}else{
			if (DBSObject.isEmpty(pExecute)){
//				System.out.println("Form/Execute não definido para o componente " + pComponent.getClientId()  + "!");
			}else if (DBSObject.isEmpty(xLocalOnClick)){
				StringBuilder xParam = new StringBuilder();
				xParam.append("'"+ pComponent.getClientId() + "':'"+ pComponent.getClientId() + "'");
				//Incorpora os parametros definidos via <f:param> dentro do componente
		    	if (pComponent.getChildCount() > 0){
		    		for (UIComponent xC: pComponent.getChildren()){
		    			if (xC instanceof UIParameter){
		    				UIParameter xP = (UIParameter) xC;
		    				xParam.append(",'"+ xP.getName() + "':'"+ xP.getValue() + "'");
		    			}
		    		}
		    	}
				xLocalOnClick = "mojarra.jsfcljs(document.getElementById('" + pExecute + "'),{"+ xParam.toString() + "},''); return false";
			}			
			//TODO
// menuitem deixa de funcionar com o código abaixo 			
//			if (DBSObject.isEmpty(pExecute)){
////				System.out.println("Form/Execute não definido para o componente " + pComponent.getClientId()  + "!");
//			}else if (DBSObject.isEmpty(xLocalOnClick)
//			  	  && !DBSObject.isEmpty(xUserOnClick)){ 
//				xLocalOnClick = "mojarra.jsfcljs(document.getElementById('" + pExecute + "'),{'"+ pComponent.getClientId() + "':'"+ pComponent.getClientId() + "'},''); return false";
//			}//Não faz nada caso não exista action, update e onclick
		}
		return xLocalOnClick;
	}
	
//	public static String getSubmitString(UIComponentBase pComponent, String pSourceEvent, String pExecute, String pClientUpdate){
//		String xUserOnClick = (String) pComponent.getAttributes().get(pSourceEvent);
//		String xLocalOnClick = xUserOnClick;
//		String xClientUpdate = pvRemoveFirstColon(pClientUpdate);
//		
//		//Ajax
//		if (!DBSObject.isEmpty(xClientUpdate)){
//			String xExecute = "";
//			if (!DBSObject.isEmpty(pExecute)){
//				xExecute = "execute:'" + pvRemoveFirstColon(pExecute) + "',";
//			}
//			xLocalOnClick = "jsf.ajax.request(this, event, {" + xExecute + " render:'" +  xClientUpdate +  "', onevent:dbsfaces.onajax}); return false;";
//		//Não Ajax
//		}else{
//			if (DBSObject.isEmpty(pExecute)){
//				System.out.println("Form/Execute não definido para o componente " + pComponent.getClientId()  + "!");
//			}else{
//				xLocalOnClick = "mojarra.jsfcljs(document.getElementById('" + pExecute + "'),{'"+ pComponent.getClientId() + "':'"+ pComponent.getClientId() + "'},'');return false;";
//			}
//		}
//		if (xUserOnClick != null){
//			xLocalOnClick = xLocalOnClick.replaceAll("'", "\\\\'");
//			xUserOnClick = xUserOnClick.replaceAll("'", "\\\\'");
//			xLocalOnClick = "jsf.util.chain(this,event,'" + xUserOnClick + "','" + xLocalOnClick + "');return false";
//		}
//		return xLocalOnClick;
//	}	
	
	public static void encodeStyleTagStart(ResponseWriter pWriter) throws IOException{
		pWriter.write("	<style type='text/css'> \n");
	}
	
	public static void encodeStyleTagEnd(ResponseWriter pWriter) throws IOException{
		pWriter.write(" </style> \n");
	}

	public static void encodeJavaScriptTagStart(ResponseWriter pWriter) throws IOException{
		pWriter.write("	<script type='text/javascript'> \n");
		//pWriter.write(" /* <![CDATA[ */ \n");
	}
	
	public static void encodeJavaScriptTagEnd(ResponseWriter pWriter) throws IOException{
		//pWriter.write(" /* ]]> */ ");
		pWriter.write(" </script> \n");
	}
	
	public static void encodeJavaScriptBeep(ResponseWriter pWriter) throws IOException{
		pWriter.write("dbsfaces.sound.beep();\n");
	}
	
	
	/**
	 * Gere HTML padrão do label a esquesta do campo, já incluindo o sinal ":"
	 * @param pContext
	 * @param pInput
	 * @param pWriter
	 * @throws IOException
	 */
	public static void encodeLabel(FacesContext pContext, DBSUIInput pInput, ResponseWriter pWriter, Boolean pRenderSeparator) throws IOException{
		if (pInput.getLabel()!=null){
			String xClientId = pInput.getClientId(pContext);
			String xStyle = "vertical-align:middle; display:inline-block;";
			if (!pInput.getLabelWidth().equals("")){
				xStyle += " width:" + pInput.getLabelWidth() + ";";
			}
			pWriter.startElement("label", pInput);
//				DBSFaces.setAttribute(pWriter, pInput, "id", xClientId + DBSFaces.CSS.MODIFIER.LABEL.trim(), null);
//				DBSFaces.setAttribute(pWriter, pInput, "name", xClientId + DBSFaces.CSS.MODIFIER.LABEL.trim(), null);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUT.LABEL + " " + DBSFaces.CSS.NOT_SELECTABLE , null);
				DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.MODIFIER.DATA.trim(), null);
				if (pRenderSeparator){
					DBSFaces.setAttribute(pWriter, "style",xStyle, null);
					pWriter.write(pInput.getLabel().trim() + ":");
				}else{
					xStyle += "padding-left:2px;";
					DBSFaces.setAttribute(pWriter, "style",xStyle, null);
					pWriter.write(pInput.getLabel().trim());
				}
			pWriter.endElement("label");
		}
	}
	
	/**
	 * Gere HTML padrão do label a esquesta do campo, já incluindo o sinal ":"
	 * @param pContext
	 * @param pInput
	 * @param pWriter
	 * @throws IOException
	 */
	public static void encodeLabel(FacesContext pContext, DBSUIInput pInput, ResponseWriter pWriter) throws IOException{
		encodeLabel(pContext, pInput, pWriter, true);
	}
	
	/**
	 * Gere HTML padrão do label a direita do campo
	 * @param pContext
	 * @param pInput
	 * @param pWriter
	 * @throws IOException
	 */
	public static void encodeRightLabel(FacesContext pContext, DBSUIInput pInput, ResponseWriter pWriter) throws IOException{
//		DBSFaces.pvEncodeLabel(pInput, xClientId, pInput.getLabel(), pInput.getLabelWidth(), pWriter);
		
		if (pInput.getRightLabel()!=null){
			String xClientId = pInput.getClientId(pContext);
			pWriter.startElement("label", pInput);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUT.LABEL + " " + DBSFaces.CSS.NOT_SELECTABLE , null);
				DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.MODIFIER.DATA.trim(), null);
				DBSFaces.setAttribute(pWriter, "style","margin:0 3px 0 3px; vertical-align: middle; display:inline-block;", null);
				pWriter.write(pInput.getRightLabel().trim());
			pWriter.endElement("label");
		}
	}
	/**
	 * Gera HTML para campos input como tag <span> para evitar qualquer alteração no cliente
	 * @param pComponent
	 * @param pWriter
	 * @param pClientId
	 * @param pStyle Style adicional
	 * @param pNormalWhiteSpace Indica se haverá quebra de linha caso o valor do campo seja ao comprimento a linha.<br/>
	 * true: quebra linha<br/>
	 * false: não quebra linha
	 * @param pValue Valor a ser exibido
	 * @throws IOException
	 */
	public static void encodeInputDataReadOnly(UIComponent pComponent, ResponseWriter pWriter, String pClientId, String pStyle, boolean pNormalWhiteSpace , String pValue) throws IOException{
		if (pNormalWhiteSpace){
			pStyle += "white-space: pre-wrap; overflow:auto;";
		}else{
			pStyle += "white-space: pre; overflow:hidden;";
		}
		pWriter.startElement("span", pComponent);
			DBSFaces.setAttribute(pWriter, "id", pClientId, null);
			DBSFaces.setAttribute(pWriter, "name", pClientId, null);
			DBSFaces.setAttribute(pWriter, "class", getInputDataClass(pComponent), null);
			DBSFaces.setAttribute(pWriter, "style", pStyle, null);
			if (pValue == null){
				pWriter.write(" ");
			}else{
				pWriter.write(pValue);
			}
		pWriter.endElement("span");
	}
	
	/**
	 * Retorna a classe utilizada nos inputs no campo na área que recebe os dados
	 * @param pInput
	 * @return
	 */
	public static String getInputDataClass(UIComponent pInput){
		String xClass = DBSFaces.CSS.INPUT.DATA;
		DBSUIInput xInput;
		if (pInput instanceof DBSUIInput){
			xInput = (DBSUIInput) pInput;
		}else{
			return xClass;
		}
		if (xInput.getReadOnly()){
			xClass += DBSFaces.CSS.MODIFIER.READONLY;
		}
		if (!xInput.isValid()){
			xClass += DBSFaces.CSS.MODIFIER.INVALID;
		}
		return xClass;
	}

	/**
	 * Cria o elemento que conterá o tooltip.
	 * @param pWriter
	 * @param pComponent
	 * @param pTooltip
	 * @throws IOException
	 */
	public static void encodeTooltip(FacesContext pContext, UIComponent pComponent, String pTooltip) throws IOException{
		encodeTooltip(pContext, pComponent, pTooltip, pComponent.getClientId());
	}
	
	/**
	 * Cria o elemento que conterá o tooltip.
	 * @param pWriter
	 * @param pComponent
	 * @param pTooltip
	 * @param pClienteId 
	 * @throws IOException
	 */
	public static void encodeTooltip(FacesContext pContext, UIComponent pComponent, String pTooltip, String pClientId) throws IOException{
		String 			xTooltipText = pTooltip;
		ResponseWriter 	xWriter = pContext.getResponseWriter();		
		UIComponent 	xTooltip = pComponent.getFacet("tooltip");
		
		//Encode do tooltip se houver um texto para o tooltip ou foi defindo via facet(name="tooltip") dentro do componente...
		if (!DBSObject.isEmpty(xTooltipText) ||
			xTooltip != null){
			xWriter.startElement("span", pComponent);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.TOOLTIP.trim() , null);
				xWriter.writeAttribute("style", "display:none;" , null);
				//Dá prioridade para o facet
				if (xTooltip != null){
					//Encode conteúdo do facet
					xTooltip.encodeAll(pContext);
				}else{
					//Encode texto
					xWriter.write(getHtmlStringWithLineBreak(xTooltipText));
				}
			xWriter.endElement("span");
			//Javascript 
			DBSFaces.encodeJavaScriptTagStart(xWriter);
			String xJS = "$(document).ready(function() { \n" +
					     " var xTooltip = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
					     " dbs_tooltip(xTooltip); \n" +
	                     "}); \n"; 
			xWriter.write(xJS);
			DBSFaces.encodeJavaScriptTagEnd(xWriter);		
		}
	}
	
	/**
	 * Encore da linha para gráfico SVG
	 * @param pComponent
	 * @param pWriter
	 * @param pStyleClass
	 * @param pStyle
	 * @param pX1
	 * @param pY1
	 * @param pX2
	 * @param pY2
	 * @throws IOException
	 */
	public static void encodeSVGLine(UIComponent pComponent, ResponseWriter pWriter, String pStyleClass, String pStyle, Integer pX1, Integer pY1, Integer pX2, Integer pY2) throws IOException{
		pWriter.startElement("line", pComponent);
			DBSFaces.setAttribute(pWriter, "class", pStyleClass, null);
			DBSFaces.setAttribute(pWriter, "style", pStyle, null);
			DBSFaces.setAttribute(pWriter, "x1", 	pX1, null);
			DBSFaces.setAttribute(pWriter, "y1", 	pY1, null);
			DBSFaces.setAttribute(pWriter, "x2", 	pX2, null);
			DBSFaces.setAttribute(pWriter, "y2", 	pY2, null);
		pWriter.endElement("line");
	}

	/**
	 * Encode de Retangulo para grádico SVG
	 * @param pComponent
	 * @param pWriter
	 * @param pStyleClass
	 * @param pStyle
	 * @param pX
	 * @param pY
	 * @throws IOException
	 */
	public static void encodeSVGRect(UIComponent pComponent, ResponseWriter pWriter, String pStyleClass, String pStyle, Integer pX, Integer pY, Integer pHeight, Integer pWidth, String pFill) throws IOException{
		pWriter.startElement("rect", pComponent);
			DBSFaces.setAttribute(pWriter, "class", pStyleClass, null);
			DBSFaces.setAttribute(pWriter, "style", pStyle, null);
			DBSFaces.setAttribute(pWriter, "x", 	pX, null);
			DBSFaces.setAttribute(pWriter, "y", 	pY, null);
			
			DBSFaces.setAttribute(pWriter, "height", pHeight, null);
			DBSFaces.setAttribute(pWriter, "width", pWidth, null);
			DBSFaces.setAttribute(pWriter, "fill",	pFill, null);			
		pWriter.endElement("rect");
	}
	
	/**
	 * Encode de Circulo/Elipse para grádico SVG
	 * @param pComponent
	 * @param pWriter
	 * @param pStyleClass
	 * @param pStyle
	 * @param pX
	 * @param pY
	 * @throws IOException
	 */
	public static void encodeSVGCircle(UIComponent pComponent, ResponseWriter pWriter, String pStyleClass, String pStyle, Integer pX, Integer pY, Integer pHeight, Integer pWidth, String pFill) throws IOException{
		pWriter.startElement("ellipse", pComponent);
			DBSFaces.setAttribute(pWriter, "class", pStyleClass, null);
			DBSFaces.setAttribute(pWriter, "style", pStyle, null);
			DBSFaces.setAttribute(pWriter, "cx", 	pX, null);
			DBSFaces.setAttribute(pWriter, "cy", 	pY, null);
			
			DBSFaces.setAttribute(pWriter, "rx", pWidth, null);
			DBSFaces.setAttribute(pWriter, "ry", pHeight, null);
			DBSFaces.setAttribute(pWriter, "fill",	pFill, null);			
		pWriter.endElement("ellipse");
	}
	
	
	/**
	 * Encode de Retangulo para grádico SVG
	 * @param pComponent
	 * @param pWriter
	 * @param pStyleClass
	 * @param pStyle
	 * @param pX
	 * @param pY
	 * @throws IOException
	 */
	public static void encodeSVGText(UIComponent pComponent, ResponseWriter pWriter, String pStyleClass, String pStyle, Integer pX, Integer pY, String pText) throws IOException{
		pWriter.startElement("text", pComponent);
			DBSFaces.setAttribute(pWriter, "class", pStyleClass, null);
			DBSFaces.setAttribute(pWriter, "style", pStyle, null);
			DBSFaces.setAttribute(pWriter, "x", 	pX, null);
			DBSFaces.setAttribute(pWriter, "y", 	pY, null);
			if (pText != null){
				pWriter.write(pText);
			}
		pWriter.endElement("text");
	}
	
	

	//UIComponent =========================================================================
	public static void handleAttribute(String pName, Object pValue, UIComponent pComponent) {
        @SuppressWarnings("unchecked")
		List<String> setAttributes = (List<String>) pComponent.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
        if (setAttributes == null) {
            String cname = pComponent.getClass().getName();
            if (cname != null && cname.startsWith(DBSFaces.OPTIMIZED_PACKAGE)) {
                setAttributes = new ArrayList<String>(6);
                pComponent.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
            }
        }
        if (setAttributes != null) {
            if (pValue == null) {
                ValueExpression ve = pComponent.getValueExpression(pName);
                if (ve == null) {
                    setAttributes.remove(pName);
                }
            } else if (!setAttributes.contains(pName)) {
                setAttributes.add(pName);
            }
        }
    }	
	
	/**
	 * Retorna ValueExpression a partir da string informada
	 * @param pELValue
	 * @param pValueTypeClass
	 * @return
	 */
	public static ValueExpression createValueExpression(String pELValue, Class<?> pValueTypeClass){
		return createValueExpression(null, pELValue, pValueTypeClass);
	}
	
	/**
	 * Retorna ValueExpression a partir da string informada
	 * @param pELValue
	 * @param pValueTypeClass
	 * @return
	 */
	public static ValueExpression createValueExpression(FacesContext pContext, String pELValue, Class<?> pValueTypeClass){
		FacesContext xContext = pContext;
		if (pContext == null){
			xContext = FacesContext.getCurrentInstance();
		}
		pELValue = DBSFaces.addELSignals(DBSFaces.removeELSignals(pELValue));
		return xContext.getApplication().getExpressionFactory().createValueExpression(xContext.getELContext(), pELValue, pValueTypeClass);
	}

	/**
	 * Retorna MethodExpression a partir da string informada
	 * @param pELMethod
	 * @param pReturnTypeClass
	 * @param pExpectedParamTypes
	 * @return
	 */
	public static MethodExpression createMethodExpression(String pELMethod, Class<?> pReturnTypeClass, Class<?>[] pExpectedParamTypes){
		return createMethodExpression(null, pELMethod, pReturnTypeClass, pExpectedParamTypes);
	}

	/**
	 * Retorna MethodExpression a partir da string informada
	 * @param pELMethod
	 * @param pReturnTypeClass
	 * @param pExpectedParamTypes
	 * @return
	 */
 	public static MethodExpression createMethodExpression(FacesContext pContext, String pELMethod, Class<?> pReturnTypeClass, Class<?>[] pExpectedParamTypes){
		FacesContext xContext = pContext;
		MethodExpression xME = null;
		if (xContext == null){
			xContext = FacesContext.getCurrentInstance();
			if (xContext == null){
				return null;
			}
		}
		pELMethod = DBSFaces.addELSignals(DBSFaces.removeELSignals(pELMethod));
		try{
			xME = xContext.getApplication().getExpressionFactory().createMethodExpression(pContext.getELContext(), pELMethod, pReturnTypeClass, pExpectedParamTypes);
			return xME;
		}catch(Exception e){
			wLogger.debug("MethodExpression não pode ser criado:" + pELMethod);
			return null;
		}
	}
 	//=====================================================================
 	// 
 	//=====================================================================
	/**
 	 * Retorna String com pagina renderizada.<br/>
 	 * Utilizar este método quando a view precisar consultar algum bean para recuperar os valores.<br/>
 	 * Caso contrário, utilize o outro método <b>getRenderedViewContent</b>.
 	 * @param pContext
 	 * @param pViewFile
 	 * @return
 	 */
 	public static String getRenderedViewContent(FacesContext pContext, String pViewFile) {
		try {
			// Salva o response writer
			ResponseWriter	xOriginalWriter = pContext.getResponseWriter();
 
			// put in a StringWriter to capture the output
			StringWriter 	xStringWriter = new StringWriter();
			ResponseWriter 	xWriter = pvCreateResponseWriter(pContext, xStringWriter);
			pContext.setResponseWriter(xWriter);
 
			// Cria a UIViewRoot 
			ViewHandler xViewHandler = pContext.getApplication().getViewHandler();
			UIViewRoot 	xView = xViewHandler.createView(pContext, pViewFile);
 
			// Encode da tela
			ViewDeclarationLanguage xVdl = xViewHandler.getViewDeclarationLanguage(pContext, pViewFile);
			xVdl.buildView(pContext, xView);
			//Encode do conteúdo
			renderChildren(pContext, xView);
 
			// Restaura o response writer
			pContext.setResponseWriter(xOriginalWriter);
 
			return xStringWriter.toString();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
 	
	/**
	 * Retorna String com resposta ao request a URL e parametros informados.
	 * Utilizar este método quando a view nao precisar consultar algum bean, mas receberá valores por parametro.<br/>
	 * Caso contrário, utilize o outro método.
	 * @param pURL
	 * @param pListParams
	 * @return
	 * @throws DBSIOException
	 */
	public static String getRenderedViewContent(String pURL, List<String> pListParams) throws DBSIOException {
		if (pURL == null){
			wLogger.error("URL não informada");
			return "";
		}
 		StringBuilder xResultado = new StringBuilder();
 		List<String>		xListProperty = pListParams;
		HttpURLConnection 	xConnection = null;
		BufferedReader 		xBuffer = null;
		
		try {
			URL 	xUrl = new URL(pURL);
			xConnection = (HttpURLConnection) xUrl.openConnection();
			xConnection.setRequestProperty("Request-Method", "POST");
			xConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			xConnection.setRequestProperty( "charset", ENCODE.ISO_8859_1);
			xConnection.setDoInput(true);
			xConnection.setDoOutput(true);
			xConnection.setUseCaches(false);
			//Adiciona parametros antes de efetuar a requisição da página
			if (!DBSObject.isNull(xListProperty) && !xListProperty.isEmpty()) {
				StringBuilder xParams = new StringBuilder();
				boolean first = true;
				for (String xProperty : xListProperty) {
					String xKey = DBSString.getSubString(xProperty, 1, xProperty.indexOf("="));
					String xValue = DBSString.getSubString(xProperty, xProperty.indexOf("=") + 2, xProperty.length());

			        if (first) {
			            first = false;
			        } else {
			            xParams.append("&");
			        }
			        xParams.append(URLEncoder.encode(xKey, ENCODE.ISO_8859_1)); //Conversão para UTF-8 gera problema no servidor cloud
			        xParams.append("=");
			        xParams.append(URLEncoder.encode(xValue, ENCODE.ISO_8859_1)); //Conversão para UTF-8 gera problema no servidor cloud
				}
				byte[] xParambytes = xParams.toString().getBytes();
				OutputStream xOs = xConnection.getOutputStream();
				xOs.write(xParambytes);
			}
			//Conecta à URL - Requisição
			xConnection.connect(); 
			xBuffer = new BufferedReader(new InputStreamReader(xConnection.getInputStream(), ENCODE.UTF_8));
			for (int xChar = xBuffer.read(); xChar != -1; xChar = xBuffer.read()){
	            xResultado.append((char)xChar);
			}
		} catch (FileNotFoundException e) {
			wLogger.error("Arquivo não achado:" + pURL);
			return "";
		} catch (IOException e) {
			DBSIO.throwIOException(e);
			return "";
		} finally {
			try {
				xBuffer.close();
			} catch (IOException e) {
				wLogger.error(e);
				DBSIO.throwIOException(e);
			}
			xConnection.disconnect();
		}
		return xResultado.toString();
 	}
 	
	/**
	 * Encode dos filhos 
	 * @param pFacesContext
	 * @param pComponent
	 * @throws IOException
	 */
	public static void renderChildren(FacesContext pFacesContext, UIComponent pComponent) throws IOException {
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
	
	
 	
 	//=======================================================================
	//DataTable 															=
	//=======================================================================
	/**
	 * Retorna a string para ser utilizada com styleclass de uma coluna do componente datatable.
	 * Cada coluna tem um styleClass com prefixo '-C'.
	 * @param pIndex
	 * @param pUserColumnClass
	 * @return
	 */
	public static String getDataTableDataColumnStyleClass(String pIndex, String pUserColumnClass){
		String xClass = "-C";
		if (!pIndex.equals("0")){
			xClass = xClass + pIndex;
		}
		if (pUserColumnClass!=null && !pUserColumnClass.equals("")){
			if (pUserColumnClass.indexOf(xClass) > -1){
				xClass = pUserColumnClass;
			}else{
				xClass = xClass + " " + pUserColumnClass;
			}
		}
		return xClass;
	}

	
	/**
	 * Cria coluna especial no DataTable contendo os respectivos controles
	 * @param pDataTable
	 */
	public static void createDataTableSpecialColumns(DBSDataTable pDataTable){
		FacesContext xContext = FacesContext.getCurrentInstance();	
		if (pDataTable.getHasViewOneAction() 
		 || pDataTable.getMultipleSelection() 
		 || !pDataTable.getKeyColumnName().equals("")){
			DBSDataTableColumn xC0 = (DBSDataTableColumn) DBSFaces.findComponent("C0", pDataTable.getFacetsAndChildren());
			if(xC0 == null){
				xC0 = (DBSDataTableColumn) xContext.getApplication().createComponent(DBSDataTableColumn.COMPONENT_TYPE);
				xC0.setId("C0");
				xC0.setStyleClass(DBSFaces.getDataTableDataColumnStyleClass("", ""));
				//Encode: botão que escondido chamará o action quando for dado um duploclick em alguma linha
				if (pDataTable.getHasViewOneAction()){
					DBSButton xBtn = (DBSButton) xContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
						xBtn.setId("btC0");
						xBtn.setStyleClass("-selectOne");
						xBtn.setActionExpression(DBSFaces.createMethodExpression(xContext, pDataTable.getViewOneAction(), String.class, new Class[0]));
						xBtn.setExecute("@this :dialog"); //:dialog necessário para fazer o submit dos campos dentro do form, em caso de crudtable dentro de crudform 
						xBtn.setUpdate(pDataTable.getUpdate()); 
					xC0.getChildren().add(xBtn);
				}
				//Encode: valor da chave que identifica a linha 
				if (!pDataTable.getKeyColumnName().equals("")){
					HtmlOutputText xKey = (HtmlOutputText) xContext.getApplication().createComponent(HtmlOutputText.COMPONENT_TYPE);
						xKey.setId("lbKey");
						xKey.setStyleClass(CSS.MODIFIER.KEY.trim()); 
						xKey.setTransient(true);
						xKey.setValueExpression("value", DBSFaces.createValueExpression(pDataTable.getVar() + "." + pDataTable.getKeyColumnName(), String.class));
					xC0.getChildren().add(xKey);
				}
				//Encode: Seleção multipla (habilitada também quando a edição é inline, para poder selecionar vários itens em caso de exclusão)
				if (pDataTable.getMultipleSelection()){
					//Troca styleClass para poder exibir a coluna
					xC0.setStyleClass(DBSFaces.CSS.MODIFIER.CHECKBOX);
					//Botão no cabeçalho para seleção de todas as linhas----------------------------------------
					DBSButton xBtn = (DBSButton) xContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
						xBtn.setId("btSelect");
						xBtn.setIconClass(" -i_checkbox_invert");
						xBtn.setActionExpression(DBSFaces.createMethodExpression(xContext, pDataTable.getSelectAllAction(), String.class, new Class[0]));
//						xBtn.setUpdate("@all"); //Configurado como @All pois não finalizava a chamada ajax corretamente
						xBtn.setUpdate(pDataTable.getClientId()); //09/09/2014 - Configurado como clienteId para evitar o update de toda tela causando problema nos grid que est
						xBtn.setExecute("@this"); 
					xC0.getFacets().put(DBSDataTable.FACET_HEADER, xBtn);
					
					//Checkbox em cada linha para seleção individual--------------------------------------------
					DBSCheckbox xCheckbox = (DBSCheckbox) xContext.getApplication().createComponent(DBSCheckbox.COMPONENT_TYPE);
						//xCheckbox.setTransient(false);
						xCheckbox.setId("fxSelect");
						xCheckbox.setValueExpression("value", DBSFaces.createValueExpression(xContext,  pDataTable.getSelected(), Boolean.class));
						AjaxBehavior xAjax = (AjaxBehavior) xContext.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
						xAjax.setRender(Arrays.asList("@this"));
						xAjax.setOnevent("dbsfaces.onajax"); //Chamada para disparar evento dbsoft de monitoramento do ajax
						xAjax.setOnerror("dbsfaces.onajaxerror");
					try{
						xCheckbox.addClientBehavior("change", xAjax); 
						xC0.getChildren().add(xCheckbox);
					}catch(javax.faces.FacesException e){
						DBSFaces.showViewRoot(xContext.getViewRoot().getChildren(), 0);
						wLogger.error(e);
					}
				}
				try{
					pDataTable.getChildren().add(0,xC0);
				}catch(javax.faces.FacesException e){
					DBSFaces.showViewRoot(xContext.getViewRoot().getChildren(), 0);
					wLogger.error(e);
				}
			}
		}
		
	}
	
	/**
	 * Cria botão 'Pesquisar'(como um facet) no dataTable 
	 * @param pDataTable
	 */
	public static UIComponent createDataTableBotaoPesquisar(DBSDataTable pDataTable){
		//Cria botão 'Pesquisar' caso existam campos para a seleção de filtro
		UIComponent 	xFacetPesquisar = pDataTable.getFacet(DBSDataTable.FACET_PESQUISAR);
		if (pDataTable.getFacet(DBSDataTable.FACET_FILTER)!=null){
			FacesContext 	xContext = FacesContext.getCurrentInstance();	
//			String 			xClientId = pDataTable.getClientId(xContext);
			if (xFacetPesquisar == null){
				DBSButton xBtPesquisar = (DBSButton) xContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
				xBtPesquisar.setId("btPesquisar");
				xBtPesquisar.setLabel("Pesquisar");
				xBtPesquisar.setIconClass(DBSFaces.CSS.ICON.trim() + " -i_find");
				if (pDataTable.getSearchAction() == null){
					wLogger.error(pDataTable.getClientId() +  ": searchAction não informado");
				}else{
					xBtPesquisar.setActionExpression(DBSFaces.createMethodExpression(xContext, pDataTable.getSearchAction(), String.class,new Class[0]));
				}
				/*
				 * Incluido em 9/9/2014.
				 * A principio resolve a questão do erro no ajax na seleção da linha por checkbox após o 'pesquisar'.
				 */
				xBtPesquisar.setUpdate(pDataTable.getClientId(xContext) + " " + pDataTable.getUpdate()); //Voltou a ser xClientId em 08/set/2014 - Ricardo	
//				xBtPesquisar.setUpdate("@all");//Substituido por @all pois dava erro no ajax na seleção da linha por checkbox após o 'pesquisar'
				
				//Cria o facet e inclui botão dentro dele. A inclusão do botão através do facet, evita problemas em incluir o botao na view várias vezes.
				pDataTable.getFacets().put(DBSDataTable.FACET_PESQUISAR, xBtPesquisar); 
				xFacetPesquisar = pDataTable.getFacet(DBSDataTable.FACET_PESQUISAR);
			}
		}
		return xFacetPesquisar;
	}

	/**
	 * Cria input somente para que possa ser posssível solicitar o update(render) ajax dele,
	 * que, posteriormente será redireciodado para o encode do toolbar.<br/>
	 * É um artifício para contornar a impossibilidade de solicitar o update de um facet, como
	 * é o caso do toolbar. 
	 * @param pDataTable
	 */
	public static void createDataTableToolbarFoo(DBSDataTable pDataTable){
		UIComponent xFacetToolbar = pDataTable.getFacet(DBSDataTable.FACET_TOOLBAR);
		if (xFacetToolbar == null || xFacetToolbar.getChildren().size() == 0){return;}
		UIComponent xFacetToolbarControl = pDataTable.getFacet(DBSDataTable.FACET_TOOLBAR_CONTROL);
		if (xFacetToolbarControl == null){
			FacesContext xContext = FacesContext.getCurrentInstance();	
	
			HtmlInputHidden xInputHidden = (HtmlInputHidden) xContext.getApplication().createComponent(HtmlInputHidden.COMPONENT_TYPE);
			xInputHidden.setId("toolbar");
			//Cria o facet e inclui botão dentro dele. A inclusão do botão através do facet, evita problemas em incluir o botao na view várias vezes.
			pDataTable.getFacets().put(DBSDataTable.FACET_TOOLBAR_CONTROL, xInputHidden); 
		}
	}

	/**
	 * Encode do cabeçalho contendo os filtros e botões do toolbar definidos pelo usuário.
	 * O encode do header da tabela é efetuado em outra rotina.
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	public static void encodeDataTableHeaderToolbar(DBSDataTable pDataTable) {
		UIComponent xToolbar = pDataTable.getFacet(DBSDataTable.FACET_TOOLBAR);
		FacesContext xFC = FacesContext.getCurrentInstance();
		ResponseWriter xWriter = xFC.getResponseWriter();

		// Toolbar -------------------------
		try {
			if (xToolbar != null) {
				xWriter.startElement("nav", pDataTable);
					DBSFaces.setAttribute(xWriter, "id", pDataTable.getClientId() + ":toolbar", null);
					DBSFaces.setAttribute(xWriter, "name", pDataTable.getClientId() + ":toolbar", null);
					xToolbar.encodeAll(xFC);
				xWriter.endElement("nav");
			}
		} catch (IOException e) {
			wLogger.error(e);
		}
	}
	
	public static void createFileUploadButtons(DBSFileUpload pFileUpload){
		DBSButton xButtonStart = (DBSButton) pFileUpload.getFacet("btStart");
		if (xButtonStart == null){
			xButtonStart = (DBSButton) FacesContext.getCurrentInstance().getApplication().createComponent(DBSButton.COMPONENT_TYPE);
			xButtonStart.setId("btStart");
			xButtonStart.setIconClass(DBSFaces.CSS.ICON + " -i_upload");
			xButtonStart.setReadOnly(pFileUpload.getReadOnly());
			xButtonStart.setExecute("");
			xButtonStart.setUpdate("");
			if (DBSObject.isEmpty(pFileUpload.getTooltip())) {
				xButtonStart.setTooltip("Upload de arquivo");
			} else {
				xButtonStart.setTooltip(pFileUpload.getTooltip());
			}
			if (DBSObject.isEmpty(pFileUpload.getFileUploadServletPath())){
				xButtonStart.setReadOnly(true);
			}
			//Adiciona como filho para gerar o id corretamente
			pFileUpload.getFacets().put("btStart", xButtonStart);
		}

		//cria botão CANCEL ----------------
		DBSButton xButtonCancel = (DBSButton) pFileUpload.getFacet("btCancel");
		if (xButtonCancel == null){ 
			xButtonCancel = (DBSButton) FacesContext.getCurrentInstance().getApplication().createComponent(DBSButton.COMPONENT_TYPE);
			xButtonCancel.setId("btCancel");
			xButtonCancel.setExecute("");
			xButtonCancel.setUpdate("");
			xButtonCancel.setIconClass(DBSFaces.CSS.ICON + " -i_media_stop");
			xButtonCancel.setStyle("display:none;");
			xButtonCancel.setReadOnly(pFileUpload.getReadOnly());
			xButtonCancel.setTooltip("Cancelar upload");
			if (DBSObject.isEmpty(pFileUpload.getFileUploadServletPath())){
				xButtonCancel.setReadOnly(true);
			}
			//Adiciona como filho para gerar o id corretamente
			pFileUpload.getFacets().put("btCancel", xButtonCancel);
		}
	}
	/**
	 * Retorna no nome do attributo(sem o nome do bean) a partir da EL do value do campo informado
	 * @param pInput
	 * @return
	 */
	public static String getAttibuteNameFromInputValueExpression(DBSUIInput pInput){
		String xColumnName = DBSFaces.getELString(pInput, "value", true);
		if (xColumnName!=null){
			Integer xI =  xColumnName.indexOf(".");
			if (xI > 0){
				xI = xI + 2;
				xColumnName = DBSString.getSubString(xColumnName, xI, xColumnName.length());
				return xColumnName;
			}
		}
		return "";
	}	
	  /**
	  * Converte o valor recebido para o mesmo tipo do 'value' definido no bean onde será atualizado o valor.<br/>
	  * Para valores númericos, converte o 'vázio' para nulo.<br/>
	  * obs:Em JSF não submit de valor nulo. O valor nulo é o 'vázio' por isso existe esta conversão.  
	  * @param pComponent
	  * @param pSubmittedValue
	  * @return
	  */
	 public static Object castSubmittedValue(DBSUIInput pComponent, Object pSubmittedValue){
	 	Object xSubmittedValue = pSubmittedValue;
	 	//Recupera qual o tipo de dado esperado para o 'value' conforme definido na propriedade do bean a que está vinculado o componente   
	 	//Se há valor
		if (pSubmittedValue != null){
		 	ValueExpression xVE =  pComponent.getValueExpression("value");
		 	if (xVE != null){
		 		try{
			 		Class<?> xValueClass = xVE.getType(FacesContext.getCurrentInstance().getELContext());	 	
					//Converte valor para nulo em determinados casos
					if(pSubmittedValue.equals("")){
						if (xValueClass.isAssignableFrom(Integer.class) ||
							xValueClass.isAssignableFrom(Double.class) ||
							xValueClass.isAssignableFrom(Float.class) ||
							xValueClass.isAssignableFrom(Date.class) ||
							xValueClass.isAssignableFrom(Timestamp.class) ||
							xValueClass.isAssignableFrom(BigDecimal.class)){
							//Seta valor diretamente como nulo caso o conteúdo recebido seja 'vázio'. Isso é necessário pois setar o submittedvalue como nulo, e a mesma coisa que ignorar o valor recebido. 
							xSubmittedValue =  null; //Seta como nulo para que o submittedvalue recebido seja ignorado, evitando o o setValue acima seja sobreposto.
							pComponent.setValue(null); 
						}
					}else{
						if (xValueClass.isAssignableFrom(Integer.class)){
							xSubmittedValue =  DBSNumber.toInteger(pSubmittedValue, null);
						}else if (xValueClass.isAssignableFrom(Double.class)){
							xSubmittedValue = DBSNumber.toDouble(pSubmittedValue, null);
						}else if (xValueClass.isAssignableFrom(BigDecimal.class)){
							xSubmittedValue = DBSNumber.toBigDecimal(pSubmittedValue, null);
						}else if (xValueClass.isAssignableFrom(Boolean.class)){
							xSubmittedValue = DBSBoolean.toBoolean(pSubmittedValue);
						}else if (xValueClass.isAssignableFrom(Date.class)){
							xSubmittedValue = DBSDate.toDate(pSubmittedValue);
						}else if (xValueClass.isAssignableFrom(Timestamp.class)){
							xSubmittedValue = DBSDate.toTimestamp(pSubmittedValue);
						}
					}
		 		}catch(Exception e){
		 			wLogger.error(e);
		 		}
			}
	 	}
	 	return xSubmittedValue;
	 }
	 
	 
	/**
	 * Exclui outros beans da sessão do usuário obrigando a serem reinicializados, para evitar que trabalhem com valores antigos
	 * @param pCrudBean
	 * @param pIsRecursive
	 */
	public static void finalizeCrudBeans(DBSCrudBean pCrudBean, boolean pIsRecursive){
		ExternalContext xEC = FacesContext.getCurrentInstance().getExternalContext();
	    for (Entry<String, Object> xEntry : xEC.getSessionMap().entrySet()) {
	        if((xEntry.getValue() instanceof SerializableContextualInstanceImpl)){
	            @SuppressWarnings("rawtypes")
				SerializableContextualInstanceImpl xImpl = (SerializableContextualInstanceImpl) xEntry.getValue();
	            Object xO = xImpl.getInstance();
	            if (xO instanceof DBSCrudBean){
	            	try {
	            		//Exclui da seção qualquer outro DBSCrudBean, para que seja reinializado caso seja chamado novamente.
	            		//Isso permite que os valores(principalmente os das listas no initilize) destes DBSCrudBean, sejam refeitos
	            		DBSCrudBean xCrud = (DBSCrudBean) xO;
	            		//1) Se quem chamou for nulo
	            		//2) Se é uma chamada recursiva e crudbean na memória for o mesmo testado
	            		//3) Se NÃO é uma chamada recursiva e não tiver pai e for o master e o crud em memória não tiver pai e não for o próprio que chamou esta rotina
	            		if (pCrudBean == null ||
	            			(pIsRecursive && xCrud.equals(pCrudBean)) ||
	            			(!pIsRecursive && pCrudBean.getParentCrudBean() == null && pCrudBean.getMasterBean() == null && xCrud.getParentCrudBean()==null && !xCrud.equals(pCrudBean))){
	            			for (DBSCrudBean xChild:xCrud.getChildrenCrudBean()){
	            				finalizeCrudBeans(xChild, true);
	            			}
	            			for (DBSBean xChild:xCrud.getSlavesBean()){
	            				if (xChild instanceof DBSCrudBean){
	            					finalizeCrudBeans((DBSCrudBean)xChild, true);
	            				}
	            			}
							xEC.getSessionMap().remove(xEntry.getKey());
							xCrud = null;
	            		}
					} catch (Throwable e) {
						wLogger.error("finalizeCrudBeans", e);
					}
	            }
	        }
	    }
	}
	
	/**
	 * SessionsBean dos Cruds e Repors foram substiuidos por conversation scope
	 * Exclui todos os DBSBean da sessão, menos o pDBSBean informado.
	 * @param pDBSBean
	 * @param pIsChild
	 */
	@Deprecated
	public static void finalizeDBSBeans(DBSBean pDBSBean, Boolean pIsChild){
		if (!pIsChild){ //Se não for chamada recursiva
			DBSCrudBean xDBSCrudBean = null;
			if (pDBSBean !=null ){
	    		if (pDBSBean instanceof DBSCrudBean){
	    			xDBSCrudBean = (DBSCrudBean) pDBSBean; 
	    		}
				if (pDBSBean.getMasterBean() != null || //Se for escravo
				    (xDBSCrudBean != null && //Se for filho
					 xDBSCrudBean.getParentCrudBean() !=null)){
					return; 
	    		}
			}
		}
		ExternalContext xEC = FacesContext.getCurrentInstance().getExternalContext();
	    for (Entry<String, Object> xEntry : xEC.getSessionMap().entrySet()) {
	        if((xEntry.getValue() instanceof SerializableContextualInstanceImpl)){
	            @SuppressWarnings("rawtypes")
				SerializableContextualInstanceImpl xImpl = (SerializableContextualInstanceImpl) xEntry.getValue();
	            Object xO = xImpl.getInstance();
	            if (xO instanceof DBSBean){
            		DBSBean 	xSessionBean = (DBSBean) xO;
            		DBSCrudBean xSessionCrudBean = null;
            		if (xSessionBean instanceof DBSCrudBean){
            			xSessionCrudBean = (DBSCrudBean) xSessionBean;
            		}
            		Boolean xFinaliza = true;
            		//Não finaliza de for o próprio bean ou algum filho ou escravo tiver o respectivo pai ou master como o prório bean 
            		if (!pIsChild){ //Se não for chamada recursiva, testa o próprio bean. Se for recursiva, finaliza o bean
            			if (xSessionBean.getMasterBean() != null || //Se for escravo
            			    (xSessionCrudBean != null && //Se for filho
            			     xSessionCrudBean.getParentCrudBean() !=null)){
            				xFinaliza = false; 
                		}
            		}else{
            			if (pDBSBean !=null && !xSessionBean.getClass().equals(pDBSBean.getClass())){
            				xFinaliza = false;
            			}
            		}
            		if (xFinaliza){
            			for (DBSBean xChild:xSessionBean.getSlavesBean()){
        					finalizeDBSBeans(xChild, true);
            			}
            			if (xSessionCrudBean != null){
                			for (DBSCrudBean xChild:xSessionCrudBean.getChildrenCrudBean()){
            					finalizeDBSBeans(xChild, true);
                			}
            			}
//	    				xEC.getSessionMap().remove(xEntry.getKey());
	    				xSessionBean = null;
	    				if (pIsChild){
	    					return;
	    				}
            		}
	            }
	        }
	    }
	}	
	
	/**
	 * Exclui todos os Session Beans
	 * @param pDBSBean
	 * @param pIsChild
	 */
	public static void finalizeSessionBean(Object pSessionBean, Boolean pIsChild){
		if (pSessionBean==null){
			return;
		}
		ExternalContext xEC = FacesContext.getCurrentInstance().getExternalContext();
	    for (Entry<String, Object> xEntry : xEC.getSessionMap().entrySet()) {
	        if((xEntry.getValue() instanceof SerializableContextualInstanceImpl)){
	            @SuppressWarnings("rawtypes")
				SerializableContextualInstanceImpl xImpl = (SerializableContextualInstanceImpl) xEntry.getValue();
	            
	            Object xSessionBean = xImpl.getInstance();
	            if (xSessionBean.equals(pSessionBean)){
		    		DBSCrudBean xDBSCrudBean = null;
		    		DBSBean xDBSBean = null;
	            	if (xSessionBean instanceof DBSBean){
		    			xDBSBean = (DBSBean) xSessionBean; 
            			for (DBSBean xChild:xDBSBean.getSlavesBean()){
    		    			finalizeSessionBean(xChild, true);
            			}
    	            	if (xSessionBean instanceof DBSCrudBean){
    		    			xDBSCrudBean = (DBSCrudBean) xSessionBean; 
                			for (DBSCrudBean xChild:xDBSCrudBean.getChildrenCrudBean()){
                				finalizeSessionBean(xChild, true);
                			}
    	            	}
		    		}
	            	xEC.getSessionMap().remove(xEntry.getKey());
	            	System.out.println("OK");
	            }
	        }
	    }
	}	

	
//	public static void finalizeDBSBeans(DBSBean pDBSBean, boolean pIsRecursive){
//		ExternalContext xEC = FacesContext.getCurrentInstance().getExternalContext();
//	    for (Entry<String, Object> xEntry : xEC.getSessionMap().entrySet()) {
//	        if((xEntry.getValue() instanceof SerializableContextualInstanceImpl)){
//	            @SuppressWarnings("rawtypes")
//				SerializableContextualInstanceImpl xImpl = (SerializableContextualInstanceImpl) xEntry.getValue();
//	            Object xO = xImpl.getInstance();
//	            if (xO instanceof DBSBean){
//            		DBSBean 	xSessionBean = (DBSBean) xO;
//            		DBSCrudBean xSessionCrudBean = null;
//            		DBSCrudBean xDBSCrudBean = null;
//            		if (xSessionBean instanceof DBSCrudBean){
//            			xSessionCrudBean = (DBSCrudBean) xSessionBean;
//            		}
//            		if (pDBSBean instanceof DBSCrudBean){
//            			xDBSCrudBean = (DBSCrudBean) pDBSBean; 
//            		}
//            		Boolean xFinaliza = true;
//            		//Não finaliza de for o próprio bean ou algum filho ou escravo tiver o respectivo pai ou master como o prório bean 
//            		if (!pIsRecursive){ //Se não for chamada recursiva, testa o próprio bean. Se for recursiva, finaliza o bean
//            			if (pDBSBean.equals(xSessionBean)){ //Se o bean da sessão é igual ao informado
//            				xFinaliza = false;
//            			}else if (xSessionBean.getMasterBean() != null){ //Se for escravo
//            				xFinaliza = false; 
//            			}else if (xSessionCrudBean != null &&
//            					  xSessionCrudBean.getParentCrudBean() != null){//Se for filho
//            				xFinaliza = false; 
//            			}else if (pDBSBean.getMasterBean() !=null && //Se o bean da sessão é igual ao master do informado(se houver)
//            					  pDBSBean.getMasterBean().equals(xSessionBean)){
//            				xFinaliza = false;
//            			}else if (xDBSCrudBean != null && //Se o bean da sessão é igual ao parent do informado(se houver)
//            					  xDBSCrudBean.getParentCrudBean() !=null &&
//          					  	  xDBSCrudBean.getParentCrudBean().equals(xSessionCrudBean)){
//            				xFinaliza = false; 
//                		}
//            		}
//            		if (xFinaliza){
//            			for (DBSBean xChild:pDBSBean.getSlavesBean()){
//        					finalizeDBSBeans(xChild, true);
//            			}
//            			if (xDBSCrudBean != null){
//                			for (DBSCrudBean xChild:xDBSCrudBean.getChildrenCrudBean()){
//            					finalizeDBSBeans(xChild, true);
//                			}
//            			}
//	    				xEC.getSessionMap().remove(xEntry.getKey());
//	    				xSessionBean = null;
//            		}
//	            }
//	        }
//	    }
//	}	
	
	/**
	 * Exclui outros bean da sessão, obrigando a serem reinicializados, evitando que trabalhem com valores antigos
	 * @param pReportBean
	 * @param pIsChild
	 */
	public static void finalizeReportBeans(DBSReportBean pReportBean){
		ExternalContext xEC = FacesContext.getCurrentInstance().getExternalContext();
	    for (Entry<String, Object> xEntry : xEC.getSessionMap().entrySet()) {
	        if((xEntry.getValue() instanceof SerializableContextualInstanceImpl)){
	            @SuppressWarnings("rawtypes")
				SerializableContextualInstanceImpl xImpl = (SerializableContextualInstanceImpl) xEntry.getValue();
	            Object xO = xImpl.getInstance();
	            if (xO instanceof DBSReportBean){
	            	try {
	            		//Exclui da seção qualquer outro DBSCrudBean, para que seja reinializado caso seja chamado novamente.
	            		//Isso permite que os valores(principalmente os das listas no initilize) destes DBSCrudBean, sejam refeitos
	            		DBSReportBean xCrud = (DBSReportBean) xO;
	            		if (!xCrud.equals(pReportBean)){
							xEC.getSessionMap().remove(xEntry.getKey());
							xCrud = null;
	            		}
					} catch (Throwable e) {
						wLogger.error("finalizeReportBeans", e);
					}
	            }
	        }
	    }
	}
	 
//	  /**
//	  * Converte o valor recebido para o mesmo tipo do 'value' definido no bean onde será atulizado o valor
//	  * @param pComponent
//	  * @param pSubmittedValue
//	  * @return
//	  */
//	 public static Object castSubmittedValue(DBSUIInput pComponent, Object pSubmittedValue){
//	 	Object xSubmittedValue = pSubmittedValue;
////		Object xKeyValueSaved = pComponent.getValue();
////	 	pComponent.getValueExpression("value").getType(FacesContext.getCurrentInstance().getELContext())	 	
//		Object xValue = pComponent.getValue();
//		if (xValue == null){ 
//			pComponent.setValue("0");
//			xValue = pComponent.getValue(); 
//		}
//		if (pSubmittedValue != null){
//			if(pSubmittedValue.equals("")){
//				if (xValue instanceof Integer){
//					//Seta valor diretamente como nulo caso o conteúdo recebido seja 'vázio'. Isso é necessário pois setar o submittedvalue como nulo, e a mesma coisa que ignorar o valor recebido. 
//					pComponent.setValue(null); 
//					xSubmittedValue =  null; //Seta como nulo para que o submittedvalue recebido seja ignorado, evitando o o setValue acima seja sobreposto.
//				}
//			}else{
//				if (xValue instanceof Integer){
//					xSubmittedValue =  DBSNumber.toInteger(pSubmittedValue);
//				}else if (xValue instanceof Boolean){
//					xSubmittedValue = DBSBoolean.toBoolean(pSubmittedValue);
//				}else if (xValue instanceof BigDecimal){
//					xSubmittedValue = DBSNumber.toBigDecimal(pSubmittedValue);
//				}
//			}
//		}
////		pComponent.setValue(xKeyValueSaved);
//	 	return xSubmittedValue;
//	 }
	
	//Private
	private static ResponseWriter pvCreateResponseWriter(FacesContext pContext, Writer pWriter) {
		ExternalContext 	xExtContext = pContext.getExternalContext();
		Map<String, Object> xRequestMap = xExtContext.getRequestMap();
		String 				xContentType = (String)xRequestMap.get("facelets.ContentType");
		String 				xEncoding = (String)xRequestMap.get("facelets.Encoding");
		RenderKit 			xRenderKit = pContext.getRenderKit();
		return xRenderKit.createResponseWriter(pWriter, xContentType, xEncoding);
	}

	// PRIVATE ===============================================================
	/**
	 * Caso exista, remove ':' do inicio da string dos ids(que soferão update via ajax ou execute), pois os mesmo não são necessário no comando.
	 * @param pUpdateString
	 * @return String sem os ":" iniciais
	 */
	private static String pvRemoveFirstColon(String pUpdateString){
		if (pUpdateString==null){
			return "";
		}
		pUpdateString = pUpdateString.trim();
		if (pUpdateString.equals("")){
			return "";
		}
		String xS = DBSString.changeStr(pUpdateString, " :", " ");
		if (xS.startsWith(":")){
			xS = DBSString.getSubString(xS, 2, xS.length());
		}
		return xS.trim();
	}

//	public static String findChildFormIds(UIComponent pComponent, String pFormsId) {
//		for (UIComponent xC: pComponent.getChildren()){
//			if (xC.getClass().equals(HtmlForm.class)){
//				pFormsId =  pFormsId; // + " " + xC.getClientId();
//			}else{
//				pFormsId = findChildFormIds(xC, pFormsId);
//			}
//		}
//		return pFormsId.trim();
//	}	
//	
	
//	private static UIComponent pvGetSubview(UIComponent pComponent){
//		//for (UIComponent xComponent: pComponent.getChildren()){
//		UIComponent xComponent = pComponent.getParent();
//		if (xComponent != null){
//			if (xComponent.getClass().equals(UINamingContainer.class)){
//				return xComponent;
//			}else{
//				return pvGetSubview(xComponent);
//			}
//		}
//		return null;
//	}



}
