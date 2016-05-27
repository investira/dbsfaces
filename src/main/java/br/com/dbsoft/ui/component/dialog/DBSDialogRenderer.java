package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSDialog.RENDERER_TYPE)
public class DBSDialogRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}

	@Override
	public boolean getRendersChildren() {
		return true;//True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
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
		DBSDialog 			xDialog = (DBSDialog) pComponent;
		ResponseWriter 		xWriter = pContext.getResponseWriter();
		String 				xClientId = xDialog.getClientId(pContext);
		String 				xClass = DBSFaces.CSS.DIALOG.MAIN + " ";
		String 				xStyle = "";
		MESSAGE_TYPE 		xMT = MESSAGE_TYPE.get(xDialog.getMessageType());
		if (xDialog.getWidth() != null
		 && xDialog.getWidth() != 0) {
			xStyle += "width:" + xDialog.getWidth() + "px;";
		}
		if (xDialog.getHeight() != null
		 && xDialog.getHeight() != 0) {
			xStyle += "height:" + xDialog.getHeight() + "px;";
		}
		
		//Altera título padrão caso tenha sido informado
		if (xMT != null){
			xClass += " -confirmation ";
		}

		//Define style do dialog
		if (xDialog.getStyle() != null){
			xStyle += xDialog.getStyle();
		}
		
		UIComponent xDialogMessage = xDialog.getFacet("beanDialogMessages");
	
		//Mascará de fundo
		xWriter.startElement("div", xDialog);
			xWriter.writeAttribute("id", xClientId, null);
			xWriter.writeAttribute("name", xClientId, null);
			xWriter.writeAttribute("class", xClass, null);
			//DIALOG
			xWriter.startElement("div", xDialog);
				xClass = "-dialog ";
				if (xDialog.getStyleClass()!=null){
					xClass += xDialog.getStyleClass();
				}
				xWriter.writeAttribute("class", xClass, null); 
				xWriter.writeAttribute("style", xStyle, null);
				xWriter.startElement("div", xDialog);
					xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, null); 

					pvEncodeTable(pContext, xWriter, xDialog);
				
				xWriter.endElement("div");

			xWriter.endElement("div");	
			//Javascript 
			DBSFaces.encodeJavaScriptTagStart(xWriter);
			String xJS = "$(document).ready(function() { \n" +
					     " var xDialogId = dbsfaces.util.jsid('" + xClientId + "'); \n " + 
					     " dbs_dialog(xDialogId); \n" +
	                     "}); \n"; 
			xWriter.write(xJS);
			DBSFaces.encodeJavaScriptTagEnd(xWriter);
			//DialogMessage
			if (xDialogMessage!=null){
				xDialogMessage.encodeAll(pContext);
			}
		xWriter.endElement("div");

	}

	private void pvEncodeTable(FacesContext pContext, ResponseWriter pWriter, DBSDialog pDialog) throws IOException{
		pWriter.startElement("table", pDialog);
			pWriter.writeAttribute("cellspacing", "0px", null);
			pWriter.writeAttribute("cellpadding", "0px", null);
			
			//HEADER
			pWriter.startElement("thead", pDialog);
				//CAPTION 
				pvEncodeCaption(pWriter, pDialog);
				
				//TOOLBAR
				pvEncodeToolbar(pContext, pWriter, pDialog);
			pWriter.endElement("thead");
			
			//BODY
			pWriter.startElement("tbody", pDialog);
				//CONTENT and CHILDREN
				pvEncodeMessage(pContext, pWriter, pDialog);
			pWriter.endElement("tbody");

			//FOOTER
			pWriter.startElement("tfoot", pDialog);
				pvEncodeFooter(pContext, pWriter, pDialog);
			pWriter.endElement("tfoot");
		pWriter.endElement("table");
	}

	/**
	 * Título do dialog
	 * @param pWriter
	 * @param pDialog
	 * @throws IOException
	 */
	private void pvEncodeCaption(ResponseWriter pWriter, DBSDialog pDialog) throws IOException{
		String xCaption = null;
		if (pDialog.getCaption()!=null){
			xCaption = pDialog.getCaption();
		}else{
			MESSAGE_TYPE xCT = MESSAGE_TYPE.get(pDialog.getMessageType());
			//Sobre escreve caption padrão a partir do tipo de confirmação
			if (xCT != null){
				xCaption = xCT.getName();
			}
		}

		if (xCaption!=null){
			pWriter.startElement("tr", pDialog);
				pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CAPTION + DBSFaces.CSS.BACK_TEXTURE_BLACK_GRADIENT, null);
				pWriter.startElement("th", pDialog);
					pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
					pWriter.startElement("div", pDialog);
						pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.LABEL + DBSFaces.CSS.NOT_SELECTABLE, null);
						pWriter.write(xCaption);
					pWriter.endElement("div");
				pWriter.endElement("th");
			pWriter.endElement("tr");
		}
	}
	
	/**
	 * Toolbar logo abaixo do título
	 * @param pContext
	 * @param pWriter
	 * @param pDialog
	 * @throws IOException
	 */
	private void pvEncodeToolbar(FacesContext pContext, ResponseWriter pWriter, DBSDialog pDialog) throws IOException{
		UIComponent xToolbar = pDialog.getFacet("toolbar");
		if (xToolbar!=null){
			pWriter.startElement("tr", pDialog);
				pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.TOOLBAR + DBSFaces.CSS.BACK_TEXTURE_WHITE_TRANSPARENT, null);
				pWriter.startElement("th", pDialog);
					pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
					//Conteúdo
					xToolbar.encodeAll(pContext);
				pWriter.endElement("th");
			pWriter.endElement("tr");
		}
	}
	
	/**
	 * Toolbar logo abaixo do título
	 * @param pContext
	 * @param pWriter
	 * @param pDialog
	 * @throws IOException
	 */
	private void pvEncodeMessage(FacesContext pContext, ResponseWriter pWriter, DBSDialog pDialog) throws IOException{
		MESSAGE_TYPE 	xMT = MESSAGE_TYPE.get(pDialog.getMessageType());
		String			xIconId = pDialog.getClientId() + "_icon"; //Id para ser utilizado no tooptip do icon.

		pWriter.startElement("tr", pDialog);
			String xClass = DBSFaces.CSS.MODIFIER.MESSAGE;
			if (xMT == null){
				xClass += DBSFaces.CSS.BACK_GRADIENT_WHITE;
			}else{
				xClass += DBSFaces.CSS.BACK_TEXTURE_BLACK_GRADIENT;
			}
			pWriter.writeAttribute("class",xClass, null);
			pWriter.startElement("td", pDialog);
				pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
				pWriter.startElement("table", pDialog);
					pWriter.writeAttribute("cellspacing", "0px", null);
					pWriter.writeAttribute("cellpadding", "0px", null);
					pWriter.startElement("tbody", pDialog);
						pWriter.startElement("tr", pDialog);
		//					pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT, null);
							//Icone da mensagem
							if (xMT != null){
								pWriter.startElement("td", pDialog);
									pWriter.writeAttribute("colspan", 0, null);
									pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.ICON, null);
		//							pWriter.startElement("div", pDialog);
		//								pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
													pWriter.startElement("div", pDialog);
														pWriter.writeAttribute("id", xIconId, null);
														pWriter.writeAttribute("class", xMT.getIconClass(), null);
														DBSFaces.encodeTooltip(pContext, pDialog, pDialog.getTooltip(), xIconId);
													pWriter.endElement("div");
		//							pWriter.endElement("div");
								pWriter.endElement("td");
							}
							//Conteúdo do dialog/mensagem
							pWriter.startElement("td", pDialog);
								pWriter.writeAttribute("colspan", 0, null);
								pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
								pWriter.startElement("div", pDialog);
									pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT, null);
									DBSFaces.renderChildren(pContext, pDialog);
								pWriter.endElement("div");
							pWriter.endElement("td");
						pWriter.endElement("tr");
					pWriter.endElement("tbody");
				pWriter.endElement("table");
			pWriter.endElement("td");
		pWriter.endElement("tr");

	}
	/**
	 * Rodapé(Onde conterá os botões caso seja uma tela de confirmação)
	 * @param pContext
	 * @param pWriter
	 * @param pDialog
	 * @throws IOException
	 */
	private void pvEncodeFooter(FacesContext pContext, ResponseWriter pWriter, DBSDialog pDialog) throws IOException{

		UIComponent xFooter = pDialog.getFacet("footer");
		
		if (!pvHasFooterAction(pDialog)
		   && xFooter == null){
			return;
		}
		pWriter.startElement("tr", pDialog);
			pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.FOOTER + DBSFaces.CSS.BACK_TEXTURE_BLACK, null);
			//Linha horizontal-------------

			//Conteudo-------------
			pWriter.startElement("td", pDialog);
				pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
				//Linha horizontal-------------
				pWriter.startElement("span", pDialog);
					pWriter.writeAttribute("class", " -line -horizontalLineAfter -black", null);
				pWriter.endElement("span");
					//Botoes padrão ----------------
					if (pvHasFooterAction(pDialog)){
						pWriter.startElement("div", pDialog);
							pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.BUTTON, null);
							if (!DBSObject.isEmpty(pDialog.getNoAction())){
								pvEncodeButton(pContext,pDialog,pDialog.getNoAction(),"btno", "Não","-i_no -red");
							}
							if (!DBSObject.isEmpty(pDialog.getYesAction())){
								pvEncodeButton(pContext,pDialog,pDialog.getYesAction(),"btyes", "Sim","-i_yes -green");
							}
							if (!DBSObject.isEmpty(pDialog.getOkAction())){
								pvEncodeButton(pContext,pDialog,pDialog.getOkAction(),"btok", "Ok","-i_yes -green");
							}
						pWriter.endElement("div");
					}
					//Footer do usuário ----------------
					if (xFooter!=null){
						pWriter.startElement("div", pDialog);
							pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT, null);
							xFooter.encodeAll(pContext);
						pWriter.endElement("div");
					}
			pWriter.endElement("td");
		pWriter.endElement("tr");
	}
	
	private void pvEncodeButton(FacesContext pContext, DBSDialog pDialog, String pMethod, String pId, String pLabel, String pIconClass) throws IOException{
		String		xClientId = pDialog.getClientId(pContext);
		DBSButton 	xBtn = (DBSButton) pDialog.getFacet(pId); 
		//Verifica se botão já havia sido criado
		if (xBtn == null){
			xBtn = (DBSButton) FacesContext.getCurrentInstance().getApplication().createComponent(DBSButton.COMPONENT_TYPE);
			xBtn.setId(pId);
			xBtn.setLabel(pLabel);
			xBtn.setIconClass(DBSFaces.CSS.MODIFIER.ICON + pIconClass);
//			xBtn.setActionExpression(pContext.getApplication().getExpressionFactory().createMethodExpression(pContext.getELContext(), pMethod, String.class, new Class[0]));
			//Se for EL...
			if (pMethod.startsWith("#")){
				xBtn.setActionExpression(DBSFaces.createMethodExpression(pContext, pMethod, String.class, new Class[0]));
//				xBtn.setUpdate(pDialog.getUpdate());
			//Se for um simples redirect para outra página
			}else if (!DBSObject.isEmpty(pMethod)){
				MethodExpression xME = pContext.getApplication().getExpressionFactory().createMethodExpression(pContext.getELContext(), pMethod, String.class, new Class[0]);
				xBtn.setActionExpression(xME);
//				xBtn.setUpdate(null);
			}
			xBtn.setUpdate(pDialog.getUpdate());
			if (DBSObject.isEmpty(pDialog.getExecute())){
				xBtn.setExecute(xClientId);
			}else{
				xBtn.setExecute(pDialog.getExecute());
			}
			//Inclui botão com facet do dialog para poder separa-lo dos componentes filhos criados pelo usuário.
			pDialog.getFacets().put(pId, xBtn);
		}
		xBtn.encodeAll(pContext);
	}

	public boolean pvHasFooterAction(DBSDialog pDialog){
		if (DBSObject.isEmpty(pDialog.getNoAction())
		 && DBSObject.isEmpty(pDialog.getYesAction())
		 && DBSObject.isEmpty(pDialog.getOkAction())){
			return false;
		}
		return true;
	}
}



