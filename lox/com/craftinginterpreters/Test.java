package com.craftinginterpreters;

public class Test {
    int a = 0;
    void foo() {
        this.bar();
    }

    void bar() {
        System.out.println(a);
    }

    class Child1 extends Test {
        int a = 10;
        void foo() {
            bar();
        }
    }

    public static void main(String[] args) {
        Child1 testchild = new Test().new Child1();
        testchild.foo();
    }
}

