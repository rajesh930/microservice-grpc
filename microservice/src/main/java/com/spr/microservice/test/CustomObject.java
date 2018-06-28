package com.spr.microservice.test;

/**
 * User: rajesh
 * Date: 28/06/18
 * Time: 5:45 PM
 */
public class CustomObject {
    private final String value1;
    private final String value2;

    public CustomObject(String value1, String value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "CustomObject{" +
                "value1='" + value1 + '\'' +
                ", value2='" + value2 + '\'' +
                '}';
    }
}
