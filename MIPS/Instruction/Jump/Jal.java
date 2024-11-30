package MIPS.Instruction.Jump;


import MIPS.Instruction.MipsInstruction;

/**
 * @className: Jal
 * @author: bxr
 * @date: 2024/11/21 22:30
 * @description:
 */

public class Jal extends MipsInstruction {
    String name;

    public Jal(String name){
        this.name=name;
    }

    @Override
    public String toString(){
        return "jal "+name;
    }
}
