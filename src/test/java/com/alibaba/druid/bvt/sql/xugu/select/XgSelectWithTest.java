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

public class XgSelectWithTest extends TestCase {

    public void testMultipleWith() {
        String sql = "WITH t1 (id) AS (\n" +
                "SELECT TB_TOP.*\n" +
                "FROM TB_TOP\n" +
                "), t2 (a, b, c) AS (\n" +
                "SELECT tb_top_merge.*\n" +
                "FROM tb_top_merge\n" +
                ")\n" +
                "SELECT t1.id, t2.c AS name\n" +
                "FROM t1 JOIN t2 ON t1.id = t2.b ORDER BY t1.id";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testWithFunction() {
        String sql = "WITH FUNCTION IF NOT EXISTS with_function(p_id IN NUMBER)  RETURN NUMBER AUTHID DEFINER COMMENT 'a' aS BEGIN RETURN p_id;END;SELECT with_function(id) FROM tb_top WHERE rownum = 1;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
    }

    public void testWithProcedure() {
        String sql = "WITH PROCEDURE with_procedure(p_id IN NUMBER) IS BEGIN DBMS_OUTPUT.put_line('p_id='||p_id); END; SELECT id FROM tb_top WHERE rownum = 1;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
    }
}
