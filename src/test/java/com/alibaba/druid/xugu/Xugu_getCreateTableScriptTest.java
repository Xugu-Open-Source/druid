package com.alibaba.druid.xugu;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;

/**
 * Created by wenshao on 23/07/2017.
 */
public class Xugu_getCreateTableScriptTest extends DbTestCase {
    public Xugu_getCreateTableScriptTest() {
        super("pool_config/mysql_db.properties");
    }

    public void test_oracle() throws Exception {
        Connection conn = getConnection();

        String createTableScript = JdbcUtils.getCreateTableScript(conn, JdbcConstants.MYSQL);
        System.out.println(createTableScript);


        conn.close();
    }
}
