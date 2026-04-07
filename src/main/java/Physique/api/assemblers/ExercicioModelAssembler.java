package Physique.api.assemblers;

import Physique.api.controllers.ExercicioController;
import Physique.api.entities.Exercicio;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ExercicioModelAssembler implements RepresentationModelAssembler<Exercicio, EntityModel<Exercicio>> {

    @Override
    public EntityModel<Exercicio> toModel(Exercicio entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(ExercicioController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(ExercicioController.class).listar(org.springframework.data.domain.Pageable.unpaged())).withRel("collection"),
                linkTo(methodOn(ExercicioController.class).deletar(entity.getId())).withRel("delete")
        );
    }
}
