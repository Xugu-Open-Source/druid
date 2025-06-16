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

public class XuGuWallSelectTest_1 extends TestCase {

    public void testInto() {
        Assert.assertTrue(WallUtils.isValidateXuGu("SELECT id  INTO abc FROM tb_top WHERE id =1;"));
    }

    public void test_for_parameterize(){
        String sql = "/*\n" +
                "The name + type results of these queries will be used by the Code Assistant\n" +
                "  if the \"Describe Context\" option is enabled. After typing 3 or more characters\n" +
                "  the Code Assistant will show a list of matching names.\n" +
                "  Separate multiple queries with semi-colons and use the :schema bind variable\n" +
                "  to restrict names to the currently connected user.\n" +
                "  In case of an error the query results will be omitted. No error message will\n" +
                "  be displayed.\n" +
                "  Place this file in the PL/SQL Developer installation directory for all users,\n" +
                "  or in the \"%APPDATA%\\PLSQL Developer\" directory for a specific user.\n" +
                " */\n" +
                "SELECT id, NAME BULK COLLECT FROM tb_top WHERE id =1;";
        Assert.assertTrue(WallUtils.isValidateXuGu(sql));

    }

}
