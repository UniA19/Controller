package diy.esp8266.controller;

public class Globals {

    private static Globals instance;

    private boolean dark;

    private Globals() {
    }

    public void setDark(boolean d) {
        this.dark = d;
    }

    public boolean getDark() {
        return this.dark;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}