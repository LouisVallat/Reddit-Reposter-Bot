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
package RedditReposterBot.redditHandler;

import RedditReposterBot.ConfigFileReader;
import RedditReposterBot.exceptions.NoSuchFile;
import RedditReposterBot.exceptions.NoSuchOrder;
import RedditReposterBot.exceptions.NoSuchProperty;
import RedditReposterBot.exceptions.NotSufficientRights;
import java.util.Arrays;

/**
 * A subreddit.
 *
 * @author louis
 */
public final class SubReddit {

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
    private int limit;

    /**
     * Order for the JSON (by default, new).
     */
    private String order;

    /**
     * Main constructor.
     *
     * @param name subreddit's name
     *
     * @throws RedditReposterBot.exceptions.NoSuchProperty
     * @throws RedditReposterBot.exceptions.NoSuchFile
     * @throws RedditReposterBot.exceptions.NotSufficientRights
     * @throws RedditReposterBot.exceptions.NoSuchOrder
     */
    public SubReddit(String name) throws NoSuchProperty, NoSuchFile,
            NotSufficientRights, NoSuchOrder {
        this.name = name;
        ConfigFileReader reader = new ConfigFileReader();
        setLimit(Integer.valueOf(reader.getProperties("reddit_posts_limit")));
        setOrder(reader.getProperties("reddit_posts_sorting_order"));
        this.url = "https://www.reddit.com/r/" + name + "/";
        this.jsonURL
                = this.url.substring(0, this.url.length()) + order + ".json";
    }

    /**
     * Set subreddit dist limit for parsing JSON file.
     *
     * @param limit the limit between 1 and 100.
     */
    public void setLimit(int limit) {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "Limit should be between 1 and 100, and it was"
                    + limit + ".");
        } else {
            this.limit = limit;
        }
    }

    /**
     * Set order for the subreddit.
     *
     * @param order an order to set
     *
     * @throws NoSuchOrder
     */
    public void setOrder(String order) throws NoSuchOrder {
        String[] availableOrders
                = {"new", "hot", "best", "controversial", "top", "rising"};
        if (!Arrays.asList(availableOrders).contains(order)) {
            throw new NoSuchOrder("This order " + order
                    + "isn't allowed. Orders allowed are: "
                    + Arrays.toString(availableOrders));
        } else {
            this.order = order;
        }
    }

    /**
     * Get subreddit dist limit.
     *
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
