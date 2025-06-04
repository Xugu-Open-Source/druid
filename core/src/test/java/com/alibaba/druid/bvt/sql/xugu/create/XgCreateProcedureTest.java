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
package com.alibaba.druid.bvt.sql.xugu.create;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class XgCreateProcedureTest extends TestCase {

	public void testCreateParameterlessProcedure() {
		String sql = "CREATE PROCEDURE proc_test_parameterless () COMMENT '无参数存储过程'\n" +
				"AS\n" +
				"\tloop_num INT;\n" +
				"BEGIN\n" +
				"\tloop_num := 0;\n" +
				"\tFOR i IN 1..10\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || i\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tloop_num := loop_num + 1;\n" +
				"\tEND LOOP;\n" +
				"\tSEND_MSG('过程执行完成' || loop_num || '次');\n" +
				"\tCOMMIT;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testReplace() {
		String sql = "CREATE OR REPLACE PROCEDURE proc_test_parameterless COMMENT '无参数存储过程1' IS\n" +
				"  loop_num INT;\n" +
				"BEGIN\n" +
				"  loop_num := 0;\n" +
				"  FOR i IN 1 .. 10 LOOP\n" +
				"\tUPDATE tb_top SET NAME = NAME || i WHERE id = i;\n" +
				"    loop_num := loop_num + 1;\n" +
				"  END LOOP;\n" +
				"  SEND_MSG('过程执行完成' || loop_num || '次');\n" +
				"  COMMIT;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testNotExists() {
		String sql = "CREATE OR REPLACE PROCEDURE IF NOT EXISTS proc_test_parameterless COMMENT '无参数存储过程1' IS\n" +
				"  loop_num INT;\n" +
				"BEGIN\n" +
				"  loop_num := 0;\n" +
				"  FOR i IN 1 .. 10 LOOP\n" +
				"\tUPDATE tb_top SET NAME = NAME || i WHERE id = i;\n" +
				"    loop_num := loop_num + 1;\n" +
				"  END LOOP;\n" +
				"  SEND_MSG('过程执行完成' || loop_num || '次');\n" +
				"  COMMIT;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testEndNameSpace() {
		String sql = "CREATE OR REPLACE PROCEDURE IF NOT EXISTS proc_test_parameterless () COMMENT '无参数存储过程1'\n" +
				"AS\n" +
				"\tloop_num INT;\n" +
				"BEGIN\n" +
				"\tloop_num := 0;\n" +
				"\tFOR i IN 1..10\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || i\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tloop_num := loop_num + 1;\n" +
				"\tEND LOOP;\n" +
				"\tSEND_MSG('过程执行完成' || loop_num || '次');\n" +
				"\tCOMMIT;\n" +
				"END proc_test_parameterless;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testDeclare() {
		String sql = "CREATE OR REPLACE PROCEDURE IF NOT EXISTS proc_test_parameterless () COMMENT '无参数存储过''程1'\n" +
				"AS DECLARE\n" +
				"\tloop_num INT;\n" +
				"BEGIN\n" +
				"\tloop_num := 0;\n" +
				"\tFOR i IN 1..10\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || i\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tloop_num := loop_num + 1;\n" +
				"\tEND LOOP;\n" +
				"\tSEND_MSG('过程执行完成' || loop_num || '次');\n" +
				"\tCOMMIT;\n" +
				"END proc_test_parameterless;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testInputTypeParameters() {
		String sql = "CREATE OR REPLACE FORCE PROCEDURE proc_test_Input (parameter INTEGER)\n" +
				"AS\n" +
				"\tx int;\n" +
				"BEGIN\n" +
				"\tx := 0;\n" +
				"\tFOR i IN 1..parameter\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || i\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tx := x + SQL%ROWCOUNT;\n" +
				"\tEND LOOP;\n" +
				"\tSEND_MSG('过程执行完成' || x || '次');\n" +
				"\tCOMMIT;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testInputTypeParameters_1() {
		String sql = "CREATE OR REPLACE FORCE PROCEDURE proc_test_Input (parameter IN INTEGER)\n" +
				"AS\n" +
				"\tx int;\n" +
				"BEGIN\n" +
				"\tx := 0;\n" +
				"\tFOR i IN 1..parameter\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || i\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tx := x + SQL%ROWCOUNT;\n" +
				"\tEND LOOP;\n" +
				"\tSEND_MSG('过程执行完成' || x || '次');\n" +
				"\tCOMMIT;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

/*	DECLARE
	OUTRET INT;
	BEGIN
	EXEC proc_test_out(OUTRET);
	SEND_MSG('共执行：' || OUTRET || '次');
	END;*/
	public void testOutputTypeParameters() {
		String sql = "CREATE OR REPLACE PROCEDURE proc_test_out(parameter OUT INTEGER) AS\n" +
				"  x int;\n" +
				"BEGIN\n" +
				"  x := 0;\n" +
				"  FOR i IN 1 .. 10 LOOP\n" +
				"    update tb_top SET NAME = i || NAME WHERE id = i;\n" +
				"    x := x + SQL%ROWCOUNT;\n" +
				"  END LOOP;\n" +
				"  parameter := x;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testInOutputTypeParameters() {
		String sql = "CREATE OR REPLACE PROCEDURE proc_test_out (\n" +
				"\tPARAMETER IN OUT INTEGER\n" +
				")\n" +
				"AS\n" +
				"\tx int;\n" +
				"BEGIN\n" +
				"\tx := 0;\n" +
				"\tFOR i IN 1..10\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = i || NAME || parameter\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tx := x + SQL % ROWCOUNT;\n" +
				"\tEND LOOP;\n" +
				"\tparameter := x;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

/*	DECLARE
	out1 INT;
	BEGIN
	EXEC proc_test_default(out1);
	SEND_MSG('共执行：' || out1 || '次');
	END;*/
	public void testDefaultParameters() {
		String sql = "CREATE OR REPLACE PROCEDURE proc_test_default (\n" +
				"\tout1 OUT INTEGER,\n" +
				"\tin1 in varchar(20) DEFAULT '-OK-'\n" +
				")\n" +
				"AS\n" +
				"\tx int;\n" +
				"BEGIN\n" +
				"\tx := 0;\n" +
				"\tFOR i IN 1..10\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || in1\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tx := x + SQL % ROWCOUNT;\n" +
				"\tEND LOOP;\n" +
				"\tout1 := x;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testLanguage() {
		String sql = "CREATE OR REPLACE PROCEDURE my_plsql_procedure () AS\n" +
				"LANGUAGE PLSQL\n" +
				"NAME abc;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testLanguage_1() {
		String sql = "CREATE OR REPLACE PROCEDURE my_plsql_procedure ()\n" +
				"AS LANGUAGE C NAME ProC;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testAuthidDefault() {
		String sql = "CREATE OR REPLACE PROCEDURE IF NOT EXISTS proc_test_parameterless () AUTHID DEFAULT COMMENT '无参数存储过程1'\n" +
				"AS\n" +
				"\tloop_num INT;\n" +
				"BEGIN\n" +
				"\tloop_num := 0;\n" +
				"\tFOR i IN 1..10\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || i\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tloop_num := loop_num + 1;\n" +
				"\tEND LOOP;\n" +
				"\tSEND_MSG('过程执行完成' || loop_num || '次');\n" +
				"\tCOMMIT;\n" +
				"END proc_test_parameterless;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testAuthidDefiner() {
		String sql = "CREATE OR REPLACE PROCEDURE IF NOT EXISTS proc_test_parameterless () AUTHID DEFINER COMMENT '无参数存储过程1'\n" +
				"AS\n" +
				"\tloop_num INT;\n" +
				"BEGIN\n" +
				"\tloop_num := 0;\n" +
				"\tFOR i IN 1..10\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || i\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tloop_num := loop_num + 1;\n" +
				"\tEND LOOP;\n" +
				"\tSEND_MSG('过程执行完成' || loop_num || '次');\n" +
				"\tCOMMIT;\n" +
				"END proc_test_parameterless;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testAuthidCurrentUser() {
		String sql = "CREATE OR REPLACE PROCEDURE IF NOT EXISTS proc_test_parameterless () AUTHID CURRENT_USER COMMENT '无参数存储过程1'\n" +
				"AS\n" +
				"\tloop_num INT;\n" +
				"BEGIN\n" +
				"\tloop_num := 0;\n" +
				"\tFOR i IN 1..10\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || i\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tloop_num := loop_num + 1;\n" +
				"\tEND LOOP;\n" +
				"\tSEND_MSG('过程执行完成' || loop_num || '次');\n" +
				"\tCOMMIT;\n" +
				"END proc_test_parameterless;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testAuthidUser() {
		String sql = "CREATE OR REPLACE PROCEDURE IF NOT EXISTS proc_test_parameterless () AUTHID USER COMMENT '无参数存储过程1'\n" +
				"AS\n" +
				"\tloop_num INT;\n" +
				"BEGIN\n" +
				"\tloop_num := 0;\n" +
				"\tFOR i IN 1..10\n" +
				"\tLOOP\n" +
				"\t\tUPDATE tb_top\n" +
				"\t\tSET NAME = NAME || i\n" +
				"\t\tWHERE id = i;\n" +
				"\t\tloop_num := loop_num + 1;\n" +
				"\tEND LOOP;\n" +
				"\tSEND_MSG('过程执行完成' || loop_num || '次');\n" +
				"\tCOMMIT;\n" +
				"END proc_test_parameterless;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}
}
