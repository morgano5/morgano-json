/*
Copyright (c) 2012 Rafael Villar Villar

This file is part of Morgano-json.

Morgano-json is free software: you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation, either version 3 of the License, or any later version.

Morgano-json is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Morgano-json.  If not, see <http://www.gnu.org/licenses/>.
*/

package au.id.villar.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ObjectSerializer {

	private static final long INITIAL_DIVISOR = getInitialDivisor();

	/**
	 *
	 * @param object
	 * @param writer
	 * @param checkRecursive
	 * @throws RecursiveException
	 * @throws IOException
	 */
	public static void write(Object object, Writer writer, boolean checkRecursive) throws RecursiveException, IOException {
		writeToStream(object, writer, checkRecursive? new LinkedList<Object>(): null);
		writer.flush();
	}

	/**
	 * Writes a
	 * @param object
	 * @param writer
	 * @throws RecursiveException
	 * @throws IOException
	 */
	public static void write(Object object, Writer writer) throws RecursiveException, IOException {
		writeToStream(object, writer, new LinkedList<Object>());
		writer.flush();
	}

	private static long getInitialDivisor() {
		long myLong =  Long.MAX_VALUE;
		long divisor = 1;
		while(myLong >= 10) {
			myLong /= 10;
			divisor *= 10;
		}
		return divisor;
	}

	private static void writeToStream(long number, Writer writer) throws IOException {
		long divisor = INITIAL_DIVISOR;
		if(number == 0) {
			writer.write('0');
		} else if(number < 0) {
			writer.write('-');
			divisor *= -1;
			while (divisor < number) {
				divisor /= 10;
			}
			while(divisor < 0) {
				writer.write((int)(number / divisor + 48));
				number %= divisor;
				divisor /= 10;
			}
		} else {
			while (divisor > number) {
				divisor /= 10;
			}
			while(divisor > 0) {
				writer.write((int)(number / divisor + 48));
				number %= divisor;
				divisor /= 10;
			}
		}
	}

	// parameter objectStack: if not null, then it looks for circular dependencies that would cause this method never return
	private static void writeArrayToStream(Object array, Writer writer, LinkedList<Object> objectStack) throws RecursiveException, IOException {
		boolean prefixComa = false;
		Class<?> compClass = array.getClass().getComponentType();
		writer.write("[");
		if(compClass == boolean.class) {
			for(boolean b: (boolean[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				writer.write(b? "true": "false");
			}
		} else if(compClass == char.class) {
			for(char c:   (char[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				writer.append('"').append(c).append('"');
			}
		} else if(compClass == byte.class) {
			for(long b:   (byte[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				writeToStream(b, writer);
			}
		} else if(compClass == short.class) {
			for(long b:   (short[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				writeToStream(b, writer);
			}
		} else if(compClass == int.class) {
			for(long b:   (int[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				writeToStream(b, writer);
			}
		} else if(compClass == long.class) {
			for(long b:   (long[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				writeToStream(b, writer);
			}
		} else if(compClass == float.class) {
			for(float f:  (float[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				boolean notANumber = Float.isNaN(f) || Float.isInfinite(f);
				if(notANumber) writer.write("\"");
				writer.write(Float.toString(f));
				if(notANumber) writer.write("\"");
			}
		} else if(compClass == double.class) {
			for(double d: (double[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				boolean notANumber = Double.isNaN(d) || Double.isInfinite(d);
				if(notANumber) writer.write("\"");
				writer.write(Double.toString(d));
				if(notANumber) writer.write("\"");
			}
		} else if (compClass == Float.class) {
			for(Float f:  (Float[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				if(f != null) {
					boolean notANumber = Float.isNaN(f) || Float.isInfinite(f);
					if(notANumber) writer.write("\"");
					writer.write(f.toString());
					if(notANumber) writer.write("\"");
				} else {
					writer.write("null");
				}
			}
		} else if (compClass == Double.class) {
			for(Double d: (Double[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				if(d != null) {
					boolean notANumber = Double.isNaN(d) || Double.isInfinite(d);
					if(notANumber) writer.write("\"");
					writer.write(d.toString());
					if(notANumber) writer.write("\"");
				} else {
					writer.write("null");
				}
			}
		} else if(Number.class.isAssignableFrom(compClass) || compClass == Boolean.class) {
			for(Object o: (Object[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				writer.write(o == null? "null": o.toString());
			}
		} else if(compClass == Character.class || compClass == String.class) {
			for(Object o:   (Object[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				if(o == null)
					writer.write("null");
				else
					writer.append('"').append(escapeString(o.toString())).append('"');
			}
		} else {
			for(Object o: (Object[])array) {
				if(prefixComa) writer.write(","); prefixComa = true;
				writeToStream(o, writer, objectStack);
			}
		}
		writer.write("]");
	}

	// parameter objectStack: if not null, then it looks for circular dependencies that would cause this method never return
	private static void writeToStream(Object object, Writer writer, LinkedList<Object> objectStack) throws RecursiveException, IOException {

		Class<?> clazz;

		// --- nulls ---
		if(object == null) {
			writer.write("null");
			return;
		}

		clazz = object.getClass();

		// --- primitives and their boxings ---
		if(Number.class.isAssignableFrom(clazz) || clazz == Boolean.class) {
			writer.write(object.toString());
			return;
		} else if(clazz == Character.class || clazz == String.class) {
			writer.append('"').append(escapeString(object.toString())).append('"');
			return;
		}

		// --- arrays ---
		if(clazz.isArray()) {
			writeArrayToStream(object, writer, objectStack);
			return;
		}

		// --- objects ---

		if(contains(objectStack, object)) {
			throw new RecursiveException();
		}
		if(objectStack != null) {
			objectStack.addFirst(object);
		}

		// special case: enums
		if(Enum.class.isAssignableFrom(clazz)) {
			writer.append('"').append(escapeString(object.toString())).append('"');
			return;
		}

		// special case: collections
		if(Collection.class.isAssignableFrom(clazz)) {
			boolean first = true;
			writer.write("[");
			for(Object item: (Collection)object) {
				if(!first) writer.write(",");
				writeToStream(item, writer, objectStack);
				first = false;
			}
			writer.write("]");
			return;
		}

		// special case: maps
		if(Map.class.isAssignableFrom(clazz)) {
			boolean first = true;
			writer.write("{");
			for(Object key: ((Map)object).keySet()) {
				if(key == null) continue;
				Object value = ((Map)object).get(key);
				if(!first) writer.write(",");
				writer.append('"').append(key.toString()).append("\":");
				writeToStream(value, writer, objectStack);
				first = false;
			}
			writer.write("}");
			return;
		}


		List<String> notIncluded = null;
		boolean prefixComma = false;

		writer.write("{");
		for(Annotation annotation: clazz.getAnnotations()) {
			if(annotation instanceof TransientJSON) {
				notIncluded = addToList(((TransientJSON) annotation).value(), notIncluded);
			}
		}

		// Fields
		field:
		for(Field field: clazz.getFields()) {
			if(Modifier.isStatic(field.getModifiers()) || containsString(notIncluded, field.getName()))
				continue;
			for(Annotation annotation: field.getAnnotations())
				if(annotation instanceof TransientJSON)
					continue field;
			if(prefixComma)
				writer.write(",");
			prefixComma = true;
			writer.append("\"").append(escapeString(field.getName())).append("\":");
			writeFieldValueToStream(field, object, writer, objectStack);
		}

		// Getters
		method:
		for(Method method: clazz.getMethods()) {
			for(Annotation annotation: method.getAnnotations()) {
				if(annotation instanceof TransientJSON) continue method;
			}

			String methodName = method.getName();
			String returnType = method.getReturnType().getName();
			boolean noParameters = method.getParameterTypes().length == 0;
			boolean returnsValue = !method.getReturnType().getName().equals("void");
			boolean returnsBoolean = returnType.equals("boolean") || returnType.equals("java.lang.Boolean");
			boolean booleanGetter = noParameters && returnsValue && methodName.startsWith("is")
					&& methodName.length() > 2 && returnsBoolean;
			boolean getter = noParameters && returnsValue && methodName.startsWith("get") && methodName.length() > 3
					|| booleanGetter;

			if(getter && !methodName.equals("getClass")) {
				String propertyName = Character.toLowerCase(methodName.charAt(booleanGetter? 2: 3))
						+ methodName.substring(booleanGetter? 3: 4);
				if(containsString(notIncluded, propertyName))
					continue;
				if(prefixComma)
					writer.write(",");
				prefixComma = true;
				writer.append("\"").append(escapeString(propertyName)).append("\":");
				writeMethodReturnToStream(method, object, writer, objectStack);
			}
		}

		writer.write("}");
		if(objectStack != null) {
			objectStack.removeFirst();
		}
	}

	private static List<String> addToList(String[] strings, List<String> list) {
		if(list == null)
			list = new ArrayList<String>(10);
		Collections.addAll(list, strings);
		return list;
	}

	private static boolean containsString(List<String> strings, String string) {
		return strings != null && strings.contains(string);
	}

	private static boolean contains(List<Object> objects, Object object) {
		if(objects == null)
			return false;
		for(Object item: objects)
			if(item == object)
				return true;
		return false;
	}

	private static void writeFieldValueToStream(Field field, Object object, Writer writer, LinkedList<Object> objectStack)
			throws RecursiveException, IOException {
		Class<?> clazz = field.getType();
		try {
			if(clazz == boolean.class) {
				writer.write(field.getBoolean(object) ? "true" : "false");
			} else if(clazz == char.class) {
				writer.append('"').append(field.getChar(object)).append('"');
			} else if(clazz == byte.class || clazz == short.class || clazz == int.class || clazz == long.class) {
				writeToStream(field.getLong(object), writer);
			} else if(clazz == float.class || clazz == double.class) {
				double value = field.getDouble(object);
				boolean notANumber = Double.isNaN(value) || Double.isInfinite(value);
				if(notANumber) writer.write("\"");
				writer.write(Double.toString(value));
				if(notANumber) writer.write("\"");
			} else if(clazz == Float.class) {
				Float value = (Float)field.get(object);
				if(value != null) {
					boolean notANumber = Float.isNaN(value) || Float.isInfinite(value);
					if(notANumber) writer.write("\"");
					writer.write(value.toString());
					if(notANumber) writer.write("\"");
				} else {
					writer.write("null");
				}
			} else if(clazz == Double.class) {
				Double value = (Double)field.get(object);
				if(value != null) {
					boolean notANumber = Double.isNaN(value) || Double.isInfinite(value);
					if(notANumber) writer.write("\"");
					writer.write(value.toString());
					if(notANumber) writer.write("\"");
				} else {
					writer.write("null");
				}
			} else if(Number.class.isAssignableFrom(clazz) || clazz == Boolean.class) {
				Object value = field.get(object);
				writer.write(value == null? "null": value.toString());
			} else if(clazz == String.class || clazz == Character.class) {
				Object value = field.get(object);
				if(value == null)
					writer.write("null");
				else
					writer.append('"').append(escapeString(value.toString())).append('"');
			} else {
				writeToStream(field.get(object), writer, objectStack);
			}
		} catch(IllegalAccessException e) {
			writer.write("null");
		}
	}

	private static void writeMethodReturnToStream(Method method, Object object, Writer writer, LinkedList<Object> objectStack)
			throws RecursiveException, IOException {
		try {
			writeToStream(method.invoke(object), writer, objectStack);
		} catch(IllegalAccessException e) {
			writer.write("null");
		} catch(InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private static String escapeString(String string) {
		StringBuilder builder = new StringBuilder(string);
		escapeString(builder);
		return builder.toString();
	}

	private static void escapeString(StringBuilder string) {
		for(int index = 0; index < string.length(); index++) {
			int ch = string.charAt(index);
			if(ch == '\b') {
				string.replace(index++, index, "\\b");
			} else if(ch == '\f') {
				string.replace(index++, index, "\\f");
			} else if(ch == '\n') {
				string.replace(index++, index, "\\n");
			} else if(ch == '\r') {
				string.replace(index++, index, "\\r");
			} else if(ch == '\t') {
				string.replace(index++, index, "\\t");
			} else if(ch == '\\') {
				string.replace(index++, index, "\\\\");
			} else if(ch == '/') {
				string.replace(index++, index, "\\/");
			} else if(ch == '"') {
				string.replace(index++, index, "\\\"");
			} else if(ch < 32 || ch > 126) {
				string.replace(index++, index++, "\\u");
				char hexDigit;
				for(int offset = 12; offset >= 0; offset -= 4) {
					hexDigit = (char)(((ch >> offset) & 0xF) + 0x30);
					if(hexDigit > '9') hexDigit += 7;
					string.insert(index, hexDigit);
					if(offset > 0)
						index++;
				}
			}
		}
	}


}
