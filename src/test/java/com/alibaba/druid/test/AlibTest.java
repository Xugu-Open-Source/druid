package com.alibaba.druid.test;

import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;

import java.sql.Connection;

public class AlibTest extends TestCase {
    protected DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:xugu://localhost:5138/SYSTEM");
        dataSource.setUsername("SYSDBA");
        dataSource.setPassword("SYSDBA");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_for_alib() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }


}
