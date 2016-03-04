//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jage.gpu.workplace;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.inject.Inject;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.IAgentEnvironment;
import org.jage.agent.ISimpleAgent;
import org.jage.agent.ISimpleAgentEnvironment;
import org.jage.agent.SimpleAggregate;
import org.jage.platform.component.exception.ComponentException;
import org.jage.property.PropertyField;
import org.jage.workplace.IWorkplaceEnvironment;
import org.jage.workplace.IllegalOperationException;
import org.jage.workplace.Workplace;
import org.jage.workplace.WorkplaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * This is copy of SimpleWorkplace class. The reason for it is that original SimpleWorkplace does not provide access to modify steps field without call of .step()
 */
public class SimpleWorkplace extends SimpleAggregate implements Workplace<ISimpleAgent>, Runnable {
    public static final String STEP_PROPERTY_NAME = "step";
    private static final long serialVersionUID = 5L;
    private static final long PAUSE_DELAY = 1000L;
    private static final Logger log = LoggerFactory.getLogger(SimpleWorkplace.class);
    @Nonnull
    @GuardedBy("stateMonitor")
    private State state;
    @Nonnull
    private final Object stateMonitor;
    @Nullable
    private IWorkplaceEnvironment workplaceEnvironment;
    @PropertyField(
        propertyName = "step",
        isMonitorable = true
    )
    protected long step;

    public SimpleWorkplace(AgentAddress address) {
        super(address);
        this.state = State.STOPPED;
        this.stateMonitor = new Object();
        this.step = 0L;
    }

    @Inject
    public SimpleWorkplace(AgentAddressSupplier supplier) {
        super(supplier);
        this.state = State.STOPPED;
        this.stateMonitor = new Object();
        this.step = 0L;
    }

    protected ISimpleAgentEnvironment getAgentEnvironment() {
        throw new IllegalOperationException("Agent environment is not applicable to workplace.");
    }

    public void setAgentEnvironment(IAgentEnvironment localEnvironment) {
        throw new IllegalOperationException("Agent environment is not applicable to workplace.");
    }

    public void start() {
        log.info("{} is starting...", this.getAddress());
        Object var1 = this.stateMonitor;
        synchronized(this.stateMonitor) {
            Preconditions.checkState(this.isStopped(), "Workplace has been already started.");
            Thread thread = new Thread(this);
            thread.setName(this.getAddress().toString());
            thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    SimpleWorkplace.log.error("Exception caught during run", e);
                    SimpleWorkplace.this.setStopped();
                }
            });
            thread.start();
        }
    }

    public void pause() {
        log.debug("{} asked to pause.", this);
        Object var1 = this.stateMonitor;
        synchronized(this.stateMonitor) {
            Preconditions.checkState(this.isRunning(), "Workplace is not running.");
            this.state = State.PAUSED;
        }
    }

    public void resume() {
        log.debug("{} asked to resume.", this);
        Object var1 = this.stateMonitor;
        synchronized(this.stateMonitor) {
            Preconditions.checkState(this.isPaused(), "Workplace is not paused.");
            this.state = State.RUNNING;
        }
    }

    public void stop() {
        log.debug("{} asked to stop.", this);
        Object var1 = this.stateMonitor;
        synchronized(this.stateMonitor) {
            Preconditions.checkState(this.isRunning() || this.isPaused(), "Workplace is not running or paused.");
            this.state = State.STOPPING;
        }
    }

    public boolean finish() throws ComponentException {
        Object var1 = this.stateMonitor;
        synchronized(this.stateMonitor) {
            Preconditions.checkState(this.isStopped(), "Illegal use of finish. Must invoke stop() first.");
            super.finish();
            log.info("{} has been shut down.", this);
            return true;
        }
    }

    public void run() {
        this.setRunning();
        log.info("{} has been started.", this);

        while(this.isRunning() || this.isPaused()) {
            if(this.isPaused()) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var2) {
                    log.info("Interrupted.", var2);
                }
            } else {
                this.step();
            }
        }

        this.setStopped();
    }

    public void step() {
        this.withReadLock(this::executeStepsOnAgents);
        this.getActionService().processActions();
        ++this.step;
        this.notifyMonitorsForChangedProperties();
    }

    protected void executeStepsOnAgents() {
        SimpleWorkplace.this.agents.values().forEach(ISimpleAgent::step);
    }

    public boolean isRunning() {
        return State.RUNNING.equals(this.state);
    }

    public boolean isPaused() {
        return State.PAUSED.equals(this.state);
    }

    public boolean isStopped() {
        return State.STOPPED.equals(this.state);
    }

    protected long getStep() {
        return this.step;
    }

    protected void setRunning() {
        Object var1 = this.stateMonitor;
        synchronized(this.stateMonitor) {
            this.state = State.RUNNING;
        }
    }

    protected void setStopped() {
        log.info("{} stopped", this);
        Object var1 = this.stateMonitor;
        synchronized(this.stateMonitor) {
            this.state = State.STOPPED;
        }

        this.getWorkplaceEnvironment().onWorkplaceStop(this);
    }

    public void setWorkplaceEnvironment(IWorkplaceEnvironment workplaceEnvironment) {
        if(workplaceEnvironment == null) {
            this.workplaceEnvironment = null;
        } else {
            if(this.workplaceEnvironment != null) {
                throw new WorkplaceException(String.format("Environment in %s is already set.", new Object[]{this}));
            }

            this.workplaceEnvironment = workplaceEnvironment;
            this.getActionService().processActions();
            if(this.temporaryAgentsList != null) {
                this.addAll(this.temporaryAgentsList);
                this.temporaryAgentsList = null;
            }
        }

    }

    @Nullable
    protected final IWorkplaceEnvironment getWorkplaceEnvironment() {
        return this.workplaceEnvironment;
    }

    protected final boolean hasWorkplaceEnvironment() {
        return this.workplaceEnvironment != null;
    }
}
