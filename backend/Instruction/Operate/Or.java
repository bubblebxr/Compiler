package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Or
 * @author: bxr
 * @date: 2024/12/26 17:14
 * @description: 按位或
 */

public class Or extends MipsInstruction {
    protected String register;
    protected String label1;
    protected String label2;

    public Or(String register,String label1,String label2){
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "or "+register+", "+label1+", "+label2;
    }
}
