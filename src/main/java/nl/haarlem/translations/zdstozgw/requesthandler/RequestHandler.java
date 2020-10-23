package nl.haarlem.translations.zdstozgw.requesthandler;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDetailsXML;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;

@Data
public abstract class RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	protected Converter converter;
	protected ConfigService configService;

	public RequestHandler(Converter converter, ConfigService configService) {
		this.converter = converter;
		this.configService = configService;
	}

	protected String getStacktrace(Exception ex) {
		var swriter = new java.io.StringWriter();
		var pwriter = new java.io.PrintWriter(swriter);
		ex.printStackTrace(pwriter);
		var stacktrace = swriter.toString();

		return stacktrace;
	}

	protected ZdsFo03 getErrorZdsDocument(Exception ex, Converter convertor) {
		log.warn("request for path: /" + this.converter.getContext().getUrl() + "/ with soapaction: "
				+ this.converter.getContext().getSoapAction(), ex);

		var fo03 = this.converter.getZdsDocument() != null
				? new ZdsFo03(this.converter.getZdsDocument().stuurgegevens, convertor.getContext().referentienummer)
				: new ZdsFo03();
		fo03.body = new ZdsFo03.Body();
		fo03.body.code = "StUF058";
		fo03.body.plek = "server";
		var omschrijving = ex.toString();
		// max 200 chars
		if (omschrijving.length() > 200) {
			omschrijving = omschrijving.substring(omschrijving.length() - 200);
		}
		fo03.body.omschrijving = omschrijving;
		if (ex instanceof ConverterException) {
			var ce = (ConverterException) ex;
			fo03.body.details = ce.details;
		} else {
			fo03.body.details = getStacktrace(ex);
		}
		// maxlength
		if (fo03.body.details != null && fo03.body.details.length() >= 1000) {

			fo03.body.details = fo03.body.details.substring(0, 1000);
		}
		fo03.body.detailsXML = new ZdsDetailsXML();
		// TODO: put the xml in DetailsXml, without escaping
		fo03.body.detailsXML.todo = this.converter.getContext().getRequestBody();
		return fo03;
	}

	public abstract ResponseEntity<?> execute();
}
