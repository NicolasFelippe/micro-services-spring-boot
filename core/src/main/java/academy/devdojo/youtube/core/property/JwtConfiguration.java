package academy.devdojo.youtube.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt.config")
@Data
public class JwtConfiguration {

    private final String loginUrl = "/login/**";
    @NestedConfigurationProperty
    private Header header = new Header();
    private int expiration = 3600;
    private String privateKey = "ipJc1xUMqXYVEc1JjmTsh3VjjXySG1nN";
    private String type = "encrypted";


    @Data
    public static class Header {
        private String name = "Authorization";
        private String prefix= "Bearer ";
    }
}
