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
package com.alibaba.druid.bvt.sql.xugu.insert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuInsertStatement;
import com.alibaba.druid.sql.dialect.xugu.parser.XuGuStatementParser;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuSchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class XgInsertTest extends TestCase {

    public void test_values_ident() {
        String sql = "DECLARE\n" +
                "\tSUBTYPE type_rowtype0 IS tb_top%ROWTYPE;\n" +
                "\tv_testRow type_rowtype0;\n" +
                "BEGIN\n" +
                "\tSELECT id INTO v_testRow.id FROM tb_top WHERE id IS NOT NULL ORDER BY id DESC LIMIT 1;\n" +
                "\tv_testRow.id := v_testRow.id + 1;\n" +
                "\tv_testRow.NAME := '' || sysdate;\n" +
                "\tinsert INTO tb_top values v_testRow;\n" +
                "END;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }


    public void test_ignore_select() {
        String sql = "insert IGNORE INTO tb_top (SELECT 18,'aaabbb' FROM dual) SELECT 222,'aaabbb' FROM dual;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuInsertStatement insertStmt = (XuGuInsertStatement) stmt;
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(insertStmt));
    }


    public void test_returning() {
        String sql = "DECLARE\n" +
                "\tTYPE type_table_var2 IS TABLE OF tb_top%ROWTYPE;\n" +
                "\ttable_var2 type_table_var2;\n" +
                "BEGIN\n" +
                "\tinsert INTO tb_top values (1,'111a')\n" +
                "\t\tRETURNING tb_top.id, tb_top.NAME  BULK COLLECT INTO table_var2;\n" +
                "END;\n";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }


    public void test_returning2() {
        String sql = "DECLARE\n" +
                "var_id NUMBER;\n" +
                "var_name VARCHAR;\n" +
                "BEGIN\n" +
                "insert INTO tb_top values (1,'222')\n" +
                "\tRETURNING tb_top.id ,tb_top.name  INTO var_id, var_name;\n" +
                "END;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_default_values() {
        String sql = "INSERT  INTO tb_top DEFAULT VALUES;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }


}
