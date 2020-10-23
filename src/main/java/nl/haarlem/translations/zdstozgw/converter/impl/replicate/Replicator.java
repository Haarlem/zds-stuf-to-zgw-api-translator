package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.converter.impl.replicate.model.ZdsReplicateGeefLijstZaakdocumentenLv01;
import nl.haarlem.translations.zdstozgw.converter.impl.replicate.model.ZdsReplicateGeefZaakdetailsLv01;
import nl.haarlem.translations.zdstozgw.converter.impl.replicate.model.ZdsReplicateGeefZaakdocumentLezenLv01;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.ZdsRequestResponseCycleRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.client.ZDSClient;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLa01GeefZaakdocumentLezen;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGerelateerde;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsHeeftRelevant;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsIsRelevantVoor;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsScope;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01GeefZaakDetails;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01LijstZaakdocumenten;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

@Service
public class Replicator {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private Converter converter;

	@Autowired
	private Replicator() {
	}

	public Replicator(Converter converter) {
		this.converter = converter;
	}

	public void replicateZaak(String zaakidentificatie) {
		log.info("replicateZaak for zaakidentificatie:" + zaakidentificatie);
		String rsin = this.converter.getZaakService().getRSIN(this.converter.getZdsDocument().stuurgegevens.zender.organisatie);

		// altijd de controle of de zaak al bestaat
		var zgwZaak = this.converter.getZaakService().zgwClient.getZaakByIdentificatie(zaakidentificatie);
		if (zgwZaak == null) {
			log.info("REPLICATION [replicate] zaakidentificatie #" + zaakidentificatie);
			
			// bestond nog niet, aanmaken
			var zdsUrl = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefZaakdetails().getUrl();
			var zdsSoapAction = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefZaakdetails().getSoapaction();
			var zdsRequest = new ZdsReplicateGeefZaakdetailsLv01();
			zdsRequest.stuurgegevens = this.converter.getZdsDocument().stuurgegevens;
			zdsRequest.parameters = new ZdsParameters();
			// zdsRequest.parameters.setSortering("0");
			zdsRequest.parameters.setIndicatorVervolgvraag("false");
			zdsRequest.gelijk = new ZdsZaak();
			zdsRequest.gelijk.identificatie = zaakidentificatie;
			zdsRequest.scope = new ZdsScope();
			zdsRequest.scope.scope = "alles";
			ZDSClient zdsClient = SpringContext.getBean(ZDSClient.class);
			var zdsResponse = zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);

			// fetch the zaak details
			log.debug("GeefZaakDetails response:" + zdsResponse);
			ZdsZakLa01GeefZaakDetails zakLa01 = (ZdsZakLa01GeefZaakDetails) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsZakLa01GeefZaakDetails.class);
			var zdsZaak = zakLa01.antwoord.zaak.get(0);

			log.info("received data from zds-zaaksysteem, now storing in zgw-zaaksysteem");
			this.converter.getZaakService().creeerZaak(rsin, zdsZaak);
		} else {
			log.info("REPLICATION [skip] zaakidentificatie #" + zaakidentificatie);			
		}

		// altijd de controle of de documenten al bestaan
		List<ZdsHeeftRelevant> relevanteDocumenten = null;
		{
			var zdsUrl = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefLijstZaakdocumenten().getUrl();
			var zdsSoapAction = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefLijstZaakdocumenten().getSoapaction();
			var zdsRequest = new ZdsReplicateGeefLijstZaakdocumentenLv01();
			zdsRequest.stuurgegevens = this.converter.getZdsDocument().stuurgegevens;
			zdsRequest.parameters = new ZdsParameters();
			// zdsRequest.parameters.setSortering("0");
			zdsRequest.parameters.setIndicatorVervolgvraag("false");
			zdsRequest.gelijk = new ZdsZaak();
			zdsRequest.gelijk.identificatie = zaakidentificatie;
			zdsRequest.scope = new ZdsScope();
			zdsRequest.scope.object = new ZdsZaak();
			zdsRequest.scope.object.heeftRelevant = new ZdsHeeftRelevant();
			zdsRequest.scope.object.heeftRelevant.gerelateerde = new ZdsZaakDocument();

			// fetch the document list
			ZDSClient zdsClient = SpringContext.getBean(ZDSClient.class);
			var zdsResponse = zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);
			log.info("GeefLijstZaakdocumenten voor zaak:" + zaakidentificatie);
			var zakZakLa01 = (ZdsZakLa01LijstZaakdocumenten) XmlUtils.getStUFObject(zdsResponse.getBody().toString(),ZdsZakLa01LijstZaakdocumenten.class);
			relevanteDocumenten = zakZakLa01.antwoord.object.heeftRelevant;
		}
		
		// check all documents
		{		
			for (ZdsHeeftRelevant relevant : relevanteDocumenten) {
				var zaakdocumentidentificatie = relevant.gerelateerde.identificatie;				
				ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = this.converter.getZaakService().zgwClient.getZgwEnkelvoudigInformatieObjectByIdentiticatie(zaakdocumentidentificatie);
				if (zgwEnkelvoudigInformatieObject == null) {
					log.info("REPLICATION [replicate] documentidentificatie #" + zaakdocumentidentificatie);

					var zdsUrl = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefZaakdocumentLezen().getUrl(); 
					var zdsSoapAction = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefZaakdocumentLezen().getSoapaction(); 
					var zdsRequest = new ZdsReplicateGeefZaakdocumentLezenLv01(); 
					 zdsRequest.stuurgegevens = this.converter.getZdsDocument().stuurgegevens; 
					 zdsRequest.parameters = new ZdsParameters(); //zdsRequest.parameters.setSortering("0");
					 zdsRequest.parameters.setIndicatorVervolgvraag("false"); 
					 zdsRequest.gelijk = new ZdsZaakDocument(); 
					 zdsRequest.gelijk.identificatie = zaakidentificatie;
					 zdsRequest.scope= new ZdsScope(); 
					 zdsRequest.scope.object = new ZdsZaak();
					 zdsRequest.scope.object.heeftRelevant = new ZdsHeeftRelevant();
					 zdsRequest.scope.object.heeftRelevant.gerelateerde = new ZdsZaakDocument();

					 ZDSClient zdsClient = SpringContext.getBean(ZDSClient.class);					 
					 var zdsResponse = zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest); 
					 // fetch the document details
					 log.debug("getGeefZaakdocumentLezen response:" + zdsResponse.getBody().toString()); 					 
					 var zdsEdcLa01 =  (ZdsEdcLa01GeefZaakdocumentLezen) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsEdcLa01GeefZaakdocumentLezen.class);
					 var zdsDocument = zdsEdcLa01.antwoord.document.get(0);
					 
					 // put the zaak in the object, so voegZaakDocument works as expected
					 zdsDocument.isRelevantVoor = new ZdsIsRelevantVoor();
					 zdsDocument.isRelevantVoor.gerelateerde = new ZdsGerelateerde();
					 zdsDocument.isRelevantVoor.gerelateerde.identificatie = zaakidentificatie;
					 
					 log.info("received data from zds-zaaksysteem, now storing in zgw-zaaksysteem");
					 this.converter.getZaakService().voegZaakDocumentToe(rsin, zdsDocument);					 
				}
				else {
					log.info("REPLICATION [skip] documentidentificatie #" + zaakdocumentidentificatie);					
					// TODO: check if zaak-relation is there
				}				
			}
		}
	}

	public ResponseEntity<?> proxy() {
		var url = this.converter.getTranslation().getLegacyservice();
		var soapaction = this.converter.getTranslation().getSoapAction();
		var request = this.converter.getContext().getRequestBody();
		log.info("relaying request to url: " + url + " with soapaction: " + soapaction + " request-size:"
				+ request.length());
		
		ZDSClient zdsClient = SpringContext.getBean(ZDSClient.class);					 
		return zdsClient.post(url, soapaction, request);
	}
}