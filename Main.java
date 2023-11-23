public class Main {
    public static void main(String[] args) {
        SomeViewModel someViewModel = new SomeViewModel();
        OtherViewModel otherViewModel = new OtherViewModel();

        someViewModel.sendMessageToOtherViewModel();

        // Faz alguma outra coisa...

        someViewModel.cleanUp();
        otherViewModel.cleanUp();
    }
}
