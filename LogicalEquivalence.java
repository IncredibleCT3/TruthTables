import java.util.*;
import java.nio.file.Paths;

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

        // Get the actual variables used in each expression
        ArrayList<Character> variables1 = assignVariables(expression1);
        ArrayList<Character> variables2 = assignVariables(expression2);

        // Generate and print the base truth table for the first expression
        System.out.println("Truth Table for Expression 1: " + expression1);
        generateAndEvaluate(expression1, variables1);

        // Generate and print the base truth table for the second expression
        System.out.println("\nTruth Table for Expression 2: " + expression2);
        generateAndEvaluate(expression2, variables2);
    }

    // Function to generate the truth table for the given variables and evaluate the expression
    public static void generateAndEvaluate(String expression, ArrayList<Character> variables) {
        // Determine the number of variables used
        int varNum = variables.size();

        // Generate and evaluate the truth table
        int[] pArray = null, qArray = null, rArray = null;

        if (variables.contains('p')) pArray = assignP(varNum);
        if (variables.contains('q')) qArray = assignQ(varNum);
        if (variables.contains('r')) rArray = assignR(varNum);

        // Print the truth table for the expression
        printTruthTable(pArray, qArray, rArray, variables, expression);
    }

    // Assign the truth values for variable 'p'
    public static int[] assignP(int varNum) {
        if (varNum == 1) return new int[]{0, 1};
        if (varNum == 2) return new int[]{0, 0, 1, 1};
        return new int[]{0, 0, 0, 0, 1, 1, 1, 1};
    }

    // Assign the truth values for variable 'q'
    public static int[] assignQ(int varNum) {
        if (varNum == 1) return new int[]{0, 1};
        if (varNum == 2) return new int[]{0, 1, 0, 1};
        return new int[]{0, 0, 1, 1, 0, 0, 1, 1};
    }

    // Assign the truth values for variable 'r'
    public static int[] assignR(int varNum) {
        return new int[]{0, 1, 0, 1, 0, 1, 0, 1};
    }

    // Helper function to assign variables based on the expression
    public static ArrayList<Character> assignVariables(String expression) {
        ArrayList<Character> variables = new ArrayList<>();
        if (expression.contains("p")) variables.add('p');
        if (expression.contains("q")) variables.add('q');
        if (expression.contains("r")) variables.add('r');
        return variables;
    }

    // Print the truth table for the given expression
    public static void printTruthTable(int[] pArray, int[] qArray, int[] rArray, ArrayList<Character> variables, String expression) {
        ArrayList<Integer> result = evaluateExpression(pArray, qArray, rArray, variables, expression);

        // Iterate over the truth table and print the result for each combination
        int rows = pArray != null ? pArray.length : (qArray != null ? qArray.length : rArray.length);
        for (int i = 0; i < rows; i++) {
            if (pArray != null) System.out.print("p = " + pArray[i] + ", ");
            if (qArray != null) System.out.print("q = " + qArray[i] + ", ");
            if (rArray != null) System.out.print("r = " + rArray[i] + ", ");
            System.out.println("-> " + result.get(i));
        }
    }

    // Evaluate the logical expression, considering parentheses and negation
    public static ArrayList<Integer> evaluateExpression(int[] pArray, int[] qArray, int[] rArray, ArrayList<Character> variables, String expression) {
        ArrayList<Integer> result = new ArrayList<>();

        int rows = pArray != null ? pArray.length : (qArray != null ? qArray.length : rArray.length);
        for (int i = 0; i < rows; i++) {
            Stack<Integer> stack = new Stack<>();
            Stack<Character> operatorStack = new Stack<>();
            boolean negateNext = false;

            for (int j = 0; j < expression.length(); j++) {
                char currentChar = expression.charAt(j);

                if (currentChar == '(') {
                    // Push open parenthesis onto the operator stack
                    operatorStack.push(currentChar);
                } else if (currentChar == ')') {
                    // Evaluate until the matching open parenthesis
                    while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                        char operator = operatorStack.pop();
                        int right = stack.pop();
                        int left = stack.pop();
                        stack.push(applyOperator(left, right, operator));
                    }
                    // Pop the '(' from the stack
                    operatorStack.pop();
                } else if (currentChar == '!') {
                    negateNext = true;  // Set flag to negate the next variable or expression
                } else if (variables.contains(currentChar)) {
                    int value = getVariableValue(currentChar, pArray, qArray, rArray, i);
                    if (negateNext) {
                        value = value == 1 ? 0 : 1;
                        negateNext = false;  // Reset the negation flag after applying it
                    }
                    stack.push(value);
                } else if (currentChar == '&' || currentChar == '|') {
                    // Ensure correct precedence for & and |
                    while (!operatorStack.isEmpty() && precedence(currentChar) <= precedence(operatorStack.peek())) {
                        char operator = operatorStack.pop();
                        int right = stack.pop();
                        int left = stack.pop();
                        stack.push(applyOperator(left, right, operator));
                    }
                    operatorStack.push(currentChar);  // Push current operator onto the stack
                }
            }

            // Apply remaining operators in the stack
            while (!operatorStack.isEmpty()) {
                char operator = operatorStack.pop();
                int right = stack.pop();
                int left = stack.pop();
                stack.push(applyOperator(left, right, operator));
            }

            // The result for this row is the final value on the stack
            result.add(stack.pop());
        }

        return result;
    }

    // Helper function to get precedence of operators
    public static int precedence(char operator) {
        if (operator == '&') return 2;
        if (operator == '|') return 1;
        return 0;
    }

    // Helper function to apply a binary operator
    public static int applyOperator(int left, int right, char operator) {
        if (operator == '&') return left & right;
        if (operator == '|') return left | right;
        return 0;  // Default case, shouldn't reach here
    }

    // Get the value of the variable based on the current truth table row
    public static int getVariableValue(char var, int[] pArray, int[] qArray, int[] rArray, int index) {
        switch (var) {
            case 'p':
                return pArray[index];
            case 'q':
                return qArray[index];
            case 'r':
                return rArray[index];
            default:
                return 0;  // Default case, shouldn't reach here
        }
    }
}
