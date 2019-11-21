import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import javax.swing.JFrame;

import static java.util.stream.Collectors.toList;

public class Mandelbrot extends JFrame {
    private BufferedImage I;

    public Mandelbrot(Integer threadNumber) {
        super("Mandelbrot Set");
        setBounds(100, 100, 1600, 950);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        I = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        int numberOfTask = threadNumber*10; // number of task to be done;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        int imageSize = getHeight() * getWidth();
        int taskRange = imageSize/numberOfTask; // rounded down
        System.out.println("Image size = " + imageSize + "(" + getHeight() + "x" + getWidth() + ")");

        List<Task> tasks = IntStream.range(0, numberOfTask-1)
                .mapToObj(i -> new Task(i * taskRange, (i+1) * taskRange, I))
                .collect(toList());

        tasks.add(new Task((numberOfTask-1) * taskRange, imageSize, I)); // last task will correct the rounding if needed

        try{
            long time = System.nanoTime();
            executorService.invokeAll(tasks); // await for all task to finish
            executorService.shutdown();
            System.out.println(threadNumber + "," + (System.nanoTime() - time));
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(I, 0, 0, this);
    }

    public static void main(String[] args) {
        Integer threadNumber = Integer.parseInt(args[0]);
        assert threadNumber > 0;
        boolean draw = args.length != 2 || Boolean.parseBoolean(args[1]);
        new Mandelbrot(threadNumber).setVisible(draw);
    }
}

class Task implements Callable<Long> { // calculate values for [start, end] interval
    private int start;
    private int end;
    private final int MAX_ITER = 5000;
    private final double ZOOM = 400;
    private BufferedImage I;
    private double zx, zy, cX, cY, tmp;

    Task(int start, int end, BufferedImage I) {
        this.start = start;
        this.end = end;
        this.I = I;
    }

    @Override
    public Long call() throws Exception {
        System.out.println("Task started, calculating values [" + start + "," + end + ")");
        long time = System.nanoTime();
        for(int pix = start; pix < end; ++pix){
            int x = pix / this.I.getHeight();
            int y = pix % this.I.getHeight();
            zx = zy = 0;
            cX = (x - 1000) / ZOOM;
            cY = (y - 500) / ZOOM;
            int iter = MAX_ITER;
            while (zx * zx + zy * zy < 4 && iter > 0) {
                tmp = zx * zx - zy * zy + cX;
                zy = 2.0 * zx * zy + cY;
                zx = tmp;
                iter--;
            }
            this.I.setRGB(x, y, iter | (iter << 8));
        }
        return System.nanoTime()-time;
    }

}