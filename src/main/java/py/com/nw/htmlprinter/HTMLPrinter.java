/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.xml.bind.JAXBException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

/**
 *
 * @author abili
 */
public class HTMLPrinter {
    
    private static final String Help1Arg = "--help";
    private static final String Help2Arg = "-h";
    private static final String CssFileArg = "--css";
    private static final String CharsetArg = "--charset";
    private static final String ToPDFArg = "--to-pdf";
    private static final String InitConfigFileArg = "--init";
    private static final String ConfigFileArg = "--config";
    private static final String ErrorLogArg = "--error-log";
    
    private static final String UserDir = System.getProperty("user.home") + "/";
    
    
    public static void main(String[] args) {
        
        Logger errorLogger = Logger.getLogger("HTMLPrinterErrorLogger");  
        FileHandler errorLogFileHandler;         
        
        boolean doInitConfig = false;
        boolean doErrorLogging = false;
        boolean showHelp = false;
        String htmlFilePath = null;
        String configFilePath = null;
        String cssFilePath = null;
        String charsetName = "UTF8";
        String pdfFileName = null;
        
        htmlFilePath = args.length > 0 ? (args[0].startsWith("-") ? null : args[0]) : null;   
        
        for(int i = (htmlFilePath == null ? 0 : 1); i < args.length; i++){
            String arg = args[i];
            if(arg.compareTo(CssFileArg) == 0){
                cssFilePath = args.length > (i + 1) ? args[i + 1] : null;
            }
            else if(arg.compareTo(CharsetArg) == 0){
                charsetName = args.length > (i + 1) ? args[i + 1] : null;
            }
            else if(arg.compareTo(ToPDFArg) == 0){
                pdfFileName = args.length > (i + 1) ? args[i + 1] : null;
            }
            else if(arg.compareTo(InitConfigFileArg) == 0){
                doInitConfig = true;
            }
            else if(arg.compareTo(ConfigFileArg) == 0){
                configFilePath = args.length > (i + 1) ? args[i + 1] : null;
            }
            else if(arg.compareTo(ErrorLogArg) == 0){
                doErrorLogging = true;
            }
            else if(arg.compareTo(Help1Arg) == 0 && arg.compareTo(Help2Arg) == 0){
                showHelp = true;
            }
        }
        if(showHelp || args.length == 0){
            System.out.println("HTMLPrinter Program Help");
            System.out.println("Syntax: java -jar HTMLPrinter.jar <HTMLFilePath> [option1] [option1 argument]...");
            System.out.println("Options:");
            System.out.println("\t-h or --help : displays this help.");
            System.out.println("\t--css <CSSFilePath> : specifies an aditional CSS file to be applied.");
            System.out.println("\t--charset <CharsetName> : sets the charset to read the files (default UTF8).");
            System.out.println("\t--config <XMLConfigFilePath> : sets a custom configuration absolute file path (including file name). Default path is the JAR's folder.");
            System.out.println("\t--init : initializes the configuration XML file.");
            System.out.println("\t--to-pdf <PDFFilePath>: generates a PDF document to be stored in the specified path.");
            System.out.println("\t--error-log : enables the error logging functionality. The program will generate one .log file with the same path of the HTML file.");
            System.out.println("Supported Charsets: UTF8, UTF16, ASCII and 8859.");
            return;
        }
        
        // Start Config File Operations /////////////////////////////////////////////////////////////////////////////
        
        if(doInitConfig){
            // Init Config File ////////////////////////////////////////////////////////////////////////////
            System.out.print("[HTMLPrinter] Initializing the configuration file at \"");
            if(configFilePath != null){
                Configuration.SetCustomFullFilePath(configFilePath);
            }
            System.out.println(Configuration.GetConfigFilePath() + "\"");
            try {
                Configuration.Init();
            } catch (Exception ex) {
                String errMsg = "[HTMLPrinter ERR] Error in while writing the configuration file:\n" + ex.getLocalizedMessage();
                System.err.println(errMsg);
            }
            System.out.println("[HTMLPrinter] Configuration file initialized. Please configure the program now.");
            return;
        }        
           
        try {     
            // Read config file /////////////////////////////////////////////////////////////////////////////////     
            System.out.println("[HTMLPrinter] Reading configuration file...");
            Configuration.ReadConfiguration();
            System.out.println("[HTMLPrinter] Config file loaded!");
            
        } catch (UnsupportedEncodingException | URISyntaxException | JAXBException ex) {
            String errMsg = "[HTMLPrinter ERR] Error in while reading the configuration file:\n" + ex.getLocalizedMessage();
            System.err.println(errMsg);
        } catch (FileNotFoundException ex) {
            System.err.println("[HTMLPrinter ERR] Configuration file not found. Please run the program with the --init option.");
            return;
        }
        // End Config File Operations   /////////////////////////////////////////////////////////////////////////////
        
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
                
                List<String> htmlLines = null;
                try {
                    htmlLines = Files.readAllLines(htmlFile.toPath(), charsetParser(charsetName));
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
                            cssLines = Files.readAllLines(cssFile.toPath(), charsetParser(charsetName));
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
                    PageSize pz = new PageSize((float) Configuration.mmToDots(Configuration.GetPaperWidth()), (float) Configuration.mmToDots(Configuration.GetPaperHeight()));
                    switch(Configuration.GetPageOrientation()){
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

                        PrintService printer = findPrintService(Configuration.GetPrinterName());
                        if(printer != null){
                            PrinterJob job = PrinterJob.getPrinterJob();
                            job.setPageable(new PDFPageable(document));
                            try {
                                job.setPrintService(printer);
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

    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
    
    private static Charset charsetParser(String charsetName){
        if(charsetName.compareTo("UTF8") == 0){
            return StandardCharsets.UTF_8;
        }
        else if(charsetName.compareTo("ASCII") == 0){
            return StandardCharsets.US_ASCII;
        }
        else if(charsetName.compareTo("8859") == 0){
            return StandardCharsets.ISO_8859_1;
        }
        else if(charsetName.compareTo("UTF16") == 0){
            return StandardCharsets.UTF_16;
        }
        else{
            return null;
        }
    }
    
}
