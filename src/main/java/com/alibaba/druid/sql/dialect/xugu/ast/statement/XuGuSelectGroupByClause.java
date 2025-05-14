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
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuObject;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XuGuSelectGroupByClause extends XuGuStatementImpl {

    private List<GroupItem> groupItems;
    private SQLExpr having;

    public XuGuSelectGroupByClause clone() {
        XuGuSelectGroupByClause x = new XuGuSelectGroupByClause();
        for (GroupItem item : groupItems) {
            SQLObject item2 = item.clone();
            item2.setParent(x);
            x.groupItems.add((GroupItem) item2);
        }
        if (having != null) {
            x.setHaving(having.clone());
        }
        return x;
    }

    public SQLExpr getHaving() {
        return having;
    }

    public void setHaving(SQLExpr having) {
        this.having = having;
    }

    public List<GroupItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(List<GroupItem> groupItems) {
        this.groupItems = groupItems;
    }

    @Override
    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.groupItems);
            acceptChild(visitor, this.having);
        }
        visitor.endVisit(this);
    }

    public interface GroupItem extends XuGuObject {
    }

    /**
     * 简单的表达式，通常是列名或基于列的表达式
     */
    public static class XgExprGroupItem extends SQLExprImpl implements GroupItem {
        private final List<SQLExpr> items = new ArrayList<SQLExpr>();

        public List<SQLExpr> getItems() {
            return items;
        }

        public void addItems(SQLExpr sqlExpr) {
            if (sqlExpr != null) {
                sqlExpr.setParent(this);
                this.items.add(sqlExpr);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof XgExprGroupItem)) {
                return false;
            }
            XgExprGroupItem other = (XgExprGroupItem) obj;
            if (items == null) {
                if (other.items != null) {
                    return false;
                }
            } else if (!items.equals(other.items)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((items == null) ? 0 : items.hashCode());
            return result;
        }

        @Override
        public SQLExpr clone() {
            XgExprGroupItem x = new XgExprGroupItem();
            for (SQLExpr item : items) {
                SQLExpr item2 = item.clone();
                item2.setParent(x);
                x.items.add(item2);
            }
            return x;
        }

        @Override
        public List getChildren() {
            return this.items;
        }

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
                acceptChild(visitor, items);
            }
            visitor.endVisit(this);
        }
    }

    /**
     * GROUP BY的三种扩展
     */
    public static class XgCompositeGroupItem extends SQLExprImpl implements GroupItem {

        private GroupType type;
        private final List<GroupItem> groupItems = new ArrayList<GroupItem>();

        public List<GroupItem> getGroupItems() {
            return this.groupItems;
        }

        public void addGroupItems(GroupItem sqlExpr) {
            if (sqlExpr != null) {
                sqlExpr.setParent(this);
                this.groupItems.add(sqlExpr);
            }
        }

        public GroupType getType() {
            return type;
        }

        public void setType(GroupType type) {
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
            if (!(obj instanceof XgCompositeGroupItem)) {
                return false;
            }
            XgCompositeGroupItem other = (XgCompositeGroupItem) obj;
            if (this.groupItems == null) {
                if (other.groupItems != null) {
                    return false;
                }
            } else if (!this.groupItems.equals(other.groupItems)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.groupItems == null) ? 0 : this.groupItems.hashCode());
            return result;
        }

        @Override
        public SQLExpr clone() {
            XgCompositeGroupItem x = new XgCompositeGroupItem();
            for (GroupItem item : this.groupItems) {
                SQLObject item2 = item.clone();
                item2.setParent(x);
                x.groupItems.add((GroupItem) item2);
            }
            return x;
        }

        @Override
        public List getChildren() {
            return this.groupItems;
        }

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
                acceptChild(visitor, this.groupItems);
            }
            visitor.endVisit(this);
        }
    }

    public enum GroupType {
        ROLLUP, CUBE, GROUPING_SETS
    }

    /**
     * 空分组项：()
     */
    public static class XgEmptyGroupItem extends SQLExprImpl implements GroupItem {
        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public SQLExpr clone() {
            return new XgEmptyGroupItem();
        }

        @Override
        public List<SQLObject> getChildren() {
            return Collections.emptyList();
        }

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
            }
            visitor.endVisit(this);
        }
    }
}
