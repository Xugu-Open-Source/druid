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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuObjectImpl;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuUnique;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuOutputVisitor;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuShowColumnOutpuVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class XuGuCreateTableStatement extends SQLCreateTableStatement implements XuGuStatement {

    private Map<String, SQLObject> tableOptions = new LinkedHashMap<String, SQLObject>();

    private List<SQLCommentHint>   hints        = new ArrayList<SQLCommentHint>();

    private List<SQLCommentHint>   optionHints  = new ArrayList<SQLCommentHint>();


    
    private SQLName                tableGroup;

    public XuGuCreateTableStatement(){
        super (JdbcConstants.XUGU);
    }



    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public void setTableOptions(Map<String, SQLObject> tableOptions) {
        this.tableOptions = tableOptions;
    }

    @Deprecated
    public SQLSelect getQuery() {
        return select;
    }

    @Deprecated
    public void setQuery(SQLSelect query) {
        this.select = query;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof XuGuASTVisitor) {
            accept0((XuGuASTVisitor) visitor);
        } else {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
    }

    public void accept0(XuGuASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, getHints());
            this.acceptChild(visitor, getTableSource());
            this.acceptChild(visitor, getTableElementList());
            this.acceptChild(visitor, getLike());
            this.acceptChild(visitor, getSelect());
        }
        visitor.endVisit(this);
    }

    public static class TableSpaceOption extends XuGuObjectImpl {

        private SQLName name;
        private SQLExpr storage;

        public SQLName getName() {
            return name;
        }

        public void setName(SQLName name) {
            if (name != null) {
                name.setParent(this);
            }
            this.name = name;
        }

        public SQLExpr getStorage() {
            return storage;
        }

        public void setStorage(SQLExpr storage) {
            if (storage != null) {
                storage.setParent(this);
            }
            this.storage = storage;
        }

        @Override
        public void accept0(XuGuASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, getName());
                acceptChild(visitor, getStorage());
            }
            visitor.endVisit(this);
        }

        public TableSpaceOption clone() {
            TableSpaceOption x = new TableSpaceOption();

            if (name != null) {
                x.setName(name.clone());
            }

            if (storage != null) {
                x.setStorage(storage.clone());
            }

            return x;
        }

    }

    public List<SQLCommentHint> getOptionHints() {
        return optionHints;
    }

    public void setOptionHints(List<SQLCommentHint> optionHints) {
        this.optionHints = optionHints;
    }

    
    public SQLName getTableGroup() {
        return tableGroup;
    }

    public void setTableGroup(SQLName tableGroup) {
        this.tableGroup = tableGroup;
    }

    @Override
    public void simplify() {
        tableOptions.clear();
        super.simplify();
    }

    public void showCoumns(Appendable out) throws IOException {
        this.accept(new XuGuShowColumnOutpuVisitor(out));
    }

    public boolean apply(XuGuRenameTableStatement x) {
        for (XuGuRenameTableStatement.Item item : x.getItems()) {
            if (apply(item)) {
                return true;
            }
        }

        return false;
    }

    protected boolean alterApply(SQLAlterTableItem item) {
        if (item instanceof XuGuAlterTableAlterColumn) {
            return apply((XuGuAlterTableAlterColumn) item);

        } else if (item instanceof XuGuAlterTableChangeColumn) {
            return apply((XuGuAlterTableChangeColumn) item);

        } else if (item instanceof SQLAlterCharacter) {
            return apply((SQLAlterCharacter) item);

        } else if (item instanceof XuGuAlterTableModifyColumn) {
            return apply((XuGuAlterTableModifyColumn) item);

        } else if (item instanceof XuGuAlterTableOption) {
            return apply((XuGuAlterTableOption) item);
        }

        return super.alterApply(item);
    }

    public boolean apply(SQLAlterTableAddIndex item) {
        if (item.isUnique()) {
            XuGuUnique x = new XuGuUnique();
            item.cloneTo(x);
            x.setParent(this);
            this.tableElementList.add(x);
            return true;
        }

        if (item.isKey()) {
            XuGuKey x = new XuGuKey();
            item.cloneTo(x);
            x.setParent(this);
            this.tableElementList.add(x);
            return true;
        }

        XuGuTableIndex x = new XuGuTableIndex();
        item.cloneTo(x);
        x.setParent(this);
        this.tableElementList.add(x);
        return true;
    }

    public boolean apply(XuGuAlterTableOption item) {
        this.tableOptions.put(item.getName(), item.getValue());
        return true;
    }

    public boolean apply(SQLAlterCharacter item) {
        SQLExpr charset = item.getCharacterSet();
        if (charset != null) {
            this.tableOptions.put("CHARACTER SET", charset);
        }

        SQLExpr collate = item.getCollate();
        if (collate != null) {
            this.tableOptions.put("COLLATE", collate);
        }
        return true;
    }

    public boolean apply(XuGuRenameTableStatement.Item item) {
        if (!SQLUtils.nameEquals((SQLName) item.getName(), this.getName())) {
            return false;
        }
        this.setName((SQLName) item.getTo().clone());
        return true;
    }

    public boolean apply(XuGuAlterTableAlterColumn x) {
        int columnIndex = columnIndexOf(x.getColumn());
        if (columnIndex == -1) {
            return false;
        }

        SQLExpr defaultExpr = x.getDefaultExpr();
        SQLColumnDefinition column = (SQLColumnDefinition) tableElementList.get(columnIndex);

        if (x.isDropDefault()) {
            column.setDefaultExpr(null);
        } else if (defaultExpr != null) {
            column.setDefaultExpr(defaultExpr);
        }

        return true;
    }

    public boolean apply(XuGuAlterTableChangeColumn item) {
        SQLName columnName = item.getColumnName();
        int columnIndex = columnIndexOf(columnName);
        if (columnIndex == -1) {
            return false;
        }

        int afterIndex = columnIndexOf(item.getAfterColumn());
        int beforeIndex = columnIndexOf(item.getFirstColumn());

        int insertIndex = -1;
        if (beforeIndex != -1) {
            insertIndex = beforeIndex;
        } else if (afterIndex != -1) {
            insertIndex = afterIndex + 1;
        } else if (item.isFirst()) {
            insertIndex = 0;
        }

        SQLColumnDefinition column = item.getNewColumnDefinition().clone();
        column.setParent(this);
        if (insertIndex == -1 || insertIndex == columnIndex) {
            tableElementList.set(columnIndex, column);
        } else {
            if (insertIndex > columnIndex) {
                tableElementList.add(insertIndex, column);
                tableElementList.remove(columnIndex);
            } else {
                tableElementList.remove(columnIndex);
                tableElementList.add(insertIndex, column);
            }
        }

        for (int i = 0; i < tableElementList.size(); i++) {
            SQLTableElement e = tableElementList.get(i);
            if(e instanceof XuGuTableIndex) {
                ((XuGuTableIndex) e).applyColumnRename(columnName, column.getName());
            } else if (e instanceof SQLUnique) {
                SQLUnique unique = (SQLUnique) e;
                unique.applyColumnRename(columnName, column.getName());
            }
        }

        return true;
    }

    public boolean apply(XuGuAlterTableModifyColumn item) {
        SQLColumnDefinition column = item.getNewColumnDefinition().clone();
        SQLName columnName = column.getName();

        int columnIndex = columnIndexOf(columnName);
        if (columnIndex == -1) {
            return false;
        }

        int afterIndex = columnIndexOf(item.getAfterColumn());
        int beforeIndex = columnIndexOf(item.getFirstColumn());

        int insertIndex = -1;
        if (beforeIndex != -1) {
            insertIndex = beforeIndex;
        } else if (afterIndex != -1) {
            insertIndex = afterIndex + 1;
        }

        column.setParent(this);
        if (insertIndex == -1 || insertIndex == columnIndex) {
            tableElementList.set(columnIndex, column);
            return true;
        } else {
            if (insertIndex > columnIndex) {
                tableElementList.add(insertIndex, column);
                tableElementList.remove(columnIndex);
            } else {
                tableElementList.remove(columnIndex);
                tableElementList.add(insertIndex, column);
            }
        }

        return true;
    }

    public void output(StringBuffer buf) {
        this.accept(new XuGuOutputVisitor(buf));
    }

    public void cloneTo(XuGuCreateTableStatement x) {
        super.cloneTo(x);
        for (Map.Entry<String, SQLObject> entry : tableOptions.entrySet()) {
            SQLObject obj = entry.getValue().clone();
            obj.setParent(x);
            x.tableOptions.put(entry.getKey(), obj);
        }
        if (partitioning != null) {
            x.setPartitioning(partitioning.clone());
        }
        for (SQLCommentHint hint : hints) {
            SQLCommentHint h2 = hint.clone();
            h2.setParent(x);
            x.hints.add(h2);
        }
        for (SQLCommentHint hint : optionHints) {
            SQLCommentHint h2 = hint.clone();
            h2.setParent(x);
            x.optionHints.add(h2);
        }
        if (like != null) {
            x.setLike(like.clone());
        }
        if (tableGroup != null) {
            x.setTableGroup(tableGroup.clone());
        }
    }

    public XuGuCreateTableStatement clone() {
        XuGuCreateTableStatement x = new XuGuCreateTableStatement();
        cloneTo(x);
        return x;
    }
}
