package com.kkinikong.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.store.domain.Store;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {}
