import java.util.ArrayList;
import java.util.Stack;
import java.nio.file.Paths;
import java.util.Scanner;

public class LogicalEquivalence {
    public static void main(String[] args) throws Exception {
        String expression1 = null;
        String expression2 = null;

        // Read expressions from file
        try (Scanner kb = new Scanner(Paths.get("file.txt"))) {
            expression1 = kb.nextLine();
            expression2 = kb.nextLine();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        // Remove spaces from expressions
        expression1 = expression1.replaceAll(" ", "");
        expression2 = expression2.replaceAll(" ", "");

        // Determine number of variables
        int varNum = numOfVariables(expression1);
        int varNum2 = numOfVariables(expression2);

        if (varNum != varNum2) {
            throw new Exception("Expressions not using the same variables.");
        }

        // Generate truth table for expression1
        generateTable(expression1, varNum);

        System.out.println("\nExpression1: " + expression1);
        System.out.println("Expression2: " + expression2);
        System.out.println("varNum: " + varNum);
    }





    // Count the number of unique variables (p, q, r)
    public static int numOfVariables(String expression) {
        int variableCount = 0;
        if (expression.contains("p")) {
            variableCount++;
        }
        if (expression.contains("q")) {
            variableCount++;
        }
        if (expression.contains("r")) {
            variableCount++;
        }
        return variableCount;
    }

    // Assign unique variables to a list
    public static ArrayList<Character> assignVariables(String expression) {
        ArrayList<Character> variables = new ArrayList<>();
        if (expression.contains("p")) {
            variables.add('p');
        }
        if (expression.contains("q")) {
            variables.add('q');
        }
        if (expression.contains("r")) {
            variables.add('r');
        }
        return variables;
    }

    // Generate the truth table
    public static void generateTable(String expression, int numVariables) {
        ArrayList<Character> variables = assignVariables(expression);

        // Print headers
        for (char variable : variables) {
            System.out.print(variable + "\t");
        }
        System.out.println();

        // Print truth table rows
        for (int i = 0; i != (1 << numVariables); i++) {
            String s = Integer.toBinaryString(i);
            while (s.length() != numVariables) {
                s = '0' + s;
            }
            // Print the binary string (each row of the truth table)
            for (int j = 0; j < s.length(); j++) {
                System.out.print(s.charAt(j) + "\t");
            }
            System.out.println();
        }
    }

    public static
}
