package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.proxy.Proxy;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.VoegZaakdocumentToeTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class VoegZaakdocumentToeReplicator extends VoegZaakdocumentToeTranslator {
    private Replicator replicator;
	
    public VoegZaakdocumentToeReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {		
		var zdsEdcLk01 = (ZdsEdcLk01) this.getZdsDocument();		
		// replicate the zaak
		replicator.replicateZaak(zdsEdcLk01.objects.get(0).identificatie);
		// send to legacy system
		var legacyresponse = Proxy.Proxy(this.getTranslation().getLegacyservice(), this.getContext().getSoapAction(), getContext().getRequestBody());
		// do the translation
		return super.execute();
	}	
}
