package com.plaintext.core.repository;

import com.plaintext.common.model.Post;
import com.plaintext.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByUserUsernameOrderByCreatedAtDesc(String username);
    Post findTopByUserOrderByCreatedAtDesc(User user);
}
