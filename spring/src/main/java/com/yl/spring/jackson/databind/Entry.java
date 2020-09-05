package com.yl.spring.jackson.databind;

import java.util.List;

public class Entry {
   private List<Dog> dogList;

    public List<Dog> getDogList() {
        return dogList;
    }

    public void setDogList(List<Dog> dogList) {
        this.dogList = dogList;
    }
}
