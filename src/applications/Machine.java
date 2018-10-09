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

    public int getNumTasks() {
        return numTasks;
    }


    void processJob(EventList eList, int theMachine, int timeNow) {
        activeJob = (Job)jobQ.remove();
        totalWait = totalWait + timeNow - activeJob.getArrivalTime();
        numTasks++;
        int t = activeJob.removeNextTask();
        eList.setFinishTime(theMachine, timeNow + t);
    }

    /**
     * change the state of theMachine
     *
     * @return last job run on this machine
     */
    Job changeState(EventList eList, int theMachine, int timeNow) {// Task on theMachine has finished,
        // schedule next one.
        Job lastJob;
        if (activeJob == null) {// in idle or change-over state
            lastJob = null;

            // wait over, ready for new job
            if (jobQ.isEmpty())  // no waiting job
                eList.setFinishTime(theMachine, MachineShopSimulator.getLargeTime());

            else { processJob(eList, theMachine, timeNow); } // take job off the queue and work on it

        } else {   // task has just finished on machine[theMachine]
            lastJob = activeJob;
            activeJob = null;
            eList.setFinishTime(theMachine, timeNow + getChangeTime()); // Schedule change-over time
        }

        return lastJob;
    }

    public void addJob(Job theJob) {
        getJobQ().put(theJob);
    }
}
