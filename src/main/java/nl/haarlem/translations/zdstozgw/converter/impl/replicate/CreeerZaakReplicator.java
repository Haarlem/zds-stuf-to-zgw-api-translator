package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.CreeerZaakTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class CreeerZaakReplicator extends CreeerZaakTranslator {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public CreeerZaakReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
		super(context, translation, zaakService);
	}

    /**
     * Creates the zaak, no replication is necessary because it's a new zaak
     *
     * @return ZDS response
     * @throws ResponseStatusException
     */
	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLk01 = (ZdsZakLk01) this.getZdsDocument();

		var replicator = new Replicator(this);

		var legacyresponse = replicator.proxy();
		if (legacyresponse.getStatusCode() != HttpStatus.OK) {
			log.warn("Service:" + this.getTranslation().getLegacyservice() + " SoapAction: "
					+ this.getContext().getSoapAction());
			return legacyresponse;
		}

		return super.execute();
	}
}
