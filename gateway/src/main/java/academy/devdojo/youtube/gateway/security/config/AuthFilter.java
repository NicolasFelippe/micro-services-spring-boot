package academy.devdojo.youtube.gateway.security.config;

import academy.devdojo.youtube.core.property.JwtConfiguration;
import academy.devdojo.youtube.security.token.converter.TokenConverter;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.ParseException;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;
    protected final JwtConfiguration jwtConfiguration;
    protected final TokenConverter tokenConverter;

    public AuthFilter(WebClient.Builder webClientBuilder,
                      JwtConfiguration jwtConfiguration,
                      TokenConverter tokenConverter) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.jwtConfiguration = jwtConfiguration;
        this.tokenConverter = tokenConverter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("Missing authorization information");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            if (authHeader == null && !authHeader.startsWith(jwtConfiguration.getHeader().getPrefix())) {
                return chain.filter(exchange);
            }
            String token = authHeader.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();

           exchange.getRequest().mutate().header() StringUtils.equalsIgnoreCase("signed", jwtConfiguration.getType())
                    ? validate(token) : decreyptValidating(token);

            return null;

//            return webClientBuilder.build()
//                    .post()
//                    .uri("http://service-users/users/validateToken?token=" + parts[1])
//                    .retrieve().bodyToMono(UserDto.class)
//                    .map(userDto -> {
//                        exchange.getRequest()
//                                .mutate()
//                                .header("X-auth-user-id", String.valueOf(userDto.getId()));
//                        return exchange;
//                    }).flatMap(chain::filter);

        };
    }

    public static class Config {
        // empty class as I don't need any particular configuration
    }

    @SneakyThrows
    private SignedJWT decreyptValidating(String encryptedToken)  {
        String signedToken = tokenConverter.decryptToken(encryptedToken);
        tokenConverter.validateTokenSignature(signedToken);

        return SignedJWT.parse(signedToken);
    }

    @SneakyThrows
    private SignedJWT validate(String signedToken) {
        tokenConverter.validateTokenSignature(signedToken);

        return SignedJWT.parse(signedToken);
    }
}
