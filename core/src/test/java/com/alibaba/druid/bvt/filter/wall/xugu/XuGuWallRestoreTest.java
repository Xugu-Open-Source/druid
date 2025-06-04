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

public class XuGuWallRestoreTest extends TestCase {

    public void testRestoreSystem() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("RESTORE SYSTEM FROM '/BACKUP/SYS.DMP' ENCRYPTOR IS 'ENCRYPTOR1';"));
    }

    public void testRestoreDatabase() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("RESTORE DATABASE druid FROM '/BACKUP/DB_BAK.EXP' ENCRYPTOR IS 'ENCRYPTOR1' WITH REINDEX;"));
    }

    public void testRestoreUser() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("RESTORE USER u_bak FROM '/BACKUP/U_BAK.EXP';"));
    }

    public void testRestoreSchema() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("RESTORE SCHEMA sysdba RENAME TO aa FROM '/BACKUP/S_BAK.EXP' ENCRYPTOR IS 'ENCRYPTOR1';"));
    }

    public void testRestoreTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateXuGu("RESTORE TABLE usert RENAME TO guest.aa FROM '/BACKUP/usert_BACKUP.EXP' ENCRYPTOR IS 'ENCRYPTOR1';"));
    }

}
