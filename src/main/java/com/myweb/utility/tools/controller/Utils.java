package com.myweb.utility.tools.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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
	
	public static void convertWebpage2HTML(String domain, String url, String fileName) {
		InputStream is = null;
		BufferedReader br = null;
		BufferedWriter writer = null;
		try {
			URL uri = new URL(domain + url);
			is = uri.openStream(); // throws an IOException
			br = new BufferedReader(new InputStreamReader(is));
			writer = new BufferedWriter(new FileWriter(fileName + ".html"));
			StringBuilder sb = new StringBuilder();
			try {
				br.lines().forEach(line -> {
					line = line.replaceAll("src=\"", "src=\"" + domain);
					sb.append(line + "\n");
				});
			} catch (Exception e) {

			}
			writer.append(sb);
			writer.close();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		convertWebpage2HTML("http://192.168.0.30:8080", 
				"/eclinic/dentalConsultAction.do?method=printDentalChart&op_number=KNG-03-000001&consult_id=11&to_date=16-05-2019", 
				"data");
	}
}
