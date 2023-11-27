package backend.util;

import backend.entity.UserEntity;
import io.ipgeolocation.api.Geolocation;
import io.ipgeolocation.api.IPGeolocationAPI;
import org.springframework.stereotype.Component;

import static backend.constants.OtherConst.GEOLOCATION_API_KEY;

@Component
public class LocationInfo {

    public void setUserLocations(UserEntity userEntity){
        Geolocation geolocation = new IPGeolocationAPI(GEOLOCATION_API_KEY).getGeolocation();
        userEntity.setCountry(geolocation.getCountryName());
        userEntity.setCity(geolocation.getCity());
        userEntity.setCurrency(geolocation.getCurrency().getCode());
    }
}
