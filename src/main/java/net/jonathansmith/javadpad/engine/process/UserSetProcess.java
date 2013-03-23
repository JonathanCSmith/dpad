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

package net.jonathansmith.javadpad.engine.process;

import net.jonathansmith.javadpad.engine.thread.DPADLocalEngine;

/**
 * UserRuntime
 *
 * @author Jonathan Smith
 */
public class UserSetProcess extends RuntimeProcess {

    boolean shutdownFlag = false;
    
    public UserSetProcess(DPADLocalEngine engine) {
        super(engine);
    }
    
    @Override
    public void init() {
        
    }

    @SuppressWarnings({"SleepWhileInLoop", "CallToThreadDumpStack"})
    @Override
    public void run() {
        while (!shutdownFlag) {
            try {
                Thread.sleep(100);
                
            } catch (Throwable t) {
                this.engine.logger.severe("Interrupted!");
                t.printStackTrace();
            }
        }
    }

    @Override
    public void forceShutdown(boolean error) {
        this.engine.logger.info("Called force shutdown in user thread...");
        this.shutdownFlag = true;
        super.end(error);
    }
}
