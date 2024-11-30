package MIPS.Instruction.Jump;


import MIPS.Instruction.MipsInstruction;

/**
 * @className: J
 * @author: bxr
 * @date: 2024/11/21 22:30
 * @description:
 */

public class J extends MipsInstruction {
    protected String name;

    public J(String name){
        this.name=name.substring(1);
    }

    @Override
    public String toString(){
        return "j "+name+"\n";
    }
}
