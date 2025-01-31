package org.example.fractalgenerator;

public class TestCase {

    public static void main(String[] args) {
        System.out.println("Hello, World!");

        Human human = new Human("Alice", 25);
        human.sayAll();

        human.setName("Bob");
        human.setAge(30);
        human.sayAll();
    }
}
