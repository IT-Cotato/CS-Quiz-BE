package cotato.csquiz.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EncryptService {

    private final AesBytesEncryptor aesBytesEncryptor;

    public String encryptPhoneNumber(final String rawPhoneNumber) {
        byte[] encrypt = aesBytesEncryptor.encrypt(rawPhoneNumber.getBytes(StandardCharsets.UTF_8));
        System.out.println(Arrays.toString(encrypt));
        return byteArrayToString(encrypt);//base64로 인코딩
    }

    public String decryptPhoneNumber(final String encryptedPhoneNumber) {
        byte[] bytes = stringToByteArray(encryptedPhoneNumber);//다시 bytes로 변환
        byte[] decrypt = aesBytesEncryptor.decrypt(bytes);//원래 bytes로 변경
        System.out.println(Arrays.toString(bytes));
        System.out.println(Arrays.toString(decrypt));
//        return byteArrayToString(decrypt);
        return new String(decrypt, StandardCharsets.UTF_8);//String으로 변환
    }

    public String byteArrayToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public byte[] stringToByteArray(String byteString) {
        return Base64.getDecoder().decode(byteString);
    }
}
