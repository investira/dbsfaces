package br.com.dbsoft.ui.component.modal;

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
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSModal.RENDERER_TYPE)
public class DBSModalRenderer extends DBSRenderer {

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSModal 			xModal = (DBSModal) pComponent;
		ResponseWriter 		xWriter = pContext.getResponseWriter();
		String 				xClientId = xModal.getClientId(pContext);
		String 				xClass = CSS.MODAL.MAIN + " ";
		String 				xStyle = "";
		MESSAGE_TYPE 		xMT = MESSAGE_TYPE.get(xModal.getMessageType());
		if (xModal.getWidth() != null
		 && xModal.getWidth() != 0) {
			xStyle += "width:" + xModal.getWidth() + "px;";
		}
		if (xModal.getHeight() != null
		 && xModal.getHeight() != 0) {
			xStyle += "height:" + xModal.getHeight() + "px;";
		}
		
		//Altera título padrão caso tenha sido informado
		if (xMT != null){
			xClass += " -confirmation ";
		}

		//Define style do modal
		if (xModal.getStyle() != null){
			xStyle += xModal.getStyle();
		}
		
		UIComponent xModalMessage = xModal.getFacet("modalMessages");
	
		//Mascará de fundo
		xWriter.startElement("div", xModal);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			//MODAL
			xWriter.startElement("div", xModal);
				xClass = "-modal ";
				if (xModal.getStyleClass()!=null){
					xClass += xModal.getStyleClass();
				}
				DBSFaces.encodeAttribute(xWriter, "class", xClass); 
				DBSFaces.encodeAttribute(xWriter, "style", xStyle);
				xWriter.startElement("div", xModal);
					DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER); 

					pvEncodeTable(pContext, xWriter, xModal);
				
				xWriter.endElement("div");

			xWriter.endElement("div");	
			//Javascript 
			DBSFaces.encodeJavaScriptTagStart(pComponent, xWriter);
			String xJS = "$(document).ready(function() { \n" +
					     " var xModalId = dbsfaces.util.jsid('" + xClientId + "'); \n " + 
					     " dbs_modal(xModalId); \n" +
	                     "}); \n"; 
			xWriter.write(xJS);
			DBSFaces.encodeJavaScriptTagEnd(xWriter);
			//ModalMessage
			if (xModalMessage!=null){
				xModalMessage.encodeAll(pContext);
			}
		xWriter.endElement("div");

	}

	private void pvEncodeTable(FacesContext pContext, ResponseWriter pWriter, DBSModal pModal) throws IOException{
		pWriter.startElement("table", pModal);
			DBSFaces.encodeAttribute(pWriter, "cellspacing", "0px");
			DBSFaces.encodeAttribute(pWriter, "cellpadding", "0px");
			
			//HEADER
			pWriter.startElement("thead", pModal);
				//CAPTION 
				pvEncodeCaption(pWriter, pModal);
				
				//TOOLBAR
				pvEncodeToolbar(pContext, pWriter, pModal);
			pWriter.endElement("thead");
			
			//BODY
			pWriter.startElement("tbody", pModal);
				//CONTENT and CHILDREN
				pvEncodeMessage(pContext, pWriter, pModal);
			pWriter.endElement("tbody");

			//FOOTER
			pWriter.startElement("tfoot", pModal);
				pvEncodeFooter(pContext, pWriter, pModal);
			pWriter.endElement("tfoot");
		pWriter.endElement("table");
	}

	/**
	 * Título do modal
	 * @param pWriter
	 * @param pModal
	 * @throws IOException
	 */
	private void pvEncodeCaption(ResponseWriter pWriter, DBSModal pModal) throws IOException{
		String xCaption = null;
		if (pModal.getCaption()!=null){
			xCaption = pModal.getCaption();
		}else{
			MESSAGE_TYPE xCT = MESSAGE_TYPE.get(pModal.getMessageType());
			//Sobre escreve caption padrão a partir do tipo de confirmação
			if (xCT != null){
				xCaption = xCT.getName();
			}
		}

		if (xCaption!=null){
			pWriter.startElement("tr", pModal);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CAPTION + CSS.BACK_TEXTURE_BLACK_GRADIENT);
				pWriter.startElement("th", pModal);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
					pWriter.startElement("div", pModal);
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL + CSS.NOT_SELECTABLE);
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
	 * @param pModal
	 * @throws IOException
	 */
	private void pvEncodeToolbar(FacesContext pContext, ResponseWriter pWriter, DBSModal pModal) throws IOException{
		UIComponent xToolbar = pModal.getFacet("toolbar");
		if (xToolbar!=null){
			pWriter.startElement("tr", pModal);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.TOOLBAR + CSS.BACK_TEXTURE_WHITE_TRANSPARENT);
				pWriter.startElement("th", pModal);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
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
	 * @param pModal
	 * @throws IOException
	 */
	private void pvEncodeMessage(FacesContext pContext, ResponseWriter pWriter, DBSModal pModal) throws IOException{
		MESSAGE_TYPE 	xMT = MESSAGE_TYPE.get(pModal.getMessageType());
		String			xIconId = pModal.getClientId() + "_icon"; //Id para ser utilizado no tooptip do icon.

		pWriter.startElement("tr", pModal);
			String xClass = CSS.MODIFIER.MESSAGE;
			if (xMT == null){
				xClass += CSS.BACK_GRADIENT_WHITE;
			}else{
				xClass += CSS.BACK_TEXTURE_BLACK_GRADIENT;
			}
			DBSFaces.encodeAttribute(pWriter, "class",xClass);
			pWriter.startElement("td", pModal);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
				pWriter.startElement("table", pModal);
					DBSFaces.encodeAttribute(pWriter, "cellspacing", "0px");
					DBSFaces.encodeAttribute(pWriter, "cellpadding", "0px");
					pWriter.startElement("tbody", pModal);
						pWriter.startElement("tr", pModal);
		//					pWriter.writeAttribute("class", CSS.MODIFIER.CONTENT, null);
							//Icone da mensagem
							if (xMT != null){
								pWriter.startElement("td", pModal);
									DBSFaces.encodeAttribute(pWriter, "colspan", 0);
									DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON);
		//							pWriter.startElement("div", pDialog);
		//								pWriter.writeAttribute("class", CSS.MODIFIER.CONTAINER, null);
													pWriter.startElement("div", pModal);
														DBSFaces.encodeAttribute(pWriter, "id", xIconId);
														DBSFaces.encodeAttribute(pWriter, "class", xMT.getIconClass());
														DBSFaces.encodeTooltip(pContext, pModal, pModal.getTooltip(), xIconId);
													pWriter.endElement("div");
		//							pWriter.endElement("div");
								pWriter.endElement("td");
							}
							//Conteúdo do modal/mensagem
							pWriter.startElement("td", pModal);
								DBSFaces.encodeAttribute(pWriter, "colspan", 0);
								DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
								pWriter.startElement("div", pModal);
									DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
									DBSFaces.renderChildren(pContext, pModal);
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
	 * @param pModal
	 * @throws IOException
	 */
	private void pvEncodeFooter(FacesContext pContext, ResponseWriter pWriter, DBSModal pModal) throws IOException{

		UIComponent xFooter = pModal.getFacet("footer");
		
		if (!pvHasFooterAction(pModal)
		   && xFooter == null){
			return;
		}
		pWriter.startElement("tr", pModal);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.FOOTER + CSS.BACK_TEXTURE_BLACK);
			//Linha horizontal-------------

			//Conteudo-------------
			pWriter.startElement("td", pModal);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
				//Linha horizontal-------------
				pWriter.startElement("span", pModal);
					DBSFaces.encodeAttribute(pWriter, "class", " -line -horizontalLineAfter -black");
				pWriter.endElement("span");
					//Botoes padrão ----------------
					if (pvHasFooterAction(pModal)){
						pWriter.startElement("div", pModal);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.BUTTON);
							if (!DBSObject.isEmpty(pModal.getNoAction())){
								pvEncodeButton(pContext,pModal,pModal.getNoAction(),"btno", "Não","-i_no -red");
							}
							if (!DBSObject.isEmpty(pModal.getYesAction())){
								pvEncodeButton(pContext,pModal,pModal.getYesAction(),"btyes", "Sim","-i_yes -green");
							}
							if (!DBSObject.isEmpty(pModal.getOkAction())){
								pvEncodeButton(pContext,pModal,pModal.getOkAction(),"btok", "Ok","-i_yes -green");
							}
						pWriter.endElement("div");
					}
					//Footer do usuário ----------------
					if (xFooter!=null){
						pWriter.startElement("div", pModal);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
							xFooter.encodeAll(pContext);
						pWriter.endElement("div");
					}
			pWriter.endElement("td");
		pWriter.endElement("tr");
	}
	
	private void pvEncodeButton(FacesContext pContext, DBSModal pModal, String pMethod, String pId, String pLabel, String pIconClass) throws IOException{
		String		xClientId = pModal.getClientId(pContext);
		DBSButton 	xBtn = (DBSButton) pModal.getFacet(pId); 
		//Verifica se botão já havia sido criado
		if (xBtn == null){
			xBtn = (DBSButton) FacesContext.getCurrentInstance().getApplication().createComponent(DBSButton.COMPONENT_TYPE);
			xBtn.setId(pId);
			xBtn.setLabel(pLabel);
			xBtn.setIconClass(CSS.MODIFIER.ICON + pIconClass);
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
			xBtn.setUpdate(pModal.getUpdate());
			if (DBSObject.isEmpty(pModal.getExecute())){
				xBtn.setExecute(xClientId);
			}else{
				xBtn.setExecute(pModal.getExecute());
			}
			//Inclui botão com facet do modal para poder separa-lo dos componentes filhos criados pelo usuário.
			pModal.getFacets().put(pId, xBtn);
		}
		xBtn.encodeAll(pContext);
	}

	public boolean pvHasFooterAction(DBSModal pModal){
		if (DBSObject.isEmpty(pModal.getNoAction())
		 && DBSObject.isEmpty(pModal.getYesAction())
		 && DBSObject.isEmpty(pModal.getOkAction())){
			return false;
		}
		return true;
	}
}



