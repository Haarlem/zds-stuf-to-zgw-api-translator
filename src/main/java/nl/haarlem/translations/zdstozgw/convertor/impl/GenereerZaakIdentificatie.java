package nl.haarlem.translations.zdstozgw.convertor.impl;


import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import lombok.Data;

import nl.haarlem.translations.zdstozgw.translation.zds.model.StufRequest;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;
import nl.haarlem.translations.zdstozgw.convertor.Convertor;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

import nl.haarlem.translations.zdstozgw.jpa.model.ApplicationParameter;

public class GenereerZaakIdentificatie implements Convertor {

	@Data
	private class GenereerZaakIdentificatie_Di02 {
	    final XpathDocument xpathDocument;
	    Document document;
	    
		public GenereerZaakIdentificatie_Di02(StufRequest stufRequest) {
	        this.document = stufRequest.body;
	        this.xpathDocument  = new XpathDocument(document);			
		}
	}
	
	@Data
	private class GenereerZaakIdentificatie_Du02 {
	    final XpathDocument xpathDocument;
	    Document document;

		public GenereerZaakIdentificatie_Du02(String template) {
			this.document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument(template);			
			this.xpathDocument  = new XpathDocument(document);
		}
	}	
	
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	protected String template;
	
    public GenereerZaakIdentificatie(String template) {
        this.template = template;
    }

    @Override
    public String Convert(ZaakService zaakService, ApplicationParameterRepository repository, Object object) {
    	var stufRequest = (StufRequest) object;
    	DateFormat tijdstipformat = new SimpleDateFormat("yyyyMMddHHmmss");   	    
    	
    	var prefixparam  = repository.getOne("ZaakIdentificatiePrefix");
    	var idparam= repository.getOne("ZaakIdentificatieHuidige");    	
    	var identificatie = Long.parseLong(idparam.getParameterValue()) + 1;	
    	idparam.setParameterValue(Long.toString(identificatie));
    	repository.save(idparam);
    	    	
    	var di02 = new GenereerZaakIdentificatie_Di02(stufRequest);
    	var du02 = new GenereerZaakIdentificatie_Du02(this.template); 	    	    	
    	du02.xpathDocument.setNodeValue(".//stuf:zender//stuf:organisatie", di02.xpathDocument.getNodeValue(".//stuf:ontvanger//stuf:organisatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:zender//stuf:applicatie", di02.xpathDocument.getNodeValue(".//stuf:ontvanger//stuf:applicatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:zender//stuf:gebruiker", di02.xpathDocument.getNodeValue(".//stuf:ontvanger//stuf:gebruiker"));
    	du02.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:organisatie", di02.xpathDocument.getNodeValue(".//stuf:zender//stuf:organisatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:applicatie", di02.xpathDocument.getNodeValue(".//stuf:zender//stuf:applicatie"));
    	du02.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:gebruiker", di02.xpathDocument.getNodeValue(".//stuf:zender//stuf:gebruiker"));
    	du02.xpathDocument.setNodeValue(".//stuf:referentienummer", di02.xpathDocument.getNodeValue(".//stuf:referentienummer"));    	
    	du02.xpathDocument.setNodeValue(".//stuf:tijdstipBericht", tijdstipformat.format(new Date()));    
    	du02.xpathDocument.setNodeValue(".//zkn:identificatie", prefixparam.getParameterValue() + identificatie);
    	
    	return XmlUtils.xmlToString(du02.document);
    }
}