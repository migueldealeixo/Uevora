package com.t2.t2.Admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.t2.t2.entitys.Admin;

public interface  AdminRepo  extends  JpaRepository<Admin, String>{
    
}
