package com.pubtile.basistestdata.dialect;

import com.pubtile.basistestdata.PrepareDataManager;
import com.pubtile.basistestdata.pojo.ColumnMetaData;
import com.pubtile.basistestdata.pojo.TestDataCell;
import javafx.util.Pair;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * mysql 的方言执行类
 *
 * @author jiayan
 * @version 0.0.1 2021-08-26
 * @since 0.0.1 2021-08-26
 */
public class OracleProviderDialect implements DatabaseProviderDialect {
    private static final String CHAR = "CHAR";
    private static final String NUMBER = "NUMBER";
    private static final String DATE = "DATE";
    private static final String TIMESTAMP = "TIMESTAMP";

    @Override
    public Pair<Integer, Object> prepareUpdateSqlByDataType(PreparedStatement pps, int pi, TestDataCell field, String fieldName, ColumnMetaData columnMetaData) throws SQLException {
        String  type = columnMetaData.getColumnTypeName();
        if(type.indexOf(CHAR)>-1){
            pps.setString(pi, field.getV());
            return new Pair<>(pi,field.getV());
        }else if(type.indexOf(NUMBER)>-1){
            if(columnMetaData.getScale()>0){
                pps.setBigDecimal(pi, new BigDecimal(field.getV()));
                return new Pair<>(pi, new BigDecimal(field.getV()));
            }else{
                pps.setLong(pi, Long.parseLong(field.getV()));
                return new Pair<>(pi, Long.parseLong(field.getV()));
            }
        }else if(type.indexOf(DATE)>-1 || type.indexOf(TIMESTAMP)>-1){
            pps.setTimestamp(pi, java.sql.Timestamp.valueOf(LocalDateTime.parse(field.getV(), PrepareDataManager.DATETIME_FORMATTER)));
            return new Pair<>(pi, java.sql.Timestamp.valueOf(LocalDateTime.parse(field.getV(), PrepareDataManager.DATETIME_FORMATTER)));
        }
        else{
            throw new IllegalStateException("the type is not support yet, " + type + " for " + fieldName);
        }
    }
}
