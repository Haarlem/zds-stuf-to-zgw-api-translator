package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.UpdateZaakTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class UpdateZaakReplicator extends UpdateZaakTranslator {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	public UpdateZaakReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLk01 = (ZdsZakLk01) this.getZdsDocument();		
        
		// replicate the zaak        
		var replicator = new Replicator(this);		
		replicator.replicateZaak(zdsZakLk01.objects.get(0).identificatie);		
		
		// send to legacy system
		var legacyresponse = replicator.proxy();
		if(legacyresponse.getStatusCode() != HttpStatus.OK) {
			log.warn("Service:" + this.getTranslation().getLegacyservice() +  " SoapAction: " +   this.getContext().getSoapAction());
			return legacyresponse;
		}		
		
		// do the translation
		return super.execute();
	}	
}
