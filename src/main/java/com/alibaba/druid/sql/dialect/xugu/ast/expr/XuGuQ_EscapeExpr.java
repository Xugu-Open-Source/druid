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
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuObjectImpl;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;

import java.util.Collections;
import java.util.List;

public class XuGuQ_EscapeExpr extends XuGuObjectImpl implements SQLExpr {

    private String text;

    public XuGuQ_EscapeExpr() {

    }

    public XuGuQ_EscapeExpr(String text) {
        this.text = text;
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        // 格式检测：不能有空if块
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public XuGuQ_EscapeExpr clone() {
        return new XuGuQ_EscapeExpr(text);
    }
}
