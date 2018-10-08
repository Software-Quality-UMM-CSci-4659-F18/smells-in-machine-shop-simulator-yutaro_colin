/** machine shop simulation */

package applications;

public class MachineShopSimulator {
    
    public static final String NUMBER_OF_MACHINES_MUST_BE_AT_LEAST_1 = "number of machines must be >= 1";
    public static final String NUMBER_OF_MACHINES_AND_JOBS_MUST_BE_AT_LEAST_1 = "number of machines and jobs must be >= 1";
    public static final String CHANGE_OVER_TIME_MUST_BE_AT_LEAST_0 = "change-over time must be >= 0";
    public static final String EACH_JOB_MUST_HAVE_AT_LEAST_1_TASK = "each job must have >= 1 task";
    public static final String BAD_MACHINE_NUMBER_OR_TASK_TIME = "bad machine number or task time";

    // Data members of MachineShopSimulator
    static int numMachines; // number of machines
    static int timeNow; // current time
    static EventList eList; // pointer to event list
    static Machine[] machine; // array of machines
    static int largeTime; // all machines finish before this

    // methods
    /**
     * move theJob to machine for its next task
     * 
     * @return false iff no next task
     */
    static boolean moveToNextMachine(Job theJob, SimulationResults simulationResults) {
        if (theJob.getTaskQ().isEmpty()) {// no next task
            simulationResults.setJobCompletionData(theJob.getId(), timeNow, timeNow - theJob.getLength());
            return false;
        } else {// theJob has a next task
                // get machine for next task
            int p = theJob.getMachineNumber();
            // put on machine p's wait queue
            machine[p].getJobQ().put(theJob);
            theJob.setArrivalTime(timeNow);
            // if p idle, schedule immediately
            if (eList.nextEventTime(p) == largeTime) {// machine is idle
                machine[p].changeState(eList, p, timeNow);
            }
            return true;
        }
    }

    static void setMachineChangeOverTimes(SimulationSpecification specification) {
        for (int i = 1; i<=specification.getNumMachines(); ++i) {
            machine[i].setChangeTime(specification.getChangeOverTimes(i));
        }
    }

    static void createEventAndMachineQueues(SimulationSpecification specification) {
        // create event and machine queues
        eList = new EventList(specification.getNumMachines(), largeTime);
        machine = new Machine[specification.getNumMachines() + 1];
        for (int i = 1; i <= specification.getNumMachines(); i++)
            machine[i] = new Machine();
    }

    private static void setTotalWaitTimePerMachine(SimulationResults simulationResults) {
        int[] totalWaitTimePerMachine = new int[numMachines+1];
        for (int i=1; i<=numMachines; ++i) {
            totalWaitTimePerMachine[i] = machine[i].getTotalWait();
        }
        simulationResults.setTotalWaitTimePerMachine(totalWaitTimePerMachine);
    }

    private static void setNumTasksPerMachine(SimulationResults simulationResults) {
        int[] numTasksPerMachine = new int[numMachines+1];
        for (int i=1; i<=numMachines; ++i) {
            numTasksPerMachine[i] = machine[i].getNumTasks();
        }
        simulationResults.setNumTasksPerMachine(numTasksPerMachine);
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

    /** output wait times at machines
     * @param simulationResults*/
    static void outputStatistics(SimulationResults simulationResults) {
        simulationResults.setFinishTime(MachineShopSimulator.timeNow);
        simulationResults.setNumMachines(MachineShopSimulator.numMachines);
        setNumTasksPerMachine(simulationResults);
        setTotalWaitTimePerMachine(simulationResults);
    }

}
