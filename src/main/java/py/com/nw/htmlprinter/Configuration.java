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
 * <p>
 * This is the configuration class for the HTMLPrinter program. It holds the basic parameters to setup the printing environment.
 * <p>
 * It is XML serializable and implements the basic file I/O operations out of the box.
 * <p>
 * There is only one static instance through which the parameters can be retrieved.
 * <p>
 * @author abiliomp
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {
    
    /**
     * A constant that holds the default configuration file name.
     */
    @XmlTransient
    private static final String FILENAME = "HTMLPrinterConfig.xml";
    
    /**
     * Constant that stores the convertion rate from user space to milimeters.
     */
    @XmlTransient
    private static final double DOTS_PER_MM = 25.4/72.0;
    
    /**
     * The custom file path, used in case it is specified at runtime.
     */
    @XmlTransient
    private static String CustomFilePath = null;
    
    /**
     * The configuration static instance that stores the retrieved config parameters.
     */
    @XmlTransient
    private static Configuration Config;
    
    /**
     * The printer's name or resource path.
     * XML serialized parameter
     */
    private String printerName = "NullPrinter";
    
    /**
     * The paper width.
     * XML serialized parameter
     */
    private double paperWidth = 210.0;
    
    /**
     * The paper height.
     * XML serialized parameter
     */
    
    private double paperHeight = 297.0;
    
    /**
     * The page printing orientation.
     * XML serialized parameter
     * The values are defined in the enumeration {@link PageOrientation}
     */
    private PageOrientation pageOrientation = PageOrientation.PORTRAIT;
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Private constructor. Only one static instance is needed / allowed.
     */
    private Configuration() {}

    ////////////////////////////////////////////////////////////////////////////

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
    
    /**
     * Sets the page orientation.
     * @param pageOrientation enum value from {@link PageOrientation}
     */
    public static void SetPageOrientation(PageOrientation pageOrientation) {
        Config.pageOrientation = pageOrientation;
    }
    
    /**
     * Sets a custom file path for the XML configuration file.
     * @param fullPath A string representing the full file path (filename and extension included).
     */
    public static void SetCustomFilePath(String fullPath){
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
