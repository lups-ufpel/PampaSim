package simuladorso.Command.Errors;

public class IllegalClassCall extends RuntimeException{
    public IllegalClassCall(String className){
        System.out.println("\n===================================== FATAL ERROR =====================================");
        System.out.println("*\tIllegal class call on Command/MainCommand/Invoker.java");
        System.out.println("*\tNo class \"" + className + "\" found");
        System.out.println("*\tVerify the class name given.");
        System.out.println("\n=======================================================================================");
    }
}
