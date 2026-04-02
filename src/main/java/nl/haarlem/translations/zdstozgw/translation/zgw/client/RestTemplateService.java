/*
 * Copyright 2020-2021 The Open Zaakbrug Contributors
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package nl.haarlem.translations.zdstozgw.translation.zgw.client;

import java.lang.invoke.MethodHandles;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

@Service
@Data
public class RestTemplateService {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private JWTService jwtService;

	RestTemplateBuilder restTemplateBuilder;

	private RestTemplate restTemplate;

	@Autowired
	public RestTemplateService(
			@Value("${nl.haarlem.translations.zdstozgw.trustAllCerts:false}") boolean trustAllCerts,
			@Value("${nl.haarlem.translations.zdstozgw.connectionRequestTimeout:30000}") int connectionRequestTimeout,
			@Value("${nl.haarlem.translations.zdstozgw.connectTimeout:30000}") int connectTimeout,
			@Value("${nl.haarlem.translations.zdstozgw.readTimeout:600000}") int readTimeout,
			@Value("${nl.haarlem.translations.zdstozgw.maxConnPerRoute:20}") int maxConnPerRoute,
			@Value("${nl.haarlem.translations.zdstozgw.maxConnTotal:100}") int maxConnTotal,
			RestTemplateBuilder restTemplateBuilder) {
	    if(trustAllCerts){
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        }
		this.restTemplate = restTemplateBuilder.build();
		this.restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(getAllCertsTrustingRequestFactory(
				connectionRequestTimeout, connectTimeout, readTimeout, maxConnPerRoute, maxConnTotal)));
	}

	private HttpComponentsClientHttpRequestFactory getAllCertsTrustingRequestFactory(int connectionRequestTimeout,
			int connectTimeout, int readTimeout, int maxConnPerRoute, int maxConnTotal) {

		HttpClientConnectionManager connectionManager;
		try {
			connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
					.setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
							.setSslContext(SSLContexts.custom().loadTrustMaterial(TrustAllStrategy.INSTANCE).build())
							.build())
					.setMaxConnTotal(maxConnTotal)
					.setMaxConnPerRoute(maxConnPerRoute)
					.build();
		} catch (Exception e) {
			log.error("Error creating trusting connection manager", e);
			throw new RuntimeException(e);
		}

		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(connectionManager)
				.build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectTimeout(connectTimeout);
		requestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
		// Note: setReadTimeout is not directly on requestFactory for HttpClient 5 in some Spring versions,
		// but it handles it via the client or properties.

		return requestFactory;
	}

	public HttpHeaders getHeaders() {
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept-Crs", "EPSG:4326");
		headers.set("Content-Crs", "EPSG:4326");
		headers.set("Authorization", "Bearer " + this.jwtService.getJWT());
		log.debug("headers:" + headers);

		return headers;
	}
}
