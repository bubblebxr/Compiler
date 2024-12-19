package backend.Instruction.Memory;


import backend.Instruction.MipsInstruction;

/**
 * @className: La
 * @author: bxr
 * @date: 2024/11/22 9:36
 * @description:
 */

public class La extends MipsInstruction {
    protected String register;
    protected String name;

    public La(String register,String name){
        this.register=register;
        this.name=name;
    }

    @Override
    public String toString(){
        return "la "+register+", "+name;
    }
}
