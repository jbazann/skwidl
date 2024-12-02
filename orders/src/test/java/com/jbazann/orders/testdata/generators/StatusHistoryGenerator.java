package com.jbazann.orders.testdata.generators;

import com.jbazann.orders.order.entities.Order;
import com.jbazann.orders.order.entities.StatusHistory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StatusHistoryGenerator {

    private final Random random;
    private final SeededUUIDGenerator uuid;
    private final String detail = "This is a test StatusHistory detail.";

    protected StatusHistoryGenerator(Random random, SeededUUIDGenerator uuid) {
        this.random = random;
        this.uuid = uuid;
    }

    protected Order initAccepted(Order order) {
        final List<StatusHistory> initialHistory = new ArrayList<>();
        initialHistory.add(stdHistory(order.ordered())
                .status(StatusHistory.Status.ACCEPTED)// Redundant but consistent.
        );
        return order.statusHistory(initialHistory);
    }

    protected Order initPreparation(Order order) {
        final List<StatusHistory> initialHistory = new ArrayList<>();
        initialHistory.add(stdHistory(order.ordered())
                .status(StatusHistory.Status.IN_PREPARATION)
        );
        return order.statusHistory(initialHistory);
    }

    protected Order initRejected(Order order) {
        final List<StatusHistory> initialHistory = new ArrayList<>();
        initialHistory.add(stdHistory(order.ordered())
                .status(StatusHistory.Status.REJECTED)
        );
        return order.statusHistory(initialHistory);
    }

    protected Order initCanceled(Order order) {
        final DateCarrier date = new DateCarrier(order.ordered());
        final List<StatusHistory> beforeCanceled = switch (random.nextInt(3)) {
            case 0 -> List.of(stdHistory(date.next()).status(StatusHistory.Status.IN_PREPARATION));
            case 1 -> List.of(
                    stdHistory(date.next()).status(StatusHistory.Status.ACCEPTED),
                    stdHistory(date.next()).status(StatusHistory.Status.IN_PREPARATION)
            );
            default -> List.of(stdHistory(date.next()).status(StatusHistory.Status.ACCEPTED));
        };
        final List<StatusHistory> initialHistory = new ArrayList<>(beforeCanceled);
        initialHistory.add(stdHistory(randomDateSince(date.next())).status(StatusHistory.Status.CANCELED));
        return order.statusHistory(initialHistory);
    }

    protected Order initDelivered(Order order) {
        final DateCarrier date = new DateCarrier(order.ordered());
        final List<StatusHistory> beforeDelivered = switch (random.nextInt(2)) {
            case 0 -> List.of(
                    stdHistory(date.next()).status(StatusHistory.Status.ACCEPTED),
                    stdHistory(date.next()).status(StatusHistory.Status.IN_PREPARATION)
            );
            default -> List.of(stdHistory(date.next()).status(StatusHistory.Status.IN_PREPARATION));
        };
        final List<StatusHistory> initialHistory = new ArrayList<>(beforeDelivered);
        initialHistory.add(stdHistory(randomDateSince(date.next())).status(StatusHistory.Status.DELIVERED));
        return order.statusHistory(initialHistory);
    }

    /**
     * May return an equivalent {@link LocalDateTime}, but none later than {@link LocalDateTime#now()}.
     */
    private LocalDateTime randomDateSince(LocalDateTime since) {
        final long minutesSince = since.until(LocalDateTime.now(), ChronoUnit.MINUTES);
        return (minutesSince == 0) ? LocalDateTime.now() : since.plusMinutes(random.nextLong(minutesSince));
    }

    private StatusHistory stdHistory(LocalDateTime dateTime) {
        return  new StatusHistory()
                .id(uuid.next())
                .date(dateTime)
                .detail(detail)
                .status(StatusHistory.Status.ACCEPTED);
    }


    /**
     * This class supports the creation of chronologically ordered StatusHistory entries
     * with simple syntax. Calling {@link DateCarrier#next()}
     * for the first time returns the date passed in the constructor, and on future calls, a
     * {@link StatusHistoryGenerator#randomDateSince(LocalDateTime)} since the last returned date,
     * while also storing said date for subsequent calls.
     * This was made for fun, there may be better ways to go about this.
     */
    private class DateCarrier {
        private LocalDateTime date;
        private boolean firstCall = true;
        private DateCarrier(LocalDateTime date) {
            this.date = date;
        }

        /**
         * Read comment at class level. 👍
         */
        private LocalDateTime next() {
            if(firstCall) {
                firstCall = false;
                return date;
            }
            final LocalDateTime next = randomDateSince(date);
            date = next;
            return next;
        }
    }

}
