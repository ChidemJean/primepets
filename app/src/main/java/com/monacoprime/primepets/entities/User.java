package com.monacoprime.primepets.entities;

public class User {
    private long id;

    public User(long id){
        this.id = id;
    }


    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
