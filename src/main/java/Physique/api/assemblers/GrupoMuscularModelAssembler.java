package Physique.api.assemblers;

import Physique.api.controllers.GrupoMuscularController;
import Physique.api.entities.GrupoMuscular;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GrupoMuscularModelAssembler implements RepresentationModelAssembler<GrupoMuscular, EntityModel<GrupoMuscular>> {

    @Override
    public EntityModel<GrupoMuscular> toModel(GrupoMuscular entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(GrupoMuscularController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(GrupoMuscularController.class).listar(org.springframework.data.domain.Pageable.unpaged())).withRel("collection"),
                linkTo(methodOn(GrupoMuscularController.class).deletar(entity.getId())).withRel("delete")
        );
    }
}
