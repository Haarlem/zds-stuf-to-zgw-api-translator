package nl.haarlem.translations.zdstozgw.converter;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
public class ConverterException extends RuntimeException {

	public String details;

	public ConverterException(String omschrijving) {
		super(omschrijving);
	}

	public ConverterException(String omschrijving, Throwable cause) {
		super(omschrijving);
		log.error(cause.getStackTrace().toString());
	}

	public ConverterException(String omschrijving, String details, Throwable cause) {
		super(omschrijving);
		this.details = details;
	}
}
