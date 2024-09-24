package com.alibaba.druid.xg;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.xugu.pool.XgDataSource;
import junit.framework.TestCase;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class QueryTimeoutTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:xugu://localhost:5135/SYSTEM");
        dataSource.setUsername("SYSDBA");
        dataSource.setPassword("SYSDBA");
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.setFilters("stat");
    }

    
    public void test_queryTimeout() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            
            String sql = "SELECT * FROM ws_product WHERE HAVE_IMAGE = 'N' AND ROWNUM < 1000";
            PreparedStatement stmt =  conn.prepareStatement(sql);
            stmt.setQueryTimeout(1);
            ResultSet rs = stmt.executeQuery();
            JdbcUtils.printResultSet(rs);
            rs.close();
            stmt.close();
            conn.close();
        }
        Connection conn =  dataSource.getConnection();
        
        String sql = "SELECT 'x' FROM TABLE1";
        PreparedStatement stmt =  conn.prepareStatement(sql);
        stmt.setQueryTimeout(1);
        ResultSet rs = stmt.executeQuery();
        JdbcUtils.printResultSet(rs);
        rs.close();
        stmt.close();
        conn.close();
    }
}
