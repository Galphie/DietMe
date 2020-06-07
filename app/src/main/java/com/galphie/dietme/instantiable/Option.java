package com.galphie.dietme.instantiable;

public class Option {
    private String name;
    private int code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Option() {
    }

    public Option(String name, int code) {
        this.name = name;
        this.code = code;
    }
}
