package au.id.villar.json;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;

public class ObjectSerializerTest {

	@Test
	public void fieldsSerializerTest() throws IOException {

		class MyLocalClass {
			public boolean myBoolean;
			public char myChar;
			public byte myByte;
			public short myShort;
			public int myInteger;
			public long myLong;
			public float myFloat;
			public double myDouble;
			public String myString;
			public Object myNullValue;
		}

		MyLocalClass local = new MyLocalClass();
		StringWriter writer = new StringWriter();

		local.myBoolean = true;
		local.myChar = 'T';
		local.myByte = 100;
		local.myShort = 200;
		local.myInteger = 400;
		local.myLong = 800;
		local.myFloat = 4.0F;
		local.myDouble = 8.0;
		local.myString = "thisIsMyString";

		ObjectSerializer.write(local, writer);

		String result = writer.getBuffer().toString();

		checkExpected("\"myBoolean\":true", result);
		checkExpected("\"myChar\":\"T\"", result);
		checkExpected("\"myByte\":100", result);
		checkExpected("\"myShort\":200", result);
		checkExpected("\"myInteger\":400", result);
		checkExpected("\"myLong\":800", result);
		checkExpected("\"myFloat\":4.0", result);
		checkExpected("\"myDouble\":8.0", result);
		checkExpected("\"myString\":\"thisIsMyString\"", result);
		checkExpected("\"myNullValue\":null", result);
		assertEquals(163, result.length());
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
	}

	@Test
	public void basicsSerializerTest() throws IOException {

		class MyLocalClass {
			private boolean myInternalBoolean;
			private char myInternalChar;
			private byte myInternalByte;
			private short myInternalShort;
			private int myInternalInteger;
			private long myInternalLong;
			private float myInternalFloat;
			private double myInternalDouble;
			private String myInternalString;
			private Object myInternalNullValue;

			public boolean isMyBoolean() { return myInternalBoolean; }
			public void setMyBoolean(boolean myBoolean) { this.myInternalBoolean = myBoolean; }

			public char getMyChar() { return myInternalChar; }
			public void setMyChar(char myChar) { this.myInternalChar = myChar; }

			public byte getMyByte() { return myInternalByte; }
			public void setMyByte(byte myByte) { this.myInternalByte = myByte; }

			public short getMyShort() { return myInternalShort; }
			public void setMyShort(short myShort) { this.myInternalShort = myShort; }

			public int getMyInteger() { return myInternalInteger; }
			public void setMyInteger(int myInteger) { this.myInternalInteger = myInteger; }

			public long getMyLong() { return myInternalLong; }
			public void setMyLong(long myLong) { this.myInternalLong = myLong; }

			public float getMyFloat() { return myInternalFloat; }
			public void setMyFloat(float myFloat) { this.myInternalFloat = myFloat; }

			public double getMyDouble() { return myInternalDouble; }
			public void setMyDouble(double myDouble) { this.myInternalDouble = myDouble; }

			public String getMyString() { return myInternalString; }
			public void setMyString(String myString) { this.myInternalString = myString; }

			public Object getMyNullValue() { return myInternalNullValue; }
			public void setMyNullValue(Object myNullValue) { this.myInternalNullValue = myNullValue; }
		}

		MyLocalClass local = new MyLocalClass();
		StringWriter writer = new StringWriter();

		local.setMyBoolean(true);
		local.setMyChar('T');
		local.setMyByte((byte) 100);
		local.setMyShort((short)200);
		local.setMyInteger(400);
		local.setMyLong(800);
		local.setMyFloat(4.0F);
		local.setMyDouble(8.0);
		local.setMyString("thisIsMyString");

		ObjectSerializer.write(local, writer);

		String result = writer.getBuffer().toString();

		checkExpected("\"myBoolean\":true", result);
		checkExpected("\"myChar\":\"T\"", result);
		checkExpected("\"myByte\":100", result);
		checkExpected("\"myShort\":200", result);
		checkExpected("\"myInteger\":400", result);
		checkExpected("\"myLong\":800", result);
		checkExpected("\"myFloat\":4.0", result);
		checkExpected("\"myDouble\":8.0", result);
		checkExpected("\"myString\":\"thisIsMyString\"", result);
		checkExpected("\"myNullValue\":null", result);
		assertEquals(163, result.length());
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
	}

	@Test
	public void arraysSerializerTest() throws IOException {

		class MyLocalClass {
			private boolean[] myInternalBooleanArray;
			private char[] myInternalCharArray;
			private byte[] myInternalByteArray;
			private short[] myInternalShortArray;
			private int[] myInternalIntegerArray;
			private long[] myInternalLongArray;
			private float[] myInternalFloatArray;
			private double[] myInternalDoubleArray;
			private Object[] myInternalObjectArray;

			public boolean[] getMyBooleanArray() { return myInternalBooleanArray; }
			public void setMyBooleanArray(boolean[] myBooleanArray) { this.myInternalBooleanArray = myBooleanArray; }

			public char[] getMyCharArray() { return myInternalCharArray; }
			public void setMyCharArray(char[] myCharArray) { this.myInternalCharArray = myCharArray; }

			public byte[] getMyByteArray() { return myInternalByteArray; }
			public void setMyByteArray(byte[] myByteArray) { this.myInternalByteArray = myByteArray; }

			public short[] getMyShortArray() { return myInternalShortArray; }
			public void setMyShortArray(short[] myShortArray) { this.myInternalShortArray = myShortArray; }

			public int[] getMyIntegerArray() { return myInternalIntegerArray; }
			public void setMyIntegerArray(int[] myIntegerArray) { this.myInternalIntegerArray = myIntegerArray; }

			public long[] getMyLongArray() { return myInternalLongArray; }
			public void setMyLongArray(long[] myLongArray) { this.myInternalLongArray = myLongArray; }

			public float[] getMyFloatArray() { return myInternalFloatArray; }
			public void setMyFloatArray(float[] myFloatArray) { this.myInternalFloatArray = myFloatArray; }

			public double[] getMyDoubleArray() { return myInternalDoubleArray; }
			public void setMyDoubleArray(double[] myDoubleArray) { this.myInternalDoubleArray = myDoubleArray; }

			public Object[] getMyObjectArray() { return myInternalObjectArray; }
			public void setMyObjectArray(Object[] myObjectArray) { this.myInternalObjectArray = myObjectArray; }
		}

		MyLocalClass local = new MyLocalClass();
		StringWriter writer = new StringWriter();

		local.setMyBooleanArray(new boolean[]{true, false, true});
		local.setMyCharArray(new char[]{'A', 'B', 'C'});
		local.setMyByteArray(new byte[]{1, 2, 3});
		local.setMyShortArray(new short[]{2, 4, 6});
		local.setMyIntegerArray(new int[]{4, 8, 12});
		local.setMyLongArray(new long[]{8, 16, 24});
		local.setMyFloatArray(new float[]{4F, 8F, 12F});
		local.setMyDoubleArray(new double[]{8D, 16D, 24D});
		local.setMyObjectArray(new Object[]{"uno", new Object(), null});

		ObjectSerializer.write(local, writer);

		String result = writer.getBuffer().toString();

		checkExpected("\"myBooleanArray\":[true,false,true]", result);
		checkExpected("\"myCharArray\":[\"A\",\"B\",\"C\"]", result);
		checkExpected("\"myByteArray\":[1,2,3]", result);
		checkExpected("\"myShortArray\":[2,4,6]", result);
		checkExpected("\"myIntegerArray\":[4,8,12]", result);
		checkExpected("\"myLongArray\":[8,16,24]", result);
		checkExpected("\"myFloatArray\":[4.0,8.0,12.0]", result);
		checkExpected("\"myDoubleArray\":[8.0,16.0,24.0]", result);
		checkExpected("\"myObjectArray\":[\"uno\",{},null]", result);
		assertEquals(253, result.length());
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
	}

	@Test
	public void objectSerializerTest() throws IOException{

		class MyInternalLocalClass {
			public String myString = "Hola mundo";
		}

		class MyLocalClass {
			private MyInternalLocalClass myObject;

			public MyInternalLocalClass getMyObject() { return myObject; }
			public void setMyObject(MyInternalLocalClass myObject) { this.myObject = myObject; }
		}

		MyLocalClass local = new MyLocalClass();
		StringWriter writer = new StringWriter();

		local.setMyObject(new MyInternalLocalClass());

		ObjectSerializer.write(local, writer);

		String result = writer.getBuffer().toString();

		assertEquals("{\"myObject\":{\"myString\":\"Hola mundo\"}}", result);
	}

	@Test
	public void nonRecursiveNullTest() throws IOException {

		class MyLocalClass {
			public MyLocalClass myValue;
		}

		MyLocalClass local = new MyLocalClass();
		StringWriter writer = new StringWriter();

		ObjectSerializer.write(local, writer);
	}

	@Test(expected = RecursiveException.class)
	public void recursiveError1Test() throws IOException {

		class MyLocalClass {
			public MyLocalClass myValue;
		}

		MyLocalClass local = new MyLocalClass();
		StringWriter writer = new StringWriter();

		local.myValue = local;

		ObjectSerializer.write(local, writer);
	}

	@Test(expected = RecursiveException.class)
	public void recursiveError2Test() throws IOException {

		class MyLocalClass {
			public MyLocalClass[] myValue;
		}

		MyLocalClass local = new MyLocalClass();
		StringWriter writer = new StringWriter();

		local.myValue = new MyLocalClass[] {local};

		ObjectSerializer.write(local, writer);
	}

	@Test(expected = RecursiveException.class)
	public void recursiveError3Test() throws IOException {

		class MyLocalClass {
			public Object myValue;
		}

		class MyLocalClass2 {
			public Object myValue;
		}

		MyLocalClass local = new MyLocalClass();
		MyLocalClass2 local2 = new MyLocalClass2();
		StringWriter writer = new StringWriter();

		local.myValue = local2;
		local2.myValue = local;

		ObjectSerializer.write(local, writer);
	}

	@Test
	public void dontIncludeSerializer1Test() throws IOException {

		@TransientJSON({"myBoolean", "myByte", "myShort", "myInteger", "myLong", "myFloat", "myDouble", "myString", "myNullValue"})
		class MyLocalClass {
			private boolean myInternalBoolean;
			private char myInternalChar;
			private byte myInternalByte;
			private short myInternalShort;
			private int myInternalInteger;
			private long myInternalLong;
			private float myInternalFloat;
			private double myInternalDouble;
			private String myInternalString;
			private Object myInternalNullValue;

			public boolean isMyBoolean() { return myInternalBoolean; }
			public void setMyBoolean(boolean myBoolean) { this.myInternalBoolean = myBoolean; }

			public char getMyChar() { return myInternalChar; }
			public void setMyChar(char myChar) { this.myInternalChar = myChar; }

			public byte getMyByte() { return myInternalByte; }
			public void setMyByte(byte myByte) { this.myInternalByte = myByte; }

			public short getMyShort() { return myInternalShort; }
			public void setMyShort(short myShort) { this.myInternalShort = myShort; }

			public int getMyInteger() { return myInternalInteger; }
			public void setMyInteger(int myInteger) { this.myInternalInteger = myInteger; }

			public long getMyLong() { return myInternalLong; }
			public void setMyLong(long myLong) { this.myInternalLong = myLong; }

			public float getMyFloat() { return myInternalFloat; }
			public void setMyFloat(float myFloat) { this.myInternalFloat = myFloat; }

			public double getMyDouble() { return myInternalDouble; }
			public void setMyDouble(double myDouble) { this.myInternalDouble = myDouble; }

			public String getMyString() { return myInternalString; }
			public void setMyString(String myString) { this.myInternalString = myString; }

			public Object getMyNullValue() { return myInternalNullValue; }
			public void setMyNullValue(Object myNullValue) { this.myInternalNullValue = myNullValue; }
		}

		MyLocalClass local = new MyLocalClass();
		StringWriter writer = new StringWriter();

		local.setMyBoolean(true);
		local.setMyChar('T');
		local.setMyByte((byte) 100);
		local.setMyShort((short)200);
		local.setMyInteger(400);
		local.setMyLong(800);
		local.setMyFloat(4.0F);
		local.setMyDouble(8.0);
		local.setMyString("thisIsMyString");

		ObjectSerializer.write(local, writer);

		String result = writer.getBuffer().toString();

		assertEquals("{\"myChar\":\"T\"}", result);
	}

	@Test
	public void dontIncludeSerializer2Test() throws IOException {

		class MyLocalClass {
			private boolean myInternalBoolean;
			private char myInternalChar;
			private byte myInternalByte;
			private short myInternalShort;
			private int myInternalInteger;
			private long myInternalLong;
			private float myInternalFloat;
			private double myInternalDouble;
			private String myInternalString;
			private Object myInternalNullValue;

			@TransientJSON()
			public boolean isMyBoolean() { return myInternalBoolean; }
			public void setMyBoolean(boolean myBoolean) { this.myInternalBoolean = myBoolean; }

			public char getMyChar() { return myInternalChar; }
			public void setMyChar(char myChar) { this.myInternalChar = myChar; }

			@TransientJSON()
			public byte getMyByte() { return myInternalByte; }
			public void setMyByte(byte myByte) { this.myInternalByte = myByte; }

			@TransientJSON()
			public short getMyShort() { return myInternalShort; }
			public void setMyShort(short myShort) { this.myInternalShort = myShort; }

			@TransientJSON()
			public int getMyInteger() { return myInternalInteger; }
			public void setMyInteger(int myInteger) { this.myInternalInteger = myInteger; }

			@TransientJSON()
			public long getMyLong() { return myInternalLong; }
			public void setMyLong(long myLong) { this.myInternalLong = myLong; }

			@TransientJSON()
			public float getMyFloat() { return myInternalFloat; }
			public void setMyFloat(float myFloat) { this.myInternalFloat = myFloat; }

			@TransientJSON()
			public double getMyDouble() { return myInternalDouble; }
			public void setMyDouble(double myDouble) { this.myInternalDouble = myDouble; }

			@TransientJSON()
			public String getMyString() { return myInternalString; }
			public void setMyString(String myString) { this.myInternalString = myString; }

			@TransientJSON()
			public Object getMyNullValue() { return myInternalNullValue; }
			public void setMyNullValue(Object myNullValue) { this.myInternalNullValue = myNullValue; }
		}

		MyLocalClass local = new MyLocalClass();
		StringWriter writer = new StringWriter();

		local.setMyBoolean(true);
		local.setMyChar('T');
		local.setMyByte((byte) 100);
		local.setMyShort((short)200);
		local.setMyInteger(400);
		local.setMyLong(800);
		local.setMyFloat(4.0F);
		local.setMyDouble(8.0);
		local.setMyString("thisIsMyString");

		ObjectSerializer.write(local, writer);

		String result = writer.getBuffer().toString();

		assertEquals("{\"myChar\":\"T\"}", result);
	}

	@Test
	public void listTest() throws IOException {

		class MyClass { public int myInt; }

		ArrayList<MyClass> list = new ArrayList<MyClass>();

		for(int x = 0; x < 5; x++) {
			MyClass newClass = new MyClass();
			newClass.myInt = x + 1;
			list.add(newClass);
		}

		StringWriter writer = new StringWriter();

		ObjectSerializer.write(list, writer);

		String result = writer.getBuffer().toString();
		assertEquals("[{\"myInt\":1},{\"myInt\":2},{\"myInt\":3},{\"myInt\":4},{\"myInt\":5}]", result);
	}

	@Test
	public void setTest() throws IOException {

		Set<String> myObj = new HashSet<String>();
		myObj.add("uno");
		myObj.add("dos");
		myObj.add("tres");

		StringWriter writer = new StringWriter();

		ObjectSerializer.write(myObj, writer);

		String result = writer.getBuffer().toString();
		String[] tokens = result.split("[\"\\[\\], ]");
		myObj = new HashSet<String>();
		for(String token: tokens) {
			if(!"".equals(token)) {
				myObj.add(token);
			}
		}
		assertEquals(3, myObj.size());
		assertTrue(myObj.contains("uno"));
		assertTrue(myObj.contains("dos"));
		assertTrue(myObj.contains("tres"));
	}

	@Test
	public void escapedCharsTest() throws IOException {

		class MyClass { public String myString = "first line\nsecond line\nsome chars: \u00f1\u00d1\tthis is after a tab\n\n...two lines after"; }
		MyClass myClass = new MyClass();

		StringWriter writer = new StringWriter();

		ObjectSerializer.write(myClass, writer);

		assertEquals("{\"myString\":\"first line\\nsecond line\\nsome chars: \\u00F1\\u00D1\\tthis is after a tab\\n\\n...two lines after\"}", writer.getBuffer().toString());
	}

	enum MyTestEnum { ONE, TWO, THREE }

	@Test
	public void enumTest() throws IOException {

		class MyClass { public int myInt; public MyTestEnum myEnumValue; }

		MyClass object = new MyClass();
		object.myEnumValue = MyTestEnum.TWO;
		object.myInt = 2;

		StringWriter writer = new StringWriter();

		ObjectSerializer.write(object, writer);

		assertEquals("{\"myInt\":2,\"myEnumValue\":\"TWO\"}", writer.getBuffer().toString());
	}

	private void checkExpected(String expected, String jsonResult) {
		if(!jsonResult.contains(expected)) {
			fail("JSON doesn't contain: " + expected);
		}
	}

}
