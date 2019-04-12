package com.myweb.utility.tools.service;

/**
 * @author Jegatheesh On 2019-04-12 <br>
 *         To use in lamda functions
 */
public class Closure {

	private String value;
	
	public Closure(String initValue) {
		this.value = initValue;
	}
	
	public void append(String content) {
		this.value = value.concat(content);
	}
	
	public String getValue() {
		return this.value;
	}
}
