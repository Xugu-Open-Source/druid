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
package com.alibaba.druid.wall.spi;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuOutFileExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuDeleteStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuInsertStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuSelectQueryBlock;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCreateTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuUpdateStatement;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitor;
import com.alibaba.druid.sql.dialect.xugu.visitor.XuGuASTVisitorAdapter;
import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallSqlTableStat;
import com.alibaba.druid.wall.WallUpdateCheckItem;
import com.alibaba.druid.wall.WallVisitor;
import com.alibaba.druid.wall.spi.WallVisitorUtils.WallTopStatementContext;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

import java.util.ArrayList;
import java.util.List;

public class XuGuWallVisitor extends XuGuASTVisitorAdapter implements WallVisitor, XuGuASTVisitor {
    private final WallConfig config;
    private final WallProvider provider;
    private final List<Violation> violations = new ArrayList<Violation>();
    private boolean sqlModified;
    private boolean sqlEndOfComment;
    private List<WallUpdateCheckItem> updateCheckItems;

    public XuGuWallVisitor(WallProvider provider) {
        this.config = provider.getConfig();
        this.provider = provider;
    }

    @Override
    public DbType getDbType() {
        return DbType.xugu;
    }

    @Override
    public boolean isSqlModified() {
        return sqlModified;
    }

    @Override
    public void setSqlModified(boolean sqlModified) {
        this.sqlModified = sqlModified;
    }

    @Override
    public WallProvider getProvider() {
        return provider;
    }

    @Override
    public WallConfig getConfig() {
        return config;
    }

    @Override
    public void addViolation(Violation violation) {
        this.violations.add(violation);
    }

    @Override
    public List<Violation> getViolations() {
        return violations;
    }

    public boolean visit(SQLInListExpr x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        return WallVisitorUtils.check(this, x);
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        WallVisitorUtils.checkSelelct(this, x);

        return true;
    }

    @Override
    public boolean visit(XuGuSelectQueryBlock x) {
        WallVisitorUtils.checkSelelct(this, x);
        return true;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        WallVisitorUtils.checkHaving(this, x.getHaving());
        return true;
    }

    @Override
    public boolean visit(XuGuDeleteStatement x) {
        WallVisitorUtils.checkReadOnly(this, x.getFrom());

        return visit((SQLDeleteStatement) x);
    }

    @Override
    public boolean visit(SQLDeleteStatement x) {
        WallVisitorUtils.checkDelete(this, x);
        return true;
    }

    @Override
    public boolean visit(XuGuUpdateStatement x) {
        return visit((SQLUpdateStatement) x);
    }

    @Override
    public boolean visit(SQLUpdateStatement x) {
        WallVisitorUtils.initWallTopStatementContext();
        WallVisitorUtils.checkUpdate(this, x);
        return true;
    }

    @Override
    public void endVisit(SQLUpdateStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    @Override
    public boolean visit(XuGuInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }

    @Override
    public boolean visit(SQLInsertStatement x) {
        WallVisitorUtils.initWallTopStatementContext();
        WallVisitorUtils.checkInsert(this, x);
        return true;
    }

    @Override
    public void endVisit(SQLInsertStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    @Override
    public boolean visit(SQLSelectStatement x) {
        if (!config.isSelelctAllow()) {
            this.getViolations().add(new IllegalSQLObjectViolation(ErrorCode.SELECT_NOT_ALLOW, "select not allow",
                                                                   this.toSQL(x)));
            return false;
        }

        WallVisitorUtils.initWallTopStatementContext();

        int selectLimit = config.getSelectLimit();
        if (selectLimit >= 0) {
            SQLSelect select = x.getSelect();
            PagerUtils.limit(select, getDbType(), 0, selectLimit, true);
            this.sqlModified = true;
        }
        return true;
    }

    @Override
    public void endVisit(SQLSelectStatement x) {
        WallVisitorUtils.clearWallTopStatementContext();
    }

    @Override
    public boolean visit(SQLLimit x) {
        if (x.getRowCount() instanceof SQLNumericLiteralExpr) {
            WallContext context = WallContext.current();

            int rowCount = ((SQLNumericLiteralExpr) x.getRowCount()).getNumber().intValue();
            if (rowCount == 0) {
                if (context != null) {
                    context.incrementWarnings();
                }

                if (!provider.getConfig().isLimitZeroAllow()) {
                    this.getViolations().add(new IllegalSQLObjectViolation(ErrorCode.LIMIT_ZERO, "limit row 0",
                                                                           this.toSQL(x)));
                }
            }
        }
        return true;
    }

    public boolean visit(SQLPropertyExpr x) {
        if (x.getOwner() instanceof SQLVariantRefExpr) {
            SQLVariantRefExpr varExpr = (SQLVariantRefExpr) x.getOwner();
            SQLObject parent = x.getParent();
            String varName = varExpr.getName();
            if (varName.equalsIgnoreCase("@@session") || varName.equalsIgnoreCase("@@global")) {
                if (!(parent instanceof SQLSelectItem) && !(parent instanceof SQLAssignItem)) {
                    violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY,
                                                                 "variable in condition not allow", toSQL(x)));
                    return false;
                }

                if (!checkVar(x.getParent(), x.getName())) {
                    boolean isTop = WallVisitorUtils.isTopNoneFromSelect(this, x);
                    if (!isTop) {
                        boolean allow = true;
                        if (isDeny(varName)
                            && (WallVisitorUtils.isWhereOrHaving(x) || WallVisitorUtils.checkSqlExpr(varExpr))) {
                            allow = false;
                        }

                        if (!allow) {
                            violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY,
                                                                         "variable not allow : " + x.getName(),
                                                                         toSQL(x)));
                        }
                    }
                }
                return false;
            }
        }

        WallVisitorUtils.check(this, x);
        return true;
    }

    public boolean checkVar(SQLObject parent, String varName) {
        if (varName == null) {
            return false;
        }

        if (varName.equals("?")) {
            return true;
        }

        if (!config.isVariantCheck()) {
            return true;
        }

        if (varName.startsWith("@@")) {
            if (!(parent instanceof SQLSelectItem) && !(parent instanceof SQLAssignItem)) {
                return false;
            }

            varName = varName.substring(2);
        }

        if (config.getPermitVariants().contains(varName)) {
            return true;
        }

        return false;
    }

    public boolean isDeny(String varName) {
        if (varName.startsWith("@@")) {
            varName = varName.substring(2);
        }

        varName = varName.toLowerCase();
        return config.getDenyVariants().contains(varName);
    }

    public boolean visit(SQLVariantRefExpr x) {
        String varName = x.getName();
        if (varName == null) {
            return false;
        }

        if (varName.startsWith("@@") && !checkVar(x.getParent(), x.getName())) {
            final WallTopStatementContext topStatementContext = WallVisitorUtils.getWallTopStatementContext();
            if (topStatementContext != null
                && (topStatementContext.fromSysSchema() || topStatementContext.fromSysTable())) {
                return false;
            }

            boolean isTop = WallVisitorUtils.isTopNoneFromSelect(this, x);
            if (!isTop) {
                boolean allow = true;
                if (isDeny(varName) && (WallVisitorUtils.isWhereOrHaving(x) || WallVisitorUtils.checkSqlExpr(x))) {
                    allow = false;
                }

                if (!allow) {
                    violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY, "variable not allow : "
                                                                                         + x.getName(), toSQL(x)));
                }
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        WallVisitorUtils.checkFunction(this, x);

        return true;
    }

    public boolean visit(SQLExprTableSource x) {
        WallVisitorUtils.check(this, x);

        if (x.getExpr() instanceof SQLName) {
            return false;
        }

        return true;
    }

    @Override
    public boolean visit(XuGuOutFileExpr x) {
        if (!config.isSelectIntoOutfileAllow() && !WallVisitorUtils.isTopSelectOutFile(x)) {
            violations.add(new IllegalSQLObjectViolation(ErrorCode.INTO_OUTFILE, "into out file not allow", toSQL(x)));
        }

        return true;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        WallVisitorUtils.checkUnion(this, x);

        return true;
    }

    @Override
    public String toSQL(SQLObject obj) {
        return SQLUtils.toXuGuString(obj);
    }

    @Override
    public boolean isDenyTable(String name) {
        if (!config.isTableCheck()) {
            return false;
        }

        return !this.provider.checkDenyTable(name);
    }

    public void preVisit(SQLObject x) {
        WallVisitorUtils.preVisitCheck(this, x);
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        WallVisitorUtils.check(this, x);
        return false;
    }

    @Override
    public boolean visit(XuGuCreateTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    public boolean visit(SQLAlterTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    public boolean visit(SQLDropTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(SQLSetStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLCallStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLCommentHint x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(XuGuShowCreateTableStatement x) {
        String tableName = ((SQLName) x.getName()).getSimpleName();
        WallContext context = WallContext.current();
        if (context != null) {
            WallSqlTableStat tableStat = context.getTableStat(tableName);
            if (tableStat != null) {
                tableStat.incrementShowCount();
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLCreateTriggerStatement x) {
        return false;
    }

    @Override
    public boolean isSqlEndOfComment() {
        return this.sqlEndOfComment;
    }

    @Override
    public void setSqlEndOfComment(boolean sqlEndOfComment) {
        this.sqlEndOfComment = sqlEndOfComment;
    }

    public void addWallUpdateCheckItem(WallUpdateCheckItem item) {
        if (updateCheckItems == null) {
            updateCheckItems = new ArrayList<WallUpdateCheckItem>();
        }
        updateCheckItems.add(item);
    }

    public List<WallUpdateCheckItem> getUpdateCheckItems() {
        return updateCheckItems;
    }

    public boolean visit(SQLJoinTableSource x) {
        WallVisitorUtils.check(this, x);
        return true;
    }
}
