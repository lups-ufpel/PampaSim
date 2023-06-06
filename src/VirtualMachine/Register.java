package VirtualMachine;
public final class Register {

    private String content;
    private final boolean privileged;

    // fazer a verificação do tamanho máximo da palavra de memoria do conteudo do registrador
    public Register(String value){

        this.content = new String(value);
        this.privileged = false;
    }
    public Register(String value, boolean privilege){

        this.content = new String(value);
        this.privileged = privilege;
    }
    public String getContent(){
        return this.content;
    }
    public void setContent(String value) {
        this.content = value;
    }
    public void setContent(int value) {

        this.content = Integer.toString(value);
    }

    public int getContentAsInt(){

        // fazer tratamento de excção 
        return Integer.parseInt(this.content);
    }

    @Override
    public String toString() {
        return "Register{" +
                "value=" + 
                content +
                '}';
    }
} 
