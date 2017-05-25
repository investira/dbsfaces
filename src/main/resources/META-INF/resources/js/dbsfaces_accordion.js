dbs_accordion = function(pId) {
	var wsResizing = false;
	var wsAnimationTime = 200;

	$(window).resize(function() {
		if (wsResizing==false){
			wsResizing = true;
			wsAnimationTime = 0;	
			if ($(document).find("div.dbs_accordion_section.-opened")){
				dbsfaces.accordion.contentSectionOpened($(document).find("div.dbs_accordion"), true);
			}
			wsAnimationTime = 200;
			wsResizing = false;
		}
	});
	
   $("div.dbs_accordion_section-container > .-cover, div.dbs_accordion_section-caption").click(function() {
    	/*ATIVA ou DESATIVA ABERTO*/

    	var xSection = $(this).closest("div.dbs_accordion_section");
    	var xAccordion = "#" + $(xSection).closest("div.dbs_accordion").attr('id');
    	xAccordion = dbsfaces.util.jsid(xAccordion);

    	/* Caso seção selecionada esteja aberta */
   		if ($(xSection).hasClass("-opened")){ /*Volta todos ao Normal*/
			$(xAccordion).children("div.dbs_accordion_section").removeClass("-closed")
									                       	   .removeClass("-opened")
									   		   		           .addClass("-normal");	
	    /* Caso seção selecionada esteja fechada */
		}else if ($(xSection).hasClass("-closed")){ /*Abre se fechado*/
			/*Fecha quem estava aberto*/
			$(xAccordion).children("div.dbs_accordion_section.-opened").removeClass("-opened")
						  		  					  			       .addClass("-closed");
			/*Abre selecionado(este)*/
			$(xSection).removeClass("-closed")
					   .addClass("-opened");
		/* Caso seção selecionada esta normal */
    	}else if ($(xSection).hasClass("-normal")){  /*Abre se normal*/
			/*Abre este*/
			$(xSection).removeClass("-normal")
					   .addClass("-opened");
			/*Fecha os outros*/
			$(xAccordion).children("div.dbs_accordion_section.-normal").removeClass("-normal")
			  	      							   				       .addClass("-closed");
        }			
   		dbsfaces.accordion.contentSectionOrganize(xAccordion);
   });
};

dbsfaces.accordion = {
	contentSectionOpened: function(e, pAnimate){
		/*posição ABERTA*/
        var xH = parseInt($(e).css('height'));
        var xT = 42;  /*Altura total considerando (21 * 2)*/
        if (xH > xT){ /*Calcula altura da seção que ficará aberta, de forma a ocupar todo o espaço*/
            xH = xH - xT + 'px';
        }
        
        /*posição ABERTA*/
        if (pAnimate){
        	/*Abre*/          
        	$(e).children("div.dbs_accordion_section.-opened").animate({
                height: xH
                }, wsAnimationTime
            );
			/*Fecha as outras*/
            if (!$(e).children("div.dbs_accordion_section").hasClass("-normal")){
             	 $(e).find("div.dbs_accordion_section-container > .-cover").animate({
                    height: 0
                    }, wsAnimationTime
                );
            }
        }else{
        	$(e).children("div.dbs_accordion_section.-opened").height(xH);
        }
    },
    
    contentSectionOrganize: function(e){
        /*posição FECHADA*/ 
        $(e).find("div.dbs_accordion_section.-closed .-caption").css("color","rgb(130,130,130)"); /*troca cor do cebeçalho fechado*/
        $(e).children("div.dbs_accordion_section.-closed").animate({
            height: "21px" /* Altura do cebeçalho */
          }, wsAnimationTime
        );  
        
        /*posição NORMAL*/
        if ($(e).children("div.dbs_accordion_section").hasClass("-normal")){ /* Existir algum filho como normal*/
	        /* Ajusta todas as seções como normal*/
	        $(e).children("div.dbs_accordion_section.-normal").animate({
	            height: '33.4%'
	          }, wsAnimationTime 
	        );
	        /* Exibe os covers(cobertura) das seções */
        	$(e).find("div.dbs_accordion_section-container > .-cover").animate({
                height: "100%"
                }, wsAnimationTime
            );
       }

        /*posição ABERTA*/
        dbsfaces.accordion.contentSectionOpened(e, true);

        /*posição NORMAL e ABERTA*/
        /*Remove cor*/
        $(e).find("div.dbs_accordion_section.-normal .-caption, div.dbs_accordion_section.-opened .-caption").css("color","");
    }
};

