/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ruby.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Java implementations of ActiveSupport string utilities.
 * 
 * @author Anthony Eden
 */
public class StringUtils {
	
    public static String underscore(String word) {
        String firstPattern = "([A-Z]+)([A-Z][a-z])";
        String secondPattern = "([a-z\\d])([A-Z])";
        String replacementPattern = "$1_$2";
        word = word.replaceAll("\\.", "/"); // replace package separator with slash
        word = word.replaceAll("\\$", "__"); // replace $ with two underscores for inner classes
        word = word.replaceAll(firstPattern, replacementPattern); // replace capital letter with _ plus lowercase letter
        word = word.replaceAll(secondPattern, replacementPattern);
        word = word.replace('-', '_');
        word = word.toLowerCase();
        return word;
    }

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
