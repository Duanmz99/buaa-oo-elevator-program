public class Main {
    public static void main(String[] args) {
        int[] accessA = {-3, -2, -1, 1, 15, 16, 17, 18, 19, 20};
        int[] accessB = {-2, -1, 1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        int[] accessC = {1, 3, 5, 7, 9, 11, 13, 15}; // 对应停靠楼层信息
        
        //开始设计共享资源
        RequestList fList = new RequestList(50);
        RequestList sList = new RequestList(50);
        RequestList aList = new RequestList(6);
        RequestList bList = new RequestList(8);
        RequestList cList = new RequestList(7);
        
        //开始构造需要的五个线程
        Producer producer = new Producer(fList);
        Dispatcher dispatcher = new Dispatcher(fList, sList, aList, bList, cList); // 共享资源，不断填充
        Elevator elevatorA = new Elevator(aList, sList, 6, 400, 200, 'A', accessA);
        Elevator elevatorB = new Elevator(bList, sList, 8, 500, 200, 'B', accessB);
        Elevator elevatorC = new Elevator(cList, sList, 7, 600, 200, 'C', accessC);
        
        // 线程开始启动
        producer.start();
        dispatcher.start();
        elevatorA.start();
        elevatorB.start();
        elevatorC.start();
    }
    
}
