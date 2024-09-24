package com.alibaba.druid.xg;

import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;

import java.sql.Connection;
import java.util.concurrent.Executors;

/**
 * Created by wenshao on 10/08/2017.
 */
public class XgConnectFailTest extends TestCase {
    DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:xugu://localhost:5138/SYSTEM");
        dataSource.setUsername("SYSDBA");
        dataSource.setPassword("SYSDBA");

        dataSource.setCreateScheduler(Executors.newScheduledThreadPool(10));
    }
    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
