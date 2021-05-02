/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.nw.htmlprinter;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;

/**
 *
 * @author abili
 */
public class PageOrientationsEventHandler implements IEventHandler {
    
    public static final PdfNumber PORTRAIT = new PdfNumber(0);
    public static final PdfNumber LANDSCAPE = new PdfNumber(90);
    public static final PdfNumber INVERTEDPORTRAIT = new PdfNumber(180);
    public static final PdfNumber SEASCAPE = new PdfNumber(270);
    
    private PdfNumber orientation = PORTRAIT;

    public void setOrientation(PdfNumber orientation) {
        this.orientation = orientation;
    }

    @Override
    public void handleEvent(Event currentEvent) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
        docEvent.getPage().put(PdfName.Rotate, orientation);
    }
}