package Events;

public class Stopwatch { 

    private final long start;

    /**
     * Initializes a new stopwatch.
     */
    public Stopwatch() {
        start = System.nanoTime();
    } 


    /**
     * Returns the elapsed CPU time (in seconds) since the stopwatch was created.
     *
     * @return elapsed CPU time (in seconds) since the stopwatch was created
     */
    public double elapsedTime() {
        long now = System.nanoTime();
        return (now - start) / 1000000000.0;
    }

    
    /**
     * Unit tests the {@code Stopwatch} data type.
     * Takes a command-line argument {@code n} and computes the 
     * sum of the square roots of the first {@code n} positive integers,
     * first using {@code Math.sqrt()}, then using {@code Math.pow()}.
     * It prints to standard output the sum and the amount of time to
     * compute the sum. Note that the discrete sum can be approximated by
     * an integral - the sum should be approximately 2/3 * (n^(3/2) - 1).
     *
     * @param args the command-line arguments
     */
} 
