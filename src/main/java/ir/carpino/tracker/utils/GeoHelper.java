package ir.carpino.tracker.utils;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.CartesianDistCalc;
import org.locationtech.spatial4j.distance.DistanceUtils;

public class GeoHelper {

    private final CartesianDistCalc geoCalculator = new CartesianDistCalc();
    private final SpatialContext ctx = SpatialContext.GEO;

    public double distanceFromKM(double latSrc, double lonSrc, double latDst, double lonDst) {
        try {
            return DistanceUtils.DEG_TO_KM * geoCalculator.distance(
                    ctx.getShapeFactory().pointXY(latSrc, lonSrc), latDst, lonDst);
        } catch (Exception ex) {
            return Double.MAX_VALUE;
        }
    }
}
