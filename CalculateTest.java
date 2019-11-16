import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CalculateTest {

	public static void main(String[] args) {

		List<String> expressions = new ArrayList<>();
		expressions.add("1 + 1");
		expressions.add("2 * 2");
		expressions.add("1 + 2 + 3");
		expressions.add("6 / 2");
		expressions.add("11 + 23");
		expressions.add("11.1 + 23");
		expressions.add("1 + 1 * 3");
		expressions.add("( 11.5 + 15.4 ) + 10.1");
		expressions.add("23 - ( 29.3 - 12.5 )");
		expressions.add("10 - ( 2 + 3 * ( 7 - 5 ))");

		for (String expression : expressions) {
			System.out.println(expression + " = " + CalculateTest.calculate(expression));
		}

	}

	public static double calculate(String sum) {

		String tidyExpression = sum.replaceAll("\\s", "").trim();

		while (tidyExpression.contains("(")) {
			String group = getGrouping(tidyExpression);
			double groupAnswer = doCompute(group.substring(group.indexOf("(") + 1, group.indexOf(")")));

			if (groupAnswer % 1 == 0) {
				tidyExpression = tidyExpression.replace(group, String.valueOf((int) groupAnswer));
			} else {
				tidyExpression = tidyExpression.replace(group, String.valueOf(groupAnswer));
			}

		}

		return doCompute(tidyExpression);
	}

	/**
	 * Get groupings
	 * 
	 * @param value
	 * @return
	 */
	public static String getGrouping(String value) {
		int beginIndex = 0;
		int endIndex = 0;
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char current = chars[i];
			if (current == '(') {
				beginIndex = i;
			} else if (current == ')') {
				endIndex = i;
				break;
			}
		}

		return value.substring(beginIndex, endIndex + 1);
	}

	/**
	 * Compute for the given expression
	 * 
	 * @param tidyExpression
	 * @return
	 */
	public static double doCompute(String tidyExpression) {
		Stack<Double> numbers = new Stack<Double>();
		Stack<Character> operations = new Stack<Character>();

		for (int i = 0; i < tidyExpression.length(); i++) {
			try {
				double number = parseStringToNumber(tidyExpression, i);
				numbers.push((double) number);

				if (number % 1 == 0) {
					i += Integer.toString((int) number).length();
				} else {
					i += Double.toString(number).length();
				}

				if (i >= tidyExpression.length()) {
					break;
				}

				Character operation = getOperation(tidyExpression, i);
				process(numbers, operations, operation);
				operations.push(operation);
			} catch (NumberFormatException ex) {
				return 0;
			}
		}

		process(numbers, operations, '!');
		if (numbers.size() == 1 && operations.size() == 0) {
			return numbers.pop();
		}

		return 0;
	}

	/**
	 * Process the computation
	 * 
	 * @param numbers
	 * @param operations
	 * @param operation
	 */
	private static void process(Stack<Double> numbers, Stack<Character> operations, Character operation) {

		while (numbers.size() >= 2 && operations.size() >= 1) {
			if (mdas(operation) <= mdas(operations.peek())) {
				double second = numbers.pop();
				double first = numbers.pop();
				Character opr = operations.pop();
				double result = compute(first, opr, second);
				numbers.push(result);
			} else {
				break;
			}
		}
	}

	/**
	 * Perform computation of first and second number
	 * 
	 * @param first
	 * @param operation
	 * @param second
	 * @return
	 */
	private static double compute(double first, Character operation, double second) {

		switch (operation) {
		case '+':
			return first + second;
		case '-':
			return first - second;
		case '*':
			return first * second;
		case '/':
			return first / second;
		default:
			return second;
		}

	}

	/**
	 * MDAS rule
	 * 
	 * @param op
	 * @return
	 */
	private static int mdas(Character ch) {

		switch (ch) {
		case '+':
			return 1;
		case '-':
			return 1;
		case '*':
			return 2;
		case '/':
			return 2;
		case '!':
			return 0;

		}

		return 0;
	}

	/**
	 * Parse String to Double
	 * 
	 * @param expression
	 * @param index
	 * @return
	 */
	private static double parseStringToNumber(String expression, int index) {
		StringBuilder sb = new StringBuilder();
		while (index < expression.length()
				&& (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.')) {
			sb.append(expression.charAt(index));
			index++;
		}

		return Double.parseDouble(sb.toString());
	}

	/**
	 * Get the operation if ADD, SUBTRACT, DIVIDE or MULTIPLY
	 * 
	 * @param expression
	 * @param index
	 * @return
	 */
	private static Character getOperation(String expression, int index) {
		if (index < expression.length()) {
			char operation = expression.charAt(index);
			switch (operation) {
			case '+':
				return '+';
			case '-':
				return '-';
			case '*':
				return '*';
			case '/':
				return '/';
			}
		}

		return '!';
	}

}
