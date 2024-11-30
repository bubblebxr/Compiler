package llvm;


/**
 * @className: Pair
 * @author: bxr
 * @date: 2024/11/11 20:12
 * @description: 用于返回两个函数值
 */

public class Pair {
    public String id;
    public Type type;

    public Pair(String id,Type type){
        this.id=id;
        this.type=type;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }
}
