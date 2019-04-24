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
package twitter_techsupportgore_bot.reddit_handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
     * @throws IOException
     */
    public RedditExtractor(String subreddit) throws IOException {
        if (!doesSubredditExists(subreddit)) {
            throw new MalformedURLException("This subreddit (" + subreddit + ") does not exist.");
        } else {
            this.sub = new SubReddit(subreddit);
        }
    }

    /**
     * Check if a subreddit exists.
     *
     * TODO: FIND A BETTER WAY TO DO THAT
     *
     * @param subredditName
     * @return
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    public boolean doesSubredditExists(String subredditName)
            throws MalformedURLException, IOException {
        String urlToTest = "https://www.reddit.com/r/" + subredditName + "/";
        HttpURLConnection huc = (HttpURLConnection) (new URL(urlToTest).openConnection());
        huc.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; "
                + "Windows NT 5.1; en-US; rv:1.8.0.11) ");
        huc.setRequestMethod("HEAD");
        huc.connect();

        int respCode = huc.getResponseCode();
        return respCode < 400;
    }

    /**
     * Obtain the subreddit JSON response.
     *
     * @return the JSON from the REDDIT api.
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    public String getSubredditJson() throws MalformedURLException, ProtocolException, IOException {
        HttpURLConnection con;
        URL myurl = new URL(this.sub.getJsonURL());
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
    }

    /**
     * Get Reddit's subreddit posts.
     *
     * @return a treeset of all the reddit posts parsed.
     * @throws java.net.ProtocolException
     * @throws java.net.MalformedURLException
     */
    public HashSet<RedditPost> getRedditPosts() throws ProtocolException, IOException, MalformedURLException {
        HashSet<RedditPost> set = new HashSet<>();
        String jsonResponse = getSubredditJson();
        JsonObject objet = new JsonParser().parse(jsonResponse).getAsJsonObject();
        JsonObject data = new JsonParser().parse(objet.get("data").toString()).getAsJsonObject();
        JsonArray children = new JsonParser().parse(data.get("children").toString()).getAsJsonArray();
        for (int i = 0; i < children.size(); i++) {
            JsonObject child = new JsonParser().parse(children.get(i).toString()).getAsJsonObject();
            JsonObject childData = new JsonParser().parse(child.get("data").toString()).getAsJsonObject();
            if (childData.get("id").toString() != null
                    && !childData.get("quarantine").getAsBoolean()
                    && childData.get("url").getAsString() != null) {
                String id = childData.get("id").toString();
                String title = childData.get("title") != null
                        ? childData.get("title").toString() : this.sub.getName();
                title = title.replaceAll("\"", "").replace("\\", "\"");
                String author = childData.get("author") != null
                        ? childData.get("author").toString() : "anonymous";
                boolean quarantine = childData.get("quarantine").getAsBoolean();
                double score = childData.get("score").getAsDouble();
                String postHint = childData.get("post_hint").getAsString();
                boolean crosspostable = !childData.get("is_crosspostable").getAsBoolean();
                boolean over18 = childData.get("over_18").getAsBoolean();
                String url;
                try {
                    JsonObject preview = new JsonParser().parse(childData.get("preview").toString()).getAsJsonObject();
                    JsonArray previewImages = new JsonParser().parse(preview.get("images").toString()).getAsJsonArray();
                    JsonObject source = new JsonParser().parse(previewImages.get(0).toString()).getAsJsonObject();
                    JsonObject urlSrc = new JsonParser().parse(source.get("source").toString()).getAsJsonObject();
                    url = urlSrc.get("url").toString().replace("amp;", "");
                } catch (NullPointerException n) {
                    url = childData.get("url").getAsString();
                }
                String permalink = childData.get("url").getAsString();
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
        }
        return set;
    }
}
