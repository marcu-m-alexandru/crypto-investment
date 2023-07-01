package com.faptic.cryptoinvestment;

import com.faptic.cryptoinvestment.app.Statistics;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class GlobalState {

  private HashMap<String, HashMap<TimePeriod, ArrayList<Statistics>>> cryptoMap = new HashMap<>();

}
