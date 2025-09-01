package com.github.madz0.springdeveloperaxon.application;

import com.github.madz0.springdeveloperaxon.domain.model.Conference;
import java.util.List;

public interface ConferenceInfoService {
  List<Conference> getAllConferences();
}
