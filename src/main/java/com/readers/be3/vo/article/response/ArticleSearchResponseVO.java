package com.readers.be3.vo.article.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;




public interface ArticleSearchResponseVO {
    @Schema(name = "aiSeq", description = "게시글 번호", example="1")
    public Integer getAiSeq();
    @Schema(name = "aiTitle", description = "게시글 제목", example="안녕하세요.")
    public String getAiTitle();
    @Schema(name = "aiRegDt" ,description = "등록일")
    public LocalDateTime getAiRegDt();
    @Schema(name = "aiMoDt", description = "수정일")
    public LocalDateTime getAiModDt();
    @Schema(name = "aiUiSeq", description = "작성한 회원 번호", example="1")
    public Integer getAiUiSeq();
    @Schema(name = "aiBiSeq", description = "책 번호", example="1")
    public Integer getAiBiSeq();
    @Schema(name = "uiNickname", description = "작성자명(닉네임)", example="testNickname")
    public String getUiNickname();
}
