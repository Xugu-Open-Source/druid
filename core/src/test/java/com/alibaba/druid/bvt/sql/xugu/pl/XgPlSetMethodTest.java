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
package com.alibaba.druid.bvt.sql.xugu.pl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class XgPlSetMethodTest extends TestCase {

    public void testExtend() throws Exception {
        String sql ="DECLARE \n" +
                "\tsubtype rec IS RECORD(id INT, name VARCHAR(10));\n" +
                " \ttab TABLE OF rec;\n" +
                "BEGIN\n" +
                "\tFOR i IN 1 .. 3 LOOP\n" +
                "    tab.EXTEND;\n" +
                "    tab(tab.last) := rec(i, 'name' || i);\n" +
                " \tEND FOR;\n" +
                "\tFOR j IN 1 .. tab.count loop\n" +
                "\tsend_msg('id:'|| (tab(j).id) || ',name:'||(tab(j).NAME));\n" +
                "\tEND FOR;\n" +
                "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.xugu);
        String xgString = SQLUtils.toSQLString(stmts, DbType.xugu);
        System.out.println(xgString);
        System.out.println("-----------------------");
    }

}
