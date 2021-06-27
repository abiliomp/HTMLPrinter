
package py.com.nw.htmlprinter;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

/**
 *
 * @author Byron Kiourtzoglou https://examples.javacodegeeks.com/desktop-java/print/determine-that-print-job-has-finished/
 * 
 */
public class JobCompleteMonitor extends PrintJobAdapter { 
    
    private boolean completed = false;
 
    @Override
    public void printJobCanceled(PrintJobEvent pje) {
        signalCompletion();
    }
 
    @Override
    public void printJobCompleted(PrintJobEvent pje) {
        signalCompletion();
    }
 
    @Override
    public void printJobFailed(PrintJobEvent pje) {
        signalCompletion();
    }
 
    @Override
    public void printJobNoMoreEvents(PrintJobEvent pje) {
        signalCompletion();
    }
    
    private void signalCompletion() {
        synchronized (JobCompleteMonitor.this) {
            completed = true;
            JobCompleteMonitor.this.notify();
        }
    }
    
    public synchronized void waitForJobCompletion() {
        try{
            while (!completed){
                wait();
            }
        }
        catch (InterruptedException e){}
    }
}