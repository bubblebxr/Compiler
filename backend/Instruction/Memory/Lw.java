package backend.Instruction.Memory;


import backend.Instruction.MipsInstruction;

/**
 * @className: Lw
 * @author: bxr
 * @date: 2024/11/22 17:48
 * @description:
 */

public class Lw extends MipsInstruction {
    protected String register;
    protected int offset;
    protected String reg;
    protected Boolean type;

    /**
     * @description: 栈指针偏移
     * @date: 2024/11/24 20:57
     **/
    public Lw(String register,int offset,String reg){
        this.register=register;
        this.offset=offset;
        this.reg=reg;
        type=true;
    }

    /**
     * @description: 全局变量引入
     * @date: 2024/11/24 20:57
     **/
    public Lw(String register,String reg){
        this.register=register;
        this.reg=reg;
        type=false;
    }

    @Override
    public String toString(){
        if(type){
            return "lw "+register+", "+offset+"("+reg+")";
        }else{
            return "lw "+register+", "+reg;
        }
    }

    /**
     * @description: 用于获取存在栈中的参数的索引
     * @date: 2024/12/13 20:48
     **/
    @Override
    public int getOffset(){
        return offset;
    }

    /**
     * @description: 用于填充存在栈中的参数
     * @date: 2024/12/13 20:53
     **/
    @Override
    public void setOffset(int offset){
        this.offset=offset;
    }

    public String getReg() {
        return reg;
    }

    public String getRegister() {
        return register;
    }
}
