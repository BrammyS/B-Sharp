package nl.uni.cos.Visitors;

import nl.uni.cos.BSharpParser;
import nl.uni.cos.Models.DataType;

import java.util.ArrayList;

/**
 * The visitor that will initialize a list of methods that are present in the BSharp file.
 * This is needed to get the correct types for method calls
 * in the {@link TypeScopeBuilder} and in the {@link CodeChecker}.
 */
public class MethodDeclarationVisitor extends BaseDataTypeVisitor {

    /**
     * The arguments for the method that the visitor is in.
     */
    private ArrayList<DataType> args;

    @Override
    public DataType visitProgram(BSharpParser.ProgramContext ctx) {

        for (BSharpParser.MethodDeclarationContext methodCtx : ctx.methodDeclaration()) {
            visit(methodCtx);
        }

        return null;
    }

    // region Method declaration

    @Override
    public DataType visitMethodDeclaration(BSharpParser.MethodDeclarationContext ctx) {
        DataType methodType = ctx.primitiveTypes() != null
                ? visit(ctx.primitiveTypes())
                : DataType.VOID;

        visit(ctx.parametersDeclaration());

        // Save the method, so it can be used in the CodeChecker.
        addMethod(ctx.IDENTIFIER(), methodType, args != null ? args : new ArrayList<>());

        return methodType;
    }

    @Override
    public DataType visitParametersDeclaration(BSharpParser.ParametersDeclarationContext ctx) {
        args = visitAll(ctx.parameterDeclaration());
        return null;
    }

    @Override
    public DataType visitParameterDeclaration(BSharpParser.ParameterDeclarationContext ctx) {
        return visit(ctx.primitiveTypes());
    }

    // endregion
}
