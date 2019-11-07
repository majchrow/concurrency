import java.util.Arrays;
import java.util.List;

public class Main {
    public static List<Integer> Buffsize = Arrays.asList(1000, 10000);
    public static List<String> ProdOrCons = Arrays.asList("Prod", "Cons");
    public static List<Integer> PkConfig = Arrays.asList(100, 1000);
    public static List<Boolean> IsFair = Arrays.asList(true, false);
    public static List<String> Randomization = Arrays.asList("Equal", "Unequal");

    public static void main(String[] args) {
        assert args.length == 5;

        Integer buffSize = Integer.parseInt(args[0]);
        String prodOrCons = args[1];
        Integer pkConfig = Integer.parseInt(args[2]);
        Boolean isFair = Boolean.parseBoolean(args[3]);
        String randomization = args[4];

        assert Buffsize.contains(buffSize);
        assert ProdOrCons.contains(prodOrCons);
        assert PkConfig.contains(pkConfig);
        assert IsFair.contains(isFair);
        assert Randomization.contains(randomization);

        System.out.println("Started with bufsize=" + buffSize +
                " prod/cons=" + prodOrCons +
                " config= " + pkConfig +
                " is_fair" + isFair +
                " randomization=" + randomization);
        try {
            Thread generator = new Thread(new Generator(buffSize, prodOrCons, pkConfig, isFair, randomization));
            generator.start();
            generator.join();
            System.exit(0); // Killing main thread will kill the rest
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
