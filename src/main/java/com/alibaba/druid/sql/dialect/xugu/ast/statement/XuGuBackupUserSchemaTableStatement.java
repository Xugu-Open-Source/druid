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


public class XuGuBackupUserSchemaTableStatement extends XuGuStatementImpl {
    private boolean user;
    private boolean schema;
    private boolean table;
    private SQLName userName;
    private SQLName schemaName;
    private SQLName tableName;
    private boolean append;
    private SQLExpr filePath;
    private boolean encryptor;
    private SQLExpr optEncryptor;
    private boolean compress;
    private boolean nocompress;

    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }

    public boolean isSchema() {
        return schema;
    }

    public void setSchema(boolean schema) {
        this.schema = schema;
    }

    public boolean isTable() {
        return table;
    }

    public void setTable(boolean table) {
        this.table = table;
    }

    public SQLName getUserName() {
        return userName;
    }

    public void setUserName(SQLName userName) {
        this.userName = userName;
    }

    public SQLName getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(SQLName schemaName) {
        this.schemaName = schemaName;
    }

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.tableName = tableName;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
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

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public boolean isNocompress() {
        return nocompress;
    }

    public void setNocompress(boolean nocompress) {
        this.nocompress = nocompress;
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, userName);
            acceptChild(visitor, schemaName);
            acceptChild(visitor, tableName);
            acceptChild(visitor, filePath);
            acceptChild(visitor, optEncryptor);
        }
        visitor.endVisit(this);
    }
}
