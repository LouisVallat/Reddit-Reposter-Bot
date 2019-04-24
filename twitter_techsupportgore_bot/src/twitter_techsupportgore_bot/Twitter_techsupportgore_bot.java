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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.TreeSet;
import twitter_techsupportgore_bot.reddit_handler.*;

/**
 * This is where everything begins.
 *
 * @author louis
 */
public class Twitter_techsupportgore_bot {

    /**
     * Delay between two scans, in seconds.
     */
    private static final int DELAY = 5;

    /**
     * Subreddit to extract info from.
     */
    private static final String SUBREDDIT = "techsupportgore";

    /**
     * Launch the bot.
     *
     * @param args the command line arguments
     * @throws java.net.MalformedURLException
     * @throws java.net.ProtocolException
     */
    public static void main(String[] args) throws MalformedURLException,
            ProtocolException, IOException {

        TreeSet<RedditPost> postsIndexed = new TreeSet<>();
        RedditExtractor red = new RedditExtractor(SUBREDDIT);
        for (RedditPost r : red.getRedditPosts()) {
            Hypervisor.saveImage(r.getUrl());
        }
    }
}
