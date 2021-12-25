package common.airport;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Airport {
  private Map<UUID, Flight> mapFlightsById;
  private Map<LocalDate, Set<Flight>> mapFlightsByDate;

  private Set<LocalDate> canceledDays;


}
