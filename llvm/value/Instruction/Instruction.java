package llvm.value.Instruction;


import llvm.Type;
import llvm.User;
import llvm.Value;

import java.util.ArrayList;

/**
 * @className: Instruction
 * @author: bxr
 * @date: 2024/11/8 8:39
 * @description:
 */

public class Instruction extends User {
    public Instruction(String name, Type type) {
        super(name, type);
    }

    public Instruction(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

}
