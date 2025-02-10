package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.service.ConcertService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.interfaces.dto.concert.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;
    private final QueueService queueService;

    //콘서트 가져오기
    @Cacheable(value = "concert", key = "'concert'", cacheManager = "redisCacheManager")
    public List<ConcertHttpDto.AvailableReservationConcertResponse> getConcerts(){
        List<Concert> concerts = concertService.getConcerts();
        return concerts.stream().map(ConcertHttpDto.AvailableReservationConcertResponse::of).toList();
    }

    //콘서트 스케쥴 리스트 가져오기
    @Cacheable(value = "scheduleByConcertId", key = "#concertId", cacheManager = "redisCacheManager")
    public ConcertHttpDto.AvailableReservationConcertDateResponse getConcertSchedules(Long concertId){
        Concert concert = concertService.getConcert(concertId);
        List<ConcertSchedule> concertSchedules = concertService.getConcertSchedules(concert);

        List<ConcertHttpDto.ScheduleDto> scheduleDtoList = concertSchedules.stream().map(ConcertHttpDto.ScheduleDto::of).toList();
        return ConcertHttpDto.AvailableReservationConcertDateResponse.of(concertId, scheduleDtoList);
    }

    //좌석리스트 결과 가져오기
    @Cacheable(value = "seatByScheduleId", key = "#concertScheduleId" , cacheManager = "redisCacheManager")
    public ConcertHttpDto.AvailableReservationConcertSeatResponse getConcertSeats(Long concertId, Long concertScheduleId){
        Concert concert = concertService.getConcert(concertId);
        ConcertSchedule concertScheduleInfo = concertService.getConcertScheduleInfo(concertScheduleId);
        List<Seat> seats = concertService.getSeats(concertScheduleInfo.getConcertId());

        List<ConcertHttpDto.SeatDto> seatDtoList = seats.stream().map(ConcertHttpDto.SeatDto::of).toList();
        return ConcertHttpDto.AvailableReservationConcertSeatResponse.of(concert.getId(), concertScheduleInfo.getConcertTime(), seatDtoList);
    }

}
