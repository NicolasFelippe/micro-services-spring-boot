package academy.devdojo.youtube.security.filter;


import academy.devdojo.youtube.core.property.JwtConfiguration;
import academy.devdojo.youtube.security.token.converter.TokenConverter;
import academy.devdojo.youtube.security.util.SecurityContextUtil;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

@Slf4j
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {

    protected final JwtConfiguration jwtConfiguration;
    protected final TokenConverter tokenConverter;

    public JwtTokenAuthorizationFilter(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
        this.jwtConfiguration = jwtConfiguration;
        this.tokenConverter = tokenConverter;
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

            SecurityContextUtil.setSecurityContext(StringUtils.equalsIgnoreCase("signed", jwtConfiguration.getType())
                    ? validate(token) : decreyptValidating(token));

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

    private SignedJWT decreyptValidating(String encryptedToken) throws ParseException {
        String signedToken = tokenConverter.decryptToken(encryptedToken);
        tokenConverter.validateTokenSignature(signedToken);

        return SignedJWT.parse(signedToken);
    }

    private SignedJWT validate(String signedToken) throws ParseException {
        tokenConverter.validateTokenSignature(signedToken);

        return SignedJWT.parse(signedToken);
    }

}
