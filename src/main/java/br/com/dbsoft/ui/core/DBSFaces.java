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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIOutcomeTarget;
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
import javax.faces.event.ActionListener;
import javax.faces.flow.FlowHandler;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.render.RenderKit;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.facelets.FaceletContext;

import org.apache.log4j.Logger;
import org.jboss.weld.context.SerializableContextualInstanceImpl;

import com.sun.faces.flow.FlowHandlerImpl;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer.Param;
import com.sun.faces.util.DebugUtil;
import com.sun.faces.util.Util;

import br.com.dbsoft.core.DBSSDK;
import br.com.dbsoft.core.DBSSDK.ENCODE;
import br.com.dbsoft.core.DBSSDK.NETWORK.METHOD;
import br.com.dbsoft.core.DBSSDK.SYS.WEB_CLIENT;
import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessageBase.MESSAGE_TYPE;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.bean.DBSBean;
import br.com.dbsoft.ui.bean.DBSBeanModalMessages;
import br.com.dbsoft.ui.bean.crud.DBSCrudOldBean;
import br.com.dbsoft.ui.bean.report.DBSReportBean;
import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.checkbox.DBSCheckbox;
import br.com.dbsoft.ui.component.datatable.DBSDataTable;
import br.com.dbsoft.ui.component.datatable.DBSDataTable.SELECTION_TYPE;
import br.com.dbsoft.ui.component.datatable.DBSDataTableColumn;
import br.com.dbsoft.ui.component.fileupload.DBSFileUpload;
import br.com.dbsoft.ui.component.inputnumber.DBSInputNumber;
import br.com.dbsoft.util.DBSBoolean;
import br.com.dbsoft.util.DBSColor;
import br.com.dbsoft.util.DBSDate;
import br.com.dbsoft.util.DBSFile;
import br.com.dbsoft.util.DBSIO;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;
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
    
	protected static final Param[] EMPTY_PARAMS = new Param[0];
	
    
    @SuppressWarnings("deprecation")
	public static final char ID_SEPARATOR = NamingContainer.SEPARATOR_CHAR; //UINamingContainer.getSeparatorChar(FacesContext.getCurrentInstance());
	
    public static class FACESCONTEXT_ATTRIBUTE{
    	/**
    	 * DBSMensagen enviadas por dbsfaces.sendMessage.
    	 */
    	public static final String MESSAGES = "DBSMESSAGES"; 
    	/**
    	 * DBSMensagen enviadas por dbsfaces.sendMessage.
    	 */
    	public static final String MESSAGES_LISTENERS = "DBSMESSAGES_LISTENERS"; 
    	/**
    	 * View antes executar um action.
    	 */
//    	public static final String PREVIOUS_VIEW = "DBSPREVIOUS_VIEW";
    	/**
    	 * Componente que originou a ação.
    	 */
    	public static final String ACTION_SOURCE = "DBSACTIONSOURCE";
    	/**
    	 * Indicador que método do bean chamado pelo action é controlado com actionController
    	 */
    	public static final String ACTION_CONTROLLED = "DBSACTIONCONTROLLED";
//    	public static final String ACTION_MESSAGEKEY = "DBSACTIONMESSAGEKEY";
    }
    
	public static class ID
	{
	    public static final String BUTTON = "button";
	    public static final String DATATABLE = "dataTable";
	    public static final String DATATABLECOLUMN = "dataTableColumn";
	    public static final String CRUDMODAL = "crudModal";
	    public static final String CRUDVIEW = "crudView";
	    public static final String CRUDDIALOG = "crudDialog";
	    public static final String CRUDTABLE = "crudTable";
	    public static final String COMMANDHASMESSAGE = "commandhasmessage";
	    public static final String DIALOG = "dialog";
	    public static final String DIALOGNAV = "dialognav";
	    public static final String DIALOGMSG = "dialogmsg";
	    public static final String DIALOGMOD = "dialogmod";
	    public static final String DIALOGBTN = "dialogbtn";
	    public static final String DIALOGCONTENT = "dialogcontent";
	    public static final String MODAL = "modal";
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
	    public static final String DIVNC = "divnc";
	    public static final String NAV = "nav";
	    public static final String NAVMESSAGE = "navmessage";
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
	    public static final String SLIDER = "slider";
	    public static final String STYLE = "style";
	    public static final String COMPONENTTREE = "componenttree";
	    public static final String TABLE = "table";
	    public static final String TOOLTIP = "tooltip";
	    public static final String UL = "ul";
	    public static final String LI = "li";
	    public static final String FORM = "form";
	    public static final String REPORT = "report"; 
	    public static final String REPORTFORM = "reportForm"; 
	    public static final String MODALMESSAGES = "modalMessages";
	    public static final String MODALCRUDMESSAGES = "modalCrudMessages";
	    public static final String BEANDIALOGMESSAGES = "beanDialogMessages";
	    public static final String BEANDIALOGCRUDCRUDMESSAGES = "beanDialogCrudMessages";
	    public static final String BEANNAVMESSAGES = "beanNavMessages";
	    public static final String BEANNAVCRUDMESSAGES = "beanNavCrudMessages";
	    public static final String MESSAGES = "messages";
	    public static final String GROUP = "group";
	    public static final String PUSH = "push";
	    public static final String MESSAGELIST = "messageList";
	    public static final String QUICKINFO = "quickInfo";
	    public static final String CHARTS = "charts";
	    public static final String CHART = "chart";
	    public static final String CHARTVALUE = "chartValue";
	    public static final String CHARTSX = "chartsX";
	    public static final String CHARTX = "chartX";
	    public static final String CHARTVALUEX = "chartValueX";
	    public static final String PAGEDSEARCH = "pagedSearch";
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
	
	/**
	 * COM EXCEÇÃO DO CLASS_PREFIX
	 * TODOS CSS DEVEM INICIAR E TERMINAR COM ESPAÇO
	 */
	public static class CSS
	{
		public static final String NOT_SELECTABLE =  " -not_selectable ";
		public static final String CLASS_PREFIX = "dbs_";
		public static final String WINDOW_CENTER = " " + CLASS_PREFIX +  "window_center "; 
		public static final String CHILD_CENTER =  " " + CLASS_PREFIX +  "child_center ";
		public static final String BACK_TEXTURE_BLACK =  " " + CLASS_PREFIX +  "back_texture_black "; 
		public static final String BACK_TEXTURE_BLACK_TRANSPARENT_GRADIENT =  " " + CLASS_PREFIX +  "back_texture_black_transparent_gradient ";
		public static final String BACK_TEXTURE_BLACK_GRADIENT =  " " + CLASS_PREFIX +  "back_texture_black_gradient ";
		public static final String BACK_TEXTURE_WHITE_GRADIENT =  " " + CLASS_PREFIX +  "back_texture_white_gradient "; 
		public static final String BACK_TEXTURE_WHITE_TRANSPARENT =  " " + CLASS_PREFIX +  "back_texture_white_transparent";
		public static final String BACK_TEXTURE_WHITE_TRANSPARENT_GRADIENT =  " " + CLASS_PREFIX +  "back_texture_white_transparent_gradient ";
		public static final String BACK_GRADIENT_WHITE =  " " + CLASS_PREFIX +  "back_gradient_white ";
		public static final String PARENT_FILL =  " " + CLASS_PREFIX +  "parent_fill ";
		public static final String HORIZONTAL_LINE =  " " + CLASS_PREFIX +  "horizontal_line ";
		public static final String HORIZONTAL_LINE_WHITE =  " " + CLASS_PREFIX +  "horizontal_line_white ";
		public static final String VERTICAL_LINE =  " " + CLASS_PREFIX +  "vertical_line ";
		
		public static class THEME
		{
			public static final String BC = " -th_bc ";
			public static final String FC = " -th_fc ";
			public static final String ACTION = " -th_action ";
			public static final String INVERT = " -th_i ";
			public static final String INPUT = " -th_input ";
			public static final String INPUT_LABEL = " -th_input-label ";
			public static final String INPUT_DATA = " -th_input-data ";
			public static final String HORIZONTAL_LINE = " -th_lineHorizontal ";
			public static final String HORIZONTAL_LINE_BEFORE = " -th_lineHorizontalBefore ";
			public static final String HORIZONTAL_LINE_AFTER = " -th_lineHorizontalAfter ";
			public static final String VERTICAL_LINE = " -th_lineVertical ";
			public static final String VERTICAL_LINE_BEFORE = " -th_lineVerticalBefore ";
			public static final String VERTICAL_LINE_AFTER = " -th_lineVerticalAfter ";
			public static final String FLEX = " -th_flex ";
			public static final String FLEX_COL = " -th_col ";
			public static final String BUTTONS_STRIP = " -th_buttons_strip ";
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
			public static final String SUB_CONTAINER = " -sub_container ";
			public static final String SUB_CONTENT = " -sub_content ";
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
			public static final String INFO = " -info ";
			public static final String INVERT = " -i ";
			public static final String KEY = " -key ";
			public static final String LABEL = " -label ";
			public static final String LAST = " -last ";
			public static final String LARGE = " -large ";
			public static final String LEFT = " -left ";
			public static final String CENTER = " -center ";
			public static final String CENTRALIZED_ABS = " -centralized_abs ";
			public static final String CENTRALIZED_REL = " -centralized_rel ";
			public static final String ABS = " -abs ";
			public static final String REL = " -rel ";
			public static final String RIGHT = " -right ";
			public static final String LINE = " -line ";
			public static final String LOADING = " -loading ";
			public static final String MASK = " -mask ";
			public static final String MESSAGE = " -message ";
			public static final String NORMAL = " -normal ";
			public static final String ON = " -on ";
			public static final String OFF = " -off ";
			public static final String POINT = " -point ";
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
			public static final String PROGRESS_TIMEOUT = "-progress_timeout";
		}

		public static class DATATABLE
		{
			public static final String MAIN = " " + CLASS_PREFIX +  ID.DATATABLE + " ";
		}

		public static class DATATABLECOLUMN
		{
			public static final String MAIN = " " + CLASS_PREFIX +  ID.DATATABLECOLUMN + " ";
		}
		
		public static class LABEL
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.LABEL + " ";
		}
		
		public static class UL
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.UL + " ";
		}

		public static class LI
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.LI + " ";
		}
	
		public static class DIALOG
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.DIALOG + " ";
		}

		public static class MODAL
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.MODAL + " ";
		}

		public static class FILEUPLOAD
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.FILEUPLOAD + " ";
		}

		public static class INPUT
		{
			public static final String SUBMIT = " " + CSS.THEME.INPUT.trim() + MODIFIER.SUBMIT.trim() + " ";
			public static final String SUGGESTION = " " + CSS.THEME.INPUT.trim() + MODIFIER.SUGGESTION.trim() + " ";
			public static final String SUGGESTIONKEY = " " + SUGGESTION.trim() + MODIFIER.KEY.trim() + " ";
		}
		
		public static class INPUTTEXT
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.INPUTTEXT + " ";
		}

		public static class INPUTTEXTAREA
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.INPUTTEXTAREA + " ";
		}

		public static class INPUTDATE
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.INPUTDATE + " ";
			public static final String DAY = " -day ";
			public static final String MONTH = " -month ";
			public static final String YEAR = " -year";
			public static final String HOUR = " -hour ";
			public static final String MINUTE = " -minute ";
			public static final String SECOND = " -second ";
		}
		
		public static class INPUTPHONE
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.INPUTPHONE + " ";
			public static final String DDI = " -ddi ";
			public static final String DDD = " -ddd ";
			public static final String NUMBER = " -number ";
		}

		public static class INPUTNUMBER
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.INPUTNUMBER + " ";
		}
		public static class INPUTMASK
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.INPUTMASK + " ";
		}
		
		public static class IMG
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.IMG + " ";
		}

		public static class CHECKBOX
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.CHECKBOX + " ";
		}
		public static class LISTBOX
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.LISTBOX + " ";
		}
		public static class COMBOBOX
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.COMBOBOX + " ";
		}

		public static class BUTTON
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.BUTTON + " ";
		}
		public static class DIV
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.DIV + " ";
		}
		public static class NAV
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.NAV + " ";
		}
		public static class NAVMESSAGE
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.NAVMESSAGE + " ";
		}
		
		public static class MENU
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.MENU + " ";
		}

		public static class MENUITEM
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.MENUITEM + " ";
		}
		
		public static class MENUITEMSEPARATOR
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.MENUITEMSEPARATOR + " ";
		}
		
		public static class COMPONENTTREE
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.COMPONENTTREE + " ";
		}
		
		
		public static class LINK
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.LINK + " ";
		}
		
		public static class LOADING
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.LOADING + " ";
		}
		
		public static class TAB
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.TAB + " ";
			public static final String CAPTION = " " + CLASS_PREFIX + ID.TAB.trim() + "Caption ";
		}

		public static class TABPAGE
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.TABPAGE + " ";
		}


		public static class ACCORDION
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.ACCORDION + " ";
			public static final String SECTION = " " + MAIN.trim() + "_section ";
			public static final String SECTION_CAPTION = " " + SECTION.trim() + MODIFIER.CAPTION.trim() + " ";
			public static final String SECTION_CONTAINER = " " + SECTION.trim() + MODIFIER.CONTAINER.trim() + " ";
		}

		public static class CALENDAR
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.CALENDAR + " ";
			public static final String DAYS = " " + MAIN.trim() + "_days ";
			public static final String MONTH = " " + MAIN.trim() +  "_month ";
			public static final String YEAR = " " + MAIN.trim() +  "_year ";
		}

		public static class RADIO
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.RADIO + " ";
		}

		public static class PROGRESS
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.PROGRESS + " ";
		}

		public static class SLIDER
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.SLIDER + " ";
		}
		
		public static class TOOLTIP
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.TOOLTIP + " ";
		}
		
		public static class REPORT
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.REPORT + " ";
		}

		public static class REPORTFORM
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.REPORTFORM + " ";
		}

		public static class MESSAGES
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.MESSAGES + " ";
		}
		
		public static class GROUP
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.GROUP + " ";
		}

		public static class PUSH
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.PUSH + " ";
		}

		public static class MESSAGELIST
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.MESSAGELIST + " ";
		}

		public static class QUICKINFO
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.QUICKINFO + " ";
		}

		public static class CHARTS
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.CHARTS + " ";
		}

		public static class CHART
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.CHART + " ";
		}
		
		public static class CHARTVALUE
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.CHARTVALUE + " ";
		}

		public static class CHARTSX
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.CHARTSX + " ";
		}

		public static class CHARTX
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.CHARTX + " ";
		}
		
		public static class CHARTVALUEX
		{	
			public static final String MAIN = " " + CLASS_PREFIX + ID.CHARTVALUEX + " ";
		}
		
		public static class PAGEDSEARCH
		{
			public static final String MAIN = " " + CLASS_PREFIX + ID.PAGEDSEARCH + " ";
			
			public static final String R1 = " -R1";
			public static final String R2 = " -R2";
			public static final String INPUTVALOR = " -input_valor";
			public static final String INPUTSEARCH = " -input_search";
			public static final String INPUTSUGGESTION = " -input_suggestion";
			public static final String BT_NEW_SEARCH = " -bt_new_search";
			public static final String BT_SEARCH_MORE = " -bt_search_more";
			public static final String VISIBLE_CONTAINER = " -visible_container";
			public static final String VISIBLE_LIST = " dbs_pagedSearch_container";
			public static final String INVISIBLE_CONTAINER = " -invisible_container";
			public static final String INVISIBLE_LIST = " -invisible_list";
			public static final String PAGED_ITEM = "-item";
			public static final String PAGED_ITEM_KEY = "-item_key";
			public static final String PAGED_ITEM_DISPLAY = "-item_display_value";
			public static final String LOADING = "-loading -small -hideLoading";
			public static final String SELECT_ROW = "-select_row";
			public static final String BT_SELECT = "-bt_select_item";



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
		return getFirstParent(pComponent, UIForm.class);
//		RenderKitUtils.getFormClientId(pComponent, pContext); //Anternativa ao código acima. Não testei. Ricardo
	}	

	/**
	 * Retorna o primeiro Parent que encontrar que seja da class informada.
	 * @param pComponent
	 * @param pClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFirstParent(UIComponent pComponent, Class<T> pClass) {
		UIComponent xParent = pComponent.getParent();
		while (xParent != null) {
			if (xParent.getClass().isAssignableFrom(pClass)) {
				return (T) xParent;
            }
			xParent = xParent.getParent();
		}
		return null;
	}	
	
	/**
	 * Retorna o Parent do primeiro Child que encontrar que seja da class informada.
	 * @param pComponent
	 * @param pClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getParentFirstChild(UIComponent pComponent, Class<T> pClass) {
		for (UIComponent xC : pComponent.getChildren()){
			if (xC.getClass().isAssignableFrom(pClass)) {
				return (T) xC.getParent();
            }
			return getParentFirstChild(xC, pClass);
		}
		return null;
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
	 * Exibe um dialog inserindo o arquivo informado, que deverá conter o componente DBSDialog, no componente dialog
	 * @param pModalFileName
	 * @return
	 */
	public static boolean showDialogFile(String pModalFileName){
		
		if (!pModalFileName.equals("")){
			FacesContext xFC = FacesContext.getCurrentInstance();
			FaceletContext xFaceletContext = (FaceletContext) xFC.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
			UIComponent xModal = xFC.getViewRoot().findComponent("modal");
	        if (xModal==null){
	        	System.out.println("componente <dbs:div id='modal'/> não existe. É necessário que exista para o componente 'Modal' ser exibido!");
	        }else{
	        	xModal.getChildren().clear();
				HtmlPanelGroup xNew = (HtmlPanelGroup) xFC.getApplication().createComponent(HtmlPanelGroup.COMPONENT_TYPE);
				try {
					xModal.getChildren().add(xNew);
		 			xFaceletContext.includeFacelet(xNew, pModalFileName);
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
		return getViewId(FacesContext.getCurrentInstance());
	}

	/**
	 * Retorna no nome da view corrente (exemplo.xhtml).
	 * @return
	 */
	public static final String getViewId(FacesContext pContext){
		try {
			if (pContext != null &&
				pContext.getViewRoot() != null){
				String xViewId = pContext.getViewRoot().getViewId();
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
		return "#" + "{" + pAttributeName + "}"; //Artifícil de separar tralha de colchete é necessário para evitar alrte no código
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
		pMessageText = DBSString.changeStr(pMessageText, "<br/>", " ", false);
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
	 * Retorna comando CSS com todos os prefixos dos browsers
	 * @param pAtribute
	 * @param pValue
	 * @return
	 */
	public static String getCSSAllBrowser(String pAtribute, String pValue){
		if (pAtribute==null
		 || pValue==null){return "";}
		StringBuilder xSB = new StringBuilder();
		for (WEB_CLIENT xClient: WEB_CLIENT.values()){
			xSB.append(xClient.getCSSPrefix());
			xSB.append(pAtribute.trim());
			xSB.append(": ");
			xSB.append(pValue.trim());
			xSB.append(";");
		}
		xSB.append(pAtribute.trim());
		xSB.append(": ");
		xSB.append(pValue.trim());
		xSB.append(";");
		return xSB.toString();
	}
	
	/**
	 * Retorna o style com largura do campo em função da quantidade de caracteres de input
	 * @param pInputSize
	 * @return
	 */
	public final static String getCSSStyleWidthFromInputSize(int pInputSize){
		if (pInputSize == 0){
			return "";
		}
		Double xW = DBSNumber.round((pInputSize + 1) * 0.662, 3);
		return "width:" + xW + "em;"; 
	}

	/**
	 * Retorna o style com largura do campo em função da quantidade de caracteres de input
	 * @param pInputSize
	 * @return
	 */
	public final static String getCSSStyleHeightFromInputSize(int pInputSize){
		if (pInputSize == 0){
			return "";
		}
		//Força altura somente quando for mais de uma linha
		if (pInputSize <= 1){
			return "";
		}
		Double xH = DBSNumber.trunc(pInputSize * 1.28, 4);
		return "height:" + xH + "em;";
	}
	
	
	/**
	 * Calcula e retorna a largura da tela de mensagem
	 * @param pMessageTextLenght
	 * @return
	 */
	public static Integer getModalMessageWidth(Integer pMessageTextLenght){
		Integer xWidth = DBSNumber.add(DBSNumber.exp(DBSNumber.multiply(pMessageTextLenght, 10), 0.70), 150).intValue();
		return xWidth;
	}

	/**
	 * Calcula e retorna a altura da tela de mensagem
	 * @param pMessageTextWidth
	 * @return
	 */
	public static Integer getModalMessageHeight(Integer pMessageTextWidth){
		Integer xHeight = DBSNumber.multiply(pMessageTextWidth, 0.70).intValue();
		return xHeight;
	}
	
	/**
	 * Retorna valor do campo enviado no request.
	 * @param pContext
	 * @param pComponenteClientId
	 * @return
	 */
	public static Object getDecodedComponenteValue(FacesContext pContext, String pComponenteClientId){
		Object xValue = null;
//		if (RenderKitUtils.isPartialOrBehaviorAction(pContext, pComponenteClientId)
//		|| pContext.getExternalContext().getRequestParameterMap().containsKey(pComponenteClientId)){ 
		if (pContext.getExternalContext().getRequestParameterMap().containsKey(pComponenteClientId)){ 
			xValue = pContext.getExternalContext().getRequestParameterMap().get(pComponenteClientId);
			if (xValue == null){
				return "";
			}
		}
		return xValue;
	}

	/**
	 * Retorna id do componente que originou o request.
	 * @param pContext
	 * @param pComponenteClientId
	 * @return
	 */
	public static String getDecodedSourceId(FacesContext pContext){
		return pContext.getExternalContext().getRequestParameterMap().get(DBSFaces.PARTIAL_SOURCE_PARAM); 
	}

	public static String getSubmitString(String pExecute, String pUpdate){
		String xLocalOnClick = "";
		String xUpdate = pvRemoveFirstColon(pUpdate);
		
		//Ajax
		if (!DBSObject.isEmpty(xUpdate)){
			String xExecute = "";
			if (!DBSObject.isEmpty(pExecute)){
				xExecute = "execute:'" + pvRemoveFirstColon(pExecute) + "',";
			}
			xLocalOnClick = "jsf.ajax.request(this, event, {" + xExecute + " render:'" +  xUpdate +  "', onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror}); return false";
		}
		return xLocalOnClick;
	}

	//############################ COMPONENTS AUXS ENCODES ################################################
	/**
	 * Retorna string em JS com o submit
	 * @param pComponent
	 * @param pSourceEvent Evento a ser disparado
	 * @param pExecute
	 * @param pUpdate
	 * @return
	 */
	public static String getSubmitString(DBSUICommand pComponent, String pSourceEvent, String pExecute, String pUpdate){

//		Collection<ClientBehaviorContext.Parameter> params = getBehaviorParameters(xLink)
//		RenderKitUtils.renderOnclick(pContext, xLink, params, "", true);
		String xUserOnClick = (String) pComponent.getAttributes().get(pSourceEvent); 
		String xLocalOnClick = xUserOnClick;
		String xUpdate = pvRemoveFirstColon(pUpdate);
		
		//Ajax
		if (!DBSObject.isEmpty(xUpdate)){
			xLocalOnClick = getSubmitString(pExecute, xUpdate);
//			xLocalOnClick = "mojarra.ab(this,event,'action','" + pvRemoveFirstColon(pExecute) + "','" + xClientUpdate + "',{'onevent':dbsfaces.onajax,'onerror':dbsfaces.onajaxerror});return false";

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
			if (pComponent.getOutcome() != null){
				FacesContext xContext = FacesContext.getCurrentInstance();
				NavigationCase xNavCase = getNavigationCase(xContext, pComponent);
				if (xNavCase !=null){
				    String hrefVal = getEncodedTargetURL(xContext, pComponent, xNavCase);
				    hrefVal += getFragment(pComponent);
				    xLocalOnClick = getOnclickOutCome(pComponent, hrefVal);
				}
			}else if (DBSObject.isEmpty(pExecute)){
//				System.out.println("Form/Execute não definido para o componente " + pComponent.getClientId()  + "!");
			}else if (DBSObject.isEmpty(xLocalOnClick)){
				StringBuilder xParam = new StringBuilder();
				xParam.append("'"+ pComponent.getClientId() + "':'"+ pComponent.getClientId() + "'");
				//Incorpora os parametros definidos via <f:param> dentro do componente
		    	if (pComponent.getChildCount() > 0){
		    		for (UIComponent xC: pComponent.getChildren()){
		    			if (xC instanceof UIParameter){
		    				UIParameter xP = (UIParameter) xC;
		    				xParam.append(",'"+ xP.getName() + "':'"+ DBSObject.getNotNull(xP.getValue(), "") + "'");
		    			}
		    		}
		    	}
				xLocalOnClick = "mojarra.jsfcljs(document.getElementById('" + pExecute + "'),{"+ xParam.toString() + "},''); return false";
			}else if (!DBSObject.isEmpty(xUserOnClick)){
				xLocalOnClick = xUserOnClick;
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
	
    /**
     * Retorna código JS para set executado no onclick.
     * @param component
     * @param targetURI
     * @return
     */
    protected static String getOnclickOutCome(UIComponent component, String targetURI) {

        String onclick = (String) component.getAttributes().get("onclick");

        if (onclick != null) {
            onclick = onclick.trim();
            if (onclick.length() > 0 && !onclick.endsWith(";")) {
                onclick += "; ";
            }
        }
        else {
            onclick = "";
        }

        if (targetURI != null) {
            onclick += "window.location.href='" + targetURI + "'; ";
        }

        onclick += "return false;";

        return onclick;
        
    }
	
	 /**
     * Invoke the {@link NavigationHandler} preemptively to resolve a {@link NavigationCase}
     * for the outcome declared on the {@link UIOutcomeTarget} component. The current view id
     * is used as the from-view-id when matching navigation cases and the from-action is
     * assumed to be null.
     *
     * @param pContext the {@link FacesContext} for the current request
     * @param pComponent the target {@link UIComponent}
     *
     * @return the NavigationCase represeting the outcome target
     */
    protected static NavigationCase getNavigationCase(FacesContext pContext, DBSUICommand pComponent) {
        NavigationHandler xNavHandler = pContext.getApplication().getNavigationHandler();
        if (!(xNavHandler instanceof ConfigurableNavigationHandler)) {
//            if (logger.isLoggable(Level.WARNING)) {
//                logger.log(Level.WARNING,
//                    "jsf.outcome.target.invalid.navigationhandler.type",
//                    component.getId());
//            }
            return null;
        }

        String xOutcome = pComponent.getOutcome();
        if (xOutcome == null) {
            xOutcome = pContext.getViewRoot().getViewId();
            // QUESTION should we avoid the call to getNavigationCase() and instead instantiate one explicitly?
            //String viewId = context.getViewRoot().getViewId();
            //return new NavigationCase(viewId, null, null, null, viewId, false, false);
        }
        String xToFlowDocumentId = (String) pComponent.getAttributes().get(ActionListener.TO_FLOW_DOCUMENT_ID_ATTR_NAME);
        NavigationCase xNavCase = null;
        if (null == xToFlowDocumentId) {
            xNavCase = ((ConfigurableNavigationHandler) xNavHandler).getNavigationCase(pContext, null, xOutcome);            
        } else {
            xNavCase = ((ConfigurableNavigationHandler) xNavHandler).getNavigationCase(pContext, null, xOutcome, xToFlowDocumentId);            
        }

//        if (navCase == null && logger.isLoggable(Level.WARNING)) {
//            logger.log(Level.WARNING,
//                    "jsf.outcometarget.navigation.case.not.resolved",
//                    component.getId());
//        }
        return xNavCase;
    }
    
    /**
     * <p>Resolve the target view id and then delegate to
     * {@link ViewHandler#getBookmarkableURL(javax.faces.context.FacesContext, String, java.util.Map, boolean)}
     * to produce a redirect URL, which will add the page parameters if necessary
     * and properly prioritizing the parameter overrides.</p>
     *
     * @param pContext the {@link FacesContext} for the current request
     * @param pComponent the target {@link UIComponent}
     * @param pNavCase the target navigation case
     *
     * @return an encoded URL for the provided navigation case
     */
    protected static String getEncodedTargetURL(FacesContext pContext, DBSUICommand pComponent, NavigationCase pNavCase) {
        // FIXME getNavigationCase doesn't resolve the target viewId (it is part of CaseStruct)
        String xToViewId = pNavCase.getToViewId(pContext);
        Map<String,List<String>> xParams = getParamOverrides(pComponent);
        addNavigationParams(pNavCase, xParams);
        String xResult = null;
        boolean xDidDisableClientWindowRendering = false;
        ClientWindow xCW = null;

        
        try {
            Map<String, Object> xAttrs = pComponent.getAttributes();
            Object xVal = xAttrs.get("disableClientWindow");
            if (null != xVal) {
                xDidDisableClientWindowRendering = "true".equalsIgnoreCase(xVal.toString());
            }
            if (xDidDisableClientWindowRendering) {
                xCW = pContext.getExternalContext().getClientWindow();
                if (null != xCW) {
                    xCW.disableClientWindowRenderMode(pContext);
                }
            }
            
            xResult = Util.getViewHandler(pContext).getBookmarkableURL(pContext,
                                                               xToViewId,
                                                               xParams,
                                                               isIncludeViewParams(pComponent, pNavCase));
        } finally {
            if (xDidDisableClientWindowRendering && null != xCW) {
                xCW.enableClientWindowRenderMode(pContext);
            }
        }
        
        return xResult;
    }
    
	protected static void addNavigationParams(NavigationCase pNavCase, Map<String, List<String>> pExistingParams) {
		Map<String, List<String>> xNavParams = pNavCase.getParameters();
		FacesContext xContext = FacesContext.getCurrentInstance();
		if (xNavParams != null && !xNavParams.isEmpty()) {
			for (Map.Entry<String, List<String>> entry : xNavParams.entrySet()) {
				String xNavParamName = entry.getKey();
				// only add the navigation params to the existing params
				// collection
				// if the parameter name isn't already present within the
				// existing
				// collection
				if (!pExistingParams.containsKey(xNavParamName)) {
					if (entry.getValue().size() == 1) {
						String xValue = entry.getValue().get(0);
						String xSanitized = null != xValue && 2 < xValue.length() ? xValue.trim() : "";
						String xS1 = "#" + "{";
						String xS2 = "&" + "{";
						if (xSanitized.contains(xS1) || xSanitized.contains(xS2)) {
							xValue = xContext.getApplication().evaluateExpressionGet(xContext, xValue, String.class);
							List<String> xValues = new ArrayList<String>();
							xValues.add(xValue);
							pExistingParams.put(xNavParamName, xValues);
						} else {
							pExistingParams.put(xNavParamName, entry.getValue());
						}
					} else {
						pExistingParams.put(xNavParamName, entry.getValue());
					}
				}
			}
		}

		String xToFlowDocumentId = pNavCase.getToFlowDocumentId();
		if (null != xToFlowDocumentId) {
			if (FlowHandler.NULL_FLOW.equals(xToFlowDocumentId)) {
				List<String> xFlowDocumentIdValues = new ArrayList<String>();
				xFlowDocumentIdValues.add(FlowHandler.NULL_FLOW);
				pExistingParams.put(FlowHandler.TO_FLOW_DOCUMENT_ID_REQUEST_PARAM_NAME, xFlowDocumentIdValues);

				FlowHandler xFH = xContext.getApplication().getFlowHandler();
				if (xFH instanceof FlowHandlerImpl) {
					FlowHandlerImpl xFHI = (FlowHandlerImpl) xFH;
					List<String> xFlowReturnDepthValues = new ArrayList<String>();
					xFlowReturnDepthValues.add("" + xFHI.getAndClearReturnModeDepth(xContext));
					pExistingParams.put(FlowHandlerImpl.FLOW_RETURN_DEPTH_PARAM_NAME, xFlowReturnDepthValues);
				}

			} else {
				String xFlowId = pNavCase.getFromOutcome();
				List<String> xFlowDocumentIdValues = new ArrayList<String>();
				xFlowDocumentIdValues.add(xToFlowDocumentId);
				pExistingParams.put(FlowHandler.TO_FLOW_DOCUMENT_ID_REQUEST_PARAM_NAME, xFlowDocumentIdValues);

				List<String> xFlowIdValues = new ArrayList<String>();
				xFlowIdValues.add(xFlowId);
				pExistingParams.put(FlowHandler.FLOW_ID_REQUEST_PARAM_NAME, xFlowIdValues);
			}
		}

	}
	
    public static boolean isIncludeViewParams(DBSUICommand pComponent, NavigationCase pNavcase) {
        return pComponent.isIncludeViewParams() || pNavcase.isIncludeViewParams();

    }

	public static Map<String, List<String>> getParamOverrides(UIComponent pComponent) {
		Map<String, List<String>> xParams = new LinkedHashMap<String, List<String>>();
		Param[] xDeclaredParams = getParamList(pComponent);
		for (Param xCandidate : xDeclaredParams) {
			// QUESTION shouldn't the trimming of name should be done elsewhere?
			// null value is allowed as a way to suppress page parameter
			if (xCandidate.name != null && xCandidate.name.trim().length() > 0) {
				xCandidate.name = xCandidate.name.trim();
				List<String> xValues = xParams.get(xCandidate.name);
				if (xValues == null) {
					xValues = new ArrayList<String>();
					xParams.put(xCandidate.name, xValues);
				}
				xValues.add(xCandidate.value);
			}
		}

		return xParams;
	}
	
    
    public static Param[] getParamList(UIComponent pCommand) {

        if (pCommand.getChildCount() > 0) {
            ArrayList<Param> parameterList = new ArrayList<Param>();

            for (UIComponent kid : pCommand.getChildren()) {
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

    public static String getFragment(UIComponent component) {

        String fragment = (String) component.getAttributes().get("fragment");
        fragment = (fragment != null ? fragment.trim() : "");
        if (fragment.length() > 0) {
            fragment = "#" + fragment;
        }
        return fragment;

    }

    
	/**
	 * Efetua o <b>writeAttribute</b> testanto se valor é nulo.
	 * @param pWriter
	 * @param pAttribute
	 * @param pValue
	 * @throws IOException
	 */
	public static void encodeAttribute(ResponseWriter pWriter, String pAttribute, Object pValue) throws IOException{
		encodeAttribute(pWriter, pAttribute, pValue, null);
	}


	/**
	 * Efetua o <b>writeAttribute</b> testanto se valor é nulo.
	 * @param pWriter
	 * @param pAttribute
	 * @param pValue
	 * @throws IOException
	 */
	public static void encodeAttribute(ResponseWriter pWriter, String pAttribute, Object pValue, String pValueDefault) throws IOException{
	   	if (pAttribute == null){return;}
	   	pAttribute = pAttribute.trim();
		if (pValue != null){
			if (pValue.getClass().isAssignableFrom(String.class)){
				String xValue = ((String) pValue).trim();
	    		if (pAttribute.trim().toLowerCase() == "class"){
	    			//Retira todos espaços extras
	    			xValue = xValue.replaceAll(" +", " ");
	    		}
	    		pValue = xValue;
			}
			pWriter.writeAttribute(pAttribute, pValue, pAttribute);
		}else{
			if (pValueDefault != null ){	
				pWriter.writeAttribute(pAttribute, pValueDefault, pAttribute);
			}
		}
	 }

//	/**
//	 * Atributo que indica que existe mensagem restornada.<br/>
//	 * Este controle é efetuado pelo compomentes DBSUICommand com render DBSUICommandRender
//	 * @param pWriter
//	 * @throws IOException
//	 */
//	public static void encodeAttributeHasMessage(DBSUICommand pComponent, ResponseWriter pWriter) throws IOException{
//		if (pComponent.getHasMessage()){
//			encodeAttribute(pWriter, "data-hasmsg", "true");
//		}
//	}

	/**
	 * Encode atributos no formato campo:valor;.
	 * @param pWriter
	 * @param pAttrs String com atributos no formato campo1=valor1. ex: id=grafico3:pie1_deltapath_l; fill=none;
	 * @throws IOException
	 */
	public static void encodeAttributes(ResponseWriter pWriter, String pAttrs) throws IOException{
		if (pAttrs!=null){
			String[] xAttrs = pAttrs.split("[;]");
			for (String xAttr: xAttrs){
				Integer xI = xAttr.indexOf("=");
				String xAttrName;
				String xAttrValue;
				if (xI != -1){
					xAttrName = xAttr.substring(0, xI).trim().toLowerCase();
					xAttrValue = xAttr.substring(xI+1).trim();
					encodeAttribute(pWriter, xAttrName, xAttrValue);
				}
			}
		}
	}


	public static void encodeStyleTagStart(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
//		pWriter.write("	<style type='text/css'> \n");
		pWriter.startElement("style", pComponent);
		encodeAttribute(pWriter, "type", "text/css");
	}
	
	public static void encodeStyleTagEnd(ResponseWriter pWriter) throws IOException{
//		pWriter.write(" </style> \n");
		pWriter.endElement("style");
	}
	
//	xWriter.startElement("script", xUICommand);
//	DBSFaces.encodeAttribute(xWriter, "type", "text/javascript");
//	DBSFaces.encodeJavaScriptTagStart(xWriter);
//	String xJS = "$(document).ready(function(){\n" +
//			     " dbsfaces.component.setHasMessage(dbsfaces.util.jsid('" + getClientId() + "'));\n" +
//                 "});\n";
//	String xJS = "dbsfaces.component.setHasMessage(dbsfaces.util.jsid('" + xUICommand.getClientId() + "'));";
//	String xJS = "$(document).ready(function() { \n" +
//		     " var xButtonId = dbsfaces.util.jsid('" + getClientId() + "'); \n " + 
//		     " dbs_button(xButtonId); \n" +
//            "}); \n";
//	xWriter.write(xJS);
	
	public static void encodeJavaScriptTagStart(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
		encodeJavaScriptTagStart(pComponent, pWriter, null);
	}

	/**
	 * @param pComponent
	 * @param pWriter
	 * @param pAttrs String com atributos no formato campo1=valor1. ex: id=grafico3:pie1_deltapath_l; fill=none;
	 * @throws IOException
	 */
	public static void encodeJavaScriptTagStart(UIComponent pComponent, ResponseWriter pWriter, String pAttrs) throws IOException{
		
		pWriter.write('\n');
		pWriter.startElement("script", null);
		pWriter.writeAttribute("type", "text/javascript", null);
//		pWriter.writeAttribute("src", "", null);
		encodeAttributes(pWriter, pAttrs);
		//pWriter.write(" /* <![CDATA[ */ \n");
	}

	
	public static void encodeJavaScriptTagEnd(ResponseWriter pWriter) throws IOException{
		//pWriter.write(" /* ]]> */ ");
		pWriter.endElement("script");
       pWriter.append('\r');
       pWriter.append('\n');
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
	public static <InputComponent extends DBSUIInput> void encodeLabel(FacesContext pContext, InputComponent pInput, ResponseWriter pWriter) throws IOException{
		if (pInput.getLabel()!=null){
			String xClientId = pInput.getClientId(pContext);
//			String xStyle = "vertical-align:middle; display:inline-block;";
			String xStyle = "";
			if (!pInput.getLabelWidth().equals("")){
				xStyle += " width:" + pInput.getLabelWidth() + ";";
			}
			pWriter.startElement("label", pInput);
				encodeAttribute(pWriter, "class", CSS.THEME.INPUT_LABEL + CSS.NOT_SELECTABLE);
				encodeAttribute(pWriter, "for", xClientId + CSS.MODIFIER.DATA.trim());
//				if (pRenderSeparator){
					encodeAttribute(pWriter, "style", xStyle);
					pWriter.write(pInput.getLabel().trim());
//				}else{
//					xStyle += "padding-left:2px;";
//					DBSFaces.setAttribute(pWriter, "style", xStyle);
//					pWriter.write(pInput.getLabel().trim());
//				}
				//Encode simbolo da moeda se componente for do tipo inputnumber
				if(pInput instanceof DBSInputNumber){
					DBSInputNumber xIN = (DBSInputNumber) pInput;
					if (xIN.getCurrencySymbol()!=null){
						pWriter.startElement("span", pInput);
							encodeAttribute(pWriter, "style","right: 0; position: absolute;");
							pWriter.write(xIN.getCurrencySymbol().trim());
						pWriter.endElement("span");
					}
				}
			pWriter.endElement("label");
		}
	}
	
	/**
	 * Gere HTML padrão do label a direita do campo
	 * @param pContext
	 * @param pInput
	 * @param pWriter
	 * @throws IOException
	 */
	public static void encodeRightLabel(FacesContext pContext, DBSUIInput pInput, ResponseWriter pWriter) throws IOException{
		if (pInput.getRightLabel()!=null){
			String xClientId = pInput.getClientId(pContext);
			pWriter.startElement("label", pInput);
				encodeAttribute(pWriter, "class", CSS.THEME.INPUT_LABEL + CSS.NOT_SELECTABLE);
				encodeAttribute(pWriter, "for", xClientId + CSS.MODIFIER.DATA.trim());
				encodeAttribute(pWriter, "style", "margin:0 3px 0 3px; vertical-align: middle; display:inline-block;");
				pWriter.write(pInput.getRightLabel().trim());
			pWriter.endElement("label");
		}
	}

	/**
	 * Gera HTML para campos input como tag <span> para evitar qualquer alteração no cliente
	 * @param pComponent
	 * @param pWriter
	 * @param pClientId
	 * @param pNormalWhiteSpace Indica se haverá quebra de linha caso o valor do campo seja ao comprimento a linha.<br/>
	 * true: quebra linha<br/>
	 * false: não quebra linha
	 * @param pValue Valor a ser exibido
	 * @param pTW
	 * @param pTH
	 * @param pStyle Style adicional
	 * @throws IOException
	 */
	public static void encodeInputDataReadOnly(UIComponent pComponent, ResponseWriter pWriter, String pClientId, boolean pNormalWhiteSpace, String pValue, Integer pTW) throws IOException{
		encodeInputDataReadOnly(pComponent, pWriter, pClientId,pNormalWhiteSpace, pValue, pTW, null, null);
	}
	/**
	 * Gera HTML para campos input como tag <span> para evitar qualquer alteração no cliente
	 * @param pComponent
	 * @param pWriter
	 * @param pClientId
	 * @param pNormalWhiteSpace Indica se haverá quebra de linha caso o valor do campo seja ao comprimento a linha.<br/>
	 * true: quebra linha<br/>
	 * false: não quebra linha
	 * @param pValue Valor a ser exibido
	 * @param pTW
	 * @param pTH
	 * @param pStyle Style adicional
	 * @throws IOException
	 */
	public static void encodeInputDataReadOnly(UIComponent pComponent, ResponseWriter pWriter, String pClientId, boolean pNormalWhiteSpace, String pValue, Integer pTW, Integer pTH, String pStyle) throws IOException{
		pStyle = DBSObject.getNotEmpty(pStyle, "");
		if (pNormalWhiteSpace){
			pStyle += "white-space: pre-wrap; overflow:auto;";
		}else{
			pStyle += "white-space: pre; overflow:hidden;";
		}
		pWriter.startElement("span", pComponent);
			encodeAttribute(pWriter, "id", pClientId);
			encodeAttribute(pWriter, "name", pClientId);
			encodeAttribute(pWriter, "class", getInputDataClass(pComponent));
			encodeAttribute(pWriter, "style", pStyle);
			setSizeAttributes(pWriter, pTW, pTH);
			if (DBSObject.isEmpty(pValue)){
				pWriter.write(" ");
			}else{
				pWriter.write(pValue);
			}
		pWriter.endElement("span");
	}

	
	/**
	 * Grava atributo com a quantidade de caracteres do campo.<br/>
	 * Esta valor será utlizado em JS para determinar a largura e altura mínima do campo em px.
	 * @param pWriter
	 * @param pTW
	 * @param pTH
	 * @throws IOException
	 */
	public static void setSizeAttributes(ResponseWriter pWriter, Integer pTW, Integer pTH) throws IOException{
		if (pTW != null && pTW > 0){
			encodeAttribute(pWriter, "tw", pTW);
		}
		if (pTH != null && pTH > 0){
			encodeAttribute(pWriter, "th", pTH);
		}
	}
	
	/**
	 * Cria o elemento que conterá o tooltip.
	 * @param pWriter
	 * @param pComponent
	 * @param pTooltip
	 * @param pClienteId 
	 * @throws IOException
	 */
	public static void encodeTooltipQuickInfo(FacesContext pContext, UIComponent pComponent, Integer pDefaultLocation) throws IOException{
		pvEncodeTooltip(false, pContext, pComponent, pDefaultLocation, null, 0);
	}
	
	
	/**
	 * Cria o elemento que conterá o tooltip.
	 * @param pWriter
	 * @param pComponent
	 * @param pTooltipText
	 * @throws IOException
	 */
	public static void encodeTooltip(FacesContext pContext, UIComponent pComponent, String pTooltipText) throws IOException{
		encodeTooltip(pContext, pComponent, null, pTooltipText, pComponent.getClientId(), null);
	}
	
	/**
	 * Cria o elemento que conterá o tooltip.<br/>
	 * <b>Somente utilize este método caso seja um tooltip para um componente filho do componente principal.</b>
	 * @param pWriter
	 * @param pComponent
	 * @param pTooltipText
	 * @param pClienteId 
	 * @throws IOException
	 */
	public static void encodeTooltip(FacesContext pContext, UIComponent pComponent, String pTooltipText, String pSourceClientId) throws IOException{
		encodeTooltip(pContext, pComponent, null, pTooltipText, pSourceClientId, null);
	}

	/**
	 * Cria o elemento que conterá o tooltip.<br/>
	 * <b>Somente utilize este método caso seja um tooltip para um componente filho do componente principal.</b>
	 * @param pWriter
	 * @param pComponent
	 * @param pTooltipText
	 * @param pClienteId 
	 * @throws IOException
	 */
	public static void encodeTooltip(FacesContext pContext, UIComponent pComponent, Integer pDefaultLocation, String pTooltipText, String pSourceClientId) throws IOException{
		encodeTooltip(pContext, pComponent, pDefaultLocation, pTooltipText, pSourceClientId, null);
	}
	/**
	 * Cria o elemento que conterá o tooltip.<br/>
	 * <b>Somente utilize este método caso seja um tooltip para um componente filho do componente principal.</b>
	 * @param pWriter
	 * @param pComponent
	 * @param pTooltipText
	 * @param pClienteId 
	 * @throws IOException
	 */
	public static void encodeTooltip(FacesContext pContext, UIComponent pComponent, Integer pDefaultLocation, String pTooltipText, String pSourceClientId, Integer pDelay) throws IOException{
		ResponseWriter 	xWriter = pContext.getResponseWriter();		
		if (pvEncodeTooltip(true, pContext, pComponent, pDefaultLocation, pTooltipText, pDelay)){
			//Javascript 
			DBSFaces.encodeJavaScriptTagStart(pComponent, xWriter);
			String xJS = "$(document).ready(function() { \n" +
					     " var xTooltip = dbsfaces.util.jsid('" + pSourceClientId + "'); \n " + 
					     " dbs_tooltip(xTooltip); \n" +
	                     "}); \n"; 
			xWriter.write(xJS);
			DBSFaces.encodeJavaScriptTagEnd(xWriter);		
		};
	}
	
	/**
	 * Retorna a classe utilizada nos inputs na área que recebe os dados
	 * @param pInput
	 * @return
	 */
	public static String getInputDataClass(UIComponent pInput){
		String xClass = CSS.THEME.INPUT_DATA;
		DBSUIInput xInput;
		if (pInput instanceof DBSUIInput){
			xInput = (DBSUIInput) pInput;
		}else{
			return xClass;
		}
		if (xInput.getReadOnly()){
			xClass += CSS.MODIFIER.READONLY;
		}
		//Verifica se existe mensagem enviada para este componente e inclui class conforme tipo de mensagem.
		IDBSMessages xMessages = DBSMessagesFacesContext.getMessages(pInput.getClientId());
		if (xMessages != null){
			//Retorna mensagem mais severa
			IDBSMessage xMessage = xMessages.getMostSevereMessage();
			if (xMessage != null){
				xClass += xMessage.getMessageType().getInputStyleClass();
				//Copia texto da mensagem para indicar que input recebeu mensagem.
				//No ProcessEvento do DBSUIInput, este input será forçado a fazer novo update para verificar se erro persite.
				xInput.setValidatorMessage(xMessage.getMessageText());
				xInput.setValid(false);
//				System.out.println("getInputDataClass:\t" + xInput.getClientId() + "\t" + xInput.isValid());
			}else if (!xInput.isValid()){
				xClass += MESSAGE_TYPE.ERROR.getInputStyleClass();
			}
		}
		return xClass;
	}

    /**
	 * Encore dos names spaces padrão do SVG
	 * @param pComponent
	 * @param pWriter
	 * @param pX1
	 * @param pY1
	 * @param pX2
	 * @param pY2
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGNamespaces(ResponseWriter pWriter) throws IOException{
		encodeAttribute(pWriter, "xmlns", "http://www.w3.org/2000/svg");
		encodeAttribute(pWriter, "xmlns:xlink", "http://www.w3.org/1999/xlink");
	}

	
	/**
	 * Encore da linha para gráfico SVG
	 * @param pComponent
	 * @param pWriter
	 * @param pX1
	 * @param pY1
	 * @param pX2
	 * @param pY2
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGTag(UIComponent pComponent, ResponseWriter pWriter, String pTagName, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		pWriter.startElement(pTagName, pComponent);
			encodeSVGSetDefaultAttr(pWriter, pStyleClass, pStyle, pAttrs);
		pWriter.endElement(pTagName);
	}

	/**
	 * Encore da linha para gráfico SVG
	 * @param pComponent
	 * @param pWriter
	 * @param pX1
	 * @param pY1
	 * @param pX2
	 * @param pY2
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGLine(UIComponent pComponent, ResponseWriter pWriter, Number pX1, Number pY1, Number pX2, Number pY2, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		pWriter.startElement("line", pComponent);
			encodeSVGSetDefaultAttr(pWriter, pStyleClass, pStyle, pAttrs);
			encodeAttribute(pWriter, "x1", 	pX1);
			encodeAttribute(pWriter, "y1", 	pY1);
			encodeAttribute(pWriter, "x2", 	pX2);
			encodeAttribute(pWriter, "y2", 	pY2);
		pWriter.endElement("line");
	}

	/**
	 * Encode de Retangulo para grádico SVG.<br/>
	 * O ponto 0,0 é a esquerda, acima.
	 * @param pComponent
	 * @param pWriter
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGRect(UIComponent pComponent, ResponseWriter pWriter, String pX, String pY, String pWidth, String pHeight, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		encodeSVGRect(pComponent, pWriter, pX, pY, pWidth, pHeight, null, null, pStyleClass, pStyle, pAttrs);
	}
	
	/**
	 * Encode de Retangulo para grádico SVG.<br/>
	 * @param pComponent
	 * @param pWriter
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGRect(UIComponent pComponent, ResponseWriter pWriter, Number pX, Number pY, String pWidth, String pHeight, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		encodeSVGRect(pComponent, pWriter, pX, pY, pWidth, pHeight, null, null, pStyleClass, pStyle, pAttrs);
	}
	
	/**
	 * @param pComponent
	 * @param pWriter
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param pRX
	 * @param pRY
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGRect(UIComponent pComponent, ResponseWriter pWriter, Number pX, Number pY, String pWidth, String pHeight, String pRX, String pRY, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		encodeSVGRect(pComponent, pWriter, DBSNumber.round(pX, 4).toString(), DBSNumber.round(pY, 4).toString(), pWidth, pHeight, pRX, pRY, pStyleClass, pStyle, pAttrs);
	}

	/**
	 * @param pComponent
	 * @param pWriter
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param pRX
	 * @param pRY
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGRect(UIComponent pComponent, ResponseWriter pWriter, String pX, String pY, String pWidth, String pHeight, String pRX, String pRY, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		pWriter.startElement("rect", pComponent);
			encodeSVGSetDefaultAttr(pWriter, pStyleClass, pStyle, pAttrs);
			encodeAttribute(pWriter, "x", 	pX, null);
			encodeAttribute(pWriter, "y", 	pY, null);
			encodeAttribute(pWriter, "rx", pRX, null);
			encodeAttribute(pWriter, "ry", pRY, null);
			
			encodeAttribute(pWriter, "height", pHeight, null);
			encodeAttribute(pWriter, "width", pWidth, null);
		pWriter.endElement("rect");
	}
	
	
	/**
	 * @param pComponent
	 * @param pWriter
	 * @param pCX
	 * @param pCY
	 * @param pRX
	 * @param pRY
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGEllipse(UIComponent pComponent, ResponseWriter pWriter, Number pCX, Number pCY, String pRX, String pRY, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		pWriter.startElement("ellipse", pComponent);
			encodeSVGSetDefaultAttr(pWriter, pStyleClass, pStyle, pAttrs);
			encodeAttribute(pWriter, "cx", pCX);
			encodeAttribute(pWriter, "cy", pCY);
			
			encodeAttribute(pWriter, "rx", pRY);
			encodeAttribute(pWriter, "ry", pRX);
		pWriter.endElement("ellipse");
	}
	
	/**
	 * @param pComponent
	 * @param pWriter
	 * @param pHRef
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGUse(UIComponent pComponent, ResponseWriter pWriter, String pHRef, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		if (pHRef == null){return;}
		pWriter.startElement("use", pComponent);
			encodeSVGSetDefaultAttr(pWriter, pStyleClass, pStyle, pAttrs);
			encodeAttribute(pWriter, "xlink:href", "#" + pHRef);
		pWriter.endElement("use");
	}
	
	/**
	 * @param pComponent
	 * @param pWriter
	 * @param pData Dados do path(d).
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGPath(UIComponent pComponent, ResponseWriter pWriter, String pData, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		pWriter.startElement("path", pComponent);
			encodeSVGSetDefaultAttr(pWriter, pStyleClass, pStyle, pAttrs);
			encodeAttribute(pWriter, "d", 	pData);
		pWriter.endElement("path");
	}
	
	
	/**
	 * Encode de Texto para grádico SVG
	 * @param pComponent
	 * @param pWriter
	 * @param pX
	 * @param pY
	 * @param pText
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGText(UIComponent pComponent, ResponseWriter pWriter, Number pX, Number pY, String pText, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		String xX = null;
		String xY = null;
		if (pX !=null){
			xX = DBSNumber.round(pX, 4).toString();
		}
		if (pY !=null){
			xY = DBSNumber.round(pY, 4).toString();
		}
		encodeSVGText(pComponent, pWriter, xX, xY, pText, pStyleClass, pStyle, pAttrs);
	}
	/**
	 * Encode de Texto para grádico SVG
	 * @param pComponent
	 * @param pWriter
	 * @param pX
	 * @param pY
	 * @param pText
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs Atributos nos formato atributo1=valor2; atributo2=valor2
	 * @throws IOException
	 */
	public static void encodeSVGText(UIComponent pComponent, ResponseWriter pWriter, String pX, String pY, String pText, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		pWriter.startElement("text", pComponent);
			encodeSVGSetDefaultAttr(pWriter, pStyleClass, pStyle, pAttrs);
			encodeAttribute(pWriter, "x", pX, null);
			encodeAttribute(pWriter, "y", pY, null);
			if (pText != null){
				pWriter.write(pText);
			}
		pWriter.endElement("text");
	}
	/**
	 * Encode da Class, Style e Atributos em componente SVG
	 * @param pWriter
	 * @param pStyleClass
	 * @param pStyle
	 * @param pAttrs String com atributos no formato campo1=valor1. ex:  id=grafico3:pie1_deltapath_l; fill=none;
	 * @throws IOException
	 */
	public static void encodeSVGSetDefaultAttr(ResponseWriter pWriter, String pStyleClass, String pStyle, String pAttrs) throws IOException{
		encodeAttributes(pWriter, pAttrs);
		encodeAttribute(pWriter, "class", pStyleClass);
		encodeAttribute(pWriter, "style", pStyle);
	}

	//================================================================================
	/**
	 * Retorna color(String hsla) em função da quantidade de gráficos e itens em cada gráficos e a posição do item que se deseja calcular a cor.<br/>
	 * Quando não for informado uma cor para o gráfico, a rotina irá atribuir um cor.
	 * @param pColor
	 * @param pChartsItensCount
	 * @param pChartItensCount
	 * @param pChartIndex
	 * @param pChartValueIndex
	 * @return
	 */
	public static String calcChartFillcolor(DBSColor pColor, Integer pChartsItensCount, Integer pChartItensCount, Integer pChartIndex, Integer pChartValueIndex){
		return calcChartFillcolor(pColor, pChartsItensCount, pChartItensCount, pChartIndex, pChartValueIndex, 1f);
	}
	/**
	 * Retorna color(String hsla) em função da quantidade de gráficos e itens em cada gráficos e a posição do item que se deseja calcular a cor.<br/>
	 * Quando não for informado uma cor para o gráfico, a rotina irá atribuir um cor.
	 * @param pColor
	 * @param pChartsItensCount
	 * @param pChartItensCount
	 * @param pChartIndex
	 * @param pChartValueIndex
	 * @return
	 */
	public static String calcChartFillcolor(DBSColor pColor, Integer pChartsItensCount, Integer pChartItensCount, Integer pChartIndex, Integer pChartValueIndex, float pAlpha){
		if (pChartItensCount ==null || pChartItensCount == 0){return null;}
		Float xChartFator = DBSNumber.divide(pChartIndex, pChartsItensCount).floatValue();
		Float xChartValueFator = DBSNumber.divide(pChartValueIndex, pChartItensCount).floatValue();
		Float xColorH;
		Float xColorL;
		Float xColorS;
		double xColorA;
		if (pColor != null){
			xColorA = pColor.toHSLA().getAlpha();
			xColorH = pColor.toHSLA().getHue();
			xColorS = pColor.toHSLA().getSaturation(); 
			//usa 40% da luminosidade para gerar nova cor 
			xColorL = DBSNumber.add(DBSNumber.multiply(DBSNumber.multiply(pColor.toHSLA().getLightness(),
																		.3), 
														xChartValueFator),
									DBSNumber.multiply(pColor.toHSLA().getLightness(),
													   .7)).floatValue();
		}else{
			xColorA = 1F;
			xColorS = 100f;
			xColorH = DBSNumber.multiply(360, xChartFator).floatValue();
			//usa 50% da luminosidade para gerar nova cor 
			xColorL = 30 + DBSNumber.multiply(20, xChartValueFator).floatValue();
		}
		xColorA *= pAlpha;
		return "hsla(" + xColorH + ", " + xColorS + "%, " + xColorL + "%, " + xColorA + ")";
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
	 * @param pContext
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
	 * @param pContext
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
 	
 	/**
 	 * Retorna valor da propriedade contida no resource-bundle informado.
 	 * @param pResourceBundle
 	 * @param pPropertyName
 	 * @return
 	 */
 	public static String getBundlePropertyValue(String pResourceBundle, String pPropertyName){
 		FacesContext xContext = FacesContext.getCurrentInstance();
 		if (xContext != null){
 			return xContext.getApplication().evaluateExpressionGet(xContext, addELSignals(pResourceBundle + "['" + pPropertyName + "']"), String.class);
 		}
 		return null;
 	}
 	
	/**
	 * Seta o valor no bean a partir do valueExpression.
	 * @param pContext
	 * @param pELString
	 * @param pValue
	 */
	public static void setValueWithValueExpression(FacesContext pContext, String pELString, Object pValue){
		ValueExpression xVE = null;
		
		//Cria chamada ao método do crudBean para configura o campo
		xVE = DBSFaces.createValueExpression(pContext, pELString, Object.class);
    	xVE.setValue(pContext.getELContext(), pValue);
		
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
 	
	public static String getRenderedViewContent(URL pURL, List<String> pListParams) throws DBSIOException {
		return getRenderedViewContent(pURL.toString(), pListParams);
	}

	/**
	 * Retorna String com resposta ao request ao arquivo da URL com os parametros informados.
	 * A URL e o nome do arquivo serão ajustados com a inclusão ou exclusão das barras "/" de separação necessárias.
	 * Utilizar este método quando a view nao precisar consultar algum bean, mas receberá valores por parametro.<br/>
	 * Caso contrário, utilize o outro método.
	 * @param pURL
	 * @param pFile 
	 * @param pListParams
	 * @return
	 * @throws DBSIOException
	 */
	public static String getRenderedViewContent(String pURL, String pFile, List<String> pListParams) throws DBSIOException {
		return getRenderedViewContent(DBSFile.getPathNormalized(pURL, pFile), pListParams);
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
			xConnection.setRequestProperty("Request-Method", METHOD.POST.getName());
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
				if (!DBSObject.isNull(xBuffer)) {
					xBuffer.close();
				}
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
		if (pDataTable.isSelectable()){
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
						xBtn.setExecute("@this :modal"); //:modal necessário para fazer o submit dos campos dentro do form, em caso de crudtable dentro de crudform 
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
				if (DBSDataTable.SELECTION_TYPE.get(pDataTable.getSelectionType()) == SELECTION_TYPE.MULTI){
					//Troca styleClass para poder exibir a coluna
					xC0.setStyleClass(CSS.MODIFIER.CHECKBOX);
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
				xBtPesquisar.setLabel(getBundlePropertyValue("dbsfaces", "pesquisar"));
				xBtPesquisar.setIconClass(CSS.MODIFIER.ICON + " -i_find");
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
					encodeAttribute(xWriter, "id", pDataTable.getClientId() + ":toolbar", null);
					encodeAttribute(xWriter, "name", pDataTable.getClientId() + ":toolbar", null);
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
			xButtonStart.setIconClass(CSS.MODIFIER.ICON + " -i_upload");
			xButtonStart.setReadOnly(pFileUpload.getReadOnly());
			xButtonStart.setExecute("");
			xButtonStart.setUpdate("");
			if (DBSObject.isEmpty(pFileUpload.getTooltip())) {
				xButtonStart.setTooltip(getBundlePropertyValue("dbsfaces", "fileupload.btStart"));
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
			xButtonCancel.setIconClass(CSS.MODIFIER.ICON + " -i_media_stop");
			xButtonCancel.setStyle("display:none;");
			xButtonCancel.setReadOnly(pFileUpload.getReadOnly());
			xButtonCancel.setTooltip(getBundlePropertyValue("dbsfaces", "fileupload.btCancel"));
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
//		 		try{
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
//		 		}catch(Exception e){
//		 			wLogger.error(e);
//		 		}
			}
	 	}
	 	return xSubmittedValue;
	 }
	 
	 
	/**
	 * Exclui outros beans da sessão do usuário obrigando a serem reinicializados, para evitar que trabalhem com valores antigos
	 * @param pCrudBean
	 * @param pIsRecursive
	 */
	public static void finalizeCrudBeans(DBSCrudOldBean pCrudBean, boolean pIsRecursive){
		ExternalContext xEC = FacesContext.getCurrentInstance().getExternalContext();
	    for (Entry<String, Object> xEntry : xEC.getSessionMap().entrySet()) {
	        if((xEntry.getValue() instanceof SerializableContextualInstanceImpl)){
	            @SuppressWarnings("rawtypes")
				SerializableContextualInstanceImpl xImpl = (SerializableContextualInstanceImpl) xEntry.getValue();
	            Object xO = xImpl.getInstance();
	            if (xO instanceof DBSCrudOldBean){
	            	try {
	            		//Exclui da seção qualquer outro DBSCrudBean, para que seja reinializado caso seja chamado novamente.
	            		//Isso permite que os valores(principalmente os das listas no initilize) destes DBSCrudBean, sejam refeitos
	            		DBSCrudOldBean xCrud = (DBSCrudOldBean) xO;
	            		//1) Se quem chamou for nulo
	            		//2) Se é uma chamada recursiva e crudbean na memória for o mesmo testado
	            		//3) Se NÃO é uma chamada recursiva e não tiver pai e for o master e o crud em memória não tiver pai e não for o próprio que chamou esta rotina
	            		if (pCrudBean == null ||
	            			(pIsRecursive && xCrud.equals(pCrudBean)) ||
	            			(!pIsRecursive && pCrudBean.getParentCrudBean() == null && pCrudBean.getMasterBean() == null && xCrud.getParentCrudBean()==null && !xCrud.equals(pCrudBean))){
	            			for (DBSCrudOldBean xChild:xCrud.getChildrenCrudBean()){
	            				finalizeCrudBeans(xChild, true);
	            			}
	            			for (DBSBean xChild:xCrud.getSlavesBean()){
	            				if (xChild instanceof DBSCrudOldBean){
	            					finalizeCrudBeans((DBSCrudOldBean)xChild, true);
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
	 * SessionsBean dos Cruds e Repors foram substiuidos por ConversationScope
	 * Exclui todos os DBSBean da sessão, menos o pDBSBean informado.
	 * @param pDBSBean
	 * @param pIsChild
	 */
	@Deprecated
	public static void finalizeDBSBeans(DBSBean pDBSBean, Boolean pIsChild){
		if (!pIsChild){ //Se não for chamada recursiva
			DBSCrudOldBean xDBSCrudBean = null;
			if (pDBSBean !=null ){
	    		if (pDBSBean instanceof DBSCrudOldBean){
	    			xDBSCrudBean = (DBSCrudOldBean) pDBSBean; 
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
	            if (xO instanceof DBSBeanModalMessages){
	            	DBSBeanModalMessages 	xSessionBean = (DBSBeanModalMessages) xO;
            		DBSCrudOldBean xSessionCrudBean = null;
            		if (xSessionBean instanceof DBSCrudOldBean){
            			xSessionCrudBean = (DBSCrudOldBean) xSessionBean;
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
                			for (DBSCrudOldBean xChild:xSessionCrudBean.getChildrenCrudBean()){
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
		    		DBSCrudOldBean xDBSCrudBean = null;
		    		DBSBeanModalMessages xDBSBean = null;
	            	if (xSessionBean instanceof DBSBeanModalMessages){
		    			xDBSBean = (DBSBeanModalMessages) xSessionBean; 
            			for (DBSBean xChild:xDBSBean.getSlavesBean()){
    		    			finalizeSessionBean(xChild, true);
            			}
    	            	if (xSessionBean instanceof DBSCrudOldBean){
    		    			xDBSCrudBean = (DBSCrudOldBean) xSessionBean; 
                			for (DBSCrudOldBean xChild:xDBSCrudBean.getChildrenCrudBean()){
                				finalizeSessionBean(xChild, true);
                			}
    	            	}
		    		}
	            	xEC.getSessionMap().remove(xEntry.getKey());
	            }
	        }
	    }
	}	

	
	
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
	
    /**
     * @param component the component of interest
     * @return <code>true</code> if the button represents a <code>reset</code>
     *  button, otherwise <code>false</code>
     */
    public static boolean isReset(DBSUICommand component) {
        return ("reset".equals(component.getAttributes().get("type")));

    }
	
    /**
     * Retorna se componente foi clicado.
     * @param pContext
     * @param pComponent
     * @return
     */
    public static boolean wasClicked(FacesContext pContext, DBSUICommand pComponent) {
		return wasClicked(pContext, pComponent, null);
	}
    
    /**
     * Retorna se componente foi clicado.
     * @param pContext
     * @param pComponent
     * @param pClientId 
     * @return
     */
    public static boolean wasClicked(FacesContext pContext, DBSUICommand pComponent, String pClientId) {
//        List<ClientBehavior> clientBehaviours = pComponent.getClientBehaviors().get("click");
//        if (clientBehaviours != null) {
//            for (ClientBehavior cb : clientBehaviours) {
//                cb.decode(pContext, pComponent);
//            }
//        }
		
        Map<String,String> xRequestParamMap = pContext.getExternalContext().getRequestParameterMap();
		
		if (pClientId == null) {
			pClientId = pComponent.getClientId(pContext);
		}
		
		// Fire an action event if we've had a traditional (non-Ajax)
		// postback, or if we've had a partial or behavior-based postback.
		if (xRequestParamMap.containsKey(pClientId)
		 || RenderKitUtils.isPartialOrBehaviorAction(pContext, pClientId)){
//			System.out.println("DBSFaces wasClicked \t" + xRequestParamMap.size() + "\t" + pComponent.getClientId());
			return true;
		}
		return false;
	}
    
	/**
	 * Cria o elemento que conterá o tooltip.
	 * @param pWriter
	 * @param pComponent
	 * @param pTooltip
	 * @param pClienteId 
	 * @throws IOException
	 */
	private static boolean pvEncodeTooltip(boolean pBasicTooltip, FacesContext pContext, UIComponent pComponent, Integer pDefaultLocation, String pTooltipText, Integer pDelay) throws IOException{
		ResponseWriter 	xWriter = pContext.getResponseWriter();	
		UIComponent 	xTooltip;
		
		if (pBasicTooltip){
			xTooltip = pComponent.getFacet("tooltip");
		}else{
			xTooltip = pComponent;
		}
		
		//Encode do tooltip se houver um texto para o tooltip ou foi defindo via facet(name="tooltip") dentro do componente...
		if (!DBSObject.isEmpty(pTooltipText) ||
			xTooltip != null){
			xWriter.startElement("div", pComponent);
				String xClass = CSS.MODIFIER.TOOLTIP;
				if (pBasicTooltip){
					xClass += " -tt"; //Tooltip padrão
				}else{
					xClass += " -qi"; //Tooltip para o quickinfo
				}
				encodeAttribute(xWriter, "class", xClass);
				encodeAttribute(xWriter, "delay", pDelay, "1000"); //Tempo padrão para exibição do tooltip:1s
				encodeAttribute(xWriter, "dl", pDefaultLocation, "1"); //Posição padrão para exibição do tooltip:Top
				xWriter.startElement("div", pComponent);
					xClass = CSS.MODIFIER.CONTAINER;
					encodeAttribute(xWriter, "class", xClass);
					xWriter.startElement("div", pComponent);
						xClass = CSS.MODIFIER.CONTENT;
						if (!pBasicTooltip){
							xClass += CSS.BACK_TEXTURE_BLACK_GRADIENT;
						}
						encodeAttribute(xWriter, "class", xClass);
						//Dá prioridade para o facet
						if (xTooltip != null){
							if (pBasicTooltip){
								//Encode conteúdo do facet
								xTooltip.encodeAll(pContext);
							}else{
								//Encode dos filhos do componente
								renderChildren(pContext, xTooltip);
							}
						}else{
							//Encode texto
//								xWriter.write(getHtmlStringWithLineBreak(pTooltipText));
							xWriter.write(pTooltipText);
						}
					xWriter.endElement("div");
				xWriter.endElement("div");
			xWriter.endElement("div");
			return true;
		}
		return false;
	}

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
	
//	/**
//	 * Envia FacesMessage para a view
//	 * @param pClientId Nome do componente ao aqual esta vinculado a mensagem
//	 * @param pSeverity Tipo de severidade da mensagem
//	 * @param pMessageText texto da mensagem
//	 */
//	private static void pvSendFacesMessage(FacesMessage.Severity pSeverity, String pMessageText, String pClientId){
//		if (pSeverity == null
//		 || pMessageText == null){return;}
//		FacesContext.getCurrentInstance().addMessage(pClientId, new FacesMessage(pSeverity, pMessageText, null));		
//	}	
//	
//	/**
//	 * Envia FacesMessage para a view
//	 * @param pClientId Nome do componente ao aqual esta vinculado a mensagem
//	 * @param pSeverity Tipo de severidade da mensagem
//	 * @param pMessageText texto da mensagem
//	 */
//	private static void pvSendFacesMessage(MESSAGE_TYPE pMessageType, String pMessageText, String pClientId){
//		if (pMessageType == null
//		 || pMessageText == null){return;}
//		FacesMessage.Severity xSeverity = null;
//		if (pMessageType.getSeverity() < 19){
//			xSeverity = FacesMessage.SEVERITY_INFO;
//		}else if (pMessageType.getSeverity() < 29){
//			xSeverity = FacesMessage.SEVERITY_WARN;
//		}else if (pMessageType.getSeverity() < 49){
//			xSeverity = FacesMessage.SEVERITY_ERROR;
//		}
//		pvSendFacesMessage(xSeverity, pMessageText, pClientId);
//	}	




}
