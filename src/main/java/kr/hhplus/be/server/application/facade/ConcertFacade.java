package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.service.ConcertService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.interfaces.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;
    private final QueueService queueService;

    //콘서트 가져오기
    public List<AvailableReservationConcertResponse> getConcerts(String token){
        queueService.validateToken(token);
        List<Concert> concerts = concertService.getConcerts();
        return concerts.stream().map(AvailableReservationConcertResponse::of).toList();
    }

    //콘서트 스케쥴 리스트 가져오기
    public AvailableReservationConcertDateResponse getConcertSchedules(String token, Long concertId){
        queueService.validateToken(token);
        Concert concert = concertService.getConcert(concertId);
        List<ConcertSchedule> concertSchedules = concertService.getConcertSchedules(concert);

        List<ScheduleDto> scheduleDtoList = concertSchedules.stream().map(ScheduleDto::of).toList();
        return AvailableReservationConcertDateResponse.of(concertId, scheduleDtoList);
    }

    //좌석리스트 결과 가져오기
    public AvailableReservationConcertSeatResponse getConcertSeats(String token, Long concertId, Long concertScheduleId){
        queueService.validateToken(token);
        Concert concert = concertService.getConcert(concertId);
        ConcertSchedule concertScheduleInfo = concertService.getConcertScheduleInfo(concertScheduleId);
        List<Seat> seats = concertService.getSeats(concertScheduleInfo.getConcertId());

        List<SeatDto> seatDtoList = seats.stream().map(SeatDto::of).toList();
        return AvailableReservationConcertSeatResponse.of(concert.getId(), concertScheduleInfo.getConcertTime(), seatDtoList);
    }

}
