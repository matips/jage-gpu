package pl.edu.agh.jage.gpu.examples.adding;

import org.jage.platform.cli.CliNodeBootstrapper;

public class AddingApplicationGPU {

    public static void main(String[] args) {
        CliNodeBootstrapper bootstrapper = new CliNodeBootstrapper(
                new String[] { "-Dage.node.conf=classpath:adding/evolutus_simulation_gpu.xml" });
        bootstrapper.start();
    }
}
