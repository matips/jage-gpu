package pl.edu.agh.jage.gpu.examples;

import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringXmlBaseRegistry implements ExternalExecutorRegistry {

    private final ClassPathXmlApplicationContext classPathXmlApplicationContext;
    private final ExternalExecutorRegistry context;

    public SpringXmlBaseRegistry(String xmlConfiguration) {
        classPathXmlApplicationContext = new ClassPathXmlApplicationContext(xmlConfiguration);
        context = classPathXmlApplicationContext.getBean(ExternalExecutorRegistry.class);
    }

    @Override
    public ExternalExecutor get(String name) {
        return context.get(name);
    }

    @Override
    public void flush() {
        context.flush();
    }
}
