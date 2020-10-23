package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.xml.soap.SOAPConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.config.model.Configuration;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycleService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class LoggingRequestHandler extends RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private RequestResponseCycleService sessionService;

	public LoggingRequestHandler(Converter converter, ConfigService configService) {
		super(converter, configService);
		this.sessionService = SpringContext.getBean(RequestResponseCycleService.class);
	}

	@Override
	public ResponseEntity<?> execute() {
		log.debug("Executing request with handler: " + this.getClass().getCanonicalName() + " and converter: "
				+ this.converter.getClass().getCanonicalName());
		Configuration configuration = this.configService.getConfiguration();

		LocalDateTime start = LocalDateTime.now();
		RequestResponseCycle session = new RequestResponseCycle().setTimestamp(start)
				.setReferentienummer(this.getConverter().getContext().getReferentienummer())
				.setClientUrl(this.getConverter().getContext().getUrl())
				.setClientSoapAction(this.getConverter().getContext().getSoapAction())
				.setClientRequestBody(this.getConverter().getContext().getRequestBody())
				.setConverterImplementation(this.getConverter().getTranslation().getImplementation())
				.setConverterTemplate(this.getConverter().getTranslation().getTemplate());
		this.sessionService.save(session);

		this.converter.load();
		try {
			var response = this.converter.execute();
			session.setClientResponseBody(response.getBody().toString());
			session.setClientResponseCode(response.getStatusCodeValue());
			session.setDurationInMilliseconds(Duration.between(start, LocalDateTime.now()).toMillis());
			this.sessionService.save(session);

			return response;
		} catch (Exception ex) {
			log.warn("Exception handling request with handler: " + this.getClass().getCanonicalName()
					+ " and converter: " + this.converter.getClass().getCanonicalName(), ex);
			var fo03 = getErrorZdsDocument(ex, this.getConverter());
			var responseBody = XmlUtils.getSOAPFaultMessageFromObject(SOAPConstants.SOAP_RECEIVER_FAULT, ex.toString(),
					fo03);
			var response = new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
			// log this error response
			session.setClientResponseBody(response.getBody().toString());
			session.setClientResponseCode(response.getStatusCodeValue());
			session.setDurationInMilliseconds(Duration.between(start, LocalDateTime.now()).toMillis());
			session.setStackTrace(getStacktrace(ex));
			this.sessionService.save(session);
			return response;
		}
	}
}
