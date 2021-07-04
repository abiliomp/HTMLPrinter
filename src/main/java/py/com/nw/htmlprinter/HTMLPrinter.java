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

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.print.PrintService;
import javax.xml.bind.JAXBException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

/**
 *
 * @author abiliomp
 */
public class HTMLPrinter {
    
    private static final String HELP_ARG = "--help";
    private static final String HELP_ARG2 = "-h";
    private static final String PRINTER_ARG = "--printer";
    private static final String PRINTER_ARG2 = "-p";
    private static final String CSS_FILE_ARG = "--css";
    private static final String CHARSET_ARG = "--charset";
    private static final String TO_PDF_ARG = "--to-pdf";
    private static final String INIT_CONFIG_FILE_ARG = "--init";
    private static final String CONFIG_FILE_ARG = "--config";
    private static final String ERROR_LOG_ARG = "--error-log";
    private static final String RUN_SERVICE_ARG = "--run-service";
    private static final String RUN_SERVICE_ARG2 = "-s";
    private static final String WEBSOCKET_PORT_ARG = "--websocket-port";
    private static final String WEBSOCKET_PORT_ARG2 = "-w";
    private static final String SERVICE_ERROR_LOGS_FOLDER = "service_error_logs";
    
    public static void main(String[] args) {
        
        
        
        Logger errorLogger = Logger.getLogger("HTMLPrinterErrorLogger");  
        FileHandler errorLogFileHandler;         
        
        boolean doInitConfig = false;
        boolean doErrorLogging = false;
        boolean showHelp = false;
        Boolean runService = null;
        Integer servicePort = null;
        String htmlFilePath = null;
        String configFilePath = null;
        String cssFilePath = null;
        String charsetName = "UTF8";
        String pdfFileName = null;
        String printer = null;
        PrinterConfiguration printerConfig = null;
        
        htmlFilePath = args.length > 0 ? (args[0].startsWith("-") ? null : args[0]) : null;   
        
        for(int i = (htmlFilePath == null ? 0 : 1); i < args.length; i++){
            String arg = args[i];
            if(arg.compareTo(CSS_FILE_ARG) == 0){
                cssFilePath = args.length > (i + 1) ? args[i + 1] : null;
            }
            else if(arg.compareTo(PRINTER_ARG) == 0 || arg.compareTo(PRINTER_ARG2) == 0){
                printer = args.length > (i + 1) ? args[i + 1] : null;                
            }
            else if(arg.compareTo(CHARSET_ARG) == 0){
                charsetName = args.length > (i + 1) ? args[i + 1] : null;
            }
            else if(arg.compareTo(TO_PDF_ARG) == 0){
                pdfFileName = args.length > (i + 1) ? args[i + 1] : null;
            }
            else if(arg.compareTo(INIT_CONFIG_FILE_ARG) == 0){
                doInitConfig = true;
            }
            else if(arg.compareTo(CONFIG_FILE_ARG) == 0){
                configFilePath = args.length > (i + 1) ? args[i + 1] : null;
            }
            else if(arg.compareTo(ERROR_LOG_ARG) == 0){
                doErrorLogging = true;
                System.out.println("[HTMLPrinter] Error logging enabled.");
            }
            else if(arg.compareTo(HELP_ARG) == 0 || arg.compareTo(HELP_ARG2) == 0){
                showHelp = true;
            }
            else if(arg.compareTo(RUN_SERVICE_ARG) == 0 || arg.compareTo(RUN_SERVICE_ARG2) == 0){
                runService = true;
            }
            else if(arg.compareTo(WEBSOCKET_PORT_ARG) == 0 || arg.compareTo(WEBSOCKET_PORT_ARG2) == 0){
                servicePort = args.length > (i + 1) ? Integer.parseInt(args[i + 1]) : 3333;
            }
        }
        if(showHelp || args.length == 0){
            System.out.println("HTMLPrinter Program Help");
            System.out.println("Syntax: java -jar HTMLPrinter.jar <HTMLFilePath> [option1] [option1 argument]...");
            System.out.println("Options:");
            System.out.println("\t-h or --help : displays this help.");
            System.out.println("\t--init : initializes the configuration XML file.");
            System.out.println("\t--config <XMLConfigFilePath> : sets a custom configuration absolute file path (including file name). Default path is the JAR's folder.");            
            System.out.println("\t-p or --printer <PrinterName> : specifies the printer id to be used. The printer must be within the configuration file.");
            System.out.println("\t--css <CSSFilePath> : specifies an aditional CSS file to be applied.");
            System.out.println("\t--charset <CharsetName> : sets the charset to read the files (default UTF8).");
            System.out.println("\t--to-pdf <PDFFilePath> : generates a PDF document to be stored in the specified path.");
            System.out.println("\t--error-log : enables the error logging functionality. The program will generate one .log file with the same path of the HTML file.");
            System.out.println("\t-s or --run-service : run the program as a service daemon. This parameter will discard the HTMLFilePath if specified.");
            System.out.println("\t-w <port> or --websocket-port <port> : sets the service daemon websocket TCP port number. Default is 3333.");
            System.out.println("Supported Charsets: UTF8, UTF16, ASCII and 8859.");
            return;
        }
        
        // Start Config File Operations /////////////////////////////////////////////////////////////////////////////
        
        if(doInitConfig){
            // Init Config File ////////////////////////////////////////////////////////////////////////////
            System.out.print("[HTMLPrinter] Initializing the configuration file at \"");
            if(configFilePath != null){
                Configuration.SetCustomFilePath(configFilePath);
            }
            System.out.println(Configuration.GetConfigFilePath() + "\"");
            try {
                Configuration.Init();
            } catch (Exception ex) {
                System.err.println("[HTMLPrinter ERR] Error in while writing the configuration file:\n" + ex.getLocalizedMessage());
                System.err.println("[HTMLPrinter ERR] Stack trace: \n" + ex.getCause());
                ex.printStackTrace();
                return;
            }
            System.out.println("[HTMLPrinter] Configuration file initialized. Please configure the program now.");
            return;
        }        
           
        try {     
            // Read config file /////////////////////////////////////////////////////////////////////////////////     
            System.out.println("[HTMLPrinter] Reading configuration file...");
            Configuration.ReadConfiguration();
            // Override service values if specified in the args.
            if(runService != null)
                Configuration.SetRunAsService(runService);
            if(servicePort != null)
                Configuration.SetWebSocketPort(servicePort);
            System.out.println("[HTMLPrinter] Config file loaded!");            
        } catch (UnsupportedEncodingException | URISyntaxException | JAXBException ex) {
            String errMsg = "[HTMLPrinter ERR] Error in while reading the configuration file:\n" + ex.getLocalizedMessage();
            System.err.println(errMsg);
        } catch (FileNotFoundException ex) {
            System.err.println("[HTMLPrinter ERR] Configuration file not found. Please run the program with the --init option.");
            return;
        }
        // End Config File Operations   /////////////////////////////////////////////////////////////////////////////
        
        if(Configuration.IsRunAsService()){
            System.out.println("[HTMLPrinter] Starting service...");
            
            // Error logging setup /////////////////////////////////////////////
            if(doErrorLogging){
                try {
                    String logFolderURIString = Configuration.GetCurrentDir().toURI().toString() + SERVICE_ERROR_LOGS_FOLDER;
                    File logFolder = new File(new URI(logFolderURIString));
                    if(!logFolder.exists()){
                        System.out.println("[HTMLPrinter] Error logging folder does not exists.");
                        if(logFolder.mkdir()){
                            System.out.println("[HTMLPrinter] Error logging folder created!");
                        }
                        else{
                            System.err.println("[HTMLPrinter] Error logging folder could not be created.");
                            System.err.println("[HTMLPrinter] Check the permission to write in the program's folder or disable the error logging.");
                            return;
                        }
                    }
                    Calendar cal = Calendar.getInstance();
                    String logFileURI = logFolder.toURI() + Integer.toString(cal.get(Calendar.YEAR)) + String.format("%02d", cal.get(Calendar.MONTH) + 1) + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + ".log";
                    File logFile = new File(new URI(logFileURI));
                    errorLogFileHandler = new FileHandler(logFile.getAbsolutePath());
                    System.out.println("[HTMLPrinter] Logging errors to: " + logFile.getAbsolutePath());
                    errorLogger.addHandler(errorLogFileHandler);
                    SimpleFormatter formatter = new SimpleFormatter();  
                    errorLogFileHandler.setFormatter(formatter);
                } catch (URISyntaxException | IOException | SecurityException ex) {
                    String errMsg = "[HTMLPrinter ERR] " + ex.getLocalizedMessage();
                    System.err.println(errMsg);
                    return;
                }
            }
            //END  Error logging setup /////////////////////////////////////////
            
            // Server setup and launch /////////////////////////////////////////
            try {
            PrintServer printServer = new PrintServer(Configuration.GetWebSocketPort());
            System.out.println("[HTMLPrinter] Starting print server...");
            printServer.start();
            System.out.println("[HTMLPrinter] Print Server started on port: " + printServer.getPort());
                BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                  String in = sysin.readLine();
                  if (in.equals("exit")) {
                    printServer.stop(1000);
                    break;
                  }
                }
            }
            catch(UnknownHostException ex){
                String errMsg = "[HTMLPrinter ERR] UnknownHostException: " + ex.getLocalizedMessage();
                System.err.println(errMsg);
                if(doErrorLogging){
                    errorLogger.severe(errMsg);
                }
                return;
            } 
            catch (IOException ex) {
                String errMsg = "[HTMLPrinter ERR] IOException: " + ex.getLocalizedMessage();
                System.err.println(errMsg);
                if(doErrorLogging){
                    errorLogger.severe(errMsg);
                }
                return;
            } 
            catch (InterruptedException ex) {
                String errMsg = "[HTMLPrinter ERR] InterruptedException: " + ex.getLocalizedMessage();
                System.err.println(errMsg);
                if(doErrorLogging){
                    errorLogger.severe(errMsg);
                }
                return;
            }
            ////////////////////////////////////////////////////////////////////
        }
        else{
            // Check the html file path
            if(htmlFilePath == null){
                String errMsg = "[HTMLPrinter ERR] HTML file argument missing.";
                System.err.println(errMsg);
            }
            else{
                System.out.println("[HTMLPrinter] Reading specified HTML file...");
                File htmlFile = new File(htmlFilePath);            
                if(!htmlFile.exists()){
                    String errMsg = "[HTMLPrinter ERR] HTML file not found in \"" + htmlFilePath + "\" .";
                    System.err.println(errMsg);
                }            
                else{
                    if(doErrorLogging){
                        try {
                            errorLogFileHandler = new FileHandler(htmlFile.getAbsolutePath().replaceAll("\\.html", ".log"));
                        } catch (IOException | SecurityException ex) {
                            String errMsg = "[HTMLPrinter ERR] " + ex.getLocalizedMessage();
                            System.err.println(errMsg);
                            return;
                        }
                        errorLogger.addHandler(errorLogFileHandler);
                        SimpleFormatter formatter = new SimpleFormatter();  
                        errorLogFileHandler.setFormatter(formatter);
                    }
                    
                    // Check printer's configuration ///////////////////////////
                    if(printer != null){
                        System.out.println("[HTMLPrinter] Printer specified in the args: " + printer);
                        printerConfig = Configuration.GetPrinter(printer);
                        if(printerConfig == null){
                            String errMsg = "[HTMLPrinter ERR] Specified printer " + printer + " not found.";
                            if(doErrorLogging){
                                errorLogger.severe(errMsg);
                            }
                            System.err.println(errMsg);
                            return;
                        }
                        else{
                            System.out.println("[HTMLPrinter] " + printer + " configuration found.");
                        }
                    }
                    else{
                        printerConfig = Configuration.GetDefaultPrinter();
                        if(printerConfig == null){
                            String errMsg = "[HTMLPrinter ERR] No default printer found.";
                            if(doErrorLogging){
                                errorLogger.severe(errMsg);
                            }
                            System.err.println(errMsg);
                            return;
                        }
                    }
                    // END Check printer's configuration ///////////////////////

                    List<String> htmlLines = null;
                    try {
                        htmlLines = Files.readAllLines(htmlFile.toPath(), CharsetHelper.parse(charsetName));
                    } catch (IOException ex) {
                        String errMsg = "[HTMLPrinter ERR] " + ex.getLocalizedMessage();
                        if(doErrorLogging){
                            errorLogger.severe(errMsg);
                        }
                        System.err.println(errMsg);
                        return;
                    }
                    if(htmlLines != null){
                        // Read the HTML file
                        StringBuilder html = new StringBuilder();
                        for(String line : htmlLines)
                            html.append(line).append("\n");                    
                        System.out.println("[HTMLPrinter] HTML content loaded!");
                        // Start CSS File Operations /////////////////////////////////////////////////////////////////////////////
                        // Check if there is a CSS file specified
                        if(cssFilePath != null){
                            // Read the CSS file
                            System.out.println("[HTMLPrinter] Reading CSS file...");
                            File cssFile = new File(cssFilePath);            
                            if(!cssFile.exists()){
                                String errMsg = "[HTMLPrinter ERR] Specified CSS file not found in \"" + cssFilePath + "\" .";
                                if(doErrorLogging){
                                    errorLogger.severe(errMsg);
                                }
                                System.err.println(errMsg);
                                return;
                            }
                            // Build the CSS content
                            StringBuilder css = new StringBuilder("\n<style>");
                            List<String> cssLines = null;
                            try {
                                cssLines = Files.readAllLines(cssFile.toPath(), CharsetHelper.parse(charsetName));
                            } catch (IOException ex) {
                                String errMsg = "[HTMLPrinter ERR] " + ex.getLocalizedMessage();
                                if(doErrorLogging){
                                    errorLogger.severe(errMsg);
                                }
                                System.err.println(errMsg);
                            }
                            if(cssLines != null){
                                for(String line : cssLines)
                                    css.append(line).append("\n");
                            }
                            css.append("</style>\n");

                            // Insert the CSS content at the end of the HTML head.
                            int headIx = html.indexOf("</head>");
                            if(headIx > 0){
                                html.insert(headIx, css.toString());
                            }
                            else{
                                String errMsg = "[HTMLPrinter ERR] No HTML </head> tag found. Can't insert the specified CSS content.";
                                if(doErrorLogging){
                                    errorLogger.severe(errMsg);
                                }
                                System.err.println(errMsg);
                                return;
                            }
                            System.out.println("[HTMLPrinter] CSS content loaded!");
                        }
                        // End CSS File Operations //////////////////////////////////////////////////////////////////////////////////

                        // HTML contents are complete at this point....
                        System.out.println("[HTMLPrinter] Generating PDF content...");
                        PdfWriter pdfW = null;
                        ByteArrayOutputStream baos = null;
                        if(pdfFileName == null){
                            // In memory content to print
                            baos = new ByteArrayOutputStream();
                            pdfW = new PdfWriter(baos);
                        }
                        else{
                            // PDF to disk
                            File pdfFile = new File(pdfFileName);
                            try {
                                pdfW = new PdfWriter(pdfFile);
                            } 
                            catch (FileNotFoundException ex) {
                                String errMsg = "[HTMLPrinter ERR] " + ex.getLocalizedMessage();
                                if(doErrorLogging){
                                    errorLogger.severe(errMsg);
                                }
                                System.err.println(errMsg);
                                return;
                            }
                        }

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
                        convP.setCharset(charsetName);
                        HtmlConverter.convertToPdf(html.toString(), pdfDoc, convP);

                        System.out.println("[HTMLPrinter] PDF contents generated!");                   

                        if(baos != null){
                            // Printing part...
                            System.out.println("[HTMLPrinter] Sending contents to printer...");
                            PDDocument document;
                            try {
                                document = PDDocument.load(baos.toByteArray());
                            } catch (IOException ex) {
                                String errMsg = "[HTMLPrinter ERR] " + ex.getLocalizedMessage();
                                if(doErrorLogging){
                                    errorLogger.severe(errMsg);
                                }
                                System.err.println(errMsg);
                                return;
                            }

                            PrintService printerService = PrintServiceHelper.find(printerConfig.getPrinterName());
                            if(printerService != null){
                                PrinterJob job = PrinterJob.getPrinterJob();
                                job.setPageable(new PDFPageable(document));
                                try {
                                    job.setPrintService(printerService);
                                } catch (PrinterException ex) {
                                    String errMsg = "[HTMLPrinter ERR] " + ex.getLocalizedMessage();
                                    if(doErrorLogging){
                                        errorLogger.severe(errMsg);
                                    }
                                    System.err.println(errMsg);
                                    return;
                                }
                                try {
                                    job.print();
                                    System.out.println("[HTMLPrinter] Print complete!");
                                } catch (PrinterException ex) {
                                    String errMsg = "[HTMLPrinter ERR] " + ex.getLocalizedMessage();
                                    if(doErrorLogging){
                                        errorLogger.severe(errMsg);
                                    }
                                    System.err.println(errMsg);
                                }
                            }
                            else{
                                String errMsg = "[HTMLPrinter ERR] Specified printer not found! Check the configuration file.";
                                if(doErrorLogging){
                                    errorLogger.severe(errMsg);
                                }
                                System.err.println(errMsg);
                            }
                        }
                    }
                    else{
                        String errMsg = "[HTMLPrinter ERR] Specied HTML file is empty.";
                        if(doErrorLogging){
                            errorLogger.severe(errMsg);
                        }
                        System.err.println(errMsg);
                    }
                } 

            }
        }        
    }
    
    
    
}
