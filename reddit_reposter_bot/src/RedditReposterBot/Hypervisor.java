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
package RedditReposterBot;

import RedditReposterBot.socialMediaHandler.SocialMediaPoster;
import RedditReposterBot.redditHandler.RedditExtractor;
import RedditReposterBot.exceptions.NoSuchFile;
import RedditReposterBot.exceptions.NoSuchOrder;
import RedditReposterBot.exceptions.NotSufficientRights;
import RedditReposterBot.exceptions.NoSuchProperty;
import RedditReposterBot.redditHandler.RedditPost;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Singleton Hypervisor. This is the object that does everything. So we want it
 * to be unique. That's why it's a singleton.
 *
 * @author louis
 */
public class Hypervisor {

    /**
     * Working folder.
     */
    private String workingDirectory;

    /**
     * SQLITE database for saving the already parsed posts.
     */
    private final String sqliteDatabase;

    /**
     * SQLITE table name.
     */
    private final String tableName;

    /**
     * Delay between two scans, in seconds.
     */
    private final int delay;

    /**
     * Minimum of all the social medias text size.
     */
    private final int maxLength;

    /**
     * Subreddit to extract info from.
     */
    private final String subreddit;

    /**
     * The singleton.
     */
    private static Hypervisor SINGLETON = null;

    /**
     * All the social medias to post our reddit content.
     */
    private final ArrayList<SocialMediaPoster> socialMedias;

    /**
     * Connection to the SQLITE database.
     */
    private Connection connexion;

    /**
     * RedditExtractor.
     */
    private final RedditExtractor myRedditExtractor;

    /**
     * Private constructor so nobody except this obect can build this object.
     */
    private Hypervisor()
            throws NotSufficientRights, ClassNotFoundException,
            SQLException, IOException, NoSuchFile, NoSuchProperty, NoSuchOrder {
        Class.forName("org.sqlite.JDBC");
        System.out.println("[+] Creating Hypervisor.");
        ConfigFileReader reader = new ConfigFileReader();
        this.subreddit = reader.getProperties("subreddit");
        this.tableName = reader.getProperties("subreddit");
        this.delay = Integer.valueOf(reader.getProperties("delay"));
        this.sqliteDatabase = reader.getProperties("sqlite_db_name");
        this.socialMedias = new ArrayList<>();
        this.workingDirectory = reader.getProperties("working_directory");
        setupTheBotDirectory();
        this.connexion = DriverManager.getConnection("jdbc:sqlite:"
                    + this.workingDirectory + File.separator
                    + this.sqliteDatabase);
        this.myRedditExtractor = new RedditExtractor(subreddit);
        if ("Y".equals(reader.getProperties("clear_database"))) {
            clearDatabase();
        }
        this.maxLength = Integer.valueOf(reader.getProperties("max_text_length"));
        load();
        this.connexion.close();
        System.out.println("[+] Hypervisor created successfully.");
    }

    /**
     * Get the Singleton Hypervisor instance.
     *
     * @return the instance.
     *
     * @throws java.lang.ClassNotFoundException
     * @throws TwitterTechSupportGoreBot.exceptions.NotSufficientRights
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws TwitterTechSupportGoreBot.exceptions.NoSuchFile
     * @throws TwitterTechSupportGoreBot.exceptions.NoSuchProperty
     * @throws TwitterTechSupportGoreBot.exceptions.NoSuchOrder
     */
    public static Hypervisor getSingleton()
            throws ClassNotFoundException, NotSufficientRights,
            SQLException, IOException, NoSuchFile, NoSuchProperty, NoSuchOrder {
        if (SINGLETON == null) {
            SINGLETON = new Hypervisor();
        }
        return SINGLETON;
    }

    /**
     * Main loop for the program. This is where everything happens.
     *
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.lang.InterruptedException
     */
    public void run()
            throws ClassNotFoundException, SQLException,
            InterruptedException {
        System.out.println("[+] Hypervisor is now running.");
        for (;;) {
            this.connexion = DriverManager.getConnection("jdbc:sqlite:"
                    + this.workingDirectory + File.separator
                    + this.sqliteDatabase);
            for (RedditPost post : myRedditExtractor.getRedditPosts()) {
                computeRedditPost(post);
            }
            this.connexion.close();
            System.out.println(
                    "[*] Hypervisor is waiting for "
                    + this.delay + " seconds.");
            Thread.sleep(this.delay * 1000);
        }
    }

    /**
     * Load all the reddit posts from the database.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void load() throws ClassNotFoundException, SQLException {
        createTable();
        PreparedStatement recherche = this.connexion.prepareStatement(
                "SELECT COUNT(id) AS cpt FROM " + this.tableName + ";");
        try (ResultSet res = recherche.executeQuery()) {
            res.next();
            System.out.println("[*] " + res.getInt("cpt") + " posts in database.");
        }
    }

    /**
     * Clear the database by dropping it.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void clearDatabase() throws ClassNotFoundException, SQLException {
        System.out.println("[*] Clearing the database.");
        PreparedStatement stmt = connexion.prepareStatement(""
                + "DROP TABLE IF EXISTS " + this.tableName + ";"
        );
        stmt.execute();
        createTable();
        System.out.println("[*] The database has been cleared successfully.");
    }

    /**
     * Add a given reddit post to the database.
     *
     * @param current a given reddit post to add to the database
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void addRedditPostToDatabase(RedditPost current)
            throws ClassNotFoundException, SQLException {
        createTable();
        if (!isInDatabase(current.getPostId())) {
            PreparedStatement ajout = this.connexion.prepareStatement(
                    "INSERT INTO " + this.tableName
                    + "("
                    + "postType, "
                    + "postId, "
                    + "title, "
                    + "quarantine, "
                    + "score, "
                    + "postHint, "
                    + "crosspostable, "
                    + "over18, "
                    + "author, "
                    + "permalink, "
                    + "spoiler, "
                    + "url, "
                    + "shared"
                    + ") "
                    + "VALUES "
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
            );
            ajout.setString(1,
                    current.isImage() ? "image"
                    : current.isLink() ? "link"
                    : current.isText() ? "text"
                    : "video");
            ajout.setString(2, current.getPostId());
            ajout.setString(3, current.getTitle());
            ajout.setBoolean(4, current.isQuarantine());
            ajout.setDouble(5, current.getScore());
            ajout.setString(6, current.getPostHint());
            ajout.setBoolean(7, current.isCrosspostable());
            ajout.setBoolean(8, current.isOver18());
            ajout.setString(9, current.getAuthor());
            ajout.setString(10, current.getPermalink());
            ajout.setBoolean(11, current.isSpoiler());
            ajout.setString(12, current.getUrl());
            ajout.setBoolean(13, true);
            ajout.execute();

        }
    }

    /**
     * Check if a post is in database.
     *
     * @param postId the post id
     * @return if the post is in the database
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private boolean isInDatabase(String postId)
            throws SQLException, ClassNotFoundException {
        PreparedStatement recherche = this.connexion.prepareStatement(
                "SELECT * FROM " + this.tableName + " "
                + "WHERE postId = '" + postId + "'");
        try (ResultSet resultats = recherche.executeQuery()) {
            return resultats.next();
        }
    }

    /**
     * Create our working table if it doesn't exists yet.
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void createTable() throws SQLException, ClassNotFoundException {
        PreparedStatement stmt = connexion.prepareStatement(""
                + "CREATE TABLE IF NOT EXISTS " + this.tableName + " "
                + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "postType TEXT, "
                + "postId TEXT UNIQUE, "
                + "title TEXT, "
                + "quarantine BOOLEAN, "
                + "score DOUBLE, "
                + "postHint TEXT, "
                + "crosspostable BOOLEAN, "
                + "over18 BOOLEAN, "
                + "author TEXT, "
                + "permalink TEXT, "
                + "spoiler BOOLEAN, "
                + "url TEXT, "
                + "shared BOOLEAN"
                + ");"
        );
        stmt.execute();
    }

    /**
     * Compute a given reddit post.
     *
     * @param r the reddit post to compute.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void computeRedditPost(RedditPost r)
            throws SQLException, ClassNotFoundException {
        if (!isInDatabase(r.getPostId()) && !r.isQuarantine() && r.hasMediaUrl()) {
            System.out.println(
                    "[*] Computing the post \"" + r.getTitle() + "\"");
            addRedditPostToDatabase(r);
            String fileName = saveImage(r.getUrl());
            socialMedias.forEach((s) -> {
                long postRef = s.postImage(
                        formatPost(r.getTitle()), fileName);
                if (postRef != 0 && postRef != -1) {
                    s.replyText(formatPost("from /u/" + r.getAuthor() + " "
                            + "on /r/" + this.subreddit + " "
                            + "at link : https://www.reddit.com"
                            + r.getPermalink()), postRef);
                }
            });
            deleteFile(fileName);
            System.out.println("[+] Post \""
                    + r.getTitle()
                    + "\" has been shared successfully.");
        }
    }

    /**
     * Format a given text for being posted on the different social networks.
     *
     * @param text the text to format
     * @return the formatted text
     */
    private String formatPost(String text) {
        return text.length() >= this.maxLength
                ? text.substring(0, this.maxLength - 4) + "..."
                : text;
    }

    /**
     * Save Image from URL. Modified version of the code that can be found at
     * https://www.programcreek.com/2012/12/download-image-from-url-in-java/
     *
     * @param imageUrl the image URL.
     */
    private String saveImage(String imageUrl) {
        System.out.println("[+] Dowloading image " + imageUrl + ".");
        try {
            URL url = new URL(imageUrl);
            String fileName = url.getFile();
            String destName = workingDirectory + fileName.substring(
                    fileName.lastIndexOf("/"), fileName.lastIndexOf("?"));
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(destName);

            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();

            System.out.println(
                    "[+] Image " + destName + " dowloaded successfully.");
            return destName;
        } catch (IOException ex) {
            System.err.println("[!] IOException : " + ex.getMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Delete a file.
     *
     * @param filePath the path to the file we want to delete.
     */
    private void deleteFile(String filePath) {
        File f = new File(filePath);
        f.delete();
        System.out.println("[*] File " + filePath + " deleted.");
    }

    /**
     * Set working directory.
     *
     * @param tempDir the path to the working directory.
     *
     * @throws TwitterTechSupportGoreBot.exceptions.NotSufficientRights
     */
    public void setWorkDir(String tempDir) throws NotSufficientRights {
        this.workingDirectory = tempDir;
        setupTheBotDirectory();
    }

    /**
     * Get the working directory.
     *
     * @return the working directory path.
     */
    public String getWorkDir() {
        return workingDirectory;
    }

    /**
     * Setup the bot.
     *
     * @throws NotSufficientRights
     */
    private void setupTheBotDirectory() throws NotSufficientRights {
        File f = new File(workingDirectory);
        f.mkdir();
        if (!f.canRead() || !f.canWrite()) {
            throw new NotSufficientRights(
                    "[!] This program does not have the sufficient "
                    + "rights on the folder \"" + workingDirectory + "\".");
        }
    }

    /**
     * Add a social media to post content to.
     *
     * @param s the social media
     */
    public void addSocialMedia(SocialMediaPoster s) {
        this.socialMedias.add(s);
    }
}
