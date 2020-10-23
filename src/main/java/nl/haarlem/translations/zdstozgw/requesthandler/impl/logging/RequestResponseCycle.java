package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;

@Entity
@Data
public class RequestResponseCycle {
	@Id
	@GeneratedValue
	private long id;
	private String referentienummer;

	private String converterImplementation;
	private String converterTemplate;

	private LocalDateTime timestamp;
	private long durationInMilliseconds;
	private String clientUrl;
	private String clientSoapAction;
	@Lob
	private String clientRequestBody;
	@Lob
	private String clientResponseBody;
	private int clientResponseCode;

	@Lob
	private String stackTrace;
}
