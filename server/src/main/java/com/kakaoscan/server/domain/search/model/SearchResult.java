package com.kakaoscan.server.domain.search.model;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchResult {
    private Profile profile;
    private int status;

    @Getter
    public static class Profile {
        private String nickName;
        private String profileImageUrl;
        private String backgroundImageUrl;
        private String statusMessage;
        private Musics musics;
        private ProfileFeeds profileFeeds;
        private ProfileFeeds backgroundFeeds;
        private String storyWebUrl;
    }

    @Getter
    public static class ProfileFeeds {
        private List<Feed> feeds;
    }

    @Getter
    public static class Feed {
        private List<Content> contents;
        private Extra extra;
        private long updatedAt;
        private boolean isCurrent;
    }

    @Getter
    public static class Content {
        private String type;
        private String value;
    }

    @Getter
    public static class Extra {
        private String originalAnimatedBackgroundImageUrl;
    }

    @Getter
    public static class Musics {
        private List<ContentInfo> contentsInfo;
    }

    @Getter
    public static class ContentInfo {
        private String contentName;
        private List<Artist> artistList;
        private String contentImgPath;
        private long updatedAt;
    }

    @Getter
    public static class Artist {
        private String artistName;
    }
}
