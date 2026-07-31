package com.daf.backend.converter;

import com.daf.backend.service.EncryptionService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Converter
@Component
@AllArgsConstructor
public class CryptoConverter implements AttributeConverter<byte[], byte[]> {
    private final EncryptionService service;

    @Override
    public byte[] convertToDatabaseColumn(byte[] attribute) {
        if (attribute == null || attribute.length ==0) return null;

        return service.encryptBytes(attribute);
    }

    @Override
    public byte[] convertToEntityAttribute(byte[] dbData) {
        if (dbData == null || dbData.length ==0) return null;

        return service.decryptBytes(dbData);
    }
}
