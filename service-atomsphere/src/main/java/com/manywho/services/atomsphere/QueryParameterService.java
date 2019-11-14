package com.manywho.services.atomsphere;

import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.sql2o.Query;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.text.ParseException;
import java.util.UUID;

public class QueryParameterService {
    /**
     * This class need to be updated whith ContentTypeUtil
     *
     *
     * @param paramName
     * @param parameterValue
     * @param databaseType
     * @param query
     * @return
     * @throws DataBaseTypeNotSupported
     */
    public Query addParameterValueToTheQuery(String paramName, String parameterValue, String databaseType, Query query)
            throws ParseException {

        // there are some interesting types that jdbc detect as others, in those cases we use the code to detect the type
        // depending of the database type, these cases out of the jdbc standard are handled at the following switch
//        switch(databaseType) {
//            case ContentTypeUtil.SQL_SERVER_dATETIMEOFFSET_TEXT:
//                DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
//                return query.addParameter(paramName,  parser.parseDateTime(parameterValue));
//            case ContentTypeUtil.POSTGRESQL_UUID_TEXT:
//                return query.addParameter(paramName, UUID.fromString(parameterValue));
//            default:
//                break;
//        }

        // cases for jdbc standard types
        JDBCType type = JDBCType.valueOf(databaseType);

        switch (type){
            case BIT:
                return query.addParameter(paramName, Boolean.valueOf(parameterValue));

            case TINYINT:
                return query.addParameter(paramName, getInteger(parameterValue));

            case SMALLINT:
                return query.addParameter(paramName, getInteger(parameterValue));

            case INTEGER:
                return query.addParameter(paramName, getInteger(parameterValue));

            case BIGINT:
                return query.addParameter(paramName, Long.parseLong(parameterValue));

            case FLOAT:
                return query.addParameter(paramName, Double.parseDouble(parameterValue));

            case REAL:
                return query.addParameter(paramName, Float.parseFloat(parameterValue));

            case DOUBLE:
                return query.addParameter(paramName, Double.parseDouble(parameterValue));

            case NUMERIC:
            case DECIMAL:
                return query.addParameter(paramName, new BigDecimal(parameterValue));

            case CHAR:
                return query.addParameter(paramName, parameterValue);

            case VARCHAR:
                return query.addParameter(paramName, parameterValue);

            case LONGVARCHAR:
                return query.addParameter(paramName, parameterValue);

            case DATE:
                DateTime dateTime = new DateTime( parameterValue) ;
                return query.addParameter(paramName, dateTime);
            case TIME:
 //               throw new DataBaseTypeNotSupported("TIME");

            case TIMESTAMP:
                DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
                if (Strings.isNullOrEmpty(parameterValue)) {
                    return query.addParameter(paramName, (DateTime)null);
                } else {
                    return query.addParameter(paramName, parser.parseDateTime(parameterValue));
                }

            case BINARY:
//                throw new DataBaseTypeNotSupported("BINARY");

            case VARBINARY:
//                throw new DataBaseTypeNotSupported("VARBINARY");

            case LONGVARBINARY:
  //              throw new DataBaseTypeNotSupported("LONGVARBINARY");

            case NULL:
    //            throw new DataBaseTypeNotSupported("NULL");

            case OTHER:
      //          throw new DataBaseTypeNotSupported("OTHER");

            case JAVA_OBJECT:
        //        throw new DataBaseTypeNotSupported("JAVA_OBJECT");

            case DISTINCT:
//                throw new DataBaseTypeNotSupported("DISTINCT");

            case STRUCT:
  //              throw new DataBaseTypeNotSupported("STRUCT");

            case ARRAY:
    //            throw new DataBaseTypeNotSupported("ARRAY");

            case BLOB:
                return query.addParameter(paramName, parameterValue);

            case CLOB:
                return query.addParameter(paramName, parameterValue);

            case REF:
      //          throw new DataBaseTypeNotSupported("REF");

            case DATALINK:
        //        throw new DataBaseTypeNotSupported("DATALINK");

            case BOOLEAN:
                return query.addParameter(paramName, Boolean.valueOf(parameterValue));

            case ROWID:
 //               throw new DataBaseTypeNotSupported("ROWID");

            case NCHAR:
                return query.addParameter(paramName, parameterValue);

            case NVARCHAR:
                return query.addParameter(paramName, parameterValue);

            case LONGNVARCHAR:
                return query.addParameter(paramName, parameterValue);

            case NCLOB:
   //             throw new DataBaseTypeNotSupported("NCLOB");

            case SQLXML:
          //      throw new DataBaseTypeNotSupported("SQLXML");

            case REF_CURSOR:
          //      throw new DataBaseTypeNotSupported("REF_CURSOR");

            case TIME_WITH_TIMEZONE:
          //      throw new DataBaseTypeNotSupported("TIME_WITH_TIMEZONE");

            case TIMESTAMP_WITH_TIMEZONE:
          //      throw new DataBaseTypeNotSupported("TIMESTAMP_WITH_TIMEZONE");

            default:
         //       throw new DataBaseTypeNotSupported(String.valueOf(type));
        }
        return null;
    }

    private Integer getInteger(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return 0;
        } else {
            return Integer.parseInt(value);
        }
    }
}
