package Physique.api.assemblers;

import Physique.api.controllers.TreinoController;
import Physique.api.entities.Treino;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TreinoModelAssembler implements RepresentationModelAssembler<Treino, EntityModel<Treino>> {

    @Override
    public EntityModel<Treino> toModel(Treino entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(TreinoController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(TreinoController.class).listar(org.springframework.data.domain.Pageable.unpaged())).withRel("collection"),
                linkTo(methodOn(TreinoController.class).deletar(entity.getId())).withRel("delete")
        );
    }
}
