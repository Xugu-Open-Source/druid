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

public class XgSelectConnectTest extends TestCase {

    public void testConnectLevel() {
        String sql = "SELECT level, * FROM TB_TOP tt CONNECT BY level < 3;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testConnectLevel_1() {
        String sql = "SELECT lpad(' ', level * 2, ' ') || name , id, parent_id, level\n" +
                "       FROM tb_top\n" +
                "       START WITH id = 1\n" +
                "       CONNECT BY PRIOR id = parent_id;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testNocycle() {
        String sql = "SELECT lpad(' ', level * 2, ' ') || name , id, parent_id, level\n" +
                "       FROM tb_top\n" +
                "       START WITH id = 1\n" +
                "       CONNECT BY NOCYCLE PRIOR id = parent_id;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testLeaf() {
        String sql = "SELECT id, parent_id, LEVEL, connect_by_isleaf\n" +
                "       FROM tb_top\n" +
                "       START WITH id = 1\n" +
                "       CONNECT BY PRIOR id = parent_id;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toXuGuString(stmt);
        System.out.println(xuGuString);
        assertEquals(sql.replaceAll("\\s+", " ").trim(), xuGuString.replaceAll("\\s+", " ").trim());
    }

    public void testPriorParentNode() {
        String sql = "SELECT level,* FROM TB_TOP tt CONNECT BY PRIOR parent_id = id START WITH id = 8;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void testPriorChildNode() {
        String sql = "SELECT level,* FROM TB_TOP tt START WITH id = 1 CONNECT BY PRIOR id = parent_id;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void testUncleNode() {
        String sql = "with t as (\n" +
                "select TB_TOP.*,prior name,level le\n" +
                "from TB_TOP\n" +
                "start with  id=1\n" +
                "connect by prior id = parent_id\n" +
                ")\n" +
                "select *\n" +
                "from t t1\n" +
                "left join t t2 on t2.id=6\n" +
                "where t1.le = (t2.le-1)\n" +
                "and t1.id not in (t2.parent_id);";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    public void testCousinBrotherNode() {
        String sql = "with t as (\n" +
                "select TB_TOP.*,prior name,level le\n" +
                "from TB_TOP\n" +
                "start with id = 1\n" +
                "connect by prior id = parent_id\n" +
                ")\n" +
                "select t.*\n" +
                "from t t\n" +
                "left join t tt on tt.id=6\n" +
                "where t.le=tt.le and t.id<>6;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }
}
