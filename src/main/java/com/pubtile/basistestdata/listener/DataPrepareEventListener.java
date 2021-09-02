package com.pubtile.basistestdata.listener;

import com.pubtile.basistestdata.PrepareDataManager;
import com.pubtile.basistestdata.annotation.DataPrepare;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.event.BeforeTestExecutionEvent;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 通过  Test Execution Events 这个5.2的新功能
 *
 * @author jiayan
 * @version 0.6.17 2021-08-30
 * @since 0.6.17 2021-08-30
 */

@Getter @Setter
public class DataPrepareEventListener {

    @Autowired
    private PrepareDataManager pdm;

    /**
     * 功能描述: 准备数据
     * @param event
     * @return void
     * @author jiayan
     * @version 0.6.17 2021/8/30
     * @since 0.6.17 2021/8/30
     */
    @EventListener
    public void handleTestExecutionEvent(BeforeTestExecutionEvent event) throws SQLException, IOException {
        TestContext testContext = event.getTestContext();
        DataPrepare dataPrepare = testContext.getTestMethod().getAnnotation(DataPrepare.class);
        if (dataPrepare != null) {
            pdm.importTestData(testContext, dataPrepare.dataSource());
        }
    }
}