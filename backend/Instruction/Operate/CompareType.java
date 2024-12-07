package backend.Instruction.Operate;


/**
 * @className: CompareType
 * @author: bxr
 * @date: 2024/11/22 8:50
 * @description:
 */

public enum CompareType {
    bne, //跳转!=
    beq, //跳转==
    slt, // <
    sle, //<=
    sne, //!=
    seq, //==
    sgt, //>
    sge, //>=
    beqz,// ==0跳转
    bnez, //!=0跳转
}
