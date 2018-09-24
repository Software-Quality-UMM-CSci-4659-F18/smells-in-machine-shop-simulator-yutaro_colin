package applications;

public class RunMachineShop extends MachineShopSimulator {

    /** process all jobs to completion
     * @param simulationResults*/
    static void simulate(SimulationResults simulationResults) {
        while (JobHandler.numJobs > 0) {// at least one job left
            int nextToFinish = eList.nextEventMachine();
            timeNow = eList.nextEventTime(nextToFinish);
            // change job on machine nextToFinish
            Job theJob = JobHandler.changeState(nextToFinish);
            // move theJob to its next machine
            // decrement numJobs if theJob has finished
            if (theJob != null && !MachineShopSimulator.moveToNextMachine(theJob, simulationResults))
                JobHandler.numJobs--;
        }
    }

    public static SimulationResults runSimulation(SimulationSpecification specification) {
        largeTime = Integer.MAX_VALUE;
        timeNow = 0;
        MachineShopSimulator.startShop(specification); // initial machine loading
        SimulationResults simulationResults = new SimulationResults(JobHandler.numJobs);
        simulate(simulationResults); // run all jobs through shop
        MachineShopSimulator.outputStatistics(simulationResults);
        return simulationResults;
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
