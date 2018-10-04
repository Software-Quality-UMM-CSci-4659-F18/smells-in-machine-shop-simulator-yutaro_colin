package applications;

public class RunMachineShop {
    static int numJobs; // number of jobs

    /** process all jobs to completion
     * @param simulationResults*/
    static void simulate(SimulationResults simulationResults) {
        while (numJobs > 0) {// at least one job left
            int nextToFinish = MachineShopSimulator.eList.nextEventMachine();
            MachineShopSimulator.timeNow = MachineShopSimulator.eList.nextEventTime(nextToFinish);
            // change job on machine nextToFinish
            Job theJob = MachineShopSimulator.machine[nextToFinish].changeState(MachineShopSimulator.eList, nextToFinish, MachineShopSimulator.timeNow);
            // move theJob to its next machine
            // decrement numJobs if theJob has finished
            if (theJob != null && !MachineShopSimulator.moveToNextMachine(theJob, simulationResults))
                numJobs--;
        }
    }

    public static SimulationResults runSimulation(SimulationSpecification specification) {
        MachineShopSimulator.largeTime = Integer.MAX_VALUE;
        MachineShopSimulator.timeNow = 0;
        startShop(specification); // initial machine loading
        SimulationResults simulationResults = new SimulationResults(numJobs);
        simulate(simulationResults); // run all jobs through shop
        MachineShopSimulator.outputStatistics(simulationResults);
        return simulationResults;
    }

    /** load first jobs onto each machine
     * @param specification*/
    static void startShop(SimulationSpecification specification) {
        // Move this to startShop when ready
        MachineShopSimulator.numMachines = specification.getNumMachines();
        numJobs = specification.getNumJobs();
        MachineShopSimulator.createEventAndMachineQueues(specification);

        // Move this to startShop when ready
        MachineShopSimulator.setMachineChangeOverTimes(specification);

        // Move this to startShop when ready
        MachineShopSimulator.setUpJobs(specification);

        for (int p = 1; p <= MachineShopSimulator.numMachines; p++)
            MachineShopSimulator.machine[p].changeState(MachineShopSimulator.eList, p, MachineShopSimulator.timeNow);
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
