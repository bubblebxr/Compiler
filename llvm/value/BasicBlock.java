package llvm.value;


import llvm.Type;
import llvm.Value;
import llvm.value.Instruction.Instruction;
import llvm.value.Instruction.Jump.Br;
import llvm.value.Instruction.Jump.Ret;

import java.util.ArrayList;

/**
 * @className: BasicBlock
 * @author: bxr
 * @date: 2024/11/7 20:38
 * @description:
 */

public class BasicBlock extends Value {

    protected ArrayList<Instruction> instructionList;

    protected ArrayList<BasicBlock> LAndExpList;  // if无条件跳转块中包含的所有的LAnd块

    public BasicBlock(String name, Type type) {
        super(name, type);
        this.instructionList=new ArrayList<>();
        this.LAndExpList=new ArrayList<>();
    }

    public void addInstruction(Instruction instruction){
        instructionList.add(instruction);
    }

    public String toString(){
        StringBuilder a=new StringBuilder();
        if(name!=null){
            a.append(name.substring(1));
            a.append(":");
            a.append("\n");
        }
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
        return instructionList.isEmpty() || !(instructionList.get(instructionList.size() - 1) instanceof Ret);
    }

    public Instruction getLastInstruction(){
        return instructionList.get(instructionList.size()-1);
    }

    public void addLAndExpList(BasicBlock basicBlock){
        LAndExpList.add(basicBlock);
    }


    public void fillNextAndLabel(String name) {
        for (BasicBlock basicBlock : LAndExpList) {
            if (basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1) instanceof Br && basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).getLabel1().equals("nextAndLabel")) {
                basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).setLabel1(name);
            }
        }
    }

    public void fillNextOrFirstAndLabelToElseStmt() {
        for (BasicBlock basicBlock : LAndExpList) {
            if (basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1) instanceof Br && basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).getLabel2().equals("nextOrFirstAndLabel")) {
                basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).setLabel2("elseStmt");
            }
        }
    }

    public void fillNextOrFirstAndLabel(String name) {
        for (BasicBlock basicBlock : LAndExpList) {
            if (basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1) instanceof Br && basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).getLabel2().equals("nextOrFirstAndLabel")) {
                basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).setLabel2(name);
            }
        }
    }

    public void fillThenStmt(BasicBlock thenStmt) {
        for (BasicBlock basicBlock : LAndExpList) {
            if (basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1) instanceof Br && basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).getLabel1().equals("thenStmt")) {
                basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).setLabel1(thenStmt.getName());
            }
        }
    }

    public void fillElseStmtToNextBlock() {
        for (BasicBlock basicBlock : LAndExpList) {
            if (basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1) instanceof Br && basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).getLabel2().equals("elseStmt")) {
                basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).setLabel2("nextBlock");
            }
        }
    }

    public void fillElseStmt(BasicBlock elseStmt) {
        for (BasicBlock basicBlock : LAndExpList) {
            if (basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1) instanceof Br && basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).getLabel2().equals("elseStmt")) {
                basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).setLabel2(elseStmt.getName());
            }
        }
    }

    public void fillNextBlock(BasicBlock nextBlock) {
        for (BasicBlock basicBlock : LAndExpList) {
            if (basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1) instanceof Br && basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).getLabel2().equals("nextBlock")) {
                basicBlock.getInstructionList().get(basicBlock.getInstructionList().size() - 1).setLabel2(nextBlock.getName());
            }
        }
    }

    public void fillUnCondNextBlock(BasicBlock nextBlock) {
        if(!instructionList.isEmpty()){
            if(instructionList.get(instructionList.size()-1) instanceof Br&&instructionList.get(instructionList.size()-1).getLabel1().equals("nextBlock")){
                instructionList.get(instructionList.size()-1).setLabel1(nextBlock.getName());
            }
        }
    }

    public void fillForStmt(BasicBlock forStmt) {
        if(!instructionList.isEmpty()){
            if(instructionList.get(instructionList.size()-1) instanceof Br&&instructionList.get(instructionList.size()-1).getLabel1().equals("thenStmt")){
                instructionList.get(instructionList.size()-1).setLabel1(forStmt.getName());
            }
        }
    }

    public void fillNextForBlockStmt(BasicBlock nextForBlock) {
        if(!instructionList.isEmpty()){
            if(instructionList.get(instructionList.size()-1) instanceof Br&&instructionList.get(instructionList.size()-1).getLabel2()!=null&&(instructionList.get(instructionList.size()-1).getLabel2().equals("elseStmt"))){
                instructionList.get(instructionList.size()-1).setLabel2(nextForBlock.getName());
            }
        }
        for (BasicBlock basicBlock : LAndExpList) {
            basicBlock.fillNextForBlockStmt(nextForBlock);
        }
    }
}
