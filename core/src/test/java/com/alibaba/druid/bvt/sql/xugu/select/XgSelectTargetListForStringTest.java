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

public class XgSelectTargetListForStringTest extends TestCase {

    /**
     * mysql兼容模式:SET compatible_mode TO mysql;
     * 兼容模式下，英文双引号标识为字符串，而不作为标识数据库对象。
     */
    public void test_string_quotation_mark() {
        String sql = "SELECT `hello`, \"'hello'\", \"''hello''\",\"hel\"\"lo\", 'hel''lo' ,'hel\"\"lo'FROM DUAL;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        // hello	'hello'	''hello''	hel"lo	hel'lo	hel""lo
        System.out.println(SQLUtils.toXuGuString(stmt));

    }

    /**
     * todo：xugu 非兼容模式下，id为tb_top中列名
     */
    public void test_string_quotation_mark1() {
        String sql = "select \"id\" from tb_top;";
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

    /**
     * 支持字符串作为别名，切换MySQL兼容模式则为连续2个字符串拼接
     */
    public void test_string_alias() {
        String sql = "SELECT 'a' \"b\" FROM DUAL;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));

    }

    /**
     * mysql兼容模式:SET compatible_mode TO mysql;
     * 支持字符串作为别名（如为两个连续字符串，则进行字符拼接，不作为别名）。
     * 兼容模式下，双引号"a"为字符串
     */
    public void test_string_alias1() {
        String sql = "SELECT \"a\" \"b\" FROM DUAL;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }

    /**
     * 字符串拼接
     */
    public void test_string_alias3() {
        String sql = "SELECT 'a' 'b' FROM DUAL;";
        XuGuStatementParser parser = new XuGuStatementParser(sql);
        List<SQLStatement> sqlStatements = parser.parseStatementList();
        SQLStatement stmt = sqlStatements.get(0);
        XuGuSchemaStatVisitor visitor = new XuGuSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(SQLUtils.toXuGuString(stmt));
    }
}
