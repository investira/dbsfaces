package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;
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
		DBSDialog 		xDialog = (DBSDialog) pComponent;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClientId = xDialog.getClientId(pContext);
		String			xIconId = xClientId + "_icon";
		String 			xClass = DBSFaces.CSS.DIALOG.MAIN + " ";
		String 			xMsgIcon = "";
		String 			xCaption = DBSFaces.getCaptionFromIcon(xDialog.getMessageIcon());
		String 			xStyle = " width:" + xDialog.getWidth() + "px; height:" + xDialog.getHeight() + "px;";
		//Altera título padrão caso tenha sido informado
		if (xDialog.getCaption()!=null){
			xCaption = xDialog.getCaption();
		}
		int xCount = 0 ;
		if (xDialog.getStyleClass()!=null){
			xClass = xClass + xDialog.getStyleClass();
		}
		
		xCount = pvDialogCount(pComponent.getChildren(),0);
		
		UIComponent xToolbar = xDialog.getFacet("toolbar");
		UIComponent xDialogMessage = xDialog.getFacet("dialogMessages");
	
		// STYLE 
		if (xDialog.getWidth() == 0 || 
			xDialog.getHeight() == 0) {
			wLogger.error("Altura e/ou largura do componente " + xClientId + " não foi definida!");
		}
		
		//DIALOG
		xWriter.startElement("div", xDialog);
			xWriter.writeAttribute("id", xClientId, null);
			xWriter.writeAttribute("name", xClientId, null);
			xWriter.writeAttribute("class", xClass, null);
			xWriter.writeAttribute("index", xCount, null);

			xWriter.startElement("div", xDialog);
				xWriter.writeAttribute("style", xStyle + xDialog.getStyle(), null);
				//CAPTION 
				if (xCaption!=null){
					xWriter.startElement("div", xDialog);
						xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CAPTION + " " + DBSFaces.CSS.BACK_TEXTURE_BLACK_GRADIENT, null);
						xWriter.startElement("div", xDialog);
							xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.LABEL, null);
							xWriter.startElement("span", xDialog);
								xWriter.writeAttribute("class", DBSFaces.CSS.NOT_SELECTABLE, null);
								xWriter.write(xCaption);
							xWriter.endElement("span");
						xWriter.endElement("div");
					xWriter.endElement("div");
				}
				//TOOLBAR
				if (xToolbar!=null){
					xWriter.startElement("div", xDialog);
						xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.TOOLBAR, null);
						xToolbar.encodeAll(pContext);
					xWriter.endElement("div");
				}
				//CHILDREN - CONTENT
				xWriter.startElement("div", xDialog);
					if (xDialog.getMessageIcon().equals(DIALOG_ICON.NENHUM.toString())){
						xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT + DBSFaces.CSS.BACK_GRADIENT_WHITE, null);
						renderChildren(pContext, xDialog);
					}else{
						xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT + DBSFaces.CSS.MODIFIER.MESSAGE + DBSFaces.CSS.BACK_TEXTURE_BLACK_GRADIENT, null);
						if (xDialog.getMessageIcon().equals(DIALOG_ICON.INFORMACAO.toString())){
							xMsgIcon = "-il_information";
						}else if (xDialog.getMessageIcon().equals(DIALOG_ICON.ATENCAO.toString())){
							xMsgIcon = "-il_warning";
						}else if (xDialog.getMessageIcon().equals(DIALOG_ICON.ERRO.toString())){
							xMsgIcon = "-il_error";
						}else if (xDialog.getMessageIcon().equals(DIALOG_ICON.CONFIRMAR.toString())){
							xMsgIcon = "-il_question_confirm";
						}else if (xDialog.getMessageIcon().equals(DIALOG_ICON.IGNORAR.toString())){
							xMsgIcon = "-il_question_ignore";
						}else if (xDialog.getMessageIcon().equals(DIALOG_ICON.PROIBIDO.toString())){
							xMsgIcon = "-il_forbidden";
						}else if (xDialog.getMessageIcon().equals(DIALOG_ICON.SOBRE.toString())){
							xMsgIcon = "-il_about";
						}else if (xDialog.getMessageIcon().equals(DIALOG_ICON.SUCESSO.toString())){
							xMsgIcon = "-il_yes";
						}else if (xDialog.getMessageIcon().equals(DIALOG_ICON.IMPORTANTE.toString())){
							xMsgIcon = "-il_important";
						}
						xWriter.startElement("div", xDialog);
							xWriter.writeAttribute("class", DBSFaces.CSS.DIALOG.CONFIRMATION, null);
							xWriter.startElement("div", xDialog);
								xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT, null);
								
								//ICONE-------------------
								xWriter.startElement("div", xDialog);
									xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.ICON, null);
									xWriter.startElement("div", xDialog);
										xWriter.writeAttribute("id", xIconId, null);
										xWriter.writeAttribute("style", "display:none;", null);
										xWriter.writeAttribute("class", DBSFaces.CSS.ICONLARGE + xMsgIcon, null);
										//Encode do tooltip é em cima do icone
										DBSFaces.encodeTooltip(pContext, xDialog, xDialog.getTooltip(), xIconId);
									xWriter.endElement("div");
								xWriter.endElement("div");

								//TEXTO-------------------
								xWriter.startElement("div", xDialog);
									xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT, null);
									xWriter.startElement("div", xDialog);
										xWriter.writeAttribute("style", "display:none;", null);
										//Encode dos filhos
										renderChildren(pContext, xDialog);
									xWriter.endElement("div");
								xWriter.endElement("div");
							xWriter.endElement("div");
							//Linha horizontal-------------
							xWriter.startElement("div", xDialog);
								xWriter.writeAttribute("class", DBSFaces.CSS.HORIZONTAL_LINE, null);
							xWriter.endElement("div");
							//Botoes padrão ----------------
							xWriter.startElement("div", xDialog);
								xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.BUTTON, null);
								if (!DBSObject.isEmpty(xDialog.getNoAction())){
									pvEncodeButton(pContext,xDialog,xDialog.getNoAction(),"btno", "Não","-i_no");
								}
								if (!DBSObject.isEmpty(xDialog.getYesAction())){
									pvEncodeButton(pContext,xDialog,xDialog.getYesAction(),"btyes", "Sim","-i_yes");
								}
								if (!DBSObject.isEmpty(xDialog.getOkAction())){
									pvEncodeButton(pContext,xDialog,xDialog.getOkAction(),"btok", "Ok","-i_yes");
								}
							xWriter.endElement("div");
						xWriter.endElement("div");
					}
				xWriter.endElement("div");
				//Javascript 
				DBSFaces.encodeJavaScriptTagStart(xWriter);
				String xJS = "$(document).ready(function() { \n" +
						     " var xDialogId = '#' + dbsfaces.util.jsid('" + xClientId + "'); \n " + 
						     " dbs_dialog(xDialogId); \n" +
		                     "}); \n"; 
				xWriter.write(xJS);
				DBSFaces.encodeJavaScriptTagEnd(xWriter);
				//DialogMessage
				if (xDialogMessage!=null){
					xDialogMessage.encodeAll(pContext);
				}

			xWriter.endElement("div");
		xWriter.endElement("div");

	}

	private void pvEncodeButton(FacesContext pContext, DBSDialog pDialog, String pMethod, String pId, String pLabel, String pIconClass) throws IOException{
		String		xClientId = pDialog.getClientId(pContext);
		DBSButton 	xBtn = (DBSButton) pDialog.getFacet(pId); 
		//Verifica se botão já havia sido criado
		if (xBtn == null){
			xBtn = (DBSButton) FacesContext.getCurrentInstance().getApplication().createComponent(DBSButton.COMPONENT_TYPE);
			xBtn.setId(pId);
			xBtn.setLabel(pLabel);
			xBtn.setIconClass(DBSFaces.CSS.ICON + pIconClass);
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
	
	private static int pvDialogCount(List<UIComponent> pComponents, int pCount){
	    for (UIComponent xComponent: pComponents) {
	    	if (xComponent.getClass().equals(DBSDialog.class)){
	    		pCount++;
	    	}
	    	pCount = pvDialogCount(xComponent.getChildren(), pCount);
	    }
	    return pCount;
	}
}



