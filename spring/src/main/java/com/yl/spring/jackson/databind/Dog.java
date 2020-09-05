package com.yl.spring.jackson.databind;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Dog dog = new Dog();
        dog.setAge(3);
        dog.setName("hurry");
        Dog dog2 = new Dog();
        dog2.setAge(6);
        dog2.setName("jiji");
        ArrayList<Dog> dogList=new ArrayList<>();
        dogList.add(dog);
        dogList.add(dog2);

        String dogs = mapper.writeValueAsString(dog);
        System.out.println(dogs);
        Dog dog1 = mapper.readValue(dogs, Dog.class);
        if (dog1 != null) {
            System.out.println(dog1.age);
        }
        //read json file
        File studentJson = new File("src/main/resources/example.json");
        Student[] studentArray = mapper.readValue(studentJson, Student[].class);
        System.out.println(studentArray.length);
        System.out.println(studentArray[0].getId());
        System.out.println(studentArray[0].getLastname());
        //write object into json file
        File dogsJson=new File("src/main/resources/dogs.json");

        mapper.writeValue(dogsJson,dogList);

    }

}
