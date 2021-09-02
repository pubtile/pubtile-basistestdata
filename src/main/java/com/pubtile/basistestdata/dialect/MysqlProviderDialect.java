package com.pubtile.basistestdata.dialect;

import com.pubtile.basistestdata.PrepareDataManager;
import com.pubtile.basistestdata.pojo.ColumnMetaData;
import com.pubtile.basistestdata.pojo.TestDataCell;
import javafx.util.Pair;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * mysql 的方言执行类
 *
 * @author jiayan
 * @version 0.0.1 2021-08-26
 * @since 0.0.1 2021-08-26
 */
public class MysqlProviderDialect implements DatabaseProviderDialect {

    @Override
    public Pair<Integer, Object> prepareUpdateSqlByDataType(PreparedStatement pps, int pi, TestDataCell field, String fieldName, ColumnMetaData columnMetaData) throws SQLException {
        String type = columnMetaData.getColumnTypeName();
        switch (type) {
            case "BIT":
                pps.setBoolean(pi, Boolean.valueOf(field.getV()));
                return new Pair<>(pi, Boolean.valueOf(field.getV()));
            case "TINYINT":
            case "TINYINT UNSIGNED":
            case "SMALLINT":
            case "SMALLINT UNSIGNED":
            case "MEDIUMINT":
            case "MEDIUMINT UNSIGNED":
                pps.setShort(pi, Short.parseShort(field.getV()));
                return new Pair<>(pi, Short.parseShort(field.getV()));
            case "INT":
            case "INT UNSIGNED":
                pps.setInt(pi, Integer.parseInt(field.getV()));
                return new Pair<>(pi, Integer.parseInt(field.getV()));
            case "BIGINT":
            case "BIGINT UNSIGNED":
                pps.setLong(pi, Long.parseLong(field.getV()));
                return new Pair<>(pi, Long.parseLong(field.getV()));
            case "DECIMAL":
                pps.setBigDecimal(pi, new BigDecimal(field.getV()));
                return new Pair<>(pi, new BigDecimal(field.getV()));
            case "VARCHAR":
            case "CHAR":
            case "JSON":
                pps.setString(pi, field.getV());
                return new Pair<>(pi, field.getV());
            case "DATE":
                pps.setDate(pi, java.sql.Date.valueOf(LocalDate.parse(field.getV(),PrepareDataManager.DATE_FORMATTER)));
                return new Pair<>(pi, java.sql.Date.valueOf(LocalDate.parse(field.getV(),PrepareDataManager.DATE_FORMATTER)));
            case "TIME":
                pps.setTime(pi, java.sql.Time.valueOf(LocalTime.parse(field.getV(),PrepareDataManager.TIME_FORMATTER)));
                return new Pair<>(pi, java.sql.Time.valueOf(LocalTime.parse(field.getV(),PrepareDataManager.TIME_FORMATTER)));
            case "TIMESTAMP":
            case "DATETIME":
                pps.setTimestamp(pi, java.sql.Timestamp.valueOf(LocalDateTime.parse(field.getV(), PrepareDataManager.DATETIME_FORMATTER)));
                return new Pair<>(pi, java.sql.Timestamp.valueOf(LocalDateTime.parse(field.getV(), PrepareDataManager.DATETIME_FORMATTER)));
            default:
                throw new IllegalStateException("the type is not support yet, " + type + " for " + fieldName);
        }
    }
}
