package kna.springsecurity.mfa;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;
import kna.springsecurity.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TwoFactorAuthenService {


    private final String ISSUER = "Authen-Author-Base"; 

    public String generateNewSecret (){
        return new DefaultSecretGenerator().generate();
    }

    public String generateQRCodeImgUri(User user) {

        QrData data = new QrData.Builder()
                        .label(ISSUER + ":" + user.getUsername())
                        .secret(user.getSecretKey())
                        .issuer(ISSUER)
                        .algorithm(HashingAlgorithm.SHA1)
                        .digits(6)
                        .period(30)
                        .build();
        QrGenerator generator = new ZxingPngQrGenerator();
        byte [] imageData = new byte[0];
        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e){
            throw new RuntimeException(e);
        }

        return Utils.getDataUriForImage(imageData, generator.getImageMimeType());
    }

    public boolean isOTPValid(String secret, String code) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        return verifier.isValidCode(secret,code);
    }

    public boolean isOTPNotValid (String secret, String code) {
        return !this.isOTPValid(secret, code);
    }
}
