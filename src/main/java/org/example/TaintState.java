package org.example;
import java.util.HashSet;
import java.util.Set;


public class TaintState {
    //Guarda os nomes das variáveis marcadas como contamindas

    private final Set<String> taintedVariables = new HashSet<>();

    public void addTaint(String variableName) {

        if (variableName != null && !variableName.isBlank()) {
            taintedVariables.add(variableName);
            System.out.println("[+] TAINTED DATA REGISTERED: Variable '" + variableName + " was marked as TAINTED!");
        }
    }

    public boolean isTainted(String variableName) {
        return taintedVariables.contains(variableName);
    }

    public boolean containsTaintedValue(String expressionText){
        for (String taintedVar : taintedVariables){
            if (expressionText.contains(taintedVar)){
                return true;
            }
        }
        return false;
    }
    public Set<String> getTaintedVariables(){
        return taintedVariables;
    }
}