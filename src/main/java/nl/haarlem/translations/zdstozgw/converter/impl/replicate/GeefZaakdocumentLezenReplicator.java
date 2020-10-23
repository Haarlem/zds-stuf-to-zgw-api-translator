package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.GeefZaakdocumentLezenTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class GeefZaakdocumentLezenReplicator extends GeefZaakdocumentLezenTranslator {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public GeefZaakdocumentLezenReplicator(RequestHandlerContext context, Translation translation,
			ZaakService zaakService, ZdsStuurgegevens stuurgegevens) {
		super(context, translation, zaakService);
	}

    /**
     * Replicates the zaak before returning zaakdocument
     *
     * @return
     * @throws ResponseStatusException
     */
	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLv01 = (ZdsZakLv01) this.getZdsDocument();

		var replicator = new Replicator(this);
		replicator.replicateZaak(zdsZakLv01.scope.object.identificatie);

		var legacyresponse = replicator.proxy();
		if (legacyresponse.getStatusCode() != HttpStatus.OK) {
			log.warn("Service:" + this.getTranslation().getLegacyservice() + " SoapAction: "
					+ this.getContext().getSoapAction());
			return legacyresponse;
		}

		return super.execute();
	}
}
