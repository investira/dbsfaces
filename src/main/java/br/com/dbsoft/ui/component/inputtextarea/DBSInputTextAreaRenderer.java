package br.com.dbsoft.ui.component.inputtextarea;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSInputTextArea.RENDERER_TYPE)
public class DBSInputTextAreaRenderer extends DBSRenderer {
	
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSInputTextArea xInputTextArea = (DBSInputTextArea) pComponent;
        if(xInputTextArea.getReadOnly()) {return;}

    	decodeBehaviors(pContext, xInputTextArea);
    	
		String xClientIdAction = getInputDataClientId(xInputTextArea);
		if (pContext.getExternalContext().getRequestParameterMap().containsKey(xClientIdAction)){
			String xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientIdAction);
	        if(xSubmittedValue != null) {
	            xInputTextArea.setSubmittedValue(xSubmittedValue);
	        }
		}

	}	
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSInputTextArea xInputTextArea = (DBSInputTextArea) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xInputTextArea.getClientId(pContext);
		String xClass = CSS.INPUTTEXTAREA.MAIN + CSS.THEME.INPUT;
		if (xInputTextArea.getStyleClass()!=null){
			xClass = xClass + xInputTextArea.getStyleClass();
		}
		xWriter.startElement("div", xInputTextArea);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xInputTextArea.getStyle());
			//Container
			xWriter.startElement("div", xInputTextArea);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
	
					DBSFaces.encodeLabel(pContext, xInputTextArea, xWriter);
					pvEncodeInput(pContext, xInputTextArea, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xInputTextArea, xInputTextArea.getTooltip());
		xWriter.endElement("div");
	}

	
	private void pvEncodeInput(FacesContext pContext, DBSInputTextArea pInputTextArea, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputTextArea);
		String xStyle = "";
		String xValue = "";

		xValue = DBSObject.getNotEmpty(pInputTextArea.getValue(), "").toString();

		//Defino tamanho mínimo quando valor for vázio e não tiver sido informado os valores de quantidade de colunas e linha
		if (xValue.equals("")){
			if (pInputTextArea.getCols()==null){
				pInputTextArea.setCols(10);
			}
			if (pInputTextArea.getRows()==null){
				pInputTextArea.setRows(1);
			}
		}
		
		//Define a largura do campo
		if (pInputTextArea.getCols()!=null){
			xStyle += DBSFaces.getCSSStyleWidthFromInputSize(pInputTextArea.getCols());
		}
		if (pInputTextArea.getRows()!=null){
			xStyle += DBSFaces.getCSSStyleHeightFromInputSize(pInputTextArea.getRows());
		}

		//Se for somente leitura, gera código como <Span>
		if (pInputTextArea.getReadOnly()){
			DBSFaces.encodeInputDataReadOnly(pInputTextArea, pWriter, xClientId, true, xValue, pInputTextArea.getCols(), pInputTextArea.getRows(), xStyle);
		}else{
			pWriter.startElement("textarea ", pInputTextArea);
				DBSFaces.encodeAttribute(pWriter, "id", xClientId);
				DBSFaces.encodeAttribute(pWriter, "name", xClientId);
				DBSFaces.encodeAttribute(pWriter, "class", DBSFaces.getInputDataClass(pInputTextArea));
				DBSFaces.encodeAttribute(pWriter, "style", xStyle);
				DBSFaces.encodeAttribute(pWriter, "placeHolder", pInputTextArea.getPlaceHolder());
				DBSFaces.setSizeAttributes(pWriter, pInputTextArea.getCols(), pInputTextArea.getRows());
				DBSFaces.encodeAttribute(pWriter, "cols", pInputTextArea.getCols());
				DBSFaces.encodeAttribute(pWriter, "rows", pInputTextArea.getRows());
				if (pInputTextArea.getResize()){
					DBSFaces.encodeAttribute(pWriter, "resize", pInputTextArea.getResize());
				}
				if (pInputTextArea.getMaxLength()!=0){
					DBSFaces.encodeAttribute(pWriter, "maxlength", pInputTextArea.getMaxLength());
				}			
				pWriter.write(DBSObject.getNotNull(xValue,""));
				encodeClientBehaviors(pContext, pInputTextArea);
			pWriter.endElement("textarea");
		}
	}
	

}






