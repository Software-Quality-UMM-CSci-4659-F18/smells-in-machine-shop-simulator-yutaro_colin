package applications;

public class JobHandler {
    // data members of MachineShopSimulator
    protected static int timeNow; // current time
    protected static EventList eList; // pointer to event list
    protected static Machine[] machine; // array of machines
    protected static int largeTime; // all machines finish before this

    /**
     * change the state of theMachine
     *
     * @return last job run on this machine
     */
    static Job changeState(int theMachine) {// Task on theMachine has finished,
                                            // schedule next one.
        Job lastJob;
        if (machine[theMachine].getActiveJob() == null) {// in idle or change-over
                                                    // state
            lastJob = null;
            // wait over, ready for new job
            if (machine[theMachine].getJobQ().isEmpty()) // no waiting job
                eList.setFinishTime(theMachine, largeTime);
            else {// take job off the queue and work on it
                machine[theMachine].setActiveJob((Job) machine[theMachine].getJobQ()
                        .remove());
                machine[theMachine].setTotalWait(machine[theMachine].getTotalWait() + timeNow
                        - machine[theMachine].getActiveJob().getArrivalTime());
                machine[theMachine].setNumTasks(machine[theMachine].getNumTasks() + 1);
                int t = machine[theMachine].getActiveJob().removeNextTask();
                eList.setFinishTime(theMachine, timeNow + t);
            }
        } else {// task has just finished on machine[theMachine]
                // schedule change-over time
            lastJob = machine[theMachine].getActiveJob();
            machine[theMachine].setActiveJob(null);
            eList.setFinishTime(theMachine, timeNow
                    + machine[theMachine].getChangeTime());
        }

        return lastJob;
    }

    protected static void setUpJobs(SimulationSpecification specification) {
        // input the jobs
        Job theJob;
        for (int i = 1; i <= specification.getNumJobs(); i++) {
            int tasks = specification.getJobSpecifications(i).getNumTasks();
            int firstMachine = 0; // machine for first task

            // create the job
            theJob = new Job(i);
            for (int j = 1; j <= tasks; j++) {
                int theMachine = specification.getJobSpecifications(i).getSpecificationsForTasks()[2*(j-1)+1];
                int theTaskTime = specification.getJobSpecifications(i).getSpecificationsForTasks()[2*(j-1)+2];
                if (j == 1)
                    firstMachine = theMachine; // job's first machine
                theJob.addTask(theMachine, theTaskTime); // add to
            } // task queue
            machine[firstMachine].getJobQ().put(theJob);
        }
    }
}
