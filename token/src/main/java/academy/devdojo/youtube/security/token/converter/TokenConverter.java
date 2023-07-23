package academy.devdojo.youtube.security.token.converter;


import academy.devdojo.youtube.core.property.JwtConfiguration;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;

@Service
@Slf4j
public class TokenConverter {

    private final JwtConfiguration jwtConfiguration;

    public TokenConverter(JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
    }


    public String decryptToken(String encryptToken) {
        try {
            JWEObject jweObject = null;
            jweObject = JWEObject.parse(encryptToken);

            DirectDecrypter directDecrypter = null;
            directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());

            jweObject.decrypt(directDecrypter);

            return jweObject.getPayload().toSignedJWT().serialize();
        } catch (ParseException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        } catch (KeyLengthException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        } catch (JOSEException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void validateTokenSignature(String signedToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(signedToken);

            RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());

            if (!signedJWT.verify(new RSASSAVerifier(publicKey))) {
                throw new AccessDeniedException("Invalid token signature!");
            }
        } catch (ParseException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        } catch (AccessDeniedException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        } catch (JOSEException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

}
