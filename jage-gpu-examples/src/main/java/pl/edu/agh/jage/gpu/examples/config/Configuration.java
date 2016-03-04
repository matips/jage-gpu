package pl.edu.agh.jage.gpu.examples.config;

public class Configuration {
    int xSize = 100;
    int ySize = 100;

    public Configuration() {
        //TODO: move xSize to config file or command line
        if (System.getProperty("xSize") != null)
            this.xSize = Integer.parseInt(System.getProperty("xSize"));
        if (System.getProperty("ySize") != null)
            this.ySize = Integer.parseInt(System.getProperty("ySize"));
    }

    public int getxSize() {
        return xSize;
    }

    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    public int getySize() {
        return ySize;
    }

    public void setySize(int ySize) {
        this.ySize = ySize;
    }
}
