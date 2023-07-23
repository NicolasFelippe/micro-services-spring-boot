package academy.devdojo.youtube.security.token.creator;

import academy.devdojo.youtube.core.model.ApplicationUser;
import academy.devdojo.youtube.core.property.JwtConfiguration;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TokenCreator {

    private JwtConfiguration jwtConfiguration;

    public TokenCreator(JwtConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
    }

    @SneakyThrows
    public SignedJWT createSignedJWT(Authentication auth) {
        ApplicationUser appUser = (ApplicationUser) auth.getPrincipal();
        JWTClaimsSet jwtClaimsSet = createJWTClaimSet(auth, appUser);
        KeyPair rsaKeys = generateKeyPair();
        JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeys.getPublic()).keyID(UUID.randomUUID().toString()).build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                .jwk(jwk)
                .type(JOSEObjectType.JWT)
                .build(), jwtClaimsSet);

        RSASSASigner rsassaSigner = new RSASSASigner(rsaKeys.getPrivate());

        signedJWT.sign(rsassaSigner);
        System.out.println("teste");
        System.out.println(signedJWT.serialize());
        return signedJWT;
    }

    public JWTClaimsSet createJWTClaimSet(Authentication auth, ApplicationUser appUser) {
        return new JWTClaimsSet.Builder()
                .subject(appUser.getUsername())
                .claim("authorities", auth.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("userId", appUser.getId())
                .issuer("http://academy.devdojo")
                .expirationTime(new Date(System.currentTimeMillis() + (jwtConfiguration.getExpiration()) * 1000))
                .build();
    }

    @SneakyThrows
    private KeyPair generateKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        return generator.genKeyPair();
    }

    public String encryptToken(SignedJWT signedJWT) throws JOSEException {
        DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());

        JWEObject jweObject = new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                .contentType("JWT")
                .build(), new Payload(signedJWT));

        jweObject.encrypt(directEncrypter);

        return jweObject.serialize();
    }
}
