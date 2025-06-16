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
import com.alibaba.druid.sql.ast.SQLSubPartitionBy;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuObject;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class XuGuSubPartitionByList extends SQLSubPartitionBy implements XuGuObject {

    private SQLExpr expr;

    private List<SQLColumnDefinition> columns = new ArrayList<SQLColumnDefinition>();

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof XuGuASTVisitor) {
            accept0((XuGuASTVisitor) visitor);
        } else {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
            acceptChild(visitor, columns);
            acceptChild(visitor, subPartitionsCount);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    public List<SQLColumnDefinition> getColumns() {
        return columns;
    }

    public void addColumn(SQLColumnDefinition column) {
        if (column != null) {
            column.setParent(this);
        }
        this.columns.add(column);
    }

    public void cloneTo(XuGuSubPartitionByList x) {
        super.cloneTo(x);
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        for (SQLColumnDefinition column : columns) {
            SQLColumnDefinition c2 = column.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }
    }

    public XuGuSubPartitionByList clone() {
        XuGuSubPartitionByList x = new XuGuSubPartitionByList();
        cloneTo(x);
        return x;
    }
}
