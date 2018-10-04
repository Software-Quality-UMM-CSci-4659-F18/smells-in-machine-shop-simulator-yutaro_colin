package applications;

public class RunMachineShop {

    /** process all jobs to completion
     * @param simulationResults*/
    static void simulate(SimulationResults simulationResults) {
        while (JobManager.numJobs > 0) {// at least one job left
            int nextToFinish = MachineShopSimulator.eList.nextEventMachine();
            MachineShopSimulator.timeNow = MachineShopSimulator.eList.nextEventTime(nextToFinish);
            // change job on machine nextToFinish
            Job theJob = MachineShopSimulator.machine[nextToFinish].changeState(MachineShopSimulator.eList, nextToFinish);
            // move theJob to its next machine
            // decrement numJobs if theJob has finished
            if (theJob != null && !MachineShopSimulator.moveToNextMachine(theJob, simulationResults))
                JobManager.numJobs--;
        }
    }

    public static SimulationResults runSimulation(SimulationSpecification specification) {
        MachineShopSimulator.largeTime = Integer.MAX_VALUE;
        MachineShopSimulator.timeNow = 0;
        startShop(specification); // initial machine loading
        SimulationResults simulationResults = new SimulationResults(JobManager.numJobs);
        simulate(simulationResults); // run all jobs through shop
        outputStatistics(simulationResults);
        return simulationResults;
    }

    /** output wait times at machines
     * @param simulationResults*/
    static void outputStatistics(SimulationResults simulationResults) {
        simulationResults.setFinishTime(MachineShopSimulator.timeNow);
        simulationResults.setNumMachines(MachineShopSimulator.numMachines);
        MachineShopSimulator.setNumTasksPerMachine(simulationResults);
        MachineShopSimulator.setTotalWaitTimePerMachine(simulationResults);
    }

    /** load first jobs onto each machine
     * @param specification*/
    static void startShop(SimulationSpecification specification) {
        // Move this to startShop when ready
        MachineShopSimulator.numMachines = specification.getNumMachines();
        JobManager.numJobs = specification.getNumJobs();
        MachineShopSimulator.createEventAndMachineQueues(specification);

        // Move this to startShop when ready
        MachineShopSimulator.setMachineChangeOverTimes(specification);

        // Move this to startShop when ready
        JobManager.setUpJobs(specification);

        for (int p = 1; p <= MachineShopSimulator.numMachines; p++)
            MachineShopSimulator.machine[p].changeState(MachineShopSimulator.eList, p);
    }

    /** entry point for machine shop simulator */
    public static void main(String[] args) {
        /*
         * It's vital that we (re)set this to 0 because if the simulator is called
         * multiple times (as happens in the acceptance tests), because timeNow
         * is static it ends up carrying over from the last time it was run. I'm
         * not convinced this is the best place for this to happen, though.
         */
        final SpecificationReader specificationReader = new SpecificationReader();
        SimulationSpecification specification = specificationReader.readSpecification();
        SimulationResults simulationResults = runSimulation(specification);
        simulationResults.print();
    }
}
