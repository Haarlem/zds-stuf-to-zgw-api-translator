package nl.haarlem.translations.zdstozgw.converter.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.Converter.ConverterException;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.zds.client.ZDSClient;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwCompleteZaak;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;

public class ActualiseerZaakStatusConverter extends Converter {
	@Data
	private class ActualiseerZaakStatus_Bv03 {
		final XpathDocument xpathDocument;
		Document document;

		public ActualiseerZaakStatus_Bv03(String template) {
			this.document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument(template);
			this.xpathDocument = new XpathDocument(this.document);
		}
	}

	public ActualiseerZaakStatusConverter(String templatePath, String legacyService) {
		super(templatePath, legacyService);
	}

	@Override
	public String proxyZds(String soapAction, RequestResponseCycle session, ApplicationParameterRepository repository,String requestBody) {
		var zdsClient= new ZDSClient();
		String zdsResponse = zdsClient.post(session, zdsUrl, soapAction, requestBody);
		return zdsResponse;
	}

	@Override
	public String proxyZdsAndReplicateToZgw(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String requestBody) {
		try {

			ZakLk01 zakLk01 = (ZakLk01) XmlUtils.getStUFObject(requestBody, ZakLk01.class);
			ZdsZaak zdsZaak = zakLk01.object.get(1);
			var translator = new ZaakTranslator(zgwClient, config);
			translator.replicateZds2ZgwZaak(session, config, zakLk01.stuurgegevens, zdsZaak.identificatie);

			// to the legacy zaaksystem
			var zdsClient= new ZDSClient();
			String zdsResponse = zdsClient.post(session, zdsUrl, soapAction, requestBody);

			// also to openzaak
			String zgwResonse = convertToZgw(session, zgwClient, config, repository, requestBody);

			// the original response
			return zdsResponse;
		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		} catch (ZaakTranslator.ZaakTranslatorException zte) {
			throw new ConverterException(this, zte.getMessage(), requestBody, zte);
		}
	}

	@Override
	public String convertToZgwAndReplicateToZds(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String requestBody) {
		try {

			ZakLk01 zakLk01 = (ZakLk01) XmlUtils.getStUFObject(requestBody, ZakLk01.class);
			ZdsZaak zdsZaak = zakLk01.object.get(1);
			var translator = new ZaakTranslator(zgwClient, config);
			translator.replicateZds2ZgwZaak(session, config, zakLk01.stuurgegevens, zdsZaak.identificatie);

			// to openzaak
			String zgwResonse = convertToZgw(session, zgwClient, config, repository, requestBody);

			// also to the legacy zaaksystem
			var zdsClient= new ZDSClient();
			String zdsResponse = zdsClient.post(session, zdsUrl, soapAction, requestBody);

			// response
			return zgwResonse;
		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		} catch (ZaakTranslator.ZaakTranslatorException zte) {
			throw new ConverterException(this, zte.getMessage(), requestBody, zte);
		}		
	}

	@Override
	public String convertToZgw(RequestResponseCycle session, ZGWClient zgwClient, ConfigService configService,
			ApplicationParameterRepository repository, String requestBody) {
		try {
			/*
			 * ZakLk01 zakLk01 = (ZakLk01) XmlUtils.getStUFObject(requestBody,
			 * ZakLk01.class); var translator = new ZaakTranslator(zgwClient,
			 * configService); var zaak = translator.actualiseerZaakstatus(zakLk01); var
			 * bv03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
			 * bv03.setReferentienummer(zaak.getUuid()); return
			 * bv03.getSoapMessageAsString();
			 */
			ZakLk01 zakLk01 = (ZakLk01) XmlUtils.getStUFObject(requestBody, ZakLk01.class);
			var translator = new ZaakTranslator(zgwClient, configService);
			var zgwZaak = translator.actualiseerZaakstatus(zakLk01);

			var bv03 = new ActualiseerZaakStatus_Bv03(this.template);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:organisatie", zakLk01.stuurgegevens.ontvanger.organisatie);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:applicatie", zakLk01.stuurgegevens.ontvanger.applicatie);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:gebruiker", zakLk01.stuurgegevens.ontvanger.gebruiker);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:organisatie", zakLk01.stuurgegevens.zender.organisatie);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:applicatie", zakLk01.stuurgegevens.zender.applicatie);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:gebruiker", zakLk01.stuurgegevens.zender.gebruiker);
			bv03.xpathDocument.setNodeValue(".//stuf:referentienummer", zgwZaak.uuid);
			bv03.xpathDocument.setNodeValue(".//stuf:crossRefnummer", zakLk01.stuurgegevens.referentienummer);
			DateFormat tijdstipformat = new SimpleDateFormat("yyyyMMddHHmmss");
			bv03.xpathDocument.setNodeValue(".//stuf:tijdstipBericht", tijdstipformat.format(new Date()));
			return XmlUtils.xmlToString(bv03.document);

		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		} catch (ZaakTranslator.ZaakTranslatorException zte) {
			throw new ConverterException(this, zte.getMessage(), requestBody, zte);
		}
		/*
		 * } catch (Exception ex) { ex.printStackTrace(); var f03 = new
		 * nl.haarlem.translations.zdstozgw.translation.zds.model.F03();
		 * f03.setFaultString("Object was not saved"); f03.setCode("StUF046");
		 * f03.setOmschrijving("Object niet opgeslagen");
		 * f03.setDetails(ex.getMessage()); return f03.getSoapMessageAsString(); }
		 */
	}
}
