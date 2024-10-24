package com.alibaba.druid.xugu;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;

/**
 *虚谷不支持建表的脚本
 */
public class Xugu_getCreateTableScriptTest extends DbTestCase {
    public Xugu_getCreateTableScriptTest() {
        super("pool_config/xugu_db.properties");
    }

    public void test_oracle() throws Exception {
        Connection conn = getConnection();


        String createTableScript = JdbcUtils.getCreateTableScript(conn, JdbcConstants.XUGU);
        System.out.println(createTableScript);
        conn.close();
    }
}
