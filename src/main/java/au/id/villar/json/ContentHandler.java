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

public interface ContentHandler {

	void startObject(StringBuilder name) throws JSONReaderException;

	void endObject() throws JSONReaderException;

	void startArray(StringBuilder name) throws JSONReaderException;

	void endArray() throws JSONReaderException;

	void simpleValue(StringBuilder name, StringBuilder value, ValueType type) throws JSONReaderException;

	public static enum ValueType {
		STRING,
		NUMBER,
		TRUE,
		FALSE,
		NULL
	}
}
