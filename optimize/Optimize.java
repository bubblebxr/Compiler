package optimize;
import midend.Module;
import midend.Value;
import midend.value.BasicBlock;
import midend.value.Function;
import midend.value.Instruction.Instruction;
import midend.value.Instruction.Jump.Br;
import midend.value.Instruction.Memory.Alloca;
import midend.value.Instruction.Memory.Load;
import midend.value.Instruction.Memory.Store;
import midend.value.Instruction.Operate.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @className: Optimize
 * @author: bxr
 * @date: 2024/12/9 16:21
 * @description: 中间代码优化
 */

public class Optimize {
    protected Module module;
    protected Map<String,Map<String,Long>> constMaps;

    public Optimize(Module module,Map<String,Map<String,Long>> constMaps){
        this.module=module;
        this.constMaps=constMaps;
    }

    public String outputOptimize() {
        return module.toString();
    }

    /**
     * @description: 中间代码优化的汇总函数
     * @date: 2024/12/9 16:24
     **/
    public void optimizer() {
        // 删除跳转到下一个基本块的br跳转
        deleteUnNeedBr();

        // 删除在函数中的常量定义
//        deleteConstAlloca();

        // 常量的传播
        constSpread();

        // 删除死函数
        deleteDeadFunc();

        // 删除死代码
        for(Function function:module.getFunctionList()){
            deleteDeadCode(function);
        }
    }

//    public void deleteConstAlloca() {
//        Set<Instruction> constAlloca=new HashSet<>();
//        for(Function function:module.getFunctionList()){
//            for (BasicBlock block:function.getBasicBlockList()){
//                for (int i=0;i<block.getInstructionList().size();i++){
//                    if(block.getInstructionList().get(i) instanceof Alloca&& ((Alloca) block.getInstructionList().get(i)).isConst){
//                        constAlloca.add(block.getInstructionList().get(i));
//                        if(i+1<block.getInstructionList().size()){
//                            constAlloca.add(block.getInstructionList().get(i+1));
//                        }
//                    }
//                }
//            }
//        }
//
//        for(Function function:module.getFunctionList()) {
//            for (BasicBlock block : function.getBasicBlockList()) {
//                block.getInstructionList().removeIf(constAlloca::contains);
//            }
//        }
//    }


    /**
     * @description: 删除死函数
     * @date: 2024/12/19 18:16
     **/
    public void deleteDeadFunc() {
        HashMap<Function, Boolean> checkFunction = new HashMap<>();
        for (Function irFunction : module.getFunctionList()) {
            if (irFunction.getName().equals("@main")) {
                checkFunction.put(irFunction, true);
            } else {
                checkFunction.put(irFunction, false);
            }
        }

        // 搜索调用过的函数
        ArrayBlockingQueue<Function> queue = new ArrayBlockingQueue<>(1000);
        queue.add(module.getFunctionList().get(module.getFunctionList().size()-1));
        while (!queue.isEmpty()) {
            Function function = queue.poll();
            checkFunction.put(function, true);
            for (Function calledFunction : function.getCallFunctions()) {
                if (!checkFunction.get(calledFunction)) {
                    queue.add(calledFunction);
                }
            }
        }

        module.getFunctionList().removeIf(irFunction -> !checkFunction.get(irFunction)&&
                module.getFunctionList().indexOf(irFunction) >= 4);
    }

    /**
     * @description: 删除死代码
     * @date: 2024/12/19 17:05
     **/
    public void deleteDeadCode(Function function) {
        HashMap<String, Instruction> checkValue = new HashMap<>();
        for(BasicBlock block:function.getBasicBlockList()){
            for (int i=0;i<block.getInstructionList().size();i++){
                // 如果是alloca，就加入到check中
                if(block.getInstructionList().get(i) instanceof Alloca){
                    block.getInstructionList().get(i).isUse=false;
                    checkValue.put(block.getInstructionList().get(i).getName(),block.getInstructionList().get(i));
                }

                // 如果有被调用的时候就将check设置为true，表示被使用过
                if(block.getInstructionList().get(i).getOperators()!=null){
                    for(Value value:block.getInstructionList().get(i).getOperators()){
                        if(checkValue.containsKey(value.getName())){
                            checkValue.get(value.getName()).isUse=true;
                            break;
                        }
                    }
                }
            }
        }

        for(BasicBlock block:function.getBasicBlockList()){
            block.getInstructionList().removeIf(instruction -> checkValue.containsValue(instruction)&&!instruction.isUse);
        }
    }

    /**
     * @description: 用于常量的传播
     * @date: 2024/12/19 15:39
     **/
    public void constSpread() {
        for(Function function:module.getFunctionList()){
            String funcName=function.getName();
            for(BasicBlock block:function.getBasicBlockList()){
                for(int i=0;i<block.getInstructionList().size();i++){
                    // 如果存在可以找到的常数就进行替换
                    if(block.getInstructionList().get(i).getOperators()!=null){
                        Map<String,Long> constVar=new HashMap<>();
                        if(constMaps.get("Global")!=null){
                            constVar.putAll(constMaps.get("Global"));
                        }
                        if(constMaps.get(funcName)!=null){
                            constVar.putAll(constMaps.get(funcName));
                        }
                        for(Value value:block.getInstructionList().get(i).getOperators()){
                            if(constVar.containsKey(value.getName())&&block.getInstructionList().get(i) instanceof Load){
                                // %24 = load i32,i32* 1
                                constMaps.get(funcName).put(block.getInstructionList().get(i).getName(),constVar.get(value.getName()));
                                block.getInstructionList().remove(i);
                                i--;
                            }else if(constVar.containsKey(value.getName())){
                                value.setName(String.valueOf(constVar.get(value.getName())));
                            }
                        }
                    }

                    // 如果可以直接计算得出结果
                    if(i>=block.getInstructionList().size()){
                        break;
                    }else if(i<0){
                        continue;
                    }
                    Instruction instruction=block.getInstructionList().get(i);
                    if(instruction instanceof Add
                            &&instruction.getOperators().get(0).getName().charAt(0)!='%'
                            &&instruction.getOperators().get(1).getName().charAt(0)!='%'
                    ){
                        constMaps.get(funcName).put(instruction.getName(),Long.parseLong(instruction.getOperators().get(0).getName())+Long.parseLong(instruction.getOperators().get(1).getName()));
                        block.getInstructionList().remove(i);
                        i--;
                    }else if(instruction instanceof Sub
                            &&instruction.getOperators().get(0).getName().charAt(0)!='%'
                            &&instruction.getOperators().get(1).getName().charAt(0)!='%'
                    ){
                        constMaps.get(funcName).put(instruction.getName(),Long.parseLong(instruction.getOperators().get(0).getName())-Long.parseLong(instruction.getOperators().get(1).getName()));
                        block.getInstructionList().remove(i);
                        i--;
                    }else if(instruction instanceof Mul
                            &&instruction.getOperators().get(0).getName().charAt(0)!='%'
                            &&instruction.getOperators().get(1).getName().charAt(0)!='%'
                    ){
                        constMaps.get(funcName).put(instruction.getName(),Long.parseLong(instruction.getOperators().get(0).getName())*Long.parseLong(instruction.getOperators().get(1).getName()));
                        block.getInstructionList().remove(i);
                        i--;
                    }else if(instruction instanceof Sdiv
                            &&instruction.getOperators().get(0).getName().charAt(0)!='%'
                            &&instruction.getOperators().get(1).getName().charAt(0)!='%'
                    ){
                        constMaps.get(funcName).put(instruction.getName(),Long.parseLong(instruction.getOperators().get(0).getName())/Long.parseLong(instruction.getOperators().get(1).getName()));
                        block.getInstructionList().remove(i);
                        i--;
                    }else if(instruction instanceof Srem
                            &&instruction.getOperators().get(0).getName().charAt(0)!='%'
                            &&instruction.getOperators().get(1).getName().charAt(0)!='%'
                    ){
                        constMaps.get(funcName).put(instruction.getName(),Long.parseLong(instruction.getOperators().get(0).getName())%Long.parseLong(instruction.getOperators().get(1).getName()));
                        block.getInstructionList().remove(i);
                        i--;
                    }
                }
            }
        }
    }

    /**
     * @description: 删除跳转到下一个基本块的br跳转
     * @date: 2024/12/18 16:50
     **/
    public void deleteUnNeedBr() {
        for(Function function:module.getFunctionList()){
            for(int i=0;i<function.getBasicBlockList().size();i++){
                Instruction instruction=function.getBasicBlockList().get(i).getInstructionList().get(function.getBasicBlockList().get(i).getInstructionList().size()-1);
                if(instruction instanceof Br
                        &&((Br) instruction).getCond()==null
                        &&i<function.getBasicBlockList().size()-1
                        &&instruction.getLabel1().equals(function.getBasicBlockList().get(i+1).getName())){
                    function.getBasicBlockList().get(i).getInstructionList().remove(function.getBasicBlockList().get(i).getInstructionList().size()-1);
                }
            }
        }
    }

    public Module getModule() {
        return module;
    }
}
