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
package xyz.vallat.louis;

import xyz.vallat.louis.socialMediaHandler.TwitterBot;
import xyz.vallat.louis.exceptions.NoSuchFile;
import xyz.vallat.louis.exceptions.NoSuchOrder;
import xyz.vallat.louis.exceptions.NoSuchProperty;
import xyz.vallat.louis.exceptions.NotSufficientRights;
import java.io.IOException;
import java.sql.SQLException;

/**
 * This is where everything begins.
 *
 * @author louis
 */
public class RedditReposterBot {

    /**
     * Version of the application.
     */
    private static final String VERSION = "1.0.1";
    
    /**
     * Launch the Hypervisor.
     *
     * @param args command line arguments
     *
     * @throws ClassNotFoundException
     * @throws NotSufficientRights
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     * @throws NoSuchFile
     * @throws NoSuchProperty
     * @throws NoSuchOrder
     */
    public static void main(String[] args) throws
            ClassNotFoundException,
            NotSufficientRights,
            SQLException,
            IOException,
            InterruptedException,
            NoSuchFile,
            NoSuchProperty,
            NoSuchOrder {

        System.out.println("[*] App version " + VERSION);
        Hypervisor master = Hypervisor.getSingleton();
        master.addSocialMedia(new TwitterBot());
        master.run();
    }
}
