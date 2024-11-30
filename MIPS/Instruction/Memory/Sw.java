package MIPS.Instruction.Memory;


import MIPS.Instruction.MipsInstruction;

/**
 * @className: Sw
 * @author: bxr
 * @date: 2024/11/22 21:55
 * @description:
 */

public class Sw extends MipsInstruction {
    protected String register;
    protected int offset;
    protected String reg;

    public Sw(String register,int offset,String reg){
        this.register=register;
        this.offset=offset;
        this.reg=reg;
    }

    @Override
    public String toString(){
        return "sw "+register+", "+offset+"("+reg+")";
    }
}
