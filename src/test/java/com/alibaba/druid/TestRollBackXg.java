package com.alibaba.druid;

import com.xugu.pool.XgDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

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
public class TestRollBackXg {
	static XgDataSource xgDataSource;
	static Dao dao_xg;


//	static String url = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
//	static String user = "alibaba";
//	static String password = "ccbuauto";
//	static String driver = "oracle.jdbc.driver.OracleDriver";

	static String url = "jdbc:xugu://localhost:5138/SYSTEM";
	static String user = "SYSDBA";
	static String password = "SYSDBA";
	static String driver = "com.xugu.cloudjdbc.Driver";

//  jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
//  user = "alibaba";
//  password = "ccbuauto";

	@BeforeClass
	public static void init() throws PropertyVetoException, SQLException {


		xgDataSource = new XgDataSource();
		xgDataSource.setUrl(url);
		xgDataSource.setUser(user);
		xgDataSource.setPassword(password);
		xgDataSource.getConnection().getAutoCommit();

		dao_xg = new NutDao(xgDataSource);

		if (!dao_xg.exists("msg")) {
			dao_xg.execute(Sqls.create("create table msg(message INT unique key)")); // 数字类型字段
		}

	}

	@AfterClass
	public static void destroy() throws SQLException {
		xgDataSource.getConnection().close();
	}

	@Before
	public void before() {
		// 清空所有数据
		dao_xg.clear("msg");
	}

	@Test
	public void test_xg() {
		try {
			// 将两条插入语句包裹在一个事务内执行,第一条可以正常插入,第二条违背唯一索引约束,会抛异常,事务会回滚
			Trans.exec(new Atom() {
				@Override
				public void run() {
					dao_xg.insert("msg", Chain.make("message", "1234567"));

					dao_xg.insert("msg", Chain.make("message", "1234567"));
				}
			});
		} catch (Exception e) {

		}
		// abc也跟着回滚了
		Assert.assertNull(dao_xg.fetch("msg", Cnd.where("message", "=", "1234567")));
	}
}