package tqs.g11.deliverize.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    @Getter
    @Setter
    private Double rating;

    @Getter
    @Setter
    private Long orderId;
}
