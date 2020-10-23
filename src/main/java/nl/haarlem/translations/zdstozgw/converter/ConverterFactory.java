package nl.haarlem.translations.zdstozgw.converter;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

@Component
public class ConverterFactory {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ConfigService configService;
	private final ZaakService zaakService;

	private Object requestHandlerFactory;

	@Autowired
	public ConverterFactory(ConfigService configService, ZaakService zaakService) {
		this.configService = configService;
		this.zaakService = zaakService;
	}

	public Converter getConverter(RequestHandlerContext context) throws ResponseStatusException {
		Translation translation = this.configService.getTranslationByPathAndSoapAction(context.getUrl(),
				context.getSoapAction());

		if (translation == null) {
			String combinations = "";
			for (Translation t : this.configService.getConfiguration().getTranslations()) {
				combinations += "\n\tpath: '" + t.getPath() + "' soapaction: '" + t.getSoapAction() + "'";
			}
			log.error("Could not load a convertor for path: '" + context.getUrl() + "' with soapaction: '"
					+ context.getSoapAction() + "'");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Could not load a convertor for path: '" + context.getUrl() + "' with soapaction: '"
							+ context.getSoapAction() + "'\navailable services:" + combinations);
		}
		String classname = translation.implementation;
		try {
			Class<?> c = Class.forName(classname);
			java.lang.reflect.Constructor<?> ctor = c.getConstructor(RequestHandlerContext.class, Translation.class,
					ZaakService.class);
			Object object = ctor.newInstance(new Object[] { context, translation, this.zaakService });

			var converter = (Converter) object;
			return converter;
		} catch (Exception e) {
			log.error("error loading class:" + classname, e);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error loading class:" + classname, e);
		}
	}
}
