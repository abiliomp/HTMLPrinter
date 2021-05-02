/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.nw.htmlprinter;

import java.awt.print.PageFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author abili
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {
    
    @XmlTransient
    private static final String FILENAME = "HTMLPrinterConfig.xml";
    
    @XmlTransient
    private static String CustomFilePath = null;
    
    
    @XmlTransient
    private static final double DOTS_PER_MM = 25.4/72.0;
    
    @XmlTransient
    private static Configuration Config;
    
    @XmlTransient
    private PageFormat defaultPageFormat;
    
    private String printerName = "NullPrinter";
    private double paperWidth = 210.0;
    private double paperHeight = 297.0;
    private PageOrientation pageOrientation = PageOrientation.PORTRAIT;
    
    ////////////////////////////////////////////////////////////////////////////
    
    private Configuration() {}

    ////////////////////////////////////////////////////////////////////////////

    public static PageFormat GetDefaultPageFormat() {
        return Config.defaultPageFormat;
    }

    public static String GetPrinterName() {
        return Config.printerName;
    }

    public static double GetPaperWidth() {
        return Config.paperWidth;
    }

    public static double GetPaperHeight() {
        return Config.paperHeight;
    }

    public static PageOrientation GetPageOrientation() {
        return Config.pageOrientation;
    }
    
    public static String GetCustomFullFilePath(){
        return CustomFilePath;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static void SetPrinterName(String printerName) {
        Config.printerName = printerName;
    }
    
    public static void SetPageOrientationd(PageOrientation pageOrientation) {
        Config.pageOrientation = pageOrientation;
    }
    
    public static void SetCustomFullFilePath(String fullPath){
        CustomFilePath = fullPath;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static URI GetConfigFileURI() throws URISyntaxException, UnsupportedEncodingException{
        URI programDirURI = GetCurrentDir().toURI();
        String configFileURIString = programDirURI.toString() + FILENAME;
        return new URI(configFileURIString);
    }
    
    public static void Init() throws Exception{
        Config = new Configuration();
        SaveConfiguration();        
    }
    
    public static void SaveConfiguration() throws JAXBException, Exception{
        File file;
        if(CustomFilePath == null)
            file = new File(GetConfigFileURI());
        else
            file = new File(CustomFilePath);
        JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(Config, file);
    }
    
    public static void ReadConfiguration() throws JAXBException, FileNotFoundException, URISyntaxException, UnsupportedEncodingException{
        File file;
        if(CustomFilePath == null)
            file = new File(GetConfigFileURI());
        else
            file = new File(CustomFilePath);
        if(!file.exists()){
            Config = null;
            throw new FileNotFoundException();
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Config = (Configuration) jaxbUnmarshaller.unmarshal(file);
    }
    
    public static void DeleteConfigFile() throws URISyntaxException, UnsupportedEncodingException{
        File file;
        if(CustomFilePath == null)
            file = new File(GetConfigFileURI());
        else
            file = new File(CustomFilePath);
        if(file.exists()){
           file.delete();
        }
    }
    
    private static File GetCurrentDir() throws URISyntaxException, UnsupportedEncodingException{        
        File file = new File(HTMLPrinter.class.getProtectionDomain().getCodeSource().getLocation().toURI());       
        File parent = file.getParentFile();
        if(parent.isDirectory()){
            return parent;
        }
        else{
            throw new RuntimeException("Invalid config file location.");
        }
    }
    
    public static String GetConfigFilePath(){
        if(CustomFilePath != null){
            return CustomFilePath;
        }
        else{
            try {
                return GetConfigFileURI().toString();
            } catch (URISyntaxException | UnsupportedEncodingException ex) {
                String errMsg = "[Configuration ERR] " + ex.getLocalizedMessage();
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, errMsg);
                System.err.println(errMsg);
            }
        }
        return "";
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static double mmToDots(double value){
        return (value / DOTS_PER_MM);
    }
    
    public static double dotsToMm(double value){
        return (value * DOTS_PER_MM);
    }
    
}
