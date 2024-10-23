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
package com.alibaba.druid.xg;

import com.xugu.pool.XgDataSource;
import junit.framework.TestCase;

import java.sql.*;

public class XgBigTableTest extends TestCase {
    final int COUNT = 800;
    
    private String          jdbcUrl;
    private String          user;
    private String          password;
    private String          driverClass;

    private XgDataSource dataSource;

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:xugu://localhost:5138/SYSTEM";
        user = "SYSDBA";
        password = "SYSDBA";
        driverClass = "com.xugu.cloudjdbc.Driver";

        dataSource = new XgDataSource();
        DriverManager.registerDriver(new com.xugu.cloudjdbc.Driver());
        dataSource.setUrl(jdbcUrl);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        createTable();
    }

    protected void tearDown() throws Exception {
        try {
            dropTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dataSource != null) {
            dataSource.getConnection().close();
        }
    }

    public void test_0() throws Exception {
        
        StringBuffer ddl = new StringBuffer();
        ddl.append("INSERT INTO t_big (");
        for (int i = 0; i < COUNT; ++i) {
            if (i != 0) {
                ddl.append(", ");
            }
            ddl.append("F" + i);
        }
        ddl.append(") VALUES (");
        for (int i = 0; i < COUNT; ++i) {
            if (i != 0) {
                ddl.append(", ");
            }
            ddl.append("?");
        }
        ddl.append(")");

        Connection conn = dataSource.getConnection();

        System.out.println(ddl.toString());

        PreparedStatement stmt = conn.prepareStatement(ddl.toString());

        for (int i = 0; i < COUNT; ++i) {
            stmt.setInt(i + 1, i);
        }
        stmt.execute();
        stmt.close();

        conn.close();
    }

    private void dropTable() throws SQLException {

        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE t_big");
        stmt.close();

        conn.close();
    }

    private void createTable() throws SQLException {
        StringBuffer ddl = new StringBuffer();
        ddl.append("CREATE TABLE t_big (FID INT AUTO_INCREMENT PRIMARY KEY ");
        for (int i = 0; i < COUNT; ++i) {
            ddl.append(", ");
            ddl.append("F" + i);
            ddl.append(" BIGINT NULL");
        }
        ddl.append(")");

        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();
        stmt.execute(ddl.toString());
        stmt.close();

        conn.close();
    }
}
