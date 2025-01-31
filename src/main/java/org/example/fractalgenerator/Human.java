package org.example.fractalgenerator;

import java.util.Date;

public class Human {
    private String name;
    private int age;
    private Date todayDate;

    public Human(String name, int age) {
        this.name = name;
        this.age = age;
        todayDate = new Date();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void sayHello() {
        System.out.println("Hello, " + name + "! How are you " + todayDate + "?");
    }

    public void sayGoodbye() {
        System.out.println("Goodbye, " + name + "! Have a great day!\nWhat are you going to do today? HOw have you been");
    }

    protected void sayGoodmorning() {
        System.out.println("Good morning, " + name + "! How did you sleep?");
    }

    public void sayGoodnight() {
        System.out.println("Goodnight, " + name + "! Sweet dreams!");
    }

    public void sayGoodafternoon() {
        System.out.println("Good afternoon, " + name + "! How is your day going?");
    }

    public void sayAll() {
        sayHello();
        sayGoodafternoon();
        sayGoodmorning();
        sayGoodbye();
        sayGoodnight();
    }

    private void calculateAgeTimes2() {
        System.out.println("Your age is: " + age*2);
    }
}
