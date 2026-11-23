package de.lpelster.devicesimulation;

import de.lpelster.devicesimulation.control.SimulationService;

public class Main {

    public static void main(String[] args) {
        SimulationService simulation = new SimulationService();
        simulation.start();
    }


}
