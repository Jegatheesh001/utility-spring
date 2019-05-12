package com.myweb.utility.tools.controller;

import org.springframework.util.StringUtils;

/**
 * @author Jegatheesh<br>
 *         <b>Created</b> On May 12, 2019
 *
 */
public class Utils {
	
	public static String cleanTextContent(String text) {
		// strips off all non-ASCII characters
		text = text.replaceAll("[^\\x00-\\x7F]", "");
		// erases all the ASCII control characters
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
		// removes non-printable characters from Unicode
		text = text.replaceAll("\\p{C}", "");
		return text.trim();
	}

	/**
	 * eg. get("Hello {}", "Jegatheesh") <br>
	 * For above input, it will return Hello Jegatheesh <br>
	 * 
	 * @param content
	 * @param values
	 * @return String
	 */
	public static String get(String content, String... values) {
		String output = null;
		String holder = "{}";
		int occurance = StringUtils.countOccurrencesOf(content, holder);
		log(false, "Input: " + content, null);
		if (values != null && values.length > 0) {
			output = content;
			if (values.length == occurance) {
				for (String value : values) {
					output = replaceFirst(output, holder, value);
				}
			} else {
				System.err.println("Please check arguments");
			}
		} else {
			return content;
		}
		return output;
	}
	
	private static String replaceFirst(String content, String regex, String replacement) {
		int index = content.indexOf(regex);
		String output = content.substring(0, index) + replacement;
		int length = output.length();
		if(index + regex.length() != length)
			output += content.substring(index + regex.length());
		return output;
	}
	
	private static String lineSeperator = "<br>";
	public static void log(boolean log, String logContent, StringBuilder logBuilder) {
		if (log) {
			logBuilder.append(logContent + lineSeperator);
		}
		System.out.println(logContent);
	}
}
