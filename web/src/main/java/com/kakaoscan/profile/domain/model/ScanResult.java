package com.kakaoscan.profile.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResult {

    @JsonProperty("OriginName")
    private String originName;

    @JsonProperty("IsEmptyProfile")
    private int isEmptyProfile;

    @JsonProperty("IsEmptyBackgroundImage")
    private int isEmptyBackgroundImage;

    @JsonProperty("ProfileImageUrl")
    private String profileImageUrl;

    @JsonProperty("StatusMessage")
    private String statusMessage;

    @JsonProperty("MusicName")
    private String musicName;

    @JsonProperty("ArtistName")
    private String artistName;

    @JsonProperty("MusicAlbumUrl")
    private String musicAlbumUrl;

    @JsonProperty("Host")
    private String host;

    @JsonProperty("ImageUrlCount")
    private int imageUrlCount;

    @JsonProperty("ImageUrl")
    private List<ImageUrl> imageUrlList;

    @JsonProperty("BgImageUrlCount")
    private int bgImageUrlCount;

    @JsonProperty("BgImageUrl")
    private List<ImageUrl> bgImageUrlList;

    @JsonProperty("VideoCount")
    private int videoCount;

    @JsonProperty("VideoUrl")
    private List<VideoUrl> videoUrlList;

    public String getOriginName() {
        return Objects.isNull(originName) || originName.isEmpty() ? "이름 없음" : originName;
    }

    public String getStatusMessage() {
        return Objects.isNull(statusMessage) || statusMessage.isEmpty() ? "상태메세지 없음" : statusMessage;
    }

    @Getter
    @Setter
    public static class ImageUrl {
        @JsonProperty("Dir")
        private String dir;

        @JsonProperty("Name")
        private String name;

        @JsonIgnore
        private String url;
    }

    @Getter
    @Setter
    public static class VideoUrl {
        @JsonProperty("Dir")
        private String dir;

        @JsonProperty("Name")
        private String name;

        @JsonIgnore
        private String url;
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ScanResult deserialize(String json) throws IOException {
        return mapper.readValue(json, ScanResult.class);
    }
}
