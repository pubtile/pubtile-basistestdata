package com.pubtile.basistestdata;

import com.pubtile.basistestdata.dialect.DatabaseProviderDialect;
import com.pubtile.basistestdata.dialect.MysqlProviderDialect;
import com.pubtile.basistestdata.dialect.OracleProviderDialect;
import com.pubtile.basistestdata.listener.DataPrepareEventListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.event.TestContextEvent;

/**
 * 配置类，装载bean
 *
 * @author jiayan
 * @version 0.0.1 2021-08-26
 * @since 0.0.1 2021-08-26
 */
@Configuration
public class BasisTestDataAutoConfiguration {

    @Bean("pubtileBasisTestDataDialectMysql")
    public MysqlProviderDialect mysqlProviderDialect(){
        return  new MysqlProviderDialect();
    }

    @Bean("pubtileBasisTestDataDialectOracle")
    public OracleProviderDialect oracleProviderDialect(){
        return  new OracleProviderDialect();
    }

    @Bean
    public PrepareDataManager prepareDataManager(@Qualifier("pubtileBasisTestDataDialectMysql") DatabaseProviderDialect mysqlProviderDialect, @Qualifier("pubtileBasisTestDataDialectOracle") DatabaseProviderDialect oracleProviderDialect){
        return new PrepareDataManager(mysqlProviderDialect,oracleProviderDialect);
    }

    @ConditionalOnClass({TestContextEvent.class})
    public class DataPrepareEventListenerConfiguration{
        @Bean
        public DataPrepareEventListener dataPrepareEventListener(PrepareDataManager pdm){
            DataPrepareEventListener dataPrepareEventListener =  new DataPrepareEventListener();
            dataPrepareEventListener.setPdm(pdm);
            return dataPrepareEventListener;
        }
    }

}
