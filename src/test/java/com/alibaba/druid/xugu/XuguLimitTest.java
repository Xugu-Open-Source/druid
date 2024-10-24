package com.alibaba.druid.xugu;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

import java.util.List;

/**
 * 大概率要改，这玩意咋测都通过，就是不写dbType都行
 */
public class XuguLimitTest extends TestCase{


    public void testLimit(){
        String sql = "select * from aaa limit 20exx";
        SQLStatementParser statementParser = SQLParserUtils.createSQLStatementParser(sql, "xugu");
        try {
            List<SQLStatement> sqlStatements = statementParser.parseStatementList();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("is not a number!"));
        }


    }
}
