/*
 * Copyright (C) 2013 Jonathan Smith
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jonathansmith.javadpad;

import net.jonathansmith.javadpad.controller.DPADController;

/**
 *
 * @author Jonathan Smith
 */
public class DPAD extends Thread {
    
    /**
     * @param args the command line arguments
     */
    @SuppressWarnings({"CallToThreadDumpStack", "SleepWhileInLoop"})
    public static void main(String[] args) {
        System.out.println("Starting DPAD");
        DPADController controller = new DPADController();
        controller.init();
        
        if (!controller.errored && controller.initialised) {
            controller.start();
            
            try {
                controller.join();
            } catch (InterruptedException ex) {
                controller.logger.severe("Runtime interruption, DPAD shutting down");
            }
            
            if (controller.errored) {
                controller.logger.severe("Runtime failure, DPAD shutting down");
            }
            
        } else {
            controller.logger.severe("Failed to setup runtime environment");
        }
        
        Runtime.getRuntime().halt(1);
    }
}
