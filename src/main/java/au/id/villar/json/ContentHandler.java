/*
 * Morgano-json library to convert between POJOs and JSON
 * Copyright (c) 2016 Rafael Villar Villar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package au.id.villar.json;

/**
 * <p>Interface to be implemented by event handlers used by {@link JSONReader}.</p>
 * <p>The following is an example of the way the methods of this interface are called by {@link JSONReader}:</p>
 *
 * <p>lets suppose we have this input:</p>
 * <pre>
 *      {
 *          "id": 95,
 *          "active": true,
 *          "info": {"lastUpdated": "1980-07-25"}
 *          "items": [{"name": "item1"}, {"name": "item2"}],
 *          "magicNumbers": [45, 80]
 *      }
 * </pre>
 * <p>The events generated will be:</p>
 * <ul>
 *     <li>startObject(null)</li>
 *     <li>simpleValue("id", "95", NUMBER)</li>
 *     <li>simpleValue("active", null, TRUE)</li>
 *     <li>startObject("info")</li>
 *     <li>simpleValue("lastUpdated", "1980-07-25")</li>
 *     <li>endObject()</li>
 *     <li>startArray("items")</li>
 *     <li>startObject(null)</li>
 *     <li>simpleValue("name", "item1")</li>
 *     <li>endObject()</li>
 *     <li>startObject(null)</li>
 *     <li>simpleValue("name", "item2")</li>
 *     <li>endObject()</li>
 *     <li>endArray()</li>
 *     <li>startArray("magicNumbers")</li>
 *     <li>simpleValue(null, "45")</li>
 *     <li>simpleValue(null, "80")</li>
 *     <li>endArray()</li>
 * </ul>
 */
public interface ContentHandler {

    /**
     * Called by {@link JSONReader} when the beginning of an object is detected. It happens when a '{' is read from the
     * input. If the object is found inside another object, then the given parameter will contain the name of the field
     * whose value is represented by the found object; otherwise the given parameter will be null.
     * @param name name of the field whose value is represented by this object if this is inside another object;
     *             otherwise is null.
     * @throws JSONReaderException
     */
    void startObject(StringBuilder name) throws JSONReaderException;

    /**
     * Called by {@link JSONReader} when the end of an object is detected. It happens when a '}' is read from the
     * input.
     * @throws JSONReaderException
     */
    void endObject() throws JSONReaderException;

    /**
     * Called by {@link JSONReader} when the beginning of an array is detected. It happens when a '[' is read from the
     * input. If the array is found inside an object, then the given parameter will contain the name of the field
     * whose value is represented by the found array; otherwise the given parameter will be null.
     * @param name name of the field whose value is represented by this array if this is inside an object; otherwise is
     *             null.
     * @throws JSONReaderException
     */
    void startArray(StringBuilder name) throws JSONReaderException;

    /**
     * Called by {@link JSONReader} when the end of an array is detected. It happens when a ']' is read from the
     * input.
     * @throws JSONReaderException
     */
    void endArray() throws JSONReaderException;

    /**
     * Called by {@link JSONReader} when a simple value is detected. If it is the value for a object's field then the
     * name of the field is specified by the parameter "name"; otherwise it is null. If the value is a number or a
     * string then the value itself is given in the parameter "value" and the value of "type" is NUMBER or STRING
     * respectively. If the value is a boolean or null, then "value" is set to null and "type" will have the value (
     * TRUE, FALSE or NULL)
     * @param name name of the field whose value is represented by this value.
     * @param value value itself if the type is {@link ValueType#STRING} or {@link ValueType#NUMBER}, null otherwise.
     * @param type one of {@link ValueType#STRING}, {@link ValueType#NUMBER}, {@link ValueType#TRUE},
     * {@link ValueType#FALSE} or {@link ValueType#NULL}
     * @throws JSONReaderException
     */
    void simpleValue(StringBuilder name, StringBuilder value, ValueType type) throws JSONReaderException;

    /** Type of value that a field can be. */
    enum ValueType {
        STRING,
        NUMBER,
        TRUE,
        FALSE,
        NULL
    }
}
