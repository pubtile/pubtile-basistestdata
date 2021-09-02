package com.pubtile.basistestdata.pojo;

import lombok.Data;

import java.util.List;

/**
 * 数据准备每一行数据
 *
 * @author jiayan
 * @version 0.6.17 2021-08-23
 * @since 0.6.17 2021-08-23
 */
@Data
public class TestDataRow {
    /**
     * primaryKey 主键名称，默认id
     */
    private String pn;
    /**
     * primaryValue 主键对应的值
     */
    private String pv;

    /**
     * 需要修改的各个列数据
     */
    private List<TestDataCell> cells;

}
