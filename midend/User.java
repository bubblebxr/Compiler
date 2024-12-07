package midend;

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

    public String getLabel1(){
        return null;
    }

    public void setLabel1(String name){
    }

    public String getLabel2(){
        return null;
    }

    public void setLabel2(String name){
    }
}
