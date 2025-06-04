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
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class XgSelectJoinTest extends TestCase {

    public void testEqInnerJoin() {
        String sql = "SELECT * FROM tb_top t1 JOIN tb_top_merge t2 ON t1.id = t2.tid;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testNaturalJoin() {
        String sql = "SELECT * FROM tb_top t1 NATURAL JOIN tb_top_merge t2;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testUnequalInnerJoin() {
        String sql = "SELECT * FROM tb_top t1 INNER JOIN tb_top_merge t2 ON t1.id < t2.id AND t2.id < 300;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testLeftOuterJoin() {
        String sql = "SELECT * FROM tb_top t1 LEFT JOIN tb_top_merge t2 ON t1.id = t2.id;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testLeftOuterJoin_1() {
        String sql = "SELECT * FROM tb_top t1 LEFT OUTER JOIN tb_top_merge t2 ON t1.id = t2.id;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
    }

    public void testRightOuterJoin() {
        String sql = "SELECT * FROM tb_top t1 RIGHT JOIN tb_top_merge t2 ON t1.id = t2.id;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testRightOuterJoin_1() {
        String sql = "SELECT * FROM tb_top t1 RIGHT OUTER JOIN tb_top_merge t2 ON t1.id = t2.id;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
    }

    public void testFullOuterJoin() {
        String sql = "SELECT * FROM tb_top t1 FULL JOIN tb_top_merge t2 ON t1.id = t2.id;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testFullOuterJoin_1() {
        String sql = "SELECT * FROM tb_top t1 FULL OUTER JOIN tb_top_merge t2 ON t1.id = t2.id;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
    }

    public void testCrossJoin() {
        String sql = "SELECT * FROM tb_top t1 CROSS JOIN tb_top_merge t2;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }
}
