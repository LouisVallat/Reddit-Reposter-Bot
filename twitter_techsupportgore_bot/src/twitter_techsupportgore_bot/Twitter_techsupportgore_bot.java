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
import twitter_techsupportgore_bot.reddit_handler.RedditPost;
import twitter_techsupportgore_bot.reddit_handler.RedditPostLink;

/**
 * This is where everything begins.
 *
 * @author louis
 */
public class Twitter_techsupportgore_bot {

    private static HttpURLConnection con;

    /**
     * Launch the bot.
     *
     * @param args the command line arguments
     * @throws java.net.MalformedURLException
     * @throws java.net.ProtocolException
     */
    public static void main(String[] args) throws MalformedURLException,
            ProtocolException, IOException {

        String url = "https://www.reddit.com/r/techsupportgore/new.json?limit=75";

        /*
        try {

            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; "
                    + "Windows NT 5.1; en-US; rv:1.8.0.11) ");

            StringBuilder content;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }

            }
            JsonObject objet = new JsonParser().parse(content.toString()).getAsJsonObject();
            JsonObject data = new JsonParser().parse(objet.get("data").toString()).getAsJsonObject();
            JsonArray children = new JsonParser().parse(data.get("children").toString()).getAsJsonArray();

            for (int i = 0; i < children.size(); i++) {
                JsonObject child = new JsonParser().parse(children.get(i).toString()).getAsJsonObject();
                JsonObject childData = new JsonParser().parse(child.get("data").toString()).getAsJsonObject();
                System.out.println("Title: " + childData.get("title").toString());
                System.out.println("From: " + childData.get("author").toString());
                System.out.println("Is crosspostable ? : " + !childData.get("is_crosspostable").getAsBoolean());
                System.out.println("Is a video ? : " + childData.get("is_video").getAsBoolean());
                System.out.println("Is mature ? : " + childData.get("over_18").getAsBoolean());
                System.out.println("Score : " + childData.get("score").getAsDouble());
                System.out.println("Post url : " + childData.get("url").getAsString());
                JsonObject preview = new JsonParser().parse(childData.get("preview").toString()).getAsJsonObject();
                JsonArray previewImages = new JsonParser().parse(preview.get("images").toString()).getAsJsonArray();
                JsonObject source = new JsonParser().parse(previewImages.get(0).toString()).getAsJsonObject();
                JsonObject urlSrc = new JsonParser().parse(source.get("source").toString()).getAsJsonObject();
                System.out.println("Source url : " + urlSrc.get("url").toString().replace("amp;", ""));
                System.out.println("-----------------");
            }

        } finally {

            con.disconnect();
        }
        
        */
    }
}
