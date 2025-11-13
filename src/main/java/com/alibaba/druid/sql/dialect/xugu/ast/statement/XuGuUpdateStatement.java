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

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class XuGuUpdateStatement extends SQLUpdateStatement implements XuGuStatement {
    private SQLLimit limit;

    private boolean lowPriority;
    private boolean ignore;
    private boolean commitOnSuccess;
    private boolean rollBackOnFail;
    private boolean queryOnPk;
    private SQLExpr targetAffectRow;

    // for petadata
    private boolean forceAllPartitions;
    private SQLName forcePartition;
    private List<SQLExpr> returningInto = new ArrayList<SQLExpr>();
    private boolean optBulk;
    protected List<SQLCommentHint> hints;

    public XuGuUpdateStatement() {
        super(JdbcConstants.XUGU);
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit limit) {
        if (limit != null) {
            limit.setParent(this);
        }
        this.limit = limit;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof XuGuASTVisitor) {
            accept0((XuGuASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, items);
            acceptChild(visitor, where);
            acceptChild(visitor, orderBy);
            acceptChild(visitor, limit);
            acceptChild(visitor, returningInto);
            if (hints != null) {
                for (int i = 0; i < hints.size(); i++) {
                    SQLCommentHint hint = hints.get(i);
                    if (hint != null) {
                        hint.accept(visitor);
                    }
                }
            }
        }
        visitor.endVisit(this);
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isCommitOnSuccess() {
        return commitOnSuccess;
    }

    public void setCommitOnSuccess(boolean commitOnSuccess) {
        this.commitOnSuccess = commitOnSuccess;
    }

    public boolean isRollBackOnFail() {
        return rollBackOnFail;
    }

    public void setRollBackOnFail(boolean rollBackOnFail) {
        this.rollBackOnFail = rollBackOnFail;
    }

    public boolean isQueryOnPk() {
        return queryOnPk;
    }

    public void setQueryOnPk(boolean queryOnPk) {
        this.queryOnPk = queryOnPk;
    }

    public SQLExpr getTargetAffectRow() {
        return targetAffectRow;
    }

    public void setTargetAffectRow(SQLExpr targetAffectRow) {
        if (targetAffectRow != null) {
            targetAffectRow.setParent(this);
        }
        this.targetAffectRow = targetAffectRow;
    }

    public boolean isForceAllPartitions() {
        return forceAllPartitions;
    }

    public void setForceAllPartitions(boolean forceAllPartitions) {
        this.forceAllPartitions = forceAllPartitions;
    }

    public SQLName getForcePartition() {
        return forcePartition;
    }

    public void setForcePartition(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.forcePartition = x;
    }

    public List<SQLExpr> getReturningInto() {
        return returningInto;
    }

    public void setReturningInto(List<SQLExpr> returningInto) {
        this.returningInto = returningInto;
    }

    public boolean isOptBulk() {
        return optBulk;
    }

    public void setOptBulk(boolean optBulk) {
        this.optBulk = optBulk;
    }

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }
        return hints.size();
    }
}
