package backend.Instruction.Memory;


import backend.Instruction.MipsInstruction;

/**
 * @className: li
 * @author: bxr
 * @date: 2024/11/21 20:46
 * @description: li
 */

public class Li extends MipsInstruction {
    /**
     * @description:
     * 1:print int
     * 4:print string/char
     * 5:read int
     * 8:read char
     * 10:exit system
     **/
    protected Long type;
    protected Boolean isOI;

    public Li(Long type,Boolean isOI){
        this.type=type;
        this.isOI=isOI;
    }

    @Override
    public String toString(){
        if(isOI){
            return "li $v0, " + type +"\n    syscall";
        }else{
            return "li $v1, " + type;
        }
    }
}
