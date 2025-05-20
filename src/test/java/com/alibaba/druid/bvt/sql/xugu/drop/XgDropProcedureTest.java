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
package com.alibaba.druid.bvt.sql.xugu.drop;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class XgDropProcedureTest extends TestCase {

    public void testDropProcedure() {
        String sql = "DROP PROCEDURE PROC_TEST_PARAMETERLESS;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.XUGU);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, JdbcConstants.XUGU, null);
        System.out.println(xuGuString);
    }

    public void testExists() {
        String sql = "DROP PROCEDURE IF EXISTS PROC_TEST_PARAMETERLESS;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.XUGU);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, JdbcConstants.XUGU, null);
        System.out.println(xuGuString);
    }

    public void testCascade() {
        String sql = "DROP PROCEDURE IF EXISTS PROC_TEST_PARAMETERLESS CASCADE;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.XUGU);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, JdbcConstants.XUGU, null);
        System.out.println(xuGuString);
    }

    public void testRestrict() {
        String sql = "DROP PROCEDURE IF EXISTS PROC_TEST_PARAMETERLESS RESTRICT;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.XUGU);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, JdbcConstants.XUGU, null);
        System.out.println(xuGuString);
    }

}
