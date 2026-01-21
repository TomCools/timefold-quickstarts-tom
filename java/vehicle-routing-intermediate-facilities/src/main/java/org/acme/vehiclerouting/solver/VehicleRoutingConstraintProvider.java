package org.acme.vehiclerouting.solver;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;

import org.acme.vehiclerouting.domain.Visit;
import org.acme.vehiclerouting.domain.Vehicle;

public class VehicleRoutingConstraintProvider implements ConstraintProvider {

    public static final String VEHICLE_CAPACITY = "vehicleCapacity";
    public static final String MAXIMIZE_VISITS_ASSIGNED = "maximizeVisitsAssigned";
    public static final String SERVICE_FINISHED_AFTER_MAX_END_TIME = "serviceFinishedAfterMaxEndTime";
    public static final String MINIMIZE_TRAVEL_TIME = "minimizeTravelTime";

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                // Hard
                vehicleCapacity(factory),
                serviceFinishedAfterMaxEndTime(factory),
                noFacilityForFirstVisit(factory),

                // Medium
                maximizeVisitsAssigned(factory),

                // Soft
                minimizeTravelTime(factory)
        };
    }

    private Constraint noFacilityForFirstVisit(ConstraintFactory factory) {
        return factory.forEachIncludingUnassigned(Visit.class)
                .filter(visit -> visit.getPreviousVisit() == null && visit.getFacilityStopBefore() != null)
                .penalizeLong(HardMediumSoftLongScore.ONE_HARD)
                .asConstraint("SHOULD_NOT_VISIT");
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    protected Constraint vehicleCapacity(ConstraintFactory factory) {
        return factory.forEachIncludingUnassigned(Visit.class)
                .filter(visit -> visit.getVehicle() != null
                        && visit.getTotalUsedCapacity() > visit.getVehicle().getCapacity())
                .penalizeLong(HardMediumSoftLongScore.ONE_HARD,
                        visit -> visit.getTotalUsedCapacity() - visit.getVehicle().getCapacity())
                .asConstraint(VEHICLE_CAPACITY);
    }

    protected Constraint serviceFinishedAfterMaxEndTime(ConstraintFactory factory) {
        return factory.forEach(Visit.class)
                .filter(Visit::isServiceFinishedAfterMaxEndTime)
                .penalizeLong(HardMediumSoftLongScore.ONE_HARD,
                        Visit::getServiceFinishedDelayInMinutes)
                .asConstraint(SERVICE_FINISHED_AFTER_MAX_END_TIME);
    }

    // ************************************************************************
    // Medium constraints
    // ************************************************************************

    protected Constraint maximizeVisitsAssigned(ConstraintFactory factory) {
        return factory.forEachIncludingUnassigned(Visit.class)
                .filter(v -> v.getVehicle() == null)
                .penalizeLong(HardMediumSoftLongScore.ONE_MEDIUM, v-> v.getServiceDuration().toMinutes())
                .asConstraint(MAXIMIZE_VISITS_ASSIGNED);
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    protected Constraint minimizeTravelTime(ConstraintFactory factory) {
        return factory.forEachIncludingUnassigned(Visit.class)
                .filter(visit -> visit.getVehicle() != null)
                .groupBy(Visit::getVehicle, ConstraintCollectors.sumLong(Visit::getDrivingTimeSecondsFromPreviousNonFacilityStandstill))
                .penalizeLong(HardMediumSoftLongScore.ONE_SOFT, (vehicle, totalDrivingTime) -> {
                    if (vehicle.getVisits().isEmpty()) {
                        return totalDrivingTime;
                    }
                    Visit lastVisit = vehicle.getVisits().get(vehicle.getVisits().size() - 1);
                    long returnHomeTime = lastVisit.getLocation().getDrivingTimeTo(vehicle.getHomeLocation());
                    return totalDrivingTime + returnHomeTime;
                })
                .asConstraint(MINIMIZE_TRAVEL_TIME);
    }
}
