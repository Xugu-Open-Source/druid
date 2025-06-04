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

public class XgSelectGroupTest extends TestCase {

    public void testRollup() {
        String sql = "SELECT region, name, SUM(amount) AS amo_sum , COUNT(*) AS row_cou FROM TB_TOP GROUP BY ROLLUP (region, name);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testCube() {
        String sql = "SELECT region, name, SUM(amount) AS amo_sum , COUNT(*) AS row_cou FROM TB_TOP GROUP BY CUBE (region, name);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testGroupingSets() {
        String sql = "SELECT region, name, SUM(amount) AS amo_sum , COUNT(*) AS row_cou FROM TB_TOP GROUP BY GROUPING SETS (region, name);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testGroup() {
        String sql = "SELECT region, name, SUM(amount) AS amo_sum , COUNT(*) AS row_cou FROM TB_TOP GROUP BY ();";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(),
                xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testGroup_1() {
        String sql = "SELECT\n" +
                "\tregion,\n" +
                "\tNAME,\n" +
                "\tSUM(amount) AS amo_sum ,\n" +
                "\tCOUNT(*) AS row_cou\n" +
                "FROM\n" +
                "\tTB_TOP\n" +
                "GROUP BY (), region,\n" +
                "\tROLLUP (region, NAME, ROLLUP (region, NAME)),\n" +
                "\tNAME,\n" +
                "\tCUBE (region, NAME, ()),\n" +
                "\tGROUPING SETS (region, NAME),\n" +
                "\t(),\n" +
                "\tNAME;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testGroup_2() {
        String sql = "SELECT * FROM TB_TOP GROUP BY (region, NAME);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

}
