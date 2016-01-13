package br.com.dbsoft.ui.component.inputtextarea;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
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
		DBSInputTextArea xInputTextArea = (DBSInputTextArea) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xInputTextArea.getClientId(pContext);
		String xClass = DBSFaces.CSS.INPUTTEXTAREA.MAIN + " " + DBSFaces.CSS.INPUT.MAIN;
		if (xInputTextArea.getStyleClass()!=null){
			xClass = xClass + xInputTextArea.getStyleClass();
		}
		xWriter.startElement("div", xInputTextArea);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			xWriter.writeAttribute("class", xClass, "class");
			DBSFaces.setAttribute(xWriter, "style", xInputTextArea.getStyle(), null);
			//Container
			xWriter.startElement("div", xInputTextArea);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, "class");
	
					DBSFaces.encodeLabel(pContext, xInputTextArea, xWriter);
					pvEncodeInput(pContext, xInputTextArea, xWriter);
					DBSFaces.encodeTooltip(pContext, xInputTextArea, xInputTextArea.getTooltip());
			xWriter.endElement("div");
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
			xStyle += DBSFaces.getStyleWidthFromInputSize(pInputTextArea.getCols()) + ";";
		}
		if (pInputTextArea.getRows()!=null){
			xStyle += DBSFaces.getStyleHeightFromInputSize(pInputTextArea.getRows());
		}

		//Se for somente leitura, gera código como <Span>
		if (pInputTextArea.getReadOnly()){
			DBSFaces.encodeInputDataReadOnly(pInputTextArea, pWriter, xClientId, true, xValue, pInputTextArea.getCols(), pInputTextArea.getRows(), xStyle);
		}else{
			pWriter.startElement("textarea ", pInputTextArea);
				DBSFaces.setAttribute(pWriter, "id", xClientId, null);
				DBSFaces.setAttribute(pWriter, "name", xClientId, null);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pInputTextArea), null);
				DBSFaces.setAttribute(pWriter, "style", xStyle, null);
				DBSFaces.setSizeAttributes(pWriter, pInputTextArea.getCols(), pInputTextArea.getRows());
				DBSFaces.setAttribute(pWriter, "cols", pInputTextArea.getCols(), null);
				DBSFaces.setAttribute(pWriter, "rows", pInputTextArea.getRows(), null);
				if (pInputTextArea.getResize()){
					pWriter.writeAttribute("resize", pInputTextArea.getResize(), null);
				}
				if (pInputTextArea.getMaxLength()!=0){
					DBSFaces.setAttribute(pWriter, "maxlength", pInputTextArea.getMaxLength(), null);
				}			
				pWriter.write(DBSObject.getNotNull(xValue,""));
				encodeClientBehaviors(pContext, pInputTextArea);
			pWriter.endElement("textarea");
		}
	}
	

}






