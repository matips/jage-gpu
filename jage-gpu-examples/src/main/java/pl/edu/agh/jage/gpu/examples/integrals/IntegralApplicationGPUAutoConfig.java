package pl.edu.agh.jage.gpu.examples.integrals;

import org.jage.platform.cli.CliNodeBootstrapper;

public class IntegralApplicationGPUAutoConfig {

    public static void main(String[] args) {
        CliNodeBootstrapper bootstrapper = new CliNodeBootstrapper(
                new String[] { "-Dage.node.conf=classpath:integralWithAutoConfiguration/evolutus_integral_gpu.xml" });
        bootstrapper.start();
    }
}
