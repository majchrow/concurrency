import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import javax.swing.JFrame;

import static java.util.stream.Collectors.toList;

public class Mandelbrot extends JFrame {

    private final int MAX_ITER = 1000;
    private final double ZOOM = 150;
    private BufferedImage I;
    private double zx, zy, cX, cY, tmp;

    public Mandelbrot(Integer threadNumber) {
        super("Mandelbrot Set");
        setBounds(100, 100, 800, 600);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        I = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        int numberOfTask = threadNumber*10; // number of task to be done;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        int imageSize = getHeight() * getWidth();
        int taskRange = imageSize/numberOfTask; // rounded down
        System.out.println("Image size = " + imageSize + "(" + getHeight() + "x" + getWidth() + ")");

        List<Task> tasks = IntStream.range(0, numberOfTask-1)
                .mapToObj(i -> new Task(i * taskRange, (i+1) * taskRange) {})
                .collect(toList());

        tasks.add(new Task((numberOfTask-1) * taskRange, imageSize)); // last task will correct the rounding if needed

        try{
            List<Future<Map<Integer, Integer>>> resultList = executorService.invokeAll(tasks); // await for all task to finish
            for(Future<Map<Integer, Integer>> future: resultList){
               Map<Integer, Integer> map = future.get();
               for(Map.Entry<Integer, Integer> entry: map.entrySet()){

                   int x = entry.getKey() / getHeight();
                   int y = entry.getKey() % getHeight();
                   int iter = entry.getValue();
                   I.setRGB(x, y, iter | (iter << 8));
               }
           }
        }catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(I, 0, 0, this);
    }

    class Task implements Callable<Map<Integer, Integer>> { // calculate values for [start, end] interval
        int start;
        int end;

        public Task(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Map<Integer, Integer> call() throws Exception {
            System.out.println("Task started, calculating values [" + start + "," + end + ")");
            Map<Integer, Integer> pixels = new HashMap<>();
            for(int pix = start; pix < end; ++pix){
                int x = pix / getHeight();
                int y = pix % getHeight();
                zx = zy = 0;
                cX = (x - 400) / ZOOM;
                cY = (y - 300) / ZOOM;
                int iter = MAX_ITER;
                while (zx * zx + zy * zy < 4 && iter > 0) {
                    tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    iter--;
                }
                pixels.put(pix, iter);
            }
            return pixels;
        }
    }

    public static void main(String[] args) {
        assert args.length == 1;
        Integer threadNumber = Integer.parseInt(args[0]);
        assert threadNumber > 0;
        new Mandelbrot(threadNumber).setVisible(true);
    }
}