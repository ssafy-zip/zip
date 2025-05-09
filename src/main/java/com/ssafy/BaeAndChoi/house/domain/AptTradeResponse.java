package com.ssafy.BaeAndChoi.house.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

/**
 * 아파트매매기본자료(API: getRTMSDataSvcAptTrade) 응답을 매핑하는 최상위 VO 클래스
 */
@JacksonXmlRootElement(localName = "response")
@Data
public class AptTradeResponse {

    /** 응답 헤더 정보 (resultCode, resultMsg 등) */
    @JacksonXmlProperty(localName = "header")
    private Header header;

    /** 응답 바디 정보 (items 포함) */
    @JacksonXmlProperty(localName = "body")
    private Body body;

    @Data
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;

        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }

    @Data
    public static class Body {
        // 페이지네이션 정보
        @JacksonXmlProperty(localName = "numOfRows")
        private int numOfRows;

        @JacksonXmlProperty(localName = "pageNo")
        private int pageNo;

        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;
        @JacksonXmlProperty(localName = "items")
        private Items items;
    }

    @Data
    public static class Items {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private List<AptTradeBasicData> item;
    }
}
