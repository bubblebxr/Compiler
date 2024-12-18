package optimize;
import midend.Module;
import midend.value.BasicBlock;
import midend.value.Function;
import midend.value.Instruction.Instruction;
import midend.value.Instruction.Jump.Br;

/**
 * @className: Optimize
 * @author: bxr
 * @date: 2024/12/9 16:21
 * @description: 中间代码优化
 */

public class Optimize {
    protected Module module;

    public Optimize(Module module){
        this.module=module;
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
