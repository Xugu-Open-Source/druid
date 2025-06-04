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
package com.alibaba.druid.bvt.sql.xugu.alter;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class XgAlterFunctionTest extends TestCase {

/*  CREATE FUNCTION fun2 RETURN int IS
    BEGIN
    RETURN func_parameterless;
    END;*/
    public void testAlterFunction() {
        String sql = "ALTER FUNCTION fun2 RECOMPILE;";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
        SQLStatement stmt = statementList.get(0);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
        stmt.accept(visitor);
        String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
        System.out.println(xuGuString);
    }

}
