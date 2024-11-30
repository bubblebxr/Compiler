package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Icmp
 * @author: bxr
 * @date: 2024/11/22 8:47
 * @description:
 */

public class Compare extends MipsInstruction {
    protected CompareType type;
    protected String register;
    protected String label1;
    protected String label2;

    public Compare(CompareType type,String register,String label1,String label2){
        this.type=type;
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return type+" "+register+", "+label1+", "+label2;
    }
}
