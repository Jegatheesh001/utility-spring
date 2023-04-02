package com.myweb.utility.extensions;

import java.io.File;

import lombok.experimental.ExtensionMethod;

/**
 * @author Jegatheesh <br>
 *         <b>Created</b> On Apr 2, 2023
 *
 */
@ExtensionMethod(FileExtension.class)
public class TestExtensionMethod {
	public static void main(String[] args) {
		File file = new File("testFile.pdf");
		file.getExtension();
	}
}
