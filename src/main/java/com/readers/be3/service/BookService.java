package com.readers.be3.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.readers.be3.entity.BookInfoEntity;
import com.readers.be3.entity.ScheduleInfoEntity;
import com.readers.be3.repository.BookInfoRepository;
import com.readers.be3.repository.ScheduleInfoRepository;
import com.readers.be3.vo.book.InvalidInputException;
import com.readers.be3.vo.book.PatchBookStatusVO;
import com.readers.be3.vo.book.BookInfoAladinVO;
import com.readers.be3.vo.book.GetBookListVO;
import com.readers.be3.vo.book.GetMyBookVO;
import com.readers.be3.vo.book.ResponseBookInfoVO;
import com.readers.be3.vo.response.BasicResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookInfoRepository bookInfoAladinRepository;
    private final ScheduleInfoRepository scheduleInfoRepository;
    // @Value("${file.image.book}") String book_img_path;

    public ResponseBookInfoVO addBookInfo(BookInfoAladinVO data) {
        if (bookInfoAladinRepository.findByBiIsbnEquals(data.getBiIsbn())==null) {
            BookInfoEntity entity = BookInfoEntity.builder()
                    .biName(data.getBiName())
                    .biAuthor(data.getBiAuthor())
                    .biPublisher(data.getBiPublisher())
                    .biPage(data.getBiPage())
                    .biIsbn(data.getBiIsbn())
                    .biUri(data.getBimgUri()).build();
            bookInfoAladinRepository.save(entity);
            ResponseBookInfoVO vo = new ResponseBookInfoVO(data);
            vo.setBiSeq(entity.getBiSeq());
            return vo;
        }
        else {
            ResponseBookInfoVO vo = new ResponseBookInfoVO(bookInfoAladinRepository.findByBiIsbnEquals(data.getBiIsbn()));
            return vo;
        }
    }

    public GetMyBookVO getMyBookList(Long uiSeq, Integer status) {
        List<GetBookListVO> list = new ArrayList<GetBookListVO>();
        if (status==0) {
            for (ScheduleInfoEntity entity : scheduleInfoRepository.findBySiUiSeqOrderBySiSeqDesc(uiSeq)) {
                GetBookListVO vo = new GetBookListVO(entity);
                list.add(vo);
            }
        }
        else if (status==4) {
            for (ScheduleInfoEntity entity : scheduleInfoRepository.findBySiUiSeqAndSiStatus(uiSeq, status)) {
                GetBookListVO vo = new GetBookListVO(entity);
                list.add(vo);
            }
        }
        else if (status==9) {
            List<Integer> statusList = Arrays.asList(1, 3);
            for (ScheduleInfoEntity entity : scheduleInfoRepository.findBySiUiSeqAndSiStatusInOrderBySiSeqDesc(uiSeq, statusList)) {
                GetBookListVO vo = new GetBookListVO(entity);
                list.add(vo);
            }
        }
        return new GetMyBookVO(uiSeq, list);
    }

    public BasicResponse patchBookStatus(PatchBookStatusVO data) {
        if (scheduleInfoRepository.findById(data.getId()).isEmpty()) {
            throw new InvalidInputException("??? ????????? ???????????? ?????? ????????????.");
        }
        else if (scheduleInfoRepository.findById(data.getId()).get().getSiStatus()==data.getStatus()) {
            throw new InvalidInputException("?????? ????????? ??????????????????.");
        }
        else {
            ScheduleInfoEntity entity = scheduleInfoRepository.findById(data.getId()).get();
            ScheduleInfoEntity newEntity = ScheduleInfoEntity.builder()
                    .siSeq(entity.getSiSeq())
                    .siContent(entity.getSiContent())
                    .siStartDate(entity.getSiStartDate())
                    .siEndDate(entity.getSiEndDate())
                    .siUiSeq(entity.getSiUiSeq())
                    .siBiSeq(entity.getSiBiSeq())
                    .siStatus(data.getStatus()).build();
            scheduleInfoRepository.save(newEntity);
        }
        return new BasicResponse("true", "???????????? ??????????????????.");
    }


    
    public List<ResponseBookInfoVO> searchBookInfo(String keyword, Integer sortNo) {
        List<ResponseBookInfoVO> list = new ArrayList<ResponseBookInfoVO>();
        Sort sort = sortBySortNo(sortNo);
        if (bookInfoAladinRepository.findByBiNameContainsOrBiAuthorContainsOrBiPublisherContains(keyword, keyword, keyword, sort).size()==0) {
            throw new NoSuchElementException();
        }
        else {
            for (BookInfoEntity data : bookInfoAladinRepository.findByBiNameContainsOrBiAuthorContainsOrBiPublisherContains(keyword, keyword, keyword, sort)) {
                ResponseBookInfoVO vo = new ResponseBookInfoVO(data);
                list.add(vo);
            }
        }
        return list;
    }

    private Sort sortBySortNo(Integer sortNo) {
        if (sortNo==null || sortNo==1) {
            return Sort.by(Sort.Direction.DESC, "biSeq");
        }
        else if (sortNo==2) {
            return Sort.by(Sort.Direction.DESC, "biName");
        }
        else {
            throw new InvalidInputException("???????????? ?????? ???????????? ?????????.");
        }
    }

    // public Map<String, Object> addBookImg(MultipartFile file) {
    //     Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

    //     String originalFileName = file.getOriginalFilename();
    //     String[] split = originalFileName.split("\\.");
    //     String ext = split[split.length - 1];
    //     String filename = "";
    //     for (int i=0; i<split.length-1; i++) {
    //         filename += split[i];
    //     }
    //     String saveFilename = "book_" + Calendar.getInstance().getTimeInMillis() + "." + ext;
        
    //     Path forderLocation = Paths.get(book_img_path);
    //     Path targetFile = forderLocation.resolve(saveFilename);
        
    //     try {
    //         Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
    //     }
    //     catch (Exception e) {
    //         resultMap.put("status", false);
    //         resultMap.put("message", "?????? ????????? ??????????????????..");
    //         resultMap.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
    //         return resultMap;
    //     }

    //     BookImgEntity data = BookImgEntity.builder()
    //             .bimgFilename(saveFilename)
    //             .bimgUri(filename).build();
        
    //     bookImgRepository.save(data);

    //     resultMap.put("status", true);
    //     resultMap.put("message", "????????? ??? ???????????? ??????????????????.");
    //     resultMap.put("code", HttpStatus.OK);
    //     return resultMap;
    // }

    // public String getFilenameByUri(String uri) {
    //     BookImgEntity img = bookImgRepository.findTopByBimgUri(uri);
    //     if (img==null) {
    //         throw new InvalidInputException("???????????? ?????? ???????????????.");
    //     }
    //     return img.getBimgFilename();
    // }
}
