package LLVM.value.Instruction.Jump;


import LLVM.Type;
import LLVM.Value;
import LLVM.type.CharType;
import LLVM.type.PointerType;
import LLVM.value.Instruction.Instruction;
import LLVM.value.Instruction.Memory.Getelementptr;
import MIPS.Instruction.Jump.Jal;
import MIPS.Instruction.Memory.Li;
import MIPS.Instruction.Memory.Lw;
import MIPS.Instruction.Memory.Sw;
import MIPS.Instruction.MipsInstruction;
import MIPS.Instruction.Operate.Addi;
import MIPS.Instruction.Operate.Move;
import MIPS.MIPSGenerator;
import MIPS.symbol.Symbol;

import java.util.ArrayList;

/**
 * @className: Cal
 * @author: bxr
 * @date: 2024/11/11 19:08
 * @description:
 */

public class Call extends Instruction {
    protected String functionName;

    public Call(String name, Type type,String functionName,ArrayList<Value> operators) {
        super(name, type,operators);
        this.functionName=functionName;
    }

    public Call(String name, Type type,String functionName) {
        super(name, type);
        this.functionName=functionName;
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
       if(name!=null){
           a.append(name);
           a.append(" = ");
       }
        a.append("call ");
        a.append(type.toString());
        a.append(" ");
        a.append(functionName);
        a.append("(");
        for(int i=0;i<operators.size();i++){
            a.append(operators.get(i).toString());
            if(i<operators.size()-1){
                a.append(",");
            }
        }
        a.append(")\n");
        return a.toString();
    }

    public ArrayList<MipsInstruction> generateMips() {
        ArrayList<MipsInstruction> list=new ArrayList<>();
        if(functionName.equals("@putstr")){
            list.add(new Li(4,true));
            return list;
        }else if(functionName.equals("@getint")){
            list.add(new Li(5,true));
            MIPSGenerator.irToMips.put(name,"$v0");
            return list;
        }else if(functionName.equals("@getchar")){
            list.add(new Li(12,true));
            MIPSGenerator.irToMips.put(name,"$v0");
            return list;
        }else if(functionName.equals("@putint")){
            String reg= MIPSGenerator.irToMips.get(operators.get(0).getName());
            list.add(new Move("$a0",reg));
            list.add( new Li(1,true));
            if(!reg.equals("$t0")){
                MIPSGenerator.registerStack.add(reg);
            }
            return list;
        }else if(functionName.equals("@putch")){
            String reg= MIPSGenerator.irToMips.get(operators.get(0).getName());
            list.add(new Move("$a0",reg));
            list.add(new Li(11,true));
            return list;
        }else{
            //TODO
            list.add(new Addi("$fp","$fp",String.valueOf(8)));
            list.add(new Sw("$ra",-4,"$fp"));
            ArrayList<String> regList=new ArrayList<>();
            regList.add("$a0");
            regList.add("$a1");
            regList.add("$a2");
            regList.add("$a3");
            for(int i=0;i<operators.size();i++){
                String label1 = "";
                if(operators.get(0).getName().equals("0")){
                    label1="$zero";
                }else if(operators.get(0).getName().charAt(0)!='%'){
                    list.add(new Li(Integer.parseInt(operators.get(i).getName()),false));
                    label1= "$v1";
                }else{
                    if(MIPSGenerator.irToMips.containsKey(operators.get(i).getName())){
                        label1= MIPSGenerator.irToMips.get(operators.get(i).getName());
                    }
                }
                if(i<4){
                    list.add(new Move(regList.get(i),label1));
                }else{
                    //TODO:使用栈
                }
            }
            list.add(new Jal(functionName.substring(1)));
            list.add(new Addi("$fp","$fp",String.valueOf(-8)));
            list.add(new Lw("$ra",-4,"$fp"));
        }
        return list;
    }
}
