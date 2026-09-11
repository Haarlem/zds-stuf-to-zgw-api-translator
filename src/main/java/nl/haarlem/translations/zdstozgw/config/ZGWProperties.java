package nl.haarlem.translations.zdstozgw.config;

import lombok.Data;

import nl.haarlem.translations.zdstozgw.config.model.ZGWEndpoint;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "zgw")
public class ZGWProperties {
    private boolean replaceInternalUrlsWithBaseurl = false;
    private ZGWEndpoint endpoint = new ZGWEndpoint();
}
