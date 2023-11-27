package backend.util;

import backend.entity.UserEntity;
import io.ipgeolocation.api.Geolocation;
import io.ipgeolocation.api.IPGeolocationAPI;
import org.springframework.stereotype.Component;

import static backend.constants.OtherConst.GEOLOCATION_API_KEY;

@Component
public class LocationInfo {

    /**
     * Sets the location information for the provided user entity based on the user's IP address.
     * The information includes country, city, and currency.
     *
     * @param userEntity The user entity for which location information is to be set.
     */
    public void setUserLocations(UserEntity userEntity){
        Geolocation geolocation = new IPGeolocationAPI(GEOLOCATION_API_KEY).getGeolocation();
        userEntity.setCountry(geolocation.getCountryName());
        userEntity.setCity(geolocation.getCity());
        userEntity.setCurrency(geolocation.getCurrency().getCode());
    }
}
