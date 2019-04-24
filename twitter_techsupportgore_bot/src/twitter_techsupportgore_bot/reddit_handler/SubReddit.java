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

/**
 * A subreddit.
 *
 * @author louis
 */
public class SubReddit {

    /**
     * Subreddit's name.
     */
    private final String name;

    /**
     * Subreddit's URL.
     */
    private final String url;

    /**
     * Subreddit's JSON URL.
     */
    private final String jsonURL;

    /**
     * Dist limit for the JSON api call.
     */
    private int limit = 25;
    
    /**
     * Order for the JSON.
     */
    private String order = "new";
    
    /**
     * Main constructor.
     *
     * @param name subreddit's name
     */
    public SubReddit(String name) {
        this.name = name;
        this.url = "https://www.reddit.com/r/" + name + "/";
        this.jsonURL = this.url.substring(0, this.url.length()) + order + ".json";
    }

    /**
     * Set subreddit dist limit for parsing JSON file.
     * @param limit the limit between 1 and 100.
     */
    public void setLimit(int limit) {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("Limit should be between 1 and 100");
        } else {
            this.limit = limit;
        }
    }
    
    /**
     * Get subreddit dist limit.
     * @return the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Get name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the url.
     *
     * @return the subreddit's url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the JSON URL.
     *
     * @return the subreddit's JSON URL.
     */
    public String getJsonURL() {
        return jsonURL + "?limit=" + limit;
    }
}
