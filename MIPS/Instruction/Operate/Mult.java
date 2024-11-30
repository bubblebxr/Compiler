package MIPS.Instruction.Operate;


import MIPS.Instruction.MipsInstruction;

/**
 * @className: Mult
 * @author: bxr
 * @date: 2024/11/22 17:16
 * @description: 乘法
 * mult $v0, $t0
 * mflo $t0 低32位存储在$t0中
 */

public class Mult extends MipsInstruction {
    protected String register;
    protected String label1;
    protected String label2;

    public Mult(String register,String label1,String label2){
        this.register=register;
        this.label1=label1;
        this.label2=label2;
    }

    @Override
    public String toString(){
        return "mult "+label1+", "+label2+"\n  mflo "+register;
    }
}
