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
import com.alibaba.druid.sql.dialect.xugu.ast.clause.XuGuSelectIntoStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuCharExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuExtractExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuOrderingExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuOutFileExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuUserName;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.*;
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

    void endVisit(XuGuShowColumnsStatement x);

    boolean visit(XuGuShowColumnsStatement x);

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

    boolean visit(XuGuShowVariantsStatement x);

    void endVisit(XuGuShowVariantsStatement x);

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
}
