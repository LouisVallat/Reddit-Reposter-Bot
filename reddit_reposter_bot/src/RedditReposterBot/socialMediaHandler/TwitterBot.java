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
package RedditReposterBot.socialMediaHandler;

import RedditReposterBot.exceptions.NotSufficientRights;
import RedditReposterBot.exceptions.NoSuchProperty;
import RedditReposterBot.ConfigFileReader;
import RedditReposterBot.exceptions.NoSuchFile;
import java.io.File;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twutter Bot object.
 *
 * @author louis
 */
public final class TwitterBot implements SocialMediaPoster {

    /**
     * The twitter link.
     */
    private final Twitter twitter;

    /**
     * The Twitter API consumer key.
     */
    private final String consumerKey;

    /**
     * The Twitter API consumer secret.
     */
    private final String consumerSecret;

    /**
     * The Twitter API access token.
     */
    private final String accessToken;

    /**
     * The Twitter API access secret.
     */
    private final String accessSecret;

    /**
     * Main constructor for the Twitter Bot.
     *
     * @throws RedditReposterBot.exceptions.NoSuchFile
     * @throws RedditReposterBot.exceptions.NotSufficientRights
     * @throws RedditReposterBot.exceptions.NoSuchProperty
     */
    public TwitterBot() throws NoSuchFile, NotSufficientRights, NoSuchProperty {
        ConfigFileReader reader = new ConfigFileReader();
        this.consumerKey = reader.getProperties("twitterAPI_consumerKey");
        this.consumerSecret = reader.getProperties("twitterAPI_consumerSecret");
        this.accessToken = reader.getProperties("twitterAPI_accessToken");
        this.accessSecret = reader.getProperties("twitterAPI_accessSecret");

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret);

        TwitterFactory factory = new TwitterFactory(cb.build());
        this.twitter = factory.getInstance();

        showScreenName();
    }

    /**
     * Print the bot's screen name.
     */
    public void showScreenName() {
        try {
            System.out.println("[+] Connected as user @"
                    + this.twitter.getScreenName()
                    + " on " + getSocialMediaName() + ".");
        } catch (TwitterException te) {
            System.err.println("[!] TwitterException: " + te.getMessage());
            System.exit(1);
        }
    }

    /**
     * Tweet a text.
     *
     * @param text the text to tweet.
     * @return the tweet's id.
     */
    @Override
    public long postText(String text) {
        try {
            System.out.println("[+] Tweeting \"" + text + "\".");
            return twitter.updateStatus(text).getId();
        } catch (TwitterException te) {
            System.err.println("[!] TwitterException: " + te.getMessage());
        }
        return 0;
    }

    /**
     * Tweet an image.
     *
     * @param imagePath the path to the image.
     * @return the tweet's id.
     */
    @Override
    public long postImage(String imagePath) {
        System.out.println("[+] Tweeting image.");
        File file = new File(imagePath);

        StatusUpdate status = new StatusUpdate("");
        status.setMedia(file);
        try {
            return twitter.updateStatus(status).getId();
        } catch (TwitterException te) {
            System.err.println("[!] TwitterException: " + te.getMessage());
        }
        return 0;
    }

    /**
     * Post an image with some text.
     *
     * @param text image's caption.
     * @param imagePath path to the image.
     * @return the tweet's id.
     */
    @Override
    public long postImage(String text, String imagePath) {
        System.out.println("[+] Tweeting image with caption \"" + text + "\".");
        File file = new File(imagePath);

        StatusUpdate status = new StatusUpdate(text);
        status.setMedia(file);
        try {
            return twitter.updateStatus(status).getId();
        } catch (TwitterException te) {
            System.err.println("[!] TwitterException: " + te.getMessage());
        }
        return 0;
    }

    /**
     * Get the social media name.
     *
     * @return the social media name.
     */
    @Override
    public String getSocialMediaName() {
        return "Twitter";
    }

    /**
     * Get the screen name.
     *
     * @return the user's screen name.
     * 
     * @throws TwitterException
     */
    public String getScreenName() throws TwitterException {
        return this.twitter.getScreenName();
    }

    /**
     * Reply to a tweet.
     *
     * @param text the text to tweet.
     * @param tweetId the tweet id.
     * @return the reply's id.
     */
    @Override
    public long replyText(String text, long tweetId) {
        System.out.println("[+] Replying \"" + text + "\" to tweet "
                + tweetId + ".");
        StatusUpdate statusReply = new StatusUpdate(text);
        statusReply.setInReplyToStatusId(tweetId);
        try {
            twitter.updateStatus(statusReply);
        } catch (TwitterException ex) {
            System.err.println("TwitterException: " + ex.getMessage());
        }
        return statusReply.getInReplyToStatusId();
    }

}
