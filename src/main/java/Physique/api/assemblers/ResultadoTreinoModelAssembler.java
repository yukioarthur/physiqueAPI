package Physique.api.assemblers;

import Physique.api.controllers.ResultadoTreinoController;
import Physique.api.entities.ResultadoTreino;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ResultadoTreinoModelAssembler implements RepresentationModelAssembler<ResultadoTreino, EntityModel<ResultadoTreino>> {

    @Override
    public EntityModel<ResultadoTreino> toModel(ResultadoTreino entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(ResultadoTreinoController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(ResultadoTreinoController.class).listar(org.springframework.data.domain.Pageable.unpaged())).withRel("collection"),
                linkTo(methodOn(ResultadoTreinoController.class).deletar(entity.getId())).withRel("delete")
        );
    }
}
