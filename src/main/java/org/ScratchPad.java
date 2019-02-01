package org;

import org.junit.Test;

public class ScratchPad {
    public static void main(String[] args) {
        Sport t = new TT();
        System.out.println(t.test());
        System.out.println(t.a);
    }

    private static void test(Integer integer){
        integer++;
        integer = integer + 10000;
    }
}

// 오버로딩은 접근제어자의 확장만 가능함. 리턴 타입 수정 불가능
class Parent{
    Integer a = 10;

    public Integer test(){
        return 0;
    }
}

class Child extends Parent{
    String a = "";
}

// 필드는 오버로딩이 아니라고 했는데.. 그냥 말만 그런듯

// 상속은 겹칠 수 없다


// 변수는 기본적으로 public static final이 붙으므로 생략 가능하다
// 메서드는 기본적으로 public 이 붙으므로 생략 가능하다
// 구현 인터페이스에 같은 메서드가 있을 경우 하나만 구현하면 된다.
// 원칙적으로 클래스내에 이름이 같은데 리턴타입이 다른것이 허용되지 않는다
interface Sport{
    int a = 10;
    default Integer test(){
        return 10;
    }
}

interface Study extends Sport{
    int a = 20;
    default Integer test(){
        return 20;
    }
}

// 인터페이스 또한 리턴타입 수정 불가능
//class Soccer implements Sport, Study{
//    String a = "20";
//}

class TT implements Study{

}