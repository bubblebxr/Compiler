package backend.value;


import backend.Instruction.MipsInstruction;
import backend.reg.LocalRegister;
import midend.value.BasicBlock;
import midend.value.Instruction.Instruction;

import java.util.ArrayList;

/**
 * @className: MipsBasicBlock
 * @author: bxr
 * @date: 2024/11/21 16:05
 * @description:
 */

public class MipsBasicBlock {
    protected BasicBlock irBlock;
    protected String name;
    protected ArrayList<MipsInstruction> instructionList;
    protected LocalRegister reg; // 局部寄存器，跳出该块时所有寄存器都释放

    public MipsBasicBlock(BasicBlock irBlock){
        this.reg=new LocalRegister();
        this.irBlock=irBlock;
        if(irBlock.getName()!=null){
            this.name=irBlock.getName().substring(1);
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
            a.append(".").append(name).append(":\n");
        }
        for(MipsInstruction instruction:instructionList){
            a.append("    ").append(instruction.toString());
            a.append("\n");
        }
        return a.toString();
    }

    public ArrayList<MipsInstruction> getInstructionList() {
        return instructionList;
    }

    public LocalRegister getReg() {
        return reg;
    }

    public void genMipsFromIr() {
        for(Instruction instruction:irBlock.getInstructionList()){
            addInstruction(instruction.genMips());
        }
    }

    public String getName() {
        return name;
    }
}
