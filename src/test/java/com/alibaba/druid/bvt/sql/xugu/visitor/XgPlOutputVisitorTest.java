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
package com.alibaba.druid.bvt.sql.xugu.visitor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class XgPlOutputVisitorTest extends TestCase {

    public void test_loop() throws Exception {
        String sql ="DECLARE\n" +
                "  x INTEGER;\n" +
                "BEGIN\n" +
                "  x := 100;\n" +
                "  LOOP\n" +
                "    x := x + 100;\n" +
                "    IF x > 1000 THEN\n" +
                "      EXIT;\n" +
                "    END IF;\n" +
                "  END LOOP;\n" +
                "  SEND_MSG(x);\n" +
                "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        System.out.println("-----------------------");
    }

    public void test_loop_1() throws Exception {
        String sql ="DECLARE\n" +
                "  x INTEGER;\n" +
                "BEGIN\n" +
                "  x := 100;\n" +
                "  LOOP\n" +
                "    x := x + 100;\n" +
                "    IF x > 1000 THEN\n" +
                "      EXIT;\n" +
                "    END IF;\n" +
                "  ENDLOOP;\n" +
                "  SEND_MSG(x);\n" +
                "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        System.out.println("-----------------------");
    }

    public void test_cursor_param() throws Exception {
        String sql ="DECLARE\n" +
                "\tCURSOR emp_cursor(t_id NUMBER, aa varchar) IS\n" +
                "\t\tSELECT id, name\n" +
                "\t\tFROM tb_top\n" +
                "\t\tWHERE id = t_id;\n" +
                "BEGIN\n" +
                "\tSELECT *\n" +
                "\tFROM tb_top;\n" +
                "END";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        assertEquals(sql,tempResult);
        System.out.println("-----------------------");
    }

    public void test_forall() throws Exception {
        String sql ="BEGIN\n" +
                "\tFORALL i IN 1..5\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('num' || i);\n" +
                "END";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        assertEquals(sql,tempResult);
        System.out.println("-----------------------");
    }

    public void test_table_index() throws Exception {
        String sql ="DECLARE\n" +
                "\tSUBTYPE TYPE_RECORD IS RECORD\n" +
                "\t\t(\n" +
                "\t\tid int,\n" +
                "\t\tNAME varchar2(255)\n" +
                "\t\t);\n" +
                "\tTYPE type_table_var1 IS TABLE OF TYPE_RECORD INDEX BY int;\n" +
                "BEGIN\n" +
                "\tSELECT * FROM tb_top;\n" +
                "END";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        System.out.println("-----------------------");
    }

    public void test_dbms_output() throws Exception {
        String sql ="DECLARE\n" +
                "hour_minute interval hour to MINUTE := '12:10';\n" +
                "BEGIN\n" +
                "\tDBMS_OUTPUT.PUT_LINE('now_hour_minute=' || (NOW() + hour_minute + hour_minute));\n" +
                "END";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        System.out.println("-----------------------");
    }

    public void test_assignment() throws Exception {
        String sql ="DECLARE\n" +
                "num NUMBER := 1;\n" +
                "BEGIN\n" +
                "\tnum := num + 1;\n" +
                "\tDBMS_OUTPUT.PUT_LINE( '' || num);\n" +
                "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        System.out.println("-----------------------");
    }

    public void test_send_msg() throws Exception {
        String sql ="DECLARE\n" +
                "hour_minute interval hour to MINUTE := '12:10';\n" +
                "BEGIN\n" +
                "\tSEND_MSG('now_hour_minute=' || (NOW() + hour_minute + hour_minute));\n" +
                "END";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        System.out.println("-----------------------");
    }

    public void test_declare() throws Exception {
        String sql = "DECLARE\n" +
                "\tTYPE num_array IS TABLE OF NUMBER;\n" +
                "\tCURSOR emp_cursor(t_id NUMBER) as\n" +
                "\t\tSELECT id, name\n" +
                "\t\tFROM tb_top\n" +
                "\t\tWHERE id = t_id;\n" +
                "\tSUBTYPE type_rowtype0 IS tb_top%ROWTYPE;\n" +
                "\tSUBTYPE type_rowtype1 IS tb_top%ROW TYPE;\n" +
                "\tSUBTYPE type_rowtype2 IS ROWTYPE OF tb_top;\n" +
                "\tSUBTYPE type_rowtype3 IS ROW TYPE OF tb_top;\n" +
                "\tSUBTYPE type_type0 IS tb_top.id%TYPE;\n" +
                "\tSUBTYPE type_type1 IS TYPE OF tb_top.NAME;\n" +
                "\tSUBTYPE TYPE_CURSOR IS REF CURSOR;\n" +
                "\tSUBTYPE TYPE_RECORD IS RECORD (\n" +
                "\t\tid int, \n" +
                "\t\tNAME varchar2(255)\n" +
                "\t);\n" +
                "\t\n" +
                "\tTYPE type_var0 IS VARCHAR;\n" +
                "\tTYPE type_table_var0 IS TABLE OF NVARCHAR2(10);\n" +
                "\tTYPE type_table_var1 IS TABLE OF TYPE_RECORD INDEX BY int;\n" +
                "\tTYPE type_table_var2 IS TABLE OF tb_top%ROWTYPE;\n" +
                "\tTYPE type_table_var3 IS TABLE OF interval day to second(3);\n" +
                "\tTYPE type_var_arr0 IS VARRAY(10) OF VARCHAR;\n" +
                "\tTYPE type_var_arr1 IS VARRAY(10) OF NUMBER(10);\n" +
                "\tTYPE type_var_arr2 IS VARYING ARRAY(10) OF VARCHAR(12);\n" +
                "\tTYPE type_var_arr3 IS VARYING ARRAY(10) OF TIMESTAMP;\n" +
                "\tTYPE type_var_arr4 IS VARYING ARRAY(10) OF interval hour to second(6);\n" +
                "\t\n" +
                "\tnums num_array := num_array(1, 2, 3);\n" +
                "\tdefalut_var VARCHAR default 'Hello,World!';\n" +
                "\tdate1 DATETIME := sysdate;\n" +
                "\tv_isTrue boolean := true;\n" +
                "\tv_table_var type_table_var1;\n" +
                "\tvar_arr0 type_var_arr0;\n" +
                "\tv_testRow type_rowtype0;\n" +
                "\tt_id type_type0;\n" +
                "\tt_name type_type1;\n" +
                "\tt_record TYPE_RECORD;\n" +
                "\tT_CURSOR TYPE_CURSOR;\n" +
                "\thour_minute interval hour to MINUTE := '12:10';\n" +
                "\ttable_var0 type_table_var0;\n" +
                "\ttable_var2 type_table_var2;\n" +
                "BEGIN\n" +
                "\tSELECT NAME\n" +
                "\tBULK COLLECT \n" +
                "\tINTO var_arr0\n" +
                "\tFROM tb_top\n" +
                "\tLIMIT 10;\n" +
                "\tFOR i IN var_arr0.FIRST..var_arr0.LAST\n" +
                "\tLOOP\n" +
                "\t\tSEND_MSG(var_arr0(i));\n" +
                "\tEND LOOP;\n" +
                "\tSEND_MSG('defalut_var default:' || defalut_var);\n" +
                "\tSEND_MSG('date1 default:' || date1);\n" +
                "\tdbms_output.put_line('v_isTrue=' || case v_isTrue\n" +
                "\t\twhen true then 'true' \n" +
                "\t\twhen false then 'false' \n" +
                "\t\telse null \n" +
                "\tend);\n" +
                "\t\n" +
                "\tSELECT id\n" +
                "\tINTO v_testRow.id\n" +
                "\tFROM tb_top\n" +
                "\tWHERE id IS NOT NULL\n" +
                "\tORDER BY id DESC\n" +
                "\tLIMIT 1;\n" +
                "\tv_testRow.id := v_testRow.id + 1;\n" +
                "\tv_testRow.NAME := '' || date1;\n" +
                "\tinsert INTO tb_top\n" +
                "\tvalues v_testRow;\n" +
                "\tv_testRow.id := v_testRow.id + 1;\n" +
                "\tv_table_var(1).id := v_testRow.id;\n" +
                "\tv_table_var(1).NAME := v_testRow.NAME;\n" +
                "\tFOR i IN 1..v_table_var.count\n" +
                "\tLOOP\n" +
                "\t\tinsert INTO tb_top\n" +
                "\t\tvalues (v_table_var(i).id,v_table_var(i).NAME)\n" +
                "\t\tRETURNING '(' || tb_top.id || ',' || v_table_var(i).NAME || ')' BULK COLLECT INTO table_var0;\n" +
                "\t\tFOR j IN 1..table_var0.COUNT()\n" +
                "\t\tLOOP\n" +
                "\t\t\tSEND_MSG('test RETURNING---->' || table_var0(j));\n" +
                "\t\tEND LOOP;\n" +
                "\tEND LOOP;\n" +
                "\tOPEN emp_cursor(2);\n" +
                "\tLOOP \n" +
                "\t\tFETCH emp_cursor INTO t_id, t_name;\n" +
                "\t\tEXIT WHEN emp_cursor % NOTFOUND;\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('emp_cursor:' || 'ID: ' || t_id || ', Name: ' || t_name);\n" +
                "\tEND LOOP;\n" +
                "\tCLOSE emp_cursor;\n" +
                "\tOPEN T_CURSOR FOR \n" +
                "\t\tSELECT id, name\n" +
                "\t\tFROM tb_top\n" +
                "\t\tORDER BY id DESC\n" +
                "\t\tLIMIT 2;\n" +
                "\tLOOP \n" +
                "\t\tFETCH T_CURSOR INTO t_id, t_name;\n" +
                "\t\tEXIT WHEN T_CURSOR % NOTFOUND;\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('T_CURSOR:' || 'ID: ' || t_id || ', Name: ' || t_name);\n" +
                "\tEND LOOP;\n" +
                "\tCLOSE T_CURSOR;\n" +
                "\tFORALL i IN 1..nums.COUNT\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('num' || i);\n" +
                "\tSELECT id, NAME\n" +
                "\tINTO t_record\n" +
                "\tFROM tb_top\n" +
                "\tWHERE name = v_testRow.NAME;\n" +
                "\tDBMS_OUTPUT.PUT_LINE('t_record.id=' || t_record.id);\n" +
                "\tDBMS_OUTPUT.PUT_LINE('t_record.name=' || t_record.name);\n" +
                "\tDBMS_OUTPUT.PUT_LINE('now_hour_minute=' || (NOW() + hour_minute));\n" +
                "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, com.alibaba.druid.util.JdbcConstants.XUGU);
        System.out.println(tempResult);
        System.out.println("-----------------------");
    }
}
