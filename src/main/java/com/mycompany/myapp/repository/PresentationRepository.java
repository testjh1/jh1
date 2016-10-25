package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Presentation;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Presentation entity.
 */
@SuppressWarnings("unused")
public interface PresentationRepository extends JpaRepository<Presentation,Long> {

    @Query("select distinct presentation from Presentation presentation left join fetch presentation.users")
    List<Presentation> findAllWithEagerRelationships();

    @Query("select presentation from Presentation presentation left join fetch presentation.users where presentation.id =:id")
    Presentation findOneWithEagerRelationships(@Param("id") Long id);

}
