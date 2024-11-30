package MIPS.Instruction.Memory;


import MIPS.Instruction.MipsInstruction;

/**
 * @className: Sb
 * @author: bxr
 * @date: 2024/11/22 21:59
 * @description:
 */

public class Sb extends MipsInstruction {
    protected String register;
    protected int offset;
    protected String reg;

    public Sb(String register,int offset,String reg){
        this.register=register;
        this.offset=offset;
        this.reg=reg;
    }

    @Override
    public String toString(){
        return "sb "+register+", "+offset+"("+reg+")";
    }
}
