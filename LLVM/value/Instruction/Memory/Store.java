package LLVM.value.Instruction.Memory;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;
import MIPS.Instruction.Memory.Li;
import MIPS.Instruction.Memory.Sb;
import MIPS.Instruction.Memory.Sw;
import MIPS.Instruction.MipsInstruction;
import MIPS.Instruction.Operate.Move;
import MIPS.MIPSGenerator;
import MIPS.MipsModule;
import MIPS.symbol.Symbol;

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

    @Override
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

    public ArrayList<MipsInstruction> generateMips() {
        ArrayList<MipsInstruction> list=new ArrayList<>();
        String reg;
        if(operators.get(0).getName().charAt(0)!='%'){
            //变量
            reg="$zero";
            if(!operators.get(0).getName().equals("0")){
                list.add(new Li(Integer.parseInt(operators.get(0).getName()),false));
                reg="$v1";
            }

        }else{
            //不是常量，是寄存器，直接存储
            reg=MIPSGenerator.irToMips.get(operators.get(0).getName());
        }
        Symbol symbol=MIPSGenerator.mipsModule.getSymbol(operators.get(1).getName());
        String saveVarReg;
        if(MipsModule.curFuncName.equals("@main")){
            saveVarReg="$sp";
        }else{
            saveVarReg="$gp";
        }
        if(reg==null){
            if(operators.get(0).getName().equals("%0")){
                reg="$a0";
            }else if(operators.get(0).getName().equals("%1")){
                reg="$a1";
            }else if(operators.get(0).getName().equals("%2")){
                reg="$a2";
            }else if(operators.get(0).getName().equals("%3")){
                reg="$a3";
            }else{
                //TODO:存在栈上
            }
        }
        if(symbol.getChar()){
            list.add(new Sb(reg,MipsModule.curFuncName.equals("@main")?symbol.getSp():symbol.getGp(),symbol.getGlobal()?"$gp":saveVarReg));
        }else{
            list.add(new Sw(reg,MipsModule.curFuncName.equals("@main")?symbol.getSp():symbol.getGp(),symbol.getGlobal()?"$gp":saveVarReg));
        }
        return list;
    }
}
