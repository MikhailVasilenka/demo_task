package com.nphase.entity;

public enum Category {

	DRINKS("drinks"),

	FOOD("food");

	public String getName() {
		return name;
	}

	private final String name;

	Category(String name) {
		this.name = name;
	}

}
