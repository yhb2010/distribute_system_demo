package com.chapter3message.rabbitmq.rpcdemo;

import java.io.Serializable;

public class Person implements Serializable {

    @Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + "]";
	}
	private static final long serialVersionUID = 1L;
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
    public Person(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }
	public Person() {
		super();
	}

}