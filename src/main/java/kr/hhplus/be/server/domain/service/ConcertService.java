package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    //콘서트 리스트 조회
    public List<Concert> getConcerts(){
        return concertRepository.findConcertsAll();
    }

    //단일 콘서트 검색 조회
    public Concert getConcert(Long concertId){
        return concertRepository.findConcertByConcertId(concertId);
    }

    //콘서트 스케쥴 리스트 조회
    public List<ConcertSchedule> getConcertSchedules(Concert concert){

        if (!concert.checkStatus()) {
            return null;
        }
        return concertRepository.findConcertSchedulesAllByConcertId(concert.getId());
    }

    //특정 스케쥴 정보 조회
    public ConcertSchedule getConcertScheduleInfo(Long concertScheduleId){
        return concertRepository.findConcertScheduleByConcertScheduleId(concertScheduleId);
    }

    //좌석 리스트 조회
    public List<Seat> getSeats(Long concertScheduleId){
        return concertRepository.findSeatsAllByConcertScheduleId(concertScheduleId);
    }

    //예약 가능 좌석 인지 확인
    public void isAvailableReservationSeat(ConcertSchedule concertSchedule, Seat seat){
        concertSchedule.checkStatus();
        seat.checkStatus();
    }

    //좌석 배정
    public void assignSeat(Seat seat){
        Seat assignedSeat = seat.assign();
        concertRepository.saveSeat(assignedSeat);
    }

}
