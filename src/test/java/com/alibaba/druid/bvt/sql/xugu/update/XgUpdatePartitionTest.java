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
package com.alibaba.druid.bvt.sql.xugu.update;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class XgUpdatePartitionTest extends TestCase {

    public void testPartition() {
        String sql = "UPDATE tb_top_merge  PARTITION (part1) t SET t.tid = t.id + 1 WHERE t.id = 10;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testPartition_1() {
        String sql = "UPDATE tb_top_merge PARTITION (part2) SET tid = id WHERE id = 10;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testSubPartition() {
        String sql = "UPDATE tb_top_merge SUBPARTITION (SUBPART1) SET tid = id + 1 WHERE id = 11;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testSubPartition_1() {
        String sql = "UPDATE tb_top_merge SUBPARTITION (SUBPART2) t SET t.tid = t.id WHERE t.id = 11;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.XUGU, true);
        String xuGuString = SQLUtils.toSQLString(statementList, JdbcConstants.XUGU);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }
}
