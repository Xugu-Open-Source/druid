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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.xugu.parser.XuGuStatementParser;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class XgSelectSubqueryTest extends TestCase {

    public void testSubqueryWithTargetList() {
        String sql = "SELECT id, name, ( SELECT name FROM tb_top_merge WHERE tid = tb_top.id) FROM tb_top;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, JdbcConstants.XUGU, null);
        System.out.println(xuGuString);
    }

    public void testSubqueryWithAll() {
        String sql = "SELECT * FROM tb_top WHERE id > ALL(SELECT tid FROM tb_top_merge WHERE tid =1 or tid =3);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, JdbcConstants.XUGU, null);
        System.out.println(xuGuString);
    }

    public void testSubqueryWithAny() {
        String sql = "SELECT * FROM tb_top WHERE id > ANY(SELECT tid FROM tb_top_merge WHERE tid =1 or tid =3);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, JdbcConstants.XUGU, null);
        System.out.println(xuGuString);
    }

    public void testSubqueryWithExists() {
        String sql = "SELECT * FROM tb_top WHERE EXISTS(SELECT id FROM tb_top_merge WHERE id = tb_top.id);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, JdbcConstants.XUGU, null);
        System.out.println(xuGuString);
    }
}
