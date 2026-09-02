package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class VoidVisitorStarter {
    private static final String FILE_PATH =
            "C:\\Users\\WINDOWS\\Documents\\UNINTER\\TCC\\Projetos\\TaintAnalyser\\TaintAnalyser\\src\\main\\java\\org\\example\\ReversePolishNotation.java";

    public static void main(String[] args) throws Exception {
        CompilationUnit cu = StaticJavaParser
                .parse((Files.newInputStream(Paths.get(FILE_PATH))));
        VoidVisitor<Void> methodNameVisitor = new MethodNamePrinter();
        methodNameVisitor.visit(cu, null);
    }

    public static class MethodNamePrinter extends VoidVisitorAdapter<Void> {
       private Set<String> taintedVariables = new HashSet<>();

        @Override
        public void visit(VariableDeclarationExpr md, Void arg) {
            super.visit(md, arg);
            for (VariableDeclarator var : md.getVariables()) {
                String nameVariable = var.getNameAsString();
                String typeVariable = var.getTypeAsString();
                //Gets value/expression if not null
                String initialValue = var.getInitializer()
                        .map(Object::toString)
                        .orElse("No initial Value");

                System.out.println("[VARIABLE FOUND] Type:" + typeVariable
                        + " | Name: " + nameVariable
                        + " | Attribution: " + initialValue);
            }
        }


        @Override
        public void visit(MethodCallExpr md, Void arg) {
            super.visit(md, arg);

            String MethodName = md.getNameAsString();
            String scope = md.getScope().map(Object::toString).orElse("Local/Static Scope");
            System.out.println("[CALL METHOD FOUND] Object/Scope: "
                    + " -> Method Called: " + MethodName
                    + " | Quantity of args: " + md.getArguments().size());
        }
    }
}
