package applications;

public class JobManager {

    static int numJobs; // number of jobs



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
