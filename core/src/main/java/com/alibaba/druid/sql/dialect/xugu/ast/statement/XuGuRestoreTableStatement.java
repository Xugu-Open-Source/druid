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
package com.alibaba.druid.sql.dialect.xugu.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;

public class XuGuRestoreTableStatement extends XuGuStatementImpl {

    private SQLName tableName;
    private boolean rename;
    private SQLName tableRename;
    private SQLExpr filePath;
    private boolean encryptor;
    private SQLExpr optEncryptor;

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.tableName = tableName;
    }

    public boolean isRename() {
        return rename;
    }

    public void setRename(boolean rename) {
        this.rename = rename;
    }

    public SQLName getTableRename() {
        return tableRename;
    }

    public void setTableRename(SQLName tableRename) {
        this.tableRename = tableRename;
    }

    public SQLExpr getFilePath() {
        return filePath;
    }

    public void setFilePath(SQLExpr filePath) {
        this.filePath = filePath;
    }

    public boolean isEncryptor() {
        return encryptor;
    }

    public void setEncryptor(boolean encryptor) {
        this.encryptor = encryptor;
    }

    public SQLExpr getOptEncryptor() {
        return optEncryptor;
    }

    public void setOptEncryptor(SQLExpr optEncryptor) {
        this.optEncryptor = optEncryptor;
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableName);
            acceptChild(visitor, tableRename);
            acceptChild(visitor, filePath);
            acceptChild(visitor, optEncryptor);
        }
        visitor.endVisit(this);
    }
}
