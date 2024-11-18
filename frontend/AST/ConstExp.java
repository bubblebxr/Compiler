package frontend.AST;

public class ConstExp {
    protected AddExp addExp;
    public void setAddExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public String outputConstExp() {
        StringBuilder a=new StringBuilder();
        a.append(addExp.outputAddExp());
        a.append("<ConstExp>\n");
        return a.toString();
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public int getValue() {
        return addExp.getAddExpValue();
    }
}
