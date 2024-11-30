package LLVM.value;

import LLVM.Type;
import LLVM.Value;
import LLVM.type.IntegerType;
import LLVM.value.Instruction.Instruction;
import LLVM.value.Instruction.Jump.Br;
import LLVM.value.Instruction.Jump.Ret;
import LLVM.value.Instruction.Memory.Alloca;
import MIPS.Instruction.Memory.Li;
import MIPS.Instruction.MipsInstruction;
import MIPS.Instruction.Operate.Addu;
import MIPS.Instruction.Operate.Move;
import MIPS.MIPSGenerator;
import MIPS.value.MipsFunction;

import javax.swing.*;
import java.util.ArrayList;

public class Function extends Value {
    protected ArrayList<Argument> argumentList;
    protected ArrayList<BasicBlock> basicBlockList;
    protected Boolean isDeclare;

    /* declare function */
    public Function(String name, Type type){
        super(name,type);
        this.basicBlockList=new ArrayList<>();
        this.isDeclare=true;
    }

    /* define function */
    public Function(String name, Type type,ArrayList<Argument> argumentList){
        super(name,type);
        this.argumentList=argumentList;
        this.basicBlockList=new ArrayList<>();
        this.isDeclare=false;
    }

    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(type.toString(name));
        if(!isDeclare){
            a.append("{\n");
            for(BasicBlock basicBlock:basicBlockList){
                a.append(basicBlock.toString());
            }
            a.append("}\n");
        }
        return a.toString();
    }

    public void addBasicBlock(BasicBlock basicBlock){
        basicBlockList.add(basicBlock);
    }

    public BasicBlock getCurBasicBlock() {
        return basicBlockList.get(basicBlockList.size()-1);
    }

    public void checkReturn() {
        if(!basicBlockList.get(basicBlockList.size()-1).getInstructionList().isEmpty()){
            if(basicBlockList.get(basicBlockList.size()-1).checkReturn()){
                getCurBasicBlock().addInstruction(new Ret(null,null,new ArrayList<>()));
            }
        }else{
            if(basicBlockList.size()==1){
                getCurBasicBlock().addInstruction(new Ret(null,null,new ArrayList<>()));
            } else if(basicBlockList.size()>=2&&basicBlockList.get(basicBlockList.size()-1).checkReturn()){
                basicBlockList.get(basicBlockList.size()-1).addInstruction(new Ret(null,null,new ArrayList<>()));
            }
        }
    }

    public void fillContinueLabel(int startIndex, int endIndex,String continueBlockLabel) {
        for(int i=startIndex;i<endIndex;i++){
            for(int j=0;j<basicBlockList.get(i).getInstructionList().size();j++){
                if(basicBlockList.get(i).getInstructionList().get(j) instanceof Br &&basicBlockList.get(i).getInstructionList().get(j).getLabel1().equals("continueBlockId")){
                    basicBlockList.get(i).getInstructionList().get(j).setLabel1(continueBlockLabel);
                }
            }
        }
    }

    public void fillBreakLabel(int startIndex, int endIndex, String breakBlockId) {
        for(int i=startIndex;i<endIndex;i++){
            for(int j=0;j<basicBlockList.get(i).getInstructionList().size();j++){
                if(basicBlockList.get(i).getInstructionList().get(j) instanceof Br &&basicBlockList.get(i).getInstructionList().get(j).getLabel1().equals("breakBlockId")){
                    basicBlockList.get(i).getInstructionList().get(j).setLabel1(breakBlockId);
                }
            }
        }
    }

    public int getBasicBlockNum(){
        return basicBlockList.size();
    }

    public void fillUnCondNextBlock(int startId, int endId, BasicBlock nextBlock) {
        for(int i=startId;i<endId;i++){
            for(int j=0;j<basicBlockList.get(i).getInstructionList().size();j++){
                if(basicBlockList.get(i).getInstructionList().get(j) instanceof Br &&basicBlockList.get(i).getInstructionList().get(j).getLabel1().equals("nextBlock")){
                    basicBlockList.get(i).getInstructionList().get(j).setLabel1(nextBlock.getName());
                }
            }
        }
    }

    public Boolean getDeclare() {
        return isDeclare;
    }

    public MipsFunction generateMipsFunction() {
        MIPSGenerator.mipsModule.addSymbolTable(name);
        MipsFunction function=new MipsFunction(name);
        for(BasicBlock block:basicBlockList){
            function.addMipsBasicBlockList(block.generateMipsBlock());
        }
        if(name.equals("@main")){
            function.removeLastInstruction();
            ArrayList<MipsInstruction> list=new ArrayList<>();
            list.add(new Li(10,true));
            function.getLastBlock().addInstruction(list);

            //如果是main函数，需要提前计算开辟多少sp
            int spNum=getAllocaSp();
            MipsInstruction instruction=new Addu("$sp","$sp",String.valueOf(-spNum));
            function.getFirstBlock().getInstructionList().add(0,instruction);
            instruction=new Move("$fp","$sp");
            function.getFirstBlock().getInstructionList().add(0,instruction);
        }
        return function;
    }

    /**
     * @description: 对于main函数，提前计算需要sp栈空间并提前开辟出来
     * @date: 2024/11/29 14:23
     **/
    public int getAllocaSp() {
        int cnt=0;
        for(BasicBlock block:basicBlockList){
            for(Instruction instruction:block.getInstructionList()){
                if(instruction instanceof Alloca){
                    if(instruction.getElementNum()==0){
                        cnt+=4;
                    }else{
                        if(instruction.getType().getType() instanceof IntegerType){
                            cnt+= instruction.getElementNum()*4;
                        }else{
                            cnt+=(int) Math.ceil((double) instruction.getElementNum() / 8) * 8;
                        }
                    }
                }
            }
        }
        return cnt;
    }
}
