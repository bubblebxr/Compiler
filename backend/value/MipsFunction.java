package backend.value;


import backend.Instruction.Memory.Lb;
import backend.Instruction.Memory.Lw;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Addi;
import backend.MipsGenerator;
import backend.reg.GlobalRegister;
import midend.value.BasicBlock;
import midend.value.Function;

import java.util.ArrayList;

/**
 * @className: MipsFunction
 * @author: bxr
 * @date: 2024/11/21 16:05
 * @description: mips function
 */

public class MipsFunction {
    protected Function irFunction;
    protected String name;
    protected Boolean isMain;
    protected GlobalRegister reg;
    protected ArrayList<MipsBasicBlock> blockList;
    protected int argumentCnt;
    // 存储每次需要在开始提前开辟的栈空间
    public int spNumForFunc;

    public MipsFunction(Function irFunction){
        this.reg=new GlobalRegister();
        this.name=irFunction.getName().substring(1);
        isMain= this.name.equals("main");
        this.irFunction=irFunction;
        this.blockList =new ArrayList<>();
        for(BasicBlock block:irFunction.getBasicBlockList()){
            blockList.add(new MipsBasicBlock(block));
        }
        argumentCnt=irFunction.getArgumentCnt();
        this.spNumForFunc=0;
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder(name).append(":\n");
        for(MipsBasicBlock block: blockList){
            a.append(block.toString()).append("\n");
        }
        return a.toString();
    }

    public String getName() {
        return name;
    }

    public GlobalRegister getReg() {
        return reg;
    }

    public Boolean getMain() {
        return isMain;
    }

    public void genMipsFromIr() {
        int i=0;
        for(MipsBasicBlock block:blockList){
            MipsGenerator.curBlockIndex=i++;
            block.genMipsFromIr();
        }
        if(spNumForFunc!=0){
            blockList.get(0).getInstructionList().add(0,(new Addi("$sp","$sp",Integer.toString(-spNumForFunc))));
            blockList.get(blockList.size()-1).getInstructionList().add(blockList.get(blockList.size()-1).getInstructionList().size()-1,(new Addi("$sp","$sp",Integer.toString(spNumForFunc))));
        }
        if(!isMain){
            for(MipsInstruction instruction:blockList.get(0).getInstructionList()){
                if(instruction instanceof Lb ||instruction instanceof Lw){
                    int offset=instruction.getOffset();
                    if(offset<0){
                        instruction.setOffset(-(argumentCnt+offset)*4);
                    }
                }
            }
        }
    }

    public ArrayList<MipsBasicBlock> getBlockList() {
        return blockList;
    }

    public void updateSp(int offset){
        spNumForFunc+=offset;
    }
}
