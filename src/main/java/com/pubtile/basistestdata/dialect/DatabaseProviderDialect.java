package com.pubtile.basistestdata.dialect;

import com.pubtile.basistestdata.pojo.ColumnMetaData;
import com.pubtile.basistestdata.pojo.TestDataCell;
import javafx.util.Pair;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 方言接口，提供对数据库的封装
 *
 * @author jiayan
 * @version 0.6.17 2021-08-27
 * @since 0.6.17 2021-08-27
 */
public interface DatabaseProviderDialect {
    /**
     * 功能描述: 根据需要导入的数据和相应字段的元数据，设置参数
     * @param pps
     * @param pi
     * @param field
     * @param fieldName
     * @param columnMetaData
     * @return javafx.util.Pair<java.lang.Integer, java.lang.Object>
     * @author jiayan
     * @throws SQLException
     * @version 0.6.17 2021/8/30
     * @since 0.6.17 2021/8/30
     */

    Pair<Integer, Object> prepareUpdateSqlByDataType(PreparedStatement pps, int pi, TestDataCell field, String fieldName, ColumnMetaData columnMetaData) throws SQLException;

}
