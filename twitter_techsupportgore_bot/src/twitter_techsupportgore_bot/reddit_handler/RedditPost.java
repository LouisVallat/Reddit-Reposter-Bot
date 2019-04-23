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
     * Post's id.
     */
    protected String postId;
    
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

    /**
     * Main constructor for a Reddit post.
     * @param id post's id
     * @param title post's title
     * @param quarantine is this post in quarantine?
     * @param score post's score
     * @param postHint post's hint
     * @param crosspostable is post crosspostable?
     * @param over18 is post NSFW?
     * @param author post's author
     * @param permalink post's permalink
     * @param spoiler is this post a spoiler?
     */
    public RedditPost(String id, String title, boolean quarantine, double score, String postHint, boolean crosspostable, boolean over18, String author, String permalink, boolean spoiler) {
        this.postId = id;
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

    /**
     * Get post's title.
     * @return post's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Is this post in quarantine?
     * @return if the post is in quarantine.
     */
    public boolean isQuarantine() {
        return quarantine;
    }
    
    /**
     * Set quarantine state for the post.
     * @param state the state to apply.
     */
    public void setQuarantineState(boolean state) {
        this.quarantine = state;
    } 

    /**
     * Get post's score.
     * @return last known post's score.
     */
    public double getScore() {
        return score;
    }
    
    /**
     * Update post's score.
     * @param newScore the new score.
     */
    public void updateScore(double newScore) {
        this.score = newScore;
    }

    /**
     * Get post's hint.
     * @return post's hint.
     */
    public String getPostHint() {
        return postHint;
    }

    /**
     * Is this post crosspostable?
     * @return if the post is crosspostable.
     */
    public boolean isCrosspostable() {
        return crosspostable;
    }

    /**
     * Is this post NSFW?
     * @return if the post is Not Safe For Work.
     */
    public boolean isOver18() {
        return over18;
    }

    /**
     * Get post's author.
     * @return post's author.
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * Get post's permalink.
     * @return the post's permalink.
     */
    public String getPermalink() {
        return permalink;
    }

    /**
     * Is this post a spoiler?
     * @return if the post is a spoiler.
     */
    public boolean isSpoiler() {
        return spoiler;
    }

    /**
     * Get post's id.
     * @return post's id.
     */
    public String getPostId() {
        return postId;
    }
    
    /**
     * Is this post an image?
     * @return if the post is an image.
     */
    public abstract boolean isImage();
    
    /**
     * Is this post a text?
     * @return if the post is a text.
     */
    public abstract boolean isText();
    
    /**
     * Is this post a video?
     * @return if the post is a video.
     */
    public abstract boolean isVideo();
    
    
    /**
     * Is this post a link?
     * @return if the post is a link.
     */
    public abstract boolean isLink();
}
