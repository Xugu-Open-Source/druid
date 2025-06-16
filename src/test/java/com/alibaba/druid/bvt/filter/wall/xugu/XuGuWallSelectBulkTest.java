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

public class XuGuWallSelectBulkTest extends TestCase {

    public void testBulk() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("SELECT * BULK COLLECT FROM tb_top ORDER BY id DESC;"));
    }

    public void testBulk2() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("SELECT * BULK FROM tb_top ORDER BY id DESC;"));
    }

    public void testBulk3() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("SELECT id BULK COLLECT FROM tb_top ORDER BY id DESC;"));
    }

    public void testBulk4() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("SELECT id BULK FROM tb_top ORDER BY id DESC;"));
    }

    public void testBulk5() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("SELECT id COLLECT FROM tb_top ORDER BY id DESC;"));
    }

}
