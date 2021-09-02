package com.pubtile.basistestdata.pojo;

import lombok.*;

/**
 * 数据库column的meta data
 *
 * @author jiayan
 * @version 0.6.17 2021-08-30
 * @since 0.6.17 2021-08-30
 */
@Data
@Builder
public class ColumnMetaData {
    @NonNull
    String columnName;
    @NonNull
    private String columnTypeName;
    private int scale;
    private int columnDisplaySize;

}
