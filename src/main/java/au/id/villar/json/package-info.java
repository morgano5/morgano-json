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

/**
 * <p>Contains utilities to work with JSON code. These classes allow to parse JSON code from a java.io.Reader to get,
 * java objects and get JSON code from java objects.</p>
 *
 * <p>To parse a JSON stream in an analog way to XML SAX:</p>
 * <ul>
 *     <li>Implement a {@link au.id.villar.json.ContentHandler} and an {@link au.id.villar.json.ErrorHandler}
 *     (see documentation for these interfaces).</li>
 *     <li>Create an instance of {@link au.id.villar.json.JSONReader}.</li>
 *     <li>Use the implementations of first step and the input (must be a {@link java.io.Reader}) to set:
 *      <ul>
 *          <li>contentHandler</li>
 *          <li>errorHandler</li>
 *          <li>input</li>
 *      </ul>
 *     </li>
 *     <li>Invoke parse() in the JSONReader object</li>
 * </ul>
 *
 * <p>To get an object serialized and written in a {@link java.io.Writer},
 * use {@link au.id.villar.json.ObjectSerializer#write(Object, java.io.Writer)}</p>
 *
 * <p>To get an object deserialized from a {@link java.io.Reader}, use one of these static methods:</p>
 * <ul>
 *     <li>{@link au.id.villar.json.ObjectDeserializer#getFromReader(java.io.Reader)}</li>
 *     <li>{@link au.id.villar.json.ObjectDeserializer#getFromReader(java.io.Reader, Class)}</li>
 *     <li>{@link au.id.villar.json.ObjectDeserializer#internalGetFromReader(java.io.Reader, Object)}</li>
 * </ul>
 */
package au.id.villar.json;
