package ninja.digitalcloud.cloud.filemanager.repository;

import ninja.digitalcloud.cloud.filemanager.controller.FileController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
@SuppressWarnings("NullableProblems")
public class FileModelAssembler implements RepresentationModelAssembler<File, EntityModel<File>> {
    @Override
    public EntityModel<File> toModel(File entity) {
        EntityModel<File> entityModel = EntityModel.of(entity);
        entityModel.add(WebMvcLinkBuilder.linkTo(methodOn(FileController.class).getFile(entity.getId())).withSelfRel());
        entityModel.add(WebMvcLinkBuilder.linkTo(methodOn(FileController.class).deleteFile(entity.getId())).withRel("delete"));
        return entityModel;
    }

    @Override
    public CollectionModel<EntityModel<File>> toCollectionModel(Iterable<? extends File> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
