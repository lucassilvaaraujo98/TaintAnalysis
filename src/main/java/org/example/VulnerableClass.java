package org.example;

import jakarta.servlet.http.HttpServletRequest;

public class VulnerableClass {
    public void processData(HttpServletRequest request) {
        // 1. Source (input fica contaminado)
        String input = request.getParameter("id");

        // 2. AssignExpr / Propagação (query reatribui e passa a ficar contaminada!)
        String query = "SELECT * FROM users WHERE id = " + input;

        // 3. Sink (recebe 'query' que veio contaminada por propagação)
        System.out.println(query);
    }
}