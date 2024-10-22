package frontend.AST;

public class Cond {
    protected LOrExp lOrExp;

    public String outputCond() {
        StringBuilder a=new StringBuilder();
        a.append(lOrExp.outputLOrExp());
        a.append("<Cond>\n");
        return a.toString();
    }

    public void setlOrExp(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public LOrExp getlOrExp() {
        return lOrExp;
    }
}
