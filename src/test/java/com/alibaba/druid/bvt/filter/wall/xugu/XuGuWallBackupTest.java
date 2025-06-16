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

public class XuGuWallBackupTest extends TestCase {

    public void testBackupSystem() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("BACKUP SYSTEM INCREMENT APPEND TO '/BACKUP/SYS.DMP' ONLINE  ENCRYPTOR IS  'ENCRYPTOR1' NOCOMPRESS;"));
        Assert.assertFalse(WallUtils.isValidateXuGu("BACKUP SYSTEM INCREMENT TO '/BACKUP/SYS1.DMP'"));
    }

    public void testBackupDatabase() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("BACKUP DATABASE ALL APPEND TO '/BACKUP/DB_BAK.EXP' ONLINE ENCRYPTOR IS 'ENCRYPTOR1' COMPRESS;"));
    }

    public void testBackupUser() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("BACKUP USER SYSDBA APPEND TO '/BACKUP/U_BAK.EXP' ENCRYPTOR IS 'ENCRYPTOR1' COMPRESS;"));
    }

    public void testBackupSchema() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("BACKUP SCHEMA SYSDBA APPEND TO '/BACKUP/S_BAK.EXP' ENCRYPTOR IS 'ENCRYPTOR1' COMPRESS;"));
    }

    public void testBackupTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("BACKUP TABLE SYSDBA.t APPEND TO '/BACKUP/T_BACKUP.EXP' ENCRYPTOR IS 'ENCRYPTOR1' NOCOMPRESS;"));
        Assert.assertFalse(WallUtils.isValidateXuGu("BACKUP TABLE t_backup TO '/BACKUP/T_BACKUP.EXP'"));
    }

}
