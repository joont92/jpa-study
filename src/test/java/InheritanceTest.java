import org.junit.Test;

public class InheritanceTest {
    @Test
    public void inheritanceTest() {
        B b = new B();
        b.printA();
    }
}

class A{
    protected long a;

    public void printA(){
        System.out.println(a);
    }
}

class B extends A{
    Long a = 20L;

    @Override
    public void printA() {
        System.out.println(a.byteValue());
    }
}
