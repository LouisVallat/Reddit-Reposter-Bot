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
package TwitterTechSupportGoreBot.socialMediaHandler;

/**
 * Interface to specify what methods should social media posters implement.
 *
 * @author louis
 */
public interface SocialMediaPoster {

    public String getSocialMediaName();

    public long postText(String text);

    public long postImage(String imagePath);

    public long postImage(String text, String imagePath);

    public long replyText(String text, long tweetId);
}
