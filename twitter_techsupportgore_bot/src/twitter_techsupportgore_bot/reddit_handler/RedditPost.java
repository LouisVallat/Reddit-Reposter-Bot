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
 * This is an abstract class to define all RedditPosts.
 *
 * @author louis
 */
public abstract class RedditPost {

    /**
     * Post's title.
     */
    protected String title;

    /**
     * Is the post in quarantine?
     */
    protected boolean quarantine;

    /**
     * Post's score.
     */
    protected double score;

    /**
     * Post's hint.
     */
    protected String postHint;

    /**
     * Is this post crosspostable?
     */
    protected boolean crosspostable;

    /**
     * Is this post NSFW?
     */
    protected boolean over18;

    /**
     * Post's author.
     */
    protected String author;

    /**
     * Post's permalink.
     */
    protected String permalink;

    /**
     * Is this post a spoiler?
     */
    protected boolean spoiler;

    public RedditPost(String title, boolean quarantine, double score, String postHint, boolean crosspostable, boolean over18, String author, String permalink, boolean spoiler) {
        this.title = title;
        this.quarantine = quarantine;
        this.score = score;
        this.postHint = postHint;
        this.crosspostable = crosspostable;
        this.over18 = over18;
        this.author = author;
        this.permalink = permalink;
        this.spoiler = spoiler;
    }

    public String getTitle() {
        return title;
    }

    public boolean isQuarantine() {
        return quarantine;
    }

    public double getScore() {
        return score;
    }

    public String getPostHint() {
        return postHint;
    }

    public boolean isCrosspostable() {
        return crosspostable;
    }

    public boolean isOver18() {
        return over18;
    }

    public String getAuthor() {
        return author;
    }

    public String getPermalink() {
        return permalink;
    }

    public boolean isSpoiler() {
        return spoiler;
    }
    
    
    
}
