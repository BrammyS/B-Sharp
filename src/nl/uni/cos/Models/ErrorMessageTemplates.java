package nl.uni.cos.Models;

/**
 * The class containing all the error message templates.
 * This is used to make it easier to avoid duplicated message templates in the code and unit tests.
 */
public enum ErrorMessageTemplates {
    // Type errors.
    UnknownTypeTemp("%s does not have a known type."),
    TypeMismatchTemp("Types do not match, %s %s."),

    // Method errors.
    MissingMainTemp("No Main method has been found."),
    MainParamsTemp("The Main method can not have any parameters."),
    DuplicatedMethodNamesTemp("Multiple methods found with the same identifier of %s."),
    UnknownMethodTemp("Method %s does not exist."),
    MissingReturnTemp("Method %s is missing a return statement."),
    WriteLineClassTemp("Unable to print a %s to the console."),

    // Variable errors.
    IncorrectVariableTypeTemp("%s can not be used for variable %s."),
    NotAVariableTemp("%s is not a variable."),
    VarNotInitializedTemp("%s has not been initialized yet."),
    DuplicatedVarNamesTemp("Multiple variables found with the same identifier of %s in the same scope."),
    UnknownVariableTemp("Variable %s does not exist in the current scope."),
    VarAssignExpectedTemp("Its only possible to assign a value to a variable."),

    // Misc errors.
    UnsupportedOperatorTemp("%s Does not support the %s operator.");

    private final String template;

    ErrorMessageTemplates(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public String toString() {
        return getTemplate();
    }
}
