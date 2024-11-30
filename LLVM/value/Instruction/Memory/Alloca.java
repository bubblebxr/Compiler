package LLVM.value.Instruction.Memory;


import LLVM.Type;
import LLVM.type.CharType;
import LLVM.type.IntegerType;
import LLVM.value.Instruction.Instruction;
import LLVM.value.Instruction.Operate.Add;
import MIPS.MipsModule;
import MIPS.symbol.Symbol;
import MIPS.Instruction.MipsInstruction;
import MIPS.Instruction.Operate.Addu;
import MIPS.MIPSGenerator;

import java.util.ArrayList;

/**
 * @className: alloca
 * @author: bxr
 * @date: 2024/11/7 20:30
 * @description:
 */   

public class Alloca extends Instruction {
    protected int id; //局部变量的id
    protected int elementNum; //如果是数组，那么数组的元素个数

    /*普通变量*/
    public Alloca(String name, Type type) {
        super(name, type);
        this.id=Integer.parseInt(name.substring(1));
        elementNum=0;
    }

    /*数组*/
    public Alloca(String name, Type type,int elementNum) {
        super(name, type);
        this.id=Integer.parseInt(name.substring(1));
        this.elementNum=elementNum;
    }

    @Override
    public String toString(){
        return name+" = alloca "+type.toString()+"\n";
    }

    public int getElementNum() {
        return elementNum;
    }

    public ArrayList<MipsInstruction> generateMips() {
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        Boolean isMain = MipsModule.curFuncName.equals("@main");
        if(elementNum==0){
            if(isMain){
                //变量:addu $sp, $sp, -4
//                temp.add(new Addu("$sp","$sp","-4"));
                int sp=MIPSGenerator.mipsModule.getCurTable().getSp();
                MIPSGenerator.mipsModule.getCurTable().updateSp(4);
                Symbol symbol=new Symbol(name,type instanceof CharType?true:false,sp);
                MIPSGenerator.mipsModule.addCurSymbol(name,symbol);
            }else{
                Symbol symbol=new Symbol(true,name,type instanceof CharType?true:false);
                MIPSGenerator.mipsModule.addCurSymbol(name,symbol);
            }
        }else{
            //TODO:数组
            if(type.getType() instanceof IntegerType){
                //int array
//                temp.add(new Addu("$sp","$sp",String.valueOf(-4*elementNum)));
                int sp=MIPSGenerator.mipsModule.getCurTable().getSp();
                MIPSGenerator.mipsModule.getCurTable().updateSp(4*elementNum);
                Symbol symbol=new Symbol(name,type instanceof CharType?true:false,elementNum,sp);
                MIPSGenerator.mipsModule.addCurSymbol(name,symbol);
            }else{
                //char array
//                temp.add(new Addu("$sp","$sp",String.valueOf(-(int) Math.ceil((double) elementNum / 8) * 8)));
                int sp=MIPSGenerator.mipsModule.getCurTable().getSp();
                MIPSGenerator.mipsModule.getCurTable().updateSp((int) Math.ceil((double) elementNum / 8) * 8);
                Symbol symbol=new Symbol(name,type instanceof CharType?true:false,elementNum,sp);
                MIPSGenerator.mipsModule.addCurSymbol(name,symbol);
            }
        }
        return temp;
    }
}
