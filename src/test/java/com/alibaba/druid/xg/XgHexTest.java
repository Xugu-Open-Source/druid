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

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.xugu.pool.XgDataSource;
import junit.framework.TestCase;


import com.xugu.cloudjdbc.Connection;
import com.xugu.cloudjdbc.PreparedStatement;
import com.xugu.cloudjdbc.ResultSet;

public class XgHexTest extends TestCase {
    final int COUNT = 800;
    
    private String          jdbcUrl;
    private String          user;
    private String          password;
    private String          driverClass;

    private XgDataSource dataSource;

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:xugu://localhost:5135/SYSTEM";
        user = "SYSDBA";
        password = "SYSDBA";
        driverClass = "com.mysql.jdbc.Driver";

        dataSource = new XgDataSource();
        Class.forName(driverClass);
        dataSource.setUrl(jdbcUrl);
//        dataSource.setPoolPreparedStatements(true);
        dataSource.setUser(user);
        dataSource.setPassword(password);

    }


    public void test_0() throws Exception {

        Connection conn = (Connection) dataSource.getConnection();

        String sql = "SELECT INSERT('Quadratic', 1, 4, 'What')";

        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sql);

        ResultSet rs = (ResultSet) stmt.executeQuery();
        JdbcUtils.printResultSet(rs);
        rs.close();
        stmt.close();

        conn.close();
    }

}
