/*
 * The MIT License
 *
 * Copyright 2021 Networkers SRL.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package py.com.nw.htmlprinter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author abiliomp
 */
public class CharsetHelper {
    
    private CharsetHelper(){}
    
    public static Charset parse(String charsetName){
        if(charsetName.compareTo("UTF8") == 0 || charsetName.compareTo("UTF-8") == 0 || charsetName.compareTo("UTF_8") == 0){
            return StandardCharsets.UTF_8;
        }
        else if(charsetName.compareTo("ASCII") == 0){
            return StandardCharsets.US_ASCII;
        }
        else if(charsetName.compareTo("8859") == 0){
            return StandardCharsets.ISO_8859_1;
        }
        else if(charsetName.compareTo("UTF16") == 0 || charsetName.compareTo("UTF-16") == 0 || charsetName.compareTo("UTF_16") == 0){
            return StandardCharsets.UTF_16;
        }
        else{
            return null;
        }
    }
}
