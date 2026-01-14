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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl.*;
import com.alibaba.druid.sql.ast.expr.SQLIntervalExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntervalUnit;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalDay;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalDayToHour;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalDayToMinute;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalDayToSecond;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalHour;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalHourToMinute;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalHourToSecond;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalMinute;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalMinuteToSecond;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalSecond;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalYear;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalYearToMonth;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuDataTypeIntervalMonth;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuMatchAgainstExpr.SearchModifier;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuPrimaryKey;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuUnique;
import com.alibaba.druid.sql.dialect.xugu.ast.XuGuForeignKey;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuCharExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuExtractExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuOrderingExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuQ_EscapeExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuRangeExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuTypeCastExpr;
import com.alibaba.druid.sql.dialect.xugu.ast.expr.XuGuUserName;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

import java.util.Arrays;

public class XuGuExprParser extends SQLExprParser {
    public final static String[] AGGREGATE_FUNCTIONS;

    public final static long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = { "AVG", "COUNT", "GROUP_CONCAT", "MAX", "MIN", "STDDEV", "SUM" };
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public XuGuExprParser(Lexer lexer){
        super(lexer, JdbcConstants.XUGU);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public XuGuExprParser(String sql){
        this(new XuGuLexer(sql));
        this.lexer.nextToken();
    }

    public XuGuExprParser(String sql, SQLParserFeature... features){
        super(new XuGuLexer(sql, features), JdbcConstants.XUGU);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
        if (sql.length() > 6) {
            char c0 = sql.charAt(0);
            char c1 = sql.charAt(1);
            char c2 = sql.charAt(2);
            char c3 = sql.charAt(3);
            char c4 = sql.charAt(4);
            char c5 = sql.charAt(5);
            char c6 = sql.charAt(6);

            if (c0 == 'S' && c1 == 'E' && c2 == 'L' && c3 == 'E' && c4 == 'C' && c5 == 'T' && c6 == ' ') {
                lexer.reset(6, ' ', Token.SELECT);
                return;
            }

            if (c0 == 's' && c1 == 'e' && c2 == 'l' && c3 == 'e' && c4 == 'c' && c5 == 't' && c6 == ' ') {
                lexer.reset(6, ' ', Token.SELECT);
                return;
            }

            if (c0 == 'I' && c1 == 'N' && c2 == 'S' && c3 == 'E' && c4 == 'R' && c5 == 'T' && c6 == ' ') {
                lexer.reset(6, ' ', Token.INSERT);
                return;
            }

            if (c0 == 'i' && c1 == 'n' && c2 == 's' && c3 == 'e' && c4 == 'r' && c5 == 't' && c6 == ' ') {
                lexer.reset(6, ' ', Token.INSERT);
                return;
            }

            if (c0 == 'U' && c1 == 'P' && c2 == 'D' && c3 == 'A' && c4 == 'T' && c5 == 'E' && c6 == ' ') {
                lexer.reset(6, ' ', Token.UPDATE);
                return;
            }

            if (c0 == 'u' && c1 == 'p' && c2 == 'd' && c3 == 'a' && c4 == 't' && c5 == 'e' && c6 == ' ') {
                lexer.reset(6, ' ', Token.UPDATE);
                return;
            }

            if (c0 == '/' && c1 == '*' && isEnabled(SQLParserFeature.OptimizedForParameterized)) {
                XuGuLexer xuGuLexer = (XuGuLexer) lexer;
                xuGuLexer.skipFirstHintsOrMultiCommentAndNextToken();
                return;
            }
        }
        this.lexer.nextToken();

    }

    public XuGuExprParser(String sql, boolean keepComments){
        this(new XuGuLexer(sql, true, keepComments));
        this.lexer.nextToken();
    }


    public XuGuExprParser(String sql, boolean skipComment, boolean keepComments){
        this(new XuGuLexer(sql, skipComment, keepComments));
        this.lexer.nextToken();
    }

    @Override
    protected boolean isCharType(long hash) {
        return hash == FnvHash.Constants.CHAR
                || hash == FnvHash.Constants.NCHAR
                || hash == FnvHash.Constants.VARCHAR
                || hash == FnvHash.Constants.VARCHAR2
                || hash == FnvHash.Constants.NVARCHAR
                || hash == FnvHash.Constants.NVARCHAR2
                || hash == FnvHash.Constants.CLOB
                ;
    }

    @Override
    public SQLDataType parseDataType(boolean restrict) {

        if (lexer.token() == Token.CONSTRAINT || lexer.token() == Token.COMMA) {
            return null;
        }

        if (lexer.token() == Token.DEFAULT || lexer.token() == Token.NOT || lexer.token() == Token.NULL) {
            return null;
        }

        if (lexer.token() == Token.INTERVAL) {
            lexer.nextToken();
            if (lexer.identifierEquals("YEAR")) {
                lexer.nextToken();
                XuGuDataTypeIntervalYear interval = new XuGuDataTypeIntervalYear();
                XuGuDataTypeIntervalYearToMonth intervalYearToMonth = new XuGuDataTypeIntervalYearToMonth();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    interval.addArgument(this.expr());
                    accept(Token.RPAREN);
                }
                if (lexer.token() != Token.TO) {
                    return interval;
                } else {
                    accept(Token.TO);
                    acceptIdentifier("MONTH");
                    for (SQLExpr sqlExpr : interval.getArguments()) {
                        intervalYearToMonth.addArgument(sqlExpr);
                    }
                    // intervalYearToMonth.addArgument(interval.get);
                }
                return intervalYearToMonth;
            } else if (lexer.identifierEquals("MONTH")) {
                lexer.nextToken();
                XuGuDataTypeIntervalMonth interval = new XuGuDataTypeIntervalMonth();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    interval.addArgument(this.expr());
                    accept(Token.RPAREN);
                }
                return interval;
            } else if (lexer.identifierEquals("DAY")) {
                lexer.nextToken();
                XuGuDataTypeIntervalDay interval = new XuGuDataTypeIntervalDay();
                XuGuDataTypeIntervalDayToHour intervalDayToHour = new XuGuDataTypeIntervalDayToHour();
                XuGuDataTypeIntervalDayToMinute intervalDayToMinute = new XuGuDataTypeIntervalDayToMinute();
                XuGuDataTypeIntervalDayToSecond intervalDayToSecond = new XuGuDataTypeIntervalDayToSecond();
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    interval.addArgument(this.expr());
                    accept(Token.RPAREN);
                }
                if (lexer.token() != Token.TO) {
                    return interval;
                }
                accept(Token.TO);
                if (lexer.identifierEquals("HOUR")) {
                    lexer.nextToken();
                    for (SQLExpr argument : interval.getArguments()) {
                        intervalDayToHour.addArgument(argument);
                    }
                    return intervalDayToHour;
                } else if (lexer.identifierEquals("MINUTE")) {
                    lexer.nextToken();
                    for (SQLExpr argument : interval.getArguments()) {
                        intervalDayToMinute.addArgument(argument);
                    }
                    return intervalDayToMinute;
                } else {
                    acceptIdentifier("SECOND");
                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        for (SQLExpr argument : interval.getArguments()) {
                            intervalDayToSecond.addArgument(argument);
                        }
                        // intervalDayToSecond.addArgument(this.expr());
                        intervalDayToSecond.getFractionalSeconds().add(this.expr());
                        accept(Token.RPAREN);
                    }
                    return intervalDayToSecond;
                }
            } else if (lexer.identifierEquals("HOUR")) {
                lexer.nextToken();
                XuGuDataTypeIntervalHour interval = new XuGuDataTypeIntervalHour();
                XuGuDataTypeIntervalHourToMinute intervalHourToMinute = new XuGuDataTypeIntervalHourToMinute();
                XuGuDataTypeIntervalHourToSecond intervalHourToSecond = new XuGuDataTypeIntervalHourToSecond();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    interval.addArgument(this.expr());
                    accept(Token.RPAREN);
                }
                if (lexer.token() != Token.TO) {
                    return interval;
                }
                accept(Token.TO);
                if (lexer.identifierEquals("MINUTE")) {
                    lexer.nextToken();
                    for (SQLExpr argument : interval.getArguments()) {
                        intervalHourToMinute.addArgument(argument);
                    }
                    return intervalHourToMinute;
                } else {
                    acceptIdentifier("SECOND");
                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        for (SQLExpr argument : interval.getArguments()) {
                            intervalHourToSecond.addArgument(argument);
                        }
                        // intervalHourToSecond.addArgument(this.expr());
                        intervalHourToSecond.getFractionalSeconds().add(this.expr());
                        accept(Token.RPAREN);
                    }
                    return intervalHourToSecond;
                }

            } else if (lexer.identifierEquals("MINUTE")) {
                lexer.nextToken();
                XuGuDataTypeIntervalMinute interval = new XuGuDataTypeIntervalMinute();
                XuGuDataTypeIntervalMinuteToSecond intervalMinuteToSecond = new XuGuDataTypeIntervalMinuteToSecond();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    interval.addArgument(this.expr());
                    accept(Token.RPAREN);
                }
                if (lexer.token() != Token.TO) {
                    return interval;
                } else {
                    accept(Token.TO);
                    acceptIdentifier("SECOND");
                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        for (SQLExpr argument : interval.getArguments()) {
                            intervalMinuteToSecond.addArgument(argument);
                        }
                        // intervalMinuteToSecond.addArgument(this.expr());
                        intervalMinuteToSecond.getFractionalSeconds().add(this.expr());
                        accept(Token.RPAREN);
                    }
                }
                return intervalMinuteToSecond;
            } else if (lexer.identifierEquals("SECOND")) {
                lexer.nextToken();
                XuGuDataTypeIntervalSecond interval = new XuGuDataTypeIntervalSecond();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    interval.addArgument(this.expr());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        interval.addArgument(this.expr());
                    }
                    accept(Token.RPAREN);
                }
                return interval;
            }
        }

        String typeName;
        if (lexer.token() == Token.EXCEPTION) {
            typeName = "EXCEPTION";
            lexer.nextToken();
        } else if (lexer.identifierEquals(FnvHash.Constants.LONG)) {
            lexer.nextToken();

            if (lexer.identifierEquals(FnvHash.Constants.RAW)) {
                lexer.nextToken();
                typeName = "LONG RAW";
            } else {
                typeName = "LONG";
            }
        } else if (lexer.token() == Token.ROW) {
            lexer.nextToken();
            acceptIdentifier("TYPE");
            accept(Token.OF);
            typeName = "ROW TYPE OF " + name();
        } else if (lexer.identifierEquals("ROWTYPE")) {
            lexer.nextToken();
            accept(Token.OF);
            typeName = "ROWTYPE OF " + name();
        } else if (lexer.identifierEquals("TYPE")) {
            lexer.nextToken();
            accept(Token.OF);
            typeName = "TYPE OF " + name();
        } else if (lexer.identifierEquals("REF")) {
            lexer.nextToken();
            accept(Token.CURSOR);
            typeName = "REF CURSOR";
        } else if (lexer.identifierEquals(FnvHash.Constants.RECORD)) {
            lexer.nextToken();
            SQLRecordDataType recordDataType = new SQLRecordDataType();
            recordDataType.setName("RECORD");
            accept(Token.LPAREN);
            for (; ; ) {
                SQLColumnDefinition column = parseColumn();
                recordDataType.addColumn(column);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
            return recordDataType;
        } else {
            SQLName typeExpr = name();
            typeName = typeExpr.toString();
        }

        if ("TIMESTAMP".equalsIgnoreCase(typeName)) {
            SQLDataTypeImpl timestamp = new SQLDataTypeImpl(typeName);
            timestamp.setDbType(dbType);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                timestamp.addArgument(this.expr());
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.WITH) {
                lexer.nextToken();

                if (lexer.identifierEquals("LOCAL")) {
                    lexer.nextToken();
                    timestamp.setWithLocalTimeZone(true);
                }

                timestamp.setWithTimeZone(true);

                acceptIdentifier("TIME");
                acceptIdentifier("ZONE");
            }

            return timestamp;
        }

        if ("TIME".equalsIgnoreCase(typeName) || "DATETIME" .equalsIgnoreCase(typeName)) {
            SQLDataTypeImpl dataType = new SQLDataTypeImpl(typeName);
            dataType.setDbType(dbType);
            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                dataType.setWithTimeZone(true);
                acceptIdentifier("TIME");
                acceptIdentifier("ZONE");
            }
            return dataType;
        }

        if ("NUMBER".equalsIgnoreCase(typeName)) {
            if (lexer.token() == Token.LPAREN) {
                accept(Token.LPAREN);
                int numLen = acceptInteger();
                accept(Token.RPAREN);
                typeName += "(" + numLen + ")";
            }
            SQLDataTypeImpl dataType = new SQLDataTypeImpl(typeName);
            dataType.setDbType(dbType);
            return dataType;
        }

        if (isCharType(typeName)) {
            SQLCharacterDataType charType = new SQLCharacterDataType(typeName);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                charType.addArgument(this.expr());

                if (lexer.identifierEquals("CHAR")) {
                    lexer.nextToken();
                    charType.setCharType(SQLCharacterDataType.CHAR_TYPE_CHAR);
                } else if (lexer.identifierEquals("BYTE")) {
                    lexer.nextToken();
                    charType.setCharType(SQLCharacterDataType.CHAR_TYPE_BYTE);
                }

                accept(Token.RPAREN);
            }

            return parseCharTypeRest(charType);
        }

        if (lexer.token() == Token.PERCENT) {
            lexer.nextToken();
            if (lexer.identifierEquals("TYPE")) {
                lexer.nextToken();
                typeName += "%TYPE";
            } else if (lexer.identifierEquals("ROWTYPE")) {
                lexer.nextToken();
                typeName += "%ROWTYPE";
            } else if (lexer.token() == Token.ROW) {
                lexer.nextToken();
                acceptIdentifier("TYPE");
                typeName += "%ROW TYPE";
            } else {
                throw new ParserException("syntax error : " + lexer.info());
            }
        }


        SQLDataTypeImpl dataType = new SQLDataTypeImpl(typeName);
        dataType.setDbType(dbType);
        return parseDataTypeRest(dataType);
    }

    public SQLExpr primary() {
        final Token tok = lexer.token();

        switch (tok) {
            case VARIANT:
                SQLVariantRefExpr varRefExpr = new SQLVariantRefExpr(lexer.stringVal());
                lexer.nextToken();
                if (varRefExpr.getName().equalsIgnoreCase("@@global")) {
                    accept(Token.DOT);
                    varRefExpr = new SQLVariantRefExpr(lexer.stringVal(), true);
                    lexer.nextToken();
                } else if (varRefExpr.getName().equals("@") && lexer.token() == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                } else if (varRefExpr.getName().equals("@@") && lexer.token() == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                }
                return primaryRest(varRefExpr);
            case VALUES:
                lexer.nextToken();
                if (lexer.token() != Token.LPAREN) {
                    throw new ParserException("syntax error, illegal values clause. " + lexer.info());
                }
                return this.methodRest(new SQLIdentifierExpr("VALUES"), true);
            case BINARY:
                lexer.nextToken();
                if (lexer.token() == Token.COMMA || lexer.token() == Token.SEMI || lexer.token() == Token.EOF) {
                    return new SQLIdentifierExpr("BINARY");
                } else {
                    SQLUnaryExpr binaryExpr = new SQLUnaryExpr(SQLUnaryOperator.BINARY, expr());
                    return primaryRest(binaryExpr);
                }
            case PRIOR:
                lexer.nextToken();
                SQLExpr sqlExpr = expr();
                sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Prior, sqlExpr);
                return primaryRest(sqlExpr);
            case Q_ESCAPE:
                XuGuQ_EscapeExpr xuGuQEscapeExpr = new XuGuQ_EscapeExpr(lexer.stringVal());
                lexer.nextToken();
                return xuGuQEscapeExpr;
            case LITERAL_ALIAS:
                // xugu 双引号"id" 默认为 字段变量或函数
                String alias = lexer.stringVal();
                lexer.nextToken();
                return primaryRest(new SQLIdentifierExpr(alias));
            default:
                return super.primary();
        }

    }

    public final SQLExpr primaryRest(SQLExpr expr) {
        if (expr == null) {
            throw new IllegalArgumentException("expr");
        }

        if (lexer.token() == Token.LITERAL_CHARS) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                String ident = identExpr.getName();

                if (ident.equalsIgnoreCase("x")) {
                    String charValue = lexer.stringVal();
                    lexer.nextToken();
                    expr = new SQLHexExpr(charValue);

                    return primaryRest(expr);
//                } else if (ident.equalsIgnoreCase("b")) {
//                    String charValue = lexer.stringVal();
//                    lexer.nextToken();
//                    expr = new SQLBinaryExpr(charValue);
//
//                    return primaryRest(expr);
                } else if (ident.startsWith("_")) {
                    String charValue = lexer.stringVal();
                    lexer.nextToken();

                    XuGuCharExpr xgCharExpr = new XuGuCharExpr(charValue);
                    xgCharExpr.setCharset(identExpr.getName());
                    if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
                        lexer.nextToken();

                        String collate = lexer.stringVal();
                        xgCharExpr.setCollate(collate);
                        accept(Token.IDENTIFIER);
                    }

                    expr = xgCharExpr;

                    return primaryRest(expr);
                }
            } else if (expr instanceof SQLCharExpr) {
                SQLMethodInvokeExpr concat = new SQLMethodInvokeExpr("CONCAT");
                concat.addParameter(expr);
                do {
                    String chars = lexer.stringVal();
                    concat.addParameter(new SQLCharExpr(chars));
                    lexer.nextToken();
                } while (lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS);
                expr = concat;
            }
        } else if (lexer.token() == Token.IDENTIFIER) {
            if (expr instanceof SQLHexExpr) {
                if ("USING".equalsIgnoreCase(lexer.stringVal())) {
                    lexer.nextToken();
                    if (lexer.token() != Token.IDENTIFIER) {
                        throw new ParserException("syntax error, illegal hex. " + lexer.info());
                    }
                    String charSet = lexer.stringVal();
                    lexer.nextToken();
                    expr.getAttributes().put("USING", charSet);

                    return primaryRest(expr);
                }
            } else if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
                lexer.nextToken();

                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                if (lexer.token() != Token.IDENTIFIER
                        && lexer.token() != Token.LITERAL_CHARS) {
                    throw new ParserException("syntax error. " + lexer.info());
                }

                String collate = lexer.stringVal();
                lexer.nextToken();

                SQLBinaryOpExpr binaryExpr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.COLLATE,
                                                                 new SQLIdentifierExpr(collate), JdbcConstants.XUGU);

                expr = binaryExpr;

                return primaryRest(expr);
            } else if (expr instanceof SQLVariantRefExpr) {
                if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
                    lexer.nextToken();

                    if (lexer.token() != Token.IDENTIFIER
                            && lexer.token() != Token.LITERAL_CHARS) {
                        throw new ParserException("syntax error. " + lexer.info());
                    }

                    String collate = lexer.stringVal();
                    lexer.nextToken();

                    expr.putAttribute("COLLATE", collate);

                    return primaryRest(expr);
                }
            }
        }

        if (lexer.token() == Token.DOTDOT) {
            lexer.nextToken();
            SQLExpr upBound = expr();
            return new XuGuRangeExpr(expr, upBound);
        }

//        if (lexer.token() == Token.LPAREN && expr instanceof SQLIdentifierExpr) {
//            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
//            String ident = identExpr.getName();
//
//            if ("POSITION".equalsIgnoreCase(ident)) {
//                return parsePosition();
//            }
//        }

        if (lexer.token() == Token.VARIANT && "@".equals(lexer.stringVal())) {
            return userNameRest(expr);
        }

        if (lexer.token() == Token.ERROR) {
            throw new ParserException("syntax error. " + lexer.info());
        }

        if (lexer.token() == Token.COLONCOLON) {
            lexer.nextToken();
            SQLDataType dataType = this.parseDataType();

            XuGuTypeCastExpr castExpr = new XuGuTypeCastExpr();

            castExpr.setExpr(expr);
            castExpr.setDataType(dataType);

            return primaryRest(castExpr);
        }

        return super.primaryRest(expr);
    }

    public SQLName userName() {
        SQLName name = this.name();
        if (lexer.token() == Token.LPAREN && name.hashCode64() == FnvHash.Constants.CURRENT_USER) {
            lexer.nextToken();
            accept(Token.RPAREN);
            return name;
        }

        return (SQLName) userNameRest(name);
    }

    private SQLExpr userNameRest(SQLExpr expr) {
        if (lexer.token() != Token.VARIANT || !lexer.stringVal().startsWith("@")) {
            return expr;
        }

        XuGuUserName userName = new XuGuUserName();
        if (expr instanceof SQLCharExpr) {
            userName.setUserName(((SQLCharExpr) expr).toString());
        } else {
            userName.setUserName(((SQLIdentifierExpr) expr).getName());
        }


        String strVal = lexer.stringVal();
        lexer.nextToken();

        if (strVal.length() > 1) {
            userName.setHost(strVal.substring(1));
            return userName;
        }

        if (lexer.token() == Token.LITERAL_CHARS) {
            userName.setHost("'" + lexer.stringVal() + "'");
        } else {
            userName.setHost(lexer.stringVal());
        }
        lexer.nextToken();

        if (lexer.token() == Token.IDENTIFIED) {
            Lexer.SavePoint mark = lexer.mark();

            lexer.nextToken();
            if (lexer.token() == Token.BY) {
                lexer.nextToken();
                if (lexer.identifierEquals(FnvHash.Constants.PASSWORD)) {
                    lexer.reset(mark);
                } else {
                    userName.setIdentifiedBy(lexer.stringVal());
                    lexer.nextToken();
                }
            } else {
                lexer.reset(mark);
            }
        }

        return userName;
    }

    protected SQLExpr parsePosition() {

        SQLExpr subStr = this.primary();
        accept(Token.IN);
        SQLExpr str = this.expr();
        accept(Token.RPAREN);

        SQLMethodInvokeExpr locate = new SQLMethodInvokeExpr("LOCATE");
        locate.addParameter(subStr);
        locate.addParameter(str);

        return primaryRest(locate);
    }

    protected SQLExpr parseExtract() {
        SQLExpr expr;
        if (lexer.token() != Token.IDENTIFIER) {
            throw new ParserException("syntax error. " + lexer.info());
        }

        String unitVal = lexer.stringVal();
        SQLIntervalUnit unit = SQLIntervalUnit.valueOf(unitVal.toUpperCase());
        lexer.nextToken();

        accept(Token.FROM);

        SQLExpr value = expr();

        XuGuExtractExpr extract = new XuGuExtractExpr();
        extract.setValue(value);
        extract.setUnit(unit);
        accept(Token.RPAREN);

        expr = extract;

        return primaryRest(expr);
    }

    protected SQLExpr parseMatch() {

        XuGuMatchAgainstExpr matchAgainstExpr = new XuGuMatchAgainstExpr();

        if (lexer.token() == Token.RPAREN) {
            lexer.nextToken();
        } else {
            exprList(matchAgainstExpr.getColumns(), matchAgainstExpr);
            accept(Token.RPAREN);
        }

        acceptIdentifier("AGAINST");

        accept(Token.LPAREN);
        SQLExpr against = primary();
        matchAgainstExpr.setAgainst(against);

        if (lexer.token() == Token.IN) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.NATURAL)) {
                lexer.nextToken();
                acceptIdentifier("LANGUAGE");
                acceptIdentifier("MODE");
                if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    acceptIdentifier("QUERY");
                    acceptIdentifier("EXPANSION");
                    matchAgainstExpr.setSearchModifier(SearchModifier.IN_NATURAL_LANGUAGE_MODE_WITH_QUERY_EXPANSION);
                } else {
                    matchAgainstExpr.setSearchModifier(SearchModifier.IN_NATURAL_LANGUAGE_MODE);
                }
            } else if (lexer.identifierEquals(FnvHash.Constants.BOOLEAN)) {
                lexer.nextToken();
                acceptIdentifier("MODE");
                matchAgainstExpr.setSearchModifier(SearchModifier.IN_BOOLEAN_MODE);
            } else {
                throw new ParserException("syntax error. " + lexer.info());
            }
        } else if (lexer.token() == Token.WITH) {
            throw new ParserException("TODO. " + lexer.info());
        }

        accept(Token.RPAREN);

        return primaryRest(matchAgainstExpr);
    }

    public SQLSelectParser createSelectParser() {
        return new XuGuSelectParser(this);
    }

    protected SQLExpr parseInterval() {
        accept(Token.INTERVAL);

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr("INTERVAL");
            if (lexer.token() != Token.RPAREN) {
                exprList(methodInvokeExpr.getParameters(), methodInvokeExpr);
            }

            accept(Token.RPAREN);
            
            // 
            
            if (methodInvokeExpr.getParameters().size() == 1 // 
                    && lexer.token() == Token.IDENTIFIER) {
                SQLExpr value = methodInvokeExpr.getParameters().get(0);
                String unit = lexer.stringVal();
                lexer.nextToken();
                
                SQLIntervalExpr intervalExpr = new SQLIntervalExpr();
                intervalExpr.setValue(value);
                intervalExpr.setUnit(SQLIntervalUnit.valueOf(unit.toUpperCase()));
                return intervalExpr;
            } else {
                return primaryRest(methodInvokeExpr);
            }
        } else {
            SQLExpr value = expr();

            if (lexer.token() != Token.IDENTIFIER) {
                throw new ParserException("Syntax error. " + lexer.info());
            }

            String unit = lexer.stringVal();
            lexer.nextToken();

            SQLIntervalExpr intervalExpr = new SQLIntervalExpr();
            intervalExpr.setValue(value);
            intervalExpr.setUnit(SQLIntervalUnit.valueOf(unit.toUpperCase()));

            return intervalExpr;
        }
    }

    public SQLColumnDefinition parseColumn() {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setDbType(dbType);
        column.setName(name());
        column.setDataType(parseDataType());

        return parseColumnRest(column);
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            accept(Token.UPDATE);
            SQLExpr expr = this.expr();
            column.setOnUpdate(expr);
        }

        if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
            lexer.nextToken();
            accept(Token.SET);
            XuGuCharExpr charSetCollateExpr=new XuGuCharExpr();
            charSetCollateExpr.setCharset(lexer.stringVal());
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
                lexer.nextToken();
                charSetCollateExpr.setCollate(lexer.stringVal());
                lexer.nextToken();
            }
            column.setCharsetExpr(charSetCollateExpr);
            return parseColumnRest(column);
        }

        if (lexer.identifierEquals(FnvHash.Constants.CHARSET)) {
            lexer.nextToken();
            XuGuCharExpr charSetCollateExpr=new XuGuCharExpr();
            charSetCollateExpr.setCharset(lexer.stringVal());
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
                lexer.nextToken();
                charSetCollateExpr.setCollate(lexer.stringVal());
                lexer.nextToken();
            }
            column.setCharsetExpr(charSetCollateExpr);
            return parseColumnRest(column);
        }
        if (lexer.identifierEquals(FnvHash.Constants.AUTO_INCREMENT)) {
            lexer.nextToken();
            column.setAutoIncrement(true);
            return parseColumnRest(column);
        }

        if (lexer.identifierEquals(FnvHash.Constants.PRECISION)
                && column.getDataType().nameHashCode64() ==FnvHash.Constants.DOUBLE) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.PARTITION) {
            throw new ParserException("syntax error " + lexer.info());
        }

        if (lexer.identifierEquals(FnvHash.Constants.STORAGE)) {
            lexer.nextToken();
            SQLExpr expr = expr();
            column.setStorage(expr);
        }
        
        if (lexer.token() == Token.AS) {
            lexer.nextToken();
            accept(Token.LPAREN);
            SQLExpr expr = expr();
            column.setAsExpr(expr);
            accept(Token.RPAREN);
        }
        
        if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
            lexer.nextToken();
            column.setSorted(true);
        }
        
        if (lexer.identifierEquals(FnvHash.Constants.VIRTUAL)) {
            lexer.nextToken();
            column.setVirtual(true);
        }

        super.parseColumnRest(column);

        return column;
    }

    protected SQLDataType parseDataTypeRest(SQLDataType dataType) {
        super.parseDataTypeRest(dataType);

        for (;;) {
            if (lexer.identifierEquals(FnvHash.Constants.UNSIGNED)) {
                lexer.nextToken();
                ((SQLDataTypeImpl) dataType).setUnsigned(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.ZEROFILL)) {
                lexer.nextToken();
                ((SQLDataTypeImpl) dataType).setZerofill(true);
            } else {
                break;
            }
        }

        return dataType;
    }

    public SQLAssignItem parseAssignItem() {
        SQLAssignItem item = new SQLAssignItem();

        SQLExpr var = primary();

        String ident = null;
        long identHash = 0;
        if (var instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) var;
            ident = identExpr.getName();
            identHash = identExpr.hashCode64();

            if (identHash == FnvHash.Constants.GLOBAL) {
                ident = lexer.stringVal();
                lexer.nextToken();
                var = new SQLVariantRefExpr(ident, true);
            } else if (identHash == FnvHash.Constants.SESSION) {
                ident = lexer.stringVal();
                lexer.nextToken();
                var = new SQLVariantRefExpr(ident, false, true);
            } else {
                var = new SQLVariantRefExpr(ident);
            }
        }

        if (identHash == FnvHash.Constants.NAMES) {
            String charset = lexer.stringVal();

            SQLExpr varExpr = null;
            boolean chars = false;
            final Token token = lexer.token();
            if (token == Token.IDENTIFIER) {
                lexer.nextToken();
            } else if (token == Token.DEFAULT) {
                charset = "DEFAULT";
                lexer.nextToken();
            } else if (token == Token.QUES) {
                varExpr = new SQLVariantRefExpr("?");
                lexer.nextToken();
            } else {
                chars = true;
                accept(Token.LITERAL_CHARS);
            }

            if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
                XuGuCharExpr charsetExpr = new XuGuCharExpr(charset);
                lexer.nextToken();

                String collate = lexer.stringVal();
                lexer.nextToken();
                charsetExpr.setCollate(collate);

                item.setValue(charsetExpr);
            } else {
                if (varExpr != null) {
                    item.setValue(varExpr);
                } else {
                    item.setValue(chars
                            ? new SQLCharExpr(charset)
                            : new SQLIdentifierExpr(charset)
                    );
                }
            }

            item.setTarget(var);
            return item;
        } else if (identHash == FnvHash.Constants.CHARACTER) {
            var = new SQLIdentifierExpr("CHARACTER SET");
            accept(Token.SET);
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
        } else {
            if (lexer.token() == Token.COLONEQ) {
                lexer.nextToken();
            } else {
                accept(Token.EQ);
            }
        }

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            item.setValue(new SQLIdentifierExpr("ON"));
        } else {
            item.setValue(this.expr());
        }

        item.setTarget(var);
        return item;
    }

    public SQLName nameRest(SQLName name) {
        if (lexer.token() == Token.VARIANT && "@".equals(lexer.stringVal())) {
            lexer.nextToken();
            XuGuUserName userName = new XuGuUserName();
            userName.setUserName(((SQLIdentifierExpr) name).getName());

            if (lexer.token() == Token.LITERAL_CHARS) {
                userName.setHost("'" + lexer.stringVal() + "'");
            } else {
                userName.setHost(lexer.stringVal());
            }
            lexer.nextToken();

            if (lexer.token() == Token.IDENTIFIED) {
                lexer.nextToken();
                accept(Token.BY);
                userName.setIdentifiedBy(lexer.stringVal());
                lexer.nextToken();
            }

            return userName;
        }
        return super.nameRest(name);
    }

    @Override
    public XuGuPrimaryKey parsePrimaryKey() {
        accept(Token.PRIMARY);
        accept(Token.KEY);

        XuGuPrimaryKey primaryKey = new XuGuPrimaryKey();

        if (lexer.identifierEquals(FnvHash.Constants.USING)) {
            lexer.nextToken();
            primaryKey.setIndexType(lexer.stringVal());
            lexer.nextToken();
        }

        if (lexer.token() != Token.LPAREN) {
            SQLName name = this.name();
            primaryKey.setName(name);
        }

        accept(Token.LPAREN);
        for (;;) {
            SQLExpr expr;
            if (lexer.token() == Token.LITERAL_ALIAS) {
                expr = this.name();
            } else {
                expr = this.expr();
            }
            primaryKey.addColumn(expr);
            if (!(lexer.token() == (Token.COMMA))) {
                break;
            } else {
                lexer.nextToken();
            }
        }
        accept(Token.RPAREN);

        if (lexer.identifierEquals(FnvHash.Constants.USING)) {
            lexer.nextToken();
            primaryKey.setIndexType(lexer.stringVal());
            lexer.nextToken();
        }

        return primaryKey;
    }

    public XuGuUnique parseUnique() {
        accept(Token.UNIQUE);

        if (lexer.token() == Token.KEY) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
        }

        XuGuUnique unique = new XuGuUnique();

        if (lexer.token() != Token.LPAREN) {
            SQLName indexName = name();
            unique.setName(indexName);
        }
        
        //5.5语法 USING BTREE 放在index 名字后
        if (lexer.identifierEquals(FnvHash.Constants.USING)) {
            lexer.nextToken();
            unique.setIndexType(lexer.stringVal());
            lexer.nextToken();
        }

        accept(Token.LPAREN);
        for (;;) {
            SQLExpr column = this.expr();
            if (lexer.token() == Token.ASC) {
                column = new XuGuOrderingExpr(column, SQLOrderingSpecification.ASC);
                lexer.nextToken();
            } else if (lexer.token() == Token.DESC) {
                column = new XuGuOrderingExpr(column, SQLOrderingSpecification.DESC);
                lexer.nextToken();
            }
            unique.addColumn(column);
            if (!(lexer.token() == (Token.COMMA))) {
                break;
            } else {
                lexer.nextToken();
            }
        }
        accept(Token.RPAREN);

        if (lexer.identifierEquals(FnvHash.Constants.USING)) {
            lexer.nextToken();
            unique.setIndexType(lexer.stringVal());
            lexer.nextToken();
        }

        if (lexer.identifierEquals(FnvHash.Constants.KEY_BLOCK_SIZE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr value = this.primary();
            unique.setKeyBlockSize(value);
        }

        return unique;
    }

    public XuGuForeignKey parseForeignKey() {
        accept(Token.FOREIGN);
        accept(Token.KEY);

        XuGuForeignKey fk = new XuGuForeignKey();

        if (lexer.token() != Token.LPAREN) {
            SQLName indexName = name();
            fk.setIndexName(indexName);
        }

        accept(Token.LPAREN);
        this.names(fk.getReferencingColumns(), fk);
        accept(Token.RPAREN);

        accept(Token.REFERENCES);

        fk.setReferencedTableName(this.name());

        accept(Token.LPAREN);
        this.names(fk.getReferencedColumns());
        accept(Token.RPAREN);

        if (lexer.identifierEquals(FnvHash.Constants.MATCH)) {
            lexer.nextToken();
            if (lexer.identifierEquals("FULL") || lexer.token() == Token.FULL) {
                fk.setReferenceMatch(Match.FULL);
                lexer.nextToken();
            } else if (lexer.identifierEquals(FnvHash.Constants.PARTIAL)) {
                fk.setReferenceMatch(Match.PARTIAL);
                lexer.nextToken();
            } else if (lexer.identifierEquals(FnvHash.Constants.SIMPLE)) {
                fk.setReferenceMatch(Match.SIMPLE);
                lexer.nextToken();
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
        }

        while (lexer.token() == Token.ON) {
            lexer.nextToken();
            
            if (lexer.token() == Token.DELETE) {
                lexer.nextToken();
                
                Option option = parseReferenceOption();
                fk.setOnDelete(option);
            } else if (lexer.token() == Token.UPDATE) {
                lexer.nextToken();
                
                Option option = parseReferenceOption();
                fk.setOnUpdate(option);
            } else {
                throw new ParserException("syntax error, expect DELETE or UPDATE, actual " + lexer.token() + " "
                                          + lexer.info());
            }
        }
        return fk;
    }

    protected SQLAggregateExpr parseAggregateExprRest(SQLAggregateExpr aggregateExpr) {
        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = this.parseOrderBy();
            aggregateExpr.putAttribute("ORDER BY", orderBy);
        }
        if (lexer.identifierEquals(FnvHash.Constants.SEPARATOR)) {
            lexer.nextToken();

            SQLExpr seperator = this.primary();
            seperator.setParent(aggregateExpr);

            aggregateExpr.putAttribute("SEPARATOR", seperator);
        }
        return aggregateExpr;
    }

    public XuGuOrderingExpr parseSelectGroupByItem() {
        XuGuOrderingExpr item = new XuGuOrderingExpr();

        item.setExpr(expr());

        if (lexer.token() == Token.ASC) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.ASC);
        } else if (lexer.token() == Token.DESC) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.DESC);
        }

        return item;
    }
    
    public SQLPartition parsePartition() {
        accept(Token.PARTITION);

        SQLPartition partitionDef = new SQLPartition();

        partitionDef.setName(this.name());

        SQLPartitionValue values = this.parsePartitionValues();
        if (values != null) {
            partitionDef.setValues(values);
        }

        for (;;) {
            boolean storage = false;
            if (lexer.identifierEquals(FnvHash.Constants.DATA)) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                partitionDef.setDataDirectory(this.expr());
            } else if (lexer.token() == Token.TABLESPACE) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLName tableSpace = this.name();
                partitionDef.setTablespace(tableSpace);
            } else if (lexer.token() == Token.INDEX) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                partitionDef.setIndexDirectory(this.expr());
            } else if (lexer.identifierEquals(FnvHash.Constants.MAX_ROWS)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr maxRows = this.primary();
                partitionDef.setMaxRows(maxRows);
            } else if (lexer.identifierEquals(FnvHash.Constants.MIN_ROWS)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr minRows = this.primary();
                partitionDef.setMaxRows(minRows);
            } else if (lexer.identifierEquals(FnvHash.Constants.ENGINE) || //
                       (storage = (lexer.token() == Token.STORAGE || lexer.identifierEquals(FnvHash.Constants.STORAGE)))) {
                if (storage) {
                    lexer.nextToken();
                }
                acceptIdentifier("ENGINE");

                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLName engine = this.name();
                partitionDef.setEngine(engine);
            } else if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                SQLExpr comment = this.primary();
                partitionDef.setComment(comment);
            } else {
                break;
            }
        }
        
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            
            for (;;) {
                acceptIdentifier("SUBPARTITION");
                
                SQLName subPartitionName = this.name();
                SQLSubPartition subPartition = new SQLSubPartition();
                subPartition.setName(subPartitionName);
                
                partitionDef.addSubPartition(subPartition);
                
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            
            accept(Token.RPAREN);
        }
        return partitionDef;
    }

    protected SQLExpr parseAliasExpr(String alias) {
        if (isEnabled(SQLParserFeature.KeepNameQuotes)) {
            return new SQLIdentifierExpr(alias);
        }
        Lexer newLexer = new Lexer(alias);
        newLexer.nextTokenValue();
        return new SQLCharExpr(newLexer.stringVal());
    }

    public SQLExpr parseTop() {
        if(lexer.token() == Token.TOP){
            lexer.nextToken();

            if (lexer.token() == Token.LITERAL_INT) {
                int i = lexer.integerValue().intValue();
                lexer.nextToken();
                return new SQLIntegerExpr(i);
            }else {
                return primary();
            }
        }
        return null;
    }
}
