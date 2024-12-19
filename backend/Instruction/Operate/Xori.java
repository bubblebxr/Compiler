package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Xori
 * @author: bxr
 * @date: 2024/12/19 14:35
 * @description: 按位异或（XOR）立即数指令
 */

public class Xori extends MipsInstruction {
    protected String register;
    protected String label1;
    protected String label2;

    public Xori(String register,String label1,String label2){
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "xori "+register+", "+label1+", "+label2;
    }
}
