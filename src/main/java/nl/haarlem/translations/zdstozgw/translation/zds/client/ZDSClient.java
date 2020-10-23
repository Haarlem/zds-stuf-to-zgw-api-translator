package nl.haarlem.translations.zdstozgw.translation.zds.client;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.ZdsRequestResponseCycle;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.ZdsRequestResponseCycleRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsObject;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

@Service
public class ZDSClient {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ZdsRequestResponseCycleRepository repository;

	public ZDSClient() {
		this.repository = SpringContext.getBean(ZdsRequestResponseCycleRepository.class);

	}

	public ResponseEntity<?> post(String zdsUrl, String zdsSoapAction, ZdsObject zdsRequest) {
		var request = XmlUtils.getSOAPMessageFromObject(zdsRequest);
		return post(zdsUrl, zdsSoapAction, request);
	}

	public ResponseEntity<?> post(String zdsUrl, String zdsSoapAction, String zdsRequest) {
		log.info("Performing ZDS request to: '" + zdsUrl + "' for soapaction:" + zdsSoapAction);
		log.debug("Requestbody:\n" + zdsRequest);
		var method = new PostMethod(zdsUrl);
		try {
			method.setRequestHeader("SOAPAction", zdsSoapAction);
			method.setRequestHeader("Content-Type", "text/xml; charset=utf-8");
			StringRequestEntity requestEntity = new org.apache.commons.httpclient.methods.StringRequestEntity(
					zdsRequest, "text/xml", "utf-8");
			method.setRequestEntity(requestEntity);
			var httpclient = new org.apache.commons.httpclient.HttpClient();

			String referentienummer = (String) RequestContextHolder.getRequestAttributes()
					.getAttribute("referentienummer", RequestAttributes.SCOPE_REQUEST);
            ZdsRequestResponseCycle zdsRequestResponseCycle = getZdsRequestResponseCycle(zdsUrl, zdsSoapAction, zdsRequest, method, referentienummer);
            this.repository.save(zdsRequestResponseCycle);

			int responsecode = httpclient.executeMethod(method);
			String zdsResponseBody = method.getResponseBodyAsString();
			zdsRequestResponseCycle.setZdsResponseCode(responsecode);
			zdsRequestResponseCycle.setZdsResponseBody(zdsResponseBody);
			this.repository.save(zdsRequestResponseCycle);

			this.repository.save(zdsRequestResponseCycle);
			return new ResponseEntity<>(zdsResponseBody, HttpStatus.valueOf(responsecode));
		} catch (IOException ce) {
			throw new ConverterException(
					"Error: " + ce.toString() + " requesting url:" + zdsUrl + " with soapaction: " + zdsSoapAction, ce);
		} catch (java.lang.IllegalArgumentException iae) {
			throw new ConverterException(
					"Error " + iae.toString() + " requesting url:" + zdsUrl + " with soapaction: " + zdsSoapAction,
					iae);
		} finally {
			// Release current connection to the connection pool once you are done
			method.releaseConnection();
		}
	}

    private ZdsRequestResponseCycle getZdsRequestResponseCycle(String zdsUrl, String zdsSoapAction, String zdsRequest, PostMethod method, String referentienummer) {
        ZdsRequestResponseCycle session = new ZdsRequestResponseCycle();
        session.setReferentienummer(referentienummer);
        session.setZdsUrl(zdsUrl);
        session.setZdsMethod(method.getName());
        session.setZdsSoapAction(zdsSoapAction);
        session.setZdsRequestBody(zdsRequest);
        return session;
    }
}
