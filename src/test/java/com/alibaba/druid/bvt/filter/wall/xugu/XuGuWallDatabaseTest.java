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

public class XuGuWallDatabaseTest extends TestCase {

    public void testCreateDatabase() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("CREATE DATABASE IF NOT EXISTS `druid` CHARACTER SET 'utf8_bin' TIME ZONE 'GMT+08:00';"));
    }

    public void testCreateDatabase2() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("CREATE DATABASE druid CHAR SET GBK TIME ZONE 'GMT+08:00' ENABLE ENCRYPT ENCRYPT BY 'ENCRYPTOR1';"));
    }

    public void testCreateDatabase3() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("CREATE DATABASE `druid` CHAR SET 'GBK' TIME ZONE 'GMT+08:00' DISABLE ENCRYPT ENCRYPT BY 'ENCRYPTOR1';"));
    }

    public void testCreateDatabase4() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("CREATE DATABASE druid CHAR SET GB18030 TIME ZONE 'GMT+08:00' ENCRYPT BY 'ENCRYPTOR1';"));
    }

    public void testDropDatabase() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("DROP DATABASE IF EXISTS druid;"));
    }

    public void testAlterDatabase() throws Exception {
        Assert.assertTrue(WallUtils.isValidateXuGu("ALTER DATABASE druid RENAME TO druid2;"));
    }

}
