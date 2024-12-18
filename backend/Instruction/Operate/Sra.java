package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Sra
 * @author: bxr
 * @date: 2024/12/19 0:09
 * @description: 算数右移
 */

public class Sra extends MipsInstruction {
    protected String register;
    protected String label1;
    protected String label2;

    public Sra(String register,String label1,String label2){
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "sra "+register+", "+label1+", "+label2;
    }
}
