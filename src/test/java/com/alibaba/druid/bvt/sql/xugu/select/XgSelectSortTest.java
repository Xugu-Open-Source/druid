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
import junit.framework.TestCase;

import java.util.List;

public class XgSelectSortTest extends TestCase {

    public void testUsing_0() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name USING <;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(),xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testUsing_1() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name USING >;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(),xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testUsing_2() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name USING =;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(),xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testUsing_3() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name USING <=;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(),xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testUsing_4() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name USING >=;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(),xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testUsing_5() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name USING <>;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(),xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testDesc() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name desc;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void testAsc() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name ASC;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void testNullsFirst() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name ASC NULLS FIRST;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(),xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testNullsLast() {
        String sql = "SELECT * FROM TB_TOP tt ORDER BY name ASC NULLS LAST;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(),xuGuString.replaceAll("\\s+", " ").trim());
    }
}
