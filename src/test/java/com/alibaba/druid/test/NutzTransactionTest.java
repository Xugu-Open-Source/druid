package com.alibaba.druid.test;

import javax.sql.DataSource;

import com.xugu.pool.XgDataSource;
import org.junit.Assert;
import junit.framework.TestCase;

import org.nutz.dao.Chain;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import com.alibaba.druid.pool.DruidDataSource;

import java.io.FileWriter;
import java.io.PrintWriter;

public class NutzTransactionTest extends TestCase {

    private XgDataSource dataSource;

    protected void setUp() throws Exception {
        XgDataSource dataSource = new XgDataSource();
        Class.forName("com.xugu.cloudjdbc.Driver");
        dataSource.setUrl("jdbc:Xugu://127.0.0.1:5138/SYSTEM");
        dataSource.setUser("SYSDBA");
        dataSource.setPassword("SYSDBA");
        dataSource.setLogWriter(new PrintWriter(new FileWriter("D://outputTest.txt")));


        this.dataSource = dataSource;
    }

    public void test_trans() throws Exception {
        Dao dao = new NutDao(dataSource);

        dao.clear("TABLE1");
        // doTran1(dao);
        doTran2(dao);
    }

    void doTran1(final Dao dao) {
        try {
            Trans.exec(new Atom() {

                @Override
                public void run() {
                    dao.insert("TABLE1", Chain.make("name", "1"));
                    throw new RuntimeException();
                }
            });
        } catch (Exception e) {
        }
        Assert.assertEquals(0, dao.count("TABLE1"));
    }

    void doTran2(final Dao dao) {
        try {
            Trans.exec(new Atom() {

                @Override
                public void run() {
                    dao.insert("TABLE1", Chain.make("name", "1"));
                    dao.insert("TABLE1", Chain.make("name", "111111111111111111111111111111"));
                    throw new RuntimeException();
                }
            });
        } catch (Exception e) {
            // e.printStackTrace();
        }
        Assert.assertEquals(0, dao.count("TABLE1"));
    }

}
