package  frontend.Token;

public class ErrorToken {
    protected int lineNumber;
    protected ErrorType type;

    public ErrorToken(int lineNumber,ErrorType type){
        this.lineNumber=lineNumber;
        this.type=type;
    }

    @Override
    public String toString() {
        return lineNumber+" "+this.type.toString()+"\n";
    }

    public int getLineNumber() {
        return lineNumber;
    }
}