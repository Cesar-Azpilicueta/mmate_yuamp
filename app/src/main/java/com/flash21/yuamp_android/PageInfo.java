package com.flash21.yuamp_android;

public class PageInfo {

    public static final String SITE_KEY = "ST0043";

    // 기본경로 페이지
    public static final String INDEX_PAGE = "http://192.168.0.18/"; //http://192.168.0.18  https://yuamp-mmate.flash21.com/

//	public static final String INDEX_PAGE2 = "http://192.168.0.18/s/YUAMP/main.do";

    public static final String MAIN_PAGE = INDEX_PAGE + "s/yuamp/main.do";

    // 멤버 확인 후 구글키와 핸드폰 번호를 저장하고 유저 이름 및 앱 버전 리턴 받음
    public static final String INSERT_PUSH_DATA_PAGE = INDEX_PAGE + "servlet/j_spring_security_check";
    // 실패페이지
    public static final String FAIL_PAGE = INDEX_PAGE + "fail";

    // 공지사항게시판 상세 페이지
    public static final String BOARD_VIEW_PAGE = INDEX_PAGE + "board_list";
    // 홍보게시판 상세 페이지
    public static final String HONGBO_BOARD_VIEW_PAGE = INDEX_PAGE + "hongbo_board_list";
    // 행사 참석
    public static final String EVENT_BOARD_VIEW_PAGE = INDEX_PAGE + "event_board_list";

}
