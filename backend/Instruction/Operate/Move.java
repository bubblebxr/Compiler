package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: Move
 * @author: bxr
 * @date: 2024/11/22 15:25
 * @description: Move指令
 * move $t1,$t2   令$t1=$t2
 */

public class Move extends MipsInstruction{
    protected String label1;
    protected String label2;

    public Move(String label1,String label2){
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "move "+label1+", "+label2;
    }
}
