public class what {
    public static void main(String[] args) {
        byte a = 0b0111;
        byte b = 0b1010;
        int c = a<<4 | b;
        System.out.println(Integer.toBinaryString(c));

    }
}

