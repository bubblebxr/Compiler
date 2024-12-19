package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Sll
 * @author: bxr
 * @date: 2024/11/22 16:46
 * @description: 向左移位，用于乘2时优化
 */

public class Sll extends MipsInstruction {
    protected String register;
    protected String label1;
    protected String label2;

    public Sll(String register,String label1,String label2){
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "sll "+register+", "+label1+", "+label2;
    }
}
