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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLParameter;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCreateProcedureStatement extends SQLStatementImpl implements SQLCreateStatement {

    private SQLName            definer;

    private boolean            create     = true;
    private boolean            orReplace;
    private SQLName            name;
    private SQLStatement       block;
    private List<SQLParameter> parameters = new ArrayList<SQLParameter>();

    // for oracle
    private String             javaCallSpec;

    private SQLName            authid;

    // for xugu
    private boolean            force;
    private boolean            exists;
    private SQLName            comment;
    private SQLName            languageWithC;
    private SQLName            languageWithPl;

    // for mysql
    private boolean            deterministic;
    private boolean            containsSql;
    private boolean            noSql;
    private boolean            readSqlData;
    private boolean            modifiesSqlData;

    private String             wrappedSource;

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, definer);
            acceptChild(visitor, name);
            acceptChild(visitor, parameters);
            acceptChild(visitor, block);
            acceptChild(visitor, comment);
            acceptChild(visitor, languageWithC);
            acceptChild(visitor, languageWithPl);
        }
        visitor.endVisit(this);
    }

    public List<SQLParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SQLParameter> parameters) {
        this.parameters = parameters;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLStatement getBlock() {
        return block;
    }

    public void setBlock(SQLStatement block) {
        if (block != null) {
            block.setParent(this);
        }
        this.block = block;
    }

    public SQLName getAuthid() {
        return authid;
    }

    public void setAuthid(SQLName authid) {
        if (authid != null) {
            authid.setParent(this);
        }
        this.authid = authid;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLName comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    public SQLName getLanguageWithC() {
        return languageWithC;
    }

    public void setLanguageWithC(SQLName languageWithC) {
        this.languageWithC = languageWithC;
    }

    public SQLName getLanguageWithPl() {
        return languageWithPl;
    }

    public void setLanguageWithPl(SQLName languageWithPl) {
        this.languageWithPl = languageWithPl;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public SQLName getDefiner() {
        return definer;
    }
    
    public void setDefiner(SQLName definer) {
        this.definer = definer;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public String getJavaCallSpec() {
        return javaCallSpec;
    }

    public void setJavaCallSpec(String javaCallSpec) {
        this.javaCallSpec = javaCallSpec;
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    public void setDeterministic(boolean deterministic) {
        this.deterministic = deterministic;
    }

    public boolean isContainsSql() {
        return containsSql;
    }

    public void setContainsSql(boolean containsSql) {
        this.containsSql = containsSql;
    }

    public boolean isNoSql() {
        return noSql;
    }

    public void setNoSql(boolean noSql) {
        this.noSql = noSql;
    }

    public boolean isReadSqlData() {
        return readSqlData;
    }

    public void setReadSqlData(boolean readSqlData) {
        this.readSqlData = readSqlData;
    }

    public boolean isModifiesSqlData() {
        return modifiesSqlData;
    }

    public void setModifiesSqlData(boolean modifiesSqlData) {
        this.modifiesSqlData = modifiesSqlData;
    }

    public SQLParameter findParameter(long hash) {
        for (SQLParameter param : this.parameters) {
            if (param.getName().nameHashCode64() == hash) {
                return param;
            }
        }

        return null;
    }

    public String getWrappedSource() {
        return wrappedSource;
    }

    public void setWrappedSource(String wrappedSource) {
        this.wrappedSource = wrappedSource;
    }
}
