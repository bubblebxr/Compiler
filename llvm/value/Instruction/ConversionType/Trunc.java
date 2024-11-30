package llvm.value.Instruction.ConversionType;


import llvm.Type;
import llvm.Value;
import llvm.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: Trunc
 * @author: bxr
 * @date: 2024/11/11 21:21
 * @description: int 类型值赋给 char 类型变量
 */

public class Trunc extends Instruction {

    public Trunc(String name, Type type) {
        super(name, type);
    }

    public Trunc(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        return name +
                " = " +
                "trunc " +
                operators.get(0).toString() +
                " to " +
                operators.get(1).getType().toString() +
                "\n";
    }
}
