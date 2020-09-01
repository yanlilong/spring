package com.yl.spring.jackson.databind;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Dog {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(String []args) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        Dog dog=new Dog();
        dog.setAge(3);
        dog.setName("hurry");

       String dogs= mapper.writeValueAsString(dog);
        System.out.println(dogs);
       Dog dog1= mapper.readValue(dogs,Dog.class);
       if(dog1!=null){
           System.out.println(dog1.age);
       }

    }

}
