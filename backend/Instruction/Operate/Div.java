package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Div
 * @author: bxr
 * @date: 2024/11/24 21:27
 * @description:
 */

public class Div extends MipsInstruction {
    protected String label1;
    protected String label2;

    public Div(String label1,String label2){
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "div "+label1+", "+label2;
    }
}
