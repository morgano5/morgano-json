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

import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Date: 28/04/12
 * Time: 9:17 PM
 */
public class ObjectDeserializer {

	public static Object getFromReader(Reader reader) throws JSONReaderException {
		return internalGetFromReader(reader, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFromReader(Reader reader, Class<T> clazz) throws JSONReaderException {
		if(clazz == null) {
			return (T)getFromReader(reader);
		}
		try {
			T object = clazz.newInstance();
			mergeFromReader(reader, object);
			return object;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void mergeFromReader(Reader reader, Object object) throws JSONReaderException {
		if(object == null)
			throw new NullPointerException();
		internalGetFromReader(reader, object);
	}

	private static Object internalGetFromReader(Reader reader, Object object) throws JSONReaderException {
		EventHandler handler = object == null? new EventHandler(): new EventHandler(object);
		JSONReader parser = new JSONReader();
		parser.setContentHandler(handler);
		parser.setErrorHandler(handler);
		parser.setInput(reader);
		parser.parse();
		JSONReaderException error = handler.getException();
		if(error != null) {
			throw error;
		}
		return handler.getResult();
	}

	private static class EventHandler implements ContentHandler, ErrorHandler {

		private LinkedList<Object> objects = new LinkedList<Object>();
		private Object result;
		private JSONReaderException exception;
		private Object root;

		public EventHandler(Object root) {
			this.root = root;
		}

		public EventHandler() {
			result = new HashMap<String, Object>();
		}

		public Object getResult() throws JSONReaderException {
			if(root != null && result != null) {
				match(root, result);
				result = root;
			}
			return result;
		}

		@Override
		public void startObject(StringBuilder name) throws JSONReaderException {
			addToParentAndList(name == null? null: name.toString(), new HashMap<String, Object>(10));
		}

		@Override
		public void endObject() throws JSONReaderException {
			objects.pollLast();
		}

		@Override
		public void startArray(StringBuilder name) throws JSONReaderException {
			addToParentAndList(name == null? null: name.toString(), new ArrayList<Object>(10));
		}

		@Override
		public void endArray() throws JSONReaderException {
			objects.pollLast();
		}

		@Override
		@SuppressWarnings("unchecked")
		public void simpleValue(StringBuilder name, StringBuilder strValue, ValueType type) throws JSONReaderException {

			Object parent = objects.peekLast();

			// assign to an object
			if(name != null) {
				Map<String, Object> map = (Map)parent;
				String property = name.toString();
				switch(type) {
					case FALSE: map.put(property, false); break;
					case TRUE: map.put(property, true); break;
					case NULL: map.put(property, null); break;
					case NUMBER: map.put(property, new BigDecimal(strValue.toString())); break;
					case STRING: map.put(property, strValue.toString()); break;
					default: throw new RuntimeException("programming error: type not known: " + type.name());
				}
				return;
			}

			// assign to an array
			List<Object> list = (List)parent;
			switch(type) {
				case FALSE: list.add(false); break;
				case TRUE: list.add(true); break;
				case NULL: list.add(null); break;
				case NUMBER: list.add(new BigDecimal(strValue.toString())); break;
				case STRING: list.add(strValue.toString()); break;
				default: throw new RuntimeException("programming error: type not known: " + type.name());
			}
		}

		@Override
		public void error(JSONReaderException exception) {
			this.exception = exception;
		}

		public JSONReaderException getException() {
			return exception;
		}

		@SuppressWarnings("unchecked")
		private void addToParentAndList(String name, Object newObject) {
			if(objects.size() > 0) {
				Object parent = objects.peekLast();
				if(name == null)
					((List<Object>)parent).add(newObject);
				else
					((Map<String, Object>)parent).put(name, newObject);
			} else {
				result = newObject;
			}
			objects.addLast(newObject);
		}

		private void match(Object original, Object json) throws JSONReaderException {
			match(original, json, original.getClass().toString());
		}

		@SuppressWarnings("unchecked")
		private void match(Object original, Object json, String strTypeOriginal) throws JSONReaderException {
			Class<?> originalClass = original.getClass();
			if(List.class.isAssignableFrom(originalClass)) {
				List<Object> listJson = (List)json;
				List<Object> list = (List)original;
				String compType = getStrComponentTypeFromStrListType(strTypeOriginal);
				Class<?> compClass = getClassFromStrType(compType);
				for(int index = 0; index < listJson.size(); index++) {
					Object value = listJson.get(index);
					while(list.size() <= index)
						list.add(null);
					if(value == null) {
						list.set(index, null);
					} else if(compClass == Object.class) {
						list.set(index, value);
						if(!isBasicType(value.getClass())) // TODO do I really need these 2 lines?   (1)
							match(value, value, compType); //                                        (2)
					} else if(isBasicType(compClass)) {
						list.set(index, cast(value, compClass));
					} else {
						Object originalValue = list.get(index);
						if(originalValue == null) {
							originalValue = newInstance(compClass);
							list.set(index, originalValue);
						}
						match(originalValue, value, compType);
					}
				}
			} else if(Collection.class.isAssignableFrom(originalClass)) {





				List<Object> listJson = (List)json;
				Collection<Object> collection = (Collection)original;
				String compType = getStrComponentTypeFromStrListType(strTypeOriginal);
				Class<?> compClass = getClassFromStrType(compType);
				for(int index = 0; index < listJson.size(); index++) {
					Object value = listJson.get(index);
					if(value == null) {
						collection.add(null);
					} else if(compClass == Object.class) {
						collection.add(value);
						if(!isBasicType(value.getClass())) // TODO do I really need these 2 lines?   (1)
							match(value, value, compType); //                                        (2)
					} else if(isBasicType(compClass)) {
						collection.add(cast(value, compClass));
					} else {
						Object originalValue = newInstance(compClass);
						if(collection.contains(originalValue)) {
							for(Object o: collection) {
								if(o != null && o.equals(originalValue)) {
									originalValue = o;
									break;
								}
							}
						} else {
							collection.add(originalValue);
						}
						match(originalValue, value, compType);
					}
				}

			} else if(Map.class.isAssignableFrom(originalClass)) {
				Map<String, Object> mapJson = (Map)json;
				Map<String, Object> map = (Map)original;
				String compType = getStrComponentTypeFromStrMapType(strTypeOriginal);
				Class<?> compClass = getClassFromStrType(compType);
				for(String strProperty: mapJson.keySet()) {
					Object property = mapJson.get(strProperty);
					if(property == null) {
						map.put(strProperty, property);
					} else if(compClass == Object.class) {
						map.put(strProperty, property);
						if(!isBasicType(property.getClass()))
							match(property, property, compType);
					} else if(isBasicType(compClass)) {
						map.put(strProperty, cast(property, compClass));
					} else {
						Object originalValue = map.get(strProperty);
						if(originalValue == null) {
							originalValue = newInstance(compClass);
							map.put(strProperty, originalValue);
						}
						match(originalValue, property, compType);
					}
				}
			} else if(originalClass.isArray()) {
				List<Object> listJson = (List)json;
				Class<?> compClass = originalClass.getComponentType();
				for(int index = 0; index < listJson.size(); index++) {
					Object value = listJson.get(index);
					Class<?> valueClass = value == null? null: value.getClass();
					if(value == null || compClass == Object.class) {
						Array.set(original, index, value);
					} else if(isBasicType(valueClass) || isBasicType(compClass)) {
						Array.set(original, index, cast(value, compClass));
					} else {
						Object[] array = ((Object[])original);
						Object originalValue = array[index];
						if(originalValue == null) {
							if(compClass.isArray())
								originalValue = newInstance(compClass, ((List)value).size());
							else
								originalValue = newInstance(compClass);
							array[index] = originalValue;
						}
						match(originalValue, value, originalValue.getClass().toString());
					}
				}
			} else {
				Map<String, Object> mapJson = (Map)json;
				for(String strProperty: mapJson.keySet()) {
					Object property = mapJson.get(strProperty);
					PropertyInfo info = getPropertyInfo(original, strProperty);
					if(isBasicType(info.clazz)) {
						setProperty(original, strProperty, cast(property, info.clazz));
					} else if(info.clazz == Object.class || property == null) {
						setProperty(original, strProperty, property);
					} else {
						if(info.value == null) {
							if(info.clazz.isArray())
								info.value = setNewValueToProperty(original, strProperty, ((List)mapJson.get(strProperty)).size());
							else
								info.value = setNewValueToProperty(original, strProperty);
						}
						match(info.value, property, info.strType);
					}
				}
			}
		}

		private String getStrComponentTypeFromStrListType(String listType) {
			return listType.contains("<") ?
					listType.substring(listType.indexOf("<") + 1, listType.lastIndexOf(">")):
					Object.class.getName();
		}

		private String getStrComponentTypeFromStrMapType(String mapType) {
			return mapType.contains("<") ?
					mapType.substring(mapType.indexOf(",") + 2, mapType.lastIndexOf(">")):
					Object.class.getName();
		}

		private Class<?> getClassFromStrType(String strType) {
			try {
				return strType.contains("<") ?
						Class.forName(strType.substring(0, strType.indexOf("<"))):
						Class.forName(strType);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		private boolean isBasicType(Class<?> clazz) {
			return clazz.isPrimitive() || Number.class.isAssignableFrom(clazz) || clazz == Boolean.class
					|| clazz == Character.class || clazz == String.class || clazz == Date.class
					|| Enum.class.isAssignableFrom(clazz);
		}

		private static class PropertyInfo {

			Object value;
			Class clazz;
			String strType;

			private PropertyInfo(Object value, Class clazz, String strType) {
				this.value = value;
				this.clazz = clazz;
				this.strType = strType;
			}
		}

		private static PropertyInfo getPropertyInfo(Object bean, String propertyName) throws JSONReaderException {
			Class<?> clazz = bean.getClass();
			try {
				Field field = clazz.getField(propertyName);
				return new PropertyInfo(field.get(bean), field.getType(), getStrType(field));
			} catch (NoSuchFieldException e) {
				// failing... using getter
			} catch (IllegalAccessException e) {
				// failing... using getter
			}
			String methodName;
			try {
				methodName = String.format("get%s%s", Character.toUpperCase(propertyName.charAt(0)), propertyName.substring(1));
				Method method = clazz.getMethod(methodName);
				return new PropertyInfo(method.invoke(bean), method.getReturnType(), getStrType(method));
			} catch(NoSuchMethodException e) {
				// failing... using getter for boolean (isXxxx ....)
			} catch (InvocationTargetException e) {
				// failing... using getter for boolean (isXxxx ....)
			} catch (IllegalAccessException e) {
				// failing... using getter for boolean (isXxxx ....)
			}
			try {
				methodName = String.format("is%s%s", Character.toUpperCase(propertyName.charAt(0)), propertyName.substring(1));
				Method method = clazz.getMethod(methodName);
				String returnType = method.getReturnType().getName();
				if(!returnType.equals("boolean") && ! returnType.equals("java.lang.Boolean")) {
					throw new JSONReaderException("neither a proper field or getter found to get object's property: " + propertyName);
				}
				return new PropertyInfo(method.invoke(bean), method.getReturnType(), getStrType(method));
			} catch (NoSuchMethodException e) {
				throw new JSONReaderException("neither a proper field or getter found to get object's property: " + propertyName);
			} catch (InvocationTargetException e) {
				throw new JSONReaderException("neither a proper field or getter found to get object's property: " + propertyName);
			} catch (IllegalAccessException e) {
				throw new JSONReaderException("neither a proper field or getter found to get object's property: " + propertyName);
			}
		}

		private static String getStrType(Field field) {
			String str = field.toGenericString();
			return str.substring(str.indexOf(' ') + 1, str.lastIndexOf(' '));
		}

		private static String getStrType(Method method) {
			StringBuilder builder = new StringBuilder(method.toGenericString());
			int spcPos = builder.indexOf(" ");
			int ltPos;
			builder.delete(0, spcPos + 1);
			spcPos = builder.indexOf(" ");
			ltPos = builder.indexOf("<");
			if(ltPos == -1 || ltPos > spcPos) {
				builder.delete(0, spcPos + 1);
				return builder.toString();
			}
			int stack = 1;
			for(ltPos++; ltPos < builder.length() && stack > 0; ltPos++) {
				switch(builder.charAt(ltPos)) {
					case '<': stack++; break;
					case '>': stack--; break;
				}
			}
			return builder.substring(0, ltPos);
		}

		private static void setProperty(Object bean, String propertyName, Object property) throws JSONReaderException {
			internalSetProperty(bean, propertyName, property, false, -1);
		}

		private static Object setNewValueToProperty(Object bean, String propertyName) throws JSONReaderException {
			return internalSetProperty(bean, propertyName, null, true, -1);
		}

		private static Object setNewValueToProperty(Object bean, String propertyName, int arrayLength) throws JSONReaderException {
			return internalSetProperty(bean, propertyName, null, true, arrayLength);
		}

		private static Object internalSetProperty(Object bean, String propertyName, Object property, boolean create, int arrayLength) throws JSONReaderException {
			Class<?> clazz = bean.getClass();

			try {
				Field field = clazz.getField(propertyName);
				Class<?> fieldType = field.getType();
				if(create)
					property = newInstance(fieldType, arrayLength);
				field.set(bean, property);
				return property;
			} catch (NoSuchFieldException e) {
				// failing... using setter
			} catch (IllegalAccessException e) {
				// failing... using setter
			}
			try {
				String methodName = String.format("set%s%s", Character.toUpperCase(propertyName.charAt(0)), propertyName.substring(1));
				Method[] methods = clazz.getMethods();
				for(Method method: methods) {
					if(method.getName().equals(methodName) && method.getReturnType().getName().equals("void")) {
						Class<?>[] parameterTypes = method.getParameterTypes();
						if(parameterTypes.length != 1)
							continue;
						if(create)
							property = newInstance(parameterTypes[0], arrayLength);
						method.invoke(bean, property);
						return property;
					}
				}
			} catch (InvocationTargetException e) {
				throw new JSONReaderException("error trying to set object's property: " + propertyName, e);
			} catch (IllegalAccessException e) {
				throw new JSONReaderException("error trying to set object's property: " + propertyName, e);
			}
			throw new JSONReaderException("error trying to set object's property: " + propertyName);
		}

		private static <T> T newInstance(Class<T> clazz) throws JSONReaderException {
			return newInstance(clazz, -1);
		}

		@SuppressWarnings("unchecked")
		private static <T> T newInstance(Class<T> clazz, int arrayLength) throws JSONReaderException {
			try {
				if(clazz.isArray())
					return (T)Array.newInstance(clazz.getComponentType(), arrayLength);
				else if(clazz == List.class)
					return (T)new ArrayList<Object>();
				else if(clazz == Map.class)
					return (T)new HashMap<String, Object>();
				else
					return clazz.newInstance();
			} catch (InstantiationException e) {
				throw new JSONReaderException("error trying to set object's property: can't create an instance of type " + clazz.getName(), e);
			} catch (IllegalAccessException e) {
				throw new JSONReaderException("error trying to set object's property: can't create an instance of type " + clazz.getName(), e);
			}
		}

		private static void validateDate(String strDate) throws JSONReaderException {
			boolean dateIsValid = strDate.length() == 19 || strDate.length() == 24;
			for(int pos = 0; pos < strDate.length() && dateIsValid; pos ++) {
				char ch = strDate.charAt(pos);
				switch(ch) {
					case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':
						if(pos == 4 || pos == 7 || pos == 10 || pos == 13 || pos == 16 || pos == 19 || pos == 23)
							dateIsValid = false;
						break;
					case '-': if(pos != 4 && pos != 7) dateIsValid = false; break;
					case ':': if(pos != 13 && pos != 16) dateIsValid = false; break;
					case '.': if(pos != 19) dateIsValid = false; break;
					case 'T': if(pos != 10) dateIsValid = false; break;
					case 'Z': if(pos != 23) dateIsValid = false; break;
					default: dateIsValid = false;
				}
			}
			if(!dateIsValid)
				throw new JSONReaderException("Invalid date format: \"" + strDate + "\"");
		}

		@SuppressWarnings("unchecked")
		private static <T> T cast(Object value, Class<T> clazz) throws JSONReaderException {
			try {
				if(value == null)
					return null;
				Class<?> valueClass = value.getClass();
				if(clazz.isAssignableFrom(valueClass))
					return (T)value;
				boolean isPrimitiveNumber = clazz == byte.class || clazz == short.class || clazz == int.class
						|| clazz == long.class || clazz == float.class || clazz == double.class;
				if(Number.class.isAssignableFrom(clazz) || isPrimitiveNumber) {
					BigDecimal bigDecimal = new BigDecimal(value.toString());
					if(clazz == Byte.class || clazz == byte.class) {
						return (T)(Byte)bigDecimal.byteValueExact();
					} else if(clazz == Short.class || clazz == short.class) {
						return (T)(Short)bigDecimal.shortValueExact();
					} else if(clazz == Integer.class || clazz == int.class) {
						return (T)(Integer)bigDecimal.intValueExact();
					} else if(clazz == Long.class || clazz == long.class) {
						return (T)(Long)bigDecimal.longValueExact();
					} else if(clazz == Float.class || clazz == float.class) {
						return (T)(Float)bigDecimal.floatValue();
					} else if(clazz == Double.class || clazz == double.class) {
						return (T)(Double)bigDecimal.doubleValue();
					} else if(clazz == BigDecimal.class) {
						return (T)bigDecimal;
					} else if(clazz == BigInteger.class) {
						return (T)bigDecimal.toBigIntegerExact();
					}
				} else if(clazz == String.class) {
					return (T)value.toString();
				} else if(clazz == Date.class) {
					String strDate = value.toString();
					validateDate(strDate);
					char[] chars = strDate.toCharArray();
					long longTime = new GregorianCalendar(
							/*year*/    chars[0] * 1000 + chars[1] * 100 + chars[2] * 10 + chars[3] - 53328,
							/*month*/   chars[5] * 10 + chars[6] - 529,
							/*day*/     chars[8] * 10 + chars[9] - 528,
							/*hour*/    chars[11] * 10 + chars[12] - 528,
							/*minute*/  chars[14] * 10 + chars[15] - 528,
							/*seconds*/ chars[17] * 10 + chars[18] - 528
					).getTimeInMillis();
					if(strDate.length() == 24) {
						longTime += chars[20] * 100 + chars[21] * 10 + chars[22] - 5328;
					}
					return (T)new Date(longTime);
				} else if(clazz == Boolean.class || clazz == boolean.class) {
					String strValue = value.toString().toUpperCase();
					if(!strValue.equals("TRUE") && !strValue.equals("FALSE"))
						throw new JSONReaderException("object of type " + valueClass.getName() + " cannot be casted to type " + clazz.getName());
					return (T)(Boolean)Boolean.parseBoolean(strValue);
				} else if(clazz == Character.class || clazz == char.class) {
					String strValue = value.toString();
					if(strValue.length() == 1) {
						return (T)(Character)strValue.charAt(0);
					}
				} else if(Enum.class.isAssignableFrom(clazz)) {
					if(valueClass == String.class) {
						return (T)Enum.valueOf((Class<Enum>)clazz, value.toString());
					} else if(valueClass == BigDecimal.class) {
						return clazz.<T>getEnumConstants()[((BigDecimal)value).intValue()];
					}
				}
				throw new JSONReaderException("object of type " + valueClass.getName() + " cannot be casted to type " + clazz.getName());
			} catch(NumberFormatException e) {
				throw new JSONReaderException("value \"" + value.toString() + "\" cannot be casted to type " + clazz.getName());
			} catch(ArithmeticException e) {
				throw new JSONReaderException("value \"" + value.toString() + "\" cannot be casted to type " + clazz.getName());
			}
		}
	}
}
