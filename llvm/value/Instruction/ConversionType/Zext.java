package llvm.value.Instruction.ConversionType;


import llvm.Type;
import llvm.Value;
import llvm.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: Zext
 * @author: bxr
 * @date: 2024/11/11 21:20
 * @description: char 类型值赋给 int 类型变量
 */

public class Zext extends Instruction {

    public Zext(String name, Type type) {
        super(name, type);
    }

    public Zext(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        return name +
                " = " +
                "zext " +
                operators.get(0).toString() +
                " to " +
                operators.get(1).getType().toString() +
                "\n";
    }
}
