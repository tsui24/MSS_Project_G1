package com.hotel.auth.repository;

import com.hotel.auth.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {}
