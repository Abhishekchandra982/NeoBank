package com.example.neobank.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES";

    private static final String SECRET  = System.getenv ("AES_SECRET_KEY");

    private static SecretKey getKey(){
        return new SecretKeySpec(SECRET.getBytes(), ALGORITHM);
    }

    public static String encrypt(String data){

        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());

            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        }catch (Exception e){
            throw new RuntimeException("Error while encrypting " , e);
        }
    }

    public static String decrypt(String encryptedData){
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey());

            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decoded));
        }catch (Exception e){
            throw new RuntimeException("Error while decrypting", e);
        }
    }
}
