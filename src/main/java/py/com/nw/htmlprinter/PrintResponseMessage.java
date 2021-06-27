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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author abiliomp
 */
public class PrintResponseMessage {
    
    public static final String PRINTER_STATUS_FREE = "Free";
    public static final String PRINTER_STATUS_BUSY = "Busy";
    public static final String PRINTER_STATUS_OFF = "Offline";
    public static final String PRINTER_STATUS_ERROR = "Error";
    
    public static final String REQUEST_STATUS_RECEIVED = "Received";
    public static final String REQUEST_STATUS_PROCESSING = "Processing";
    public static final String REQUEST_STATUS_DONE = "Done";
    public static final String REQUEST_STATUS_REJECTED = "Rejected";
    
    private String printerStatus;
    private String requestStatus;
    private String message;

    public PrintResponseMessage(String printerStatus, String requestStatus, String message) {
        this.printerStatus = printerStatus;
        this.requestStatus = requestStatus;
        this.message = message;
    }

    public String getPrinterStatus() {
        return printerStatus;
    }

    public void setPrinterStatus(String printerStatus) {
        this.printerStatus = printerStatus;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String toJson(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(this);
    }
    
    public static PrintRequestMessage fromJson(String message){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(message, PrintRequestMessage.class);
    }
    
}
