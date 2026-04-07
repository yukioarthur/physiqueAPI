package Physique.api.assemblers;

import Physique.api.controllers.MusculoController;
import Physique.api.entities.Musculo;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MusculoModelAssembler implements RepresentationModelAssembler<Musculo, EntityModel<Musculo>> {

    @Override
    public EntityModel<Musculo> toModel(Musculo entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(MusculoController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(MusculoController.class).listar(org.springframework.data.domain.Pageable.unpaged())).withRel("collection"),
                linkTo(methodOn(MusculoController.class).deletar(entity.getId())).withRel("delete")
        );
    }
}
