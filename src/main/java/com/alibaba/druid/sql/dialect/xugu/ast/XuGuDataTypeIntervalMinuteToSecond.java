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
package com.alibaba.druid.sql.dialect.xugu.ast;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class XuGuDataTypeIntervalMinuteToSecond extends SQLDataTypeImpl implements XuGuObject {

    public XuGuDataTypeIntervalMinuteToSecond() {
        this.setName("INTERVAL MINUTE TO SECOND");
    }

    private boolean toSecond;

    protected final List<SQLExpr> fractionalSeconds = new ArrayList<SQLExpr>();

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((XuGuASTVisitor) visitor);
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getArguments());
        }
        visitor.endVisit(this);
    }

    public boolean isToSecond() {
        return toSecond;
    }

    public void setToSecond(boolean toSecond) {
        this.toSecond = toSecond;
    }

    public List<SQLExpr> getFractionalSeconds() {
        return fractionalSeconds;
    }

    public XuGuDataTypeIntervalMinuteToSecond clone() {
        XuGuDataTypeIntervalMinuteToSecond x = new XuGuDataTypeIntervalMinuteToSecond();

        super.cloneTo(x);
        for (SQLExpr arg : fractionalSeconds) {
            arg.setParent(x);
            x.fractionalSeconds.add(arg);
        }

        return x;
    }
}
