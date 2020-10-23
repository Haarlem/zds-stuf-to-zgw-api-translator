package nl.haarlem.translations.zdstozgw.controller;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.ConverterFactory;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerFactory;

@RestController
public class SoapController {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ConverterFactory converterFactory;
	private final ConfigService configService;
	private final RequestHandlerFactory requestHandlerFactory;

	@Autowired
	public SoapController(ConverterFactory converterFactory, ConfigService configService,
			RequestHandlerFactory requestHandlerFactory) {
		this.converterFactory = converterFactory;
		this.configService = configService;
		this.requestHandlerFactory = requestHandlerFactory;
	}


    /**
     * Does not handle any reqyests, returns a list of avaialble endpoints
     *
     * @return List of available endpoints
     */
	@GetMapping(path = { "/" }, produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<?> HandleRequest() {
		var context = new RequestHandlerContext("/", "", "");
		this.requestHandlerFactory.getRequestHandler(this.converterFactory.getConverter(context));
		return null;
	}

    /**
     * Receives the SOAP requests. Based on the configuration and path variables, the correct translation implementation is used.
     *
     * @param modus
     * @param version
     * @param protocol
     * @param endpoint
     * @param soapAction
     * @param body
     * @return ZDS response
     */
	@PostMapping(path = { "/{modus}/{version}/{protocol}/{endpoint}" },
                    consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
	public ResponseEntity<?> HandleRequest(
			@PathVariable String modus, @PathVariable String version, @PathVariable String protocol,
			@PathVariable String endpoint, @RequestHeader(name = "SOAPAction", required = true) String soapAction,
			@RequestBody String body) {

		var path = modus + "/" + version + "/" + protocol + "/" + endpoint;
		var context = new RequestHandlerContext(path, soapAction.replace("\"", ""), body);
		log.info("Processing request for path: /" + path + "/ with soapaction: " + soapAction
				+ " with referentienummer:" + context.getReferentienummer());

		RequestContextHolder.getRequestAttributes().setAttribute("referentienummer", context.getReferentienummer(),
				RequestAttributes.SCOPE_REQUEST);

		var converter = this.converterFactory.getConverter(context);
		var handler = this.requestHandlerFactory.getRequestHandler(converter);
		return handler.execute();
	}
}
