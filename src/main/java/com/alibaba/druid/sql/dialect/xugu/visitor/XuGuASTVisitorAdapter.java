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

import com.alibaba.druid.sql.ast.statement.SQLAlterCharacter;
import com.alibaba.druid.sql.ast.expr.SQLIntervalExpr;
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
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class XuGuASTVisitorAdapter extends SQLASTVisitorAdapter implements XuGuASTVisitor {

    @Override
    public boolean visit(XuGuTableIndex x) {
        return true;
    }

    @Override
    public void endVisit(XuGuTableIndex x) {

    }

    @Override
    public boolean visit(XuGuKey x) {
        return true;
    }

    @Override
    public void endVisit(XuGuKey x) {

    }

    @Override
    public boolean visit(XuGuPrimaryKey x) {

        return true;
    }

    @Override
    public void endVisit(XuGuPrimaryKey x) {

    }

    @Override
    public void endVisit(SQLIntervalExpr x) {
    }

    @Override
    public boolean visit(SQLIntervalExpr x) {
        return true;
    }

    @Override
    public void endVisit(XuGuExtractExpr x) {

    }

    @Override
    public boolean visit(XuGuExtractExpr x) {

        return true;
    }

    @Override
    public void endVisit(XuGuMatchAgainstExpr x) {

    }

    @Override
    public boolean visit(XuGuMatchAgainstExpr x) {

        return true;
    }

    @Override
    public void endVisit(XuGuPrepareStatement x) {

    }

    @Override
    public boolean visit(XuGuPrepareStatement x) {

        return true;
    }

    @Override
    public void endVisit(XuGuExecuteStatement x) {

    }

    @Override
    public boolean visit(XuGuExecuteStatement x) {

        return true;
    }
    
    @Override
    public void endVisit(XuGuDeallocatePrepareStatement x) {
    	
    }
    
    @Override
    public boolean visit(XuGuDeallocatePrepareStatement x) {
    	return true;
    }

    @Override
    public void endVisit(XuGuDeleteStatement x) {

    }

    @Override
    public boolean visit(XuGuDeleteStatement x) {

        return true;
    }

    @Override
    public void endVisit(XuGuInsertStatement x) {

    }

    @Override
    public boolean visit(XuGuInsertStatement x) {

        return true;
    }

    @Override
    public void endVisit(XuGuLoadDataInFileStatement x) {

    }

    @Override
    public boolean visit(XuGuLoadDataInFileStatement x) {

        return true;
    }

    @Override
    public void endVisit(XuGuLoadXmlStatement x) {

    }

    @Override
    public boolean visit(XuGuLoadXmlStatement x) {

        return true;
    }

    @Override
    public void endVisit(XuGuShowColumnsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowColumnsStatement x) {

        return true;
    }

    @Override
    public void endVisit(XuGuShowDatabasesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowDatabasesStatement x) {

        return true;
    }

    @Override
    public void endVisit(XuGuShowWarningsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowWarningsStatement x) {

        return true;
    }

    @Override
    public void endVisit(XuGuShowStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowStatusStatement x) {

        return true;
    }

    @Override
    public void endVisit(CobarShowStatus x) {

    }

    @Override
    public boolean visit(CobarShowStatus x) {
        return true;
    }

    @Override
    public void endVisit(XuGuKillStatement x) {

    }

    @Override
    public boolean visit(XuGuKillStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuBinlogStatement x) {

    }

    @Override
    public boolean visit(XuGuBinlogStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuResetStatement x) {

    }

    @Override
    public boolean visit(XuGuResetStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCreateUserStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(UserSpecification x) {

    }

    @Override
    public boolean visit(UserSpecification x) {
        return true;
    }

    @Override
    public void endVisit(XuGuPartitionByKey x) {

    }

    @Override
    public boolean visit(XuGuPartitionByKey x) {
        return true;
    }

    @Override
    public boolean visit(XuGuSelectQueryBlock x) {
        return true;
    }

    @Override
    public void endVisit(XuGuSelectQueryBlock x) {

    }

    @Override
    public boolean visit(XuGuOutFileExpr x) {
        return true;
    }

    @Override
    public void endVisit(XuGuOutFileExpr x) {

    }

    @Override
    public boolean visit(XuGuExplainStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuExplainStatement x) {

    }

    @Override
    public boolean visit(XuGuUpdateStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuUpdateStatement x) {

    }

    @Override
    public boolean visit(XuGuSetTransactionStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuSetTransactionStatement x) {

    }

    @Override
    public boolean visit(XuGuShowAuthorsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowAuthorsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowBinaryLogsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowBinaryLogsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowMasterLogsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowMasterLogsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCollationStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowCollationStatement x) {

    }

    @Override
    public boolean visit(XuGuShowBinLogEventsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowBinLogEventsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCharacterSetStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowCharacterSetStatement x) {

    }

    @Override
    public boolean visit(XuGuShowContributorsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowContributorsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowCreateDatabaseStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateEventStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowCreateEventStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowCreateTableStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowCreateTriggerStatement x) {

    }

    @Override
    public boolean visit(XuGuShowCreateViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowCreateViewStatement x) {

    }

    @Override
    public boolean visit(XuGuShowEngineStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowEngineStatement x) {

    }

    @Override
    public boolean visit(XuGuShowEnginesStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowEnginesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowErrorsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowErrorsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowEventsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowEventsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowFunctionCodeStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowFunctionCodeStatement x) {

    }

    @Override
    public boolean visit(XuGuShowFunctionStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowFunctionStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowGrantsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowGrantsStatement x) {
    }

    @Override
    public boolean visit(XuGuUserName x) {
        return true;
    }

    @Override
    public void endVisit(XuGuUserName x) {

    }

    @Override
    public boolean visit(XuGuShowIndexesStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowIndexesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowKeysStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowKeysStatement x) {

    }

    @Override
    public boolean visit(XuGuShowMasterStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowMasterStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowOpenTablesStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowOpenTablesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowPluginsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowPluginsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowPrivilegesStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowPrivilegesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProcedureCodeStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowProcedureCodeStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProcedureStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowProcedureStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProcessListStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowProcessListStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProfileStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowProfileStatement x) {

    }

    @Override
    public boolean visit(XuGuShowProfilesStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowProfilesStatement x) {

    }

    @Override
    public boolean visit(XuGuShowRelayLogEventsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowRelayLogEventsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowSlaveHostsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowSlaveHostsStatement x) {

    }

    @Override
    public boolean visit(XuGuShowSlaveStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowSlaveStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowTableStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowTableStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuShowTriggersStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowTriggersStatement x) {

    }

    @Override
    public boolean visit(XuGuShowVariantsStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowVariantsStatement x) {

    }

    @Override
    public boolean visit(XuGuRenameTableStatement.Item x) {
        return true;
    }

    @Override
    public void endVisit(XuGuRenameTableStatement.Item x) {

    }

    @Override
    public boolean visit(XuGuRenameTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuRenameTableStatement x) {

    }

    @Override
    public boolean visit(XuGuUseIndexHint x) {
        return true;
    }

    @Override
    public void endVisit(XuGuUseIndexHint x) {

    }

    @Override
    public boolean visit(XuGuIgnoreIndexHint x) {
        return true;
    }

    @Override
    public void endVisit(XuGuIgnoreIndexHint x) {

    }

    @Override
    public boolean visit(XuGuLockTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuLockTableStatement x) {

    }

    @Override
    public boolean visit(XuGuUnlockTablesStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuUnlockTablesStatement x) {

    }

    @Override
    public boolean visit(XuGuForceIndexHint x) {
        return true;
    }

    @Override
    public void endVisit(XuGuForceIndexHint x) {

    }

    @Override
    public boolean visit(XuGuAlterTableChangeColumn x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterTableChangeColumn x) {

    }

    @Override
    public boolean visit(SQLAlterCharacter x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterCharacter x) {

    }

    @Override
    public boolean visit(XuGuAlterTableOption x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterTableOption x) {

    }

    @Override
    public boolean visit(XuGuCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCreateTableStatement x) {

    }

    @Override
    public boolean visit(XuGuHelpStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuHelpStatement x) {

    }

    @Override
    public boolean visit(XuGuCharExpr x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCharExpr x) {

    }

    @Override
    public boolean visit(XuGuUnique x) {
        return true;
    }

    @Override
    public void endVisit(XuGuUnique x) {

    }

    @Override
    public boolean visit(XuGuForeignKey x) {
        return true;
    }

    @Override
    public void endVisit(XuGuForeignKey x) {

    }

    @Override
    public boolean visit(XuGuAlterTableModifyColumn x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterTableModifyColumn x) {

    }

    @Override
    public boolean visit(XuGuAlterTableDiscardTablespace x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterTableDiscardTablespace x) {

    }

    @Override
    public boolean visit(XuGuAlterTableImportTablespace x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterTableImportTablespace x) {

    }

    @Override
    public boolean visit(TableSpaceOption x) {
        return true;
    }

    @Override
    public void endVisit(TableSpaceOption x) {

    }

    @Override
    public boolean visit(XuGuAnalyzeStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAnalyzeStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterUserStatement x) {

    }

    @Override
    public boolean visit(XuGuOptimizeStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuOptimizeStatement x) {

    }

    @Override
    public boolean visit(XuGuHintStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuHintStatement x) {

    }

    @Override
    public boolean visit(XuGuOrderingExpr x) {
        return true;
    }

    @Override
    public void endVisit(XuGuOrderingExpr x) {

    }

    @Override
    public boolean visit(XuGuCaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCaseStatement x) {

    }

    @Override
    public boolean visit(XuGuDeclareStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDeclareStatement x) {

    }

    @Override
    public boolean visit(XuGuSelectIntoStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuSelectIntoStatement x) {

    }

    @Override
    public boolean visit(XuGuWhenStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuWhenStatement x) {

    }
    // add:end

    @Override
    public boolean visit(XuGuLeaveStatement x) {
        return false;
    }

    @Override
    public void endVisit(XuGuLeaveStatement x) {

    }

    @Override
    public boolean visit(XuGuIterateStatement x) {
        return false;
    }

    @Override
    public void endVisit(XuGuIterateStatement x) {

    }

    @Override
    public boolean visit(XuGuRepeatStatement x) {
        return false;
    }

    @Override
    public void endVisit(XuGuRepeatStatement x) {

    }

    @Override
    public boolean visit(XuGuCursorDeclareStatement x) {
        return false;
    }

    @Override
    public void endVisit(XuGuCursorDeclareStatement x) {

    }

    @Override
    public boolean visit(XuGuUpdateTableSource x) {
        return true;
    }

    @Override
    public void endVisit(XuGuUpdateTableSource x) {

    }

    @Override
    public boolean visit(XuGuAlterTableAlterColumn x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterTableAlterColumn x) {

    }

    @Override
    public boolean visit(XuGuSubPartitionByKey x) {
        return true;
    }

    @Override
    public void endVisit(XuGuSubPartitionByKey x) {

    }
    
    @Override
    public boolean visit(XuGuSubPartitionByList x) {
        return true;
    }
    
    @Override
    public void endVisit(XuGuSubPartitionByList x) {
    }

	@Override
	public boolean visit(XuGuDeclareHandlerStatement x) {
		return false;
	}

	@Override
	public void endVisit(XuGuDeclareHandlerStatement x) {

	}

	@Override
	public boolean visit(XuGuDeclareConditionStatement x) {
		return false;
	}

	@Override
	public void endVisit(XuGuDeclareConditionStatement x) {

	}

	@Override
	public boolean visit(XuGuFlushStatement x) {
		return false;
	}

	@Override
	public void endVisit(XuGuFlushStatement x) {

	}

    @Override
    public boolean visit(XuGuEventSchedule x) {
        return true;
    }

    @Override
    public void endVisit(XuGuEventSchedule x) {

    }

    @Override
    public boolean visit(XuGuCreateEventStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCreateEventStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateAddLogFileGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCreateAddLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateServerStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCreateServerStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateTableSpaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCreateTableSpaceStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterEventStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterEventStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterLogFileGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterServerStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterServerStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterTablespaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterTablespaceStatement x) {

    }

    @Override
    public boolean visit(XuGuShowDatabasePartitionStatusStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuShowDatabasePartitionStatusStatement x) {

    }

    @Override
    public boolean visit(XuGuChecksumTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuChecksumTableStatement x) {

    }

    @Override
    public boolean visit(XuGuBackupSystemDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuBackupSystemDatabaseStatement x) {

    }

    @Override
    public boolean visit(XuGuBackupUserSchemaTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuBackupUserSchemaTableStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreSystemStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuRestoreSystemStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuRestoreDatabaseStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuRestoreUserStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreSchemaStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuRestoreSchemaStatement x) {

    }

    @Override
    public boolean visit(XuGuRestoreTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuRestoreTableStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterDatabaseStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateSchemaStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCreateSchemaStatement x) {

    }

    @Override
    public boolean visit(XuGuDropSchemaStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDropSchemaStatement x) {

    }

    @Override
    public boolean visit(XuGuAlterSchemaStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuAlterSchemaStatement x) {

    }

    @Override
    public boolean visit(XuGuCreateTypeStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuCreateTypeStatement x) {

    }

    @Override
    public boolean visit(XuGuForStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuForStatement x) {

    }

    @Override
    public boolean visit(XuGuRangeExpr x) {
        return true;
    }

    @Override
    public void endVisit(XuGuRangeExpr x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalDay x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalDay x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalYearToMonth x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalYearToMonth x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalYear x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalYear x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalMonth x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalMonth x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalDayToHour x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalDayToHour x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalDayToMinute x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalDayToMinute x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalDayToSecond x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalDayToSecond x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalHour x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalHour x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalHourToMinute x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalHourToMinute x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalHourToSecond x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalHourToSecond x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalMinute x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalMinute x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalMinuteToSecond x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalMinuteToSecond x) {

    }

    @Override
    public boolean visit(XuGuDataTypeIntervalSecond x) {
        return true;
    }

    @Override
    public void endVisit(XuGuDataTypeIntervalSecond x) {

    }

    @Override
    public boolean visit(XuGuReturningClause x) {
        return true;
    }

    @Override
    public void endVisit(XuGuReturningClause x) {

    }

    @Override
    public boolean visit(XuGuExitStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuExitStatement x) {

    }

    @Override
    public boolean visit(XuGuMultiInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(XuGuMultiInsertStatement x) {

    }

    @Override
    public boolean visit(XuGuMultiInsertStatement.ConditionalInsertClause x) {
        return true;
    }

    @Override
    public void endVisit(XuGuMultiInsertStatement.ConditionalInsertClause x) {

    }

    @Override
    public boolean visit(XuGuMultiInsertStatement.ConditionalInsertClauseItem x) {
        return true;
    }

    @Override
    public void endVisit(XuGuMultiInsertStatement.ConditionalInsertClauseItem x) {

    }

    @Override
    public boolean visit(XuGuMultiInsertStatement.InsertIntoClause x) {
        return true;
    }

    @Override
    public void endVisit(XuGuMultiInsertStatement.InsertIntoClause x) {

    }
}
