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

public class XuGuWallSelectQ_EscapeTest extends TestCase {

    public void testQ_EscapeSquareBrackets() {
        Assert.assertTrue(WallUtils.isValidateXuGu(" SELECT id, name, amount, region, PARENT_ID parentId FROM tb_top WHERE name = q'[''four'']';"));
    }

    public void testQ_EscapeSquareBrackets1() {
        Assert.assertTrue(WallUtils.isValidateXuGu(" SELECT q'[it's an example1]' FROM dual;"));
    }

}
