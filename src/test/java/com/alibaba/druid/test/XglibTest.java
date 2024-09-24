package com.alibaba.druid.test;

import com.xugu.pool.XgDataSource;
import junit.framework.TestCase;

import java.sql.Connection;

public class XglibTest extends TestCase {
    protected XgDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new XgDataSource();
        dataSource.setUrl("jdbc:Xugu://127.0.0.1:5135/SYSTEM");
        dataSource.setUser("SYSDBA");
        dataSource.setPassword("SYSDBA");
    }

    protected void tearDown() throws Exception {
        dataSource.getConnection().close();
    }

    public void test_for_alib() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }


}
