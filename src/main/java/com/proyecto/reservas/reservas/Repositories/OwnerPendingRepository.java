package com.proyecto.reservas.reservas.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.reservas.reservas.Models.OwnerDetailsModel;



@Repository
public interface OwnerPendingRepository extends JpaRepository<OwnerDetailsModel, String> {
        
}
