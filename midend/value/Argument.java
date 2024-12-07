package midend.value;


import midend.Type;
import midend.Value;

/**
 * @className: Argument
 * @author: bxr
 * @date: 2024/11/6 10:40
 * @description: 函数参数
 */

public class Argument extends Value {
    protected int id;

    public Argument(String name, Type type, int id){
        super(name,type);
        this.id=id;
    }

    public String toString(){
        return type.toString(name);
    }
}
