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
import java.util.Map;

import javax.inject.Inject;

import com.ixortalk.assetmgmt.domain.Asset;
import com.ixortalk.assetmgmt.rest.dto.BulkUpdate;
import org.springframework.data.domain.Example;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.google.common.collect.Iterables.size;
import static com.ixortalk.assetmgmt.domain.Asset.assetWithProperties;
import static com.ixortalk.assetmgmt.domain.AssetId.assetId;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RepositoryRestController
@RequestMapping("/assets")
public class AssetRepositoryImpl implements AssetRepositoryCustom {

    @Inject
    private AssetRepository assetRepository;

    @GetMapping("/search/property/{key}/{value}")
    @ResponseBody
    public List<Asset> searchByProps(@PathVariable String key, @PathVariable String value) {
        return assetRepository.findAll(Example.of(Asset.assetWith(key, value)));
    }


    @PostMapping(value = "/find/property", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Asset findOne(@RequestBody Map<String, Object> assetProperties) throws NotFoundException, BadRequestException {
        Example<Asset> example = Example.of(assetWithProperties(assetProperties));
        List<Asset> assets = assetRepository.findAll(example); //no count : findall is security filtered
        if (assets.size() == 0) {
            throw new NotFoundException();
        }
        if (assets.size() > 1) {
            throw new BadRequestException();
        }

        return assets.get(0);
    }

    @PutMapping(value = "/{id}/properties", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProperties(@PathVariable String id, @RequestBody Map<String, Object> newProperties) {
        return ofNullable(assetRepository.findOne(assetId(id)))
                .map(existingAsset -> {
                    existingAsset.getAssetProperties().putAll(newProperties);
                    assetRepository.save(existingAsset);
                    return ok().build();
                })
                .orElse(notFound().build());
    }

    @PostMapping(value = "/search/property", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Asset> search(@RequestBody Map<String, Object> assetProperties) {
        return assetRepository.findAll(Example.of(assetWithProperties(assetProperties)));
    }

    @PatchMapping(value = "/bulk", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> bulkUpdate(@RequestBody BulkUpdate bulkUpdate) {
        Iterable<Asset> allAssets = assetRepository.findAll(bulkUpdate.getAssetIds());

        if (size(allAssets) != bulkUpdate.getAssetIds().size()) {
            return status(NOT_FOUND)
                    .body("Following assets cannot be found: " +
                            bulkUpdate.getAssetIds()
                                    .stream()
                                    .filter(assetId -> !assetRepository.exists(assetId))
                                    .collect(toList()));
        }

        stream(allAssets.spliterator(), false)
                .forEach(asset -> {
                    asset.getAssetProperties().putAll(bulkUpdate.getProperties());
                    assetRepository.save(asset);
                });
        return ok().build();
    }

    @SuppressWarnings("serial")
    @ResponseStatus(NOT_FOUND)
    class NotFoundException extends Exception {
    }

    @SuppressWarnings("serial")
    @ResponseStatus(BAD_REQUEST)
    class BadRequestException extends Exception {
    }

}
