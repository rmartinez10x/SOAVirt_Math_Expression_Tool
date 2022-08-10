package com.mathExpressions.soavirt.tool;

import com.parasoft.api.tool.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.expression.parser.*;

public class MathExpressions implements ICustomTool {
	public boolean acceptsInput(IToolInput input, ICustomToolConfiguration settings) {
		return true;
	}

	public boolean execute(IToolInput input, IToolContext context) throws CustomToolException, InterruptedException {
		ICustomToolConfiguration config = context.getConfiguration();

		// Pull values from the settings
		String inputString = config.getString("stringToCompute");
		String decimalPlaces = config.getString("decimalPlaces");

		// Set a default decimal places. Needed if the text field for decimal points is
		// left empty
		if (decimalPlaces.isEmpty())
			decimalPlaces = "0";

		//Replace with logger
		com.parasoft.api.Application.showMessage("string = " + inputString + " : Decimals=" + decimalPlaces);

		// Use the parser to evaluate the mathematical expression
		double result = Parser.simpleEval(inputString);

		// Round the value to the desired number of decimal places
		result = round(result, Integer.parseInt(decimalPlaces));

		com.parasoft.api.Application.showMessage("results = " + result);

		// Return create the output string. Will be different if the decimal places is 1
		// since it needs to return an integer in that case
		DefaultTextInput output;
		if (decimalPlaces.equals("0"))
			output = new DefaultTextInput("<results>" + (int) result + "</results>", "UTF-8", "text/plain");
		else
			output = new DefaultTextInput("<results>" + result + "</results>", "UTF-8", "text/plain");
		return context.getOutputManager().runOutput("response", output, context);
	}

	public boolean isValidConfig(ICustomToolConfiguration settings) {
		// com.parasoft.api.Application.showMessage(String.valueOf(isInteger(settings.getString("decimalPlaces"))));

		// If statements for checking if the value set for the decimal places is valid.
		// Must be a positive integer or empty
		if (!settings.getString("decimalPlaces").isEmpty()) {
			if (!isInteger(settings.getString("decimalPlaces")) || settings.getString("decimalPlaces").contains("-")) {
				com.parasoft.api.Application.showMessage("Invalid decimal places. Must be a positive integer or empty");
				return false;
			}
		}
		return !settings.getString("stringToCompute").isEmpty();
	}

	public static boolean isInteger(String s) {
		return isInteger(s, 10);
	}

	// Method checking if the provided string an integer
	public static boolean isInteger(String s, int radix) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), radix) < 0)
				return false;
		}
		return true;
	}

	// Method for rounding the provided double
	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

}
