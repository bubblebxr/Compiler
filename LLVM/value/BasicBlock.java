package LLVM.value;


import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Instruction;
import LLVM.value.Instruction.Jump.Ret;

import java.util.ArrayList;

/**
 * @className: BasicBlock
 * @author: bxr
 * @date: 2024/11/7 20:38
 * @description:
 */

public class BasicBlock extends Value {

    protected ArrayList<Instruction> instructionList;

    public BasicBlock(String name, Type type) {
        super(name, type);
        this.instructionList=new ArrayList<>();
    }

    public void addInstruction(Instruction instruction){
        instructionList.add(instruction);
    }

    public String toString(){
        StringBuilder a=new StringBuilder();
        for(int i=0;i<instructionList.size();i++){
            a.append("    ");
            a.append(instructionList.get(i).toString());
        }
        return a.toString();
    }

    public ArrayList<Instruction> getInstructionList() {
        return instructionList;
    }

    public boolean checkReturn(){
        if(!(instructionList.get(instructionList.size()-1) instanceof Ret)){
            return true;
        }else{
            return false;
        }
    }
}
