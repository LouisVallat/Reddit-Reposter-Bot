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
package xyz.vallat.louis.redditHandler;

import com.google.gson.*;
import xyz.vallat.louis.exceptions.NoSuchFile;
import xyz.vallat.louis.exceptions.NoSuchOrder;
import xyz.vallat.louis.exceptions.NoSuchProperty;
import xyz.vallat.louis.exceptions.NotSufficientRights;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

/**
 * Reddit extractor object.
 *
 * @author louis
 */
public final class RedditExtractor {

    /**
     * Subreddit to extract infos from.
     */
    private final SubReddit sub;

    /**
     * Main Constructor.
     *
     * @param subreddit Subreddit name. Just after /r/
     *
     * @throws IOException
     * @throws NoSuchProperty
     * @throws NoSuchFile
     * @throws NotSufficientRights
     * @throws NoSuchOrder
     */
    public RedditExtractor(String subreddit)
            throws IOException, NoSuchProperty, NoSuchFile,
            NotSufficientRights, NoSuchOrder {
        if (!doesSubredditExists(subreddit)) {
            throw new MalformedURLException("This subreddit ("
                    + subreddit + ") does not exist.");
        } else {
            this.sub = new SubReddit(subreddit);
        }
    }

    /**
     * Check if a subreddit exists.
     *
     * @param subredditName
     * @return if a subreddit exists
     */
    public boolean doesSubredditExists(String subredditName) {
        System.out.println("[*] Checking if subreddit /r/" + subredditName
                + " exists.");
        return ((JsonParser.parseString(getJsonFromURL(""
                        + "https://www.reddit.com/api/search_reddit_names.json"
                        + "?query=" + subredditName + "&exact=true")).getAsJsonObject()
                .get("names").getAsJsonArray()
                .size()) >= 1);
    }

    /**
     * Obtain the subreddit JSON response.
     *
     * @return the JSON from the REDDIT API.
     */
    public String getSubredditJson() {
        return getJsonFromURL(this.sub.getJsonURL());
    }

    /**
     * Get JSON from URL.
     *
     * @param URL the JSON url.
     * @return the JSON data as a String from the given URL.
     */
    public String getJsonFromURL(String URL) {
        try {
            System.out.println("[+] Obtaining JSON from URL " + URL + ".");
            HttpURLConnection con;
            URL myurl = new URL(URL);
            con = (HttpURLConnection) myurl.openConnection();
            try {
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; "
                        + "Windows NT 5.1; en-US; rv:1.8.0.11) ");
                StringBuilder response;
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {
                    String line;
                    response = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                        response.append(System.lineSeparator());
                    }
                    return response.toString();
                }
            } finally {
                con.disconnect();
            }
        } catch (IOException e) {
            System.err.println("[!] IOException: " + e.getMessage());
            System.out.println("[!] Retrying...");
            return getJsonFromURL(URL);
        }
    }

    /**
     * Get Reddit's subreddit posts.
     *
     * @return a treeset of all the reddit posts parsed.
     */
    public HashSet<RedditPost> getRedditPosts() {
        try {
            HashSet<RedditPost> set = new HashSet<>();
            String jsonResponse = getSubredditJson();
            JsonObject objet = JsonParser.parseString(jsonResponse)
                    .getAsJsonObject();
            JsonObject data = JsonParser.parseString(objet.get("data")
                    .toString()).getAsJsonObject();
            JsonArray children = JsonParser.parseString(data.get("children")
                    .toString()).getAsJsonArray();
            for (int i = 0; i < children.size(); i++) {
                try {
                    JsonObject child = JsonParser.parseString(children.get(i)
                            .toString()).getAsJsonObject();
                    JsonObject childData = JsonParser.parseString(child.get("data")
                            .toString()).getAsJsonObject();
                    if (childData.get("id").toString() != null
                            && !childData.get("quarantine").getAsBoolean()
                            && childData.get("url").getAsString() != null) {
                        String id = childData.get("id").toString();
                        String title = childData.get("title") != null
                                ? childData.get("title").toString()
                                : this.sub.getName();
                        title = title
                                .replace("\"", "")
                                .replace("\\", "\"")
                                .replace("&lt;", "<")
                                .replace("&gt;", ">")
                                .replace("&amp;", "&");
                        String author = childData.get("author") != null
                                ? childData.get("author").toString()
                                        .replace("&lt;", "<")
                                        .replace("&gt;", ">")
                                        .replace("&amp;", "&") : "anonymous";
                        author = author.replace("\"", "");
                        boolean quarantine = childData.get("quarantine")
                                .getAsBoolean();
                        double score = childData.get("score").getAsDouble();
                        String postHint = childData.get("post_hint").getAsString();
                        boolean crosspostable = !childData.get("is_crosspostable")
                                .getAsBoolean();
                        boolean over18 = childData.get("over_18").getAsBoolean();
                        String url;
                        try {
                            JsonObject preview = JsonParser.parseString(childData
                                    .get("preview").toString()).getAsJsonObject();
                            JsonArray previewImages = JsonParser.parseString(preview
                                    .get("images").toString()).getAsJsonArray();
                            JsonObject source = JsonParser.parseString(previewImages
                                    .get(0).toString()).getAsJsonObject();
                            JsonObject urlSrc = JsonParser.parseString(source
                                    .get("source").toString()).getAsJsonObject();
                            url = urlSrc.get("url").toString().replace("&amp;", "&")
                                    .replace("\"", "");
                        } catch (NullPointerException n) {
                            url = childData.get("url").getAsString();
                        }
                        String permalink = childData.get("permalink").getAsString();
                        boolean spoiler = childData.get("spoiler").getAsBoolean();

                        if (postHint.contains("video")) {
                            set.add(new RedditPostVideo(
                                    id, title, quarantine, score, postHint,
                                    crosspostable, over18, author,
                                    permalink, spoiler, url));
                        } else if ("link".equals(postHint)) {
                            set.add(new RedditPostLink(
                                    id, title, quarantine, score, postHint,
                                    crosspostable, over18, author,
                                    permalink, spoiler, url));
                        } else if ("text".equals(postHint)) {
                            set.add(new RedditPostText(
                                    id, title, quarantine, score, postHint,
                                    crosspostable, over18, author,
                                    permalink, spoiler, url));
                        } else if ("image".equals(postHint)) {
                            set.add(new RedditPostImage(
                                    id, title, quarantine, score, postHint,
                                    crosspostable, over18, author,
                                    permalink, spoiler, url));
                        }
                    }
                } catch (NullPointerException e) {
                    System.out.println(
                            "[*] There were a problem while parsing. "
                            + "Continuing");
                }
            }
            return set;
        } catch (JsonSyntaxException e) {
            System.err.println("[!] JsonSyntaxException: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
