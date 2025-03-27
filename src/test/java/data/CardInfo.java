package data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data

public class CardInfo {
    private String number;
    private String month;
    private String year;
    private String holder;
    private String cvc;
}
