import com.oocourse.TimableOutput;
import com.oocourse.elevator3.PersonRequest;

public class Elevator extends Thread { // 目前看好像只需要一次类扩展就可以？不需要多次？,对应于消费者角色
    private final RequestList list; // 对应于自己的请求队列
    private final RequestList secondList; // 对应可能需要放入的二次队列
    private int floor = 1; // 当前楼梯层数
    private int extime; // 走一层楼梯的时间
    private int octime;  // 开门/关门的时间
    private int fromfloor;
    private int tofloor;
    private int pos = 0; // 负责标记电梯的行驶方向,1为向上，-1为向下
    private int maxPeople; // 最大放人数
    private char elevatorName;
    private int people; // 当前人数
    private int[] access; // 表示该电梯可以到达的楼层，帮助后面进行判断
    
    public Elevator(RequestList eList, RequestList sList, int maxP, int floortime, int doortime, char name, int[] x) {
        this.list = eList;
        this.secondList = sList;
        this.maxPeople = maxP;
        this.extime = floortime;
        this.octime = doortime;
        this.elevatorName = name;
        this.people = 0;
        access = x;
    }
    
    private void printOpen(int floor, char name) {
        TimableOutput.println(String.format("OPEN-%d-%c", floor, name));
    }
    
    private void printClose(int floor, char name) {
        TimableOutput.println(String.format("CLOSE-%d-%c", floor, name));
    }
    
    private void peopleIn(int id, int floor, char name) {
        TimableOutput.println(String.format("IN-%d-%d-%c", id, floor, name));
    }
    
    private void peopleOut(int id, int floor, char name) {
        TimableOutput.println(String.format("OUT-%d-%d-%c", id, floor, name));
    }
    
    private void printArrive(int floor, char name) { // 输出到达某层的信息
        TimableOutput.println(String.format("ARRIVE-%d-%c", floor, name));
    }
    
    private void sleepOctime() {
        try {
            Thread.sleep(octime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void sleepFloortime() {
        try {
            Thread.sleep(extime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private int getPos(UpersonRequest p) { // 需要考虑两次倒的问题
        if (p.getTime() == 1) {
            if (p.getMidFloor() != 0) {
                if (p.getFromFloor() < p.getMidFloor()) {
                    this.pos = 1;
                } else {
                    this.pos = -1;
                }
            } else {
                if (p.getFromFloor() < p.getToFloor()) {
                    this.pos = 1;
                } else {
                    this.pos = -1;
                }
            }
        } else if (p.getTime() == 2) { // 此时必然需要使用MidFloor，已经不需要进行讨论
            if (p.getMidFloor() < p.getToFloor()) {
                this.pos = 1;
            } else {
                this.pos = -1;
            }
        }
        return pos;
    } // 判断电梯运行的方向
    
    /* 到每一层时的电梯带人处理函数(从分工来看，人数多少及电梯能不能再带人这件事应该由电梯本身来判断)
     * 电梯本身设有最大的阈值，这方面由电梯判断
     */
    protected void floorExecute(int floor) {
    
    }
    
    public void run() {
        while (true) {
            //如何各个线程安全退出暂时不管
            while (this.list.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } // 插入部分由主调度器负责完成
            // 默认此时已经有东西了
            UpersonRequest p = this.list.getRequest(0);
            // 开始使用主线程这个概念，循环带人
            if (p.getTime() == 1) {
                if (p.getMidFloor() != 0) {
                    this.fromfloor = p.getFromFloor();
                    this.tofloor = p.getMidFloor();
                    this.pos = getPos(p);
                } else {
                    this.fromfloor = p.getFromFloor();
                    this.tofloor = p.getToFloor();
                    this.pos = getPos(p);
                }
            } else if (p.getTime() == 2) {
                this.fromfloor = p.getMidFloor();
                this.tofloor = p.getToFloor();
                this.pos = getPos(p);
            } // 此时已经有主请求，电梯运行方向已经确定
            // 开始准备达到指定楼层
            for (int i = floor + pos; i != fromfloor + pos; i = i + pos) { // 开始向主请求的位置赶去
                if (i == 0) {
                    continue;
                }
                sleepFloortime();
                printArrive(i, elevatorName);
            } // 达到主请求所在位置
            floor = fromfloor;
            int begin = floor;
            // 开始边走边带人，如果达到最大限度则直接忽略那个人
            for (int i = fromfloor; i != (tofloor + pos); i = i + pos) {
                if (i == 0) {
                    continue;
                }
                floor = i;
                if (i != begin) {
                    sleepFloortime();
                    printArrive(i, elevatorName);
                    floorExecute();
                }
            }
        }
    }
    
    protected boolean availableFloor(int floor) {
        for (int i = 0; i < access.length; i++) {
            if (access[i] == floor) {
                return true;
            }
        }
        return false; // 如果队列里面有就可以访问，否则不行
    } // 判断这个电梯是否可以访问当前楼层
    
}
