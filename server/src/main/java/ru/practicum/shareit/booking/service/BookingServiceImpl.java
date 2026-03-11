package ru.practicum.shareit.booking.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingResponseDto addBooking(Long bookerId, BookingCreateDto bookingCreateDto) {
        if (bookingCreateDto.getStart().isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
            log.error("Попытка бронирования. Время начала бронирования указано в прошлом.");
            throw new ValidationException("Начало бронирования не может быть в прошлом.");
        }

        if (bookingCreateDto.getEnd().isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
            log.error("Попытка бронирования. Время окончания бронирования указано в прошлом.");
            throw new ValidationException("Окончание бронирования не может быть в прошлом.");
        }

        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())) {
            log.error("Попытка бронирования. " +
                    "Время окончания бронирования указано ранее, чем время начала бронирования.");
            throw new ValidationException("Окончание бронирования не может быть раньше его начала.");
        }

        if (bookingCreateDto.getEnd().isEqual(bookingCreateDto.getStart())) {
            log.error("Попытка бронирования. " +
                    "Время начала и окончания бронирования совпадают.");
            throw new ValidationException("Начало и окончание бронирования не могут совпадать по времени.");
        }

        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(
                "Добавление бронирования. Пользователь с ID: " + bookerId + " не найден."));

        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(() -> new NotFoundException(
                "Добавление бронирования. Вещь с ID: " + bookingCreateDto.getItemId() + " не найдена."));

        if (item.getAvailable() == false) {
            log.error("Попытка бронирования вещи недоступной для бронирования.");
            throw new ValidationException("Данная вещь недоступна для бронирования.");
        }

        item.setAvailable(false);
        Booking addedBooking = BookingMapper.dtoToBookingForCreate(bookingCreateDto, item, booker);
        bookingRepository.save(addedBooking);
        return BookingMapper.bookingToDtoForResponse(addedBooking);
    }

    @Transactional
    @Override
    public BookingResponseDto approveBooking(Long approverId, Long bookingId, String approved) {
        User user = userRepository.findById(approverId).orElseThrow(() -> new ValidationException(
                "Подтверждение бронирования. Пользователь с ID: " + approverId + " не найден."));

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                "Подтверждение бронирования. Бронирование с ID: " + bookingId + " не найдено."));

        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException(
                "Подтверждение бронирования. Вещь с ID: " + booking.getItem().getId() + " не найдена."));

        if (approverId.equals(item.getOwnerId())) {
            if ("true".equals(approved)) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            log.error("Попытка подтверждения бронирования вещи не ее владельцем.");
            throw new ValidationException("Подтвердить бронирование может только владелец вещи.");
        }
        bookingRepository.save(booking);
        return BookingMapper.bookingToDtoForResponse(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                "Получение бронирования по ID. Бронирование с ID: " + bookingId + " не найдено."));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException(
                "Получение бронирования по ID. Вещь с ID: " + booking.getItem().getId() + " не найдена."));
        if (booking.getBooker().getId().equals(userId) || item.getOwnerId().equals(userId)) {
            return BookingMapper.bookingToDtoForResponse(booking);
        } else {
            log.error("Попытка просмотра данных бронирования не ее владельцем или не забронировавшим пользователем.");
            throw new ValidationException("Посмотреть данные бронирования может только владелец вещи " +
                    "или пользователь, совершивший бронирование.");
        }
    }

    @Override
    public List<BookingResponseDto> getBookingsMadeByUser(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Получение всех бронирований пользователя. Пользователь с ID: " + userId + " не найден."));
        switch (state.toUpperCase()) {
            case "CURRENT":
                List<Booking> currentBookings = bookingRepository.findByBookerIdWhereNowBetweenStartAndEnd(userId,
                    LocalDateTime.now(), Sort.by("start").descending());
                return currentBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "PAST":
                List<Booking> pastBookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        Sort.by("start").descending());
                return pastBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "FUTURE":
                List<Booking> futureBookings = bookingRepository.findByBookerIdAndStartIsAfter(userId,
                        LocalDateTime.now(), Sort.by("start").descending());
                return futureBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "WAITING":
                List<Booking> waitingBookings = bookingRepository.findByBookerIdAndStatus(userId,
                        BookingStatus.WAITING, Sort.by("start").descending());
                return waitingBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "REJECTED":
                List<Booking> rejectedBookings = bookingRepository.findByBookerIdAndStatus(userId,
                        BookingStatus.REJECTED, Sort.by("start").descending());
                return rejectedBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "ALL":
                List<Booking> allBookings = bookingRepository.findAllByBookerId(userId,
                        Sort.by("start").descending());
                return allBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            default: throw new ValidationException("Неверно задано значение статуса.");
        }
    }

    @Override
    public List<BookingResponseDto> getBookingsOfUserItems(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Получение бронирований всех вещей пользователя. Пользователь с ID: " + userId + " не найден."));
        switch (state.toUpperCase()) {
            case "CURRENT":
                List<Booking> currentBookings = bookingRepository.findItemsOfBookerWhereCurrentStatus(userId,
                        LocalDateTime.now(), Sort.by("start").descending());
                return currentBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "PAST":
                List<Booking> pastBookings = bookingRepository.findItemsOfBookerWherePastStatus(userId,
                        LocalDateTime.now(), Sort.by("start").descending());
                return pastBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "FUTURE":
                List<Booking> futureBookings = bookingRepository.findItemsOfBookerWhereFutureStatus(userId,
                        LocalDateTime.now(), Sort.by("start").descending());
                return futureBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "WAITING":
                List<Booking> waitingBookings = bookingRepository.findItemsOfBookerWhereWaitingStatus(userId,
                        BookingStatus.WAITING, Sort.by("start").descending());
                return waitingBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "REJECTED":
                List<Booking> rejectedBookings = bookingRepository.findItemsOfBookerWhereRejectedStatus(userId,
                        BookingStatus.REJECTED, Sort.by("start").descending());
                return rejectedBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            case "ALL":
                List<Booking> allBookings = bookingRepository.findAllItemsOfBooker(userId,
                        Sort.by("start").descending());
                return allBookings.stream()
                        .map(BookingMapper::bookingToDtoForResponse)
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Неверно задано значение статуса.");
        }
    }

}