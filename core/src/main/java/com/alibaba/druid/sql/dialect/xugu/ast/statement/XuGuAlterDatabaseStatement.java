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

public class XuGuAlterDatabaseStatement extends XuGuStatementImpl implements SQLAlterStatement {
    private SQLName databaseName;

    private SQLName databaseNewName;

    public SQLName getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(SQLName databaseName) {
        this.databaseName = databaseName;
    }

    public SQLName getDatabaseNewName() {
        return databaseNewName;
    }

    public void setDatabaseNewName(SQLName databaseNewName) {
        this.databaseNewName = databaseNewName;
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, databaseName);
            acceptChild(visitor, databaseNewName);
        }
        visitor.endVisit(this);
    }
}
