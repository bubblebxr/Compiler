package LLVM.value.Instruction.Operate;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;
import MIPS.Instruction.Memory.Li;
import MIPS.Instruction.MipsInstruction;
import MIPS.Instruction.Operate.Move;
import MIPS.Instruction.Operate.Mult;
import MIPS.Instruction.Operate.Sll;
import MIPS.MIPSGenerator;

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

    public ArrayList<MipsInstruction> generateMips() {
        ArrayList<MipsInstruction> list = new ArrayList<>();
        String label1="",label2="";
        int isSll=0;
        if(operators.get(0).getName().equals("0")){
            String reg=MIPSGenerator.registerStack.pop();
            list.add(new Li(0,false));
            MIPSGenerator.irToMips.put(name,reg);
            return list;
        }else if(operators.get(0).getName().charAt(0)!='%'){
            if(MIPSGenerator.isPowerOfTwo(Integer.parseInt(operators.get(0).getName()))){
                isSll=1;
                label1= String.valueOf(MIPSGenerator.getPowerOfTwo(Integer.parseInt(operators.get(0).getName())));
            }else{
                list.add(new Li(Integer.parseInt(operators.get(0).getName()),false));
                label1= MIPSGenerator.registerStack.pop();
                list.add(new Move(label1,"$v1"));
            }
        }else{
            if(MIPSGenerator.irToMips.containsKey(operators.get(0).getName())){
                label1= MIPSGenerator.irToMips.get(operators.get(0).getName());
            }
        }
        if(operators.get(1).getName().equals("0")){
            String reg=MIPSGenerator.registerStack.pop();
            list.add(new Li(0,false));
            MIPSGenerator.irToMips.put(name,reg);
            return list;
        }else if(operators.get(1).getName().charAt(0)!='%'){
            if(isSll!=1&&MIPSGenerator.isPowerOfTwo(Integer.parseInt(operators.get(1).getName()))){
                label2= String.valueOf(MIPSGenerator.getPowerOfTwo(Integer.parseInt(operators.get(1).getName())));
                isSll=2;
            }else{
                list.add(new Li(Integer.parseInt(operators.get(1).getName()),false));
                label2="$v1";
            }
        }else{
            if(MIPSGenerator.irToMips.containsKey(operators.get(1).getName())){
                label2=MIPSGenerator.irToMips.get(operators.get(1).getName());
            }
        }

        String reg=MIPSGenerator.registerStack.pop();
        if(isSll==0){
            //正常做乘法
            list.add(new Mult(reg,label1,label2));
        }else if(isSll==1){
            //第一个label是2的次方
            list.add(new Sll(reg,label2,label1));
        }else{
            //第2个label是2的次方
            list.add(new Sll(reg,label1,label2));
        }

        MIPSGenerator.irToMips.put(name,reg);
        if(!label1.matches("\\d+")){
            MIPSGenerator.registerStack.add(label1);
        }
        if(!label2.equals("$v1")&&!label2.matches("\\d+")){
            MIPSGenerator.registerStack.add(label2);
        }

        return list;
    }
}
