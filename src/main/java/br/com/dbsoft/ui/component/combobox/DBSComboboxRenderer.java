package br.com.dbsoft.ui.component.combobox;


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.core.DBSSDK.UI.COMBOBOX;
import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSCombobox.RENDERER_TYPE)
public class DBSComboboxRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSCombobox xCombobox = (DBSCombobox) pComponent;
        if(xCombobox.getReadOnly()) {return;}

    	decodeBehaviors(pContext, xCombobox);
    	
		String xClientIdAction = getInputDataClientId(xCombobox);
		
		if (pContext.getExternalContext().getRequestParameterMap().containsKey(xClientIdAction)){
			Object xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientIdAction);
			//Se valor recebido for igual o valor considerado como nulo ou o vázio 
			//Envia o valor como nulo
			if (xSubmittedValue.equals(COMBOBOX.NULL_VALUE)){ 
				xCombobox.setValue(null);
				xCombobox.setSubmittedValue(null);
			}else{
				xCombobox.setSubmittedValue(xSubmittedValue); 
			}			
		}
	}	

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSCombobox xCombobox = (DBSCombobox) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xCombobox.getClientId(pContext);
		String xClass = CSS.COMBOBOX.MAIN + CSS.THEME.INPUT;
		if (xCombobox.getStyleClass()!=null){
			xClass += xCombobox.getStyleClass();
		}
		xWriter.startElement("div", xCombobox);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xCombobox.getStyle());
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCombobox, DBSPassThruAttributes.getAttributes(Key.COMBOBOX));
			
			xWriter.startElement("div", xCombobox);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
					DBSFaces.encodeLabel(pContext, xCombobox, xWriter);
					pvEncodeInput(xCombobox, xWriter);
					DBSFaces.encodeRightLabel(pContext, xCombobox, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xCombobox, xCombobox.getTooltip());

			if (!xCombobox.getReadOnly()){
				pvEncodeJS(xCombobox, xWriter);
			}
		xWriter.endElement("div");
	}

	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
					     " var xComboboxId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
					     " dbs_combobox(xComboboxId); \n" +
		                "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}

	private void pvEncodeInput(DBSCombobox pCombobox, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pCombobox);
		String xStyle = "";
		Object xValueKey = "";
		String xValue = "";
		LinkedHashMap<Object, Object> xList = pCombobox.getList();
		
		//Se não foi informada a lista ou lista estiver vazia
		if (xList == null || 
			xList.size() == 0){
			DBSFaces.encodeInputDataReadOnly(pCombobox, pWriter, xClientId, false, null, pCombobox.getSize());
			return;
		}
		
		//Recupera valor do item selecionado
		xValueKey = pCombobox.getValue();
		//Converte o valor para o mesmo tipo da chave utilizada na lista para garantir que a 
		//comparação para verificar se a chave existe será efetuados com valores do mesmo tipo de class
		if (xValueKey != null){
			Iterator<Object> xListKeyIterator = xList.keySet().iterator();
			Class<?> xValueKeyClass = xValueKey.getClass();
			Object xListKeyValue;
			//Pega primeiro item que não seja o valor nulo para poder verificar o seu tipo de class
			while (xListKeyIterator.hasNext()){
				xListKeyValue = xListKeyIterator.next();
				if (!xListKeyValue.equals(COMBOBOX.NULL_VALUE)){
					xValueKeyClass = xListKeyValue.getClass();
					break;
				}
			}
			//Converte para o tipo
			xValueKey = DBSObject.toClassValue(xValueKey, xValueKeyClass, null);
		}
		//Busca item na lista para recuperar o valor que será utlizado para exibir a informação
		xValue = DBSString.toString(xList.get(xValueKey), null);
		
		//Se não achou item, tenta setar como item nulo(NULL_VALUE), caso faça parte da lista.
		//e se também não encontrar, tenta setar como o primeiro item da lista, se houver algum
		if (xValue==null){
			//Utiliza o valor vázio ao invés do null, por não existir null em html
			xValueKey = COMBOBOX.NULL_VALUE;
			//Busca item na lista para recuperar o valor que será utlizado para exibir a informação
			xValue = DBSString.toString(xList.get(xValueKey), null);
			//Se não achar item nulo na lista e lista possuir algum item, seta para o primeiro item da lista.
			if (xValue==null){
				if (xList.size() > 0){
					xValueKey = xList.keySet().iterator().next();
					xValue = DBSString.toString(xList.get(xValueKey), null);
				}
			}
		}
		//Seta valor atual com o valor utilizado considerando as possíveis modificações acima
		
		if (!DBSObject.isEqual(pCombobox.getValue(), DBSObject.getNotEmpty(xValueKey, null))){
			//Seta novo valor atual. Será disparado evento JS change para indicar que houve mudança
			pCombobox.setValue(DBSObject.getNotEmpty(xValueKey, null));
		}
		

		//Se não achar item na lista e lista possuir o item nulo, seta para este item nulo, caso contrário exibe erro.
		if (xValue==null){
			wLogger.error("Combobox [" + pCombobox.getClientId() + "] não encontrado item na lista com a chave [" + xValueKey + "]"); 
			xValue = "*Erro*:Item [" + xValueKey + "] não encontrado.";
			xValueKey = "";
		}
		
		xStyle = DBSFaces.getCSSStyleWidthFromInputSize(pCombobox.getSize());

		if (pCombobox.getReadOnly()){
			DBSFaces.encodeInputDataReadOnly(pCombobox, pWriter, xClientId, false, xValue.toString(), pCombobox.getSize(), null, xStyle);
		}else{
			pWriter.startElement("span", pCombobox);
				DBSFaces.encodeAttribute(pWriter, "class", DBSFaces.getInputDataClass(pCombobox));
				DBSFaces.encodeAttribute(pWriter, "style", xStyle);
				DBSFaces.setSizeAttributes(pWriter, pCombobox.getSize(), null);
				pWriter.startElement("span", pCombobox);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.DATA);
					pWriter.write(xValue);
				pWriter.endElement("span");
				//Encode do botão
				pWriter.startElement("span", pCombobox);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.BUTTON + CSS.MODIFIER.SMALL + " -i_navigate_down");
				pWriter.endElement("span");
				//Encode do combobox escondido
				pWriter.startElement("select", pCombobox);
					DBSFaces.encodeAttribute(pWriter, "id", xClientId);
					DBSFaces.encodeAttribute(pWriter, "name", xClientId);
					DBSFaces.encodeAttribute(pWriter, "size", "1");
//					encodeClientBehaviors(pContext, pCombobox);
					if (xList != null
					 && xList.size() > 0){
						for (Map.Entry<Object, Object> xListItem : xList.entrySet()) {
							if (xListItem.getKey() != null){
								pWriter.startElement("option", pCombobox);
									DBSFaces.encodeAttribute(pWriter, "value", xListItem.getKey());
									if (xListItem.getKey().equals(xValueKey)){
										DBSFaces.encodeAttribute(pWriter, "selected", "");
									}
									pWriter.write((String) DBSObject.getNotEmpty(xListItem.getValue(), "") );
									
								pWriter.endElement("option");
							}
						}
					}
				pWriter.endElement("select"); 
			pWriter.endElement("span");
		}
	}
	
}






