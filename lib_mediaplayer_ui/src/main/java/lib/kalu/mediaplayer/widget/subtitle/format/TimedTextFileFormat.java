/**
 * Class that represents the .ASS and .SSA subtitle file format
 *
 * <br><br>
 * Copyright (c) 2012 J. David Requejo <br>
 * j[dot]david[dot]requejo[at] Gmail
 * <br><br>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * <br><br>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <br><br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * @author J. David REQUEJO
 */

package lib.kalu.mediaplayer.widget.subtitle.format;

import java.io.IOException;
import java.io.InputStream;

import lib.kalu.mediaplayer.widget.subtitle.exception.FatalParsingException;
import lib.kalu.mediaplayer.widget.subtitle.model.TimedTextObject;

public interface TimedTextFileFormat {

    /**
     * This methods receives the path to a file, parses it, and returns a TimedTextObject
     *
     * @return TimedTextObject representing the parsed file
     * @throws IOException when having trouble reading the file from the given path
     */
    TimedTextObject parseFile(String fileName, InputStream is) throws IOException, FatalParsingException;

    /**
     * This method transforms a given TimedTextObject into a formated subtitle file
     *
     * @param tto the object to transform into a file
     * @return NULL if the given TimedTextObject has not been built first,
     * 		or String[] where each String is at least a line, if size is 2, then the file has at least two lines.
     * 		or byte[] in case the file is a binary (as is the case of STL format)
     */
    Object toFile(TimedTextObject tto);


}
