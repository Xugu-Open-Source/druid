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
package com.alibaba.druid.sql.dialect.xugu.visitor;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalDay;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalDayToHour;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalDayToMinute;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalDayToSecond;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalHour;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalHourToMinute;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalHourToSecond;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalMinute;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalMinuteToSecond;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalMonth;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalSecond;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalYear;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalYearToMonth;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuForceIndexHint;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuIgnoreIndexHint;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuPrimaryKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuUnique;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuUseIndexHint;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuForeignKey;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.ConditionValue;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.ConditionValue.ConditionType;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuCaseStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuCaseStatement.XuGuWhenStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuCursorDeclareStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuDeclareConditionStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuDeclareHandlerStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuDeclareStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuIterateStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuLeaveStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuRepeatStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuReturningClause;
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuSelectIntoStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuCharExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuExtractExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuOrderingExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuOutFileExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuRangeExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuUserName;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.*;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateUserStatement.UserSpecification;
import com.alibaba.druid.sql.visitor.*;
import com.alibaba.druid.util.JdbcConstants;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class XuGuOutputVisitor extends SQLASTOutputVisitor implements XuGuASTVisitor {

    {
        this.dbType = JdbcConstants.XUGU;
        this.shardingSupport = true;
    }

    public XuGuOutputVisitor(Appendable appender) {
        super(appender);
    }

    public XuGuOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);

        try {
            configFromProperty(System.getProperties());
        } catch (AccessControlException e) {
            // skip
        }
    }

    public void configFromProperty(Properties properties) {
        if (this.parameterized) {
            String property = properties.getProperty("druid.parameterized.shardingSupport");
            if ("true".equals(property)) {
                this.setShardingSupport(true);
            } else if ("false".equals(property)) {
                this.setShardingSupport(false);
            }
        }
    }

    public boolean isShardingSupport() {
        return this.parameterized
                && shardingSupport;
    }

    public void setShardingSupport(boolean shardingSupport) {
        this.shardingSupport = shardingSupport;
    }

    @Override
    public boolean visit(SQLSelectQueryBlock select) {
        if (select instanceof XuGuSelectQueryBlock) {
            return visit((XuGuSelectQueryBlock) select);
        }

        return super.visit(select);
    }

    public boolean visit(XuGuSelectQueryBlock x) {
        final boolean bracket = x.isBracket();
        if (bracket) {
            print('(');
        }

        if ((!isParameterized()) && isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        String cachedSelectList = x.getCachedSelectList();

        if (cachedSelectList != null) {
            if (!isEnabled(VisitorFeature.OutputSkipSelectListCacheString)) {
                print0(cachedSelectList);
            }
        } else {
            print0(ucase ? "SELECT " : "select ");

            for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
                SQLCommentHint hint = x.getHints().get(i);
                hint.accept(this);
                print(' ');
            }

            if (x.getTopExpr() != null) {
                print0(ucase ? "TOP " : "top ");
                x.getTopExpr().accept(this);
                print0(" ");
            }

            final int distionOption = x.getDistionOption();
            if (SQLSetQuantifier.ALL == distionOption) {
                print0(ucase ? "ALL " : "all ");
            } else if (SQLSetQuantifier.DISTINCT == distionOption) {
                print0(ucase ? "DISTINCT " : "distinct ");
            } else if (SQLSetQuantifier.DISTINCTROW == distionOption) {
                print0(ucase ? "DISTINCTROW " : "distinctrow ");
            }

            if (x.isHignPriority()) {
                print0(ucase ? "HIGH_PRIORITY " : "high_priority ");
            }

            if (x.isStraightJoin()) {
                print0(ucase ? "STRAIGHT_JOIN " : "straight_join ");
            }

            if (x.isSmallResult()) {
                print0(ucase ? "SQL_SMALL_RESULT " : "sql_small_result ");
            }

            if (x.isBigResult()) {
                print0(ucase ? "SQL_BIG_RESULT " : "sql_big_result ");
            }

            if (x.isBufferResult()) {
                print0(ucase ? "SQL_BUFFER_RESULT " : "sql_buffer_result ");
            }

            if (x.getCache() != null) {
                if (x.getCache().booleanValue()) {
                    print0(ucase ? "SQL_CACHE " : "sql_cache ");
                } else {
                    print0(ucase ? "SQL_NO_CACHE " : "sql_no_cache ");
                }
            }

            if (x.isCalcFoundRows()) {
                print0(ucase ? "SQL_CALC_FOUND_ROWS " : "sql_calc_found_rows ");
            }

            printSelectList(x.getSelectList());

            SQLName forcePartition = x.getForcePartition();
            if (forcePartition != null) {
                println();
                print0(ucase ? "FORCE PARTITION " : "force partition ");
                printExpr(forcePartition);
            }

            if (x.isBulk()) {
                println();
                print0(ucase ? "BULK COLLECT " : "bulk collect ");
            }

            SQLExprTableSource into = x.getInto();
            if (into != null) {
                println();
                print0(ucase ? "INTO " : "into ");
                printTableSource(into);
            }
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();
            print0(ucase ? "FROM " : "from ");

            printTableSource(from);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            printExpr(where);
        }

        printHierarchical(x);

        SQLSelectGroupByClause groupBy = x.getGroupBy();
        if (groupBy != null) {
            println();
            visit(groupBy);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            visit(orderBy);
        }

        SQLLimit limit = x.getLimit();
        if (limit != null) {
            println();
            visit(limit);
        }

        SQLName procedureName = x.getProcedureName();
        if (procedureName != null) {
            print0(ucase ? " PROCEDURE " : " procedure ");
            procedureName.accept(this);
            if (!x.getProcedureArgumentList().isEmpty()) {
                print('(');
                printAndAccept(x.getProcedureArgumentList(), ", ");
                print(')');
            }
        }

        if (x.isForUpdate()) {
            println();
            print0(ucase ? "FOR UPDATE" : "for update");
            if (x.isNoWait()) {
                print0(ucase ? " NOWAIT" : " nowait");
            } else if (x.getWaitTime() != null) {
                print0(ucase ? " WAIT " : " wait ");
                x.getWaitTime().accept(this);
            }
        }

        if (x.isLockInShareMode()) {
            println();
            print0(ucase ? "LOCK IN SHARE MODE" : "lock in share mode");
        }

        if (bracket) {
            print(')');
        }

        return false;
    }

    public boolean visit(SQLColumnDefinition x) {
        boolean parameterized = this.parameterized;
        this.parameterized = false;

        x.getName().accept(this);

        SQLDataType dataType = x.getDataType();
        if (dataType != null) {
            print(' ');
            dataType.accept(this);
        }

        final SQLExpr charsetExpr = x.getCharsetExpr();
        if (charsetExpr != null) {
            print0(ucase ? " CHARSET " : " charset ");
            charsetExpr.accept(this);
        }

        for (SQLColumnConstraint item : x.getConstraints()) {
            print(' ');
            item.accept(this);
        }

        final SQLExpr defaultExpr = x.getDefaultExpr();
        if (defaultExpr != null) {
            print0(ucase ? " DEFAULT " : " default ");
            defaultExpr.accept(this);
        }

        final SQLExpr storage = x.getStorage();
        if (storage != null) {
            print0(ucase ? " STORAGE " : " storage ");
            storage.accept(this);
        }

        SQLExpr onUpdate = x.getOnUpdate();
        if (onUpdate != null) {
            print0(ucase ? " ON UPDATE " : " on update ");

            onUpdate.accept(this);
        }

        if (x.isAutoIncrement()) {
            print0(ucase ? " AUTO_INCREMENT" : " auto_increment");
        }

        if (x.getComment() != null) {
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }

        if (x.getAsExpr() != null) {
            print0(ucase ? " AS (" : " as (");
            x.getAsExpr().accept(this);
            print(')');
        }

        if (x.isSorted()) {
            print0(ucase ? " SORTED" : " sorted");
        }

        this.parameterized = parameterized;
        return false;
    }

    public boolean visit(SQLDataType x) {
        printDataType(x);

        if (x instanceof SQLDataTypeImpl) {
            SQLDataTypeImpl dataTypeImpl = (SQLDataTypeImpl) x;
            if (dataTypeImpl.isUnsigned()) {
                print0(ucase ? " UNSIGNED" : " unsigned");
            }

            if (dataTypeImpl.isZerofill()) {
                print0(ucase ? " ZEROFILL" : " zerofill");
            }
        }

        if (x instanceof SQLCharacterDataType) {
            SQLCharacterDataType charType = (SQLCharacterDataType) x;
            if (charType.getCharSetName() != null) {
                print0(ucase ? " CHARACTER SET " : " character set ");
                print0(charType.getCharSetName());

                if (charType.getCollate() != null) {
                    print0(ucase ? " COLLATE " : " collate ");
                    print0(charType.getCollate());
                }
            }

            List<SQLCommentHint> hints = ((SQLCharacterDataType) x).hints;
            if (hints != null) {
                print(' ');
                for (SQLCommentHint hint : hints) {
                    hint.accept(this);
                }
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLCharacterDataType x) {
        printDataType(x);

        if (x.isHasBinary()) {
            print0(ucase ? " BINARY " : " binary ");
        }

        if (x.getCharSetName() != null) {
            print0(ucase ? " CHARACTER SET " : " character set ");
            print0(x.getCharSetName());
            if (x.getCollate() != null) {
                print0(ucase ? " COLLATE " : " collate ");
                print0(x.getCollate());
            }
        } else if (x.getCollate() != null) {
            print0(ucase ? " COLLATE " : " collate ");
            print0(x.getCollate());
        }

        List<SQLCommentHint> hints = ((SQLCharacterDataType) x).hints;
        if (hints != null) {
            print(' ');
            for (SQLCommentHint hint : hints) {
                hint.accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(XuGuTableIndex x) {

    }

    @Override
    public boolean visit(XuGuTableIndex x) {
        String indexType = x.getIndexType();

        boolean indexTypePrinted = false;
        if ("FULLTEXT".equalsIgnoreCase(indexType)) {
            print0(ucase ? "FULLTEXT " : "fulltext ");
            indexTypePrinted = true;
        } else if ("SPATIAL".equalsIgnoreCase(indexType)) {
            print0(ucase ? "SPATIAL " : "spatial ");
            indexTypePrinted = true;
        }

        print0(ucase ? "INDEX" : "index");
        if (x.getName() != null) {
            print(' ');
            x.getName().accept(this);
        }

        if (indexType != null && !indexTypePrinted) {
            print0(ucase ? " USING " : " using ");
            print0(indexType);
        }

        print('(');
        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');
        return false;
    }

    public boolean visit(XuGuCreateTableStatement x) {
        if (isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "CREATE " : "create ");

        for (SQLCommentHint hint : x.getHints()) {
            hint.accept(this);
            print(' ');
        }

        if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(x.getType())) {
            print0(ucase ? "TEMPORARY TABLE " : "temporary table ");
        } else {
            print0(ucase ? "TABLE " : "table ");
        }

        if (x.isIfNotExiists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printTableSourceExpr(x.getName());

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        printTableElements(x.getTableElementList());

        for (Map.Entry<String, SQLObject> option : x.getTableOptions().entrySet()) {
            String key = option.getKey();

            print(' ');
            print0(ucase ? key : key.toLowerCase());

            if ("TABLESPACE".equals(key)) {
                print(' ');
                option.getValue().accept(this);
                continue;
            } else if ("UNION".equals(key)) {
                print0(" = (");
                option.getValue().accept(this);
                print(')');
                continue;
            }

            print0(" = ");

            option.getValue().accept(this);
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            comment.accept(this);
        }

        if (x.getPartitioning() != null) {
            println();
            x.getPartitioning().accept(this);
        }

        if (x.getTableGroup() != null) {
            println();
            print0(ucase ? "TABLEGROUP " : "tablegroup ");
            x.getTableGroup().accept(this);
        }

        if (x.getSelect() != null) {
            println();
            print0(ucase ? "AS" : "as");
            println();
            x.getSelect().accept(this);
        }

        for (SQLCommentHint hint : x.getOptionHints()) {
            print(' ');
            hint.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuKey x) {

    }

    @Override
    public void endVisit(XuGuPrimaryKey x) {

    }

    @Override
    public void endVisit(XuGuForeignKey x) {

    }

    @Override
    public boolean visit(XuGuKey x) {
        if (x.isHasConstaint()) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        print0(ucase ? "KEY" : "key");

        SQLName name = x.getName();
        if (name != null) {
            print(' ');
            name.accept(this);
        }

        if (x.getIndexType() != null) {
            print0(ucase ? " USING " : " using ");
            print0(x.getIndexType());
        }

        print0(" (");

        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');

        return false;
    }

    @Override
    public boolean visit(XuGuPrimaryKey x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }

        print0(ucase ? "PRIMARY KEY" : "primary key");

        if (x.getIndexType() != null) {
            print0(ucase ? " USING " : " using ");
            print0(x.getIndexType());
        }

        print0(" (");

        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');

        return false;
    }

    public boolean visit(SQLCharExpr x) {
        if (this.appender == null) {
            return false;
        }

        try {
            if (this.parameterized) {
                this.appender.append('?');
                incrementReplaceCunt();
                if (this.parameters != null) {
                    ExportParameterVisitorUtils.exportParameter(this.parameters, x);
                }
                return false;
            }

            this.appender.append('\'');

            String text = x.getText();

            boolean hasSpecial = false;
            for (int i = 0; i < text.length(); ++i) {
                char ch = text.charAt(i);
                if (ch == '\'' || ch == '\\' || ch == '\0') {
                    hasSpecial = true;
                    break;
                }
            }

            if (hasSpecial) {
                for (int i = 0; i < text.length(); ++i) {
                    char ch = text.charAt(i);
                    if (ch == '\'') {
                        appender.append('\'');
                        appender.append('\'');
                    } else if (ch == '\\') {
                        appender.append('\\');
                        appender.append('\\');
                    } else if (ch == '\0') {
                        appender.append('\\');
                        appender.append('0');
                    } else {
                        appender.append(ch);
                    }
                }
            } else {
                appender.append(text);
            }

            appender.append('\'');
        return false;
        } catch (IOException e) {
            throw new RuntimeException("println error", e);
        }
    }

    public boolean visit(SQLVariantRefExpr x) {
        {
            int index = x.getIndex();

            if (inputParameters != null && index < inputParameters.size()) {

                return super.visit(x);
            }
        }

        if (x.isGlobal()) {
            print0("@@global.");
        }else if(x.isSession()){
            print0("@@session.");
        }

        String varName = x.getName();
        for (int i = 0; i < varName.length(); ++i) {
            char ch = varName.charAt(i);
            if (ch == '\'') {
                if (varName.startsWith("@@") && i == 2) {
                    print(ch);
                } else if (varName.startsWith("@") && i == 1) {
                    print(ch);
                } else if (i != 0 && i != varName.length() - 1) {
                    print0("\\'");
                } else {
                    print(ch);
                }
            } else {
                print(ch);
            }
        }

        String collate = (String) x.getAttribute("COLLATE");
        if (collate != null) {
            print0(ucase ? " COLLATE " : " collate ");
            print0(collate);
        }

        return false;
    }

    @Override
    public boolean visit(XuGuExtractExpr x) {
        print0(ucase ? "EXTRACT(" : "extract(");
        print0(x.getUnit().name());
        print0(ucase ? " FROM " : " from ");
        x.getValue().accept(this);
        print(')');
        return false;
    }

    @Override
    public void endVisit(XuGuExtractExpr x) {

    }

    @Override
    public void endVisit(XuGuMatchAgainstExpr x) {

    }

    @Override
    public boolean visit(XuGuMatchAgainstExpr x) {
        print0(ucase ? "MATCH (" : "match (");
        printAndAccept(x.getColumns(), ", ");
        print(')');

        print0(ucase ? " AGAINST (" : " against (");
        x.getAgainst().accept(this);
        if (x.getSearchModifier() != null) {
            print(' ');
            print0(ucase ? x.getSearchModifier().name : x.getSearchModifier().name_lcase);
        }
        print(')');

        return false;
    }

    @Override
    public void endVisit(XuGuPrepareStatement x) {
    }

    @Override
    public boolean visit(XuGuPrepareStatement x) {
        print0(ucase ? "PREPARE " : "prepare ");
        x.getName().accept(this);
        print0(ucase ? " FROM " : " from ");
        x.getFrom().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuExecuteStatement x) {

    }

    @Override
    public boolean visit(XuGuExecuteStatement x) {
        print0(ucase ? "EXECUTE " : "execute ");
        x.getStatementName().accept(this);
        if (x.getParameters().size() > 0) {
            print0(ucase ? " USING " : " using ");
            ;
            printAndAccept(x.getParameters(), ", ");
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDeallocatePrepareStatement x) {

    }

    public boolean visit(XuGuDeallocatePrepareStatement x) {
        print0(ucase ? "DEALLOCATE PREPARE " : "deallocate prepare ");
        x.getStatementName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuDeleteStatement x) {

    }

    @Override
    public boolean visit(XuGuDeleteStatement x) {
        print0(ucase ? "DELETE " : "delete ");

        for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
            SQLCommentHint hint = x.getHints().get(i);
            hint.accept(this);
            print(' ');
        }

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isQuick()) {
            print0(ucase ? "QUICK " : "quick ");
        }

        if (x.isIgnore()) {
            print0(ucase ? "IGNORE " : "ignore ");
        }

        if (x.isForceAllPartitions()) {
            print0(ucase ? "FORCE ALL PARTITIONS " : "force all partitions ");
        } else {
            SQLName partition = x.getForcePartition();
            if (partition != null) {
                print0(ucase ? "FORCE PARTITION " : "force partition ");
                printExpr(partition);
                print(' ');
            }
        }

        SQLTableSource from = x.getFrom();
        if (from == null) {
            print0(ucase ? "FROM " : "from ");
            x.getTableSource().accept(this);
        } else {
            x.getTableSource().accept(this);
            println();
            print0(ucase ? "FROM " : "from ");
            from.accept(this);
        }

        SQLTableSource using = x.getUsing();
        if (using != null) {
            println();
            print0(ucase ? "USING " : "using ");
            using.accept(this);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            this.indentCount++;
            print0(ucase ? "WHERE " : "where ");
            printExpr(where);
            this.indentCount--;
        }


        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            println();
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuInsertStatement x) {

    }

    @Override
    public boolean visit(XuGuInsertStatement x) {
        print0(ucase ? "INSERT " : "insert ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isDelayed()) {
            print0(ucase ? "DELAYED " : "delayed ");
        }

        if (x.isHighPriority()) {
            print0(ucase ? "HIGH_PRIORITY " : "high_priority ");
        }

        if (x.isIgnore()) {
            print0(ucase ? "IGNORE " : "ignore ");
        }

        if (x.isRollbackOnFail()) {
            print0(ucase ? "ROLLBACK_ON_FAIL " : "rollback_on_fail ");
        }

        print0(ucase ? "INTO " : "into ");

        SQLExprTableSource tableSource = x.getTableSource();
        if (tableSource.getClass() == SQLExprTableSource.class) {
            visit(tableSource);
        } else {
            tableSource.accept(this);
        }

        String columnsString = x.getColumnsString();
        if (columnsString != null) {
            if (!isEnabled(VisitorFeature.OutputSkipInsertColumnsString)) {
                print0(columnsString);
            }
        } else {
            List<SQLExpr> columns = x.getColumns();
            if (columns.size() > 0) {
                this.indentCount++;
                print0(" (");
                for (int i = 0, size = columns.size(); i < size; ++i) {
                    if (i != 0) {
                        if (i % 5 == 0) {
                            println();
                        }
                        print0(", ");
                    }

                    SQLExpr column = columns.get(i);
                    if (column instanceof SQLIdentifierExpr) {
                        print0(((SQLIdentifierExpr) column).getName());
                    } else {
                        printExpr(column);
                    }
                }
                print(')');
                this.indentCount--;
            }
        }

        List<SQLInsertStatement.ValuesClause>  valuesList = x.getValuesList();
        if (!valuesList.isEmpty()) {
            println();
            printValuesList(valuesList);
        }

        if (x.getQuery() != null) {
            println();
            x.getQuery().accept(this);
        }

        if (x.isDefaultValues()) {
            print0(ucase ? " DEFAULT VALUES" : " default values");
        }

        List<SQLExpr> duplicateKeyUpdate = x.getDuplicateKeyUpdate();
        if (duplicateKeyUpdate.size() != 0) {
            println();
            print0(ucase ? "ON DUPLICATE KEY UPDATE " : "on duplicate key update ");
            for (int i = 0, size = duplicateKeyUpdate.size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print0(", ");
                }
                duplicateKeyUpdate.get(i).accept(this);
            }
        }

        if (x.getReturning() != null) {
            println();
            x.getReturning().accept(this);
        }

        return false;
    }

    protected void printValuesList(List<SQLInsertStatement.ValuesClause> valuesList) {

        if (this.parameterized && valuesList.size() > 0) {
            print0(ucase ? "VALUES " : "values ");
            this.indentCount++;
            visit(valuesList.get(0));
            this.indentCount--;
            if (valuesList.size() > 1) {
                this.incrementReplaceCunt();
            }
            return;
        }

        print0(ucase ? "VALUES " : "values ");
        if (valuesList.size() > 1) {
            this.indentCount++;
        }
        for (int i = 0, size = valuesList.size(); i < size; ++i) {
            if (i != 0) {
                print(',');
                println();
            }

            SQLInsertStatement.ValuesClause item = valuesList.get(i);
            visit(item);
        }
        if (valuesList.size() > 1) {
            this.indentCount--;
        }
    }

    @Override
    public void endVisit(XuGuLoadDataInFileStatement x) {

    }

    @Override
    public boolean visit(XuGuLoadDataInFileStatement x) {
        print0(ucase ? "LOAD DATA " : "load data ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isConcurrent()) {
            print0(ucase ? "CONCURRENT " : "concurrent ");
        }

        if (x.isLocal()) {
            print0(ucase ? "LOCAL " : "local ");
        }

        print0(ucase ? "INFILE " : "infile ");

        x.getFileName().accept(this);

        if (x.isReplicate()) {
            print0(ucase ? " REPLACE " : " replace ");
        }

        if (x.isIgnore()) {
            print0(ucase ? " IGNORE " : " ignore ");
        }

        print0(ucase ? " INTO TABLE " : " into table ");
        x.getTableName().accept(this);

        if (x.getColumnsTerminatedBy() != null || x.getColumnsEnclosedBy() != null || x.getColumnsEscaped() != null) {
            print0(ucase ? " COLUMNS" : " columns");
            if (x.getColumnsTerminatedBy() != null) {
                print0(ucase ? " TERMINATED BY " : " terminated by ");
                x.getColumnsTerminatedBy().accept(this);
            }

            if (x.getColumnsEnclosedBy() != null) {
                if (x.isColumnsEnclosedOptionally()) {
                    print0(ucase ? " OPTIONALLY" : " optionally");
                }
                print0(ucase ? " ENCLOSED BY " : " enclosed by ");
                x.getColumnsEnclosedBy().accept(this);
            }

            if (x.getColumnsEscaped() != null) {
                print0(ucase ? " ESCAPED BY " : " escaped by ");
                x.getColumnsEscaped().accept(this);
            }
        }

        if (x.getLinesStartingBy() != null || x.getLinesTerminatedBy() != null) {
            print0(ucase ? " LINES" : " lines");
            if (x.getLinesStartingBy() != null) {
                print0(ucase ? " STARTING BY " : " starting by ");
                x.getLinesStartingBy().accept(this);
            }

            if (x.getLinesTerminatedBy() != null) {
                print0(ucase ? " TERMINATED BY " : " terminated by ");
                x.getLinesTerminatedBy().accept(this);
            }
        }

        if (x.getIgnoreLinesNumber() != null) {
            print0(ucase ? " IGNORE " : " ignore ");
            x.getIgnoreLinesNumber().accept(this);
            print0(ucase ? " LINES" : " lines");
        }

        if (x.getColumns().size() != 0) {
            print0(" (");
            printAndAccept(x.getColumns(), ", ");
            print(')');
        }

        if (x.getSetList().size() != 0) {
            print0(ucase ? " SET " : " set ");
            printAndAccept(x.getSetList(), ", ");
        }

        return false;
    }

    public boolean visit(SQLReplaceStatement x) {
        print0(ucase ? "REPLACE " : "replace ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isDelayed()) {
            print0(ucase ? "DELAYED " : "delayed ");
        }

        print0(ucase ? "INTO " : "into ");

        printTableSourceExpr(x.getTableName());

        List<SQLExpr> columns = x.getColumns();
        if (columns.size() > 0) {
            print0(" (");
            for (int i = 0, size = columns.size(); i < size; ++i) {
                if (i != 0) {
                    print0(", ");
                }

                SQLExpr columnn = columns.get(i);
                printExpr(columnn);
            }
            print(')');
        }

        List<SQLInsertStatement.ValuesClause> valuesClauseList = x.getValuesList();
        if (valuesClauseList.size() != 0) {
            println();
            print0(ucase ? "VALUES " : "values ");
            int size = valuesClauseList.size();
            if (size == 0) {
                print0("()");
            } else {
                for (int i = 0; i < size; ++i) {
                    if (i != 0) {
                        print0(", ");
                    }
                    visit(valuesClauseList.get(i));
                }
            }
        }

        SQLQueryExpr query = x.getQuery();
        if (query != null) {
            visit(query);
        }

        return false;
    }

    @Override
    public boolean visit(SQLStartTransactionStatement x) {
        print0(ucase ? "START TRANSACTION" : "start transaction");
        if (x.isConsistentSnapshot()) {
            print0(ucase ? " WITH CONSISTENT SNAPSHOT" : " with consistent snapshot");
        }

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }

        if (x.isBegin()) {
            print0(ucase ? " BEGIN" : " begin");
        }

        if (x.isWork()) {
            print0(ucase ? " WORK" : " work");
        }

        return false;
    }

    public boolean visit(SQLRollbackStatement x) {
        print0(ucase ? "ROLLBACK" : "rollback");

        if (x.getChain() != null) {
            if (x.getChain().booleanValue()) {
                print0(ucase ? " AND CHAIN" : " and chain");
            } else {
                print0(ucase ? " AND NO CHAIN" : " and no chain");
            }
        }

        if (x.getRelease() != null) {
            if (x.getRelease().booleanValue()) {
                print0(ucase ? " AND RELEASE" : " and release");
            } else {
                print0(ucase ? " AND NO RELEASE" : " and no release");
            }
        }

        if (x.getTo() != null) {
            print0(ucase ? " TO " : " to ");
            x.getTo().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowColumnsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowColumnsStatement x) {
        if (x.isFull()) {
            print0(ucase ? "SHOW FULL COLUMNS" : "show full columns");
        } else {
            print0(ucase ? "SHOW COLUMNS" : "show columns");
        }

        if (x.getTable() != null) {
            print0(ucase ? " FROM " : " from ");
            if (x.getDatabase() != null) {
                x.getDatabase().accept(this);
                print('.');
            }
            x.getTable().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLShowTablesStatement x) {
        if (x.isFull()) {
            print0(ucase ? "SHOW FULL TABLES" : "show full tables");
        } else {
            print0(ucase ? "SHOW TABLES" : "show tables");
        }

        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowDatabasesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowDatabasesStatement x) {
        print0(ucase ? "SHOW DATABASES" : "show databases");

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowWarningsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowWarningsStatement x) {
        if (x.isCount()) {
            print0(ucase ? "SHOW COUNT(*) WARNINGS" : "show count(*) warnings");
        } else {
            print0(ucase ? "SHOW WARNINGS" : "show warnings");
            if (x.getLimit() != null) {
                print(' ');
                x.getLimit().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowStatusStatement x) {
        print0(ucase ? "SHOW " : "show ");

        if (x.isGlobal()) {
            print0(ucase ? "GLOBAL " : "global ");
        }

        if (x.isSession()) {
            print0(ucase ? "SESSION " : "session ");
        }

        print0(ucase ? "STATUS" : "status");

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuLoadXmlStatement x) {

    }

    @Override
    public boolean visit(XuGuLoadXmlStatement x) {
        print0(ucase ? "LOAD XML " : "load xml ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isConcurrent()) {
            print0(ucase ? "CONCURRENT " : "concurrent ");
        }

        if (x.isLocal()) {
            print0(ucase ? "LOCAL " : "local ");
        }

        print0(ucase ? "INFILE " : "infile ");

        x.getFileName().accept(this);

        if (x.isReplicate()) {
            print0(ucase ? " REPLACE " : " replace ");
        }

        if (x.isIgnore()) {
            print0(ucase ? " IGNORE " : " ignore ");
        }

        print0(ucase ? " INTO TABLE " : " into table ");
        x.getTableName().accept(this);

        if (x.getCharset() != null) {
            print0(ucase ? " CHARSET " : " charset ");
            print0(x.getCharset());
        }

        if (x.getRowsIdentifiedBy() != null) {
            print0(ucase ? " ROWS IDENTIFIED BY " : " rows identified by ");
            x.getRowsIdentifiedBy().accept(this);
        }

        if (x.getSetList().size() != 0) {
            print0(ucase ? " SET " : " set ");
            printAndAccept(x.getSetList(), ", ");
        }

        return false;
    }

    @Override
    public void endVisit(CobarShowStatus x) {

    }

    @Override
    public boolean visit(CobarShowStatus x) {
        print0(ucase ? "SHOW COBAR_STATUS" : "show cobar_status");
        return false;
    }

    @Override
    public void endVisit(XuGuKillStatement x) {

    }

    @Override
    public boolean visit(XuGuKillStatement x) {
        if (XuGuKillStatement.Type.CONNECTION.equals(x.getType())) {
            print0(ucase ? "KILL CONNECTION " : "kill connection ");
        } else if (XuGuKillStatement.Type.QUERY.equals(x.getType())) {
            print0(ucase ? "KILL QUERY " : "kill query ");
        } else {
            print0(ucase ? "KILL " : "kill ");
        }

        printAndAccept(x.getThreadIds(), ", ");
        return false;
    }

    @Override
    public void endVisit(XuGuBinlogStatement x) {

    }

    @Override
    public boolean visit(XuGuBinlogStatement x) {
        print0(ucase ? "BINLOG " : "binlog ");
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuResetStatement x) {

    }

    @Override
    public boolean visit(XuGuResetStatement x) {
        print0(ucase ? "RESET " : "reset ");
        for (int i = 0; i < x.getOptions().size(); ++i) {
            if (i != 0) {
                print0(", ");
            }
            print0(x.getOptions().get(i));
        }
        return false;
    }

    @Override
    public void endVisit(XuGuCreateUserStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateUserStatement x) {
        print0(ucase ? "CREATE USER " : "create user ");
        printAndAccept(x.getUsers(), ", ");
        return false;
    }

    @Override
    public void endVisit(UserSpecification x) {

    }

    @Override
    public boolean visit(UserSpecification x) {
        x.getUser().accept(this);

        if (x.getPassword() != null) {
            print0(ucase ? " IDENTIFIED BY " : " identified by ");
            if (x.isPasswordHash()) {
                print0(ucase ? "PASSWORD " : "password ");
            }
            x.getPassword().accept(this);
        }

        if (x.getAuthPlugin() != null) {
            print0(ucase ? " IDENTIFIED WITH " : " identified with ");
            x.getAuthPlugin().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuPartitionByKey x) {

    }

    @Override
    public boolean visit(XuGuPartitionByKey x) {
        if (x.isLinear()) {
            print0(ucase ? "PARTITION BY LINEAR KEY (" : "partition by linear key (");
        } else {
            print0(ucase ? "PARTITION BY KEY (" : "partition by key (");
        }
        printAndAccept(x.getColumns(), ", ");
        print(')');

        printPartitionsCountAndSubPartitions(x);
        return false;
    }


    //

    @Override
    public void endVisit(XuGuSelectQueryBlock x) {

    }

    @Override
    public boolean visit(XuGuOutFileExpr x) {
        print0(ucase ? "OUTFILE " : "outfile ");
        x.getFile().accept(this);

        if (x.getCharset() != null) {
            print0(ucase ? " CHARACTER SET " : " character set ");
            print0(x.getCharset());
        }

        if (x.getColumnsTerminatedBy() != null || x.getColumnsEnclosedBy() != null || x.getColumnsEscaped() != null) {
            print0(ucase ? " COLUMNS" : " columns");
            if (x.getColumnsTerminatedBy() != null) {
                print0(ucase ? " TERMINATED BY " : " terminated by ");
                x.getColumnsTerminatedBy().accept(this);
            }

            if (x.getColumnsEnclosedBy() != null) {
                if (x.isColumnsEnclosedOptionally()) {
                    print0(ucase ? " OPTIONALLY" : " optionally");
                }
                print0(ucase ? " ENCLOSED BY " : " enclosed by ");
                x.getColumnsEnclosedBy().accept(this);
            }

            if (x.getColumnsEscaped() != null) {
                print0(ucase ? " ESCAPED BY " : " escaped by ");
                x.getColumnsEscaped().accept(this);
            }
        }

        if (x.getLinesStartingBy() != null || x.getLinesTerminatedBy() != null) {
            print0(ucase ? " LINES" : " lines");
            if (x.getLinesStartingBy() != null) {
                print0(ucase ? " STARTING BY " : " starting by ");
                x.getLinesStartingBy().accept(this);
            }

            if (x.getLinesTerminatedBy() != null) {
                print0(ucase ? " TERMINATED BY " : " terminated by ");
                x.getLinesTerminatedBy().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(XuGuOutFileExpr x) {

    }

    @Override
    public boolean visit(XuGuExplainStatement x) {
        String name = x.isDescribe() ? "desc" : "explain";
        print0(ucase ? name.toUpperCase() : name);
        print(' ');

        // tbl_name [col_name | wild]
        if (x.getTableName() != null) {
            x.getTableName().accept(this);
            if (x.getColumnName() != null) {
                print(' ');
                x.getColumnName().accept(this);
            } else if (x.getWild() != null) {
                print(' ');
                x.getWild().accept(this);
            }
        } else {
            // [explain_type]
            String type = x.getType();
            if (type != null) {
                print0(type);
                print(' ');
                if ("format".equalsIgnoreCase(type)) {
                    print0("= ");
                    print0(x.getFormat());
                    print(' ');
                }
            }

            // {explainable_stmt | FOR CONNECTION connection_id}
            if (x.getConnectionId() != null) {
                print0(ucase ? "FOR CONNECTION " : "for connection ");
                x.getConnectionId().accept(this);
            } else {
                x.getStatement().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(XuGuExplainStatement x) {

    }

    @Override
    public boolean visit(XuGuUpdateStatement x) {
        List<SQLExpr> returning = x.getReturning();
        if (returning != null && returning.size() > 0) {
            print0(ucase ? "SELECT " : "select ");
            printAndAccept(returning, ", ");
            println();
            print0(ucase ? "FROM " : "from ");
        }

        print0(ucase ? "UPDATE " : "update ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isIgnore()) {
            print0(ucase ? "IGNORE " : "ignore ");
        }


        if (x.getHints() != null && x.getHints().size() > 0) {
            printAndAccept(x.getHints(), " ");
            print0(" ");
        }

        if (x.isCommitOnSuccess()) {
            print0(ucase ? "COMMIT_ON_SUCCESS " : "commit_on_success ");
        }

        if (x.isRollBackOnFail()) {
            print0(ucase ? "ROLLBACK_ON_FAIL " : "rollback_on_fail ");
        }

        if (x.isQueryOnPk()) {
            print0(ucase ? "QUEUE_ON_PK " : "queue_on_pk ");
        }

        SQLExpr targetAffectRow = x.getTargetAffectRow();
        if (targetAffectRow != null) {
            print0(ucase ? "TARGET_AFFECT_ROW " : "target_affect_row ");
            printExpr(targetAffectRow);
            print(' ');
        }

        if (x.isForceAllPartitions()) {
            print0(ucase ? "FORCE ALL PARTITIONS " : "force all partitions ");
        } else {
            SQLName partition = x.getForcePartition();
            if (partition != null) {
                print0(ucase ? "FORCE PARTITION " : "force partition ");
                printExpr(partition);
                print(' ');
            }
        }

        printTableSource(x.getTableSource());

        println();
        print0(ucase ? "SET " : "set ");
        List<SQLUpdateSetItem> items = x.getItems();
        for (int i = 0, size = items.size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            SQLUpdateSetItem item = items.get(i);
            visit(item);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            indentCount++;
            print0(ucase ? "WHERE " : "where ");
            printExpr(where);
            indentCount--;
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            visit(orderBy);
        }

        SQLLimit limit = x.getLimit();
        if (limit != null) {
            println();
            visit(limit);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuUpdateStatement x) {

    }

    @Override
    public boolean visit(XuGuSetTransactionStatement x) {
        if (x.getGlobal() == null) {
            print0(ucase ? "SET TRANSACTION " : "set transaction ");
        } else if (x.getGlobal().booleanValue()) {
            print0(ucase ? "SET GLOBAL TRANSACTION " : "set global transaction ");
        } else {
            print0(ucase ? "SET SESSION TRANSACTION " : "set session transaction ");
        }

        if (x.getIsolationLevel() != null) {
            print0(ucase ? "ISOLATION LEVEL " : "isolation level ");
            print0(x.getIsolationLevel());
        }

        if (x.getAccessModel() != null) {
            print0(ucase ? "READ " : "read ");
            print0(x.getAccessModel());
        }

        return false;
    }

    @Override
    public void endVisit(XuGuSetTransactionStatement x) {

    }
//
//    @Override
//    public boolean visit(MySqlSetNamesStatement x) {
//        print0(ucase ? "SET NAMES " : "set names ");
//        if (x.isDefault()) {
//            print0(ucase ? "DEFAULT" : "default");
//        } else {
//            print0(x.getCharSet());
//            if (x.getCollate() != null) {
//                print0(ucase ? " COLLATE " : " collate ");
//                print0(x.getCollate());
//            }
//        }
//        return false;
//    }

//    public boolean visit(MySqlSetCharSetStatement x) {
//        print0(ucase ? "SET CHARACTER SET " : "set character set ");
//        if (x.isDefault()) {
//            print0(ucase ? "DEFAULT" : "default");
//        } else {
//            print0(x.getCharSet());
//            if (x.getCollate() != null) {
//                print0(ucase ? " COLLATE " : " collate ");
//                print0(x.getCollate());
//            }
//        }
//        return false;
//    }

    @Override
    public void endVisit(XuGuShowAuthorsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowAuthorsStatement x) {
        print0(ucase ? "SHOW AUTHORS" : "show authors");
        return false;
    }

    @Override
    public void endVisit(XuGuShowBinaryLogsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowBinaryLogsStatement x) {
        print0(ucase ? "SHOW BINARY LOGS" : "show binary logs");
        return false;
    }

    @Override
    public boolean visit(XuGuShowMasterLogsStatement x) {
        print0(ucase ? "SHOW MASTER LOGS" : "show master logs");
        return false;
    }

    @Override
    public void endVisit(XuGuShowMasterLogsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCollationStatement x) {
        print0(ucase ? "SHOW COLLATION" : "show collation");
        if (x.getPattern() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getPattern().accept(this);
        }
        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowCollationStatement x) {

    }

    @Override
    public boolean visit(XuGuShowBinLogEventsStatement x) {
        print0(ucase ? "SHOW BINLOG EVENTS" : "show binlog events");
        if (x.getIn() != null) {
            print0(ucase ? " IN " : " in ");
            x.getIn().accept(this);
        }
        if (x.getFrom() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getFrom().accept(this);
        }
        if (x.getLimit() != null) {
            print(' ');
            x.getLimit().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowBinLogEventsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCharacterSetStatement x) {
        print0(ucase ? "SHOW CHARACTER SET" : "show character set");
        if (x.getPattern() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getPattern().accept(this);
        }
        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowCharacterSetStatement x) {

    }

    @Override
    public boolean visit(XuGuShowContributorsStatement x) {
        print0(ucase ? "SHOW CONTRIBUTORS" : "show contributors");
        return false;
    }

    @Override
    public void endVisit(XuGuShowContributorsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateDatabaseStatement x) {
        print0(ucase ? "SHOW CREATE DATABASE " : "show create database ");
        x.getDatabase().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowCreateDatabaseStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateEventStatement x) {
        print0(ucase ? "SHOW CREATE EVENT " : "show create event ");
        x.getEventName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowCreateEventStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateFunctionStatement x) {
        print0(ucase ? "SHOW CREATE FUNCTION " : "show create function ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateProcedureStatement x) {
        print0(ucase ? "SHOW CREATE PROCEDURE " : "show create procedure ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateTableStatement x) {
        print0(ucase ? "SHOW CREATE TABLE " : "show create table ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowCreateTableStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateTriggerStatement x) {
        print0(ucase ? "SHOW CREATE TRIGGER " : "show create trigger ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowCreateTriggerStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateViewStatement x) {
        print0(ucase ? "SHOW CREATE VIEW " : "show create view ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowCreateViewStatement x) {

    }

    @Override
    public boolean visit(XuGuShowEngineStatement x) {
        print0(ucase ? "SHOW ENGINE " : "show engine ");
        x.getName().accept(this);
        print(' ');
        print0(x.getOption().name());
        return false;
    }

    @Override
    public void endVisit(XuGuShowEngineStatement x) {

    }

    @Override
    public boolean visit(XuGuShowEventsStatement x) {
        print0(ucase ? "SHOW EVENTS" : "show events");
        if (x.getSchema() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getSchema().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowEventsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowFunctionCodeStatement x) {
        print0(ucase ? "SHOW FUNCTION CODE " : "show function code ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowFunctionCodeStatement x) {

    }

    @Override
    public boolean visit(XuGuShowFunctionStatusStatement x) {
        print0(ucase ? "SHOW FUNCTION STATUS" : "show function status");
        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowFunctionStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowEnginesStatement x) {
        if (x.isStorage()) {
            print0(ucase ? "SHOW STORAGE ENGINES" : "show storage engines");
        } else {
            print0(ucase ? "SHOW ENGINES" : "show engines");
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowEnginesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowErrorsStatement x) {
        if (x.isCount()) {
            print0(ucase ? "SHOW COUNT(*) ERRORS" : "show count(*) errors");
        } else {
            print0(ucase ? "SHOW ERRORS" : "show errors");
            if (x.getLimit() != null) {
                print(' ');
                x.getLimit().accept(this);
            }
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowErrorsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowGrantsStatement x) {
        print0(ucase ? "SHOW GRANTS" : "show grants");
        if (x.getUser() != null) {
            print0(ucase ? " FOR " : " for ");
            x.getUser().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowGrantsStatement x) {

    }

    @Override
    public boolean visit(XuGuUserName x) {
        print0(x.getUserName());

        String host = x.getHost();
        if (host != null) {
            print('@');
            print0(host);
        }

        String identifiedBy = x.getIdentifiedBy();
        if (identifiedBy != null) {
            print0(ucase ? " IDENTIFIED BY '" : " identified by '");
            print0(identifiedBy);
            print('\'');
        }

        return false;
    }

    @Override
    public void endVisit(XuGuUserName x) {

    }

    @Override
    public boolean visit(XuGuShowIndexesStatement x) {
        print0(ucase ? "SHOW INDEX" : "show index");

        if (x.getTable() != null) {
            print0(ucase ? " FROM " : " from ");
            if (x.getDatabase() != null) {
                x.getDatabase().accept(this);
                print('.');
            }
            x.getTable().accept(this);
        }

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowIndexesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowKeysStatement x) {
        print0(ucase ? "SHOW KEYS" : "show keys");

        if (x.getTable() != null) {
            print0(ucase ? " FROM " : " from ");
            if (x.getDatabase() != null) {
                x.getDatabase().accept(this);
                print('.');
            }
            x.getTable().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowKeysStatement x) {

    }

    @Override
    public boolean visit(XuGuShowMasterStatusStatement x) {
        print0(ucase ? "SHOW MASTER STATUS" : "show master status");
        return false;
    }

    @Override
    public void endVisit(XuGuShowMasterStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowOpenTablesStatement x) {
        print0(ucase ? "SHOW OPEN TABLES" : "show open tables");

        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowOpenTablesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowPluginsStatement x) {
        print0(ucase ? "SHOW PLUGINS" : "show plugins");
        return false;
    }

    @Override
    public void endVisit(XuGuShowPluginsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowPrivilegesStatement x) {
        print0(ucase ? "SHOW PRIVILEGES" : "show privileges");
        return false;
    }

    @Override
    public void endVisit(XuGuShowPrivilegesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProcedureCodeStatement x) {
        print0(ucase ? "SHOW PROCEDURE CODE " : "show procedure code ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowProcedureCodeStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProcedureStatusStatement x) {
        print0(ucase ? "SHOW PROCEDURE STATUS" : "show procedure status");
        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowProcedureStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProcessListStatement x) {
        if (x.isFull()) {
            print0(ucase ? "SHOW FULL PROCESSLIST" : "show full processlist");
        } else {
            print0(ucase ? "SHOW PROCESSLIST" : "show processlist");
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowProcessListStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProfileStatement x) {
        print0(ucase ? "SHOW PROFILE" : "show profile");
        for (int i = 0; i < x.getTypes().size(); ++i) {
            if (i == 0) {
                print(' ');
            } else {
                print0(", ");
            }
            print0(x.getTypes().get(i).name);
        }

        if (x.getForQuery() != null) {
            print0(ucase ? " FOR QUERY " : " for query ");
            x.getForQuery().accept(this);
        }

        if (x.getLimit() != null) {
            print(' ');
            x.getLimit().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuShowProfileStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProfilesStatement x) {
        print0(ucase ? "SHOW PROFILES" : "show profiles");
        return false;
    }

    @Override
    public void endVisit(XuGuShowProfilesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowRelayLogEventsStatement x) {
        print0("SHOW RELAYLOG EVENTS");

        if (x.getLogName() != null) {
            print0(ucase ? " IN " : " in ");
            x.getLogName().accept(this);
        }

        if (x.getFrom() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getFrom().accept(this);
        }

        if (x.getLimit() != null) {
            print(' ');
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowRelayLogEventsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowSlaveHostsStatement x) {
        print0(ucase ? "SHOW SLAVE HOSTS" : "show slave hosts");
        return false;
    }

    @Override
    public void endVisit(XuGuShowSlaveHostsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowSlaveStatusStatement x) {
        print0(ucase ? "SHOW SLAVE STATUS" : "show slave status");
        return false;
    }

    @Override
    public void endVisit(XuGuShowSlaveStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowTableStatusStatement x) {
        print0(ucase ? "SHOW TABLE STATUS" : "show table status");
        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowTableStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowTriggersStatement x) {
        print0(ucase ? "SHOW TRIGGERS" : "show triggers");

        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowTriggersStatement x) {

    }

    @Override
    public boolean visit(XuGuShowVariantsStatement x) {
        print0(ucase ? "SHOW " : "show ");

        if (x.isGlobal()) {
            print0(ucase ? "GLOBAL " : "global ");
        }

        if (x.isSession()) {
            print0(ucase ? "SESSION " : "session ");
        }

        print0(ucase ? "VARIABLES" : "variables");

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuShowVariantsStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        if (x.isIgnore()) {
            print0(ucase ? "ALTER IGNORE TABLE " : "alter ignore table ");
        } else {
            print0(ucase ? "ALTER TABLE " : "alter table ");
        }
        printTableSourceExpr(x.getName());
        this.indentCount++;
        for (int i = 0; i < x.getItems().size(); ++i) {
            SQLAlterTableItem item = x.getItems().get(i);
            if (i != 0) {
                print(',');
            }
            println();
            item.accept(this);
        }

        if (x.isRemovePatiting()) {
            println();
            print0(ucase ? "REMOVE PARTITIONING" : "remove partitioning");
        }

        if (x.isUpgradePatiting()) {
            println();
            print0(ucase ? "UPGRADE PARTITIONING" : "upgrade partitioning");
        }

        if (x.getTableOptions().size() > 0) {
            println();
        }

        this.indentCount--;

        int i = 0;
        for (Map.Entry<String, SQLObject> option : x.getTableOptions().entrySet()) {
            String key = option.getKey();
            if (i != 0) {
                print(' ');
            }
            print0(ucase ? key : key.toLowerCase());

            if ("TABLESPACE".equals(key)) {
                print(' ');
                option.getValue().accept(this);
                continue;
            } else if ("UNION".equals(key)) {
                print0(" = (");
                option.getValue().accept(this);
                print(')');
                continue;
            }

            print0(" = ");

            option.getValue().accept(this);
            i++;
        }

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        print0(ucase ? "ADD COLUMN " : "add column ");

        if (x.getColumns().size() > 1) {
            print('(');
        }
        printAndAccept(x.getColumns(), ", ");
        if (x.getFirstColumn() != null) {
            print0(ucase ? " FIRST " : " first ");
            x.getFirstColumn().accept(this);
        } else if (x.getAfterColumn() != null) {
            print0(ucase ? " AFTER " : " after ");
            x.getAfterColumn().accept(this);
        } else if (x.isFirst()) {
            print0(ucase ? " FIRST" : " first");
        }

        if (x.getColumns().size() > 1) {
            print(')');
        }
        return false;
    }

    @Override
    public boolean visit(XuGuRenameTableStatement.Item x) {
        x.getName().accept(this);
        print0(ucase ? " TO " : " to ");
        x.getTo().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuRenameTableStatement.Item x) {

    }

    @Override
    public boolean visit(XuGuRenameTableStatement x) {
        print0(ucase ? "RENAME TABLE " : "rename table ");
        printAndAccept(x.getItems(), ", ");
        return false;
    }

    @Override
    public void endVisit(XuGuRenameTableStatement x) {

    }

    @Override
    public boolean visit(XuGuUseIndexHint x) {
        print0(ucase ? "USE INDEX " : "use index ");
        if (x.getOption() != null) {
            print0(ucase ? "FOR " : "for ");
            print0(x.getOption().name);
            print(' ');
        }
        print('(');
        printAndAccept(x.getIndexList(), ", ");
        print(')');
        return false;
    }

    @Override
    public void endVisit(XuGuUseIndexHint x) {

    }

    @Override
    public boolean visit(XuGuIgnoreIndexHint x) {
        print0(ucase ? "IGNORE INDEX " : "ignore index ");
        if (x.getOption() != null) {
            print0(ucase ? "FOR " : "for ");
            print0(ucase ? x.getOption().name : x.getOption().name_lcase);
            print(' ');
        }
        print('(');
        printAndAccept(x.getIndexList(), ", ");
        print(')');
        return false;
    }

    @Override
    public void endVisit(XuGuIgnoreIndexHint x) {

    }

    public boolean visit(SQLExprTableSource x) {
        printTableSourceExpr(x.getExpr());

        String alias = x.getAlias();
        if (alias != null) {
            print(' ');
            print0(alias);
        }

        for (int i = 0; i < x.getHintsSize(); ++i) {
            print(' ');
            x.getHints().get(i).accept(this);
        }

        if (x.getPartitionSize() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printlnAndAccept(x.getPartitions(), ", ");
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(XuGuLockTableStatement x) {
        print0(ucase ? "LOCK TABLES " : "lock tables ");
        x.getTableSource().accept(this);
        if (x.getLockType() != null) {
            print(' ');
            print0(x.getLockType().name);
        }

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }
        return false;
    }

    @Override
    public void endVisit(XuGuLockTableStatement x) {

    }

    @Override
    public boolean visit(XuGuUnlockTablesStatement x) {
        print0(ucase ? "UNLOCK TABLES" : "unlock tables");
        return false;
    }

    @Override
    public void endVisit(XuGuUnlockTablesStatement x) {

    }

    @Override
    public boolean visit(XuGuForceIndexHint x) {
        print0(ucase ? "FORCE INDEX " : "force index ");
        if (x.getOption() != null) {
            print0(ucase ? "FOR " : "for ");
            print0(x.getOption().name);
            print(' ');
        }
        print('(');
        printAndAccept(x.getIndexList(), ", ");
        print(')');
        return false;
    }

    @Override
    public void endVisit(XuGuForceIndexHint x) {

    }

    @Override
    public boolean visit(XuGuAlterTableChangeColumn x) {
        print0(ucase ? "CHANGE COLUMN " : "change column ");
        x.getColumnName().accept(this);
        print(' ');
        x.getNewColumnDefinition().accept(this);
        if (x.getFirstColumn() != null) {
            print0(ucase ? " FIRST " : " first ");
            x.getFirstColumn().accept(this);
        } else if (x.getAfterColumn() != null) {
            print0(ucase ? " AFTER " : " after ");
            x.getAfterColumn().accept(this);
        } else if (x.isFirst()) {
            print0(ucase ? " FIRST" : " first");
        }

        return false;
    }

    @Override
    public void endVisit(XuGuAlterTableChangeColumn x) {

    }

    @Override
    public boolean visit(XuGuAlterTableModifyColumn x) {
        print0(ucase ? "MODIFY COLUMN " : "modify column ");
        x.getNewColumnDefinition().accept(this);
        if (x.getFirstColumn() != null) {
            print0(ucase ? " FIRST " : " first ");
            x.getFirstColumn().accept(this);
        } else if (x.getAfterColumn() != null) {
            print0(ucase ? " AFTER " : " after ");
            x.getAfterColumn().accept(this);
        } else if (x.isFirst()) {
            print0(ucase ? " FIRST" : " first");
        }

        return false;
    }

    @Override
    public void endVisit(XuGuAlterTableModifyColumn x) {

    }

    @Override
    public boolean visit(XuGuAlterTableOption x) {
        print0(x.getName());
        print0(" = ");
        print0(x.getValue().toString());
        return false;
    }

    @Override
    public void endVisit(XuGuAlterTableOption x) {

    }

    @Override
    public void endVisit(XuGuCreateTableStatement x) {

    }

    @Override
    public boolean visit(XuGuHelpStatement x) {
        print0(ucase ? "HELP " : "help ");
        x.getContent().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuHelpStatement x) {

    }

    @Override
    public boolean visit(XuGuCharExpr x) {
        if (this.parameterized) {
            print('?');
            incrementReplaceCunt();
            if (this.parameters != null) {
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return false;
        }

        print0(x.toString());
        return false;
    }

    @Override
    public void endVisit(XuGuCharExpr x) {

    }

    @Override
    public boolean visit(XuGuUnique x) {
        if (x.isHasConstaint()) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        print0(ucase ? "UNIQUE" : "unique");

        SQLName name = x.getName();
        if (name != null) {
            print(' ');
            name.accept(this);
        }

        if (x.getIndexType() != null) {
            print0(ucase ? " USING " : " using ");
            print0(x.getIndexType());
        }

        print0(" (");
        printAndAccept(x.getColumns(), ", ");
        print(')');

        SQLExpr keyBlockSize = x.getKeyBlockSize();
        if (keyBlockSize != null) {
            print0(ucase ? " KEY_BLOCK_SIZE = " : " key_block_size = ");
            keyBlockSize.accept(this);
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(" COMMENT ");
            comment.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(XuGuForeignKey x) {
        if (x.isHasConstraint()) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        print0(ucase ? "FOREIGN KEY" : "foreign key");

        if (x.getIndexName() != null) {
            print(' ');
            x.getIndexName().accept(this);
        }

        print0(" (");
        printAndAccept(x.getReferencingColumns(), ", ");
        print(')');

        print0(ucase ? " REFERENCES " : " references ");
        x.getReferencedTableName().accept(this);

        print0(" (");
        printAndAccept(x.getReferencedColumns(), ", ");
        print(')');

        SQLForeignKeyImpl.Match match = x.getReferenceMatch();
        if (match != null) {
            print0(ucase ? " MATCH " : " match ");
            print0(ucase ? match.name : match.name_lcase);
        }

        if (x.getOnDelete() != null) {
            print0(ucase ? " ON DELETE " : " on delete ");
            print0(ucase ? x.getOnDelete().name : x.getOnDelete().name_lcase);
        }

        if (x.getOnUpdate() != null) {
            print0(ucase ? " ON UPDATE " : " on update ");
            print0(ucase ? x.getOnUpdate().name : x.getOnUpdate().name_lcase);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuUnique x) {

    }

    @Override
    public boolean visit(XuGuAlterTableDiscardTablespace x) {
        print0(ucase ? "DISCARD TABLESPACE" : "discard tablespace");
        return false;
    }

    @Override
    public void endVisit(XuGuAlterTableDiscardTablespace x) {

    }

    @Override
    public boolean visit(XuGuAlterTableImportTablespace x) {
        print0(ucase ? "IMPORT TABLESPACE" : "import tablespace");
        return false;
    }

    @Override
    public void endVisit(XuGuAlterTableImportTablespace x) {

    }

    @Override
    public boolean visit(SQLAssignItem x) {


        String tagetString = x.getTarget().toString();

        boolean xgSpecial = false;

        if (JdbcConstants.XUGU.equals(dbType)) {
            xgSpecial = "NAMES".equalsIgnoreCase(tagetString)
                    || "CHARACTER SET".equalsIgnoreCase(tagetString)
                    || "CHARSET".equalsIgnoreCase(tagetString);
        }

        if (!xgSpecial) {
            x.getTarget().accept(this);
            print0(" = ");
        } else {
            print0(ucase ? tagetString.toUpperCase() : tagetString.toLowerCase());
            print(' ');
        }
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(TableSpaceOption x) {
        x.getName().accept(this);

        if (x.getStorage() != null) {
            print(' ');
            x.getStorage().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(TableSpaceOption x) {

    }

    protected void visitAggreateRest(SQLAggregateExpr aggregateExpr) {
        {
            SQLOrderBy value = (SQLOrderBy) aggregateExpr.getAttribute("ORDER BY");
            if (value != null) {
                print(' ');
                ((SQLObject) value).accept(this);
            }
        }
        {
            Object value = aggregateExpr.getAttribute("SEPARATOR");
            if (value != null) {
                print0(ucase ? " SEPARATOR " : " separator ");
                ((SQLObject) value).accept(this);
            }
        }
    }

    @Override
    public boolean visit(XuGuAnalyzeStatement x) {
        print0(ucase ? "ANALYZE " : "analyze ");
        if (x.isNoWriteToBinlog()) {
            print0(ucase ? "NO_WRITE_TO_BINLOG " : "no_write_to_binlog ");
        }

        if (x.isLocal()) {
            print0(ucase ? "LOCAL " : "local ");
        }

        print0(ucase ? "TABLE " : "table ");

        printAndAccept(x.getTableSources(), ", ");
        return false;
    }

    @Override
    public void endVisit(XuGuAnalyzeStatement x) {

    }

    @Override
    public boolean visit(XuGuOptimizeStatement x) {
        print0(ucase ? "OPTIMIZE " : "optimize ");
        if (x.isNoWriteToBinlog()) {
            print0(ucase ? "NO_WRITE_TO_BINLOG " : "No_write_to_binlog ");
        }

        if (x.isLocal()) {
            print0(ucase ? "LOCAL " : "local ");
        }

        print0(ucase ? "TABLE " : "table ");

        printAndAccept(x.getTableSources(), ", ");
        return false;
    }

    @Override
    public void endVisit(XuGuOptimizeStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterUserStatement x) {
        print0(ucase ? "ALTER USER" : "alter user");
        for (SQLExpr user : x.getUsers()) {
            print(' ');
            user.accept(this);
            print0(ucase ? " PASSWORD EXPIRE" : " password expire");
        }
        return false;
    }

    @Override
    public void endVisit(XuGuAlterUserStatement x) {

    }

    @Override
    public boolean visit(SQLSetStatement x) {
        boolean printSet = x.getAttribute("parser.set") == Boolean.TRUE || !JdbcConstants.ORACLE.equals(dbType);
        if (printSet) {
            print0(ucase ? "SET " : "set ");
        }
        SQLSetStatement.Option option = x.getOption();
        if (option != null) {
            print(option.name());
            print(' ');
        }

        if (option == SQLSetStatement.Option.PASSWORD) {
            print0("FOR ");
        }

        printAndAccept(x.getItems(), ", ");

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }

        return false;
    }

    @Override
    public boolean visit(XuGuHintStatement x) {
        List<SQLCommentHint> hints = x.getHints();

        for (SQLCommentHint hint : hints) {
            hint.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuHintStatement x) {

    }

    @Override
    public boolean visit(XuGuOrderingExpr x) {
        x.getExpr().accept(this);
        if (x.getType() != null) {
            print(' ');
            print0(ucase ? x.getType().name : x.getType().name_lcase);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuOrderingExpr x) {

    }

    @Override
    public boolean visit(SQLBlockStatement x) {
        SQLObject parent = x.getParent();
        String labelName = x.getLabelName();

        if (labelName != null && !labelName.equals("")) {
            print0(labelName);
            print0(": ");
        }

        List<SQLParameter> parameters = x.getParameters();
        if (parameters.size() != 0) {
            this.indentCount++;
            if (parent instanceof SQLCreateProcedureStatement) {
                printIndent();
            }
            if (!(parent instanceof SQLCreateProcedureStatement)) {
                print0(ucase ? "DECLARE" : "declare");
                println();
            }

            for (int i = 0, size = parameters.size(); i < size; ++i) {
                if (i != 0) {
                    println();
                }
                SQLParameter param = parameters.get(i);
                visit(param);
                print(';');
            }

            this.indentCount--;
            println();
        }

        print0(ucase ? "BEGIN" : "begin");
        if (!x.isEndOfCommit()) {
            this.indentCount++;
        } else {
            print(';');
        }
        println();
        List<SQLStatement> statementList = x.getStatementList();
        for (int i = 0, size = statementList.size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement stmt = statementList.get(i);
            stmt.accept(this);
        }

        if (!x.isEndOfCommit()) {
            this.indentCount--;
            println();
            print0(ucase ? "END" : "end");
            if (labelName != null && !labelName.equals("")) {
                print(' ');
                print0(labelName);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLIfStatement x) {
        print0(ucase ? "IF " : "if ");
        x.getCondition().accept(this);
        print0(ucase ? " THEN" : " then");
        this.indentCount++;
        println();
        List<SQLStatement> statements = x.getStatements();
        for (int i = 0, size = statements.size(); i < size; ++i) {
            SQLStatement item = statements.get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        for (SQLIfStatement.ElseIf iterable_element : x.getElseIfList()) {
            iterable_element.accept(this);
        }

        if (x.getElseItem() != null) {
            x.getElseItem().accept(this);
        }

        print0(ucase ? "END IF" : "end if");
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.ElseIf x) {
        print0(ucase ? "ELSE IF " : "else if ");
        x.getCondition().accept(this);
        print0(ucase ? " THEN" : " then");
        this.indentCount++;
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.Else x) {
        print0(ucase ? "ELSE " : "else ");
        this.indentCount++;
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        return false;
    }

    @Override
    public boolean visit(XuGuCaseStatement x) {
        print0(ucase ? "CASE " : "case ");
        x.getCondition().accept(this);
        println();
        for (int i = 0; i < x.getWhenList().size(); i++) {
            x.getWhenList().get(i).accept(this);
        }
        if (x.getElseItem() != null) x.getElseItem().accept(this);
        print0(ucase ? "END CASE" : "end case");
        return false;
    }

    @Override
    public void endVisit(XuGuCaseStatement x) {

    }

    @Override
    public boolean visit(XuGuDeclareStatement x) {
        print0(ucase ? "DECLARE " : "declare ");
        printAndAccept(x.getVarList(), ", ");
        return false;
    }

    @Override
    public void endVisit(XuGuDeclareStatement x) {

    }

    @Override
    public boolean visit(XuGuSelectIntoStatement x) {
        x.getSelect().accept(this);
        print0(ucase ? " INTO " : " into ");
        for (int i = 0; i < x.getVarList().size(); i++) {
            x.getVarList().get(i).accept(this);
            if (i != x.getVarList().size() - 1) print0(", ");
        }
        return false;
    }

    @Override
    public void endVisit(XuGuSelectIntoStatement x) {

    }

    @Override
    public boolean visit(XuGuWhenStatement x) {
        print0(ucase ? "WHEN " : "when ");
        x.getCondition().accept(this);
        print0(" THEN");
        println();
        for (int i = 0; i < x.getStatements().size(); i++) {
            x.getStatements().get(i).accept(this);
            if (i != x.getStatements().size() - 1) {
                println();
            }
        }
        println();
        return false;
    }

    @Override
    public void endVisit(XuGuWhenStatement x) {

    }

    @Override
    public boolean visit(SQLLoopStatement x) {
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(x.getLabelName());
            print0(": ");
        }

        print0(ucase ? "LOOP " : "loop ");
        this.indentCount++;
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        print0(ucase ? "END LOOP" : "end loop");
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(" ");
            print0(x.getLabelName());
        }
        return false;
    }

    @Override
    public boolean visit(XuGuLeaveStatement x) {
        print0(ucase ? "LEAVE " : "leave ");
        print0(x.getLabelName());
        return false;
    }

    @Override
    public void endVisit(XuGuLeaveStatement x) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean visit(XuGuIterateStatement x) {
        print0(ucase ? "ITERATE " : "iterate ");
        print0(x.getLabelName());
        return false;
    }

    @Override
    public void endVisit(XuGuIterateStatement x) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean visit(XuGuRepeatStatement x) {
        // TODO Auto-generated method stub
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(x.getLabelName());
            print0(": ");
        }

        print0(ucase ? "REPEAT " : "repeat ");
        this.indentCount++;
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        print0(ucase ? "UNTIL " : "until ");
        x.getCondition().accept(this);
        println();
        print0(ucase ? "END REPEAT" : "end repeat");
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print(' ');
            print0(x.getLabelName());
        }
        return false;
    }

    @Override
    public void endVisit(XuGuRepeatStatement x) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean visit(XuGuCursorDeclareStatement x) {
        print0(ucase ? "DECLARE " : "declare ");
        printExpr(x.getCursorName());
        print0(ucase ? " CURSOR FOR" : " cursor for");
        this.indentCount++;
        println();
        x.getSelect().accept(this);
        this.indentCount--;
        return false;
    }

    @Override
    public void endVisit(XuGuCursorDeclareStatement x) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean visit(XuGuUpdateTableSource x) {
        XuGuUpdateStatement update = x.getUpdate();
        if (update != null) {
            update.accept0(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuUpdateTableSource x) {

    }

    @Override
    public boolean visit(XuGuAlterTableAlterColumn x) {
        print0(ucase ? "ALTER COLUMN " : "alter column ");
        x.getColumn().accept(this);
        if (x.getDefaultExpr() != null) {
            print0(ucase ? " SET DEFAULT " : " set default ");
            x.getDefaultExpr().accept(this);
        } else if (x.isDropDefault()) {
            print0(ucase ? " DROP DEFAULT" : " drop default");
        }
        return false;
    }

    @Override
    public void endVisit(XuGuAlterTableAlterColumn x) {

    }

    @Override
    public boolean visit(XuGuSubPartitionByKey x) {
        if (x.isLinear()) {
            print0(ucase ? "SUBPARTITION BY LINEAR KEY (" : "subpartition by linear key (");
        } else {
            print0(ucase ? "SUBPARTITION BY KEY (" : "subpartition by key (");
        }
        printAndAccept(x.getColumns(), ", ");
        print(')');

        if (x.getSubPartitionsCount() != null) {
            print0(ucase ? " SUBPARTITIONS " : " subpartitions ");
            x.getSubPartitionsCount().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuSubPartitionByKey x) {

    }

    @Override
    public boolean visit(XuGuSubPartitionByList x) {
        print0(ucase ? "SUBPARTITION BY LIST " : "subpartition by list ");
        if (x.getExpr() != null) {
            print('(');
            x.getExpr().accept(this);
            print0(") ");
        } else {
            if (x.getColumns().size() == 1 && Boolean.TRUE.equals(x.getAttribute("ads.subPartitionList"))) {
                print('(');
            } else {
                print0(ucase ? "COLUMNS (" : "columns (");
            }
            printAndAccept(x.getColumns(), ", ");
            print(")");
        }

        if (x.getOptions().size() != 0) {
            println();
            print0(ucase ? "SUBPARTITION OPTIONS (" : "subpartition options (");
            printAndAccept(x.getOptions(), ", ");
            print(')');
        }

        return false;
    }

    @Override
    public void endVisit(XuGuSubPartitionByList x) {

    }


    @Override
    public boolean visit(XuGuDeclareHandlerStatement x) {
        String handleType = x.getHandleType().name();

        print0(ucase ? "DECLARE " : "declare ");
        print0(ucase ? handleType : handleType.toLowerCase());
        print0(ucase ? " HANDLER FOR " : " handler for ");
        for (int i = 0; i < x.getConditionValues().size(); i++) {
            ConditionValue cv = x.getConditionValues().get(i);
            if (cv.getType() == ConditionType.SQLSTATE) {
                print0(ucase ? " SQLSTATE " : " sqlstate ");
                print0(cv.getValue());
            } else if (cv.getType() == ConditionType.XUGU_ERROR_CODE) {
                print0(cv.getValue());
            } else if (cv.getType() == ConditionType.SELF) {
                print0(cv.getValue());
            } else if (cv.getType() == ConditionType.SYSTEM) {
                print0(ucase ? cv.getValue().toUpperCase() : cv.getValue().toLowerCase());
            }

            if (i != x.getConditionValues().size() - 1) {
                print0(", ");
            }

        }
        this.indentCount++;
        println();
        x.getSpStatement().accept(this);
        this.indentCount--;
        return false;
    }

    @Override
    public void endVisit(XuGuDeclareHandlerStatement x) {

    }

    @Override
    public boolean visit(XuGuDeclareConditionStatement x) {
        print0(ucase ? "DECLARE " : "declare ");
        print0(x.getConditionName());
        print0(ucase ? " CONDITION FOR " : " condition for ");

        if (x.getConditionValue().getType() == ConditionType.SQLSTATE) {
            print0(ucase ? "SQLSTATE " : "sqlstate ");
            print0(x.getConditionValue().getValue());
        } else {
            print0(x.getConditionValue().getValue());
        }

        println();
        return false;
    }

    @Override
    public void endVisit(XuGuDeclareConditionStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropColumnItem x) {

        for (int i = 0; i < x.getColumns().size(); ++i) {
            if (i != 0) {
                print0(", ");
            }

            SQLName columnn = x.getColumns().get(i);

            print0(ucase ? "DROP COLUMN " : "drop column ");
            columnn.accept(this);

            if (x.isCascade()) {
                print0(ucase ? " CASCADE" : " cascade");
            }
        }
        return false;
    }

    /**
     * visit procedure create node
     */
    @Override
    public boolean visit(SQLCreateProcedureStatement x) {
        if (x.isOrReplace()) {
            print0(ucase ? "CREATE OR REPLACE PROCEDURE " : "create or replace procedure ");
        } else {
            print0(ucase ? "CREATE PROCEDURE " : "create procedure ");
        }
        x.getName().accept(this);

        int paramSize = x.getParameters().size();

        print0(" (");
        if (paramSize > 0) {
            this.indentCount++;
            println();

            for (int i = 0; i < paramSize; ++i) {
                if (i != 0) {
                    print0(", ");
                    println();
                }
                SQLParameter param = x.getParameters().get(i);
                param.accept(this);
            }

            this.indentCount--;
            println();
        }
        print(')');

        if (x.isDeterministic()) {
            println();
            print(ucase ? "DETERMINISTIC" : "deterministic");
        }

        if (x.isContainsSql()) {
            println();
            print0(ucase ? "CONTAINS SQL" : "contains sql");
        }

        if (x.isNoSql()) {
            println();
            print(ucase ? "NO SQL" : "no sql");
        }

        if (x.isModifiesSqlData()) {
            println();
            print(ucase ? "MODIFIES SQL DATA" : "modifies sql data");
        }

        SQLName authid = x.getAuthid();
        if (authid != null) {
            println();
            print(ucase ? "SQL SECURITY " : "sql security ");
            authid.accept(this);
        }

        println();
        x.getBlock().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCreateFunctionStatement x) {
        print0(ucase ? "CREATE FUNCTION " : "create function ");
        x.getName().accept(this);

        int paramSize = x.getParameters().size();

        if (paramSize > 0) {
            print0(" (");
            this.indentCount++;
            println();

            for (int i = 0; i < paramSize; ++i) {
                if (i != 0) {
                    print0(", ");
                    println();
                }
                SQLParameter param = x.getParameters().get(i);
                param.accept(this);
            }

            this.indentCount--;
            println();
            print(')');
        }

        println();
        print(ucase ? "RETURNS " : "returns ");
        x.getReturnDataType().accept(this);

        if (x.isDeterministic()) {
            print(ucase ? " DETERMINISTIC" : " deterministic");
        }

        SQLStatement block = x.getBlock();

        println();

        block.accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        SQLCommentStatement.Type type = x.getType();

        SQLExprTableSource on = x.getOn();
        if (type == SQLCommentStatement.Type.TABLE) {
            print0(ucase ? "ALTER TABLE " : "alter table ");
            on.accept(this);
            print0(ucase ? " COMMENT = " : " comment = ");
            x.getComment().accept(this);
        } else {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) on.getExpr();

            SQLExpr table = propertyExpr.getOwner();
            String column = propertyExpr.getName();

            print0(ucase ? "ALTER TABLE " : "alter table ");
            printTableSourceExpr(table);
            print0(ucase ? " MODIFY COLUMN " : " modify column ");
            print(column);
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(XuGuFlushStatement x) {
        print0(ucase ? "FLUSH" : "flush");

        if (x.isNoWriteToBinlog()) {
            print0(ucase ? " NO_WRITE_TO_BINLOG" : " no_write_to_binlog");
        } else if (x.isLocal()) {
            print0(ucase ? " LOCAL" : " local");
        }

        if(x.isBinaryLogs()) {
            print0(ucase ? " BINARY LOGS" : " binary logs");
        }
        if (x.isDesKeyFile()) {
            print0(ucase ? " DES_KEY_FILE" : " des_key_file");
        }
        if (x.isEngineLogs()) {
            print0(ucase ? " ENGINE LOGS" : " engine logs");
        }
        if (x.isErrorLogs()) {
            print0(ucase ? " ERROR LOGS" : " error logs");
        }
        if (x.isGeneralLogs()) {
            print0(ucase ? " GENERAL LOGS" : " general logs");
        }
        if (x.isHots()) {
            print0(ucase ? " HOSTS" : " hosts");
        }
        if (x.isLogs()) {
            print0(ucase ? " LOGS" : " logs");
        }
        if (x.isPrivileges()) {
            print0(ucase ? " PRIVILEGES" : " privileges");
        }
        if (x.isOptimizerCosts()) {
            print0(ucase ? " OPTIMIZER_COSTS" : " optimizer_costs");
        }
        if (x.isQueryCache()) {
            print0(ucase ? " QUERY CACHE" : " query cache");
        }
        if (x.isRelayLogs()) {
            print0(ucase ? " RELAY LOGS" : " relay logs");
            SQLExpr channel = x.getRelayLogsForChannel();
            if (channel != null) {
                print(' ');
                channel.accept(this);
            }
        }
        if (x.isSlowLogs()) {
            print0(ucase ? " SLOW LOGS" : " slow logs");
        }
        if (x.isStatus()) {
            print0(ucase ? " STATUS" : " status");
        }
        if (x.isUserResources()) {
            print0(ucase ? " USER_RESOURCES" : " user_resources");
        }

        if(x.isTableOption()){
            print0(ucase ? " TABLES" : " tables");

            List<SQLExprTableSource> tables = x.getTables();
            if (tables != null && tables.size() > 0) {
                print(' ');
                printAndAccept(tables, ", ");
            }

            if (x.isWithReadLock()) {
                print0(ucase ? " WITH READ LOCK" : " with read lock");
            }

            if (x.isForExport()) {
                print0(ucase ? " FOR EXPORT" : " for export");
            }
        }
        return false;
    }

    @Override
    public void endVisit(XuGuFlushStatement x) {

    }

    @Override
    public boolean visit(XuGuEventSchedule x) {
        int cnt = 0;
        if (x.getAt() != null) {
            print0(ucase ? "AT " : "at ");
            printExpr(x.getAt());

            cnt++;
        }

        if (x.getEvery() != null) {
            print0(ucase ? "EVERY " : "every ");
            SQLIntervalExpr interval = (SQLIntervalExpr) x.getEvery();
            printExpr(interval.getValue());
            print(' ');
            print(interval.getUnit().name());

            cnt++;
        }

        if (x.getStarts() != null) {
            if (cnt > 0) {
                print(' ');
            }

            print0(ucase ? "STARTS " : "starts ");
            printExpr(x.getStarts());

            cnt++;
        }

        if (x.getEnds() != null) {
            if (cnt > 0) {
                print(' ');
            }
            print0(ucase ? "ENDS " : "ends ");
            printExpr(x.getEnds());

            cnt++;
        }

        return false;
    }

    @Override
    public void endVisit(XuGuEventSchedule x) {

    }

    @Override
    public boolean visit(XuGuCreateEventStatement x) {
        print0(ucase ? "CREATE " : "create ");

        SQLName definer = x.getDefiner();
        if (definer != null) {
            print0(ucase ? "DEFINER = " : "definer = ");
        }

        print0(ucase ? "EVENT " : "evnet ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printExpr(x.getName());

        XuGuEventSchedule schedule = x.getSchedule();
        print0(ucase ? " ON SCHEDULE " : " on schedule ");
        schedule.accept(this);

        Boolean enable = x.getEnable();
        if (enable != null) {
            if (enable) {
                print0(ucase ? " ENABLE" : " enable");
            } else {
                print0(ucase ? " DISABLE" : " disable");
                if (x.isDisableOnSlave()) {
                    print0(ucase ? " ON SLAVE" : " on slave");
                }
            }
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? "COMMENT " : "comment ");
            comment.accept(this);
        }

        println();
        SQLStatement body = x.getEventBody();
        if (!(body instanceof SQLExprStatement)) {
            print0(ucase ? "DO" : "do");
            println();
        }
        body.accept(this);

        return false;
    }

    @Override
    public void endVisit(XuGuCreateEventStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateAddLogFileGroupStatement x) {
        print0(ucase ? "CREATE LOGFILE GROUP " : "create logfile group ");
        x.getName().accept(this);
        print(' ');
        print0(ucase ? "ADD UNDOFILE " : "add undofile ");
        printExpr(x.getAddUndoFile());

        SQLExpr initialSize = x.getInitialSize();
        if (initialSize != null) {
            print0(ucase ? " INITIAL_SIZE " : " initial_size ");
            printExpr(initialSize);
        }

        SQLExpr undoBufferSize = x.getUndoBufferSize();
        if (undoBufferSize != null) {
            print0(ucase ? " UNDO_BUFFER_SIZE " : " undo_buffer_size ");
            printExpr(undoBufferSize);
        }

        SQLExpr redoBufferSize = x.getRedoBufferSize();
        if (redoBufferSize != null) {
            print0(ucase ? " REDO_BUFFER_SIZE " : " redo_buffer_size ");
            printExpr(redoBufferSize);
        }

        SQLExpr nodeGroup = x.getNodeGroup();
        if (nodeGroup != null) {
            print0(ucase ? " NODEGROUP " : " nodegroup ");
            printExpr(nodeGroup);
        }

        if (x.isWait()) {
            print0(ucase ? " WAIT" : " wait");
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            printExpr(comment);
        }

        SQLExpr engine = x.getEngine();
        if (engine != null) {
            print0(ucase ? " ENGINE " : " engine ");
            printExpr(engine);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuCreateAddLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateServerStatement x) {
        print0(ucase ? "CREATE SERVER " : "create server ");
        x.getName().accept(this);
        print0(ucase ? " FOREIGN DATA WRAPPER " : " foreign data wrapper ");
        printExpr(x.getForeignDataWrapper());

        print(" OPTIONS(");
        int cnt = 0;
        SQLExpr host = x.getHost();
        if (host != null) {
            print0(ucase ? "HOST " : "host ");
            printExpr(host);
            cnt++;
        }

        SQLExpr database = x.getDatabase();
        if (database != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "DATABASE " : "database ");
            printExpr(database);
        }

        SQLExpr user = x.getUser();
        if (user != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "USER " : "user ");
            printExpr(user);
        }

        SQLExpr password = x.getPassword();
        if (password != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "PASSWORD " : "password ");
            printExpr(password);
        }

        SQLExpr socket = x.getSocket();
        if (socket != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "SOCKET " : "socket ");
            printExpr(socket);
        }

        SQLExpr owner = x.getOwner();
        if (owner != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "OWNER " : "owner ");
            printExpr(owner);
        }

        SQLExpr port = x.getPort();
        if (port != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "PORT " : "port ");
            printExpr(port);
        }
        print(')');

        return false;
    }

    @Override
    public void endVisit(XuGuCreateServerStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateTableSpaceStatement x) {
        print0(ucase ? "CREATE TABLESPACE " : "create tablespace ");
        x.getName().accept(this);

        SQLExpr addDataFile = x.getAddDataFile();
        if (addDataFile != null) {
            print0(ucase ? " ADD DATAFILE " : " add datafile ");
            addDataFile.accept(this);
        }

        SQLExpr fileBlockSize = x.getFileBlockSize();
        if (fileBlockSize != null) {
            print0(ucase ? " FILE_BLOCK_SIZE = " : " file_block_size = ");
            fileBlockSize.accept(this);
        }

        SQLExpr logFileGroup = x.getLogFileGroup();
        if (logFileGroup != null) {
            print0(ucase ? " USE LOGFILE GROUP " : " use logfile group ");
            logFileGroup.accept(this);
        }

        SQLExpr extentSize = x.getExtentSize();
        if (extentSize != null) {
            print0(ucase ? " EXTENT_SIZE = " : " extent_size = ");
            extentSize.accept(this);
        }

        SQLExpr initialSize = x.getInitialSize();
        if (initialSize != null) {
            print0(ucase ? " INITIAL_SIZE = " : " initial_size = ");
            initialSize.accept(this);
        }

        SQLExpr autoExtentSize = x.getAutoExtentSize();
        if (autoExtentSize != null) {
            print0(ucase ? " AUTOEXTEND_SIZE = " : " autoextend_size = ");
            autoExtentSize.accept(this);
        }

        SQLExpr maxSize = x.getMaxSize();
        if (autoExtentSize != null) {
            print0(ucase ? " MAX_SIZE = " : " max_size = ");
            maxSize.accept(this);
        }

        SQLExpr nodeGroup = x.getNodeGroup();
        if (nodeGroup != null) {
            print0(ucase ? " NODEGROUP = " : " nodegroup = ");
            nodeGroup.accept(this);
        }

        if (x.isWait()) {
            print0(ucase ? " WAIT" : " wait");
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            printExpr(comment);
        }

        SQLExpr engine = x.getEngine();
        if (engine != null) {
            print0(ucase ? " ENGINE " : " engine ");
            printExpr(engine);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuCreateTableSpaceStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterEventStatement x) {
        print0(ucase ? "ALTER " : "alter ");

        SQLName definer = x.getDefiner();
        if (definer != null) {
            print0(ucase ? "DEFINER = " : "definer = ");
        }

        print0(ucase ? "EVENT " : "evnet ");
        printExpr(x.getName());

        XuGuEventSchedule schedule = x.getSchedule();
        if (schedule != null) {
            print0(ucase ? " ON SCHEDULE " : " on schedule ");
            schedule.accept(this);
        }

        Boolean enable = x.getEnable();
        if (enable != null) {
            if (enable) {
                print0(ucase ? " ENABLE" : " enable");
            } else {
                print0(ucase ? " DISABLE" : " disable");
                if (x.isDisableOnSlave()) {
                    print0(ucase ? " ON SLAVE" : " on slave");
                }
            }
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? "COMMENT " : "comment ");
            comment.accept(this);
        }

        SQLStatement body = x.getEventBody();
        if (body != null) {
            println();
            if (!(body instanceof SQLExprStatement)) {
                print0(ucase ? "DO" : "do");
                println();
            }
            body.accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuAlterEventStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterLogFileGroupStatement x) {
        print0(ucase ? "ALTER LOGFILE GROUP " : "alter logfile group ");
        x.getName().accept(this);
        print(' ');
        print0(ucase ? "ADD UNDOFILE " : "add undofile ");
        printExpr(x.getAddUndoFile());

        SQLExpr initialSize = x.getInitialSize();
        if (initialSize != null) {
            print0(ucase ? " INITIAL_SIZE " : " initial_size ");
            printExpr(initialSize);
        }

        if (x.isWait()) {
            print0(ucase ? " WAIT" : " wait");
        }

        SQLExpr engine = x.getEngine();
        if (engine != null) {
            print0(ucase ? " ENGINE " : " engine ");
            printExpr(engine);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuAlterLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterServerStatement x) {
        print0(ucase ? "ATLER SERVER " : "alter server ");
        x.getName().accept(this);

        print(" OPTIONS(");
        SQLExpr user = x.getUser();
        if (user != null) {
            print0(ucase ? "USER " : "user ");
            printExpr(user);
        }

        print(')');

        return false;
    }

    @Override
    public void endVisit(XuGuAlterServerStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterTablespaceStatement x) {
        print0(ucase ? "CREATE TABLESPACE " : "create tablespace ");
        x.getName().accept(this);

        SQLExpr addDataFile = x.getAddDataFile();
        if (addDataFile != null) {
            print0(ucase ? " ADD DATAFILE " : " add datafile ");
            addDataFile.accept(this);
        }

        SQLExpr initialSize = x.getInitialSize();
        if (initialSize != null) {
            print0(ucase ? " INITIAL_SIZE = " : " initial_size = ");
            initialSize.accept(this);
        }

        if (x.isWait()) {
            print0(ucase ? " WAIT" : " wait");
        }

        SQLExpr engine = x.getEngine();
        if (engine != null) {
            print0(ucase ? " ENGINE " : " engine ");
            printExpr(engine);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuAlterTablespaceStatement x) {

    }

    @Override
    public boolean visit(XuGuShowDatabasePartitionStatusStatement x) {
        print0(ucase ? "SHOW DATABASE PARTITION STATUS FOR " : "show database partition status for ");
        x.getDatabase().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuShowDatabasePartitionStatusStatement x) {

    }

    protected void printQuery(SQLSelectQuery x) {
        Class<?> clazz = x.getClass();
        if (clazz == XuGuSelectQueryBlock.class) {
            visit((XuGuSelectQueryBlock) x);
        } else if (clazz == SQLSelectQueryBlock.class) {
            visit((SQLSelectQueryBlock) x);
        } else if (clazz == SQLUnionQuery.class) {
            visit((SQLUnionQuery) x);
        } else {
            x.accept(this);
        }
    }

    public void printInsertColumns(List<SQLExpr> columns) {
        final int size = columns.size();
        if (size > 0) {
            if (size > 5) {
                this.indentCount++;
                print(' ');
            }
            print('(');
            for (int i = 0; i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print0(", ");
                }

                SQLExpr column = columns.get(i);
                if (column instanceof SQLIdentifierExpr) {
                    visit((SQLIdentifierExpr) column);
                } else {
                    printExpr(column);
                }

                String dataType = (String) column.getAttribute("dataType");
                if (dataType != null) {
                    print(' ');
                    print(dataType);
                }
            }
            print(')');
            if (size > 5) {
                this.indentCount--;
            }
        }
    }

    @Override
    public void endVisit(XuGuChecksumTableStatement x) {

    }

    @Override
    public boolean visit(XuGuChecksumTableStatement x) {
        print0(ucase ? "CHECKSUM TABLE " : "checksum table ");
        printAndAccept(x.getTables(), "，");
        return false;
    }

    @Override
    public boolean visit(XuGuBackupSystemDatabaseStatement x) {
        print0(ucase ? "BACKUP " : "backup ");

        if (x.isSystem()) {
            print0(ucase ? "SYSTEM " : "system ");
        }else if (x.isDatabase()){
            print0(ucase ? "DATABASE " : "database ");
        }

        if (x.isAll()) {
            print0(ucase ? "ALL " : "all ");
        } else if (x.isIncrement()) {
            print0(ucase ? "INCREMENT " : "increment ");
        }

        if (x.isAppend()) {
            print0(ucase ? "APPEND " : "append ");
        }

        print0(ucase ? "TO " : "to ");

        x.getFilePath().accept(this);

        if (x.isOnline()) {
            print0(ucase ? " ONLINE" : " online");
        } else if (x.isOffline()) {
            print0(ucase ? " OFFLINE" : " offline");
        }

        if (x.isEncryptor()) {
            print0(ucase ? " ENCRYPTOR IS " : " encryptor is ");
            x.getOptEncryptor().accept(this);
        }

        if (x.isCompress()) {
            print0(ucase ? " COMPRESS" : " compress");
        } else if (x.isNocompress()) {
            print0(ucase ? " NOCOMPRESS" : " nocompress");
        }

        return false;
    }

    @Override
    public void endVisit(XuGuBackupSystemDatabaseStatement x) {

    }

    @Override
    public boolean visit(XuGuBackupUserSchemaTableStatement x) {
        print0(ucase ? "BACKUP " : "backup ");

        if (x.isUser()) {
            print0(ucase ? "USER " : "user ");
            x.getUserName().accept(this);
        } else if (x.isSchema()) {
            print0(ucase ? "SCHEMA " : "schema ");
            x.getSchemaName().accept(this);
        } else if (x.isTable()){
            print0(ucase ? "TABLE " : "table ");
            x.getTableName().accept(this);
        }

        if (x.isAppend()) {
            print0(ucase ? " APPEND" : " append");
        }

        print0(ucase ? " TO " : " to ");

        x.getFilePath().accept(this);

        if (x.isEncryptor()) {
            print0(ucase ? " ENCRYPTOR IS " : " encryptor is ");
            x.getOptEncryptor().accept(this);
        }

        if (x.isCompress()) {
            print0(ucase ? " COMPRESS" : " compress");
        } else if (x.isNocompress()) {
            print0(ucase ? " NOCOMPRESS" : " nocompress");
        }

        return false;
    }

    @Override
    public void endVisit(XuGuBackupUserSchemaTableStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreSystemStatement x) {
        print0(ucase ? "RESTORE SYSTEM FROM " : "restore system from ");

        x.getFilePath().accept(this);

        if (x.isEncryptor()) {
            print0(ucase ? " ENCRYPTOR IS " : " encryptor is ");
            x.getOptEncryptor().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(XuGuRestoreSystemStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreDatabaseStatement x) {
        print0(ucase ? "RESTORE DATABASE " : "restore database ");

        x.getDbName().accept(this);

        print0(ucase ? " FROM " : " from ");

        x.getFilePath().accept(this);

        if (x.isEncryptor()) {
            print0(ucase ? " ENCRYPTOR IS " : " encryptor is ");
            x.getOptEncryptor().accept(this);
        }

        if (x.isReindex()) {
            print0(ucase ? " WITH REINDEX" : " with reindex");
        }
        return false;
    }

    @Override
    public void endVisit(XuGuRestoreDatabaseStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreUserStatement x) {
        print0(ucase ? "RESTORE USER " : "restore user ");
        x.getUserName().accept(this);
        print0(ucase ? " FROM " : " from ");
        x.getFilePath().accept(this);
        if (x.isEncryptor()) {
            print0(ucase ? " ENCRYPTOR IS " : " encryptor is ");
            x.getOptEncryptor().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuRestoreUserStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreSchemaStatement x) {
        print0(ucase ? "RESTORE SCHEMA " : "restore schema ");
        x.getSchemaName().accept(this);
        if (x.isRename()) {
            print0(ucase ? " RENAME TO " : " rename to ");
            x.getNewSchemaName().accept(this);
        }
        print0(ucase ? " FROM " : " from ");
        x.getFilePath().accept(this);
        if (x.isEncryptor()) {
            print0(ucase ? " ENCRYPTOR IS " : " encryptor is ");
            x.getOptEncryptor().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuRestoreSchemaStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreTableStatement x) {
        print0(ucase ? "RESTORE TABLE " : "restore table ");
        x.getTableName().accept(this);
        if (x.isRename()) {
            print0(ucase ? " RENAME TO " : " rename to ");
            x.getTableRename().accept(this);
        }
        print0(ucase ? " FROM " : " from ");
        x.getFilePath().accept(this);
        if (x.isEncryptor()) {
            print0(ucase ? " ENCRYPTOR IS " : " encryptor is ");
            x.getOptEncryptor().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuRestoreTableStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterDatabaseStatement x) {
        print0(ucase ? "ALTER DATABASE " : "alter database ");
        x.getDatabaseName().accept(this);
        print0(ucase ? " RENAME TO " : " rename to ");
        x.getDatabaseNewName().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuAlterDatabaseStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateSchemaStatement x) {
        print0(ucase ? "CREATE SCHEMA " : "create schema ");
        x.getSchemaName().accept(this);
        if (x.getUserName() != null) {
            print0(ucase ? " AUTHORIZATION " : " authorization ");
            x.getUserName().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuCreateSchemaStatement x) {

    }

    @Override
    public boolean visit(XuGuDropSchemaStatement x) {
        print0(ucase ? "DROP SCHEMA " : "drop schema ");
        x.getSchemaName().accept(this);
        if (x.isCascade()) {
            print0(ucase ? " CASCADE" : " cascade");
        } else if (x.isRestrict()) {
            print0(ucase ? " RESTRICT" : " restrict");
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDropSchemaStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterSchemaStatement x) {
        print0(ucase ? "ALTER SCHEMA " : "alter schema ");
        x.getSchemaName().accept(this);
        if (x.getNewName() != null) {
            print0(ucase ? " RENAME TO " : " rename to ");
            x.getNewName().accept(this);
        } else {
            print0(ucase ? " OWNER TO " : " owner to ");
            x.getUserName().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(XuGuAlterSchemaStatement x) {

    }

    @Override
    public boolean visit(XuGuForStatement x) {
        boolean all = x.isAll();
        if (all) {
            print0(ucase ? "FORALL " : "forall ");
        } else {
            print0(ucase ? "FOR " : "for ");
        }
        x.getIndex().accept(this);
        print0(ucase ? " IN " : " in ");

        if (x.getRange() instanceof XuGuRangeExpr) {
            XuGuRangeExpr rangeExpr = (XuGuRangeExpr) x.getRange();
            if (rangeExpr.getLowBound() instanceof SQLIntegerExpr && rangeExpr.getUpBound() instanceof SQLIntegerExpr) {
                if (((SQLIntegerExpr) rangeExpr.getLowBound()).getNumber() instanceof Integer && ((SQLIntegerExpr) rangeExpr.getUpBound()).getNumber() instanceof Integer) {
                    Integer lowBound = (Integer) ((SQLIntegerExpr) rangeExpr.getLowBound()).getNumber();
                    Integer upBound = (Integer) ((SQLIntegerExpr) rangeExpr.getUpBound()).getNumber();
                    if (lowBound > upBound) {
                        print0(" REVERSE ");
                    }
                }
            }
        }

        SQLExpr range = x.getRange();
        range.accept(this);

        if (!all) {
            println();
            print0(ucase ? "LOOP" : "loop");
        }
        this.indentCount++;
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement stmt = x.getStatements().get(i);
            stmt.accept(this);
            if (!all) {
                if (i != size - 1) {
                    println();
                }
            }
        }

        this.indentCount--;
        if (!all) {
            println();
            print0(ucase ? "END LOOP" : "end loop");
            SQLName endLabel = x.getEndLabel();
            if (endLabel != null) {
                print(' ');
                endLabel.accept(this);
            }
        }
        return false;
    }

    @Override
    public void endVisit(XuGuForStatement x) {

    }

    @Override
    public boolean visit(XuGuRangeExpr x) {
        x.getLowBound().accept(this);
        print0("..");
        x.getUpBound().accept(this);
        return false;
    }

    @Override
    public void endVisit(XuGuRangeExpr x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalDay x) {
        print0(x.getName());
        if (x.getArguments().size() > 0) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalDay x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalYearToMonth x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalYearToMonth x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalYear x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalYear x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalMonth x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalMonth x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalDayToHour x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalDayToHour x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalDayToMinute x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalDayToMinute x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalDayToSecond x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            //x.getArguments().stream().forEach(sqlExpr -> sqlExpr.accept(this));
            print(')');
        }

        if (!x.getFractionalSeconds().isEmpty()) {
            print('(');
            x.getFractionalSeconds().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalDayToSecond x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalHour x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }

        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalHour x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalHourToMinute x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalHourToMinute x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalHourToSecond x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }

        if (!x.getFractionalSeconds().isEmpty()) {
            print('(');
            x.getFractionalSeconds().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalHourToSecond x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalMinute x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalMinute x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalMinuteToSecond x) {
        print0(x.getName());
        if (!x.getArguments().isEmpty()) {
            print('(');
            x.getArguments().get(0).accept(this);
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalMinuteToSecond x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalSecond x) {
        print0(x.getName());
        if (x.getArguments().size() > 0) {
            print('(');
            //x.getArguments().get(0).accept(this);
            //x.getArguments().stream().forEach(sqlExpr -> sqlExpr.accept(this));//Xugu Interval to Second支持传入精度和标度
            for(int i=0;i<x.getArguments().size();i++){
                if(i!=x.getArguments().size()-1){
                    x.getArguments().get(i).accept(this);
                    print(",");
                }else{
                    x.getArguments().get(i).accept(this);
                }
            }
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalSecond x) {

    }

    @Override
    public boolean visit(XuGuReturningClause x) {
        print0(ucase ? "RETURNING " : "returning ");
        printAndAccept(x.getItems(), ", ");
        if (x.isOptBulk()) {
            print0(ucase ? " BULK COLLECT" : " bulk collect");
        }
        print0(ucase ? " INTO " : " into ");
        printAndAccept(x.getValues(), ", ");
        return false;
    }

    @Override
    public void endVisit(XuGuReturningClause x) {

    }
}
