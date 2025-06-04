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
import com.alibaba.druid.sql.dialect.xugu.parser.XuGuStatementParser;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuSchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class XgReplaceTest extends TestCase {

    public void test_replace() {
        String sql = "REPLACE INTO tb_top\n" +
                "        VALUES (1, now(), 2, 'a', 0) (13, now(), 2, 'a', 1);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_values_ident() {
        String sql = "DECLARE\n" +
                "\tSUBTYPE type_rowtype0 IS tb_top%ROWTYPE;\n" +
                "\tv_testRow type_rowtype0;\n" +
                "BEGIN\n" +
                "\tSELECT id INTO v_testRow.id FROM tb_top WHERE id IS NOT NULL ORDER BY id DESC LIMIT 1;\n" +
                "\tv_testRow.id := v_testRow.id + 1;\n" +
                "\tv_testRow.NAME := '' || sysdate;\n" +
                "\tREPLACE INTO tb_top values v_testRow;\n" +
                "END;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_values() {
        String sql = "REPLACE  INTO tb_top(id,name) VALUES ( 18,'aaabbb') (222,'aaabbb');";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_select_union() {
        String sql = "REPLACE INTO tb_top(id,name) (SELECT 18,'18-1' FROM dual) UNION all SELECT 19,'19-1' FROM dual ;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_default_values() {
        String sql = "REPLACE INTO tb_top DEFAULT VALUES;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }
}
