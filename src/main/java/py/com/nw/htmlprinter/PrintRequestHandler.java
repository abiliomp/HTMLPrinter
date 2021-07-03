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

import com.google.gson.JsonSyntaxException;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.print.PrintService;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterState;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.java_websocket.WebSocket;

/**
 *
 * @author abiliomp
 */
public class PrintRequestHandler implements Runnable{
    
    private WebSocket conn;
    private String message;

    public PrintRequestHandler(WebSocket conn, String message) {
        this.conn = conn;
        this.message = message;
    }

    @Override
    public void run() {
        String charset = null;
        PrintResponseMessage prm;
        System.out.println("[PrintServer] Message received from " + conn + ": \n" + message);
        System.out.println("[PrintServer] Decoding PrintRequest...");
        try{
            PrintRequestMessage pr = PrintRequestMessage.fromJson(message);
            if(pr != null){
                System.out.println("[PrintServer] PrintRequest decoded.");
                // Check the printer config
                PrinterConfiguration printerConfig;
                if(pr.getPrinterId() != null){
                    System.out.println("[PrintServer] Checking for " + pr.getPrinterId() + " printer ID configuration...");
                    printerConfig = Configuration.GetPrinter(pr.getPrinterId());
                }
                else{
                    System.out.println("[PrintServer] Checking default printer configuration...");
                    printerConfig = Configuration.GetDefaultPrinter();
                }
                if(printerConfig == null){
                    System.err.println("[PrintServer] The printer was not found in the configuration of this service.");
                    prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_ERROR, PrintResponseMessage.REQUEST_STATUS_REJECTED, "The printer was not found in the configuration of this service.");
                    conn.send(prm.toJson());
                }
                else{
                    // Printer config found. Check the printer's status
                    System.out.println("[PrintServer] Configuration found. Printer name: " + printerConfig.getPrinterName());
                    PrintService printerService = PrintServiceHelper.find(printerConfig.getPrinterName());
                    if(printerService == null){
                        System.err.println("[PrintServer] Printer not found. Check the configuration and server setup.");
                        prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_ERROR, PrintResponseMessage.REQUEST_STATUS_REJECTED, "Printer not found. Check the configuration and server setup.");
                        conn.send(prm.toJson());       
                    }
                    else{
                        PrinterState prnState = (PrinterState) printerService.getAttribute(PrinterState.class);
                        if(prnState == PrinterState.STOPPED){
                            System.err.println("[PrintServer] Printer is not available.");
                            prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_ERROR, PrintResponseMessage.REQUEST_STATUS_REJECTED, "Printer is not available.");
                            conn.send(prm.toJson());
                        }
                        if(prnState == PrinterState.PROCESSING){
                            System.err.println("[PrintServer] Printer is processing previous jobs.");
                            prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_BUSY, PrintResponseMessage.REQUEST_STATUS_REJECTED, "Printer is processing previous jobs.");
                            conn.send(prm.toJson());
                        }
                        else if((PrinterIsAcceptingJobs) (printerService.getAttribute(PrinterIsAcceptingJobs.class)) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS){
                                System.err.println("[PrintServer] Printer is not accepting new jobs.");
                                prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_ERROR, PrintResponseMessage.REQUEST_STATUS_REJECTED, "Printer is not accepting new jobs.");
                                conn.send(prm.toJson());
                        }
                        else{
                            // Printer is idle and ready to print //////////////
                            System.out.println("[PrintServer] OK! Request received and printer available.");
                            prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_FREE, PrintResponseMessage.REQUEST_STATUS_RECEIVED, "Request received and printer available.");
                            conn.send(prm.toJson());
                            // Check the charset
                            charset = pr.getCharset().name();
                            
                            System.out.println("[PrintService] Generating PDF content...");
                            PdfWriter pdfW = null;
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            pdfW = new PdfWriter(baos);
                            PdfDocument pdfDoc = new PdfDocument(pdfW);
                            PageOrientationsEventHandler eventHandler = new PageOrientationsEventHandler();
                            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, eventHandler);
                            PageSize pz = new PageSize((float) Configuration.mmToDots(printerConfig.getPaperWidth()), (float) Configuration.mmToDots(printerConfig.getPaperHeight()));
                            switch(printerConfig.getPageOrientation()){
                                case LANDSCAPE:
                                    eventHandler.setOrientation(PageOrientationsEventHandler.LANDSCAPE);
                                    pz = pz.rotate();
                                    break;
                                case INVERTEDPORTRAIT:
                                    eventHandler.setOrientation(PageOrientationsEventHandler.INVERTEDPORTRAIT);
                                    break;
                                case SEASCAPE:
                                    eventHandler.setOrientation(PageOrientationsEventHandler.SEASCAPE);
                                    pz = pz.rotate();
                                    break;
                                default:
                                    eventHandler.setOrientation(PageOrientationsEventHandler.PORTRAIT);
                                    break;
                            }
                            pdfDoc.setDefaultPageSize(pz);
                            ConverterProperties convP = new ConverterProperties();
                            convP.setCharset(charset);
                            HtmlConverter.convertToPdf(pr.getHtml(), pdfDoc, convP);
                            System.out.println("[PrintService] PDF contents generated!");   
                            
                            // Printing part...
                            System.out.println("[PrintService] Sending contents to printer...");
                            prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_BUSY, PrintResponseMessage.REQUEST_STATUS_PROCESSING, "Printing started.");
                            conn.send(prm.toJson());
                            
                            try {
                                PDDocument document = PDDocument.load(baos.toByteArray());
                                PrinterJob job = PrinterJob.getPrinterJob();
                                job.setPageable(new PDFPageable(document));
                                try { 
                                    job.setPrintService(printerService);
                                    job.print();
                                    System.out.println("[PrintService] Printing complete.");
                                    prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_FREE, PrintResponseMessage.REQUEST_STATUS_DONE, "Printing complete.");
                                    conn.send(prm.toJson());
                                } catch (PrinterException ex) {
                                    String errMsg = "[PrintService ERR] PrintException: " + ex.getLocalizedMessage();
                                    System.err.println(errMsg);
                                    prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_ERROR, PrintResponseMessage.REQUEST_STATUS_REJECTED, "PrintException occurred: " + ex.getLocalizedMessage());
                                    conn.send(prm.toJson());
                                }
                            } catch (IOException ex) {
                                String errMsg = "[PrintService ERR] " + ex.getLocalizedMessage();
                                System.err.println(errMsg);
                                prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_ERROR, PrintResponseMessage.REQUEST_STATUS_REJECTED, "IOException occurred: " + ex.getLocalizedMessage());
                                conn.send(prm.toJson());
                            }
                            ////////////////////////////////////////////////////
                        }
                    }
                }
            }
        }
        catch(JsonSyntaxException ex){
            prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_ERROR, PrintResponseMessage.REQUEST_STATUS_REJECTED, "An error ocurred during the message processing: " + ex.getLocalizedMessage());
            conn.send(prm.toJson());
        }
    }
    
    
    
}
