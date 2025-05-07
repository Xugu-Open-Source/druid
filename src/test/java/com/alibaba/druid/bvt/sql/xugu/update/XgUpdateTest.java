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
import com.alibaba.druid.sql.dialect.xugu.parser.XuGuStatementParser;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuSchemaStatVisitor;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class XgUpdateTest extends TestCase {

    /**
     * 虚谷不支持 limit
     */
    public void test_rownum() {
        String sql = "UPDATE tb_top SET NAME = 'id>1 and ROWNUM <= 3' WHERE id > 1 and ROWNUM <= 2;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
    }

    public void test_base_table_refs_alias() {
        String sql = "UPDATE tb_top as t1(a,c),tb_top_merge AS t2 SET t1.c = '112233', t2.NAME = '456' WHERE t1.a > 1 AND t2.id < 3 ;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_base_table_refs_alias1() {
        String sql = "UPDATE tb_top t1(a,c),tb_top_merge AS t2 SET t1.c = '112233', t2.NAME = '456' WHERE t1.a > 1 AND t2.id < 3 ;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_update_target_list_alias() {
        String sql = "UPDATE  tb_top t1(a,c),tb_top_merge AS t2 SET c = 'a' , t2.NAME = 'b' WHERE a > 1 AND t2.id < 3 ;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_update_target_list_expr() {
        String sql = "UPDATE tb_top_merge AS t2 SET (t2.NAME,t2.year) =(SELECT 'SET t2.name', 100 FROM dual) WHERE t2.id < 3 ;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_update_target_list_expr1() {
        String sql = "UPDATE  tb_top t1(a,c),tb_top_merge AS t2 SET (c = 'a', t2.NAME,t2.year) =(SELECT 'aaa','SET t2.name', 100 FROM dual) WHERE a > 1 AND t2.id < 3 ;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_update_target_list_default() {
        String sql = "UPDATE TB_TOP t1 SET NAME = DEFAULT WHERE id > 1;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_opt_from_clause() {
        String sql = "UPDATE tb_top t1\nSET t1.NAME = t2.name\nFROM tb_top_merge t2\nWHERE t2.year = 30\n\tAND t1.id = t2.tid;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
        Assert.assertEquals(sql,SQLUtils.toXuGuString(stmt));
    }


    public void test_cursor() {
        String sql = "DECLARE\n" +
                "\tCURSOR cur IS SELECT tt.id FROM tb_top tt join tb_top_merge ttm ON tt.id = ttm.tid WHERE ttm.YEAR =30 FOR UPDATE;\n" +
                "BEGIN\n" +
                "\tOPEN cur;\n" +
                "\tWHILE cur%FOUND LOOP\n" +
                "\tUPDATE tb_top \n" +
                "\tSET NAME = '123' \n" +
                "\tFROM tb_top_merge t2\n" +
                "\tWHERE CURRENT OF cur;\n" +
                "    END LOOP;\n" +
                "\tCLOSE cur;\n" +
                "COMMIT;\n" +
                "END;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_returning() {
        String sql = "DECLARE\n" +
                "\tTYPE type_c3 IS TABLE OF tb_top%ROWTYPE;\n" +
                "\tvar_c3 type_c3;\n" +
                "BEGIN\n" +
                "\tUPDATE tb_top t1 \n" +
                "\tSET NAME = t2.name\n" +
                "\tFROM tb_top_merge t2\n" +
                "\tWHERE t1.id = t2.tid AND t2.YEAR = 30\n" +
                "\tRETURNING t1.*  BULK COLLECT INTO var_c3;\n" +
                "\tFOR i IN 1..var_c3.COUNT() LOOP\n" +
                "\t\tSEND_MSG('id:' || var_c3(i).id|| ',name:' || var_c3(i).name);\n" +
                "\tEND LOOP;\n" +
                "COMMIT;\n" +
                "END;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

}
