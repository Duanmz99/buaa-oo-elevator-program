import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.ElevatorInput;

public class Producer extends Thread {
    private RequestList dlist;
    private ElevatorInput elevatorInput = new ElevatorInput(System.in);
    
    public Producer(RequestList r) {
        this.dlist = r;
    }
    
    public void run() {
        try {
            while (true) {
                PersonRequest request = elevatorInput.nextPersonRequest();
                // when request == null
                // it means there are no more lines in stdin
                if (request == null) {
                    dlist.addRequest(null); // 此时在子类的addlist方法已经让end为1
                    dlist.setEnd();
                    break;
                } else {
                    // a new valid request
                    dlist.addRequest(request); // addRequest方法自动转化为UpersonRequest
                }
            }
            elevatorInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}