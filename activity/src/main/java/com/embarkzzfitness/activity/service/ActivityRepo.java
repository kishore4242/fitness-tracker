package com.embarkzzfitness.activity.service;

import com.embarkzzfitness.activity.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepo extends MongoRepository<Activity, String> {
    List<Activity> findByUserId(String userId);
}
