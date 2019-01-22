/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.assetmgmt.rest;

import java.util.List;

import com.ixortalk.assetmgmt.domain.Asset;
import com.ixortalk.assetmgmt.domain.AssetId;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public interface AssetRepository extends MongoRepository<Asset, AssetId>, AssetRepositoryCustom {

	@Override
    @PreAuthorize("permitAll()")
    @PostFilter("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_SCHEDULED_TASK') or hasAnyRole(filterObject.roles)")
    List<Asset> findAll();

    @Override
    @PreAuthorize("permitAll()")
    @PostFilter("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAnyRole(filterObject.roles)")
    <S extends Asset> List<S> findAll(Example<S> example);

    @Override
    @PreAuthorize("permitAll()")
    @Query("{roles: ?#{ (hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')) ? {$exists:true} : {$in: @securityService.getAuthorities()} }}")
    Page<Asset> findAll(Pageable pageable);
    
    @Override
    @PreAuthorize("permitAll()")
    @PostFilter("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAnyRole(filterObject.roles)")
    <S extends Asset> List<S> findAll(Example<S> example, Sort sort);
    
    @Override
    @PreAuthorize("permitAll()")
    @PostFilter("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAnyRole(filterObject.roles)")
    Iterable<Asset> findAll(Iterable<AssetId> ids);
    
    @Override
    @PreAuthorize("permitAll()")
    @PostFilter("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or hasAnyRole(filterObject.roles)")
    List<Asset> findAll(Sort sort);
    
    
    

    
    @Override
    @PreAuthorize("permitAll()")
    @PostAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or (returnObject!=null and hasAnyRole(returnObject.roles))")
    <S extends Asset> S findOne(Example<S> example);
    
    @Override
    @PreAuthorize("permitAll()")
    @PostAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER') or (returnObject!=null and hasAnyRole(returnObject.roles))")
    Asset findOne(AssetId id);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Iterable<Asset> findByRoles(@Param("role") String role);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    boolean exists(AssetId assetId);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    <S extends Asset> boolean exists(Example<S> example);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    <S extends Asset> S save(S asset);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    <S extends Asset> List<S> save(Iterable<S> entites);
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    <S extends Asset> S insert(S entity);
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    <S extends Asset> List<S> insert(Iterable<S> entities);
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void delete(AssetId assetId);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void delete(Asset entity);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void delete(Iterable<? extends Asset> entities);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteAll();
    
    @Override
    @PreAuthorize("permitAll()")
    long count();
    
    @Override
    @PreAuthorize("permitAll()")
    <S extends Asset> long count(Example<S> example);

}
