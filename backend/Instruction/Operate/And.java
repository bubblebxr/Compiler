package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: And
 * @author: bxr
 * @date: 2024/12/26 17:13
 * @description: 按位与
 */

public class And extends MipsInstruction {
    protected String register;
    protected String label1;
    protected String label2;

    public And(String register,String label1,String label2){
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "and "+register+", "+label1+", "+label2;
    }
}
