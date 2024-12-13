package optimize;
import midend.Module;

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
    }
}
