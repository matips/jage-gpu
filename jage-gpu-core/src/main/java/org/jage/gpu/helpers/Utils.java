package org.jage.gpu.helpers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class Utils {

	public static InputStream getResourceAsStream(String resourceName) {
		return Utils.class.getClassLoader().getResourceAsStream(resourceName);
	}
	public static String getResourceAsString(String resourceName) throws IOException {
		return IOUtils.toString(getResourceAsStream(resourceName), "UTF-8");

	}

}