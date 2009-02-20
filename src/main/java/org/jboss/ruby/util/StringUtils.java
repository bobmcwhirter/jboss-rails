package org.jboss.ruby.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static String camelize(String word, boolean lowercaseFirstLetter) {
		// replace all slashes with dots (package separator)
		Pattern p = Pattern.compile("\\/(.?)");
		Matcher m = p.matcher(word);
		while (m.find()) {
			word = m.replaceFirst("." + m.group(1)/* .toUpperCase() */);
			m = p.matcher(word);
		}

		// uppercase the class name
		p = Pattern.compile("(\\.?)(\\w)([^\\.]*)$");
		// System.out.println("Does " + word + " match " + p + "?");
		m = p.matcher(word);
		if (m.find()) {
			// System.out.println("match! group count: " + m.groupCount());
			// for (int i = 1; i <= m.groupCount(); i++) {
			// System.out.println("group " + i + "=" + m.group(i));
			// }
			String rep = m.group(1) + m.group(2).toUpperCase() + m.group(3);
			// System.out.println("replacement string raw: " + rep);
			rep = rep.replaceAll("\\$", "\\\\\\$");
			// System.out.println("replacement string processed: " + rep);
			word = m.replaceAll(rep);
		}

		// replace two underscores with $ to support inner classes
		p = Pattern.compile("(__)(.)");
		m = p.matcher(word);
		while (m.find()) {
			word = m.replaceFirst("\\$" + m.group(2).toUpperCase());
			m = p.matcher(word);
		}

		// remove all underscores
		p = Pattern.compile("(_)(.)");
		m = p.matcher(word);
		while (m.find()) {
			word = m.replaceFirst(m.group(2).toUpperCase());
			m = p.matcher(word);
		}

		if (lowercaseFirstLetter) {
			word = word.substring(0, 1).toLowerCase() + word.substring(1);
		}

		return word;

	}

}
