package com.chapter6zookeeper.queue;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {

	String name;
	String id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", id=" + id + "]";
	}

}