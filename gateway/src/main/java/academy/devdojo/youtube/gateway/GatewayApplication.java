package academy.devdojo.youtube.gateway;

import academy.devdojo.youtube.core.property.JwtConfiguration;
import academy.devdojo.youtube.gateway.security.filter.GlobalPreFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(value = JwtConfiguration.class)
@ComponentScan("academy.devdojo.youtube")
public class GatewayApplication {
    static final Logger logger =
            LoggerFactory.getLogger(GatewayApplication.class);
    public static void main(String[] args) {
        logger.info("iniciando getaway");
        SpringApplication.run(GatewayApplication.class, args);
    }



}
