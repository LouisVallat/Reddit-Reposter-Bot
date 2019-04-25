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
package TwitterTechSupportGoreBot;

import TwitterTechSupportGoreBot.redditHandler.RedditPostImage;
import TwitterTechSupportGoreBot.redditHandler.RedditPost;
import TwitterTechSupportGoreBot.redditHandler.RedditPostText;
import TwitterTechSupportGoreBot.redditHandler.RedditPostLink;
import TwitterTechSupportGoreBot.redditHandler.RedditPostVideo;
import TwitterTechSupportGoreBot.socialMediaHandler.*;
import TwitterTechSupportGoreBot.redditHandler.*;
import TwitterTechSupportGoreBot.exceptions.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
     * All the reddit posts parsed.
     */
    private final HashMap<String, RedditPost> redditPosts;

    /**
     * All the already shared posts, so there are no boule shares.
     */
    private final HashMap<String, RedditPost> alreadySharedPosts;

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
    private final Connection connexion;

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
        this.redditPosts = new HashMap<>();
        this.workingDirectory = reader.getProperties("working_directory");
        setupTheBotDirectory();
        this.connexion = DriverManager.getConnection("jdbc:sqlite:"
                + this.workingDirectory + File.separator + this.sqliteDatabase);
        this.myRedditExtractor = new RedditExtractor(subreddit);
        this.alreadySharedPosts = new HashMap<>();
        if ("Y".equals(reader.getProperties("clear_database"))) {
            clearDatabase();
        }
        this.maxLength = Integer.valueOf(reader.getProperties("max_text_length"));
        load();
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
            for (RedditPost post : myRedditExtractor.getRedditPosts()) {
                computeRedditPost(post);
            }
            save();
            System.out.println(
                    "[*] Hypervisor is waiting for " + this.delay + "seconds.");
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
        System.out.println("[*] Loading the Reddit posts from database.");
        createTable();
        PreparedStatement recherche = this.connexion.prepareStatement(
                "SELECT * FROM " + this.tableName + ";");
        try (ResultSet res = recherche.executeQuery()) {
            while (res.next()) {
                RedditPost post
                        = "image".equals(res.getString("postType"))
                        ? new RedditPostImage(res.getString("postId"),
                                res.getString("title"),
                                res.getBoolean("quarantine"),
                                res.getDouble("score"),
                                res.getString("postHint"),
                                res.getBoolean("crosspostable"),
                                res.getBoolean("over18"),
                                res.getString("author"),
                                res.getString("permalink"),
                                res.getBoolean("spoiler"),
                                res.getString("url"))
                        : "link".equals(res.getString("postType"))
                        ? new RedditPostLink(res.getString("postId"),
                                res.getString("title"),
                                res.getBoolean("quarantine"),
                                res.getDouble("score"),
                                res.getString("postHint"),
                                res.getBoolean("crosspostable"),
                                res.getBoolean("over18"),
                                res.getString("author"),
                                res.getString("permalink"),
                                res.getBoolean("spoiler"),
                                res.getString("url"))
                        : "video".equals(res.getString("postType"))
                        ? new RedditPostVideo(res.getString("postId"),
                                res.getString("title"),
                                res.getBoolean("quarantine"),
                                res.getDouble("score"),
                                res.getString("postHint"),
                                res.getBoolean("crosspostable"),
                                res.getBoolean("over18"),
                                res.getString("author"),
                                res.getString("permalink"),
                                res.getBoolean("spoiler"),
                                res.getString("url"))
                        : new RedditPostText(res.getString("postId"),
                                res.getString("title"),
                                res.getBoolean("quarantine"),
                                res.getDouble("score"),
                                res.getString("postHint"),
                                res.getBoolean("crosspostable"),
                                res.getBoolean("over18"),
                                res.getString("author"),
                                res.getString("permalink"),
                                res.getBoolean("spoiler"),
                                res.getString("url"));
                this.redditPosts.put(post.getPostId(), post);
                if (res.getBoolean("shared")) {
                    this.alreadySharedPosts.put(post.getPostId(), post);
                }
            }
        }
        System.out.println("[*] Loading done. "
                + this.redditPosts.size() + " posts loaded. "
                + this.alreadySharedPosts.size() + " posts already shared.");
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
     * Save all the reddit posts to the database.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void save() throws ClassNotFoundException, SQLException {
        createTable();
        for (String postId : this.redditPosts.keySet()) {
            RedditPost current = this.redditPosts.get(postId);
            if (!isInDatabase(postId)) {
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
                ajout.setBoolean(13, this.alreadySharedPosts
                        .containsKey(current.getPostId()));
                ajout.execute();
            }
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
     * Set a post as shared onsocial networks.
     *
     * @param postId the post id
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void setPostAsShared(String postId)
            throws SQLException, ClassNotFoundException {
        System.out.println("[+] Post \""
                + this.redditPosts.get(postId).getTitle()
                + "\" has been shared successfully.");
        this.alreadySharedPosts.put(postId, this.redditPosts.get(postId));
        if (isInDatabase(postId)) {
            PreparedStatement update = this.connexion.prepareStatement(
                    "UPDATE " + this.tableName + " "
                    + "SET shared=? "
                    + "WHERE postId = '" + postId + "';"
            );
            update.setBoolean(1, true);
            update.execute();
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
        if (!this.alreadySharedPosts.containsKey(r.getPostId())
                && !r.isQuarantine()) {
            System.out.println(
                    "[*] Computing the post \"" + r.getTitle() + "\"");
            this.redditPosts.put(r.getPostId(), r);
            if (r.hasMediaUrl()) {
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
            }
            setPostAsShared(r.getPostId());
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
