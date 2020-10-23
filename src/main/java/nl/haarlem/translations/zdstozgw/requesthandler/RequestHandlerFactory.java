package nl.haarlem.translations.zdstozgw.requesthandler;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;

@Component
public class RequestHandlerFactory {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final ConfigService configService;

	@Autowired
	public RequestHandlerFactory(ConfigService configService) {
		this.configService = configService;
	}

	public RequestHandler getRequestHandler(Converter converter) throws ResponseStatusException {
		var classname = this.configService.getConfiguration().getRequestHandlerImplementation();
		try {
			Class<?> c = Class.forName(classname);
			java.lang.reflect.Constructor<?> ctor = c.getConstructor(Converter.class, ConfigService.class);
			Object object = ctor.newInstance(new Object[] { converter, this.configService });
			return (RequestHandler) object;
		} catch (Exception e) {
			log.error("error loading class:" + classname, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error loading class:" + classname, e);
		}
	}
}
