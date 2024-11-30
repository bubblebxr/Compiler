package backend.Instruction.Operate;


import backend.Instruction.MipsInstruction;

/**
 * @className: mflo
 * @author: bxr
 * @date: 2024/11/24 21:29
 * @description: 保存商
 */

public class Mflo extends MipsInstruction {
    protected String register;

    public Mflo(String register){
        this.register=register;
    }

    @Override
    public String toString(){
        return "mflo "+register;
    }
}
