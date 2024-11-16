package com.study.sddodyandroid.common

import com.study.sddodyandroid.dto.AlarmDto
import com.study.sddodyandroid.dto.BoardRequestDto
import com.study.sddodyandroid.dto.BoardResponseDto
import com.study.sddodyandroid.dto.ChatDto
import com.study.sddodyandroid.dto.ChatInfoDto
import com.study.sddodyandroid.dto.ChatRoomDto
import com.study.sddodyandroid.dto.CommentReqestDto
import com.study.sddodyandroid.dto.FirebaseSendDto
import com.study.sddodyandroid.dto.GithubUserProfileDto
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.dto.RequestParticipationRequestDto
import com.study.sddodyandroid.dto.StudyIdMemberIdDto
import com.study.sddodyandroid.dto.StudyRequestDto
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.dto.TokenDto
import com.study.sddodyandroid.helper.study.StudyOrderType
import com.study.sddodyandroid.helper.study.StudyStateEnum
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

public interface APIService {
    @POST("/api/auth/start")
    fun signup(@Body tokenDto: TokenDto): Call<TokenDto>?


//    @POST("/api/auth/signup")
//    fun signup(@Body requestSignupRequestDto: SignupRequestDto): Call<Void?>?

    @POST("/api/chatRoom/firebaseTest")
    fun test(@Body firebaseSend: FirebaseSendDto) : Call<Void>?

    @Multipart
    @PUT("/api/auth/memberInfo")
    fun setMemberInfo(@Part("memberDto") memberDto: RequestBody,
                      @Part profileImage: MultipartBody.Part?
    ): Call<TokenDto>


    @GET("/api/auth/checkTokenValid")
    fun isValidToken() : Call<Void>

    @GET("/api/auth/memberInfo/{id}")
    fun getMemberInfo(@Path("id") memberId : Long): Call<MemberInfoDto>?

    @POST("/api/auth/githubInfo")
    fun setGithubMemberInfo(@Body githubUserProfileDto: GithubUserProfileDto) : Call<Void>

    @GET("/api/chatRoom/list")
    fun getChatRoomList(): Call<List<ChatRoomDto>>

    @GET("/api/chatRoom/subscribeInfoList")
    fun getSubscribeInfoList(): Call<List<String>>

    @GET("/api/chatRoom/{studyId}/msg")
    fun getChatInfoList(@Path("studyId") studyId: Long): Call<List<ChatInfoDto>>

    @Multipart
    @POST("/api/study/")
    fun createStudy(
        @Part("studyDto") studyRequestDto: StudyRequestDto,
        @Part imageList: List<MultipartBody.Part?>): Call<Void>?


    @POST("/api/study/{id}/participationRequest")
    fun studyParticipationRequest(@Path("id") studyId: Long,@Body message : String): Call<Void>?


    @GET("/api/study/list")
    fun getStudyList(
        @Query("page") pageDto: Int,
        @Query("orderType") orderType: StudyOrderType
    ): Call<List<StudyResponseDto>>?

    @GET("/api/study/member/list/")
    fun getMemberStudyList(
        @Query("memberId") memberId : Long,
        @Query("orderType") orderType: StudyOrderType
    ) : Call<List<StudyResponseDto>>?

    @GET("/api/study/{id}")
    fun getStudy(@Path("id") id: Long): Call<StudyResponseDto>?


    @PUT("/api/study/memberRefuse")
    fun studyMemberRefuse(@Body studyMemberDto: StudyIdMemberIdDto): Call<Void>?
    @GET("/api/chatRoom/{studyId}")
    fun getChatRoomInfo(@Path("studyId") studyId: Long): Call<ChatRoomDto>

    @Multipart
    @POST("/api/chatRoom/{studyId}")
    fun sendChat(@Path("studyId") studyId: Long,@Part("chatDto") chatDto: ChatDto?,@Part imageList : List<MultipartBody.Part?>): Call<Void>?


    @GET("/api/study/location/list")
    fun getStudyLocationInfoList(@Query("isOnlyNearbyRecommend") isOnlyNearbyRecommend : Boolean = false): Call<List<StudyResponseDto>>

    /*
    커뮤니티
     */
    @Multipart
    @POST("/api/board/")
    fun createBoard(@Part("boardRequestDto") boardRequestDto : BoardRequestDto, @Part imageList : List<MultipartBody.Part?>) : Call<Void>?

    @GET("/api/board/{boardId}")
    fun getBoard(@Path("boardId") boardId: Long) : Call<BoardResponseDto>?

    @GET("/api/img/get.cf")
    fun downloadImage(@Query("fileName") fileName: String): Call<ResponseBody>?

    @Multipart
    @PUT("/api/board/{boardId}")
    fun updateBoard(@Path("boardId") boardId : Long, @Part("boardRequestDto") boardRequestDto: BoardRequestDto, @Part imageList : List<MultipartBody.Part?>) : Call<Void>?

    /**
     *
     * get 일반 보드
     * 이때 한 페이지당 20개씩 가져온다
     * @param pageDto
     */
    @GET("/api/board/list")
    fun getBoardList(@Query("page") pageDto : Int,
                     @Query("isPortfolio") isPortfolio : Boolean? = false): Call<List<BoardResponseDto>>?


    @GET("/api/board/member/list")
    fun getMemberBoardList(
                           @Query("isPortfolio") isPortfolio : Boolean? = false,
                           @Query("memberId") memberId : Long,
                         ): Call<List<BoardResponseDto>>?


    /**
     * 유저가 작성한 리뷰 가져오기
     */
    @GET("/api/board/member/{memberId}/studyReviewList")
    fun getMemberStudyReviewList(
        @Path("memberId") memberId : Long,
    ): Call<List<BoardResponseDto>>?

    /**
     * 스터디의 리뷰
     */

    @GET("/api/board/study/{studyId}/studyReviewList")
    fun getStudyReviewList(
        @Path("studyId") studyId : Long,
    ): Call<List<BoardResponseDto>>?


    //Study관련 게시물 들고와야함
    @GET("/api/board/list/{studyId}")
    fun getStudyBoardList(@Path("studyId") studyId : Long,@Query("page") pageDto : Int): Call<List<BoardResponseDto>>?

    @DELETE("/api/board/{boardId}")
    fun deleteBoard(@Path("boardId") boardId : Long): Call<Void>?


    //댓글
    @POST("/api/comment/")
    fun createComment(@Body commentRequestDto : CommentReqestDto) : Call<Void>?


    //좋아요
    @POST("/api/heart/")
    fun createHeart(@Body heartDto : HeartDto) : Call<Void>?

    @DELETE("/api/comment/{id}")
    fun deleteComment(@Path("id") commentId : Long): Call<Void>?

    @GET("/api/study/recommend")
    fun getRecommendStudy(): Call<List<StudyResponseDto>>?


    //참여신청한 멤버리스트
    @GET("/api/study/{id}/requestMemberList")
    fun getRequestMemberList(@Path("id") studyId: Long): Call<List<MemberInfoDto>>

    @POST("/api/study/{id}/checkParticipation")
    fun studyCheckParticipation(@Path("id") studyId: Long,@Body requestParticipationRequestDto: RequestParticipationRequestDto) : Call<Void>?


    @PUT("/api/study/memberDelete")
    fun studyDeleteMember(@Body studyMemberDto: StudyIdMemberIdDto) : Call<Void>?

    @PUT("/api/study/{studyId}/changeStatus")
    fun updateStudyStatus(@Path("studyId") studyId: Long,@Body newStatus : StudyStateEnum): Call<Void>?

    @GET("/api/auth/notification")
    fun getAlarmDto(): Call<List<AlarmDto>>

    @PUT("/api/auth/readAlarm")
    fun readAlarm(): Call<Void>

    @PUT("/api/chatRoom/{studyId}/read")
    fun readChat(@Path("studyId") studyId : Long) : Call<Void>

    @PUT("/api/auth/changeMemberStatus")
    fun changeMemberStatus() : Call<Void>

}