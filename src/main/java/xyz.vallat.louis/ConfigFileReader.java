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

import xyz.vallat.louis.exceptions.NoSuchFile;
import xyz.vallat.louis.exceptions.NotSufficientRights;
import xyz.vallat.louis.exceptions.NoSuchProperty;
import java.io.File;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A config file reader.
 *
 * @author louis
 */
public final class ConfigFileReader {

    /**
     * The config file name.
     */
    private static String CONFIGFILE = "data/settings.conf";

    /**
     * The properties object.
     */
    private final Properties prop = new Properties();

    /**
     * Create a new config file reader that automatically reads the config file.
     *
     * @throws NoSuchFile
     * @throws NotSufficientRights
     */
    public ConfigFileReader() throws NoSuchFile, NotSufficientRights {
        readConfigFile();
    }

    /**
     * Create a new config file reader that automatically reads the config file,
     * precising the config file name.
     *
     * @param conf the cnfig file path name.
     * @throws NoSuchFile
     * @throws NotSufficientRights
     */
    public ConfigFileReader(String conf) throws NoSuchFile, NotSufficientRights {
        CONFIGFILE = conf;
        readConfigFile();
    }

    /**
     * Read the config file.
     *
     * @throws RedditReposterBot.exceptions.NoSuchFile
     * @throws RedditReposterBot.exceptions.NotSufficientRights
     */
    public void readConfigFile() throws NoSuchFile, NotSufficientRights {
        if (!new File(CONFIGFILE).exists()) {
            throw new NoSuchFile(
                    "[!] The config file " + CONFIGFILE + " doesn't exists.");
        } else if (!new File(CONFIGFILE).canRead()) {
            throw new NotSufficientRights(
                    "[!] Can't read the config file " + CONFIGFILE + ".");
        }
        try {
            prop.load(new FileInputStream(new File(CONFIGFILE)));
        } catch (IOException ex) {
            System.out.println(
                    "[!] Error on loading the config file.");
            System.out.println("[!] " + ex.getMessage());
        }

    }

    /**
     * Get the property for an id.
     *
     * @param id the property id.
     * @return the properties.
     * @throws RedditReposterBot.exceptions.NoSuchProperty
     */
    public String getProperties(String id) throws NoSuchProperty {
        if (!this.prop.containsKey(id)) {
            throw new NoSuchProperty(
                    "[!] The property " + id + " is not defined in the config "
                    + "file. Define it in " + CONFIGFILE + " and try again.");
        }
        return this.prop.getProperty(id);
    }
}
