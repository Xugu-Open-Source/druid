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
package com.alibaba.druid.sql.dialect.xugu.ast.clause;

import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuStatementImpl;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;

/**
 * @author zz [455910092@qq.com]
 */
public class XuGuIterateStatement extends XuGuStatementImpl {
    private String labelName;

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

}
