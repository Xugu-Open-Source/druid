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

public class XuGuWallSchemaTest extends TestCase {

    public void testCreateSchema() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("CREATE SCHEMA SCH_TEST;"));
    }

    public void testCreateSchema2() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("CREATE SCHEMA SCH_TEST AUTHORIZATION SYSDBA;"));
    }

    public void testDropSchema() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("DROP SCHEMA SCH_TEST;"));
    }

    public void testDropSchema2() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("DROP SCHEMA SCH_TEST CASCADE;"));
    }

    public void testDropSchema3() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("DROP SCHEMA SCH_TEST RESTRICT;"));
    }

    public void testAlterSchema() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("ALTER SCHEMA sch_test RENAME TO new_sch_test;"));
    }

    public void testAlterSchema2() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("ALTER SCHEMA sch_test OWNER TO user_test;"));
    }

}
