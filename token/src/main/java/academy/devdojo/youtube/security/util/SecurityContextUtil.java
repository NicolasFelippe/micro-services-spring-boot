package academy.devdojo.youtube.security.util;

import academy.devdojo.youtube.core.model.ApplicationUser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class SecurityContextUtil {


    private SecurityContextUtil() {

    }

    public static void setSecurityContext(SignedJWT signedJWT) {
        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            String username = claims.getSubject();
            if (username == null) {
                throw new JOSEException("Username is missing from JWT");
            }
            List<String> authorities = claims.getStringListClaim("authorities");
            ApplicationUser appUser = new ApplicationUser();
            appUser.setId(claims.getLongClaim("userId"));
            appUser.setUsername(username);
            appUser.setRole(String.join(",", authorities));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(appUser, null, createAuthorities(authorities));
            auth.setDetails(signedJWT.serialize());

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }
    }


    private static List<SimpleGrantedAuthority> createAuthorities(List<String> authorities) {
        return authorities
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
