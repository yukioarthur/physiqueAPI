package Physique.api.assemblers;

import Physique.api.controllers.TreinoSerieController;
import Physique.api.entities.TreinoSerie;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TreinoSerieModelAssembler implements RepresentationModelAssembler<TreinoSerie, EntityModel<TreinoSerie>> {

    @Override
    public EntityModel<TreinoSerie> toModel(TreinoSerie entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(TreinoSerieController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(TreinoSerieController.class).listar(org.springframework.data.domain.Pageable.unpaged())).withRel("collection"),
                linkTo(methodOn(TreinoSerieController.class).deletar(entity.getId())).withRel("delete")
        );
    }
}
