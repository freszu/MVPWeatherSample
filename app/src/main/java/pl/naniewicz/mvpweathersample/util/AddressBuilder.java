package pl.naniewicz.mvpweathersample.util;

import pl.naniewicz.mvpweathersample.Constants;

/**
 * Created by Rafal on 2015-07-14.
 */
public class AddressBuilder {

    public static String getIconAddress(String iconName) {
        if (iconName != null) {
            return Constants.ICON_BASE_URL.concat(iconName).concat(".png");
        }
        return null;
    }

}
