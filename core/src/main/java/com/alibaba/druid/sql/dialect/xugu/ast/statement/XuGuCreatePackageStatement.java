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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class XuGuCreatePackageStatement extends XuGuStatementImpl implements SQLCreateStatement {
    private boolean orReplace;
    private SQLName name;

    private boolean body;

    private final List<SQLStatement> statements = new ArrayList<SQLStatement>();

    public XuGuCreatePackageStatement() {
        super.setDbType(DbType.xugu);
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, statements);
        }
        visitor.endVisit(this);
    }

    public XuGuCreatePackageStatement clone() {
        XuGuCreatePackageStatement x = new XuGuCreatePackageStatement();

        x.orReplace = orReplace;
        if (name != null) {
            x.setName(name.clone());
        }
        x.body = body;

        for (SQLStatement stmt : statements) {
            SQLStatement s2 = stmt.clone();
            s2.setParent(x);
            x.statements.add(s2);
        }

        return x;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public boolean isBody() {
        return body;
    }

    public void setBody(boolean body) {
        this.body = body;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public List<SQLStatement> getStatements() {
        return statements;
    }
}
