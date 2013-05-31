/*
 * Copyright (C) 2013 jonathansmith
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
package net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.decoder;

import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.decoder.DecodeResult.Type;

/**
 *
 * @author jonathansmith
 */
public class ContinueDecodeResult<T extends Enum<T>> implements DecodeResult<T> {
    
    private final T nextState;
    
    public ContinueDecodeResult(T nextState) {
        this.nextState = nextState;
    }
    
    @Override
    public Type getType() {
        return Type.CONTINUE;
    }
    
    public T getNextState() {
        return this.nextState;
    }
}
