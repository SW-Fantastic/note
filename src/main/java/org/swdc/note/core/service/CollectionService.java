package org.swdc.note.core.service;


import jakarta.inject.Inject;
import org.swdc.data.anno.Transactional;
import org.swdc.dependency.annotations.With;
import org.swdc.note.core.aspect.RefreshAspect;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.repo.CollectionTypeRepo;

import java.util.List;

@With(aspectBy = RefreshAspect.class)
public class CollectionService {

    @Inject
    private CollectionTypeRepo typeRepo;

    @Transactional
    public CollectionType saveType(CollectionType type) {
        if(type == null || type.getTitle() == null || type.getTitle().isBlank()) {
            return null;
        }

        if (type.getId() == null || type.getId().isBlank()) {
            List<CollectionType> exists = typeRepo.findByTypeName(type.getTitle());
            if (exists == null || exists.isEmpty()) {
                return typeRepo.save(type);
            }
            return null;
        } else {
            return typeRepo.save(type);
        }
    }

}
