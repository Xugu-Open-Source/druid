package com.alibaba.druid.xugu;

import com.alibaba.druid.DbTestCase;

import java.sql.Connection;
import java.sql.Statement;

public class XuguInsertTest extends DbTestCase {
    public XuguInsertTest() {
        super("pool_config/xugu_db.properties");
    }
    public void test_for_mysql() throws Exception {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        stmt.execute("use school");

        stmt.close();
        conn.close();
    }
}
