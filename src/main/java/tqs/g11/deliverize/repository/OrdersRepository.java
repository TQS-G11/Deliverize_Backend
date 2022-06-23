package tqs.g11.deliverize.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {
    @Query(
            "select order from Order order where " +
                    "(:id is null or order.id = :id) and "
                    + "(:company is null or order.company = :company) and "
                    + "(:rider is null or order.rider = :rider) and "
                    + "(:buyer is null or order.buyer = :buyer) and "
                    + "(:destination is null or order.destination = :destination) and "
                    + "(:notes is null or order.notes = :notes) and "
                    + "(:deliveryStatus is null or order.deliveryStatus = :deliveryStatus) and "
                    + "(:origin is null or order.origin like :origin) and"
                    + "(:price is null or order.price = :price) and"
                    + "(:requestedAt is null or order.requestedAt = :requestedAt) and"
                    + "(:acceptedAt is null or order.acceptedAt = :acceptedAt) and"
                    + "(:deliveredAt is null or order.deliveredAt = :deliveredAt)"
    )
    List<Order> findOrders(
            @Param("id") Long id,
            @Param("company") User company,
            @Param("rider") User rider,
            @Param("buyer") String buyer,
            @Param("destination") String destination,
            @Param("notes") String notes,
            @Param("deliveryStatus") String deliveryStatus,
            @Param("origin") String origin,
            @Param("price") Double price,
            @Param("requestedAt") LocalDateTime requestedAt,
            @Param("acceptedAt") LocalDateTime acceptedAt,
            @Param("deliveredAt") LocalDateTime deliveredAt
    );

    List<Order> getOrdersByRiderEqualsAndDeliveryStatusIn(User rider, Collection<String> statuses);
}