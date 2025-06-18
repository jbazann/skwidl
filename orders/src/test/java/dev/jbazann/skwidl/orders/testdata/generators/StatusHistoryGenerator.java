package dev.jbazann.skwidl.orders.testdata.generators;

import dev.jbazann.skwidl.commons.testing.SeededUUIDGenerator;
import dev.jbazann.skwidl.orders.order.entities.Order;
import dev.jbazann.skwidl.orders.order.entities.StatusHistory;

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
        initialHistory.add(stdHistory(order.getOrdered())
                .setStatus(StatusHistory.Status.ACCEPTED)// Redundant but consistent.
        );
        return order.setStatusHistory(initialHistory);
    }

    protected Order initPreparation(Order order) {
        final List<StatusHistory> initialHistory = new ArrayList<>();
        initialHistory.add(stdHistory(order.getOrdered())
                .setStatus(StatusHistory.Status.IN_PREPARATION)
        );
        return order.setStatusHistory(initialHistory);
    }

    protected Order initRejected(Order order) {
        final List<StatusHistory> initialHistory = new ArrayList<>();
        initialHistory.add(stdHistory(order.getOrdered())
                .setStatus(StatusHistory.Status.REJECTED)
        );
        return order.setStatusHistory(initialHistory);
    }

    protected Order initCanceled(Order order) {
        final DateCarrier date = new DateCarrier(order.getOrdered());
        final List<StatusHistory> beforeCanceled = switch (random.nextInt(3)) {
            case 0 -> List.of(stdHistory(date.next()).setStatus(StatusHistory.Status.IN_PREPARATION));
            case 1 -> List.of(
                    stdHistory(date.next()).setStatus(StatusHistory.Status.ACCEPTED),
                    stdHistory(date.next()).setStatus(StatusHistory.Status.IN_PREPARATION)
            );
            default -> List.of(stdHistory(date.next()).setStatus(StatusHistory.Status.ACCEPTED));
        };
        final List<StatusHistory> initialHistory = new ArrayList<>(beforeCanceled);
        initialHistory.add(stdHistory(randomDateSince(date.next())).setStatus(StatusHistory.Status.CANCELED));
        return order.setStatusHistory(initialHistory);
    }

    protected Order initDelivered(Order order) {
        final DateCarrier date = new DateCarrier(order.getOrdered());
        //noinspection SwitchStatementWithTooFewBranches
        final List<StatusHistory> beforeDelivered = switch (random.nextInt(2)) {
            case 0 -> List.of(
                    stdHistory(date.next()).setStatus(StatusHistory.Status.ACCEPTED),
                    stdHistory(date.next()).setStatus(StatusHistory.Status.IN_PREPARATION)
            );
            default -> List.of(stdHistory(date.next()).setStatus(StatusHistory.Status.IN_PREPARATION));
        };
        final List<StatusHistory> initialHistory = new ArrayList<>(beforeDelivered);
        initialHistory.add(stdHistory(randomDateSince(date.next())).setStatus(StatusHistory.Status.DELIVERED));
        return order.setStatusHistory(initialHistory);
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
                .setId(uuid.next())
                .setDate(dateTime)
                .setDetail(detail)
                .setStatus(StatusHistory.Status.ACCEPTED);
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
