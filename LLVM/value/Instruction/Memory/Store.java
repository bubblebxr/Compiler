package LLVM.value.Instruction.Memory;


import LLVM.Type;
import LLVM.Value;
import LLVM.type.ArrayType;
import LLVM.type.PointerType;
import LLVM.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: store
 * @author: bxr
 * @date: 2024/11/7 20:31
 * @description:
 */

public class Store extends Instruction {

    public Store(String name, Type type) {
        super(name, type);
    }

    public Store(String name, Type type, ArrayList<Value> operators) {
        super(name, type);
        this.operators=operators;
    }

    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append("store ");
        for(int i=0;i<operators.size();i++){
            a.append(operators.get(i).toString());
            if(i<operators.size()-1){
                a.append(",");
            }
        }
        a.append("\n");
        return a.toString();
    }
}
