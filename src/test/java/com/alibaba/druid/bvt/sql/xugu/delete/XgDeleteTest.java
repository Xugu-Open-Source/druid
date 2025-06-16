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
package com.alibaba.druid.bvt.sql.xugu.delete;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.xugu.parser.XuGuStatementParser;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuSchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class XgDeleteTest extends TestCase {

    /**
     * 虚谷不支持 limit
     */
    public void test_rownum() {
        String sql = "DELETE tb_top WHERE id > 1 AND ROWNUM <= 2;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
        System.out.println("--------------------------------");
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
    }

    public void test_from_clause() {
        String sql = "DELETE tb_top t1 FROM tb_top_merge t2 WHERE t1.id = t2.id AND t2.tid > 1;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_from_clause1() {
        String sql = "DELETE from SYSDBA.tb_top t1 FROM tb_top_merge t2 WHERE t1.id = t2.id AND t2.tid > 1;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_from_clause_92() {
        String sql = "DELETE from tb_top t1 from tb_top_merge t2 , tb_top1 t3 WHERE t1.id = t2.id and t3.id = t2.id AND t2.tid > 1;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_from_clause_99() {
        String sql = "DELETE from tb_top t1 from tb_top_merge t2 join tb_top1 t3 ON t2.id = t3.id WHERE t1.id = t2.id AND t2.tid > 1;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void test_from_clause_select() {
        String sql = "DELETE from tb_top t1 from tb_top_merge t2 join tb_top1 t3 ON t2.id = t3.id WHERE t1.id = t2.id AND t3.id IN (SELECT 3 FROM dual ) AND t2.tid > 1 ;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
        System.out.println("-------------------------------");
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
    }

    public void test_cursor() {
        String sql = "DECLARE\n" +
                "\tCURSOR cur IS SELECT * FROM tb_top;\n" +
                "BEGIN\n" +
                "\tOPEN cur;\n" +
                "\tWHILE cur%FOUND LOOP\n" +
                "\tDELETE tb_top WHERE CURRENT OF cur;\n" +
                "    END LOOP;\n" +
                "\tCLOSE cur;\n" +
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
                "\tDELETE tb_top WHERE id = 2\n" +
                "    RETURNING *  BULK COLLECT INTO var_c3;\n" +
                "  \tFOR i IN 1..var_c3.COUNT() LOOP\n" +
                "    \tSEND_MSG('id:' || var_c3(i).id|| ',name:' || var_c3(i).name);\n" +
                "  \tEND LOOP;\n" +
                "END;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }
}
