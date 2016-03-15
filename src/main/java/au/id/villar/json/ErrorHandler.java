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
 * Interface to be implemented by error handlers used by {@link JSONReader}. If the parser detects an error the parsing
 * process finishes and reports the error by executing the method {@link ErrorHandler#error(JSONReaderException)} on
 * this class.
 */
public interface ErrorHandler {

    /**
     * Method executed when {@link JSONReader} finds an error.
     * @param exception Exception object with information about the error.
     */
    void error(JSONReaderException exception);

}
