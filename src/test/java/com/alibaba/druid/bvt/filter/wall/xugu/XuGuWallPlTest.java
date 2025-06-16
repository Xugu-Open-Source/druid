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

package com.alibaba.druid.bvt.filter.wall.xugu;

import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class XuGuWallPlTest extends TestCase {

    /**
     * 测试for语句
     */
    public void test_pl_for() {
        String sql = "DECLARE\n" +
                "\tTYPE v_table IS VARRAY(10) OF VARCHAR;\n" +
                "\n" +
                "\tst v_table;\n" +
                "BEGIN\n" +
                "\tSELECT NAME BULK COLLECT INTO st FROM tb_top ;\n" +
                "\tFOR i IN st.FIRST..st.LAST\n" +
                "\t      LOOP\n" +
                "\t        SEND_MSG(st(i));\n" +
                "\tENDFOR;\n" +
                "END;";
        Assert.assertFalse(WallUtils.isValidateXuGu(sql));
    }


    /**
     * 测试
     * DECLARE 类型定义与标识符
     * insert 使用VALUES ident、 RETURNING target_list
     */
    public void test_pl_declare() {
        String sql = "DECLARE\n" +
                "    TYPE num_array IS TABLE OF NUMBER;\n" +
                "\tCURSOR emp_cursor(t_id NUMBER) as\n" +
                "    \tSELECT id, name FROM tb_top WHERE id = t_id ORDER BY id DESC;\n" +
                "\tSUBTYPE type_rowtype0 IS tb_top%ROWTYPE;\n" +
                "\tSUBTYPE type_rowtype1 IS tb_top%ROW TYPE;\n" +
                "\tSUBTYPE type_rowtype2 IS ROWTYPE OF tb_top;\n" +
                "\tSUBTYPE type_rowtype3 IS ROW TYPE OF tb_top;\n" +
                "\tSUBTYPE type_type0 IS tb_top.id%TYPE;\n" +
                "\tSUBTYPE type_type1 IS TYPE OF tb_top.NAME;\n" +
                "\tSUBTYPE TYPE_CURSOR IS REF CURSOR;\n" +
                "\tSUBTYPE TYPE_RECORD IS RECORD\n" +
                "\t(\n" +
                "\t\tid int,\n" +
                "\t\tNAME varchar2(255)\n" +
                "\t);\n" +
                "\t\n" +
                "\tTYPE type_var0 IS VARCHAR;\n" +
                "\tTYPE type_table_var0 IS TABLE OF NVARCHAR2(10);\n" +
                "\tTYPE type_table_var1 IS TABLE OF TYPE_RECORD INDEX BY int;\n" +
                "\tTYPE type_table_var2 IS TABLE OF tb_top%ROWTYPE;\n" +
                "\tTYPE type_table_var3 IS TABLE OF interval day to second(3);\n" +
                "\tTYPE type_var_arr0 IS VARRAY (10) OF VARCHAR;\n" +
                "\tTYPE type_var_arr1 IS VARRAY(10) OF NUMBER(10);\n" +
                "\tTYPE type_var_arr2 IS VARYING ARRAY (10) OF VARCHAR(12);\n" +
                "\tTYPE type_var_arr3 IS VARYING ARRAY(10) OF TIMESTAMP;\n" +
                "\tTYPE type_var_arr4 IS VARYING ARRAY(10) OF interval hour to second(6);\n" +
                "\t\n" +
                "\tnums num_array := num_array(1, 2, 3);\n" +
                "\tdefalut_var VARCHAR default 'Hello,World!';\n" +
                "\tdate1 DATETIME := sysdate;\n" +
                "\tv_isTrue  boolean := true;\n" +
                "\tv_table_var type_table_var1;\n" +
                "\tvar_arr0 type_var_arr0;\n" +
                "\tv_testRow type_rowtype0;\n" +
                "\tt_id type_type0;\n" +
                "\tt_name type_type1;\n" +
                "\tt_record TYPE_RECORD;\n" +
                "\tT_CURSOR TYPE_CURSOR ;\n" +
                "\thour_minute interval hour to MINUTE := '12:10';\n" +
                "\ttable_var0 type_table_var0;\n" +
                "BEGIN\n" +
                "\tSELECT NAME BULK COLLECT INTO var_arr0 FROM tb_top LIMIT 10;\n" +
                "\tFOR i IN var_arr0.FIRST..var_arr0.LAST\n" +
                "\t      LOOP\n" +
                "\t        SEND_MSG(var_arr0(i));\n" +
                "\tEND LOOP;\n" +
                "\t\n" +
                "\tSEND_MSG('defalut_var default:'||defalut_var);\n" +
                "\t\n" +
                "\tSEND_MSG('date1 default:'||date1);\n" +
                "\t\n" +
                "\tdbms_output.put_line('v_isTrue='||(case v_isTrue when true then 'true' when false then 'false' else null end));\n" +
                "\t\n" +
                "\tSELECT id INTO v_testRow.id FROM tb_top WHERE id IS NOT NULL ORDER BY id DESC LIMIT 1;\n" +
                "\tv_testRow.id := v_testRow.id + 1;\n" +
                "\tv_testRow.NAME := '' || date1;\n" +
                "\tinsert INTO tb_top values v_testRow;\n" +
                "\t\n" +
                "\tv_testRow.id := v_testRow.id + 1;\n" +
                "\tv_table_var(1).id := v_testRow.id;\n" +
                "\tv_table_var(1).NAME := v_testRow.NAME;\n" +
                "\tFOR i IN 1..v_table_var.count\n" +
                "\tLOOP\n" +
                "\t\tinsert INTO tb_top values (v_table_var(i).id,v_table_var(i).NAME)\n" +
                "\t\t\tRETURNING '('||v_table_var(i).id||','||v_table_var(i).NAME||')' BULK COLLECT INTO table_var0;\n" +
                "\t\tFOR j IN 1..table_var0.COUNT() LOOP\n" +
                "      \t\tSEND_MSG('test RETURNING---->'||table_var0(j));\n" +
                "    \tEND LOOP;\n" +
                "\tEND LOOP;\n" +
                "\t\n" +
                "\t\n" +
                "\tOPEN emp_cursor(2);\n" +
                "\tLOOP\n" +
                "\t\tFETCH emp_cursor INTO t_id, t_name;\n" +
                "\t\tEXIT WHEN emp_cursor%NOTFOUND;\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('emp_cursor:'||'ID: ' || t_id ||', Name: ' || t_name);\n" +
                "\tEND LOOP;\n" +
                "\tCLOSE emp_cursor;\n" +
                "\t\n" +
                "\tOPEN T_CURSOR FOR SELECT id, name FROM tb_top ORDER BY id DESC LIMIT 2;\n" +
                "\tLOOP\n" +
                "\t\tFETCH T_CURSOR INTO t_id, t_name;\n" +
                "\t\tEXIT WHEN T_CURSOR%NOTFOUND;\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('T_CURSOR:'||'ID: ' || t_id ||', Name: ' || t_name);\n" +
                "\tEND LOOP;\n" +
                "\tCLOSE T_CURSOR;\n" +
                "\t\n" +
                "\tFORALL i IN 1..nums.COUNT\n" +
                "\t\tDBMS_OUTPUT.PUT_LINE('num' || i);\n" +
                "\t\n" +
                "\tSELECT id, NAME INTO t_record FROM tb_top WHERE name = v_testRow.NAME;\n" +
                "\tDBMS_OUTPUT.PUT_LINE('t_record.id='||t_record.id);\n" +
                "\tDBMS_OUTPUT.PUT_LINE('t_record.name='||t_record.name);\n" +
                "\tDBMS_OUTPUT.PUT_LINE('now_hour_minute='|| (NOW() + hour_minute));\n" +
                "END;";
        Assert.assertFalse(WallUtils.isValidateXuGu(sql));
    }

    public void test_Pl_open1(){
        String sql = "DECLARE\n" +
                "\tTYPE v_table IS VARRAY(10) OF VARCHAR;\n" +
                "\n" +
                "\tst v_table;\n" +
                "BEGIN\n" +
                "\tPROCEDURE aaa() IS\n" +
                "\t\tloop_num INT;\n" +
                "\t\tdrop_sql VARCHAR;\n" +
                "\tBEGIN\n" +
                "\t\tloop_num := 0;\n" +
                "\t\tFOR t IN (SELECT TABLE_NAME FROM  USER_tables) LOOP\n" +
                "\t\t\tdrop_sql := 'DROP TABLE \"' || t.table_name || '\"';\n" +
                "\t\t\tloop_num := loop_num + 1;\n" +
                "\t\t\tEXECUTE IMMEDIATE drop_sql;\n" +
                "\t\n" +
                "\t\tEND LOOP;\n" +
                "\t\tSEND_MSG('过程执行完成' || loop_num || '次');\n" +
                "\tEND;\n" +
                "\tSELECT NAME BULK COLLECT INTO st FROM tb_top ;\n" +
                "\tFOR i IN st.FIRST..st.LAST\n" +
                "\t      LOOP\n" +
                "\t        SEND_MSG(st(i));\n" +
                "\tEND FOR;\n" +
                "\taaa;\n" +
                "END;";
        Assert.assertFalse(WallUtils.isValidateXuGu(sql));
    }
}
