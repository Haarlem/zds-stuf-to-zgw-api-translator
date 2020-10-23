package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.GeefLijstZaakdocumentenTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class GeefLijstZaakdocumentenReplicator extends GeefLijstZaakdocumentenTranslator {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public GeefLijstZaakdocumentenReplicator(RequestHandlerContext context, Translation translation,
			ZaakService zaakService) {
		super(context, translation, zaakService);
	}

    /**
     * Replicates the zaak before returning list of documents
     *
     * @return ZDS Lijst Zaakdocumenten
     * @throws ResponseStatusException
     */
	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLv01 = (ZdsZakLv01) this.getZdsDocument();

		var replicator = new Replicator(this);
		replicator.replicateZaak(zdsZakLv01.gelijk.identificatie);

		var legacyresponse = replicator.proxy();
		if (legacyresponse.getStatusCode() != HttpStatus.OK) {
			log.warn("Service:" + this.getTranslation().getLegacyservice() + " SoapAction: "
					+ this.getContext().getSoapAction());
			return legacyresponse;
		}

		return super.execute();
	}
}
