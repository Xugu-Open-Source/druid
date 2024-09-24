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

import junit.framework.TestCase;


public class TestSqlServer extends TestCase {
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
    public void test_0 () throws Exception {
        Class.forName(driverClass);
        
        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
        conn.close();
    }
}
