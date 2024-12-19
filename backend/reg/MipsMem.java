package backend.reg;


/**
 * @className: MipsMem
 * @author: bxr
 * @date: 2024/12/4 10:26
 * @description: 记录存储在哪里
 */

public class MipsMem {
    public Boolean isInReg;
    public String RegName;
    public int offset;
    public int elementNum;
    public Boolean isPointer; // 如果true则需要通过0($t0)类似存储
    public Boolean isArrayParam;// 是数组传参

    /**
     * @description: 存储在寄存器中
     * @date: 2024/12/4 10:27
     **/
    public MipsMem(String RegName){
        this.RegName=RegName;
        isInReg=true;
    }

    /**
     * @description: 存储在sp栈中
     * @date: 2024/12/4 10:27
     **/
    public MipsMem(int offset){
        this.offset=offset;
        isInReg=false;
    }

    /**
     * @description: 数组，一定存储在sp栈中
     * @date: 2024/12/6 13:26
     **/
    public MipsMem(int offset,int elementNum){
        this.offset=offset;
        this.elementNum=elementNum;
        isInReg=false;
    }
}
