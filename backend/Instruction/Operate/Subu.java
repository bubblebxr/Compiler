package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Subu
 * @author: bxr
 * @date: 2024/11/22 22:21
 * @description:
 */

public class Subu extends MipsInstruction {
    protected String register;
    protected String label1;
    protected String label2;

    public Subu(String register,String label1,String label2){
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "subu "+register+", "+label1+", "+label2;
    }
}
