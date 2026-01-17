package com.plaintext.core.repository;

import com.plaintext.common.model.Follow;
import com.plaintext.common.model.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    // Count for profile stats
    long countByFollowingId(UUID userId); // How many followers I have
    long countByFollowerId(UUID userId); // How many people I follow
}