package org.acme.conferencescheduling.solver;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;
import org.acme.conferencescheduling.domain.ConferenceSchedule;
import org.acme.conferencescheduling.domain.Speaker;
import org.acme.conferencescheduling.domain.Talk;
import org.jspecify.annotations.NonNull;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

public class SpeakerTalksUpdatingVariableListener implements VariableListener<ConferenceSchedule, Talk> {

    @Override
    public void beforeVariableChanged(@NonNull ScoreDirector<ConferenceSchedule> scoreDirector, @NonNull Talk talk) {

    }

    @Override
    public void afterVariableChanged(@NonNull ScoreDirector<ConferenceSchedule> scoreDirector, @NonNull Talk talk) {
        handleChange(scoreDirector, talk);
    }

    private void handleChange(@NonNull ScoreDirector<ConferenceSchedule> scoreDirector, @NonNull Talk talk) {
        scoreDirector.beforeVariableChanged(talk, "timeslot");
        talk.getSpeakers().forEach(
                s-> s.getTimeslots().add(talk.getTimeslot())
        );
        scoreDirector.afterVariableChanged(talk, "timeslot");
    }

    @Override
    public void beforeEntityAdded(@NonNull ScoreDirector<ConferenceSchedule> scoreDirector, @NonNull Talk talk) {

    }

    @Override
    public void afterEntityAdded(@NonNull ScoreDirector<ConferenceSchedule> scoreDirector, @NonNull Talk talk) {
        handleChange(scoreDirector, talk);
    }

    @Override
    public void beforeEntityRemoved(@NonNull ScoreDirector<ConferenceSchedule> scoreDirector, @NonNull Talk talk) {

    }

    @Override
    public void afterEntityRemoved(@NonNull ScoreDirector<ConferenceSchedule> scoreDirector, @NonNull Talk talk) {

    }
}
