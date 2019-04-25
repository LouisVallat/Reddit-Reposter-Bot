/*
 * Copyright (C) 2019 louis
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
package TwitterTechSupportGoreBot.exceptions;

/**
 * Exception throwable when there are no property in the config file for a given
 * setting name/id.
 *
 * @author louis
 */
public class NoSuchProperty extends Exception {

    /**
     * Creates a new instance of <code>NoSuchProperty</code> without detail
     * message.
     */
    public NoSuchProperty() {
    }

    /**
     * Constructs an instance of <code>NoSuchProperty</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchProperty(String msg) {
        super(msg);
    }
}
