/*
 * Copyright 2019 YoungTeam@Sogou Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yt.kratos.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yt.kratos.util.ParseUtil;

/**
 * @ClassName: ServerParseShow
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author YoungTeam
 * @date 2019年1月16日 下午11:18:47
 *
 */
public final class ServerParseShow {

    public static final int OTHER = -1;
    public static final int DATABASES = 1;
    public static final int DATASOURCES = 2;
    public static final int Kratos_STATUS = 3;
    public static final int Kratos_CLUSTER = 4;
	public static final int TABLES = 5;
    public static final int FULLTABLES =65;
    
    public static int parse(String stmt, int offset) {
        int i = offset;
        for (; i < stmt.length(); i++) {
            switch (stmt.charAt(i)) {
                case ' ':
                    continue;
                case '/':
                case '#':
                    i = ParseUtil.comment(stmt, i);
                    continue;
/*                case 'C':
                case 'c':
                    return cobarCheck(stmt, i);*/
                case 'F':
                case 'f':
                	return fullTableCheck(stmt,i) ;                    
                case 'D':
                case 'd':
                    return dataCheck(stmt, i);
    			case 'T':
    			case 't':
    				return tableCheck(stmt, i);                    
                default:
                    return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW DATA
    static int dataCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ata?".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            if ((c1 == 'A' || c1 == 'a') && (c2 == 'T' || c2 == 't') && (c3 == 'A' || c3 == 'a')) {
                switch (stmt.charAt(++offset)) {
                    case 'B':
                    case 'b':
                        return showDatabases(stmt, offset);
                    case 'S':
                    case 's':
                        return showDataSources(stmt, offset);
                    default:
                        return OTHER;
                }
            }
        }
        return OTHER;
    }

    // SHOW DATABASES
    static int showDatabases(String stmt, int offset) {
        if (stmt.length() > offset + "ases".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            if ((c1 == 'A' || c1 == 'a') && (c2 == 'S' || c2 == 's') && (c3 == 'E' || c3 == 'e')
                    && (c4 == 'S' || c4 == 's') && (stmt.length() == ++offset || ParseUtil.isEOF(stmt.charAt(offset)))) {
                return DATABASES;
            }
        }
        return OTHER;
    }

    // SHOW DATASOURCES
    static int showDataSources(String stmt, int offset) {
        if (stmt.length() > offset + "ources".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            if ((c1 == 'O' || c1 == 'o') && (c2 == 'U' || c2 == 'u') && (c3 == 'R' || c3 == 'r')
                    && (c4 == 'C' || c4 == 'c') && (c5 == 'E' || c5 == 'e') && (c6 == 'S' || c6 == 's')
                    && (stmt.length() == ++offset || ParseUtil.isEOF(stmt.charAt(offset)))) {
                return DATASOURCES;
            }
        }
        return OTHER;
    }

    private  static     Pattern fullpattern = Pattern.compile("^\\s*(SHOW)\\s+(FULL)+\\s+(TABLES)\\s+\\s*([\\!\\'\\=a-zA-Z_0-9\\s]*)", Pattern.CASE_INSENSITIVE);
    public static int fullTableCheck(String  stmt,int offset )
    {
        if(fullpattern.matcher(stmt).matches())
        {
         return FULLTABLES;
        }
        return OTHER;
    }
    
    public 	static int tableCheck(String stmt, int offset) {

		// strict match
		String pat1 = "^\\s*(SHOW)\\s+(TABLES)\\s*";
		String pat2 = "^\\s*(SHOW)\\s+(TABLES)\\s+(LIKE\\s+'(.*)')\\s*";
		String pat3 = "^\\s*(SHOW)\\s+(TABLES)\\s+(FROM)\\s+([a-zA-Z_0-9]+)\\s*";
		String pat4 = "^\\s*(SHOW)\\s+(TABLES)\\s+(FROM)\\s+([a-zA-Z_0-9]+)\\s+(LIKE\\s+'(.*)')\\s*";

		boolean flag = isShowTableMatched(stmt, pat1);
		if (flag) {
			return TABLES;
		}

		flag = isShowTableMatched(stmt, pat2);
		if (flag) {
			return TABLES;
		}

		flag = isShowTableMatched(stmt, pat3);
		if (flag) {
			return TABLES;
		}

		flag = isShowTableMatched(stmt, pat4);
		if (flag) {
			return TABLES;
		}

		return OTHER;

	}
    
	private static boolean isShowTableMatched(String stmt, String pat1) {
		Pattern pattern = Pattern.compile(pat1, Pattern.CASE_INSENSITIVE);
		Matcher ma = pattern.matcher(stmt);

		boolean flag = ma.matches();
		return flag;
	}    
}