package academy.devdojo.youtube.gateway.security.filter;

import academy.devdojo.youtube.core.property.JwtConfiguration;
import academy.devdojo.youtube.security.filter.JwtTokenAuthorizationFilter;
import academy.devdojo.youtube.security.token.converter.TokenConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class GatewayJwtTokenAuthorizationFilter extends JwtTokenAuthorizationFilter {

    public GatewayJwtTokenAuthorizationFilter(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
        super(jwtConfiguration, tokenConverter);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        try {
            String header = request.getHeader(jwtConfiguration.getHeader().getName());

            if (header == null && !header.startsWith(jwtConfiguration.getHeader().getPrefix())) {
                chain.doFilter(request, response);
                return;
            }

            String token = header.replace(jwtConfiguration.getHeader().getPrefix(), "").trim();
            String signedToken = tokenConverter.decryptToken(token);
            tokenConverter.validateTokenSignature(signedToken);

            if (jwtConfiguration.getType().equalsIgnoreCase("signed"))


                chain.doFilter(request, response);
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }


    }
}
