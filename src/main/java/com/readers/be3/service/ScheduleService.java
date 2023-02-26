package com.readers.be3.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.readers.be3.entity.ScheduleInfoEntity;
import com.readers.be3.repository.BookInfoRepository;
import com.readers.be3.repository.ScheduleInfoRepository;
import com.readers.be3.repository.UserInfoRepository;
import com.readers.be3.vo.book.InvalidInputException;
import com.readers.be3.vo.schedule.AddScheduleVO;
import com.readers.be3.vo.schedule.UpdateScheduleVO;
import com.readers.be3.vo.schedule.ViewScheduleVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final UserInfoRepository userInfoRepository;
    private final BookInfoRepository bookInfoRepository;
    private final ScheduleInfoRepository scheduleInfoRepository;

    public List<ViewScheduleVO> getSchedule(Long uiSeq) {
        List<ViewScheduleVO> list = new ArrayList<ViewScheduleVO>();
        userInfoRepository.findById(uiSeq).orElseThrow(() -> new InvalidInputException("존재하지 않는 회원번호 입니다."));
        if (scheduleInfoRepository.findBySiUiSeq(uiSeq).size()==0) {
            throw new NoSuchElementException();
        }
        for (ScheduleInfoEntity entity : scheduleInfoRepository.findBySiUiSeq(uiSeq)) {
            ViewScheduleVO vo = new ViewScheduleVO(entity);
            list.add(vo);
        }
        return list;
    }

    public ViewScheduleVO addSchedule(AddScheduleVO data) {
        ViewScheduleVO vo = new ViewScheduleVO();
        LocalDateTime sDate = null;
        LocalDateTime eDate = null;
        if (data.getStart()!=null) {
            sDate = data.getStart();
        }
        if (data.getEnd()!=null) {
            eDate = data.getEnd();
        }
        Integer status = 1;
        if (bookInfoRepository.findById(data.getBiSeq()).isEmpty()) {
            throw new InvalidInputException("존재하지 않는 책 번호 입니다.");
        }
        if ((sDate!=null && eDate!=null) && sDate.isAfter(eDate)) {
            throw new InvalidInputException("종료일은 시작일보다 빠를 수 없습니다.");
        }
        vo.setTitle(bookInfoRepository.findById(data.getBiSeq()).get().getBiName());
        vo.setDescription(data.getDescription());
        vo.setStart(sDate);
        vo.setEnd(eDate);
        vo.setStatus(status);

        ScheduleInfoEntity entity = ScheduleInfoEntity.builder()
                .siContent(data.getDescription())
                .siStartDate(sDate)
                .siEndDate(eDate)
                .siStatus(status)
                .siUiSeq(data.getUiSeq())
                .siBiSeq(data.getBiSeq()).build();

        scheduleInfoRepository.save(entity);
        vo.setId(entity.getSiSeq());
        return vo;
    }

    public void deleteSchedule(Long id) {
        ScheduleInfoEntity entity = scheduleInfoRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("존재하지 않는 스케쥴입니다."));
        scheduleInfoRepository.delete(entity);
    }

    public ViewScheduleVO updateSchedule(UpdateScheduleVO data) {
        ScheduleInfoEntity entity = scheduleInfoRepository.findById(data.getId())
                .orElseThrow(() -> new InvalidInputException("존재하지 않는 스케쥴입니다."));
        
        Long uiSeq = entity.getSiUiSeq();
        Long biSeq = entity.getSiBiSeq();
        Integer status = entity.getSiStatus();
        LocalDateTime sDate = null;
        LocalDateTime eDate = null;
        if (data.getStart()!=null) {
            sDate = data.getStart();
        }
        if (data.getEnd()!=null) {
            eDate = data.getEnd();
        }
        if ((data.getStart()!=null && data.getEnd()!=null) && sDate.isAfter(eDate)) {
            throw new InvalidInputException("종료일은 시작일보다 빠를 수 없습니다.");
        }

        ScheduleInfoEntity newEntity = ScheduleInfoEntity.builder()
                .siSeq(data.getId())
                .siContent(data.getSiContent())
                .siStartDate(sDate)
                .siEndDate(eDate)
                .siUiSeq(uiSeq).siBiSeq(biSeq)
                .siStatus(status).build();
        scheduleInfoRepository.save(newEntity);

        ViewScheduleVO responseVO = new ViewScheduleVO();
        responseVO.setId(newEntity.getSiSeq());
        responseVO.setTitle(bookInfoRepository.findByBiSeq(newEntity.getSiBiSeq()).getBiName());
        responseVO.setDescription(newEntity.getSiContent());
        responseVO.setStart(newEntity.getSiStartDate());
        responseVO.setEnd(newEntity.getSiEndDate());
        responseVO.setStatus(status);
        return responseVO;
    }
}
