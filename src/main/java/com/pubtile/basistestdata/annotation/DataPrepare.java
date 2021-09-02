package com.pubtile.basistestdata.annotation;

import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * 该annotation来标识哪些测试类和方法是需要准备数据
 * 如果目标是类，就等同于该测试类的所有方法都用annotation标识了。
 * @author jiayan
 * @version 0.0.1 2021-08-01
 * @since 0.0.1 2021-08-01
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Transactional
@Rollback
public @interface DataPrepare {
    String dataSource() default "";
}
