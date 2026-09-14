package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
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
            "C:\\Users\\WINDOWS\\Documents\\UNINTER\\TCC\\Projetos\\TaintAnalyser\\TaintAnalyser\\src\\main\\java\\org\\example\\VulnerableClass.java";

    public static void main(String[] args) throws Exception {
        CompilationUnit cu = StaticJavaParser
                .parse((Files.newInputStream(Paths.get(FILE_PATH))));
       VoidVisitor methodNameVisitor = new MethodNamePrinter();
       TaintState state = new TaintState();
        methodNameVisitor.visit(cu, state);
    }

    public static class MethodNamePrinter extends VoidVisitorAdapter<TaintState> {
       //Conjunto que guarda nome de todas as variáveis marcadas como contaminadas.
        private Set<String> taintedVariables = new HashSet<>();

        //Exemplo simplificado de Regras (Sources e Sinks).
        private static final Set<String> SOURCES = Set.of("getParameter", "split", "readLine");
        private static final Set<String> SINKS = Set.of("executeQuery", "println", "push");


        @Override
        public void visit(VariableDeclarationExpr md, TaintState state) {
            super.visit(md, state);
            for (VariableDeclarator var : md.getVariables()) {
                String nameVariable = var.getNameAsString();
                String typeVariable = var.getTypeAsString();

                if (var.getInitializer().isPresent()){
                    //Checa se a variável tem inicialização (ex: String input = request.getParameter("id");)
                    Expression initializer = var.getInitializer().get();

                    //1. Caso a atribuição venha direto de uma Source (Ponto de Entrada)
                    boolean fromSource = SOURCES.stream()
                            .anyMatch(source -> initializer.toString().contains(source));

                    boolean fromTaintedVar = taintedVariables.stream()
                            .anyMatch(tainted -> initializer.toString().contains(tainted));

                    if (fromSource || fromTaintedVar ){
                        taintedVariables.add(nameVariable);
                        System.out.println("[+] CONTAMINATION REGISTRED: Variable '" + nameVariable + "' was marked as TAINTED!");
                    }
                }
                //Gets value/expression if not null
                String initialValue = var.getInitializer()
                        .map(Object::toString)
                        .orElse("No initial Value");

                System.out.println("[VARIABLE FOUND] Type:" + typeVariable
                        + " | Name: " + nameVariable
                        + " | Attribution: " + initialValue);
            }
        }

        // Propagação por Reatribuição / Concatenação (ex: query = "SELECT * FROM user WHERE id=" + input;)
        @Override
        public void visit(AssignExpr md, TaintState state){
            super.visit(md, state);

            String targetVariable = md.getTarget().toString(); //Lado esquerdo da atribuição
            String attributeValue = md.getValue().toString(); //Lado Direito da atribuição

            //Se o lado direito contiver algo contaminado, o lado esquerdo se contamina..
            if (state.containsTaintedValue(attributeValue)){
                state.addTaint(targetVariable);
            }
        }


        @Override
        public void visit(MethodCallExpr md, TaintState state) {
            super.visit(md, state);

            String MethodName = md.getNameAsString();
            String scope = md.getScope().map(Object::toString).orElse("Local/Static Scope");
            System.out.println("[CALL METHOD FOUND] Object/Scope: "
                    + " -> Method Called: " + MethodName
                    + " | Quantity of args: " + md.getArguments().size());

            if (SINKS.contains(MethodName)){
                for (Expression argExpr : md.getArguments()){
                    String argumentsText = argExpr.toString();
                    if (taintedVariables.contains(argumentsText)){
                        System.out.println("[!] SECURITY ALERT: Sink '" + MethodName + "' received an tainted argument '" + argumentsText + "'!");
                    }
                }
            }
        }
    }
}
