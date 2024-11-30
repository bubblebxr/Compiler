package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Addi
 * @author: bxr
 * @date: 2024/11/24 21:36
 * @description:
 */

public class Addi extends MipsInstruction {
    protected String register;
    protected String label1;
    protected String label2;

    public Addi(String register,String label1,String label2){
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "addi "+register+", "+label1+", "+label2;
    }
}
