package com.pdd.redcurrant.infrastructure.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcUtils {

    /**
     * Builds a callable SQL statement using placeholders for parameters. Ensures safety
     * by not concatenating raw input into the SQL string.
     * @param procedureName the name of the procedure
     * @param params the parameters for the procedure
     * @return the callable SQL statement
     */
    public static String buildCallStatement(String procedureName, Object... params) {
        // Create placeholders (e.g., "?, ?, ?") based on the number of parameters
        String placeholders = (params != null) ? String.join(",", Collections.nCopies(params.length, "?")) : "";
        // Use braces for stored procedures (standard SQLsyntax)
        return "{CALL " + procedureName + "(" + placeholders + ")}";
    }

}
