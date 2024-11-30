package llvm.value.Instruction.Operate;


import llvm.Type;
import llvm.Value;
import llvm.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: Sub
 * @author: bxr
 * @date: 2024/11/7 20:37
 * @description: 减
 */

public class Sub extends Instruction {

    public Sub(String name, Type type) {
        super(name, type);
    }

    public Sub(String name, Type type, ArrayList<Value> operators) {
        super(name,type,operators);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(name);
        a.append(" = sub ");
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
