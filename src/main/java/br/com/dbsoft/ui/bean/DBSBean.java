package br.com.dbsoft.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

/**
 * @author ricardo.villar
 */
public abstract class DBSBean implements Serializable{
 
	private static final long serialVersionUID = -5273728796912868413L;

	protected 	Logger 		wLogger =  Logger.getLogger(this.getClass());
	
	private   	DBSBean						wMasterBean = null;
	private 	List<DBSBean>				wSlavesBean = new ArrayList<DBSBean>();
	private 	Locale						wLocale;
 
	//--------------------------------------------------------------------------------------
	//Código para impedir o erro de 'Cannot create a session after the response has been committed'
	//que ocorre em algumas situações que a página(como resultado da quantidade de registros do ResultDataModel) por conter muitos dados
	//o que faz que por algum motivo o JSF envie algum resposta no momento que não deveria, principalmente com @ResquestScoped
	@PostConstruct
	void pvInitializeClass() {
		if (FacesContext.getCurrentInstance() == null){
			wLogger.warn(this.getClass().getCanonicalName() + ":Não há scope ativo para este bean.");
		}else{ 
			FacesContext xFC = FacesContext.getCurrentInstance();
			if(xFC.getExternalContext().getSession(false) == null){
				xFC.getExternalContext().getSession(true);
			}
			pvGetUserLocate();
		}
		initializeClass();
	}
	
	private void pvGetUserLocate(){
//		for( Cookie cookie : FacesContext.getCurrentInstance().getExternalContext( httpServletRequest.getCookies() ) {
//		    System.out.println( cookie.getName() + " - " + cookie.getValue() );
//		}
//		String cookieValue_Language = new Locale( "tr", "TR" ).getLanguage();
//		Cookie localeCookie_lang = new Cookie( "locale", cookieValue_Language );
//		response.addCookie( localeCookie_lang );
//		Iterator xLocales = (Iterator) FacesContext.getCurrentInstance().getExternalContext().getRequestLocales();
//		while (xLocales.){e
//			Locale xLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocales().next();
//			System.out.println(xLocale);
//		}
//		System.out.println(FacesContext.getCurrentInstance().getApplication().getDefaultLocale());
//		System.out.println(FacesContext.getCurrentInstance().getViewRoot().getLocale());
//		System.out.println(FacesContext.getCurrentInstance().getExternalContext().getRequestLocale());
//		setLocale(FacesContext.getCurrentInstance().getExternalContext().getRequestLocale());
		setLocale(FacesContext.getCurrentInstance().getApplication().getDefaultLocale());
	}
	
	@PreDestroy
	void pvFinalizeClass(){
		finalizeClass();
	}
	
	
	//Public -------------------------------------------------------------
	
	/**
	 * Crudbean que será o principal. Responsável por apagar da memória os crudbean escravaos que existem
	 * @return
	 */
	public DBSBean getMasterBean() {
		return wMasterBean;
	}
	
	public void setLocaleCode(String pLocale){
		wLocale = new Locale(pLocale);
		setLocale(wLocale);
	}
	public String getLocaleCode(){
		return wLocale.toString();
	}	
	
    public String getLanguage() {
        return wLocale.getLanguage();
    }

    public void setLanguage(String pLanguage) {
    	setLocale(new Locale(pLanguage));
    }
    
	public void setLocale(Locale pLocale){
//		HttpServletResponse xR = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//		xR.setLocale(pLocale);
//		xR.addHeader("Content-Language", "pt-BR");
		FacesContext.getCurrentInstance().getViewRoot().setLocale(pLocale);
	}
	
	public Locale getLocale(){
		return wLocale;
	}
	/**
	 * Lista de CrudBean escravos dentro deste crud.
	 * Os crudbean escravos serão retirados da memória quando o master for
	 * @return
	 */
	public List<DBSBean> getSlavesBean() {
		return wSlavesBean;
	}
	
	
	/**
	 * CrudBean que será o principal. Responsável por apagar da memória os CrudBean escravos que a ele vinculados
	 */
	public void setMasterBean(DBSBean pBean) {
		wMasterBean = pBean;
		if (!pBean.getSlavesBean().contains(this)){
			pBean.getSlavesBean().add(this);
		}
	}
	

	/**
	 * Define o tempo máximo sem atividade para invalidar a seção. O padrão é de 600 segundos(10 minutos).<br/> 
	 * Tempo em segundos.
	 * @return
	 */
	public Integer getMaxInactiveInterval() {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMaxInactiveInterval();
	}

	/**
	 * Define o tempo máximo sem atividade para invalidar a seção. O padrão é de 600 segundos(10 minutos).
	 * @param pMaxInactiveInterval Tempo em segundos
	 */
	public void setMaxInactiveInterval(Integer pMaxInactiveInterval) {
		FacesContext.getCurrentInstance().getExternalContext().setSessionMaxInactiveInterval(pMaxInactiveInterval);
	};
	
	/**
	 * Método após a inicialização do bean.
	 * Ao sobre escrever este método, deve-se estar atendo em chamar o <b>super</b>.
	 */
	protected void initializeClass(){};
	
	/**
	 * Método após a finalização do bean.
	 * Ao sobre escrever este método, deve-se estar atendo em chamar o <b>super</b>.
	 */
	protected void finalizeClass(){}

	
}
