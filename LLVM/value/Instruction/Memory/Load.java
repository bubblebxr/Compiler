package LLVM.value.Instruction.Memory;


import LLVM.Type;
import LLVM.Value;
import LLVM.type.CharType;
import LLVM.type.PointerType;
import LLVM.value.Instruction.Instruction;
import LLVM.value.Instruction.Jump.Call;
import MIPS.Instruction.Memory.*;
import MIPS.Instruction.MipsInstruction;
import MIPS.Instruction.Operate.Move;
import MIPS.MIPSGenerator;
import MIPS.MipsModule;
import MIPS.symbol.Symbol;
import MIPS.value.MipsGlobalVariable;

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

    public ArrayList<MipsInstruction> generateMips() {
        ArrayList<MipsInstruction> list=new ArrayList<>();
        if(operators.get(0).getName().charAt(0)=='@'){
            //全局变量
            MipsGlobalVariable variable=MIPSGenerator.mipsModule.getGlobalSymbol(operators.get(0).getName().substring(1));
            if(variable!=null){
                String reg=MIPSGenerator.registerStack.pop();
                if(variable.getType() instanceof CharType){
                    list.add(new Lb(reg,operators.get(0).getName().substring(1)));
                }else{
                    list.add(new Lw(reg,operators.get(0).getName().substring(1)));
                }
                MIPSGenerator.irToMips.put(name,reg);
                return list;
            }
        }
        String saveVarReg;
        if(MipsModule.curFuncName.equals("@main")){
            saveVarReg="$sp";
        }else{
            saveVarReg="$gp";
        }
        Symbol symbol=MIPSGenerator.mipsModule.getSymbol(operators.get(0).getName());
        String reg=MIPSGenerator.registerStack.pop();
        if(symbol.getChar()){
            list.add(new Lb(reg,symbol.getGlobal()? symbol.getGp():(MipsModule.curFuncName.equals("@main")?symbol.getSp():symbol.getGp()),symbol.getGlobal()?"$gp":saveVarReg));
        }else{
            list.add(new Lw(reg,symbol.getGlobal()? symbol.getGp():(MipsModule.curFuncName.equals("@main")?symbol.getSp():symbol.getGp()),symbol.getGlobal()?"$gp":saveVarReg));
        }
        symbol.putToReg(reg);
        MIPSGenerator.irToMips.put(name,reg);

        return list;
    }
}
