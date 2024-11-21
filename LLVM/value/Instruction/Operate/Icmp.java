package LLVM.value.Instruction.Operate;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: Icmp
 * @author: bxr
 * @date: 2024/11/12 22:12
 * @description: int Compare
 */

public class Icmp extends Instruction {
    protected IcmpType icmpType;

    public Icmp(String name, Type type) {
        super(name, type);
    }

    public Icmp(String name, Type type, ArrayList<Value> operators,IcmpType icmpType) {
        super(name,type,operators);
        this.icmpType=icmpType;
    }

    @Override
    public String toString(){
        return name +
                " = " +
                "icmp " +
                icmpType +
                " " +
                operators.get(0).getType().toString() +
                " " +
                operators.get(0).getName() +
                "," +
                operators.get(1).getName() +
                "\n";
    }
}
