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

/**
 * Date: 27/04/12
 * Time: 9:21 PM
 */
public class JSONReaderException extends Exception {

	public JSONReaderException(String message) {
		super(message);
	}

	public JSONReaderException(Throwable cause) {
		super(cause);
	}

	public JSONReaderException(String message, Throwable cause) {
		super(message, cause);
	}
}
