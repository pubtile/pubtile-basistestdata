package com.pubtile.basistestdata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pubtile.basistestdata.dialect.DatabaseProviderDialect;
import com.pubtile.basistestdata.pojo.ColumnMetaData;
import com.pubtile.basistestdata.pojo.TestDataCell;
import com.pubtile.basistestdata.pojo.TestDataRow;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.transaction.TestContextTransactionUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 核心类，负责导入逻辑
 *
 * @author jiayan
 * @version 0.0.1 2021-08-24
 * @since 0.0.1 2021-08-24
 */
@Slf4j
public class PrepareDataManager {

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Map<String, Map<String, ColumnMetaData>> tableColumTypes = new HashMap<>();

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();


    private DatabaseProviderDialect mysqlProviderDialect;

    private DatabaseProviderDialect oracleProviderDialect;

    public PrepareDataManager(DatabaseProviderDialect mysqlProviderDialect, DatabaseProviderDialect oracleProviderDialect) {
        this.mysqlProviderDialect = mysqlProviderDialect;
        this.oracleProviderDialect = oracleProviderDialect;
    }

    /**
     * 功能描述: 导入数据
     * @param testContext
     * @param dataSourceName
     * @return void
     * @author jiayan
     * @version 0.6.17 2021/8/30
     * @since 0.6.17 2021/8/30
     */
    public void importTestData(TestContext testContext, String dataSourceName) throws IOException {

        Connection connection = getConnection(testContext, dataSourceName);

        //get the data file and  update
        Consumer<Pair<String,List<TestDataRow>>> cons = testData->{
            try {
                updateData(connection, testData.getKey(), testData.getValue());
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        };
        findTestDataAndAmend(testContext, cons);
    }

    private Connection getConnection(TestContext testContext, String dataSourceName) {
        //obtain the connection object.
        DataSource dataSource = TestContextTransactionUtils.retrieveDataSource(testContext, dataSourceName);
        Connection connection = DataSourceUtils.getConnection(dataSource);
        testContext.getApplicationContext().getResource("");
        return connection;
    }

    protected void findTestDataAndAmend(TestContext testContext, Consumer<Pair<String,List<TestDataRow>>> updater) throws IOException {
        String resourcePath=ClassUtils.convertClassNameToResourcePath(testContext.getTestClass().getName());
        String datFilePattern = "classpath:"+resourcePath+"_"+ testContext.getTestMethod().getName()+"&*.json";
        Resource[]  resources = resourcePatternResolver.getResources(datFilePattern);
        for(Resource resource :resources){
            String fileName = resource.getFilename();
            if (fileName == null){
                log.warn("File not found, {}"+ fileName);
                continue;
            }
            List<TestDataRow>  testDataRows = readFromJsonFile(resource);

            if (!testDataRows.isEmpty()){
                String tableName = fileName.substring(fileName.lastIndexOf("&") + 1, fileName.lastIndexOf("."));
                Assert.hasText(tableName,"the table name is not specified, please follow the naming convention, TestCLassName_TestMethod&TableName.json. file:"+fileName);
                updater.accept(new Pair<>(tableName,testDataRows));
            }
        }
    }

    /**
     * 提取指定table各个列的元数据
     * @param connection
     * @param tableName
     * @throws SQLException
     */
    private void extractColumnTypes(Connection connection, String tableName) throws SQLException {
        if (!tableColumTypes.containsKey(tableName)) {
            PreparedStatement ps=connection.prepareStatement("select * from " + tableName + " where 1=2");
            try( ResultSet rs=ps.executeQuery();){
                ResultSetMetaData rsmd=rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                Map<String, ColumnMetaData> columnTypes = new HashMap<>(8);
                for (int i = 1; i <= columnCount; i++) {
                    ColumnMetaData columnMetaData = ColumnMetaData.builder().columnName(rsmd.getColumnName(i).toLowerCase()).columnTypeName(rsmd.getColumnTypeName(i)).scale(rsmd.getScale(i)).columnDisplaySize(rsmd.getColumnDisplaySize(i)).build();
                    columnTypes.put(rsmd.getColumnName(i).toLowerCase(),columnMetaData);
                }
                tableColumTypes.put(tableName, columnTypes);
            }
        }
    }

    private int updateData(Connection connection ,String tableName, List<TestDataRow> testDataRows) throws SQLException {
        int  effectLineCount=0;
        DatabaseProviderDialect databaseProviderDialect = getDatabaseProviderDialect(connection);

        for (TestDataRow pojo : testDataRows) {
            String sql = generateUdateSql(tableName, pojo);
            extractColumnTypes(connection, tableName);
            List<Pair<Integer, Object>> params = new ArrayList<>();
            DatabaseProviderDialect finalDatabaseProviderDialect = databaseProviderDialect;
            int effectLine = 0;
            try(PreparedStatement ps=connection.prepareStatement(sql)){
                Map<String, ColumnMetaData> columnTypes = tableColumTypes.get(tableName);
                for (int i = 0,pi=1; i < pojo.getCells().size(); i++, pi++) {
                    TestDataCell field = pojo.getCells().get(i);
                    String fieldName =field.getC().toLowerCase();
                    ColumnMetaData columnMetaData = columnTypes.get(fieldName);
                    Assert.notNull(columnMetaData,"column is not found: "+fieldName);
                    params.add(finalDatabaseProviderDialect.prepareUpdateSqlByDataType(ps, pi, field, fieldName, columnMetaData));
                }
                ColumnMetaData columnMetaData = columnTypes.get(pojo.getPn());
                params.add(finalDatabaseProviderDialect.prepareUpdateSqlByDataType(ps, pojo.getCells().size() + 1, new TestDataCell(pojo.getPn(),pojo.getPv(),""), pojo.getPn(), columnMetaData));

                log.debug("execute sql：{}, replace params：{}", sql,params);
                effectLine = ps.executeUpdate();
            }

            if(effectLine == 0){
                log.warn("update row is 0, please verify your json data file, key:{}, value:{}",pojo.getPn(),pojo.getPv());
            }
            effectLineCount += effectLine;
        }
        return effectLineCount;
    }

    private String generateUdateSql(String tableName, TestDataRow pojo) {
        StringBuilder sb = new StringBuilder();
        for (TestDataCell field : pojo.getCells()) {
            if (StringUtils.isEmpty(sb)) {
                sb.append(field.getC()).append(" = ? ");
            } else {
                sb.append(" , " + field.getC()).append(" = ? ");
            }
        }
        return "update " + tableName + " set " +
                sb.toString() +
                "where " + pojo.getPn() + " = " + " ? ";

    }

    private DatabaseProviderDialect getDatabaseProviderDialect(Connection connection) throws SQLException {
        String productName = connection.getMetaData().getDatabaseProductName();
        DatabaseProviderDialect databaseProviderDialect = null;
        if (productName.startsWith("MySQL")||productName.startsWith("MariaDB")){
            databaseProviderDialect = mysqlProviderDialect;
        }else if(productName.startsWith("Oracle")){
            databaseProviderDialect = oracleProviderDialect;
        }else{
            throw new IllegalStateException("Unsupported database, mysql, mariadb and oracle is supported now. "+productName);
        }
        return databaseProviderDialect;
    }


    /**
     * 功能描述: 充文件读取数据
     * @param resource
     * @return java.util.List<com.pubtile.basistestdata.pojo.TestDataRow>
     * @author jiayan
     * @version 0.6.17 2021/9/2
     * @since 0.6.17 2021/9/2
     */
    private List<TestDataRow> readFromJsonFile(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            String content = FileCopyUtils.copyToString(reader);
            return JSON.parseArray(content, TestDataRow.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
