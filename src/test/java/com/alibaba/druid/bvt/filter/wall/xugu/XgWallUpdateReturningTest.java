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

public class XgWallUpdateReturningTest extends TestCase {

    public void test_update_returning_for() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("DECLARE\n" +
                "    TYPE type_c3 IS TABLE OF tb_top%ROWTYPE;\n" +
                "    var_c3 type_c3;\n" +
                "BEGIN\n" +
                "    UPDATE tb_top t1\n" +
                "    SET NAME = t2.name || CURTIME()\n" +
                "    FROM tb_top_merge t2\n" +
                "    WHERE t1.id = t2.tid AND t2.YEAR = 30\n" +
                "    RETURNING t1.*  BULK COLLECT INTO var_c3;\n" +
                "    FOR i IN 1..var_c3.COUNT() LOOP\n" +
                "    SEND_MSG('id:' || var_c3(i).id|| ',name:' || var_c3(i).name);\n" +
                "    END LOOP;\n" +
                "    COMMIT;\n" +
                "END;"));
    }

}
