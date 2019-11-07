public class Main {
    public static Integer[] Bufsize = {(int) 1e4, (int) 1e5};
    public static String[]  Prod_or_cons = {"Prod", "Cons"};
    public static Integer   Size = 1000;
    public static Integer[] PK_config = {100, 1000};
    public static boolean[] Is_fair = {true, false};
    public static String[]  Randomization = {"Equal", "Unequal"};

    public static void main(String[] args){

        for (Integer bufsize : Bufsize)
        for (String prod_or_cons : Prod_or_cons)
        for (Integer pk_config : PK_config)
        for (boolean is_fair : Is_fair)
        for (String randomization : Randomization){
            try {
                Thread generator = new Thread(new Generator(bufsize, prod_or_cons, pk_config, is_fair, randomization));
                generator.start();
                generator.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }
}
