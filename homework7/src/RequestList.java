import java.util.ArrayList;
import java.util.List;

import com.oocourse.elevator3.PersonRequest;

public class RequestList {
    private final List<UpersonRequest> rlist;
    private int end;
    
    public RequestList(int max) {
        this.rlist = new ArrayList<UpersonRequest>();
        end = 0;
    }
    
    protected synchronized UpersonRequest getRequest(int i) {
        while (rlist.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        UpersonRequest p = rlist.get(i);
        notifyAll();
        return p;
    }
    
    protected synchronized void addRequest(PersonRequest p) {
        if (p == null) {
            end = 1;
            this.rlist.add(null);
        } else {
            UpersonRequest u = new UpersonRequest(p);
            this.rlist.add(u);
        }
        notifyAll();
        
    }
    
    protected synchronized void removeRequest(int i) {
        this.rlist.remove(i);
    }
    
    protected synchronized int getEnd() {
        return this.end;
    } // 最后需要判断是否需要加null
    
    protected synchronized void setEnd() {
        this.rlist.add(null);
        this.end = 1;
    }
    
    protected synchronized boolean isEmpty() {
        return this.rlist.isEmpty(); // 为1表示空，否则不空
    }
    
    protected synchronized int getSize() {
        return rlist.size();
    }
    
    protected synchronized boolean isFull(int max) {
        return this.rlist.size() >= max;
    }
    
}