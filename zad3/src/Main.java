import java.util.Arrays;
import java.util.List;

public class Main {
    public static List<Integer> Buffsize = Arrays.asList(1000, 10000);
    public static List<Integer> PkConfig = Arrays.asList(100, 1000);
    public static List<Boolean> IsFair = Arrays.asList(true, false);
    public static List<String> Randomization = Arrays.asList("Equal", "Unequal");

    public static void main(String[] args) {
        assert args.length == 4;

        Integer buffSize = Integer.parseInt(args[0]);
        Integer pkConfig = Integer.parseInt(args[1]);
        boolean isFair = Boolean.parseBoolean(args[2]);
        String randomization = args[3];
        assert Buffsize.contains(buffSize);
        assert PkConfig.contains(pkConfig);
        assert IsFair.contains(isFair);
        assert Randomization.contains(randomization);

        try {
            Thread generator = new Thread(new Generator(buffSize, pkConfig, isFair, randomization));
            generator.start();
            generator.join();
            System.exit(0);
            // thread will kill the rest
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
