package MIPS.Instruction.Jump;


import MIPS.Instruction.MipsInstruction;

/**
 * @className: Jr
 * @author: bxr
 * @date: 2024/11/24 21:51
 * @description:
 */

public class Jr extends MipsInstruction {
    protected String name;

    public Jr(String name){
        this.name=name;
    }

    @Override
    public String toString(){
        return "jr "+name+"\n";
    }
}
