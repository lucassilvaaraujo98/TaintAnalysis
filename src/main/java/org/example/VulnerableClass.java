package org.example;

import jakarta.servlet.http.HttpServletRequest;

public class VulnerableClass {
    public void processData(HttpServletRequest request) {
        // 1. Source: pega dado não confiável
        String input = request.getParameter("id");

        // 2. Sink: passa a variável diretamente para um método crítico
        System.out.println(input);
    }
}