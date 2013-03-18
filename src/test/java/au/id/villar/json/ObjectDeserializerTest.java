package au.id.villar.json;

import org.junit.*;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.*;

public class ObjectDeserializerTest {

	@Test
	public void generationTest() throws JSONReaderException {
		MyClass myClass = ObjectDeserializer.getFromReader(new StringReader("{\"myString\": \"HOLA MUNDO\"}"), MyClass.class);
		assertEquals("HOLA MUNDO", myClass.myString);
	}

	@Test
	public void mapTest() throws JSONReaderException {
		Object myObject = ObjectDeserializer.getFromReader(new StringReader("{\"myString\": \"HOLA MUNDO\"}"));
		assertTrue(Map.class.isAssignableFrom(myObject.getClass()));
		assertEquals("HOLA MUNDO", ((Map)myObject).get("myString"));
	}

	@Test
	public void listTest() throws JSONReaderException {
		Object myObject = ObjectDeserializer.getFromReader(new StringReader("[\"HOLA MUNDO\"]"));
		assertTrue(List.class.isAssignableFrom(myObject.getClass()));
		assertEquals("HOLA MUNDO", ((List) myObject).get(0));
	}

	@Test
	public void collectionsTest() throws JSONReaderException {
		Collection<String> myObject = new HashSet<String>();
		ObjectDeserializer.mergeFromReader(new StringReader("[\"uno\",\"dos\",\"tres\"]"), myObject);
		assertTrue(myObject.contains("uno"));
		assertTrue(myObject.contains("dos"));
		assertTrue(myObject.contains("tres"));
	}


	@Test
	public void primitivesTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myString\": \"Hola mundo\", \"myBoolean\": true, \"myInteger\": 12345, \"myNull\": null, \"myDate\": \"1973-12-22T03:10:00.000Z\"}");
		assertEquals("Hola mundo", myClass.myString);
		assertTrue(myClass.myBoolean);
		assertEquals(12345, myClass.myInteger);
		assertNull(myClass.myNull);
		GregorianCalendar gc = new GregorianCalendar(1973, Calendar.DECEMBER, 22, 3, 10, 0);
		assertEquals(gc.getTime(), myClass.myDate);
	}

	@Test
	public void getterSetterTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myProperty\": \"Hola mundo\"}");
		assertEquals("Hola mundo", myClass.getMyProperty());
	}

	@Test
	public void objectsObjectTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myInternalClass\": {\"myString\": \"HI\"}}");
		assertNotNull(myClass.myInternalClass);
		assertEquals("HI", myClass.myInternalClass.myString);
	}

	@Test
	public void objectsArrayTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myIntegerArray\": [1, 2, 3, 4, 5]}");
		assertEquals(5, myClass.myIntegerArray.length);
		assertEquals(5, myClass.myIntegerArray[4]);
	}

	@Test
	public void objectsArray2Test() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myArrayOfIntegerArray\": [[1, 2, 3, 4, 5], [2, 4, 6, 8, 10]]}");
		assertNotNull(myClass.myArrayOfIntegerArray);
		assertEquals(2, myClass.myArrayOfIntegerArray.length);
		assertEquals(10, myClass.myArrayOfIntegerArray[1][4]);
	}

	@Test
	public void objectsListTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myIntegerList\": [2, 4, 6, 8, 10]}");
		assertNotNull(myClass.myIntegerList);
		assertEquals(5, myClass.myIntegerList.size());
		assertEquals(10, (int)myClass.myIntegerList.get(4));
	}

	@Test
	public void objectsList2Test() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myListOfIntegerList\": [[1, 2, 3, 4, 5], [2, 4, 6, 8, 10]]}");
		assertNotNull(myClass.myListOfIntegerList);
		assertEquals(2, myClass.myListOfIntegerList.size());
		assertEquals(10, (int)myClass.myListOfIntegerList.get(1).get(4));
	}

	@Test
	public void objectsObjectListTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myObjList\": [{\"myInteger\": 1}, {\"myInteger\": 2}]}");
		assertNotNull(myClass.myObjList);
		assertEquals(2, myClass.myObjList.size());
		assertEquals(2, myClass.myObjList.get(1).myInteger);
	}

	@Test
	public void objectsObjectArrayTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myObjArray\": [{\"myInteger\": 1}, {\"myInteger\": 2}]}");
		assertNotNull(myClass.myObjArray);
		assertEquals(2, myClass.myObjArray.length);
		assertEquals(2, myClass.myObjArray[1].myInteger);
	}

	@Test
	public void objectsArrayinObjectTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myObject\": [ 1, 2]}");
		assertNotNull(myClass.myObject);
		assertEquals(2, ((List)myClass.myObject).size());
		assertEquals(2, ((BigDecimal)((List)myClass.myObject).get(1)).intValueExact());
	}

	@Test
	public void complexMapTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myComplexMap\": {\"uno\": {\"u_n_o\": [1,2,3,4]}, \"dos\": {}}}");
		assertNotNull(myClass.myComplexMap);
		assertEquals(4, (byte)myClass.myComplexMap.get("uno").get("u_n_o").get(3));
	}

	@Test
	public void mapOfIntegersTest() throws JSONReaderException {
		MyClass myClass = parseMyClass("{\"myMapOfIntegers\": {\"uno\": 1, \"dos\": 2, \"tres\": 3}}");
		assertNotNull(myClass.myMapOfIntegers);
		assertEquals(1, (int)myClass.myMapOfIntegers.get("uno"));
		assertEquals(2, (int)myClass.myMapOfIntegers.get("dos"));
		assertEquals(3, (int)myClass.myMapOfIntegers.get("tres"));
	}

	private MyClass parseMyClass(String json) throws JSONReaderException {
		return ObjectDeserializer.getFromReader(new StringReader(json), MyClass.class);
	}

	public static class MyClass {
		public String myString;
		public boolean myBoolean;
		public int myInteger;
		public Object myNull = new Object();
		public Date myDate;
		public int[] myIntegerArray;
		public int[][] myArrayOfIntegerArray;
		public MyClass[] myObjArray;
		public List<Integer> myIntegerList;
		public List<List<Integer>> myListOfIntegerList;
		public List<MyClass> myObjList;
		public Map<String, Integer> myMapOfIntegers;
		public Map<String, Map<String, List<Byte>>> myComplexMap;
		public MyClass myInternalClass;
		public Object myObject;
		private String myProperty;
		public String getMyProperty() { return myProperty; }
		public void setMyProperty(String myProperty) { this.myProperty = myProperty; }
	}

	@Test
	public void rootArrayTest() throws JSONReaderException {
		int[] myIntegerArray = new int[5];
		ObjectDeserializer.mergeFromReader(new java.io.StringReader("[ 4, 8, 12, 16, 20]"), myIntegerArray);
		assertEquals(20, myIntegerArray[4]);
	}

	@Test
	public void rootArray2Test() throws JSONReaderException {
		Object[] array = new Object[5];
		ObjectDeserializer.mergeFromReader(new java.io.StringReader("[ \"Hola mundo\", true, 12345, null, \"1973-12-22T03:10:00.000Z\"]"), array);
		assertEquals("Hola mundo", array[0]);
		assertEquals("1973-12-22T03:10:00.000Z", array[4]);
	}

	@Test
	public void rootArray3Test() throws JSONReaderException {
		Integer[] integers = new Integer[5];
		ObjectDeserializer.mergeFromReader(new java.io.StringReader("[ 2, 4, 6, 8, 10 ]"), integers);
		assertEquals(2, (int) integers[0]);
		assertEquals(6, (int)integers[2]);
		assertEquals(10, (int)integers[4]);
	}

	@Test
	public void rootArray4Test() throws JSONReaderException {
		List<Object> moreIntegers = new ArrayList<Object>();
		ObjectDeserializer.mergeFromReader(new java.io.StringReader("[ 3, 6, 9, 12, 15 ]"), moreIntegers);
		assertEquals(BigDecimal.class, moreIntegers.get(0).getClass());
		assertEquals(new BigDecimal(3), moreIntegers.get(0));
		assertEquals(new BigDecimal(9), moreIntegers.get(2));
		assertEquals(new BigDecimal(15), moreIntegers.get(4));
	}

	@Test
	public void autoGeneratedMapsTest() throws JSONReaderException {
		Object result = ObjectDeserializer.getFromReader(new java.io.StringReader("{\"myString\": \"Hola mundo\", \"myBoolean\": true, \"myInteger\": 12345, \"myNull\": null, \"myDate\": \"1973-12-22T03:10:00.000Z\"}"));
		assertTrue(Map.class.isAssignableFrom(result.getClass()));
		Map map = (Map)result;
		assertEquals(true, map.get("myBoolean"));
		assertEquals("Hola mundo", map.get("myString"));
		assertEquals(new BigDecimal(12345), map.get("myInteger"));
		assertNull(map.get("myNull"));
		assertEquals("1973-12-22T03:10:00.000Z", map.get("myDate"));
	}

	@Test
	public void autoGeneratedListsTest() throws JSONReaderException {
		Object result = ObjectDeserializer.getFromReader(new java.io.StringReader("[ \"Hola mundo\", true, 12345, null, \"1973-12-22T03:10:00.000Z\"]"));
		assertTrue(List.class.isAssignableFrom(result.getClass()));
		List list = (List)result;
		assertEquals("Hola mundo", list.get(0));
		assertEquals(true, list.get(1));
		assertEquals(new BigDecimal(12345), list.get(2));
		assertNull(list.get(3));
		assertEquals("1973-12-22T03:10:00.000Z", list.get(4));
	}

	@Test
	public void autoGeneratedMapInMaptest() throws JSONReaderException {
		Object result = ObjectDeserializer.getFromReader(new java.io.StringReader("{\"myString\": \"Hola mundo\", \"myBoolean\": true, \"myNull\": null, \"myDate\": \"1973-12-22T03:10:00.000Z\", \"myInternalClass\": {\"myString\": \"inside...\"}, \"myInteger\": 12345}"));
		assertTrue(Map.class.isAssignableFrom(result.getClass()));
		Map map = (Map)result;
		assertEquals("inside...", ((Map) map.get("myInternalClass")).get("myString"));
	}

	@Test
	public void nullTest() throws JSONReaderException {
		Object result = ObjectDeserializer.getFromReader(new java.io.StringReader("{\"myString\": \"Hola mundo\", \"myBoolean\": true, \"myNull\": null, \"myDate\": \"1973-12-22T03:10:00.000Z\", \"myInternalClass\": {\"myString\": \"inside...\"}, \"myInteger\": 12345}"), null);
		assertTrue(Map.class.isAssignableFrom(result.getClass()));
		Map map = (Map)result;
		assertEquals("inside...", ((Map) map.get("myInternalClass")).get("myString"));
	}
}
