package br.com.dbsoft.ui.component.setAttribute;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public class DBSSetAttributeOnParent extends TagHandler implements Serializable{

	private static final long serialVersionUID = -5626382030884586959L;
	private final TagAttribute wAttributeName;
    private final TagAttribute wAttributeValue;
//    private final TagAttribute wAttributeObject;
    private final TagAttribute wAttributeValueExpression;

    public DBSSetAttributeOnParent(TagConfig pConfig) {
        super(pConfig);
        this.wAttributeName = this.getRequiredAttribute("attributeName");
        this.wAttributeValue = this.getAttribute("attributeValue");
//        this.wAttributeObject = this.getAttribute("attributeObject");
        this.wAttributeValueExpression = this.getAttribute("attributeValueExpression");
    }
    
    @Override
	public void apply(FaceletContext pContext, UIComponent pParent)
            throws IOException, FacesException, FaceletException, ELException {
        try {
        	//Move o valor através da chamada ao método set do componente
        	if (wAttributeValue!=null){
	        	String xValue = this.wAttributeValue.getValue(pContext);
	            String xMethodName = this.wAttributeName.getValue(pContext);
	        	//Inclui o prefixo set 
	            xMethodName = "set" + xMethodName.substring(0, 1).toUpperCase() + xMethodName.substring(1, xMethodName.length());
	            
	            Method xM = pParent.getClass().getMethod(xMethodName, new Class[]{String.class});
	            xM.invoke(pParent, new Object[]{xValue});
	        }
//        	else if (wAttributeObject!=null){
//	        	Object xValue = this.wAttributeObject.getObject(pContext);
//	            String xMethodName = this.wAttributeName.getValue(pContext);
//	        	//Inclui o prefixo set 
//	            xMethodName = "set" + xMethodName.substring(0, 1).toUpperCase() + xMethodName.substring(1, xMethodName.length());
//	            
//	            Method xM = pParent.getClass().getMethod(xMethodName, new Class[]{Object.class});
//	            xM.invoke(pParent, new Object[]{xValue});
//	        }

        	//Move o valor do EL ao invés do próprio valor
        	if (wAttributeValueExpression!=null){
        		
	        	ValueExpression xVE = (ValueExpression) this.wAttributeValueExpression.getObject(pContext);
	        	String xMethodName = this.wAttributeName.getValue(pContext);
        		pParent.setValueExpression(xMethodName, xVE);
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.nextHandler.apply(pContext, pParent);
    }
}
