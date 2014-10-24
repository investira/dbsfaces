package br.com.dbsoft.ui.component.combobox;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.core.DBSSDK.UI.COMBOBOX;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSNumber;
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
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }
    
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSCombobox xCombobox = (DBSCombobox) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xCombobox.getClientId(pContext);
		String xClass = CSS.COMBOBOX.MAIN + " " + CSS.INPUT.MAIN + " ";
		if (xCombobox.getStyleClass()!=null){
			xClass = xClass + xCombobox.getStyleClass();
		}
		xWriter.startElement("div", xCombobox);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			xWriter.writeAttribute("class", xClass, "class");
			DBSFaces.setAttribute(xWriter, "style", xCombobox.getStyle(), null);
			xWriter.startElement("div", xCombobox);
				xWriter.writeAttribute("class", CSS.MODIFIER.CONTAINER, "class");
					DBSFaces.encodeLabel(pContext, xCombobox, xWriter);
					pvEncodeInput(pContext, xCombobox, xWriter);
					DBSFaces.encodeRightLabel(pContext, xCombobox, xWriter);
					DBSFaces.encodeTooltip(pContext, xCombobox, xCombobox.getTooltip());
			xWriter.endElement("div");
		xWriter.endElement("div");
		
		DBSFaces.encodeJavaScriptTagStart(xWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xComboboxId = '#' + dbsfaces.util.jsid('" + xClientId + "'); \n " + 
				     " dbs_combobox(xComboboxId); \n" +
                     "}); \n"; 
		xWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(xWriter);			
	}

	
	private void pvEncodeInput(FacesContext pContext, DBSCombobox pCombobox, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pCombobox);
		String xStyle = "";
		Object xValueKey = "";
		Object xValue = "";
		LinkedHashMap<Object, Object> xList = pCombobox.getList();
		
		//Se não foi informada a lista ou lista estiver vazia
		if (xList == null || 
			xList.size() == 0){
			DBSFaces.encodeInputDataReadOnly(pCombobox, pWriter, xClientId,  DBSFaces.getStyleWidthFromInputSize(pCombobox.getSize()), false, "");
			return;
		}

		//Recupera valor
		xValueKey = pCombobox.getValue();

		//Converte o valor para o mesmo tipo da chave utilizada na lista para garantir que a comparação para verificar se a chave existe será efetuados com valores do mesmo tipo de class
		if (xValueKey != null){
			Iterator<Object> xListKeyIterator = xList.keySet().iterator();
			Class<?> xValueKeyClass = xValueKey.getClass();
			Object xListKeyValue;
			//Pega primeiro item que não seja o valor nulo para poder verificar o seu tipo
			while (xListKeyIterator.hasNext()){
				xListKeyValue = xListKeyIterator.next();
				if (!xListKeyValue.equals(COMBOBOX.NULL_VALUE)){
					xValueKeyClass = xListKeyValue.getClass();
					break;
				}
			}
			if (xValueKeyClass.isAssignableFrom(Integer.class)){
				xValueKey =  DBSNumber.toInteger(xValueKey, null);
			}else if (xValueKeyClass.isAssignableFrom(BigDecimal.class)){
				xValueKey =  DBSNumber.toBigDecimal(xValueKey, null);
			}else if (xValueKeyClass.isAssignableFrom(Double.class)){
				xValueKey =  DBSNumber.toDouble(xValueKey, null);
			}
		}else{
			xValueKey = COMBOBOX.NULL_VALUE;
		}
		
		//Busca item na lista para recuperar o valor que será utlizado para exibir a informação
		xValue = xList.get(xValueKey);

		//Se não achar item na lista e lista possuir o item nulo, seta para este item nulo, caso contrário exibe erro.
		if (xValue==null){
			wLogger.error("Combobox [" + pCombobox.getClientId() + "] não encontrado item na lista com a chave [" + xValueKey + "]"); 
			xValue = "*erro*:Item [" + xValueKey + "] não encontrado.";
			xValueKey = "";
		}
		
		xStyle = DBSFaces.getStyleWidthFromInputSize(pCombobox.getSize());

		if (pCombobox.getReadOnly()){
			DBSFaces.encodeInputDataReadOnly(pCombobox, pWriter, xClientId, xStyle, false, xValue.toString());
		}else{
			//Encode dos itens na lista
			String xSelectedText = "";
			//Texto selecionado
			xSelectedText = DBSString.toString(xList.get(xValueKey));
			pWriter.startElement("span", pCombobox);
				DBSFaces.setAttribute(pWriter, "style", xStyle, null);
				DBSFaces.setAttribute(pWriter, "class", CSS.INPUT.DATA, null);
				pWriter.startElement("span", pCombobox);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.DATA, null);
					pWriter.write(xSelectedText);
				pWriter.endElement("span");
				//Encode do botão
				pWriter.startElement("span", pCombobox);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.BUTTON + CSS.ICONSMALL + " -is_select_down", null);
				pWriter.endElement("span");
				//Encode do combobox escondido
				pWriter.startElement("select", pCombobox);
					DBSFaces.setAttribute(pWriter, "id", xClientId, null);
					DBSFaces.setAttribute(pWriter, "name", xClientId, null);
					DBSFaces.setAttribute(pWriter, "size", "1", null);
					DBSFaces.setAttribute(pWriter, "style", xStyle, null);
					encodeClientBehaviors(pContext, pCombobox);
					if (xList != null
					 && xList.size() > 0){
						for (Map.Entry<Object, Object> xListItem : xList.entrySet()) {
							if (xListItem.getKey() != null){
								pWriter.startElement("option", pCombobox);
									DBSFaces.setAttribute(pWriter, "value", xListItem.getKey(), null);
									if (xListItem.getKey().equals(xValueKey)){
										DBSFaces.setAttribute(pWriter, "selected", "", null);
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






