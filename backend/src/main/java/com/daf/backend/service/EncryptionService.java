package com.daf.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.HexFormat;
// AES 256 ENCRYPTION SERVICE

@Service
public class EncryptionService {
    private final String masterKey;

    private static final String KDF_ALGO   = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 210_000;
    private static final int SALT_LEN   = 16;
    private static final int IV_LEN     = 12;
    private static final int TAG_BITS   = 128;
    private static final int KEY_BITS   = 256;

    public  EncryptionService(@Value("${pbm.master-key}") String masterKey) {
        if (masterKey == null || masterKey.isEmpty()) {
            throw new IllegalArgumentException("MasterKey is null or empty");
        }

        this.masterKey = masterKey;
    }

    public SecretKey deriveKey(byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KDF_ALGO);
            PBEKeySpec spec = new PBEKeySpec(masterKey.toCharArray(), salt, ITERATIONS, KEY_BITS);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Key-Ableitung fehlgeschlagen", e);
        }
    }

    public String[] encryptToFile(InputStream input, Path output) {   // gibt {orig, enc} zurück
        try {
            byte[] salt = new byte[SALT_LEN];
            byte[] iv   = new byte[IV_LEN];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            random.nextBytes(iv);

            SecretKey key = deriveKey(salt);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));

            MessageDigest shaOrig = MessageDigest.getInstance("SHA-256");  // Klartext
            MessageDigest shaEnc  = MessageDigest.getInstance("SHA-256");  // Ciphertext

            DigestInputStream in = new DigestInputStream(input, shaOrig);
            try (OutputStream out = Files.newOutputStream(output)) {
                out.write(salt);   // Header roh -> NICHT in shaEnc
                out.write(iv);
                // cipher -> shaEnc (hasht die verschlüsselten Bytes) -> Datei
                try (CipherOutputStream cipherOut =
                             new CipherOutputStream(new DigestOutputStream(out, shaEnc), cipher)) {
                    in.transferTo(cipherOut);
                }
            }

            return new String[]{
                    HexFormat.of().formatHex(shaOrig.digest()),
                    HexFormat.of().formatHex(shaEnc.digest())
            };

        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("Verschlüsselung fehlgeschlagen", e);
        }
    }

}

