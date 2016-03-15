package au.id.villar.json;

import org.junit.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class JSONReaderTest {

    @Test
    public void test() throws IOException, JSONReaderException {
        List<Event> result = parse("{\"fieldFalse\": false, \"fieldTrue\": true, \"myObject\": {}, \"myString\":\"testing...\", \"myOtherObject\": {\"uno\": null, \"myArray\":[true, true, true], \"myNumber\":200}}");
        assertEquals(new Event(null, null, null, EventType.START_OBJECT), result.get(0));
        assertEquals(new Event("fieldFalse", ContentHandler.ValueType.FALSE, null, EventType.SIMPLE_VALUE), result.get(1));
        assertEquals(new Event("fieldTrue", ContentHandler.ValueType.TRUE, null, EventType.SIMPLE_VALUE), result.get(2));
        assertEquals(new Event("myObject", null, null, EventType.START_OBJECT), result.get(3));
        assertEquals(new Event(null, null, null, EventType.END_OBJECT), result.get(4));
        assertEquals(new Event("myString", ContentHandler.ValueType.STRING, "testing...", EventType.SIMPLE_VALUE), result.get(5));
        assertEquals(new Event("myOtherObject", null, null, EventType.START_OBJECT), result.get(6));
        assertEquals(new Event("uno", ContentHandler.ValueType.NULL, null, EventType.SIMPLE_VALUE), result.get(7));
        assertEquals(new Event("myArray", null, null, EventType.START_ARRAY), result.get(8));
        assertEquals(new Event(null, ContentHandler.ValueType.TRUE, null, EventType.SIMPLE_VALUE), result.get(9));
        assertEquals(new Event(null, ContentHandler.ValueType.TRUE, null, EventType.SIMPLE_VALUE), result.get(10));
        assertEquals(new Event(null, ContentHandler.ValueType.TRUE, null, EventType.SIMPLE_VALUE), result.get(11));
        assertEquals(new Event(null, null, null, EventType.END_ARRAY), result.get(12));
        assertEquals(new Event("myNumber", ContentHandler.ValueType.NUMBER, "200", EventType.SIMPLE_VALUE), result.get(13));
        assertEquals(new Event(null, null, null, EventType.END_OBJECT), result.get(14));
        assertEquals(new Event(null, null, null, EventType.END_OBJECT), result.get(15));
    }

    @Test
    public void emptyTest() throws IOException, JSONReaderException {
        List<Event> result = parse("");
        assertEquals(0, result.size());
    }

    @Test
    public void emptyObjectsTest() throws IOException, JSONReaderException {
        List<Event> result = parse("{}");
        assertEquals(2, result.size());
        assertEquals(new Event(null, null, null, EventType.START_OBJECT), result.get(0));
        assertEquals(new Event(null, null, null, EventType.END_OBJECT), result.get(1));
        result = parse("[]");
        assertEquals(2, result.size());
        assertEquals(new Event(null, null, null, EventType.START_ARRAY), result.get(0));
        assertEquals(new Event(null, null, null, EventType.END_ARRAY), result.get(1));
    }

    @Test
    public void blanksTest() throws IOException, JSONReaderException {
        List<Event> result = parse("    \t     {    \r    \"myVar\"  \n    :   \t    -56.78  \n   ,\t\"myArray\"  " +
                "  \n\r\t    :  \n  [ \t  1  \r  , \n   2  \t  , \r  3    \n   ] \t  , \r   \"myObj\" \t  : \n  { \n " +
                "  }  \r ,  \t  \"myOtherArray\"  \n  :  \r  [   \t  ]  \n  }  \t  ");


        assertEquals(new Event(null, null, null, EventType.START_OBJECT), result.get(0));
        assertEquals(new Event("myVar", ContentHandler.ValueType.NUMBER, "-56.78", EventType.SIMPLE_VALUE), result.get(1));
        assertEquals(new Event("myArray", null, null, EventType.START_ARRAY), result.get(2));
        assertEquals(new Event(null, ContentHandler.ValueType.NUMBER, "1", EventType.SIMPLE_VALUE), result.get(3));
        assertEquals(new Event(null, ContentHandler.ValueType.NUMBER, "2", EventType.SIMPLE_VALUE), result.get(4));
        assertEquals(new Event(null, ContentHandler.ValueType.NUMBER, "3", EventType.SIMPLE_VALUE), result.get(5));
        assertEquals(new Event(null, null, null, EventType.END_ARRAY), result.get(6));
        assertEquals(new Event("myObj", null, null, EventType.START_OBJECT), result.get(7));
        assertEquals(new Event(null, null, null, EventType.END_OBJECT), result.get(8));
        assertEquals(new Event("myOtherArray", null, null, EventType.START_ARRAY), result.get(9));
        assertEquals(new Event(null, null, null, EventType.END_ARRAY), result.get(10));
        assertEquals(new Event(null, null, null, EventType.END_OBJECT), result.get(11));
        assertEquals(12, result.size());
    }

    @Test(expected = JSONReaderException.class)
    public void error1Test() throws IOException, JSONReaderException {
        parse("{\"uno\" : 1}}");
    }

    @Test(expected = JSONReaderException.class)
    public void error2Test() throws IOException, JSONReaderException {
        parse("{\"uno\" : 1} {}");
    }

    @Test
    public void rootSingleRootValues() throws IOException, JSONReaderException {
        List<Event> result;
        result = parse("\"Hello world\"");
        assertEquals(new Event(null, ContentHandler.ValueType.STRING, "Hello world", EventType.SIMPLE_VALUE), result.get(0));
        result = parse("32.45");
        assertEquals(new Event(null, ContentHandler.ValueType.NUMBER, "32.45", EventType.SIMPLE_VALUE), result.get(0));
        result = parse("true");
        assertEquals(new Event(null, ContentHandler.ValueType.TRUE, null, EventType.SIMPLE_VALUE), result.get(0));
    }

    @Test
    public void rootNull() throws IOException, JSONReaderException {
        List<Event> result = parse("null");
        assertEquals(new Event(null, ContentHandler.ValueType.NULL, null, EventType.SIMPLE_VALUE), result.get(0));
    }

    @Test
    public void rootSingleRootValuesWithSpaces() throws IOException, JSONReaderException {
        List<Event> result;
        result = parse("   \n \"Hello world\"   \r");
        assertEquals(new Event(null, ContentHandler.ValueType.STRING, "Hello world", EventType.SIMPLE_VALUE), result.get(0));
        result = parse("   32.45   \t   ");
        assertEquals(new Event(null, ContentHandler.ValueType.NUMBER, "32.45", EventType.SIMPLE_VALUE), result.get(0));
        result = parse(" \n true   \n");
        assertEquals(new Event(null, ContentHandler.ValueType.TRUE, null, EventType.SIMPLE_VALUE), result.get(0));
    }


    private List<Event> parse(String json) throws IOException, JSONReaderException {
        JSONReader reader = new JSONReader();
        StringReader input = new StringReader(json);
        TestContentHandler handler = new TestContentHandler();
        TestErrorHandler errorHandler = new TestErrorHandler();
        reader.setInput(input);
        reader.setContentHandler(handler);
        reader.setErrorHandler(errorHandler);
        reader.parse();
        JSONReaderException exception = errorHandler.getException();
        if(exception != null) {
            throw errorHandler.getException();
        }
        return handler.getEvents();
    }

    private enum EventType {
        START_OBJECT,
        END_OBJECT,
        START_ARRAY,
        END_ARRAY,
        SIMPLE_VALUE
    }

    private class Event {

        private Event(String name, ContentHandler.ValueType valueType, String value, EventType eventType) {
            this.name = name;
            this.valueType = valueType;
            this.value = value;
            this.eventType = eventType;
        }

        public String name;
        public ContentHandler.ValueType valueType;
        public String value;
        public EventType eventType;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Event event = (Event) o;

            return eventType == event.eventType
                    && (name != null ? name.equals(event.name) : event.name == null)
                    && (value != null ? value.equals(event.value) : event.value == null)
                    && valueType == event.valueType;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "name='" + name + '\'' +
                    ", valueType=" + valueType +
                    ", value='" + value + '\'' +
                    ", eventType=" + eventType +
                    '}';
        }
    }

    private class TestErrorHandler implements ErrorHandler {

        private JSONReaderException exception;

        public JSONReaderException getException() {
            return exception;
        }

        @Override
        public void error(JSONReaderException exception) {
            this.exception = exception;
        }
    }

    private class TestContentHandler implements ContentHandler {

        private ArrayList<Event> events = new ArrayList<>();

        @Override
        public void startObject(StringBuilder name) {
            events.add(new Event(name == null? null: name.toString(), null, null, EventType.START_OBJECT));
        }

        @Override
        public void endObject() {
            events.add(new Event(null, null, null, EventType.END_OBJECT));
        }

        @Override
        public void startArray(StringBuilder name) {
            events.add(new Event(name == null? null: name.toString(), null, null, EventType.START_ARRAY));
        }

        @Override
        public void endArray() {
            events.add(new Event(null, null, null, EventType.END_ARRAY));
        }

        @Override
        public void simpleValue(StringBuilder name, StringBuilder value, ValueType type) {
            events.add(new Event(name == null? null: name.toString(), type, value == null? null: value.toString(),
                    EventType.SIMPLE_VALUE));
        }

        public ArrayList<Event> getEvents() {
            return events;
        }
    }

}
