package com.schemaworkflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.schemaworkflow.entity.FormSchema;

public interface FormSchemaRepository extends JpaRepository<FormSchema, String> {

    List<FormSchema> findByIsActiveTrue();
}