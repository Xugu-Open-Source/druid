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
package com.alibaba.druid.bvt.sql.xugu.visitor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class XgPlOutputVisitorTest extends TestCase {

    public void test_loop() throws Exception {
        String sql ="DECLARE\n" +
                "  x INTEGER;\n" +
                "BEGIN\n" +
                "  x := 100;\n" +
                "  LOOP\n" +
                "    x := x + 100;\n" +
                "    IF x > 1000 THEN\n" +
                "      EXIT;\n" +
                "    END IF;\n" +
                "  END LOOP;\n" +
                "  SEND_MSG(x);\n" +
                "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        System.out.println("-----------------------");
    }

    public void test_loop_1() throws Exception {
        String sql ="DECLARE\n" +
                "  x INTEGER;\n" +
                "BEGIN\n" +
                "  x := 100;\n" +
                "  LOOP\n" +
                "    x := x + 100;\n" +
                "    IF x > 1000 THEN\n" +
                "      EXIT;\n" +
                "    END IF;\n" +
                "  ENDLOOP;\n" +
                "  SEND_MSG(x);\n" +
                "END;";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.XUGU);
        String tempResult = SQLUtils.toSQLString(stmts, JdbcConstants.XUGU);
        System.out.println(tempResult);
        System.out.println("-----------------------");
    }

}
