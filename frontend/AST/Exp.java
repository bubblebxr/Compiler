package frontend.AST;

public class Exp {
    protected AddExp addExp;

    public void setAddExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public String outputExp() {
        StringBuilder a=new StringBuilder();
        a.append(addExp.outputAddExp());
        a.append("<Exp>\n");
        return a.toString();
    }
}
