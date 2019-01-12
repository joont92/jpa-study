package org;

public class ScratchPad {
    public static void main(String[] args) {
        Integer a = 10000;
        test(a);

        System.out.println(a);
    }

    private static void test(Integer integer){
        integer++;
        integer = integer + 10000;
    }
}
