package com.yl.spring.kafka;

public class Greeting {
    private String msg;
    private String name;

    public Greeting(){
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Greeting{" +
                "msg='" + msg + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
