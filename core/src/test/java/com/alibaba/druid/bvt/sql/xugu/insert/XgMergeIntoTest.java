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

public class XgMergeIntoTest extends TestCase {

    public void test_update_delete_insert() {
        String sql = "MERGE INTO tb_top a USING (SELECT b.tid,b.NAME FROM tb_top_merge b) c\n" +
                "ON(a.id=c.tid)\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE SET a.name=c.NAME\n" +
                "    WHERE c.tid = 1 OR c.tid = 3\n" +
                "    DELETE WHERE a.id > 1\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT(a.id,a.NAME,amount,region,parent_id) VALUES(c.tid,c.NAME,4,'b',1);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
        System.out.println("----------------------------------");
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
    }

    public void test_insert_update_delete() {
        String sql = "MERGE INTO tb_top a USING (SELECT b.tid,b.NAME FROM tb_top_merge b) c \n" +
                "ON(a.id=c.tid)\n" +
                "WHEN NOT MATCHED THEN\n" +
                "INSERT(a.id,a.name) VALUES(c.tid,c.name)\n" +
                "WHEN MATCHED THEN\n" +
                "UPDATE SET a.name=c.NAME\n" +
                "WHERE c.tid = 1 OR c.tid = 3\n" +
                "DELETE WHERE a.id > 1;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_insert_default() {
        String sql = "MERGE INTO tb_top a\n" +
                "USING (\n" +
                "\tSELECT b.tid, b.NAME\n" +
                "\tFROM tb_top_merge b\n" +
                ") c ON (a.id = c.tid) \n" +
                "WHEN MATCHED THEN UPDATE \n" +
                "SET a.name = c.NAME\n" +
                "\tWHERE c.tid >= 1\n" +
                "\tDELETE WHERE a.id > 1\n" +
                "WHEN NOT MATCHED THEN INSERT  DEFAULT VALUES;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_insert_default1() {
        String sql = "MERGE INTO tb_top a\n" +
                "USING (\n" +
                "\tSELECT b.tid, b.NAME\n" +
                "\tFROM tb_top_merge b\n" +
                ") c ON (a.id = c.tid)\n" +
                "WHEN NOT MATCHED THEN INSERT  DEFAULT VALUES\n" +
                "WHEN MATCHED THEN UPDATE \n" +
                "SET a.name = c.name\n" +
                "\tWHERE c.tid >= 1\n" +
                "\tDELETE WHERE a.id > 1;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_insert_values_default() {
        String sql = "MERGE INTO tb_top a USING (SELECT b.tid,b.NAME FROM tb_top_merge b) c \n" +
                "ON(a.id=c.tid)\n" +
                "WHEN MATCHED THEN\n" +
                "UPDATE SET a.name=c.NAME\n" +
                "WHERE c.tid >= 1\n" +
                "DELETE  WHERE a.id > 1\n" +
                "WHEN NOT MATCHED THEN\n" +
                "INSERT(a.id,a.name) VALUES(c.tid,DEFAULT);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }


}
