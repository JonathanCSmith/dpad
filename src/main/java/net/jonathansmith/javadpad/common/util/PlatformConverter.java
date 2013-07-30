/*
 * Copyright (C) 2013 Jon
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
package net.jonathansmith.javadpad.common.util;

import net.jonathansmith.javadpad.api.Platform;

import com.beust.jcommander.IStringConverter;

/**
 *
 * @author Jon
 */
public class PlatformConverter implements IStringConverter<Platform> {

    public Platform convert(String string) {
        return Enum.valueOf(Platform.class, string.toUpperCase());
    }
}
