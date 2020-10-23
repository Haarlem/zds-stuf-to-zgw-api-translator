package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import nl.haarlem.translations.zdstozgw.config.SpringContext;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	private RequestResponseCycleService requestResponseCycleService;
	private ZgwRequestResponseCycle currentInterimRequestResponseCycle;
	private String referentienummer;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		this.requestResponseCycleService = SpringContext.getBean(RequestResponseCycleService.class);
		addRequestToDatabase(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		addResponseToDatabase(response);
		return response;
	}

	private void addRequestToDatabase(HttpRequest request, byte[] body) throws UnsupportedEncodingException {
		String referentienummer = (String) RequestContextHolder.getRequestAttributes().getAttribute("referentienummer",
				RequestAttributes.SCOPE_REQUEST);
		this.currentInterimRequestResponseCycle = new ZgwRequestResponseCycle().setReferentienummer(referentienummer)
				.setZgwRequestBody(new String(body, "UTF-8")).setZgwUrl(request.getURI().toString())
				.setZgwMethod(request.getMethodValue());

		this.requestResponseCycleService.add(this.currentInterimRequestResponseCycle);
	}

	private void addResponseToDatabase(ClientHttpResponse response) throws IOException {
		StringBuilder inputStringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
		String line = bufferedReader.readLine();
		while (line != null) {
			inputStringBuilder.append(line);
			inputStringBuilder.append('\n');
			line = bufferedReader.readLine();
		}
		this.requestResponseCycleService
				.add(this.currentInterimRequestResponseCycle.setZgwResponseBody(inputStringBuilder.toString())
						.setZgwResponseCode(response.getStatusCode().value()));
	}
}
