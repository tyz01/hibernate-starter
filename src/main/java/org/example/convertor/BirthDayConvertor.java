package org.example.convertor;

import org.example.entity.BirthDay;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.util.Optional;

//@Converter(autoApply = true)
public class BirthDayConvertor implements AttributeConverter<BirthDay, Date> {
    @Override
    public Date convertToDatabaseColumn(BirthDay attribute) {
        return Optional.ofNullable(attribute)
                .map(BirthDay::birthDate)
                .map(Date::valueOf)
                .orElse(null);
    }

    @Override
    public BirthDay convertToEntityAttribute(Date dbData) {
        return Optional.ofNullable(dbData)
                .map(Date::toLocalDate)
                .map(BirthDay::new)
                .orElse(null);
    }
}
