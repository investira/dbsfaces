package br.com.dbsoft.ui.component.inputtext;

import java.io.IOException;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.core.DBSSDK;
import br.com.dbsoft.io.DBSResultDataModel;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.datatable.DBSDataTable;
import br.com.dbsoft.ui.component.datatable.DBSDataTableColumn;
import br.com.dbsoft.ui.component.div.DBSDiv;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSInputText.RENDERER_TYPE)
public class DBSInputTextRenderer extends DBSRenderer {
	
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSInputText xInputText = (DBSInputText) pComponent;
        if(xInputText.getReadOnly()) {return;}

    	decodeBehaviors(pContext, xInputText); 

		String xValueClientId; 
		
		//Utiliza o valor da chave da sugestão como valor recebido
		if (xInputText.hasSuggestion()){
			//Se foi o submit do botão de refresh, chama o método para fazer o refresh da lista passando o valor digitado
			if (RenderKitUtils.isPartialOrBehaviorAction(pContext, pvGetButtonId(xInputText, true))){
				//Recupera valor digitado se tiver sido enviado
				xValueClientId = getInputDataClientId(xInputText);
				if (pContext.getExternalContext().getRequestParameterMap().containsKey(xValueClientId)){
					String xDisplayValue = pContext.getExternalContext().getRequestParameterMap().get(xValueClientId);
					//Chama o método refresh se o valor digitado não for nulo
			        if(xDisplayValue != null) {
			        	pvSearchList(pContext, xInputText, xDisplayValue);
			        }
				}
			}else{
				//Recupera o valor da chave(key) e seta como submittedvalue
				xValueClientId = pvGetSuggestionKeyId(xInputText, true);
				if (pContext.getExternalContext().getRequestParameterMap().containsKey(xValueClientId)){
					Object xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xValueClientId);
					//Se valor recebido for igual o valor considerado como nulo ou o vázio
					//Envia o valor como nulo
					if (xSubmittedValue.equals(DBSSDK.UI.COMBOBOX.NULL_VALUE) ||
						(xInputText.getSuggestionNullText()!=null &&
						 xSubmittedValue.toString().toUpperCase().equals(xInputText.getSuggestionNullText().toUpperCase()))){
						xInputText.setValue(null);
						xInputText.setSubmittedValue(null);
					}else{
						xInputText.setSubmittedValue(xSubmittedValue); 
					}
				}
			}
		}else{
			//Utiliza o próprio valor recebido 
			xValueClientId = getInputDataClientId(xInputText);
			if (pContext.getExternalContext().getRequestParameterMap().containsKey(xValueClientId)){
				String xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xValueClientId);
		        if(xSubmittedValue != null) {
		            xInputText.setSubmittedValue(xSubmittedValue);
		        }
			}
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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}

		DBSInputText xInputText = (DBSInputText) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xInputText.getClientId(pContext);
		String xClass = CSS.INPUTTEXT.MAIN + CSS.THEME.INPUT; 
		if (xInputText.getStyleClass()!=null){
			xClass = xClass + xInputText.getStyleClass();
		}
		xWriter.startElement("div", xInputText);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xInputText.getStyle(), null);
			//Container
			xWriter.startElement("div", xInputText);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
					DBSFaces.encodeLabel(pContext, xInputText, xWriter);
					pvEncodeInput(pContext, xInputText, xWriter);
					DBSFaces.encodeRightLabel(pContext, xInputText, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xInputText, xInputText.getTooltip());
		xWriter.endElement("div");
		if (!xInputText.getReadOnly()){
			pvEncodeJS(xClientId, xWriter);
		}
	}
	
	private void pvEncodeInput(FacesContext pContext, DBSInputText pInputText, ResponseWriter pWriter) throws IOException{
		String xClientId = pInputText.getClientId();
		String xClientIdData = getInputDataClientId(pInputText);
		String xClientIdSuggestionKey = pvGetSuggestionKeyId(pInputText, true);
		String xClientIdButton = pvGetButtonId(pInputText, false);
		String xClientIdSuggestion = xClientId + CSS.MODIFIER.SUGGESTION.trim();
		String xClientAjaxUpdate = pvGetListId(pInputText, true);//pvGetListClientId(pInputText); //pvGetDataTableClientId(pInputText);
		String xStyle = "";
		String xValue = "";
		String xValueKey = "";
		String xClass = DBSFaces.getInputDataClass(pInputText) + " -"+ pInputText.getLetterCase().toLowerCase();
		
		//Seta valor que será exibido e armazena valor da respectiva chave
		if (pInputText.getValue() != null){			
			xValue = pInputText.getValue().toString();
			xValueKey = xValue;
			//Recupera o respectivo valor a partir da chave
			if (pInputText.hasSuggestion()){
				//Recupera a string que será exibida, a partir da chave informada
				xValue = pvSetDisplayValue(pContext, pInputText, xValue);
			}
		}else{
			if (pInputText.hasSuggestion() &&
				DBSObject.getNotEmpty(pInputText.getSuggestionNullText(), null) != null){
				xValue = pInputText.getSuggestionNullText();
			}
		}
		//Define a largura do campo
		xStyle = DBSFaces.getCSSStyleWidthFromInputSize(pInputText.getSize());

		//Se for somente leitura, gera código como <Span>
		if (pInputText.getReadOnly()){ 
			DBSFaces.encodeInputDataReadOnly(pInputText, pWriter, xClientIdData, false, xValue, pInputText.getSize(), null, xStyle);
		}else{
			if (pInputText.hasSuggestion()){ 
				pWriter.startElement("div", pInputText);
					DBSFaces.setAttribute(pWriter, "class", "-input");
					//Campo escondido que receberá a chave resultado da pesquisa ajax
					pWriter.startElement("input", pInputText);
						DBSFaces.setAttribute(pWriter, "id", xClientIdSuggestionKey);
						DBSFaces.setAttribute(pWriter, "name", xClientIdSuggestionKey);
						DBSFaces.setAttribute(pWriter, "type", "hidden");
						DBSFaces.setAttribute(pWriter, "class", CSS.INPUT.SUGGESTIONKEY);
						DBSFaces.setAttribute(pWriter, "value", xValueKey);
						DBSFaces.setAttribute(pWriter, "key", xValueKey);
						DBSFaces.setAttribute(pWriter, "nulltext", pInputText.getSuggestionNullText());
					pWriter.endElement("input");	

					//Icon que indica para indicar que é possível efetuar pesquisa a partir do texto digitado
					pWriter.startElement("div", pInputText);
						DBSFaces.setAttribute(pWriter, "class", "-small -i_find");
					pWriter.endElement("div");	

					//Campo que receberá o conteúdo integral ou parcial do campo escondido ajax. 
					pWriter.startElement("input", pInputText);
						DBSFaces.setAttribute(pWriter, "id", xClientIdSuggestion);
						DBSFaces.setAttribute(pWriter, "name", xClientIdSuggestion);
						DBSFaces.setAttribute(pWriter, "type", pInputText.getType());
						if (pInputText.getSize()!=0){
							DBSFaces.setAttribute(pWriter, "size", pInputText.getSize());
						}
						if (!xStyle.equals("")){
							DBSFaces.setAttribute(pWriter, "style", xStyle);
						}						
						DBSFaces.setAttribute(pWriter, "autocomplete", "off");
						DBSFaces.setAttribute(pWriter, "class", CSS.INPUT.SUGGESTION);
						DBSFaces.setAttribute(pWriter, "tabindex", "-1");
						DBSFaces.setAttribute(pWriter, "value", xValue);
					pWriter.endElement("input");
					
					//Botão que efetuará a chamada ajax
					if (pInputText.hasSuggestion()){
						DBSButton xBtn = (DBSButton) DBSFaces.findComponent(xClientIdButton, pInputText.getChildren());
						if (xBtn == null){
							xBtn = (DBSButton) pContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
						}
						xBtn.setId(xClientIdButton);
						xBtn.setStyleClass(CSS.INPUT.SUBMIT); 
						//xBtn.setActionExpression(DBSFaces.createMethodExpression(pContext, pInputText.getSuggestionSearchAction(), String.class, new Class[0]));
						if (pInputText.getSuggestionUpdate()!=null){
							xBtn.setUpdate(xClientAjaxUpdate + " " + pInputText.getSuggestionUpdate());
						}else{
							xBtn.setUpdate(xClientAjaxUpdate);
						}
						xBtn.setExecute(xClientId);
						pInputText.getChildren().add(xBtn);
						xBtn.encodeAll(pContext);
					}
			}
			//Campo Input
			pWriter.startElement("input", pInputText);
				DBSFaces.setAttribute(pWriter, "id", xClientIdData);
				DBSFaces.setAttribute(pWriter, "name", xClientIdData);
				if (pInputText.getSecret()){
					DBSFaces.setAttribute(pWriter, "type", "password");
				}else{
					DBSFaces.setAttribute(pWriter, "type", pInputText.getType());
				}
				if (pInputText.hasSuggestion() ||
					pInputText.getAutocomplete().toLowerCase().equals("off") ||
					pInputText.getAutocomplete().toLowerCase().equals("false")){
					DBSFaces.setAttribute(pWriter, "autocomplete", "off", null);
				}
				DBSFaces.setAttribute(pWriter, "placeHolder", pInputText.getPlaceHolder());
				DBSFaces.setAttribute(pWriter, "class", xClass);
				DBSFaces.setSizeAttributes(pWriter, pInputText.getSize(), null);
				if (!xStyle.equals("")){
					DBSFaces.setAttribute(pWriter, "style", xStyle, null);
				}
				//Grava a largura do campo
				if (pInputText.getSize()!=0){
					DBSFaces.setAttribute(pWriter, "size", pInputText.getSize());
				}
				if (pInputText.getMaxLength()!=0){
					DBSFaces.setAttribute(pWriter, "maxlength", pInputText.getMaxLength(), null);
				}
				DBSFaces.setAttribute(pWriter, "value", xValue, "");
				encodeClientBehaviors(pContext, pInputText);
//				encodeClientParameters(pContext, pInputText);
			pWriter.endElement("input");
			
			//Se tiver suggestion
			if (pInputText.hasSuggestion()){
				//Encode List --------------------------------------------------
				pvEncodeList(pContext, pInputText, pWriter);
				pWriter.endElement("div");
			}
		}
	}
	
	
	/**
	 * Encode da lista de valores da sugestão
	 * @param pContext
	 * @param pInputText
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeList(FacesContext pContext, DBSInputText pInputText, ResponseWriter pWriter) throws IOException{
		DBSDiv xDiv = (DBSDiv) DBSFaces.findComponent(pvGetListId(pInputText, false), pInputText.getChildren());
		String xVar = "list";
		if (xDiv == null){
			xDiv = (DBSDiv) pContext.getApplication().createComponent(DBSDiv.COMPONENT_TYPE);
			xDiv.setStyleClass("-list");
			xDiv.setStyle("display:none;");//Esconde lista. Deixando para o JS a função de exibir
			xDiv.setId(pvGetListId(pInputText, false));
			//Cria dataTable
			DBSDataTable xDT = (DBSDataTable) pContext.getApplication().createComponent(DBSDataTable.COMPONENT_TYPE);
				xDT.setId(pvGetDataTableId(pInputText));
				xDT.setValueExpression("value", DBSFaces.createValueExpression(pContext, pInputText.getValueExpression(DBSInputText.PropertyKeys.suggestionsBean.name()).getExpressionString() + ".list", DBSResultDataModel.class)); 
				xDT.setKeyColumnName(pInputText.getSuggestionKeyColumnName());
				xDT.setVar(xVar);
	
				//Se existir, exibe as colunas de sugestão definidas manualmente pelo usuário
				boolean	xIsUsersColumns = false;
				int		xY = 0;
				int 	xZ = pInputText.getChildren().size(); //É necessário guarda o quantidade antes do loop, pois a quantidade é reduzida dinamicamente após cada getChildren().add()
				for (int xX = 0; xX < xZ; xX++){
					UIComponent xComponent = pInputText.getChildren().get(xY);
					if (xComponent instanceof DBSDataTableColumn){
						DBSDataTableColumn xC = (DBSDataTableColumn) xComponent;
						xIsUsersColumns = true;
						xDT.getChildren().add(xC);
					}else{
						xY++; 
					}
				}

				String xDisplayValue = pInputText.getValueExpression(DBSInputText.PropertyKeys.suggestionsBean.name()).getExpressionString() + ".getDisplayValue()";

				/*
				 * Adiciona coluna que contém o valor a exibido na seleção selecionado. 
				 * Caso não haja coluna definidas manualmente, ela será utulizada para exibir os valores a serem selecionados
				 */

				DBSDataTableColumn xDTC = (DBSDataTableColumn) pContext.getApplication().createComponent(DBSDataTableColumn.COMPONENT_TYPE);
					xDTC.setWidth("100%");
					xDTC.setId("dv"); //Coluna default
					//class para esconder exibição da coluna, pois 
					xDTC.setStyleClass("-dv");
					if (xIsUsersColumns){
						xDTC.setStyle("display:none;");
					}
					//Valor padrão da coluna
					UIOutput xValue = (UIOutput) pContext.getApplication().createComponent(UIOutput.COMPONENT_TYPE);

					xValue.setValueExpression("value", DBSFaces.createValueExpression(pContext,  xDisplayValue, String.class));
					xDTC.getChildren().add(xValue);
				xDT.getChildren().add(xDTC);
					
				xDiv.getChildren().add(xDT);

				
			pInputText.getChildren().add(xDiv);
		}
		xDiv.encodeAll(pContext);
	}	
	

	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xInputTextId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_inputText(xInputTextId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}

	
	private String pvGetDataTableId(UIComponent pInputText){
		return pInputText.getId() + "-dataTable";
	}

	private String pvGetSuggestionKeyId(UIComponent pInputText, boolean pFullId){
		return pvGetId(pInputText, CSS.MODIFIER.SUGGESTION.trim() + CSS.MODIFIER.KEY.trim(), pFullId);
	}

	private String pvGetListId(UIComponent pInputText, boolean pFullId){
		return pvGetId(pInputText, "-list", pFullId);
	}

	
	private String pvGetButtonId(DBSInputText pInputText, boolean pFullId){
		return pvGetId(pInputText, CSS.MODIFIER.SUBMIT.trim(), pFullId);
	}
	
	private String pvGetId(UIComponent pInputText, String pSufix, boolean pFullId){
		String xId;
		if (pFullId){
			xId = pInputText.getClientId() + pSufix;
		}else{
			xId = pInputText.getId() + pSufix;
		}
		return xId;
	}
	
	/**
	 * Executa o metódo que atualiza a lista 
	 * @param pContext
	 * @param pInputText
	 * @param pString
	 */
	private void pvSearchList(FacesContext pContext, DBSInputText pInputText, String pString){
    	String[] xParms = new String[1]; 
    	xParms[0] = pString;
        MethodExpression xME = DBSFaces.createMethodExpression(pContext, pInputText.getValueExpression(DBSInputText.PropertyKeys.suggestionsBean.name()).getExpressionString() + ".searchList", null, new Class[]{String.class}); 
        xME.invoke(pContext.getELContext(), xParms);
    }

	/**
	 * Executa o método que retorna o valor a partir da chave informada
	 * @param pContext
	 * @param pInputText
	 * @param pString
	 * @return
	 */
	private String pvSetDisplayValue(FacesContext pContext, DBSInputText pInputText, String pString){
    	String[] xParms = new String[1]; 
    	String 	 xDisplayValue;
    	xParms[0] = pString;
        MethodExpression xME = DBSFaces.createMethodExpression(pContext, pInputText.getValueExpression(DBSInputText.PropertyKeys.suggestionsBean.name()).getExpressionString() + ".setDisplayValue", String.class, new Class[]{String.class}); 
        xDisplayValue = (String) xME.invoke(pContext.getELContext(), xParms);
        if (xDisplayValue == null){
        	xDisplayValue = DBSSDK.UI.COMBOBOX.NULL_VALUE;
        }
        //Atualiza os itens da lista de sugestão somente quando a edição do campo estiver habilitada(not readOnly)
        if (!pInputText.getReadOnly()){
        	pvSearchList(pContext, pInputText, xDisplayValue);
        }
        return xDisplayValue;
    }

}





