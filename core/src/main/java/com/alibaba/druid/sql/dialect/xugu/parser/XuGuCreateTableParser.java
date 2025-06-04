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
package com.alibaba.druid.sql.dialect.xugu.parser;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLPartition;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByHash;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.ast.SQLPartitionByRange;
import com.alibaba.druid.sql.ast.SQLSubPartitionBy;
import com.alibaba.druid.sql.ast.SQLSubPartitionByHash;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyConstraint;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLTableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuForeignKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuPrimaryKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuUnique;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuOrderingExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateTableStatement;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuPartitionByKey;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuSubPartitionByKey;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuSubPartitionByList;
import com.alibaba.druid.sql.dialect.xugu.ast.statement.XuGuTableIndex;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class XuGuCreateTableParser extends SQLCreateTableParser {

    public XuGuCreateTableParser(String sql) {
        super(new XuGuExprParser(sql));
    }

    public XuGuCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLCreateTableStatement parseCreateTable() {
        return parseCreateTable(true);
    }

    public XuGuExprParser getExprParser() {
        return (XuGuExprParser) exprParser;
    }

    public XuGuCreateTableStatement parseCreateTable(boolean acceptCreate) {
        XuGuCreateTableStatement stmt = new XuGuCreateTableStatement();
        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                stmt.addBeforeComment(lexer.readAndResetComments());
            }
            accept(Token.CREATE);
        }

        if (lexer.nextIfIdentifier("TEMPORARY")) {
            stmt.config(SQLCreateTableStatement.Feature.Temporary);
        } else if (lexer.nextIfIdentifier("SHADOW")) {
            stmt.config(SQLCreateTableStatement.Feature.Shadow);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setLike(name);
        }

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                SQLName name = this.exprParser.name();
                stmt.setLike(name);
            } else if (lexer.token() == Token.SELECT) {
                SQLSelect query = new XuGuSelectParser(this.exprParser).select();
                stmt.setSelect(query);
            } else {
                for (; ; ) {
                    if (lexer.identifierEquals(FnvHash.Constants.FULLTEXT)) {
                        Lexer.SavePoint mark = lexer.mark();
                        lexer.nextToken();
                        if (lexer.token() == Token.KEY) {
                            XuGuKey fulltextKey = (XuGuKey) parseConstraint();
                            fulltextKey.setIndexType("FULLTEXT");
                            stmt.getTableElementList().add(fulltextKey);
                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        } else {
                            lexer.reset(mark);
                        }
                    } else if (lexer.identifierEquals(FnvHash.Constants.SPATIAL)) {
                        Lexer.SavePoint mark = lexer.mark();
                        lexer.nextToken();
                        if (lexer.token() == Token.INDEX) {
                            lexer.nextToken();
                            XuGuTableIndex idx = new XuGuTableIndex();
                            idx.setIndexType("SPATIAL");

                            if (lexer.token() == Token.IDENTIFIER) {
                                if (!"USING".equalsIgnoreCase(lexer.stringVal())) {
                                    idx.setName(this.exprParser.name());
                                }
                            }

                            if (lexer.identifierEquals("USING")) {
                                lexer.nextToken();
                                idx.setIndexType(lexer.stringVal());
                                lexer.nextToken();
                            }

                            accept(Token.LPAREN);
                            for (; ; ) {
                                idx.addColumn(this.exprParser.parseSelectOrderByItem());
                                if (!(lexer.token() == (Token.COMMA))) {
                                    break;
                                } else {
                                    lexer.nextToken();
                                }
                            }
                            accept(Token.RPAREN);

                            if (lexer.identifierEquals("USING")) {
                                lexer.nextToken();
                                idx.setIndexType(lexer.stringVal());
                                lexer.nextToken();
                            }

                            stmt.getTableElementList().add(idx);

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        } else {
                            lexer.reset(mark);
                        }
                    }

                    SQLColumnDefinition column = null;
                    if (lexer.token() == Token.IDENTIFIER //
                            || lexer.token() == Token.LITERAL_CHARS) {
                        column = this.exprParser.parseColumn();
                        stmt.getTableElementList().add(column);

                        if (lexer.isKeepComments() && lexer.hasComment()) {
                            column.addAfterComment(lexer.readAndResetComments());
                        }
                    } else if (lexer.token() == Token.CONSTRAINT //
                            || lexer.token() == Token.PRIMARY //
                            || lexer.token() == Token.UNIQUE) {
                        SQLTableConstraint constraint = this.parseConstraint();
                        constraint.setParent(stmt);
                        stmt.getTableElementList().add(constraint);
                    } else if (lexer.token() == (Token.INDEX)) {
                        lexer.nextToken();

                        XuGuTableIndex idx = new XuGuTableIndex();

                        if (lexer.token() == Token.IDENTIFIER) {
                            if (!"USING".equalsIgnoreCase(lexer.stringVal())) {
                                idx.setName(this.exprParser.name());
                            }
                        }

                        if (lexer.identifierEquals("USING")) {
                            lexer.nextToken();
                            idx.setIndexType(lexer.stringVal());
                            lexer.nextToken();
                        }

                        accept(Token.LPAREN);
                        for (; ; ) {
                            idx.addColumn(this.exprParser.parseSelectOrderByItem());
                            if (!(lexer.token() == (Token.COMMA))) {
                                break;
                            } else {
                                lexer.nextToken();
                            }
                        }
                        accept(Token.RPAREN);

                        if (lexer.identifierEquals("USING")) {
                            lexer.nextToken();
                            idx.setIndexType(lexer.stringVal());
                            lexer.nextToken();
                        }

                        stmt.getTableElementList().add(idx);
                    } else if (lexer.token() == (Token.KEY)) {
                        Lexer.SavePoint savePoint = lexer.mark();
                        lexer.nextToken();

                        boolean isColumn = false;
                        if (lexer.identifierEquals(FnvHash.Constants.VARCHAR)) {
                            isColumn = true;
                        }
                        lexer.reset(savePoint);

                        if (isColumn) {
                            column = this.exprParser.parseColumn();
                            stmt.getTableElementList().add(column);
                        } else {
                            stmt.getTableElementList().add(parseConstraint());
                        }
                    } else if (lexer.token() == (Token.PRIMARY)) {
                        SQLTableConstraint pk = parseConstraint();
                        pk.setParent(stmt);
                        stmt.getTableElementList().add(pk);
                    } else if (lexer.token() == (Token.FOREIGN)) {
                        SQLForeignKeyConstraint fk = this.getExprParser().parseForeignKey();
                        fk.setParent(stmt);
                        stmt.getTableElementList().add(fk);
                    } else if (lexer.token() == Token.CHECK) {
                        SQLCheck check = this.exprParser.parseCheck();
                        stmt.getTableElementList().add(check);
                    } else {
                        column = this.exprParser.parseColumn();
                        stmt.getTableElementList().add(column);
                    }

                    if (lexer.token() != Token.COMMA) {
                        break;
                    } else {
                        lexer.nextToken();
                        if (lexer.isKeepComments() && lexer.hasComment() && column != null) {
                            column.addAfterComment(lexer.readAndResetComments());
                        }
                    }
                }
            }

            accept(Token.RPAREN);
        }

        for (; ; ) {
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
            }

            if (lexer.identifierEquals("ENGINE")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLExpr expr = null;
                if (lexer.token() == Token.MERGE) {
                    expr = new SQLIdentifierExpr(lexer.stringVal());
                    lexer.nextToken();
                } else {
                    expr = this.exprParser.expr();
                }
                stmt.addOption("ENGINE", expr);
                continue;
            }

            if (lexer.identifierEquals("AUTO_INCREMENT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("AUTO_INCREMENT", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("AVG_ROW_LENGTH")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("AVG_ROW_LENGTH", this.exprParser.expr());
                continue;
            }

            if (lexer.token() == Token.DEFAULT) {
                lexer.nextToken();
                parseTableOptionCharsetOrCollate(stmt);
                continue;
            }

            if (parseTableOptionCharsetOrCollate(stmt)) {
                continue;
            }

            if (lexer.identifierEquals("CHECKSUM")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("CHECKSUM", this.exprParser.expr());
                continue;
            }

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.setComment(this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("CONNECTION")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("CONNECTION", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("DATA")) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("DATA DIRECTORY", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("DELAY_KEY_WRITE")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("DELAY_KEY_WRITE", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("INDEX")) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("INDEX DIRECTORY", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("INSERT_METHOD")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("INSERT_METHOD", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("KEY_BLOCK_SIZE")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("KEY_BLOCK_SIZE", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("MAX_ROWS")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("MAX_ROWS", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("MIN_ROWS")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("MIN_ROWS", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("PACK_KEYS")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("PACK_KEYS", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("PASSWORD")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("PASSWORD", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("ROW_FORMAT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("ROW_FORMAT", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("STATS_AUTO_RECALC")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                stmt.addOption("STATS_AUTO_RECALC", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("STATS_PERSISTENT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                stmt.addOption("STATS_PERSISTENT", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("STATS_SAMPLE_PAGES")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                stmt.addOption("STATS_SAMPLE_PAGES", this.exprParser.expr());
                continue;
            }

            if (lexer.token() == Token.UNION) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                accept(Token.LPAREN);
                SQLTableSource tableSrc = this.createSQLSelectParser().parseTableSource();
                stmt.addOption("UNION", (SQLExpr) tableSrc);
                accept(Token.RPAREN);
                continue;
            }

            if (lexer.token() == Token.TABLESPACE) {
                lexer.nextToken();

                TableSpaceOption option = new TableSpaceOption();
                option.setName(this.exprParser.name());

                if (lexer.identifierEquals("STORAGE")) {
                    lexer.nextToken();
                    option.setStorage(this.exprParser.name());
                }

                stmt.addOption("TABLESPACE", (SQLExpr) option);
                continue;
            }

            if (lexer.identifierEquals("TABLEGROUP")) {
                lexer.nextToken();

                SQLName tableGroup = this.exprParser.name();
                stmt.setTableGroup(tableGroup);
                continue;
            }

            if (lexer.identifierEquals("TYPE")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.addOption("TYPE", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("ENCRYPTION")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("ENCRYPTION", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.COMPRESSION)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("COMPRESSION", this.exprParser.expr());
                continue;
            }

            if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();
                accept(Token.BY);

                SQLPartitionBy partitionClause;

                boolean linera = false;
                if (lexer.identifierEquals("LINEAR")) {
                    lexer.nextToken();
                    linera = true;
                }

                if (lexer.token() == Token.KEY) {
                    XuGuPartitionByKey clause = new XuGuPartitionByKey();
                    lexer.nextToken();

                    if (linera) {
                        clause.setLinear(true);
                    }

                    if (lexer.identifierEquals(FnvHash.Constants.ALGORITHM)) {
                        lexer.nextToken();
                        accept(Token.EQ);
                        clause.setAlgorithm(lexer.integerValue().shortValue());
                        lexer.nextToken();
                    }

                    accept(Token.LPAREN);
                    if (lexer.token() != Token.RPAREN) {
                        for (; ; ) {
                            clause.addColumn(this.exprParser.name());
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                            break;
                        }
                    }
                    accept(Token.RPAREN);

                    partitionClause = clause;

                    partitionClauseRest(clause);
                } else if (lexer.identifierEquals("HASH")) {
                    lexer.nextToken();
                    SQLPartitionByHash clause = new SQLPartitionByHash();

                    if (linera) {
                        clause.setLinear(true);
                    }

                    if (lexer.token() == Token.KEY) {
                        lexer.nextToken();
                        clause.setKey(true);
                    }

                    accept(Token.LPAREN);
                    this.exprParser.exprList(clause.getColumns(), clause);
                    accept(Token.RPAREN);
                    partitionClause = clause;

                    partitionClauseRest(clause);

                } else if (lexer.identifierEquals("RANGE")) {
                    SQLPartitionByRange clause = partitionByRange();
                    partitionClause = clause;

                    partitionClauseRest(clause);

                } else if (lexer.identifierEquals("LIST")) {
                    lexer.nextToken();
                    SQLPartitionByList clause = new SQLPartitionByList();

                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        clause.addColumn(this.exprParser.expr());
                        accept(Token.RPAREN);
                    } else {
                        acceptIdentifier("COLUMNS");
                        accept(Token.LPAREN);
                        for (; ; ) {
                            clause.addColumn(this.exprParser.name());
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                            break;
                        }
                        accept(Token.RPAREN);
                    }
                    partitionClause = clause;

                    partitionClauseRest(clause);
                } else {
                    throw new ParserException("TODO. " + lexer.info());
                }

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    for (; ; ) {
                        SQLPartition partitionDef = this.getExprParser().parsePartition();

                        partitionClause.addPartition(partitionDef);

                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        } else {
                            break;
                        }
                    }
                    accept(Token.RPAREN);
                }

                stmt.setPartitionBy(partitionClause);

                continue;
            }

            break;
        }

        if (lexer.token() == (Token.ON)) {
            throw new ParserException("TODO. " + lexer.info());
        }

        if (lexer.token() == (Token.AS)) {
            lexer.nextToken();
        }

        if (lexer.token() == (Token.SELECT)) {
            SQLSelect query = new XuGuSelectParser(this.exprParser).select();
            stmt.setSelect(query);
        }

        while (lexer.token() == (Token.HINT)) {
            this.exprParser.parseHints(stmt.getOptionHints());
        }
        return stmt;
    }


    protected SQLPartitionByRange partitionByRange() {
        acceptIdentifier("RANGE");

        SQLPartitionByRange clause = new SQLPartitionByRange();

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            clause.addColumn(this.exprParser.expr());
            accept(Token.RPAREN);
        } else {
            acceptIdentifier("COLUMNS");
            accept(Token.LPAREN);
            for (; ; ) {
                clause.addColumn(this.exprParser.name());
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }
        return clause;
    }

    protected void partitionClauseRest(SQLPartitionBy clause) {
        if (lexer.identifierEquals("PARTITIONS")) {
            lexer.nextToken();

            SQLIntegerExpr countExpr = this.exprParser.integerExpr();
            clause.setPartitionsCount(countExpr);
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();

            if (lexer.identifierEquals("NUM")) {
                lexer.nextToken();
            }

            clause.setPartitionsCount(this.exprParser.expr());

            clause.putAttribute("ads.partition", Boolean.TRUE);
        }

        if (lexer.identifierEquals("SUBPARTITION")) {
            lexer.nextToken();
            accept(Token.BY);

            SQLSubPartitionBy subPartitionByClause = null;

            boolean linear = false;
            if (lexer.identifierEquals("LINEAR")) {
                lexer.nextToken();
                linear = true;
            }

            if (lexer.token() == Token.KEY) {
                XuGuSubPartitionByKey subPartitionKey = new XuGuSubPartitionByKey();
                lexer.nextToken();

                if (linear) {
                    clause.setLinear(true);
                }

                if (lexer.identifierEquals(FnvHash.Constants.ALGORITHM)) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    subPartitionKey.setAlgorithm(lexer.integerValue().shortValue());
                    lexer.nextToken();
                }

                accept(Token.LPAREN);
                for (; ; ) {
                    subPartitionKey.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);

                subPartitionByClause = subPartitionKey;

            } else if (lexer.identifierEquals("HASH")) {
                lexer.nextToken();
                SQLSubPartitionByHash subPartitionHash = new SQLSubPartitionByHash();

                if (linear) {
                    clause.setLinear(true);
                }

                if (lexer.token() == Token.KEY) {
                    lexer.nextToken();
                    subPartitionHash.setKey(true);
                }

                accept(Token.LPAREN);
                subPartitionHash.setExpr(this.exprParser.expr());
                accept(Token.RPAREN);
                subPartitionByClause = subPartitionHash;

            } else if (lexer.identifierEquals("LIST")) {
                lexer.nextToken();
                XuGuSubPartitionByList subPartitionList = new XuGuSubPartitionByList();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    SQLExpr expr = this.exprParser.expr();

                    if (expr instanceof SQLIdentifierExpr && (lexer.identifierEquals("bigint") || lexer.identifierEquals("long"))) {
                        String dataType = lexer.stringVal();
                        lexer.nextToken();

                        SQLColumnDefinition column = this.exprParser.createColumnDefinition();
                        column.setName((SQLIdentifierExpr) expr);
                        column.setDataType(new SQLDataTypeImpl(dataType));
                        subPartitionList.addColumn(column);

                        subPartitionList.putAttribute("ads.subPartitionList", Boolean.TRUE);
                    } else {
                        subPartitionList.setExpr(expr);
                    }
                    accept(Token.RPAREN);
                } else {
                    acceptIdentifier("COLUMNS");
                    accept(Token.LPAREN);
                    for (; ; ) {
                        subPartitionList.addColumn(this.exprParser.parseColumn());
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                }
                subPartitionByClause = subPartitionList;
            }

            if (lexer.identifierEquals("SUBPARTITION")) {
                lexer.nextToken();
                acceptIdentifier("OPTIONS");
                accept(Token.LPAREN);

                SQLAssignItem option = this.exprParser.parseAssignItem();
                accept(Token.RPAREN);

                option.setParent(subPartitionByClause);

                subPartitionByClause.getOptions().add(option);
            }

            if (lexer.identifierEquals("SUBPARTITIONS")) {
                lexer.nextToken();
                Number intValue = lexer.integerValue();
                SQLNumberExpr numExpr = new SQLNumberExpr(intValue);
                subPartitionByClause.setSubPartitionsCount(numExpr);
                lexer.nextToken();
            }

            if (subPartitionByClause != null) {
                subPartitionByClause.setLinear(linear);

                clause.setSubPartitionBy(subPartitionByClause);
            }
        }
    }

    private boolean parseTableOptionCharsetOrCollate(XuGuCreateTableStatement stmt) {
        if (lexer.identifierEquals("CHARACTER")) {
            lexer.nextToken();
            accept(Token.SET);
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.addOption("CHARACTER SET", this.exprParser.expr());
            return true;
        }

        if (lexer.identifierEquals("CHARSET")) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.addOption("CHARSET", this.exprParser.expr());
            return true;
        }

        if (lexer.identifierEquals("COLLATE")) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.addOption("COLLATE", this.exprParser.expr());
            return true;
        }

        return false;
    }

    protected SQLTableConstraint parseConstraint() {
        SQLName name = null;
        boolean hasConstaint = false;
        if (lexer.token() == (Token.CONSTRAINT)) {
            hasConstaint = true;
            lexer.nextToken();
        }

        if (lexer.token() == Token.IDENTIFIER) {
            name = this.exprParser.name();
        }

        SQLTableConstraint constraint = null;

        if (lexer.token() == (Token.KEY)) {
            lexer.nextToken();

            XuGuKey key = new XuGuKey();
            key.setHasConstaint(hasConstaint);

            // if (identifierEquals("USING")) {
            // lexer.nextToken();
            // key.setIndexType(lexer.stringVal());
            // lexer.nextToken();
            // }

            if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.LITERAL_ALIAS) {
                SQLName indexName = this.exprParser.name();
                if (indexName != null) {
                    key.setName(indexName);
                }
            }

            // 5.5语法 USING BTREE 放在index 名字后
            if (lexer.identifierEquals(FnvHash.Constants.USING)) {
                lexer.nextToken();
                key.setIndexType(lexer.stringVal());
                lexer.nextToken();
            }

            accept(Token.LPAREN);
            for (; ; ) {
                SQLExpr expr;
                if (lexer.token() == Token.LITERAL_ALIAS) {
                    expr = this.exprParser.name();
                    expr = this.exprParser.primaryRest(expr);
                } else {
                    expr = this.exprParser.expr();
                }
                if (lexer.token() == Token.ASC) {
                    lexer.nextToken();
                    expr = new XuGuOrderingExpr(expr, SQLOrderingSpecification.ASC);
                } else if (lexer.token() == Token.DESC) {
                    lexer.nextToken();
                    expr = new XuGuOrderingExpr(expr, SQLOrderingSpecification.DESC);
                }

                key.addColumn(expr);
                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);

            if (name != null) {
                key.setName(name);
            }

            if (lexer.identifierEquals(FnvHash.Constants.USING)) {
                lexer.nextToken();
                key.setIndexType(lexer.stringVal());
                lexer.nextToken();
            }

            constraint = key;
        } else if (lexer.token() == Token.PRIMARY) {
            XuGuPrimaryKey pk = this.getExprParser().parsePrimaryKey();
            if (name != null) {
                pk.setName(name);
            }
            pk.setHasConstaint(hasConstaint);
            constraint = pk;
        } else if (lexer.token() == Token.UNIQUE) {
            XuGuUnique uk = this.getExprParser().parseUnique();
            if (name != null) {
                uk.setName(name);
            }
            uk.setHasConstaint(hasConstaint);

            constraint = uk;
        } else if (lexer.token() == Token.FOREIGN) {
            XuGuForeignKey fk = this.getExprParser().parseForeignKey();
            fk.setName(name);
            fk.setHasConstraint(hasConstaint);
            constraint = fk;
        } else if (lexer.token() == Token.CHECK) {
            lexer.nextToken();
            SQLCheck check = new SQLCheck();
            check.setName(name);
            SQLExpr expr = this.exprParser.expr();
            check.setExpr(expr);
            constraint = check;
        }

        if (constraint != null) {
            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                SQLExpr comment = this.exprParser.primary();
                constraint.setComment(comment);
            }

            return constraint;
        }

        throw new ParserException("TODO. " + lexer.info());
    }
}
