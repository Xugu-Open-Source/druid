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
package com.alibaba.druid.bvt.sql.xugu.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class XgSelectQ_EscapeTest extends TestCase {

    public void testQ_Escape() {
        String sql = "SELECT q'[it's an example1]' FROM dual;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
        System.out.println(xuGuString);
    }

    public void testQ_Escape_1() {
        String sql = "SELECT q'{it's an example2}' FROM dual;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
        System.out.println(xuGuString);
    }

    public void testQ_Escape_2() {
        String sql = "SELECT q'(it's an example3)' FROM dual;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
        System.out.println(xuGuString);
    }

    public void testQ_Escape_3() {
        String sql = "SELECT q'<it's an example4>' FROM dual;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
        System.out.println(xuGuString);
    }

    public void testQ_Escape_4() {
        String sql = "SELECT q'\\it's an example5\\' FROM dual;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
        System.out.println(xuGuString);
    }

    public void testQ_Escape_5() {
        String sql = "SELECT Q'!it''s an example6!' FROM dual;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
        System.out.println(xuGuString);
    }

    public void testQ_Escape_6() {
        String sql = "SELECT id from tb_top WHERE name = Q'!one!';";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
        System.out.println(xuGuString);
    }

    public void testQ_Escape_7() {
        String sql = "SELECT b'0'Q'!on''e!' FROM dual;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
        System.out.println(xuGuString);
    }
}
