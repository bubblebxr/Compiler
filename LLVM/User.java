package LLVM;

import java.util.ArrayList;

public class User extends Value{
    protected ArrayList<Value> operators;//该user使用的value

    public User(String name, Type type) {
        super(name, type);
    }

    public User(String name, Type type,ArrayList<Value> operators) {
        super(name, type);
        this.operators=operators;
    }
}
