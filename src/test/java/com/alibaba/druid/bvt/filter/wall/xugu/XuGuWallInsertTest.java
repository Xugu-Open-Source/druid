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

public class XuGuWallInsertTest extends TestCase {

    public void test_0() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("INSERT INTO test_tab_1 VALUES(DEFAULT, NULL, '2000-01-01', 2.459E+23);"));
    }

    public void test_1() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("INSERT INTO test_tab_2(c3, c4) VALUES(2+3, SUBSTR(c3,LEN(c3)-3,1));"));
    }

    public void test_2() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("INSERT INTO sysdba.tb_top(c3, c4)SELECT salary,addr FROM t2 WHERE t2.id > 10;"));
    }

    public void test_3() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("insert INTO tb_top values (17,'aaabbb') (17,'aaabbb'),(17,'aaabbb');"));
    }

    public void test_4() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("insert INTO tb_top (SELECT 18,'aaabbb' FROM dual);"));
    }
}
