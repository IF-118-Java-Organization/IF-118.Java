package com.softserve.itacademy.repository;

import com.softserve.itacademy.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    @Modifying
    @Query(value = "update invitation set approved = true where user_id = ?1 and code = ?2", nativeQuery = true)
    int approve(Integer id, String code);

    Optional<Invitation> findByCode(String code);

    @Modifying
    @Query(value = "insert into groups_users (user_id, group_id) VALUE (?1, ?2)", nativeQuery = true)
    void groupApprove(Integer userId, Integer groupId);

    List<Invitation> findAllByEmail(String email);

    @Modifying
    @Query(value = "delete from invitation where expiration_date < created_at", nativeQuery = true)
    int deleteByExpirationDate();
}
