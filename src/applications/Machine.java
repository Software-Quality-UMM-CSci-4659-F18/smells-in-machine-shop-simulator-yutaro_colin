package applications;

import dataStructures.LinkedQueue;

class Machine {
    // data members
    private LinkedQueue jobQ; // queue of waiting jobs for this machine
    private int changeTime; // machine change-over time
    private int totalWait; // total delay at this machine
    private int numTasks; // number of tasks processed on this machine
    private Job activeJob; // job currently active on this machine

    // constructor
    Machine() {
        jobQ = new LinkedQueue();
    }

    public LinkedQueue getJobQ() {
        return jobQ;
    }

    public int getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(int changeTime) {
        this.changeTime = changeTime;
    }

    public int getTotalWait() {
        return totalWait;
    }

    public void setTotalWait(int totalWait) {
        this.totalWait = totalWait;
    }

    public int getNumTasks() {
        return numTasks;
    }

    public void setNumTasks(int numTasks) {
        this.numTasks = numTasks;
    }

    public Job getActiveJob() {
        return activeJob;
    }

    public void setActiveJob(Job activeJob) {
        this.activeJob = activeJob;
    }

    /**
     * change the state of theMachine
     *
     * @return last job run on this machine
     */
    Job changeState(EventList eList, int theMachine) {// Task on theMachine has finished,
        // schedule next one.
        Job lastJob;
        if (getActiveJob() == null) {// in idle or change-over
            // state
            lastJob = null;
            // wait over, ready for new job
            if (getJobQ().isEmpty()) // no waiting job
                eList.setFinishTime(theMachine, MachineShopSimulator.largeTime);
            else {// take job off the queue and work on it
                MachineShopSimulator.machine[theMachine].setActiveJob((Job) MachineShopSimulator.machine[theMachine].getJobQ()
                        .remove());
                MachineShopSimulator.machine[theMachine].setTotalWait(MachineShopSimulator.machine[theMachine].getTotalWait() + MachineShopSimulator.timeNow
                        - MachineShopSimulator.machine[theMachine].getActiveJob().getArrivalTime());
                MachineShopSimulator.machine[theMachine].setNumTasks(MachineShopSimulator.machine[theMachine].getNumTasks() + 1);
                int t = MachineShopSimulator.machine[theMachine].getActiveJob().removeNextTask();
                MachineShopSimulator.eList.setFinishTime(theMachine, MachineShopSimulator.timeNow + t);
            }
        } else {// task has just finished on machine[theMachine]
            // schedule change-over time
            lastJob = MachineShopSimulator.machine[theMachine].getActiveJob();
            MachineShopSimulator.machine[theMachine].setActiveJob(null);
            MachineShopSimulator.eList.setFinishTime(theMachine, MachineShopSimulator.timeNow
                    + MachineShopSimulator.machine[theMachine].getChangeTime());
        }

        return lastJob;
    }
}
