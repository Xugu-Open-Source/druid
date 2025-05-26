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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuReturningClause;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class XuGuInsertStatement extends SQLInsertStatement implements XuGuStatement{

    private XuGuReturningClause returning;
    private boolean             lowPriority        = false;
    private boolean             delayed            = false;
    private boolean             highPriority       = false;
    private boolean             ignore             = false;
    private boolean             rollbackOnFail     = false;
    private boolean             defaultValues      = false;
    private boolean             xgSubPartition     = false;
    private List<SQLName>       partitions;

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
        if (partitions != null) {
            for (SQLName p : partitions) {
                SQLName p1 = p.clone();
                x.addPartition(p1);
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

    public List<SQLName> getPartitions() {
        if (this.partitions == null) {
            this.partitions = new ArrayList<SQLName>(2);
        }
        return partitions;
    }

    public void addPartition(SQLName partition) {
        if (partition != null) {
            partition.setParent(this);
        }

        if (this.partitions == null) {
            this.partitions = new ArrayList<SQLName>(2);
        }
        this.partitions.add(partition);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof XuGuASTVisitor) {
            accept0((XuGuASTVisitor) visitor);
        } else {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
    }

    public void output(StringBuffer buf) {
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
