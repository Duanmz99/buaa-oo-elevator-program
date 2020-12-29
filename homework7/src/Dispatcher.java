import com.oocourse.elevator3.PersonRequest;

public class Dispatcher extends Thread { // 调度器，作为总调度接口与剩下三个电梯进行交互
    private final RequestList firstList; // 第一次的请求队列
    private final RequestList secondList; // 一个电梯无法运送完毕，第二次过来的中转请求队列
    private final RequestList aList;
    private final RequestList bList;
    private final RequestList cList;
    private int[] accessA = {-3, -2, -1, 1, 15, 16, 17, 18, 19, 20};
    private int[] accessB = {-2, -1, 1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private int[] accessC = {1, 3, 5, 7, 9, 11, 13, 15};
    
    public Dispatcher(RequestList r, RequestList s, RequestList a, RequestList b, RequestList c) {
        this.firstList = r;
        this.secondList = s;
        this.aList = a;
        this.bList = b;
        this.cList = c;
    }
    
    public void run() {
        try {
            while (true) { // 此函数负责向子调度器分发指令，功能较为简单
                int list = 0;
                if (!firstList.isEmpty()) {
                    UpersonRequest request = firstList.getRequest(0);
                    // when request == null
                    // it means there are no more lines in stdin
                    if (request == null) {
                        aList.setEnd();
                        bList.setEnd();
                        cList.setEnd();
                        firstList.removeRequest(0);
                    }
                }
                // 开始判断所有程序是否全部退出
                if (secondList.isEmpty() && aList.isEmpty() && bList.isEmpty() && cList.isEmpty()
                        && firstList.getEnd() == 1) { // 默认调度器是最后一个结束的
                    System.exit(0);
                }
                
                //判断队列里是否有可能有进入的请求
                while (this.firstList.isEmpty() && this.secondList.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                UpersonRequest p = new UpersonRequest(new PersonRequest(0, 0, 0));
                if (!firstList.isEmpty()) {
                    p = firstList.getRequest(0);
                    list = 1;
                } else if (!secondList.isEmpty()) {
                    p = secondList.getRequest(0);
                    list = 2;
                }
                
                //虽然有请求，但不一定能进,直接进行预处理，让其进行处理
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 需要添加个调度算法，看到底把东西分到哪一项
    protected void disp(UpersonRequest p, int list) {
        int a = p.getCharA();
        int b = p.getCharB();
        int c = p.getCharC();
        if (a == 1 && aList.isEmpty()) {
            func(p, aList, list);
        } else if (b == 1 && bList.isEmpty()) {
            func(p, bList, list);
        } else if (c == 1 && cList.isEmpty()) {
            func(p, cList, list);
        } else {
            try {
                Thread.sleep(100); // 直接sleep 0.1s,防止出现问题
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    } // 需要采用随机算法判断给谁
    
    protected void func(UpersonRequest p, RequestList x, int l) { // 暂时不synchronized
        x.addRequest(p);
        if (l == 1) {
            this.firstList.removeRequest(0);
        } else if (l == 2) {
            this.secondList.removeRequest(0);
        }
    }
    
}
