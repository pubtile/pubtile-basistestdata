package com.pubtile.basistestdata.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据准备每一个cell的数据
 *
 * @author jiayan
 * @version 0.6.17 2021-08-23
 * @since 0.6.17 2021-08-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDataCell {

    /**
     * the column to be change
     * 需要修改的字段的名称
     */
    private String c;
    /**
     * the value of the column will be change to
     * 需要修改的字段的值
     */
    private String v;
    /**
     * type of the column
     * 需要修改的字段的类型，取值范围为i：整型， s: 字符串类型s
     */
    private String t;

}
