import com.oocourse.elevator3.PersonRequest;

public class UpersonRequest extends PersonRequest { // 特殊拆分函数应该直接在这里完成
    private int midFloor; // 对应于中间的起始站,二级转站的时候需要使用
    private int time; // 统计后面的内容，为1表示第一次上电梯，为2表示第二次上电梯
    //最多两次就可以到达终点站，这件事由程序员进行保证
    //中间如果已经送到需要进行二次覆盖
    private int[] accessA = {-3, -2, -1, 1, 15, 16, 17, 18, 19, 20};
    private int[] accessB = {-2, -1, 1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private int[] accessC = {1, 3, 5, 7, 9, 11, 13, 15};
    private int a;
    private int b;
    private int c;
    
    public UpersonRequest(PersonRequest personRequest) {
        super(personRequest.getFromFloor(), personRequest.getToFloor(), personRequest.getPersonId());
        this.midFloor = 1;
        this.time = 1; // 还是采用最直观的想法实现，如果是第一次就为1，第二次为2
        this.a = 0;
        this.b = 0;
        this.c = 0;
        setEle();
    }
    
    protected int getMidFloor() {
        return this.midFloor;
    }
    
    protected int getTime() {
        return this.time;
    }
    
    protected void setTime() {
        this.time = 2;
    }
    
    protected int getCharA() {
        return this.a;
    }
    
    protected int getCharB() {
        return this.b;
    }
    
    protected int getCharC() {
        return this.c;
    }
    
    
    protected void setEle() {
        if (hasItem(this, accessA)) {
            a = 1;
        }
        if (hasItem(this, accessB)) {
            b = 1;
        }
        if (hasItem(this, accessC)) {
            c = 1;
        }
        int from = this.getFromFloor();
        int to = this.getToFloor();
        if (a == 0 && b == 0 && c == 0) {
            specialSet(from, to);
        } // 一开始直接拆分
    }
    
    protected void specialSet(int from, int to) {
        if (from < 4 || to < 4) {
            this.midFloor = 1;
        } // 到2为止的最优解都是先到一层
        else if ((from >= 4 && from <= 14) || (to <= 14 && to >= 4)) {
            this.midFloor = 15;
        }
    } // 判断逻辑：每个if循环及else判断使得只需处理之后的情况
    
    //判断能否从该电梯走
    protected boolean hasItem(UpersonRequest p, int[] x) { // 封装在requestlist里面使得调度成为可能
        int judge = 0;
        for (int i = 0; i < x.length; i++) {
            if (p.getTime() == 1) {
                if (p.getMidFloor() == 0) {
                    if (x[i] == p.getFromFloor()) {
                        judge++;
                    }
                    if (x[i] == p.getToFloor()) {
                        judge++;
                    }
                } else if (p.getMidFloor() != 0) {
                    if (x[i] == p.getFromFloor()) {
                        judge++;
                    }
                    if (x[i] == p.getMidFloor()) {
                        judge++;
                    }
                }
            } else if (p.getTime() == 2) { // 逻辑不一样，此时必然已经需要采用midFloor了
                for (i = 0; i < x.length; i++) {
                    if (x[i] == p.getMidFloor()) {
                        judge++;
                    }
                    if (x[i] == p.getToFloor()) {
                        judge++;
                    }
                }
            }
        }
        if (judge == 2) {
            return true;
        } else {
            return false;
        }
    }
    
}
