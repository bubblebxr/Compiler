package midend.value.Instruction;


import backend.Instruction.MipsInstruction;
import midend.Type;
import midend.User;
import midend.Value;

import java.util.ArrayList;

/**
 * @className: Instruction
 * @author: bxr
 * @date: 2024/11/8 8:39
 * @description:
 */

public class Instruction extends User {
    public Boolean isUse;
    public Instruction(String name, Type type) {
        super(name, type);
    }

    public Instruction(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    public ArrayList<MipsInstruction> genMips() {
        return null;
    }
}
