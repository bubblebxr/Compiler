package midend;

public class Value {
    protected String name;
    protected Type type;

    public Value(String name,Type type){
        this.name=name;
        this.type=type;
    }

    public String getName() {
        return name;
    }

    public Type getType(){
        return type;
    }

    @Override
    public String toString(){
        return type.toString()+" "+name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
