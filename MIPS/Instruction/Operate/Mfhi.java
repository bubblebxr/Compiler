package MIPS.Instruction.Operate;


import MIPS.Instruction.MipsInstruction;

/**
 * @className: Mfhi
 * @author: bxr
 * @date: 2024/11/24 21:31
 * @description: 保存余数
 */

public class Mfhi extends MipsInstruction {
    protected String register;

    public Mfhi(String register){
        this.register=register;
    }

    @Override
    public String toString(){
        return "mfhi "+register;
    }
}
