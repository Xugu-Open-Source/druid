package com.alibaba.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.xugu.pool.XgDataSource;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by wenshao on 18/05/2017.
 */
public abstract class XgDbTestCase extends TestCase {
    protected XgDataSource dataSource;

    protected final String resource;

    public XgDbTestCase(String resource) {
        this.resource = resource;
    }

    protected void setUp() throws Exception {
        this.dataSource = createDataSourceFromResource(resource);
    }

    static XgDataSource createDataSourceFromResource(String resource) throws IOException {
        Properties properties = new Properties();

        InputStream configStream = null;
        try {
            configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            properties.load(configStream);
        } finally {
            JdbcUtils.close(configStream);
        }

        XgDataSource dataSource = new XgDataSource();
        dataSource.setPro(properties);
        return dataSource;
    }

    protected void tearDown() throws Exception {
        dataSource.getConnection().close();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
