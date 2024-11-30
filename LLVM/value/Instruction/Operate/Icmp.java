package LLVM.value.Instruction.Operate;


import LLVM.Type;
import LLVM.Value;
import LLVM.type.PointerType;
import LLVM.value.Instruction.Instruction;
import MIPS.Instruction.Jump.J;
import MIPS.Instruction.Memory.Li;
import MIPS.Instruction.MipsInstruction;
import MIPS.Instruction.Operate.Compare;
import MIPS.Instruction.Operate.CompareType;
import MIPS.Instruction.Operate.Move;
import MIPS.MIPSGenerator;
import MIPS.MipsModule;

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

    public ArrayList<MipsInstruction> generateMips() {
        ArrayList<MipsInstruction> list=new ArrayList<>();
        String label1 = "",label2="";
        if(operators.get(0).getName().equals("0")){
            label1="$zero";
        }else if(operators.get(0).getName().charAt(0)!='%'){
            list.add(new Li(Integer.parseInt(operators.get(0).getName()),false));
            label1=MIPSGenerator.registerStack.pop();
            list.add(new Move(label1,"$v1"));
        }else{
            if(MIPSGenerator.irToMips.containsKey(operators.get(0).getName())){
                label1= MIPSGenerator.irToMips.get(operators.get(0).getName());
            }
        }
        if(operators.get(1).getName().equals("0")){
            label2="$zero";
        }else if(operators.get(1).getName().charAt(0)!='%'){
            list.add(new Li(Integer.parseInt(operators.get(1).getName()),false));
            label2="$v1";
        }else{
            if(MIPSGenerator.irToMips.containsKey(operators.get(1).getName())){
                label2=MIPSGenerator.irToMips.get(operators.get(1).getName());
            }
        }
        if(icmpType==IcmpType.eq){
            // ==
            list.add(new Compare(CompareType.seq,"$t0",label1,label2));
        }else if(icmpType==IcmpType.ne){
            // !=
            list.add(new Compare(CompareType.sne,"$t0",label1,label2));
        }else if(icmpType==IcmpType.sgt){
            // >
            list.add(new Compare(CompareType.sgt,"$t0",label1,label2));
        }else if(icmpType==IcmpType.sge){
            // >=
            list.add(new Compare(CompareType.sge,"$t0",label1,label2));
        }else if(icmpType==IcmpType.slt){
            // <
            list.add(new Compare(CompareType.slt,"$t0",label1,label2));
        }else{
            // <=
            list.add(new Compare(CompareType.sle,"$t0",label1,label2));
        }
        MIPSGenerator.irToMips.put(name,"$t0");
        if(!label1.equals("$zero")&&!label1.equals("$t0")){
            MIPSGenerator.registerStack.add(label1);
        }
        if(!label2.equals("$v1")&&!label2.equals("$zero")&&!label2.equals("$t0")){
            MIPSGenerator.registerStack.add(label2);
        }
        return list;
    }
}
