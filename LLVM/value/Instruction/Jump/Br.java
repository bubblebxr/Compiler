package LLVM.value.Instruction.Jump;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: Br
 * @author: bxr
 * @date: 2024/11/18 9:57
 * @description: 跳转
 */

public class Br extends Instruction {
    public Br(String name, Type type) {
        super(name, type);
    }

    public Br(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }
}
