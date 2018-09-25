package applications;

public class JobManager {

    static int numJobs; // number of jobs

    /**
     * change the state of theMachine
     *
     * @return last job run on this machine
     */
    static Job changeState(int theMachine) {// Task on theMachine has finished,
                                            // schedule next one.
        Job lastJob;
        if (MachineShopSimulator.machine[theMachine].getActiveJob() == null) {// in idle or change-over
                                                    // state
            lastJob = null;
            // wait over, ready for new job
            if (MachineShopSimulator.machine[theMachine].getJobQ().isEmpty()) // no waiting job
                MachineShopSimulator.eList.setFinishTime(theMachine, MachineShopSimulator.largeTime);
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

    static void setUpJobs(SimulationSpecification specification) {
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
            MachineShopSimulator.machine[firstMachine].getJobQ().put(theJob);
        }
    }
}
