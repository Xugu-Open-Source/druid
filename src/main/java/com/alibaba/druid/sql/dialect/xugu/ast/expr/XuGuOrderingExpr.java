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
package com.alibaba.druid.sql.dialect.xugu.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class XuGuOrderingExpr extends SQLExprImpl implements XuGuExpr {

    protected SQLExpr                  expr;
    protected SQLOrderingSpecification type;
    
    public XuGuOrderingExpr() {
        
    }
    
    public XuGuOrderingExpr(SQLExpr expr, SQLOrderingSpecification type){
        super();
        setExpr(expr);
        this.type = type;
    }

    public XuGuOrderingExpr clone() {
        XuGuOrderingExpr x = new XuGuOrderingExpr();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.type = type;
        return x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        XuGuASTVisitor xgVisitor = (XuGuASTVisitor) visitor;
        if (xgVisitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        xgVisitor.endVisit(this);
    }

    @Override
    public List getChildren() {
        return Collections.singletonList(this.expr);
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

    public SQLOrderingSpecification getType() {
        return type;
    }

    public void setType(SQLOrderingSpecification type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        XuGuOrderingExpr other = (XuGuOrderingExpr) obj;
        if (expr != other.expr) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

}
