package MIPS.value;


import MIPS.MIPSGenerator;

import java.util.ArrayList;

/**
 * @className: MipsFunction
 * @author: bxr
 * @date: 2024/11/21 16:05
 * @description: mips function
 */

public class MipsFunction {
    protected String name;

    protected ArrayList<MipsBasicBlock> MipsBasicBlockList;

    public MipsFunction(String name){
        this.name=name.substring(1);
        this.MipsBasicBlockList=new ArrayList<>();
    }

    public void addMipsBasicBlockList(MipsBasicBlock mipsBasicBlock){
        this.MipsBasicBlockList.add(mipsBasicBlock);
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder(name);
        a.append(":\n");
        for(MipsBasicBlock block:MipsBasicBlockList){
            a.append(block.toString()).append("\n");
        }
        return a.toString();
    }

    public MipsBasicBlock getLastBlock(){
        return this.MipsBasicBlockList.get(MipsBasicBlockList.size()-1);
    }

    public String getName() {
        return name;
    }

    public void removeLastInstruction(){
        getLastBlock().instructionList.remove(getLastBlock().instructionList.size()-1);
    }

    public MipsBasicBlock getFirstBlock(){
        return MipsBasicBlockList.get(0);
    }
}
