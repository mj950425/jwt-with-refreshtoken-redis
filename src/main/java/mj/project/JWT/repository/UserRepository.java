package mj.project.JWT.repository;

import mj.project.JWT.model.UserMaster;
import mj.project.JWT.model.dto.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("AU-UserRepository")
public interface UserRepository extends JpaRepository<UserMaster, Long> {
    Optional<UserMaster> findByEmail(String email);

    Boolean existsByEmail(String email);
    @Query("select u from UserMaster u where u.providerId = :providerId and u.provider = :provider")
    Optional<UserMaster> findByProviderIdAndProvider(@Param("providerId") String providerId,
                                                     @Param("provider") AuthProvider provider);
    Optional<UserMaster> findByUserUuid(String userUuid);
}

