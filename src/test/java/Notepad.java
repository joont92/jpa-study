import org.junit.Test;

public class Notepad {
    @Test
    public void stringTest() {
        String a = "🥰";
        for (char c : a.toCharArray()) {
            System.out.println(c);
        }
    }
}
