package nl.uni.cos.Exceptions;

public class CompilerException extends RuntimeException {

    public CompilerException(String msg) {
        super(msg);
    }

    public CompilerException(String msg, Exception innerException) {
        super(msg, innerException);
    }
}
