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
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class XuGuDataTypeIntervalYearToMonth extends SQLDataTypeImpl implements XuGuObject {

    public XuGuDataTypeIntervalYearToMonth() {
        this.setName("INTERVAL YEAR TO MONTH");
    }

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

    public XuGuDataTypeIntervalYearToMonth clone() {
        XuGuDataTypeIntervalYearToMonth x = new XuGuDataTypeIntervalYearToMonth();

        super.cloneTo(x);

        return x;
    }

}
