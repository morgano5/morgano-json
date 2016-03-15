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
 *
 */

package au.id.villar.json;

import java.io.IOException;
import java.io.Reader;

/**
 * Object to parse from a {@link java.io.Reader} JSON data. See {@link ContentHandler} and {@link ErrorHandler} for
 * an example of how the input is processed.
 */
public class JSONReader {

    private static final int BUFFER_LENGTH = 2048;

    private Reader input;
    private ContentHandler contentHandler;
    private ErrorHandler errorHandler;

    private char[] buffer = new char[BUFFER_LENGTH];
    private int bufferIndex = 0;
    private int bufferUsed = 0;
    private StringBuilder charStack = new StringBuilder(10);
    private StringBuilder fieldName = new StringBuilder(60);
    private boolean fieldNameNull = true;

    /**
     * Sets the {@link ContentHandler} used to handle the events generated when invoking {@link JSONReader#parse()} on
     * this JSONReader.
     * @param contentHandler Handler that will handle events from this JSONReader.
     */
    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    /**
     * Sets the {@link ErrorHandler} used to handle the errors generated when invoking {@link JSONReader#parse()} on
     * this JSONReader.
     * @param errorHandler Handler that will handle errors from this JSONReader.
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Sets the input from where this JSONReader is going to read JSON data. The given {@link java.io.Reader input} is
     * never closed.
     * @param input input from where this JSONReader is going to read JSON data.
     */
    public void setInput(Reader input) {
        this.input = input;
    }

    /**
     * Starts the process of parsing. It reads from a reader (given through {@link JSONReader#setInput(Reader)}
     * setInput()) and calls methods in the given {@link ContentHandler} and {@link ErrorHandler} accordingly.
     */
    public void parse() {

        verifyInputAndHandlersPresent();

        StringBuilder fieldValue = new StringBuilder(60);

        int readChar;

        try {
            while((readChar = read()) != -1) {
                switch(readChar) {
                    case ' ': case '\n': case '\r': case '\t':
                        break;
                    case '{':
                        charStack.append('{');
                        contentHandler.startObject(fieldNameNull ? null : fieldName);
                        if(!readFieldNameToNextValue()) {
                            charStack.delete(charStack.length() - 1, charStack.length());
                            contentHandler.endObject();
                            readToNextValue();
                        }
                        break;
                    case '[':
                        charStack.append('[');
                        contentHandler.startArray(fieldNameNull? null: fieldName);
                        fieldName.delete(0, fieldName.length());
                        fieldNameNull = true;
                        break;
                    case ']':
                        readToNextValue(']');
                        break;
                    case 'f':
                        verifyLiteral("false");
                        contentHandler.simpleValue(fieldNameNull ? null : fieldName, null, ContentHandler.ValueType.FALSE);
                        fieldNameNull = true;
                        readToNextValue();
                        break;
                    case 't':
                        verifyLiteral("true");
                        contentHandler.simpleValue(fieldNameNull ? null : fieldName, null, ContentHandler.ValueType.TRUE);
                        fieldNameNull = true;
                        readToNextValue();
                        break;
                    case 'n':
                        verifyLiteral("null");
                        contentHandler.simpleValue(fieldNameNull ? null : fieldName, null, ContentHandler.ValueType.NULL);
                        fieldNameNull = true;
                        readToNextValue();
                        break;
                    case '"':
                        fieldValue.delete(0, fieldValue.length());
                        readRestOfString(fieldValue);
                        contentHandler.simpleValue(fieldNameNull ? null : fieldName, fieldValue, ContentHandler.ValueType.STRING);
                        fieldNameNull = true;
                        readToNextValue();
                        break;
                    case '-': case '0': case '1': case '2': case '3': case '4':
                    case '5': case '6': case '7': case '8': case '9':
                        fieldValue.delete(0, fieldValue.length()).append((char)readChar);
                        readChar = readRestOfNumber(fieldValue);
                        contentHandler.simpleValue(fieldNameNull? null: fieldName, fieldValue, ContentHandler.ValueType.NUMBER);
                        if(readChar == -1 && charStack.length() == 0) break;
                        fieldNameNull = true;
                        readToNextValue(readChar);
                        break;
                    default:
                        throw new JSONReaderException("Unexpected character: [" + (char)readChar + "], value expected");
                }
            }
        } catch (JSONReaderException e) {
            if(errorHandler != null) {
                errorHandler.error(e);
            }
        } catch (IOException e) {
            if(errorHandler != null) {
                errorHandler.error(new JSONReaderException(e));
            }
        }
    }

    private void readRestOfString(StringBuilder builder) throws IOException, JSONReaderException {
        int readChar = read();

        stringLoop:
        while(readChar != '"' && readChar > 31) {
            if(readChar == '\\') {
                readChar = read();
                if(readChar < 32)
                    break;
                switch (readChar) {
                    case '"': case '\\': case '/':  builder.append((char)readChar); break;
                    case 'b':                       builder.append((char)8);        break;
                    case 'f':                       builder.append((char)12);       break;
                    case 'n':                       builder.append((char)10);       break;
                    case 'r':                       builder.append((char)13);       break;
                    case 't':                       builder.append((char)9);        break;
                    case 'u':
                        char hex = 0;
                        for(int x = 0; x < 4; x++) {
                            readChar = read();
                            if(readChar < 32)
                                break stringLoop;
                            if(readChar >= '0' && readChar <= '9') {
                                hex |= (readChar - 48) << (4 * (3 - x));
                            } else if(readChar >= 'A' && readChar <= 'F') {
                                hex |= (readChar - 55) << (4 * (3 - x));
                            } else if(readChar >= 'a' && readChar <= 'f') {
                                hex |= (readChar - 87) << (4 * (3 - x));
                            } else {
                                throw new JSONReaderException("expected hexadecimal digit, found instead: [" + readChar + "]");
                            }
                        }
                        builder.append(hex);
                        break;
                    default:
                        break stringLoop;
                }
            } else {
                builder.append((char)readChar);
            }
            readChar = read();
        }
        if(readChar == -1)
            throw new JSONReaderException("unexpected end of data");
        if(readChar < 32)
            throw new JSONReaderException("unexpected control character: [" + readChar + "]");
        if(readChar != '"')
            throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
    }

    private int readRestOfNumber(StringBuilder builder) throws IOException, JSONReaderException {
        int readChar = builder.charAt(0);

        final int START_0       = 1, START_MINUS   = 2, START_1_9     = 3, DOT           = 4,
                  FRACTION      = 5, E             = 6, EXPONENT_SIGN = 7, EXPONENT      = 8;

        int status;

        switch(readChar) {
            case '0': status = START_0; break;
            case '-': status = START_MINUS; break;
            case '1':case '2':case '3':case '4':case '5':
            case '6':case '7':case '8':case '9': status = START_1_9; break;
            default: throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
        }

        while((readChar = read()) != -1) {
            switch(status) {
                case START_0:
                    switch(readChar) {
                        case ' ': return ' ';
                        case ',': return ',';
                        case '}': return '}';
                        case ']': return ']';
                        case '.': status = DOT; break;
                        case 'e': status = E; break;
                        case 'E': status = E; break;
                        default: throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
                    }
                    break;
                case START_MINUS:
                    switch(readChar) {
                        case '0': status = START_0; break;
                        case '1': case '2': case '3': case '4': case '5':
                        case '6': case '7': case '8': case '9': status = START_1_9; break;
                        default: throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
                    }
                    break;
                case START_1_9:
                    switch(readChar) {
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': status = START_1_9; break;
                        case ' ': return ' ';
                        case ',': return ',';
                        case '}': return '}';
                        case ']': return ']';
                        case '.': status = DOT; break;
                        case 'e': status = E; break;
                        case 'E': status = E; break;
                        default: throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
                    }
                    break;
                case DOT:
                    if(readChar >= '0' && readChar <= '9')
                        status = FRACTION;
                    else
                        throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
                    break;
                case FRACTION:
                    switch(readChar) {
                        case ' ': return ' ';
                        case ',': return ',';
                        case '}': return '}';
                        case ']': return ']';
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': status = FRACTION; break;
                        case 'e': status = E; break;
                        case 'E': status = E; break;
                        default: throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
                    }
                    break;
                case E:
                    switch(readChar) {
                        case '+': status = EXPONENT_SIGN; break;
                        case '-': status = EXPONENT_SIGN; break;
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': status = EXPONENT; break;
                        default: throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
                    }
                    break;
                case EXPONENT_SIGN:
                    if(readChar >= '0' && readChar <= '9')
                        status = EXPONENT;
                    else
                        throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
                    break;
                case EXPONENT:
                    switch(readChar) {
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': status = EXPONENT; break;
                        case ' ': return ' ';
                        case ',': return ',';
                        case '}': return '}';
                        case ']': return ']';
                        default: throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
                    }
                    break;
                default:
                    throw new JSONReaderException("programming error parsing a number (debug this)");
            }
            builder.append((char)readChar);
        }
        return -1;
    }

    private int readSkippingWhites() throws IOException, JSONReaderException {
        int readChar;
        do {readChar = read();} while (readChar == ' ' || readChar == '\n' || readChar == '\r' || readChar == '\t');
        if(readChar == -1)
            throw new JSONReaderException("unexpected end of data");
        return readChar;
    }

    private void verifyLiteral(String literal)
            throws IOException, JSONReaderException {
        for(int pos = 1; pos < literal.length(); pos++) {
            char charAtPos = literal.charAt(pos);
            int readChar = read();
            if(readChar == -1)
                throw new JSONReaderException("unexpected end of data");
            if(charAtPos != readChar)
                throw new JSONReaderException("unknown literal. Expected: \"" + literal + "\"");
        }
    }

    private void readToNextValue() throws IOException, JSONReaderException {
        readToNextValue(0, false);
    }

    private void readToNextValue(int initialValue) throws IOException, JSONReaderException {
        readToNextValue(initialValue, true);
    }

    private void readToNextValue(int initialValue, boolean getInitialValue) throws IOException, JSONReaderException {
        char last;
        int lastCharPosition = charStack.length() - 1;
        int readChar = getInitialValue? initialValue: read();
        boolean nextValue = false;
        while(!nextValue) {
            switch(readChar) {
                case ' ': case '\n': case '\r': case '\t':
                    readChar = read();
                    break;
                case ',':
                    if(lastCharPosition < 0) {
                        throw new JSONReaderException("expected end of data, but still got a character: [,]");
                    }
                    last = charStack.charAt(lastCharPosition);
                    if(last == '{') {
                        if(!readFieldNameToNextValue()) {
                            readChar = '}';
                            break;
                        }
                    }
                    nextValue = true;
                    break;
                case '}':
                    if(lastCharPosition < 0) {
                        throw new JSONReaderException("expected end of data, but still got a character: [}]");
                    }
                    last = charStack.charAt(lastCharPosition);
                    charStack.delete(lastCharPosition, lastCharPosition + 1);
                    lastCharPosition--;
                    if(last != '{')
                        throw new JSONReaderException("expected [,] or \"]\"");
                    contentHandler.endObject();
                    readChar = read();
                    break;
                case ']':
                    if(lastCharPosition < 0) {
                        throw new JSONReaderException("expected end of data, but still got a character: \"]\"");
                    }
                    last = charStack.charAt(lastCharPosition);
                    charStack.delete(lastCharPosition, lastCharPosition + 1);
                    lastCharPosition--;
                    if(last != '[')
                        throw new JSONReaderException("expected [,] or [}]");
                    contentHandler.endArray();
                    readChar = read();
                    break;
                case -1:
                    if(lastCharPosition > 0)
                        throw new JSONReaderException("unexpected end of data");
                    nextValue = true;
                    break;
                default:
                    throw new JSONReaderException("unexpected character: [" + (char)readChar + "]");
            }
        }
    }

    private boolean readFieldNameToNextValue() throws IOException, JSONReaderException {
        int readChar = readSkippingWhites();
        if(readChar == '}')
            return false;
        if(readChar != '"')
            throw new JSONReaderException("expected: [\"]");
        fieldName.delete(0, fieldName.length());
        readRestOfString(fieldName);
        fieldNameNull = false;
        readChar = readSkippingWhites();
        if(readChar != ':')
            throw new JSONReaderException("expected: [:]");
        return true;
    }

    private int read() throws IOException {
        if(bufferIndex >= bufferUsed) {
            bufferUsed = input.read(buffer);
            if (bufferUsed == -1) return -1;
            bufferIndex = 0;
        }
        return buffer[bufferIndex++];
    }

    private void verifyInputAndHandlersPresent() {
        if(input == null)
            throw new NullPointerException("An input has not been specified yet, set an input with setInput()");
        if(contentHandler == null)
            throw new NullPointerException(
                    "A ContentHandler has not been specified yet, set a ContentHandler with setContentHandler()");
        if(errorHandler == null)
            throw new NullPointerException(
                    "An ErrorHandler has not been specified yet, set an ErrorHandler with setErrorHandler()");

    }
}
