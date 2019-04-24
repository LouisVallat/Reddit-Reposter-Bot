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
package twitter_techsupportgore_bot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Singleton Hypervisor. This is the object that does everything. So we want it
 * to be unique. That's why it's a singleton.
 *
 * @author louis
 */
public class Hypervisor {

    /**
     * Private constructor so nobody except this obect can build this object.
     */
    private Hypervisor() {
    }

    /**
     * The singleton.
     */
    private static Hypervisor SINGLETON = null;

    /**
     * Get the Singleton Hypervisor instance.
     *
     * @return the instance.
     */
    public static Hypervisor getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new Hypervisor();
        }
        return SINGLETON;
    }

    /**
     * Save Image from URL. Modified version of the code that can be found atF
     * https://www.programcreek.com/2012/12/download-image-from-url-in-java/
     *
     * @param imageUrl the image URL.
     * @throws IOException
     */
    public static void saveImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        String fileName = url.getFile();
        String destName = "../downloads" + fileName.substring(fileName.lastIndexOf("/"), fileName.lastIndexOf("?"));

        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destName);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

}
