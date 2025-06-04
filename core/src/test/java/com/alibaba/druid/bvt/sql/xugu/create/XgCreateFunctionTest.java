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

public class XgCreateFunctionTest extends TestCase {

	public void testCreateParameterlessFunction() {
		String sql = "CREATE FUNCTION func_parameterless\n" +
				"  RETURN INT\n" +
				"AS\n" +
				"  ret INT;\n" +
				"BEGIN\n" +
				"  SELECT MAX(id) INTO ret FROM tb_top;\n" +
				"  RETURN ret;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

	public void testReplace() {
		String sql = "CREATE OR REPLACE FUNCTION func_parameterless\n" +
				"  RETURN INT\n" +
				"AS\n" +
				"  ret INT;\n" +
				"BEGIN\n" +
				"  SELECT MAX(id) INTO ret FROM tb_top;\n" +
				"  RETURN ret;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

	public void testReplaceForce() {
		String sql = "CREATE OR REPLACE FORCE FUNCTION func_parameterless\n" +
				"  RETURN INT\n" +
				"AS\n" +
				"  ret INT;\n" +
				"BEGIN\n" +
				"  SELECT MAX(id) INTO ret FROM tb_top;\n" +
				"  RETURN ret;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

	public void testNotExists() {
		String sql = "CREATE OR REPLACE FUNCTION IF NOT EXISTS func_parameterless()\n" +
				"  RETURN INT COMMENT '''无参数存储函数'''\n" +
				"AS\n" +
				"  ret INT;\n" +
				"BEGIN\n" +
				"  SELECT MAX(id) INTO ret FROM tb_top;\n" +
				"  RETURN ret;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

	public void testEndNameSpace() {
		String sql = "CREATE OR REPLACE FUNCTION IF NOT EXISTS func_parameterless()\n" +
				"  RETURN INT COMMENT '无参数存储函数'\n" +
				"AS\n" +
				"  ret INT;\n" +
				"BEGIN\n" +
				"  SELECT MAX(id) INTO ret FROM tb_top;\n" +
				"  RETURN ret;\n" +
				"END func_parameterless;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

	public void testDeclare() {
		String sql = "CREATE OR REPLACE FUNCTION IF NOT EXISTS func_parameterless()\n" +
				"  RETURN INT COMMENT '无参数存储函数'\n" +
				"AS DECLARE\n" +
				"  ret INT;\n" +
				"BEGIN\n" +
				"  SELECT MAX(id) INTO ret FROM tb_top;\n" +
				"  RETURN ret;\n" +
				"END func_parameterless;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

	/*SELECT * FROM TABLE(func_tab(3));*/
	public void testReturnSet() {
		String sql = "CREATE OR REPLACE FUNCTION func_tab(num INT) RETURN TABLE OF RECORD(id INT, name VARCHAR(10)) IS\n" +
				"DECLARE  subtype rec IS RECORD(id INT, name CHAR(10));\n" +
				"  tab TABLE OF rec;\n" +
				"BEGIN\n" +
				"  FOR i IN 1 .. num LOOP\n" +
				"    tab.EXTEND;\n" +
				"    tab(tab.last) := rec(i, 'name' || i);\n" +
				"  END FOR;\n" +
				"  RETURN tab;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

/*	CREATE OR REPLACE TYPE tab AS TABLE OF VARCHAR(100);*/
/*	SELECT * FROM TABLE(fun_pipe(1));*/
	public void testPipelined() {
		String sql = "CREATE OR REPLACE FUNCTION fun_pipe(a INT)\n" +
				"RETURN tab PIPELINED\n" +
				"IS\n" +
				"    j VARCHAR:='test';\n" +
				"BEGIN\n" +
				"    FOR i IN 1..10 LOOP\n" +
				"        PIPE ROW('abc'||j);\n" +
				"        j:= j||(a+i);\n" +
				"    END LOOP;\n" +
				"    PIPE ROW('pipe row end');\n" +
				"    RETURN;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

	public void testPipelinedNoReturn() {
		String sql = "CREATE OR REPLACE FUNCTION fun_pipe(a INT)\n" +
				"RETURN tab PIPELINED\n" +
				"IS\n" +
				"    j VARCHAR:='test';\n" +
				"BEGIN\n" +
				"    FOR i IN 1..10 LOOP\n" +
				"        PIPE ROW('abc'||j);\n" +
				"        j:= j||(a+i);\n" +
				"    END LOOP;\n" +
				"    PIPE ROW('pipe row end');\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

	public void testSelfAsResult() {
		String sql = "CREATE OR REPLACE FUNCTION fun_self (\n" +
				"\ta INT\n" +
				")\n" +
				"RETURN SELF AS RESULT IS\n" +
				"\ti int;\n" +
				"BEGIN\n" +
				"\tIF i IS null then\n" +
				"\t\ti := 0;\n" +
				"\tEND IF;\n" +
				"\ti := i + a;\n" +
				"\tSEND_MSG(i);\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		String xuGuString = SQLUtils.toSQLString(statementList, DbType.xugu);
		System.out.println(xuGuString);
	}

	public void testLanguage() {
		String sql = "CREATE OR REPLACE FUNCTION my_plsql_fun ()\n" +
				"RETURN int IS\n" +
				"LANGUAGE PLSQL NAME abc;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testLanguage_1() {
		String sql = "CREATE OR REPLACE FUNCTION my_plsql_fun ()\n" +
				"RETURN int IS\n" +
				"LANGUAGE C NAME ProC;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testDefaultParameters() {
		String sql = "CREATE OR REPLACE FUNCTION func_default (\n" +
				"\tin1 in varchar(20) DEFAULT '-OK-'\n" +
				") RETURN INT\n" +
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
				"\tRETURN x;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testAuthidDefault() {
		String sql = "CREATE OR REPLACE FUNCTION func_default (\n" +
				"\tin1 IN varchar(20) := '-OK-'\n" +
				")\n" +
				"RETURN INT AUTHID DEFAULT IS\n" +
				"DECLARE\n" +
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
				"\tRETURN x;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testAuthidDefiner() {
		String sql = "CREATE OR REPLACE FUNCTION func_default (\n" +
				"\tin1 IN varchar(20) := '-OK-'\n" +
				")\n" +
				"RETURN INT AUTHID DEFINER IS\n" +
				"DECLARE\n" +
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
				"\tRETURN x;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testAuthidCurrentUser() {
		String sql = "CREATE OR REPLACE FUNCTION func_default (\n" +
				"\tin1 IN varchar(20) := '-OK-'\n" +
				")\n" +
				"RETURN INT AUTHID CURRENT_USER IS\n" +
				"DECLARE\n" +
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
				"\tRETURN x;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}

	public void testAuthidUser() {
		String sql = "CREATE OR REPLACE FUNCTION func_default (\n" +
				"\tin1 IN varchar(20) := '-OK-'\n" +
				")\n" +
				"RETURN INT AUTHID USER IS\n" +
				"DECLARE\n" +
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
				"\tRETURN x;\n" +
				"END;";
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.xugu, true);
		SQLStatement stmt = statementList.get(0);
		SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.xugu);
		stmt.accept(visitor);
		String xuGuString = SQLUtils.toSQLString(stmt, DbType.xugu, null);
		System.out.println(xuGuString);
	}
}