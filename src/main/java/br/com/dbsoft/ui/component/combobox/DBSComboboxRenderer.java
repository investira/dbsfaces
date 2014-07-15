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

import br.com.dbsoft.core.DBSSDK;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSCombobox.RENDERER_TYPE)
public class DBSComboboxRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSCombobox xCombobox = (DBSCombobox) pComponent;
        if(xCombobox.getReadOnly()) {return;}
    	if (!xCombobox.isRendered()){return;}

    	decodeBehaviors(pContext, xCombobox);
    	
		String xClientIdAction = getInputDataClientId(xCombobox);
		
		if (pContext.getExternalContext().getRequestParameterMap().containsKey(xClientIdAction)){
			Object xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientIdAction);
			//Se valor recebido for igual o valor considerado como nulo ou o vázio
			//Envia o valor como nulo
			if (xSubmittedValue.equals(DBSSDK.UI.COMBOBOX.NULL_VALUE)){
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
		String xClass = DBSFaces.CSS.COMBOBOX.MAIN + " " + DBSFaces.CSS.INPUT.MAIN + " ";
		if (xCombobox.getStyleClass()!=null){
			xClass = xClass + xCombobox.getStyleClass();
		}
		xWriter.startElement("div", xCombobox);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			xWriter.writeAttribute("class", xClass, "class");
			DBSFaces.setAttribute(xWriter, "style", xCombobox.getStyle(), null);
			xWriter.startElement("div", xCombobox);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, "class");
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
		
		if (xList !=null){
			xValueKey = pCombobox.getValue();
//			//Se valor for nulo, busca pela chave DBSSDK.UI.COMBOBOX.NULL_VALUE
//			if (xValueKey==null){
//				//Posiciona o item referente ao valor nulo se existir na lista, 
//				//caso contrário posiciona o primeiro item da lista.
//				if (xList.containsKey(DBSSDK.UI.COMBOBOX.NULL_VALUE)){
//					xValueKey = DBSSDK.UI.COMBOBOX.NULL_VALUE;
//				}else{
//					//Posiciona o primeiro item da lista
//					xValueKey = xList.entrySet().iterator().next().getKey();
//				}
//			}else{
				//Converte o valor para o mesmo tipo da chave utilizada na lista para garantir que a comparação para verificar se a chave existe será efetuados com valores do mesmo tipo de class
				Iterator<Object> xListKeyIterator = xList.keySet().iterator();
				Class<?> xKeyClass = xValueKey.getClass();
				Object xListKeyValue;
				//Pega primeiro item que não seja o valor nulo para poder verificar o seu tipo
				while (xListKeyIterator.hasNext()){
					xListKeyValue = xListKeyIterator.next();
					if (!xListKeyValue.equals(DBSSDK.UI.COMBOBOX.NULL_VALUE)){
						xKeyClass = xListKeyValue.getClass();
						break;
					}
				}
				if (xKeyClass.isAssignableFrom(Integer.class)){
					xValueKey =  DBSNumber.toInteger(xValueKey);
				}else if (xKeyClass.isAssignableFrom(BigDecimal.class)){
					xValueKey =  DBSNumber.toBigDecimal(xValueKey);
				}else if (xKeyClass.isAssignableFrom(Double.class)){
					xValueKey =  DBSNumber.toDouble(xValueKey);
				}
//			}
			//Busca item na lista para recuperar o valor que será utlizado para exibir a informação
			xValue = xList.get(xValueKey);
			if (xValue==null){
				//Exibe erro no console, se não tiver sido encontrado na lista o respectivo valor conforme a chave informada
				if  (!xValueKey.equals(DBSSDK.UI.COMBOBOX.NULL_VALUE)){
					wLogger.error("Checkbox " + pCombobox.getClientId() + " não encontrado item na lista com a chave [" + xValueKey + "]");
				}
				xValue = "";
				xValueKey = "";
			}
		}
		
		xStyle = DBSFaces.getStyleWidthFromInputSize(pCombobox.getSize());

		if (xList == null || 
			xList.size() == 0){
			DBSFaces.encodeInputDataReadOnly(pCombobox, pWriter, xClientId, DBSFaces.getStyleWidthFromInputSize(9), false, "'List' vázio");
		}else if (pCombobox.getReadOnly()){
			DBSFaces.encodeInputDataReadOnly(pCombobox, pWriter, xClientId, xStyle, false, xValue.toString());
		}else{
			//Encode do combobox escondido
			pWriter.startElement("select", pCombobox);
				DBSFaces.setAttribute(pWriter, "id", xClientId, null);
				DBSFaces.setAttribute(pWriter, "name", xClientId, null);
				DBSFaces.setAttribute(pWriter, "size", "1", null);
				DBSFaces.setAttribute(pWriter, "style", xStyle, null);
				encodeClientBehaviors(pContext, pCombobox);
				//Encode dos itens na lista
				String xSelectedText = "";
				if (xList != null
				 && xList.size() > 0){
					xSelectedText =  (String) xList.values().toArray()[0];
					for (Map.Entry<Object, Object> xListItem : xList.entrySet()) {
						if (xListItem.getKey() != null){
							pWriter.startElement("option", pCombobox);
								DBSFaces.setAttribute(pWriter, "value", xListItem.getKey(), null);
								if (xListItem.getKey().equals(xValueKey)){
									DBSFaces.setAttribute(pWriter, "selected", "", null);
									xSelectedText = (String) DBSObject.getNotEmpty(xListItem.getValue(), "");
								}
								pWriter.write((String) DBSObject.getNotEmpty(xListItem.getValue(), "") );
								
							pWriter.endElement("option");
						}
					}
				}
			pWriter.endElement("select"); 
			//Texto selecionado
			pWriter.startElement("span", pCombobox);
				DBSFaces.setAttribute(pWriter, "style", xStyle, null);
				if (pCombobox.getReadOnly()){
					DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUT.DATA + DBSFaces.CSS.MODIFIER.DISABLED, null);
					DBSFaces.setAttribute(pWriter, "disabled","disabled", null);
				}else{
					DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUT.DATA, null);
				}
				pWriter.write(xSelectedText);
				//Encode do botão
				pWriter.startElement("span", pCombobox);
					DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.BUTTON + DBSFaces.CSS.ICONSMALL + " -is_select_down", null);
				pWriter.endElement("span");
			pWriter.endElement("span");
		}
	}
	
}






