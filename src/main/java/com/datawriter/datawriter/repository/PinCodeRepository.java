package com.datawriter.datawriter.repository;

import com.datawriter.datawriter.domain.PinCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PinCodeRepository extends JpaRepository<PinCode,Integer> {

}
