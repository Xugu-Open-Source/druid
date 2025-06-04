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
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuForeignKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuIgnoreIndexHint;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuPrimaryKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuUnique;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuUseIndexHint;
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
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuQ_EscapeExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuRangeExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuTypeCastExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuUserName;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.CobarShowStatus;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterDatabaseStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterEventStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterLogFileGroupStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterSchemaStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterServerStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterTableAlterColumn;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterTableDiscardTablespace;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterTableImportTablespace;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterTableModifyColumn;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterTableOption;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterTablespaceStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAlterUserStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuAnalyzeStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuBackupSystemDatabaseStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuBackupUserSchemaTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuBinlogStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuChecksumTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateAddLogFileGroupStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateEventStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreatePackageStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateSchemaStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateServerStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateTableSpaceStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateTypeStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateUserStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuDeallocatePrepareStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuDeleteStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuDropSchemaStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuEventSchedule;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuExecuteStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuExitStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuExplainStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuFlushStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuForStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuHelpStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuHintStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuInsertStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuKillStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuLoadDataInFileStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuLoadXmlStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuLockTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuMultiInsertStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuOptimizeStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuPartitionByKey;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuPipeRowStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuPrepareStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuRenameTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuResetStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuRestoreDatabaseStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuRestoreSchemaStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuRestoreSystemStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuRestoreTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuRestoreUserStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuSelectGroupByClause;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuSelectQueryBlock;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuSetTransactionStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowAuthorsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowBinLogEventsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowBinaryLogsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCharacterSetStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCollationStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowContributorsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCreateDatabaseStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCreateEventStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCreateProcedureStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCreateTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCreateTriggerStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowCreateViewStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowDatabasePartitionStatusStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowDatabasesStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowEngineStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowEnginesStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowErrorsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowEventsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowFunctionCodeStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowFunctionStatusStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowGrantsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowIndexesStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowKeysStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowMasterLogsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowMasterStatusStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowOpenTablesStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowPluginsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowPrivilegesStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowProcedureCodeStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowProcedureStatusStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowProcessListStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowProfileStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowProfilesStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowRelayLogEventsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowSlaveHostsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowSlaveStatusStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowStatusStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowTableStatusStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowTriggersStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuShowWarningsStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuSubPartitionByKey;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuSubPartitionByList;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuTableIndex;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuUnlockTablesStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuUpdateStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuUpdateTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface XuGuASTVisitor extends SQLASTVisitor {
    boolean visit(XuGuTableIndex x);

    void endVisit(XuGuTableIndex x);

    boolean visit(XuGuKey x);

    void endVisit(XuGuKey x);

    boolean visit(XuGuPrimaryKey x);

    void endVisit(XuGuPrimaryKey x);

    boolean visit(XuGuUnique x);

    void endVisit(XuGuUnique x);

    boolean visit(XuGuForeignKey x);

    void endVisit(XuGuForeignKey x);

    void endVisit(XuGuExtractExpr x);

    boolean visit(XuGuExtractExpr x);

    void endVisit(XuGuMatchAgainstExpr x);

    boolean visit(XuGuMatchAgainstExpr x);

    void endVisit(XuGuPrepareStatement x);

    boolean visit(XuGuPrepareStatement x);

    void endVisit(XuGuExecuteStatement x);

    boolean visit(XuGuDeallocatePrepareStatement x);

    void endVisit(XuGuDeallocatePrepareStatement x);

    boolean visit(XuGuExecuteStatement x);

    void endVisit(XuGuDeleteStatement x);

    boolean visit(XuGuDeleteStatement x);

    void endVisit(XuGuInsertStatement x);

    boolean visit(XuGuInsertStatement x);

    void endVisit(XuGuLoadDataInFileStatement x);

    boolean visit(XuGuLoadDataInFileStatement x);

    void endVisit(XuGuLoadXmlStatement x);

    boolean visit(XuGuLoadXmlStatement x);

    void endVisit(XuGuShowDatabasesStatement x);

    boolean visit(XuGuShowDatabasesStatement x);

    void endVisit(XuGuShowWarningsStatement x);

    boolean visit(XuGuShowWarningsStatement x);

    void endVisit(XuGuShowStatusStatement x);

    boolean visit(XuGuShowStatusStatement x);

    void endVisit(XuGuShowAuthorsStatement x);

    boolean visit(XuGuShowAuthorsStatement x);

    void endVisit(CobarShowStatus x);

    boolean visit(CobarShowStatus x);

    void endVisit(XuGuKillStatement x);

    boolean visit(XuGuKillStatement x);

    void endVisit(XuGuBinlogStatement x);

    boolean visit(XuGuBinlogStatement x);

    void endVisit(XuGuResetStatement x);

    boolean visit(XuGuResetStatement x);

    void endVisit(XuGuCreateUserStatement x);

    boolean visit(XuGuCreateUserStatement x);

    void endVisit(XuGuCreateUserStatement.UserSpecification x);

    boolean visit(XuGuCreateUserStatement.UserSpecification x);

    void endVisit(XuGuPartitionByKey x);

    boolean visit(XuGuPartitionByKey x);

    boolean visit(XuGuSelectQueryBlock x);

    void endVisit(XuGuSelectQueryBlock x);

    boolean visit(XuGuOutFileExpr x);

    void endVisit(XuGuOutFileExpr x);

    boolean visit(XuGuExplainStatement x);

    void endVisit(XuGuExplainStatement x);

    boolean visit(XuGuUpdateStatement x);

    void endVisit(XuGuUpdateStatement x);

    boolean visit(XuGuSetTransactionStatement x);

    void endVisit(XuGuSetTransactionStatement x);

    boolean visit(XuGuShowBinaryLogsStatement x);

    void endVisit(XuGuShowBinaryLogsStatement x);

    boolean visit(XuGuShowMasterLogsStatement x);

    void endVisit(XuGuShowMasterLogsStatement x);

    boolean visit(XuGuShowCharacterSetStatement x);

    void endVisit(XuGuShowCharacterSetStatement x);

    boolean visit(XuGuShowCollationStatement x);

    void endVisit(XuGuShowCollationStatement x);

    boolean visit(XuGuShowBinLogEventsStatement x);

    void endVisit(XuGuShowBinLogEventsStatement x);

    boolean visit(XuGuShowContributorsStatement x);

    void endVisit(XuGuShowContributorsStatement x);

    boolean visit(XuGuShowCreateDatabaseStatement x);

    void endVisit(XuGuShowCreateDatabaseStatement x);

    boolean visit(XuGuShowCreateEventStatement x);

    void endVisit(XuGuShowCreateEventStatement x);

    boolean visit(XuGuShowCreateFunctionStatement x);

    void endVisit(XuGuShowCreateFunctionStatement x);

    boolean visit(XuGuShowCreateProcedureStatement x);

    void endVisit(XuGuShowCreateProcedureStatement x);

    boolean visit(XuGuShowCreateTableStatement x);

    void endVisit(XuGuShowCreateTableStatement x);

    boolean visit(XuGuShowCreateTriggerStatement x);

    void endVisit(XuGuShowCreateTriggerStatement x);

    boolean visit(XuGuShowCreateViewStatement x);

    void endVisit(XuGuShowCreateViewStatement x);

    boolean visit(XuGuShowEngineStatement x);

    void endVisit(XuGuShowEngineStatement x);

    boolean visit(XuGuShowEnginesStatement x);

    void endVisit(XuGuShowEnginesStatement x);

    boolean visit(XuGuShowErrorsStatement x);

    void endVisit(XuGuShowErrorsStatement x);

    boolean visit(XuGuShowEventsStatement x);

    void endVisit(XuGuShowEventsStatement x);

    boolean visit(XuGuShowFunctionCodeStatement x);

    void endVisit(XuGuShowFunctionCodeStatement x);

    boolean visit(XuGuShowFunctionStatusStatement x);

    void endVisit(XuGuShowFunctionStatusStatement x);

    boolean visit(XuGuShowGrantsStatement x);

    void endVisit(XuGuShowGrantsStatement x);

    boolean visit(XuGuUserName x);

    void endVisit(XuGuUserName x);

    boolean visit(XuGuShowIndexesStatement x);

    void endVisit(XuGuShowIndexesStatement x);

    boolean visit(XuGuShowKeysStatement x);

    void endVisit(XuGuShowKeysStatement x);

    boolean visit(XuGuShowMasterStatusStatement x);

    void endVisit(XuGuShowMasterStatusStatement x);

    boolean visit(XuGuShowOpenTablesStatement x);

    void endVisit(XuGuShowOpenTablesStatement x);

    boolean visit(XuGuShowPluginsStatement x);

    void endVisit(XuGuShowPluginsStatement x);

    boolean visit(XuGuShowPrivilegesStatement x);

    void endVisit(XuGuShowPrivilegesStatement x);

    boolean visit(XuGuShowProcedureCodeStatement x);

    void endVisit(XuGuShowProcedureCodeStatement x);

    boolean visit(XuGuShowProcedureStatusStatement x);

    void endVisit(XuGuShowProcedureStatusStatement x);

    boolean visit(XuGuShowProcessListStatement x);

    void endVisit(XuGuShowProcessListStatement x);

    boolean visit(XuGuShowProfileStatement x);

    void endVisit(XuGuShowProfileStatement x);

    boolean visit(XuGuShowProfilesStatement x);

    void endVisit(XuGuShowProfilesStatement x);

    boolean visit(XuGuShowRelayLogEventsStatement x);

    void endVisit(XuGuShowRelayLogEventsStatement x);

    boolean visit(XuGuShowSlaveHostsStatement x);

    void endVisit(XuGuShowSlaveHostsStatement x);

    boolean visit(XuGuShowSlaveStatusStatement x);

    void endVisit(XuGuShowSlaveStatusStatement x);

    boolean visit(XuGuShowTableStatusStatement x);

    void endVisit(XuGuShowTableStatusStatement x);

    boolean visit(XuGuShowTriggersStatement x);

    void endVisit(XuGuShowTriggersStatement x);

    boolean visit(XuGuRenameTableStatement.Item x);

    void endVisit(XuGuRenameTableStatement.Item x);

    boolean visit(XuGuRenameTableStatement x);

    void endVisit(XuGuRenameTableStatement x);

    boolean visit(XuGuUseIndexHint x);

    void endVisit(XuGuUseIndexHint x);

    boolean visit(XuGuIgnoreIndexHint x);

    void endVisit(XuGuIgnoreIndexHint x);

    boolean visit(XuGuLockTableStatement x);

    void endVisit(XuGuLockTableStatement x);

    boolean visit(XuGuUnlockTablesStatement x);

    void endVisit(XuGuUnlockTablesStatement x);

    boolean visit(XuGuForceIndexHint x);

    void endVisit(XuGuForceIndexHint x);

    boolean visit(XuGuAlterTableChangeColumn x);

    void endVisit(XuGuAlterTableChangeColumn x);

    boolean visit(XuGuAlterTableOption x);

    void endVisit(XuGuAlterTableOption x);

    boolean visit(XuGuCreateTableStatement x);

    void endVisit(XuGuCreateTableStatement x);

    boolean visit(XuGuHelpStatement x);

    void endVisit(XuGuHelpStatement x);

    boolean visit(XuGuCharExpr x);

    void endVisit(XuGuCharExpr x);

    boolean visit(XuGuAlterTableModifyColumn x);

    void endVisit(XuGuAlterTableModifyColumn x);

    boolean visit(XuGuAlterTableDiscardTablespace x);

    void endVisit(XuGuAlterTableDiscardTablespace x);

    boolean visit(XuGuAlterTableImportTablespace x);

    void endVisit(XuGuAlterTableImportTablespace x);

    boolean visit(XuGuCreateTableStatement.TableSpaceOption x);

    void endVisit(XuGuCreateTableStatement.TableSpaceOption x);

    boolean visit(XuGuAnalyzeStatement x);

    void endVisit(XuGuAnalyzeStatement x);

    boolean visit(XuGuAlterUserStatement x);

    void endVisit(XuGuAlterUserStatement x);

    boolean visit(XuGuOptimizeStatement x);

    void endVisit(XuGuOptimizeStatement x);

    boolean visit(XuGuHintStatement x);

    void endVisit(XuGuHintStatement x);

    boolean visit(XuGuOrderingExpr x);

    void endVisit(XuGuOrderingExpr x);

    boolean visit(XuGuCaseStatement x);

    void endVisit(XuGuCaseStatement x);

    boolean visit(XuGuDeclareStatement x);

    void endVisit(XuGuDeclareStatement x);

    boolean visit(XuGuSelectIntoStatement x);

    void endVisit(XuGuSelectIntoStatement x);

    boolean visit(XuGuWhenStatement x);

    void endVisit(XuGuWhenStatement x);

    boolean visit(XuGuLeaveStatement x);

    void endVisit(XuGuLeaveStatement x);

    boolean visit(XuGuIterateStatement x);

    void endVisit(XuGuIterateStatement x);

    boolean visit(XuGuRepeatStatement x);

    void endVisit(XuGuRepeatStatement x);

    boolean visit(XuGuCursorDeclareStatement x);

    void endVisit(XuGuCursorDeclareStatement x);

    boolean visit(XuGuUpdateTableSource x);

    void endVisit(XuGuUpdateTableSource x);

    boolean visit(XuGuAlterTableAlterColumn x);

    void endVisit(XuGuAlterTableAlterColumn x);

    boolean visit(XuGuSubPartitionByKey x);

    void endVisit(XuGuSubPartitionByKey x);

    boolean visit(XuGuSubPartitionByList x);

    void endVisit(XuGuSubPartitionByList x);

    boolean visit(XuGuDeclareHandlerStatement x);

    void endVisit(XuGuDeclareHandlerStatement x);

    boolean visit(XuGuDeclareConditionStatement x);

    void endVisit(XuGuDeclareConditionStatement x);

    boolean visit(XuGuFlushStatement x);

    void endVisit(XuGuFlushStatement x);

    boolean visit(XuGuEventSchedule x);

    void endVisit(XuGuEventSchedule x);

    boolean visit(XuGuCreateEventStatement x);

    void endVisit(XuGuCreateEventStatement x);

    boolean visit(XuGuCreateAddLogFileGroupStatement x);

    void endVisit(XuGuCreateAddLogFileGroupStatement x);

    boolean visit(XuGuCreateServerStatement x);

    void endVisit(XuGuCreateServerStatement x);

    boolean visit(XuGuCreateTableSpaceStatement x);

    void endVisit(XuGuCreateTableSpaceStatement x);

    boolean visit(XuGuAlterEventStatement x);

    void endVisit(XuGuAlterEventStatement x);

    boolean visit(XuGuAlterLogFileGroupStatement x);

    void endVisit(XuGuAlterLogFileGroupStatement x);

    boolean visit(XuGuAlterServerStatement x);

    void endVisit(XuGuAlterServerStatement x);

    boolean visit(XuGuAlterTablespaceStatement x);

    void endVisit(XuGuAlterTablespaceStatement x);

    boolean visit(XuGuShowDatabasePartitionStatusStatement x);

    void endVisit(XuGuShowDatabasePartitionStatusStatement x);

    boolean visit(XuGuChecksumTableStatement x);

    void endVisit(XuGuChecksumTableStatement x);

    boolean visit(XuGuBackupSystemDatabaseStatement x);

    void endVisit(XuGuBackupSystemDatabaseStatement x);

    boolean visit(XuGuBackupUserSchemaTableStatement x);

    void endVisit(XuGuBackupUserSchemaTableStatement x);

    boolean visit(XuGuRestoreSystemStatement x);

    void endVisit(XuGuRestoreSystemStatement x);

    boolean visit(XuGuRestoreDatabaseStatement x);

    void endVisit(XuGuRestoreDatabaseStatement x);

    boolean visit(XuGuRestoreUserStatement x);

    void endVisit(XuGuRestoreUserStatement x);

    boolean visit(XuGuRestoreSchemaStatement x);

    void endVisit(XuGuRestoreSchemaStatement x);

    boolean visit(XuGuRestoreTableStatement x);

    void endVisit(XuGuRestoreTableStatement x);

    boolean visit(XuGuAlterDatabaseStatement x);

    void endVisit(XuGuAlterDatabaseStatement x);

    boolean visit(XuGuCreateSchemaStatement x);

    void endVisit(XuGuCreateSchemaStatement x);

    boolean visit(XuGuDropSchemaStatement x);

    void endVisit(XuGuDropSchemaStatement x);

    boolean visit(XuGuAlterSchemaStatement x);

    void endVisit(XuGuAlterSchemaStatement x);

    boolean visit(XuGuCreateTypeStatement x);

    void endVisit(XuGuCreateTypeStatement x);

    boolean visit(XuGuForStatement x);

    void endVisit(XuGuForStatement x);

    boolean visit(XuGuRangeExpr x);

    void endVisit(XuGuRangeExpr x);

    boolean visit(XuGuDataTypeIntervalDay x);

    void endVisit(XuGuDataTypeIntervalDay x);

    boolean visit(XuGuDataTypeIntervalYearToMonth x);

    void endVisit(XuGuDataTypeIntervalYearToMonth x);

    boolean visit(XuGuDataTypeIntervalYear x);

    void endVisit(XuGuDataTypeIntervalYear x);

    boolean visit(XuGuDataTypeIntervalMonth x);

    void endVisit(XuGuDataTypeIntervalMonth x);

    boolean visit(XuGuDataTypeIntervalDayToHour x);

    void endVisit(XuGuDataTypeIntervalDayToHour x);

    boolean visit(XuGuDataTypeIntervalDayToMinute x);

    void endVisit(XuGuDataTypeIntervalDayToMinute x);

    boolean visit(XuGuDataTypeIntervalDayToSecond x);

    void endVisit(XuGuDataTypeIntervalDayToSecond x);

    boolean visit(XuGuDataTypeIntervalHour x);

    void endVisit(XuGuDataTypeIntervalHour x);

    boolean visit(XuGuDataTypeIntervalHourToMinute x);

    void endVisit(XuGuDataTypeIntervalHourToMinute x);

    boolean visit(XuGuDataTypeIntervalHourToSecond x);

    void endVisit(XuGuDataTypeIntervalHourToSecond x);

    boolean visit(XuGuDataTypeIntervalMinute x);

    void endVisit(XuGuDataTypeIntervalMinute x);

    boolean visit(XuGuDataTypeIntervalMinuteToSecond x);

    void endVisit(XuGuDataTypeIntervalMinuteToSecond x);

    boolean visit(XuGuDataTypeIntervalSecond x);

    void endVisit(XuGuDataTypeIntervalSecond x);

    boolean visit(XuGuReturningClause x);

    void endVisit(XuGuReturningClause x);

    boolean visit(XuGuExitStatement x);

    void endVisit(XuGuExitStatement x);

    boolean visit(XuGuMultiInsertStatement x);

    void endVisit(XuGuMultiInsertStatement x);

    boolean visit(XuGuMultiInsertStatement.ConditionalInsertClause x);

    void endVisit(XuGuMultiInsertStatement.ConditionalInsertClause x);

    boolean visit(XuGuMultiInsertStatement.ConditionalInsertClauseItem x);

    void endVisit(XuGuMultiInsertStatement.ConditionalInsertClauseItem x);

    boolean visit(XuGuMultiInsertStatement.InsertIntoClause x);

    void endVisit(XuGuMultiInsertStatement.InsertIntoClause x);

    boolean visit(XuGuTypeCastExpr x);

    void endVisit(XuGuTypeCastExpr x);

    boolean visit(XuGuSelectGroupByClause x);

    void endVisit(XuGuSelectGroupByClause x);

    boolean visit(XuGuSelectGroupByClause.XgCompositeGroupItem x);

    void endVisit(XuGuSelectGroupByClause.XgCompositeGroupItem x);

    boolean visit(XuGuSelectGroupByClause.XgExprGroupItem x);

    void endVisit(XuGuSelectGroupByClause.XgExprGroupItem x);

    boolean visit(XuGuSelectGroupByClause.XgEmptyGroupItem x);

    void endVisit(XuGuSelectGroupByClause.XgEmptyGroupItem x);

    boolean visit(XuGuCreatePackageStatement x);

    void endVisit(XuGuCreatePackageStatement x);

    boolean visit(XuGuPipeRowStatement x);

    void endVisit(XuGuPipeRowStatement x);

    boolean visit(XuGuQ_EscapeExpr x);

    void endVisit(XuGuQ_EscapeExpr x);
}
