/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import oracle.jdbc.OracleConnection;

public class TestXgPing extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private String SQL;

    protected void setUp() throws Exception {
        // jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
        // user = "alibaba";
        // password = "ccbuauto";
        // SQL = "SELECT * FROM WP_ORDERS WHERE ID = ?";

        jdbcUrl = "jdbc:xugu://localhost:5135/SYSTEM";
        user = "SYSDBA";
        password = "SYSDBA";
        SQL = "SELECT * FROM TABLE1 WHERE ID = ?";

        driverClass = "com.xugu.cloudjdbc.Driver";
    }

    public void test_o() throws Exception {
        Class.forName(driverClass);
        Connection xgConn = DriverManager.getConnection(jdbcUrl, user, password);

        for (int i = 0; i < 10; ++i) {
            ping_1000(xgConn);
            select_1000(xgConn);
        }

        xgConn.close();
    }

    private void ping_1000(Connection xgConn) throws SQLException {
        long startMillis = System.currentTimeMillis();
        final int COUNT = 10000;
        for (int i = 0; i < COUNT; ++i) {
            pring(xgConn);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("ping : " + millis);
    }

    private void select_1000(Connection xgConn) throws SQLException {
        long startMillis = System.currentTimeMillis();
        final int COUNT = 10000;
        for (int i = 0; i < COUNT; ++i) {
            select(xgConn);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("select : " + millis);
    }

    public void pring(Connection xgConn) throws SQLException {
        xgConn.isValid(1000);
    }

    public void select(Connection xgConn) throws SQLException {
        Statement stmt = xgConn.createStatement();
        stmt.execute("SELECT 1 FROM TABLE1");
        stmt.close();
    }
}
