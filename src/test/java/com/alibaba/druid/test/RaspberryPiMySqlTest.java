package com.alibaba.druid.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.util.JdbcUtils;
import com.xugu.pool.XgDataSource;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by wenshao on 10/12/2016.
 */
public class RaspberryPiMySqlTest extends TestCase {
    private XgDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new XgDataSource();
        dataSource.setUrl("jdbc:Xugu://127.0.0.1:5138/SYSTEM?allowMultiQueries=true");
        dataSource.setUser("SYSDBA");
        dataSource.setPassword("SYSDBA");

        dataSource.getConnection().prepareStatement("SELECT 1");
    }

    protected void tearDown() throws Exception {
        dataSource.getConnection().close();
    }

    public void test_mysql() throws Exception {
        Connection connection = dataSource.getConnection();

        // 如果 getVariables 和 getGloablVariables 不是有效的方法，可以使用以下方法获取变量信息
        DatabaseMetaData metaData = connection.getMetaData();
        System.out.println("Database Product Name: " + metaData.getDatabaseProductName());
        System.out.println("Database Major Version: " + metaData.getDatabaseMajorVersion());
        System.out.println("Database Minor Version: " + metaData.getDatabaseMinorVersion());

        connection.close();
    }
}
