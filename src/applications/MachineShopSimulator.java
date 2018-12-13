/** machine shop simulation */

package applications;

public class MachineShopSimulator {
    
    public static final String NUMBER_OF_MACHINES_MUST_BE_AT_LEAST_1 = "number of machines must be >= 1";
    public static final String NUMBER_OF_MACHINES_AND_JOBS_MUST_BE_AT_LEAST_1 = "number of machines and jobs must be >= 1";
    public static final String CHANGE_OVER_TIME_MUST_BE_AT_LEAST_0 = "change-over time must be >= 0";
    public static final String EACH_JOB_MUST_HAVE_AT_LEAST_1_TASK = "each job must have >= 1 task";
    public static final String BAD_MACHINE_NUMBER_OR_TASK_TIME = "bad machine number or task time";

    // Data members of MachineShopSimulator
    public static int numMachines; // number of machines
    public static int timeNow; // current time
    public static EventList eList; // pointer to event list
    public static Machine[] machine; // array of machines
    public static int largeTime; // all machines finish before this

    // Get and Set methods
    static int getNumMachines() { return numMachines;}

    static int getTimeNow() { return timeNow; }

    static EventList getEventList() { return eList; }

    static Machine getMachine(int machineNumber) { return machine[machineNumber]; }

    static int getLargeTime() { return largeTime; }

    static void setTimeNow(int nextEventTime) { timeNow = nextEventTime; }

    static void setLargeTime(int theTime) { largeTime = theTime; }

    static void setNumMachines(int numberOfMachines) { numMachines = numberOfMachines; }

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
            int machineNumber = theJob.getMachineNumber();
            // put on this machine's wait queue
            machine[machineNumber].addJob(theJob);
            theJob.setArrivalTime(timeNow);
            // if this machine is idle, schedule immediately
            if (eList.nextEventTime(machineNumber) == largeTime) {// machine is idle
                machine[machineNumber].changeState(eList, machineNumber, timeNow);
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
        // i represents a jobNumber
        for (int i = 1; i <= specification.getNumJobs(); i++) {
            int taskNum = specification.getNumTasksInJob(i);
            int firstMachine = 0; // machine for first task

            // create the job
            theJob = new Job(i);
            // j represents a taskNumber
            for (int j = 1; j <= taskNum; j++) {
                int machine = specification.getMachineForJobTask(i,j);
                int taskTime = specification.getTimeForJobTask(i,j);
                if (j == 1)
                    firstMachine = machine; // job's first machine
                theJob.addTask(machine, taskTime); // add to
            } // task queue
            machine[firstMachine].addJob(theJob);
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
