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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author abiliomp
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PrinterConfiguration {
    
    /**
     * The printer's name or resource path.
     * XML serialized parameter
     */
    private String printerName;
    
    /**
     * The paper width. Default A4.
     * XML serialized parameter
     */
    private double paperWidth = 210.0;
    
    /**
     * The paper height. Default A4.
     * XML serialized parameter
     */
    
    private double paperHeight = 297.0;
    
    /**
     * Left margin value. Allows negative values.
     * Default value 0.
     * XML serialized parameter
     */
    
    private float marginLeft = 0;
    
    
    /**
     * Right margin value. Allows negative values.
     * Default value 0.
     * XML serialized parameter
     */
    
    private float marginRight = 0;
    
    /**
     * Top margin value. Allows negative values.
     * Default value 0.
     * XML serialized parameter
     */
    
    private float marginTop = 0;
    
    /**
     * Bottom margin value. Allows negative values.
     * Default value 0.
     * XML serialized parameter
     */
    
    private float marginBottom = 0;
    
    /**
     * The page printing orientation. Default PORTRAIT
     * XML serialized parameter
     * The values are defined in the enumeration {@link PageOrientation}
     */
    private PageOrientation pageOrientation = PageOrientation.PORTRAIT;
        
    /**
     * Sets this printer instance as the default printer. 
     * This value is used when no printer arg is specified.
     * XML serialized parameter
     * The values are defined in the enumeration {@link PageOrientation}
     */
    private boolean isDefault = false;
    
    // Constructor /////////////////////////////////////////////////////////////

    public PrinterConfiguration() {
        this.printerName = null;
    }
    
    public PrinterConfiguration(String printerName) {
        this.printerName = printerName;
    }
    
    // Getters /////////////////////////////////////////////////////////////////
    
    public String getPrinterName() {
        return printerName;
    }

    public double getPaperWidth() {
        return paperWidth;
    }

    public double getPaperHeight() {
        return paperHeight;
    }

    public PageOrientation getPageOrientation() {
        return pageOrientation;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public float getMarginBottom() {
        return marginBottom;
    }
    
    // Setters /////////////////////////////////////////////////////////////////
    
    /**
     * Sets the printer's name or resource path for the current printer configuration
     * @param printerName a string value containing the printer's name or resource path
     */
    public void SetPrinterName(String printerName) {
        this.printerName = printerName;
    }
    
    public void setPaperWidth(double paperWidth) {
        this.paperWidth = paperWidth;
    }

    public void setPaperHeight(double paperHeight) {
        this.paperHeight = paperHeight;
    }
    
    /**
     * Sets the page orientation.
     * @param pageOrientation enum value from {@link PageOrientation}
     */
    public void SetPageOrientation(PageOrientation pageOrientation) {
        this.pageOrientation = pageOrientation;
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }
    
}
