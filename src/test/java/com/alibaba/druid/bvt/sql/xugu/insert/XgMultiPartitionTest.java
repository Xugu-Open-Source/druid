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
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class XgMultiPartitionTest extends TestCase {

    public void testFirstPartition() {
        String sql = "INSERT FIRST WHEN mod(id, 8) = 0 THEN INTO tb_top1 VALUES (id, 'abcd') WHEN mod(id, 4) = 0 THEN INTO tb_top_merge PARTITION (part1) (id, tid, name) SELECT id, id, name FROM tb_top;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testAllPartition() {
        String sql = "INSERT ALL WHEN mod(id, 8) = 0 THEN INTO tb_top1 VALUES (id, 'abcd') WHEN mod(id, 4) = 0 THEN INTO tb_top_merge PARTITION (part1) (id, tid, name) SELECT id, id, name FROM tb_top;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testFirstSubPartition() {
        String sql = "INSERT FIRST WHEN mod(id, 6) = 0 THEN INTO tb_top1 VALUES (id, 'abcd') WHEN mod(id, 2) = 0 AND mod(id, 4) != 0 THEN INTO tb_top_merge SUBPARTITION (SUBPART2) (id, tid, name) SELECT id, id, name FROM tb_top;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testAllSubPartition() {
        String sql = "INSERT ALL WHEN mod(id, 6) = 0 THEN INTO tb_top1 VALUES (id, 'abcd') WHEN mod(id, 2) = 0 AND mod(id, 4) != 0  THEN INTO tb_top_merge SUBPARTITION (SUBPART2) (id, tid, name) SELECT id, id, name FROM tb_top;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testNoWhen() {
        String sql = "INSERT ALL INTO tb_top1 VALUES (id, 'all_PARTITION_no_when') INTO tb_top_merge PARTITION (part1) (id, tid, name) SELECT id, id, name FROM tb_top;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }
}
