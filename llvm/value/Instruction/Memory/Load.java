package llvm.value.Instruction.Memory;


import llvm.Type;
import llvm.Value;
import llvm.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: load
 * @author: bxr
 * @date: 2024/11/7 20:31
 * @description: 从寄存器中加载变量
 */

public class Load extends Instruction {

    public Load(String name, Type type) {
        super(name, type);
    }

    public Load(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(name);
        a.append(" = load ");
        a.append(type.toString());
        a.append(",");
        a.append(operators.get(0).toString());
        a.append("\n");
        return a.toString();
    }

}
