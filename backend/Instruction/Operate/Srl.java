package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Srl
 * @author: bxr
 * @date: 2024/12/18 23:57
 * @description:
 */

public class Srl extends MipsInstruction {
    protected String register;
    protected String label1;
    protected String label2;

    public Srl(String register,String label1,String label2){
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "srl "+register+", "+label1+", "+label2;
    }
}
