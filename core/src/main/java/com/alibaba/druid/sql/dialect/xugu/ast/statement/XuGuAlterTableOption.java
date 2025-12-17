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

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuObjectImpl;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;

public class XuGuAlterTableOption extends XuGuObjectImpl implements SQLAlterTableItem {
    private String name;
    private SQLObject value;

    public XuGuAlterTableOption(String name, String value) {
        this(name, new SQLIdentifierExpr(value));
    }

    public XuGuAlterTableOption(String name, SQLObject value) {
        this.name = name;
        this.setValue(value);
    }

    public XuGuAlterTableOption() {
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SQLObject getValue() {
        return value;
    }

    public void setValue(SQLObject value) {
        this.value = value;
    }

}
