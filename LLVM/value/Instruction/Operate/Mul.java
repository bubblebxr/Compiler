package LLVM.value.Instruction.Operate;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: Mul
 * @author: bxr
 * @date: 2024/11/11 18:52
 * @description: 乘
 */

public class Mul extends Instruction {

    public Mul(String name, Type type) {
        super(name, type);
    }

    public Mul(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(name);
        a.append(" = mul ");
        a.append(type.toString());
        a.append(" ");
        for(int i=0;i<operators.size();i++){
            a.append(operators.get(i).getName());
            if(i<operators.size()-1){
                a.append(",");
            }
        }
        a.append("\n");
        return a.toString();
    }
}
