package com.pettalk.wcboard.service;

import com.pettalk.exception.BusinessLogicException;
import com.pettalk.exception.ExceptionCode;
import com.pettalk.wcboard.entity.WcBoard;
import com.pettalk.wcboard.repository.WcBoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WcBoardService {
    private final WcBoardRepository wcBoardRepository;

    public WcBoardService(WcBoardRepository wcBoardRepository){ this.wcBoardRepository = wcBoardRepository; }

    //구현 주요 로직 로그인한 경우에만 게시글 작성 가능
    public WcBoard createWcBoardPost (WcBoard wcboard){
        wcboard.setPostStatus(WcBoard.PostStatus.DEFAULT);
        // TODO : 멤버 ID 가져오기 로그인한 사용자만 게시글 작성 가능
        // memberService.findMember(WcBoard.getMember().getMemberId());
        wcboard.setCreatedAt(LocalDateTime.now());
        wcBoardRepository.save(wcboard);
        return wcboard;
    }

    // 구현 주요 로직 : 로그인한 상태라도 본인의 게시글이 아니면 수정 불가
    public WcBoard updateWcBoardPost (WcBoard wcboard) {
        WcBoard findPost = findVerifyPost(wcboard.getWcboardId()); // TODO : 멤버 아이디 불러와서 검증 로직 구현 필요

        if (wcboard.getPostStatus() == WcBoard.PostStatus.DEFAULT){
            Optional.ofNullable(wcboard.getTitle())
                    .ifPresent(title -> findPost.setTitle(title));
            Optional.ofNullable(wcboard.getContent())
                    .ifPresent(content -> findPost.setContent(content)); // TODO : 타이틀과 내용말고 다른것도 수정 추가 필요
        }else{
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND); // TODO : 수정불가 에러코드로 변경 예정
        }


        return wcBoardRepository.save(findPost);
    }

    // wcBoardId 검색해주는 메서드
    public WcBoard findWcBoardPost(long wcboardId){
        return findVerifyPost(wcboardId);
    }

    //전체 글 조회 (최신순 정렬)
    public Page<WcBoard> findAllPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return wcBoardRepository.findAll(pageRequest);
    }

    public Page<WcBoard> findPostByWcTag(int page, int size, String wcTag) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return wcBoardRepository.findByWcTagContaining(wcTag, pageRequest);
    }

    public Page<WcBoard> findPostByAnimalTag(int page, int size, String animalTag) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return wcBoardRepository.findByAnimalTagContaining(animalTag, pageRequest);
    }

    public Page<WcBoard> findPostByAreaTag(int page, int size, String areaTag) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return wcBoardRepository.findByAreaTagContaining(areaTag, pageRequest);
    }

    public void deletePost(long wcboardId) {
        WcBoard findPost = findVerifyPost(wcboardId);

        if(!findPost.getPostStatus().equals(WcBoard.PostStatus.DEFAULT)) { // 진행중인 게시글만 삭제 가능
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND); // 삭제 불가능 예외처리 TODO : 에러코드 수정 필요
        }
        /** 멤버와 연결후 구현 / 게시글 작성자만 삭제 가능한 로직
        if(!findPost.getMember().getMemberId().equals(MemberService.getLoginUserId())) { // 게시글 작성자만 삭제 가능
            throw new BusinessLogicException(ExceptionCode.NOT_RESOURCE_OWNER); // 삭제 불가능 예외처리
        }
        */

        wcBoardRepository.delete(findPost);
    }


    // 게시글 찾고 없으면 예외처리
    public WcBoard findVerifyPost (long wcboardId) {
        Optional<WcBoard> optionalPOST = wcBoardRepository.findById(wcboardId);

        WcBoard findWcBoardPost =
                optionalPOST.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)); // TODO : 게시글 없음으로 에러 변경
        return findWcBoardPost;
    }


}