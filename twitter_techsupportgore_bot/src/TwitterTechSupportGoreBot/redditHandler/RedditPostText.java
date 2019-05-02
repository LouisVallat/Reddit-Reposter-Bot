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
package TwitterTechSupportGoreBot.redditHandler;

/**
 * Reddit text post object representation.
 *
 * @author louis
 */
public class RedditPostText extends RedditPost {

    public RedditPostText(String id, String title, boolean quarantine,
            double score, String postHint, boolean crosspostable,
            boolean over18, String author, String permalink,
            boolean spoiler, String url) {
        super(id, title, quarantine, score, postHint,
                crosspostable, over18, author, permalink, spoiler, url);
    }

    @Override
    public boolean isImage() {
        return false;
    }

    @Override
    public boolean isText() {
        return true;
    }

    @Override
    public boolean isVideo() {
        return false;
    }

    @Override
    public boolean isLink() {
        return false;
    }
}
