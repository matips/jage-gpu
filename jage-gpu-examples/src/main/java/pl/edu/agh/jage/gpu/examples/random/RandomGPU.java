package pl.edu.agh.jage.gpu.examples.random;
import org.jage.platform.cli.CliNodeBootstrapper;

public class RandomGPU {

    public static void main(String[] args) {
        CliNodeBootstrapper bootstrapper = new CliNodeBootstrapper(
                new String[] { "-Dage.node.conf=classpath:random/evolutus_random_gpu.xml" });
        bootstrapper.start();
    }
}
