package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b " +
            "where b.booker = ?1 " +
            "and ?2 between b.start and b.end")
    List<Booking> findByBookerIdWhereNowBetweenStartAndEnd(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.ownerId = ?1 " +
            "and ?2 between b.start and b.end")
    List<Booking> findItemsOfBookerWhereCurrentStatus(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.ownerId = ?1 " +
            "and ?2 > b.end")
    List<Booking> findItemsOfBookerWherePastStatus(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.ownerId = ?1" +
            "and ?2 < b.start")
    List<Booking> findItemsOfBookerWhereFutureStatus(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.ownerId = ?1" +
            "and b.status = ?2 ")
    List<Booking> findItemsOfBookerWhereWaitingStatus(Long bookerId, BookingStatus status, Sort sort);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.ownerId = ?1" +
            "and b.status = ?2 ")
    List<Booking> findItemsOfBookerWhereRejectedStatus(Long bookerId, BookingStatus status, Sort sort);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.ownerId = ?1")
    List<Booking> findAllItemsOfBooker(Long bookerId, Sort sort);

    @Query("select b from Booking b " +
           "where b.item.id = ?1 " +
           "and b.end <= ?2 " +
           "order by b.end desc")
    List<Booking> findByItemIdLastBooking(Long itemId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
           "where b.item.id = ?1 " +
           "and b.start >= ?2 " +
           "order by b.start")
    List<Booking> findByItemIdNextBooking(Long itemId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
           "where b.item.id = ?1 " +
           "and b.booker.id = ?2 " +
           "and b.end <= current_timestamp " +
           "order by b.end desc")
    List<Booking> findBookingForComment(Long itemId, Long bookerId, Pageable pageable);

}