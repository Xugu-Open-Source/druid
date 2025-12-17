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

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;

public class XuGuAlterSchemaStatement extends XuGuStatementImpl implements SQLAlterStatement {
    private SQLName schemaName;
    private SQLName newName;
    private SQLName userName;

    public SQLName getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(SQLName schemaName) {
        this.schemaName = schemaName;
    }

    public SQLName getNewName() {
        return newName;
    }

    public void setNewName(SQLName newName) {
        this.newName = newName;
    }

    public SQLName getUserName() {
        return userName;
    }

    public void setUserName(SQLName userName) {
        this.userName = userName;
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, schemaName);
            acceptChild(visitor, newName);
            acceptChild(visitor, userName);
        }
        visitor.endVisit(this);
    }
}
