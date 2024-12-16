package ng.org.mirabilia.pms.repositories;

import ng.org.mirabilia.pms.domain.entities.Phase;
import ng.org.mirabilia.pms.domain.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    List<State> findByNameContainingIgnoreCaseOrStateCodeContainingIgnoreCase(String name, String stateCode);
    boolean existsByName(String name);
    boolean existsByStateCode(String stateCode);
    Optional<State> findByName(String name);

}
