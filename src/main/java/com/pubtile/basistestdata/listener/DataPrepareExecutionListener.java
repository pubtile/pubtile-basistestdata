package com.pubtile.basistestdata.listener;

import com.pubtile.basistestdata.PrepareDataManager;
import com.pubtile.basistestdata.annotation.DataPrepare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;


/**
 * 一个 TestExecutionListener在测试用例前保证数据准备好
 * @author jiayan
 * @version 0.0.1 2021-08-07
 * @since 0.0.1 2021-08-07
 */
@Slf4j
public class DataPrepareExecutionListener extends AbstractTestExecutionListener {

    @Autowired
    PrepareDataManager pdm;

    @Override
    public int getOrder() {
        return 50000;
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        pdm=testContext.getApplicationContext().getBean(PrepareDataManager.class);
    }

    @Override
    public void beforeTestExecution(TestContext testContext) throws Exception {
        DataPrepare dataPrepare = testContext.getTestMethod().getAnnotation(DataPrepare.class);
        if (dataPrepare != null) {
            pdm.importTestData(testContext, dataPrepare.dataSource());
        }
    }
}
