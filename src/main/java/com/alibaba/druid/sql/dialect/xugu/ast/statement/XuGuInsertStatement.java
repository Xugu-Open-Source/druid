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
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuReturningClause;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class XuGuInsertStatement extends SQLInsertStatement implements XuGuStatement {

    private XuGuReturningClause returning;
    private boolean lowPriority;
    private boolean delayed;
    private boolean highPriority;
    private boolean ignore;
    private boolean rollbackOnFail;
    private boolean defaultValues;
    private boolean xgSubPartition;
    private List<SQLName> xgPartitions;

    private final List<SQLExpr> duplicateKeyUpdate = new ArrayList<SQLExpr>();

    public XuGuInsertStatement() {
        dbType = JdbcConstants.XUGU;
    }

    public void cloneTo(XuGuInsertStatement x) {
        super.cloneTo(x);
        if (returning != null) {
            x.setReturning(returning.clone());
        }
        x.lowPriority = lowPriority;
        x.delayed = delayed;
        x.highPriority = highPriority;
        x.ignore = ignore;
        x.rollbackOnFail = rollbackOnFail;
        x.defaultValues = defaultValues;
        x.xgSubPartition = xgSubPartition;
        for (SQLExpr e : duplicateKeyUpdate) {
            SQLExpr e2 = e.clone();
            e2.setParent(x);
            x.duplicateKeyUpdate.add(e2);
        }
        if (xgPartitions != null) {
            for (SQLName p : xgPartitions) {
                SQLName p1 = p.clone();
                x.addXgPartition(p1);
            }
        }
    }

    public List<SQLExpr> getDuplicateKeyUpdate() {
        return duplicateKeyUpdate;
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isRollbackOnFail() {
        return rollbackOnFail;
    }

    public void setRollbackOnFail(boolean rollbackOnFail) {
        this.rollbackOnFail = rollbackOnFail;
    }

    public boolean isDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(boolean defaultValues) {
        this.defaultValues = defaultValues;
    }

    public XuGuReturningClause getReturning() {
        return returning;
    }

    public void setReturning(XuGuReturningClause returning) {
        this.returning = returning;
    }

    public boolean isXgSubPartition() {
        return xgSubPartition;
    }

    public void setXgSubPartition(boolean xgSubPartition) {
        this.xgSubPartition = xgSubPartition;
    }

    public List<SQLName> getXgPartitions() {
        if (this.xgPartitions == null) {
            this.xgPartitions = new ArrayList<SQLName>(2);
        }
        return xgPartitions;
    }

    public void addXgPartition(SQLName xgPartitions) {
        if (xgPartitions != null) {
            xgPartitions.setParent(this);
        }

        if (this.xgPartitions == null) {
            this.xgPartitions = new ArrayList<SQLName>(2);
        }
        this.xgPartitions.add(xgPartitions);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof XuGuASTVisitor) {
            accept0((XuGuASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void output(StringBuilder buf) {
        new XuGuOutputVisitor(buf).visit(this);
    }

    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, getTableSource());
            this.acceptChild(visitor, getColumns());
            this.acceptChild(visitor, getValuesList());
            this.acceptChild(visitor, getQuery());
            this.acceptChild(visitor, getDuplicateKeyUpdate());
            this.acceptChild(visitor, returning);
        }

        visitor.endVisit(this);
    }

    public SQLInsertStatement clone() {
        XuGuInsertStatement x = new XuGuInsertStatement();
        cloneTo(x);
        return x;
    }
}
