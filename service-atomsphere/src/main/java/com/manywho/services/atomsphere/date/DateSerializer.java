package com.manywho.services.atomsphere.date;

import com.manywho.sdk.services.types.TypePropertyInvalidException;
import microsoft.sql.DateTimeOffset;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class DateSerializer {
    public static String serializeDate(String property, Object object) {
        if (object == null){
            return null;
        } else if (object instanceof TemporalAccessor) {

            return java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.format((TemporalAccessor) object);
        } else if (object instanceof DateTimeOffset) {
            java.sql.Timestamp timestamp =  ((DateTimeOffset) object).getTimestamp();
            Integer minutesOffset =  ((DateTimeOffset) object).getMinutesOffset();
            OffsetDateTime offsetDateTime = OffsetDateTime.of(timestamp.toLocalDateTime(),
                    ZoneOffset.ofTotalSeconds(minutesOffset*60));

            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime);
        } else if (object instanceof java.sql.Date) {
            ZonedDateTime dateTime = ((java.sql.Date) object).toLocalDate().atStartOfDay(ZoneId.of("UTC"));

            return java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime);
        } else if (object instanceof Date) {
            Instant instant = ((Date) object).toInstant();
            OffsetDateTime dateTime = OffsetDateTime.ofInstant(instant, ZoneId.of("UTC"));

            return java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime);
        }

        throw new TypePropertyInvalidException(property, "Date");
    }
}
