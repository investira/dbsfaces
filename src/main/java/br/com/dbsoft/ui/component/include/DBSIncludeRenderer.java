package br.com.dbsoft.ui.component.include;

import java.io.IOException;

import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.view.facelets.FaceletContext;

import com.sun.faces.facelets.el.VariableMapperWrapper;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSInclude.RENDERER_TYPE)
public class DBSIncludeRenderer extends DBSRenderer {

	
	@Override
	public boolean getRendersChildren() { //Indica se a rotina abaixo será chamada pelo renderChild
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent)
			throws IOException {
	}
	

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}

		DBSInclude xInclude = (DBSInclude) pComponent;
	
		if (DBSObject.isEmpty(xInclude.getSrc())){
			return;
		}
		
    	//INCLUI O COMPONENTE ===============================================================
		FacesContext xFC = pContext;
		FaceletContext xFaceletContext = (FaceletContext) xFC.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        VariableMapper xOrig = xFaceletContext.getVariableMapper();
        xFaceletContext.setVariableMapper(new VariableMapperWrapper(xOrig));
        try {
            xFaceletContext.includeFacelet(xInclude.getParent(), xInclude.getSrc());
        } catch (IOException e) {
            System.out.println("Caminho inválido : " + xInclude.getSrc());
        } finally {
            xFaceletContext.setVariableMapper(xOrig);   
        }        
	}
}

//Código antigo
////EXCLUI OS RESOURCES PARA EVITAR A DUPLICAÇÃO, POIS SERÃO AUTOMATICAMENTE ADICIONADOS QUANDO O COMPONENTE FOR INCLUIDO
//UIComponent xHeadFacet = pContext.getViewRoot().getFacet(DBSFaces.JAVAX_FACES_LOCATION_HEAD);
//@SuppressWarnings("unchecked")
//Set<ResourceDependency> xDependencies = (Set<ResourceDependency>)
//        RequestStateManager.get(pContext, RequestStateManager.PROCESSED_RESOURCE_DEPENDENCIES);
//if (xDependencies != null){
//    for (ResourceDependency xD: xDependencies){
//    	//System.out.println("D: " + xD.name());
//        if (xHeadFacet != null){
//        	if (xHeadFacet.getChildren()!=null){
//		        for (UIComponent xC :  xHeadFacet.getChildren()){
//			        if (xC.getAttributes().get("name").equals(xD.name())){
//			        	//System.out.println("   Excluido");
//			        	xHeadFacet.getChildren().remove(xC);
//			        	break;
//			        }
//		        }
//        	}
//        }
//    }
//}
//    
//

//try {
//	//Vincula o arquivo ao Div de trabalho
//	xFaceletContext.includeFacelet(xDiv, xInclude.getSrc());
//	//Força o encode dos filhos
//	xDiv.encodeChildren(xFC);
//	//Salva quantidade de filhos, pois durante o for o getChildren().size() reduz a medida que o add é efetuado
//	int xSize = xDiv.getChildren().size();
//	//Transfere todos os filhos do DIV para este componente
//	for (int xX = 0; xX < xSize;xX++){
//		//Adiciona sempre o item 0(zero) pois os filhos são excluidos do DIV a medida que são transferidos para este componente
//		//xDiv.getChildren().get(0).getAttributes().remove(com.sun.faces.facelets.tag.jsf.ComponentSupport.MARK_CREATED);
//		xInclude.getParent().getChildren().add(xDiv.getChildren().get(0));
//		//xInclude.getAttributes().remove(com.sun.faces.facelets.tag.jsf.ComponentSupport.MARK_CREATED);
//		//xInclude.getParent().getAttributes().remove(com.sun.faces.facelets.tag.jsf.ComponentSupport.MARK_CREATED);
//	}
//	//xDiv.getAttributes().remove(com.sun.faces.facelets.tag.jsf.ComponentSupport.MARK_CREATED);
//} catch (IOException e) {
//    try {
//		throw new Exception(e);
//	} catch (Exception e1) {
//		e1.printStackTrace();
//	}
//} finally {
//	xFaceletContext.setVariableMapper(xOrig);
//}
