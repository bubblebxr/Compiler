package MIPS.value;


import MIPS.Instruction.MipsInstruction;

import java.util.ArrayList;

/**
 * @className: MipsBasicBlock
 * @author: bxr
 * @date: 2024/11/21 16:05
 * @description:
 */

public class MipsBasicBlock {
    protected String name;
    protected ArrayList<MipsInstruction> instructionList;

    public MipsBasicBlock(String name){
        if(name!=null){
            this.name=name.substring(1);
        }else{
            this.name=null;
        }
        this.instructionList=new ArrayList<>();
    }

    public void addInstruction(ArrayList<MipsInstruction> list){
        if(list!=null){
            this.instructionList.addAll(list);
        }
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        if(name!=null){
            a.append(name).append(":\n");
        }
        for(MipsInstruction instruction:instructionList){
            a.append("  ").append(instruction.toString());
            a.append("\n");
        }
        return a.toString();
    }

    public ArrayList<MipsInstruction> getInstructionList() {
        return instructionList;
    }
}
